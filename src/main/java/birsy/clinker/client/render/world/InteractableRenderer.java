package birsy.clinker.client.render.world;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.level.interactableOLD.Interactable;
import birsy.clinker.common.world.level.interactableOLD.InteractableManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.common.world.physics.rigidbody.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InteractableRenderer {
    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        if (InteractableManager.seenInteractable.isPresent()) {
            if (!InteractableManager.seenInteractable.get().hasOutline) return;
            event.setCanceled(InteractableManager.seenInteractable.get().hasOutline);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
//        Clinker.LOGGER.info("START: " + event.getPoseStack().poseStack.size());
//
//        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
//            event.getPoseStack().pushPose();
//            Vec3 camPos = event.getCamera().getPosition();
//            event.getPoseStack().translate(-camPos.x(), -camPos.y(), -camPos.z());
//
//            if (InteractableManager.seenInteractable.isPresent()) {
//                if (!InteractableManager.seenInteractable.get().hasOutline) return;
//                renderInteractable(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(RenderType.lines()), InteractableManager.seenInteractable.get(), event.getPartialTick(), 0.0F, 0.0F, 0.0F, 0.4F);
//            }
//
//            renderDebug(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(RenderType.lines()), event.getFrustum(), event.getPartialTick());
//
//            event.getPoseStack().popPose();
//        }
//
//        Clinker.LOGGER.info("END: " + event.getPoseStack().poseStack.size());
    }

    private static void renderDebug(PoseStack poseStack, VertexConsumer pBuffer, Frustum camera, float pPartialTicks) {
        if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) return;

        for (Interactable interactable : InteractableManager.clientInteractableManager.storage.getAllInteractables()) {
            if (camera.isVisible(interactable.shape.getBounds().inflate(0.5))) {
                renderInteractable(poseStack, pBuffer, interactable, pPartialTicks, 0.3F, 0.28F, 0.44F, 1.0F);
            }
        }
    }

    private static void renderInteractable(PoseStack poseStack, VertexConsumer pBuffer, Interactable interactable, float pPartialTicks, float r, float g, float b, float a) {
        AABB aabb = new AABB(interactable.shape.size.scale(-1.0), interactable.shape.size);
        Transform transform = interactable.previousTransform.lerp(interactable.getTransform(), pPartialTicks);
        Vec3 position = transform.getPosition();
        Quaterniond orientation = transform.getOrientation();

        poseStack.pushPose();

        poseStack.translate(position.x(), position.y(), position.z());
        poseStack.mulPose(new Quaternionf(orientation));
        DebugRenderUtil.renderBox(poseStack, pBuffer, aabb, r, g, b, a);

        poseStack.popPose();
    }
}
