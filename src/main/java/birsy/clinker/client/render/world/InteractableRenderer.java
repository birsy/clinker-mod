package birsy.clinker.client.render.world;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.rigidbody.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
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
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InteractableRenderer {
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            event.getPoseStack().pushPose();
            Vec3 camPos = event.getCamera().getPosition();
            event.getPoseStack().translate(-camPos.x(), -camPos.y(), -camPos.z());
            render(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(RenderType.LINES), event.getFrustum(), event.getPartialTick());
            event.getPoseStack().popPose();
        }
    }

    public static void render(PoseStack poseStack, VertexConsumer pBuffer, Frustum camera, float pPartialTicks) {
        for (InteractableManager.Ray2 ray : InteractableManager.clientInteractableManager.rays) {
            //DebugRenderUtil.renderLine(poseStack, pBuffer, ray.start().x(), ray.start().y(), ray.start().z(), ray.end().x(), ray.end().y(), ray.end().z(), 1, 1, 0, 0.5F);
        }
        if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) return;
        for (Interactable interactable : InteractableManager.clientInteractableManager.interactableMap.values()) {
            if (camera.isVisible(interactable.shape.getBounds().inflate(0.5))) {
                renderInteractable(poseStack, pBuffer, interactable, pPartialTicks);
            }
        }
    }

    private static void renderInteractable(PoseStack poseStack, VertexConsumer pBuffer, Interactable interactable, float pPartialTicks) {
        AABB aabb = new AABB(interactable.shape.size.scale(-1.0), interactable.shape.size);
        Vector3f size = new Vector3f(interactable.shape.size);
        Transform transform = interactable.previousTransform.lerp(interactable.getTransform(), pPartialTicks);
        Vec3 position = transform.getPosition();
        Quaternion orientation = transform.getOrientation().toMojangQuaternion();


        poseStack.pushPose();
        poseStack.translate(position.x(), position.y(), position.z());
        poseStack.mulPose(orientation);
        float r = 0.3F;
        float g = 0.28F;
        float b = 0.44F;
        float a = 1;

        LevelRenderer.renderLineBox(poseStack, pBuffer, aabb, r, g, b, a);
        poseStack.popPose();
        //LevelRenderer.renderLineBox(poseStack, pBuffer, interactable.shape.getBounds(), 0.63F, 0.60F, 0.34F, 0.5F);
    }
}
