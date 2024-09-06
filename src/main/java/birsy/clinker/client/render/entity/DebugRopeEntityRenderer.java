package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.entity.rope.RopeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
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

        for (int i = 0; i < pEntity.segments.size(); i++) {
            RopeEntity.RopeEntitySegment segment = (RopeEntity.RopeEntitySegment) pEntity.segments.get(i);
            float brightness = 1 - (i / (float)pEntity.segments.size());
            Vector3dc pos = segment.getPosition(pPartialTick);
            DebugRenderUtil.renderSphere(pPoseStack, consumer, 32, segment.radius, pos.x(), pos.y(), pos.z(), 0.2F, 0.2F, 1.0F * brightness, 1.0F);
        }

        for (int i = 0; i < pEntity.segments.size() - 1; i++) {
            float brightness = 1 - (i / (float)pEntity.segments.size());

            RopeEntity.RopeEntitySegment segment1 = (RopeEntity.RopeEntitySegment) pEntity.segments.get(i);
            RopeEntity.RopeEntitySegment segment2 = (RopeEntity.RopeEntitySegment) pEntity.segments.get(i);

            Vector3dc pos1 = segment1.getPosition(pPartialTick);
            Vector3dc pos2 = segment2.getPosition(pPartialTick);

            DebugRenderUtil.renderLine(pPoseStack, consumer, pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), 1, (i%2==0) ? 1 : 0, brightness, 1);
        }

        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(RopeEntity pEntity) {
        return null;
    }
}

