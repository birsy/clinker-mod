package birsy.clinker.client.entity;

import birsy.clinker.common.entity.SegmentedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.util.DebugRenderHelper;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.List;

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
        List<SegmentedEntity.Joint> joints = entity.joints;
        int n = joints.size();
        if (n == 0) return;

        double eX = Mth.lerp(partialTicks, entity.xOld, entity.getX()),
               eY = Mth.lerp(partialTicks, entity.yOld, entity.getY()),
               eZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        if (entityRenderDispatcher.shouldRenderHitBoxes()) {
            for (SegmentedEntity.Joint joint : joints) {
                double x = joint.getX(partialTicks), y = joint.getY(partialTicks), z = joint.getZ(partialTicks);
                poseStack.pushPose();
                poseStack.translate(x - eX, y - eY + 1F / 2, z - eZ);
                DebugRenderHelper.renderSphere(poseStack, buffer.getBuffer(RenderType.lines()), 8, 0.5F, 0, 0, 0, 0, 0, 1F, 1F);
                poseStack.popPose();
            }

        }

        Quaternionf rot = new Quaternionf(), delta = new Quaternionf();
        Vector3d pos = new Vector3d(), dir = new Vector3d(), prevDir = new Vector3d(0, 0, -1);
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < n; i++) {
            SegmentedEntity.Joint joint = joints.get(i);
            joint.getSegmentPosAndDirection(pos, dir, partialTicks);

            if (i == 0)
                rot.rotationYXZ(
                        (float) Math.atan2(-dir.x, -dir.z),
                        (float) Math.asin(Mth.clamp(dir.y, -1f, 1f)),
                        0
                );
            else
                rot.premul(delta.rotationTo(
                        (float) prevDir.x, (float) prevDir.y, (float) prevDir.z,
                        (float) dir.x, (float) dir.y, (float) dir.z
                ));

            prevDir.set(dir);

            int light = LevelRenderer.getLightColor(entity.level(), blockPos.set(pos.x(), pos.y() + 1F / 2, pos.z()));

            poseStack.pushPose();
            poseStack.translate(pos.x() - eX, pos.y() - eY + 1F / 2, pos.z() - eZ);
            poseStack.mulPose(rot);
            poseStack.scale(i == 0 ? 1.2F : 1F, i == 0 ? 1.2F : 1F, i == 0 ? 1.2F : 1F);
            poseStack.translate(-0.5, -0.5, -0.5);
            dispatcher.renderSingleBlock(Blocks.DISPENSER.defaultBlockState(), poseStack, buffer, light, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SegmentedEntity pEntity) {
        return null;
    }
}

