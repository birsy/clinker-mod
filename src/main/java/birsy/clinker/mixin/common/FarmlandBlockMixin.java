package birsy.clinker.mixin.common;

import birsy.clinker.common.world.SaltpetreFiltrationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
public class FarmlandBlockMixin {
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
}
