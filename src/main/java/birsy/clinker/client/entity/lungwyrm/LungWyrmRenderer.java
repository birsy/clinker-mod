package birsy.clinker.client.entity.lungwyrm;

import birsy.clinker.common.entity.SegmentedEntity;
import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

public class LungWyrmRenderer extends EntityRenderer<LungWyrmEntity> {
    private final BlockRenderDispatcher dispatcher;
    public LungWyrmRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
        this.shadowRadius = 0.0001F;
        this.shadowStrength = 0.0F;
    }

    @Override
    public void render(LungWyrmEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        List<LungWyrmEntity.LungWyrmJoint> joints = entity.joints;
        int n = joints.size();
        if (n == 0) return;

        double eX = Mth.lerp(partialTicks, entity.xOld, entity.getX()),
               eY = Mth.lerp(partialTicks, entity.yOld, entity.getY()),
               eZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        if (entityRenderDispatcher.shouldRenderHitBoxes()) {
            for (SegmentedEntity.Joint<?,?> joint : joints) {
                double x = joint.getX(partialTicks), y = joint.getY(partialTicks), z = joint.getZ(partialTicks);
                poseStack.pushPose();
                poseStack.translate(x - eX, y - eY + joint.height / 2, z - eZ);
                DebugRenderHelper.renderSphere(poseStack, buffer.getBuffer(RenderType.lines()), 8, joint.length * 0.5F, 0, 0, 0, 0, 0, 1F, 0.25F);
                poseStack.popPose();
            }
        }

        Quaternionf rot = new Quaternionf();
        Vector3d pos = new Vector3d(), dir = new Vector3d();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        boolean dying = entity.deathTime > 0;
        float deathRotation = 0F;
        if (dying) {
            deathRotation = ((float)entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            deathRotation = Mth.sqrt(deathRotation);
            if (deathRotation > 1.0F) deathRotation = 1.0F;
            deathRotation *= 90.0F;
        }

        int overlay = OverlayTexture.pack(0F, entity.hurtTime > 0 || entity.deathTime > 0);

        for (int i = 0; i < n; i++) {
            LungWyrmEntity.LungWyrmJoint joint = joints.get(i);
            joint.getSegmentPosAndDirection(pos, dir, partialTicks);

            float dirYRot = (float) Math.atan2(-dir.x(), dir.z()),
                  dirXRot = (float) -Math.asin(Math.clamp(dir.y(), -1, 1));
            rot.rotationYXZ(Mth.PI - dirYRot, -dirXRot, 0);
            if (i == 0) {
                float yRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
                float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
                rot.rotationYXZ(
                        (180F -yRot) * Mth.DEG_TO_RAD,
                        -xRot * Mth.DEG_TO_RAD, 0
                );
            }

            int light = LevelRenderer.getLightColor(entity.level(), blockPos.set(pos.x(), pos.y(), pos.z()));

            float width = joint.radius * 2, height = joint.height, length = joint.length;
            float heightOffset = 0.5F * height;
            float inflation = 1.0F + joint.getInflationAmount(partialTicks) * 0.5F;

            poseStack.pushPose();
            if (i == 0) poseStack.translate(joint.getX(partialTicks) - eX, joint.getY(partialTicks) - eY + heightOffset, joint.getZ(partialTicks) - eZ);
            else poseStack.translate(pos.x() - eX, pos.y() - eY + heightOffset, pos.z() - eZ);
            poseStack.mulPose(rot);
            if (i == 0) poseStack.translate(0F, 0F, -0.5F * length);
            poseStack.mulPose(Axis.ZP.rotationDegrees(deathRotation + joint.getRoll(partialTicks)));

            poseStack.scale(inflation, inflation, 1);
            poseStack.scale(width, height, length);

            poseStack.translate(-0.5, -0.5, -0.5);

            dispatcher.renderSingleBlock(Blocks.DISPENSER.defaultBlockState(), poseStack, buffer, light, overlay);
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(LungWyrmEntity pEntity) {
        return null;
    }
}

