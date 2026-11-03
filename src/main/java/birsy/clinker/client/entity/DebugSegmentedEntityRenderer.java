package birsy.clinker.client.entity;

import birsy.clinker.client.render.utilities.DebugRenderUtil;
import birsy.clinker.common.entity.GiantLeggyCritterEntity;
import birsy.clinker.common.entity.LegManager;
import birsy.clinker.common.entity.SegmentedEntity;
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

public class DebugSegmentedEntityRenderer extends EntityRenderer<SegmentedEntity> {
    private final BlockRenderDispatcher dispatcher;
    public DebugSegmentedEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
        this.shadowRadius = 0.0001F;
        this.shadowStrength = 0.0F;
    }

    @Override
    public void render(SegmentedEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        //pPoseStack.mulPose(Axis.YN.rotationDegrees(180 + pEntity.getViewYRot(pPartialTick)));
        //pPoseStack.mulPose(Axis.XN.rotationDegrees(pEntity.getViewXRot(pPartialTick)));
        pPoseStack.scale(pEntity.getBbWidth(), pEntity.getBbHeight(), pEntity.getBbWidth());
        //pPoseStack.translate(-0.5F, 0, -0.5F);

        for (SegmentedEntity.Segment segment : pEntity.segments) {
            pPoseStack.pushPose();
            double x = segment.getX(pPartialTick) - pEntity.getX(),
                   y = segment.getY(pPartialTick) - pEntity.getY(),
                   z = segment.getZ(pPartialTick) - pEntity.getZ();
            pPoseStack.translate((float) x - 0.5F, (float) y, (float) z - 0.5F);
            this.dispatcher.renderSingleBlock(Blocks.DISPENSER.defaultBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();
        }

        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SegmentedEntity pEntity) {
        return null;
    }
}

