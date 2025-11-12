package birsy.clinker.mixin.common;

import birsy.clinker.common.world.SaltpetreFiltrationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public class CropBlockMixin {
    @Inject(method = "performBonemeal", at = @At("RETURN"))
    private void clinker$performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.is(Blocks.FARMLAND)) {
            level.setBlockAndUpdate(belowPos, belowState.setValue(SaltpetreFiltrationHandler.SALTPETRE_LEACHED_PROPERTY, true));
        }
    }
}
