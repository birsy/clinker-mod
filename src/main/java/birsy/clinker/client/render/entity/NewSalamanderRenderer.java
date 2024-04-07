package birsy.clinker.client.render.entity;

import birsy.clinker.client.model.base.constraint.Constraint;
import birsy.clinker.client.model.entity.SalamanderSkeletonFactory;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.client.render.entity.layer.SalamanderOverlayLayer;
import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.common.world.entity.salamander.SalamanderPartEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NewSalamanderRenderer extends InterpolatedEntityRenderer<NewSalamanderEntity, SalamanderSkeletonFactory.SalamanderSkeleton> {
    private static final ResourceLocation SALAMANDER_HEAD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head.png");
    private static final ResourceLocation SALAMANDER_BODY_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body.png");
    private static final ResourceLocation SALAMANDER_TAIL_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_tail.png");

    public NewSalamanderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SalamanderSkeletonFactory(), 0.0F);
        //this.addLayer(new SalamanderCharredLayer(this));
        this.addLayer(new SalamanderOverlayLayer(this));
    }

    @Override
    public void setupModelFactory(NewSalamanderEntity parent) {
        ((SalamanderSkeletonFactory) this.modelFactory).entity = parent;
    }

    @Override
    public void render(NewSalamanderEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);

        pPoseStack.pushPose();
        float scale = 1.0F / 16.0F;
        pPoseStack.scale(scale, scale, scale);
        this.setupRotations(pEntity, pPoseStack, pEntity.tickCount + pPartialTick, pPartialTick);
        if (pEntity.getSkeleton() != null) {
            if (pEntity.getSkeleton() instanceof SalamanderSkeletonFactory.SalamanderSkeleton salamanderSkeleton) {
                for (SalamanderSkeletonFactory.SalamanderSegmentSkeleton segment : salamanderSkeleton.segments) {
                    for (Object obj : segment.constraints) {
                        if (obj instanceof Constraint constraint) {
                            //constraint.renderDebugInfo(pEntity.getSkeleton(), pEntity, pPartialTick, pPoseStack, pBuffer);
                        }
                    }
                }
            }
        }
        pPoseStack.popPose();

        for (SalamanderPartEntity partEntity : pEntity.partEntities) {
            pPoseStack.pushPose();
            AABB bounds = partEntity.getBoundingBox();
            Vec3 offset = pEntity.position().scale(-1);
            pPoseStack.translate(offset.x, offset.y, offset.z);
            DebugRenderUtil.renderBox(pPoseStack, pBuffer.getBuffer(RenderType.LINES),bounds, 1.0F, 0.0F, 1.0F, 1.0F);
            pPoseStack.popPose();
        }

        //renderDebug(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public void renderModel(NewSalamanderEntity pEntity, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(pEntity, 0);

        if (pEntity.getSkeleton() != null) {
            if (pEntity.getSkeleton() instanceof SalamanderSkeletonFactory.SalamanderSkeleton salamanderSkeleton) {
                for (SalamanderSkeletonFactory.SalamanderSegmentSkeleton segment : salamanderSkeleton.segments) {
                    VertexConsumer vertexconsumer = null;
                    if (segment instanceof SalamanderSkeletonFactory.SalamanderHeadSkeleton) {
                        vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(SALAMANDER_HEAD_TEXTURE));
                    } else if (segment instanceof SalamanderSkeletonFactory.SalamanderBodySkeleton) {
                        vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(SALAMANDER_BODY_TEXTURE));
                    } else if (segment instanceof SalamanderSkeletonFactory.SalamanderTailSkeleton) {
                        vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(SALAMANDER_TAIL_TEXTURE));
                    }

                    segment.render(pPartialTicks, poseStack, vertexconsumer, pPackedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }

    private void renderDebug(NewSalamanderEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        Vec3 pos = pEntity.getPosition(pPartialTick);
        pPoseStack.translate(-pos.x, -pos.y, -pos.z);

        int amount = 0;
        for (NewSalamanderEntity.SalamanderJoint joint : pEntity.joints) {
            float brightness = 1 - (amount / (float)pEntity.joints.size());
            Vec3 jPos = joint.getPosition(pPartialTick);
            DebugRenderUtil.renderSphere(pPoseStack, pBuffer.getBuffer(RenderType.LINES), 32, (float)joint.radius, jPos.x, jPos.y, jPos.z, joint.isHead ? 1.0F : 0.2F, 0.2F, 0.8F * brightness, 0.2F);
            amount ++;
        }

        boolean color1 = true;
        for (NewSalamanderEntity.SalamanderSegment segment : pEntity.segments) {
            Vec3 pos1 = segment.joint1.getPosition(pPartialTick);
            Vec3 pos2 = segment.joint2.getPosition(pPartialTick);
            DebugRenderUtil.renderLine(pPoseStack, pBuffer.getBuffer(RenderType.LINES), pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), 1, color1 ? 1 : 0, segment.isHead() ? 1 : 0, 1);
            color1 = !color1;

            if (segment.hasLegs) {
                renderLeg(segment.leftLeg, pPoseStack, pBuffer);
                renderLeg(segment.rightLeg, pPoseStack, pBuffer);
            }
        }

        pPoseStack.popPose();
    }

    private static void renderLeg(NewSalamanderEntity.SalamanderSegment.SalamanderLeg leg, PoseStack pPoseStack, MultiBufferSource pBuffer) {
        DebugRenderUtil.renderSphere(pPoseStack, pBuffer.getBuffer(RenderType.LINES), 32, 0.25F, leg.hipPos.x, leg.hipPos.y, leg.hipPos.z, 1.0F, 1.0F, 1.0F, 1.0F);
        DebugRenderUtil.renderSphere(pPoseStack, pBuffer.getBuffer(RenderType.LINES), 32, 0.2F, leg.getFootPosition().x, leg.getFootPosition().y, leg.getFootPosition().z, 1.0F, 0.0F, 0.0F, 1.0F);
        DebugRenderUtil.renderLine(pPoseStack, pBuffer.getBuffer(RenderType.LINES), leg.hipPos.x(), leg.hipPos.y(), leg.hipPos.z(), leg.getFootPosition().x(), leg.getFootPosition().y(), leg.getFootPosition().z(), 1, 1, 1, 1, 1, 0, 0, 1);

        DebugRenderUtil.renderSphere(pPoseStack, pBuffer.getBuffer(RenderType.LINES), 32, 0.1F, leg.targetPos.x, leg.targetPos.y, leg.targetPos.z, 0.0F, 1.0F, 0.0F, 0.5F);
        DebugRenderUtil.renderLine(pPoseStack, pBuffer.getBuffer(RenderType.LINES), leg.hipPos.x(), leg.hipPos.y(), leg.hipPos.z(), leg.targetPos.x(), leg.targetPos.y(), leg.targetPos.z(), 1, 1, 1, 1, 0, 1, 0, 0.5F);
    }

    @Override
    public RenderType getRenderType(NewSalamanderEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getTextureLocation(NewSalamanderEntity entity) {
        return SALAMANDER_HEAD_TEXTURE;
    }
}

