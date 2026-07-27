package birsy.clinker.mixin.client;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {
//    @Redirect(method = "renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"))
//    public void clinker$redirectLiquidTessellate(LiquidBlockRenderer liquidBlockRenderer, BlockAndTintGetter tintGetter, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
//        WeirdLiquidRendererRemake.tessellate(tintGetter, pos, vertexConsumer, blockState, fluidState);
//    }
}
