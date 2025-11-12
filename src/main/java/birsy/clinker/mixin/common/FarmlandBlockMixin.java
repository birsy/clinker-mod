package birsy.clinker.mixin.common;

import birsy.clinker.common.world.SaltpetreFiltrationHandler;
import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmlandBlockMixin extends Block {
    public FarmlandBlockMixin(Properties properties) {
        super(properties);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"),
            index = 0
    )
    private BlockState clinker$setFarmlandDefaultBlockState(BlockState state) {
        return state.setValue(SaltpetreFiltrationHandler.SALTPETRE_LEACHED_PROPERTY, false);
    }

    @Inject(method = "createBlockStateDefinition", at = @At("RETURN"))
    private void clinker$createFarmlandBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(SaltpetreFiltrationHandler.SALTPETRE_LEACHED_PROPERTY);
    }

    @Inject(method = "randomTick", at = @At("RETURN"))
    private void clinker$randomTickFarmland(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        SaltpetreFiltrationHandler.tickFarmland(state, level, pos, random);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(SaltpetreFiltrationHandler.SALTPETRE_LEACHED_PROPERTY) && random.nextInt(5) == 0) {
            Direction direction = Direction.getRandom(random);
            if (direction != Direction.UP) {
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos);
                if (!blockstate.isFaceSturdy(level, blockpos, direction.getOpposite()) &&
                     blockstate.getFluidState().isEmpty()) {
                    double x = direction.getStepX() == 0 ? random.nextDouble() : 0.5 + direction.getStepX() * 0.6;
                    double y = direction.getStepY() == 0 ? random.nextDouble() * (14.0 / 16.0) : 0.5 + direction.getStepY() * 0.6;
                    double z = direction.getStepZ() == 0 ? random.nextDouble() : 0.5 + direction.getStepZ() * 0.6;
                    level.addParticle(
                            ClinkerParticles.DRIPPING_SALTPETRE.get(),
                            pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                            0.0, 0.0, 0.0
                    );
                }
            }
        }
    }
}
