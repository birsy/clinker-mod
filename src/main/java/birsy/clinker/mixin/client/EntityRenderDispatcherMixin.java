package birsy.clinker.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
//    @Inject(method = "renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;F)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/AABB;FFFF)V"),
//            cancellable = true)
//    private static void clinker$renderHitbox(PoseStack pPoseStack, VertexConsumer pBuffer, Entity pEntity, float pPartialTicks, CallbackInfo ci, @Local AABB aabb) {
//        if (pEntity instanceof ColliderEntity) {
//            LevelRenderer.renderLineBox(pPoseStack, pBuffer, aabb, 0.5F, 0.8F, 1.0F, 1.0F);
//            ci.cancel();
//        }
//    }
}
