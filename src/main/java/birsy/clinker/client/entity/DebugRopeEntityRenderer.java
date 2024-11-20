package birsy.clinker.client.entity;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.common.world.entity.rope.RopeEntitySegment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class DebugRopeEntityRenderer extends EntityRenderer<RopeEntity> {
    public DebugRopeEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0.0001F;
        this.shadowStrength = 0.0F;
    }

    @Override
    public void render(RopeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        Vec3 entityPos = pEntity.getPosition(pPartialTick);
        pPoseStack.translate(-entityPos.x, -entityPos.y, -entityPos.z);

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.LINES);

        Quaternionf rotation = new Quaternionf();
        float length = 1.0F;
        for (int i = 0; i < pEntity.segments.size(); i++) {
            RopeEntitySegment segment = (RopeEntitySegment) pEntity.segments.get(i);
            float brightness = 1 - (i / (float)pEntity.segments.size());

            Vector3dc pos = segment.getPosition(pPartialTick);
            Vector3dc direction = segment.getAttachmentDirection(pPartialTick);
            double forward = segment.getWalkAmount(pPartialTick);
            double strafe = segment.getStrafeAmount(pPartialTick);

            pPoseStack.pushPose();
            pPoseStack.translate(pos.x(), pos.y(), pos.z());

            pPoseStack.pushPose();
            double xRot = Math.acos(direction.y());
            double yRot = Mth.atan2(direction.x(), direction.z());
            rotation.set(0, 0, 0, 1).rotateY((float) yRot).rotateX((float) xRot - Mth.HALF_PI);
            pPoseStack.mulPose(rotation);

            DebugRenderUtil.renderSphere(pPoseStack, consumer, 32, segment.radius,
                    0, 0, 0,
                    0.2F, 0.2F, brightness, 1.0F);
            DebugRenderUtil.renderLine(pPoseStack, consumer,
                    0, 0, 0,
                    strafe*length, 0, 0,
                    0.0F, 1.0F, 0.0F, 1F);
            DebugRenderUtil.renderLine(pPoseStack, consumer,
                    0, 0, 0,
                    0, 0, forward*length,
                    1.0F, 0.0F, 0.0F, 1F);
            if (i == 0) {
                Vector3d headPos = rotation.transform(new Vector3d(0, 0, segment.radius));

                rotation.set(0, 0, 0, 1).rotateY(pEntity.getViewYRot(pPartialTick) * -Mth.DEG_TO_RAD).rotateX(pEntity.getViewXRot(pPartialTick) * Mth.DEG_TO_RAD);
                Vector3d lookVector = rotation.transform(new Vector3d(0, 0, 1));

                pPoseStack.popPose();

                DebugRenderUtil.renderLine(pPoseStack, consumer,
                        headPos.x(), headPos.y(), headPos.z(),
                        headPos.x() + lookVector.x, headPos.y() + lookVector.y(), headPos.z() + lookVector.z,
                        1.0F, 1.0F, 1.0F, 1F);
            } else {
                pPoseStack.popPose();
            }

//            DebugRenderUtil.renderLine(pPoseStack, consumer,
//                    0, 0, 0,
//                    walk.x()*length, walk.y()*length, walk.z()*length,
//                    1.0F, 1.0F, 0.0F, 1F);

            pPoseStack.popPose();

        }

        for (int i = 0; i < pEntity.segments.size() - 1; i++) {
            float brightness = 1 - (i / (float)pEntity.segments.size());

            RopeEntitySegment segment1 = (RopeEntitySegment) pEntity.segments.get(i);
            RopeEntitySegment segment2 = (RopeEntitySegment) pEntity.segments.get(i + 1);

            Vector3dc pos1 = segment1.getPosition(pPartialTick);
            Vector3dc pos2 = segment2.getPosition(pPartialTick);

            DebugRenderUtil.renderLine(pPoseStack, consumer,
                    pos1.x(), pos1.y(), pos1.z(),
                    pos2.x(), pos2.y(), pos2.z(),
                    1.0F, (i%2==0) ? 1.0F : 0.0F, brightness, 0.1F);
        }

        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(RopeEntity pEntity) {
        return null;
    }
}

