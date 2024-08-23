package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.entity.InverseKinematicsLegEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class InverseKinematicsEntityRenderer extends EntityRenderer<InverseKinematicsLegEntity> {
    public InverseKinematicsEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(InverseKinematicsLegEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.LINES);
        Vec3 position = pEntity.getPosition(pPartialTick);

        for (int i = 0; i < pEntity.legs.length; i++) {
            this.renderLeg(consumer, pPoseStack, pPartialTick, position, pEntity, pEntity.legs[i], pEntity.cLegs[i]);
        }

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    void renderLeg(VertexConsumer consumer, PoseStack pPoseStack, float partialTick, Vec3 position, InverseKinematicsLegEntity pEntity, InverseKinematicsLegEntity.Leg leg, InverseKinematicsLegEntity.ClientLeg clientLeg) {
//        DebugRenderUtil.renderSphere(pPoseStack, consumer, 32, 0.1F,
//                leg.base.x() - position.x(), leg.base.y() - position.y(), leg.base.z() - position.z(),
//                1.0F, 0.0F, 0.0F, 0.5F);

        for (int i = 1; i < clientLeg.getPointCount(); i++) {
            Vector3dc segment = clientLeg.getPointPosition(i, partialTick);
            Vector3dc previousSegment = clientLeg.getPointPosition(i - 1, partialTick);

            float factor = i / ((float)clientLeg.getPointCount());

            DebugRenderUtil.renderLine(pPoseStack, consumer,
                    segment.x() - position.x(), segment.y() - position.y(), segment.z() - position.z(),
                    previousSegment.x() - position.x(), previousSegment.y() - position.y(), previousSegment.z() - position.z(),
                    0.0F, 0.0F, factor, 1.0F);
        }

//        DebugRenderUtil.renderSphere(pPoseStack, consumer, 32, 0.1F,
//                leg.foot.x() - position.x(), leg.foot.y() - position.y(), leg.foot.z() - position.z(),
//                0.0F, 1.0F, 0.0F, 0.5F);
//        DebugRenderUtil.renderSphere(pPoseStack, consumer, 32, 0.1F,
//                leg.restPos.x() - position.x(), leg.restPos.y() - position.y(), leg.restPos.z() - position.z(),
//                1.0F, 1.0F, 0.0F, 0.5F);
//        DebugRenderUtil.renderCircle(pPoseStack, consumer, 32, leg.getStepDistance(pEntity),
//                leg.restPos.x() - position.x(), leg.restPos.y() - position.y(), leg.restPos.z() - position.z(),
//                0.0F, 1.0F, 0.0F, 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(InverseKinematicsLegEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
