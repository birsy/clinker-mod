package birsy.clinker.client.render.world;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.common.world.physics.rigidbody.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            event.getPoseStack().pushPose();
            Vec3 camPos = event.getCamera().getPosition();
            event.getPoseStack().translate(-camPos.x(), -camPos.y(), -camPos.z());
            render(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(RenderType.lines()), event.getFrustum(), event.getPartialTick());

            if (InteractableManager.seenInteractable.isPresent()) {
                if (!InteractableManager.seenInteractable.get().hasOutline) return;
                renderInteractable(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(RenderType.lines()), InteractableManager.seenInteractable.get(), event.getPartialTick(), 0.0F, 0.0F, 0.0F, 0.4F);
            }

            event.getPoseStack().popPose();
        }
    }

    public static void render(PoseStack poseStack, VertexConsumer pBuffer, Frustum camera, float pPartialTicks) {
        if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) return;

        //Clinker.LOGGER.info("here:");
        for (Interactable interactable : InteractableManager.clientInteractableManager.storage.getAllInteractables()) {
            //Clinker.LOGGER.info(interactable.uuid);
            if (camera.isVisible(interactable.shape.getBounds().inflate(0.5))) {
                renderInteractable(poseStack, pBuffer, interactable, pPartialTicks, 0.3F, 0.28F, 0.44F, 1.0F);
            }
        }
    }

    private static void renderInteractable(PoseStack poseStack, VertexConsumer pBuffer, Interactable interactable, float pPartialTicks, float r, float g, float b, float a) {
        AABB aabb = new AABB(interactable.shape.size.scale(-1.0), interactable.shape.size);
        Vector3f size = new Vector3f(interactable.shape.size);
        Transform transform = interactable.previousTransform.lerp(interactable.getTransform(), pPartialTicks);
        Vec3 position = transform.getPosition();
        Quaternion orientation = transform.getOrientation().toMojangQuaternion();

        poseStack.pushPose();
        poseStack.translate(position.x(), position.y(), position.z());
        poseStack.mulPose(orientation);
        LevelRenderer.renderLineBox(poseStack, pBuffer, aabb, r, g, b, a);
        poseStack.popPose();
        //LevelRenderer.renderLineBox(poseStack, pBuffer, interactable.shape.getBounds(), 0.63F, 0.60F, 0.34F, 0.5F);
    }
}
