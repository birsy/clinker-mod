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
import net.minecraft.util.Mth;
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
    public void render(SegmentedEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.pushPose();
        //pPoseStack.mulPose(Axis.YN.rotationDegrees(180 + pEntity.getViewYRot(pPartialTick)));
        //pPoseStack.mulPose(Axis.XN.rotationDegrees(pEntity.getViewXRot(pPartialTick)));
        poseStack.scale(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
        //pPoseStack.translate(-0.5F, 0, -0.5F);

        for (SegmentedEntity.Segment segment : entity.segments) {
            poseStack.pushPose();
            double x = segment.getX(partialTicks) - Mth.lerp(partialTicks, entity.xOld, entity.getX()),
                   y = segment.getY(partialTicks) - Mth.lerp(partialTicks, entity.yOld, entity.getY()),
                   z = segment.getZ(partialTicks) - Mth.lerp(partialTicks, entity.zOld, entity.getZ());
            poseStack.translate((float) x - 0.5F, (float) y, (float) z - 0.5F);
            this.dispatcher.renderSingleBlock(Blocks.DISPENSER.defaultBlockState(), poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SegmentedEntity pEntity) {
        return null;
    }
}

