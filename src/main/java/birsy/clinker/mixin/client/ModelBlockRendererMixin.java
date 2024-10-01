package birsy.clinker.mixin.client;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {
//    @Inject(method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
//            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", shift = At.Shift.AFTER))
//    private void nomansland$tesselateBlockSnowlogging(
//            BlockAndTintGetter blockAndTintGetter,
//            BakedModel model,
//            BlockState state,
//            BlockPos pos,
//            PoseStack poseStack,
//            VertexConsumer vertexConsumer,
//            boolean checkSides,
//            RandomSource random,
//            long seed,
//            int packedOverlay,
//            ModelData modelData,
//            RenderType renderType,
//            CallbackInfo ci) {
//        ModelBlockRenderer me = (ModelBlockRenderer) ((Object) this);
//        if (state.getOptionalValue(NoMansLandBlockstates.SNOWLOGGED).orElse(false)) {
//            BakedModel snowModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.SNOW.defaultBlockState());
//            me.tesselateWithAO(blockAndTintGetter, snowModel, Blocks.SNOW.defaultBlockState(), pos, poseStack, vertexConsumer, checkSides, random, seed, packedOverlay, modelData, renderType);
//        }
//    }
}
