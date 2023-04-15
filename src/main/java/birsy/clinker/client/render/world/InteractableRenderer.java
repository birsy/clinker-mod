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

        for (Interactable.Ray ray : interactable.incomingRays) {
            DebugRenderUtil.renderLine(poseStack, pBuffer, ray.start().x(), ray.start().y(), ray.start().z(), ray.end().x(), ray.end().y(), ray.end().z(), 1, 1, 0, 0.5F);
            DebugRenderUtil.renderSphere(poseStack, pBuffer, 4, 0.025F, ray.hit().x, ray.hit().y, ray.hit().z, 1, 0, 0, 1.0F);
        }


        poseStack.pushPose();
        poseStack.translate(position.x(), position.y(), position.z());
        poseStack.mulPose(orientation);
        float r = 0.2F;
        float g = 0.5F;
        float b = 0.8F;
        float a = 1;

        for (Interactable.Ray ray : interactable.incomingRays) {
            Vec3 fromT = interactable.getTransform().toLocalSpace(ray.start());
            Vec3 toT = interactable.getTransform().toLocalSpace(ray.end());
            //DebugRenderUtil.renderLine(poseStack, pBuffer, fromT.x(), fromT.y(), fromT.z(), toT.x(), toT.y(), toT.z(), 0, 1, 0, 0.1F);
            Vec3 hitT = interactable.getTransform().toLocalSpace(ray.hit());
            //DebugRenderUtil.renderSphere(poseStack, pBuffer, 4, 0.025F, hitT.x, hitT.y, hitT.z, 0, 1, 0, 0.5F);
        }
        LevelRenderer.renderLineBox(poseStack, pBuffer, aabb, r, g, b, a);

        poseStack.popPose();
    }
}
