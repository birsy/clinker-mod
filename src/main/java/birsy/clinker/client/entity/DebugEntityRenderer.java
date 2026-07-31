package birsy.clinker.client.entity;

import birsy.clinker.client.render.utilities.DebugRenderUtil;
import birsy.clinker.common.entity.GiantLeggyCritterEntity;
import birsy.clinker.common.entity.LegManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class DebugEntityRenderer extends EntityRenderer<Entity> {
    private final BlockRenderDispatcher dispatcher;
    public DebugEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
        this.shadowRadius = 0.0001F;
        this.shadowStrength = 0.0F;
    }

    @Override
    public void render(Entity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(180 + pEntity.getViewYRot(pPartialTick)));
        pPoseStack.mulPose(Axis.XN.rotationDegrees(pEntity.getViewXRot(pPartialTick)));
        pPoseStack.scale(pEntity.getBbWidth(), pEntity.getBbHeight(), pEntity.getBbWidth());
        pPoseStack.translate(-0.5F, 0, -0.5F);


        this.dispatcher.renderSingleBlock(Blocks.DISPENSER.defaultBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);

        pPoseStack.popPose();


        if (pEntity instanceof GiantLeggyCritterEntity leggy) {
            pPoseStack.pushPose();
            // eliminate partial tick stuffs
            Vec3 previousEntityPos = pEntity.getPosition(pPartialTick);

            double entityX = pEntity.getX(), entityY = pEntity.getY(), entityZ = pEntity.getZ();
            pPoseStack.translate(
                    entityX - previousEntityPos.x(),
                    entityY - previousEntityPos.y(),
                    entityZ - previousEntityPos.z()
            );

            Vector3d scratch = new Vector3d();

            LegManager legManager = leggy.legManager;
            for (int i = 0; i < legManager.legCount(); i++) {
                LegManager.Leg leg = legManager.getLeg(i);

                legManager.fromParentSpace(leg.relativeSocketPos, scratch);
                double socketX = scratch.x - entityX, socketY = scratch.y - entityY, socketZ = scratch.z - entityZ;
                legManager.fromParentSpace(leg.getRelativeFootPos(), scratch);
                double footX = scratch.x - entityX, footY = scratch.y - entityY, footZ = scratch.z - entityZ;

                DebugRenderUtil.renderLine(
                        pPoseStack,
                        pBuffer.getBuffer(RenderType.lines()),
                        socketX, socketY, socketZ,
                        footX, footY, footZ,
                        1F, 1F, 1F, 1F
                );

                pPoseStack.pushPose();
                pPoseStack.translate(footX, footY, footZ);
                pPoseStack.scale(0.2F, 0.2F, 0.2F);
                this.dispatcher.renderSingleBlock(Blocks.COBBLESTONE.defaultBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
                pPoseStack.popPose();

                legManager.fromParentSpace(leg.relativeIdealFootPos, scratch);
                double idealX = scratch.x - entityX, idealY = scratch.y - entityY, idealZ = scratch.z - entityZ;

                pPoseStack.pushPose();
                pPoseStack.translate(idealX, idealY, idealZ);
                pPoseStack.scale(0.2F, 0.2F, 0.2F);
                this.dispatcher.renderSingleBlock(Blocks.REDSTONE_BLOCK.defaultBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
                pPoseStack.popPose();

                legManager.fromParentSpacePredicted(leg.relativeIdealFootPos, legManager.ticksUntilNextTurn(leg.stepGroup), scratch);
                double predictedIdealX = scratch.x - entityX, predictedIdealY = scratch.y - entityY, predictedIdealZ = scratch.z - entityZ;
                pPoseStack.pushPose();
                pPoseStack.translate(predictedIdealX, predictedIdealY, predictedIdealZ);
                pPoseStack.scale(0.2F, 0.2F, 0.2F);
                this.dispatcher.renderSingleBlock(Blocks.LAPIS_BLOCK.defaultBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
                pPoseStack.popPose();
            }
            pPoseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Entity pEntity) {
        return null;
    }
}

