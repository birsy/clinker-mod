package birsy.clinker.client.render.entity;

import birsy.clinker.client.CatenaryArc;
import birsy.clinker.client.model.entity.MogulWarhookModel;
import birsy.clinker.client.render.RenderUtils;
import birsy.clinker.common.world.entity.projectile.WarhookEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class WarhookRenderer extends EntityRenderer<WarhookEntity> {
    private static final ResourceLocation WARHOOK_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/mogul_warhook.png");
    private static final ResourceLocation WARHOOK_CHAIN_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/mogul_warhook_chain.png");
    private static MogulWarhookModel.MogulWarhookSkeleton WARHOOK = MogulWarhookModel.skeleton;

    public WarhookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(WarhookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        Vec3 tailDirection = pEntity.getDirection(pPartialTick);
        float yRot = (float) Mth.atan2(tailDirection.x(), tailDirection.z());
        float xRot = (float) Mth.atan2(Math.sqrt(tailDirection.x() * tailDirection.x() + tailDirection.z() * tailDirection.z()), tailDirection.y());
        Quaternionf rotation = new Quaternionf(new AxisAngle4d(Mth.HALF_PI + Mth.lerp(pPartialTick, pEntity.pRoll, pEntity.roll), tailDirection.toVector3f())).mul(new Quaternionf().rotateYXZ(yRot, xRot, 0));

        VertexConsumer consumer;
        if (pEntity.hasOwner()) {
            consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(WARHOOK_CHAIN_LOCATION));
            Vec3 startPos = pEntity.getOwnerPos(pPartialTick).subtract(pEntity.getPosition(pPartialTick));
            Vec3 endPos = new Vec3(rotation.transform(new Vector3f(1 / 16.0F, -2.4F, 0)));
            this.drawChain(consumer, pPoseStack, startPos, endPos, 4, 16, pEntity);
        }

        consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(WARHOOK_LOCATION));
        pPoseStack.pushPose();
        pPoseStack.translate(0, 1.5F / 16.0F, 0);

        pPoseStack.mulPose(rotation);
        pPoseStack.translate(0, -1, 0);
        pPoseStack.scale(1.0F / 16.0F, 1.0F / 16.0F, 1.0F / 16.0F);
        //pPoseStack.translate(-1.8, 0, 0);
        WARHOOK.render(pPartialTick, pPoseStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    private void drawChain(VertexConsumer consumer, PoseStack pPoseStack, Vec3 startPos, Vec3 endPos, float length, int segments, WarhookEntity entity) {
        CatenaryArc arc = new CatenaryArc(new Vector3d(startPos.toVector3f()), new Vector3d(endPos.toVector3f()), length);

        float add = 1.0F / segments;
        float distance = 0;
        Vec3 previousPoint = startPos;
        for (float t = 1; t > 0; t-=add) {
            double height = arc.evaluateHeight(t);
            Vec3 point = startPos.lerp(endPos, t).multiply(1, 0, 1).add(0, height, 0);

            double nextHeight = arc.evaluateHeight(t + add);
            Vec3 nextPoint = startPos.lerp(endPos, t + add).multiply(1, 0, 1).add(0, nextHeight, 0);

           // DebugRenderUtil.renderLine(pPoseStack, consumer, previousPoint, point, 1, 1, 1, 1);
            distance += drawChainSegment(consumer, pPoseStack, previousPoint, previousPoint.subtract(point).normalize(), point, point.subtract(nextPoint).normalize(), distance, entity);

            previousPoint = point;
        }
    }


    private float drawChainSegment(VertexConsumer consumer, PoseStack pPoseStack, Vec3 pos1, Vec3 dir1, Vec3 pos2, Vec3 dir2, float totalDistance, WarhookEntity entity) {
        float vOffset1 = totalDistance / 4.0F;
        float distance = (float) pos1.distanceTo(pos2);
        float vOffset2 = vOffset1 + (distance / 4.0F);
        float uOffset = 3.0F / 16.0F;
        float scale = uOffset * 0.5F;

        Vector3f localForward1 = dir1.toVector3f();
        Vector3f localRight1 = new Vector3f(0, 1, 0).cross(localForward1).normalize();
        Vector3f localUp1 = localForward1.cross(localRight1, new Vector3f()).normalize();

        Vector3f localForward2 = dir2.toVector3f();
        Vector3f localRight2 = new Vector3f(0, 1, 0).cross(localForward2).normalize();
        Vector3f localUp2 = localForward2.cross(localRight2, new Vector3f()).normalize();

        int light1 = LightTexture.pack(entity.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(pos1)), entity.level().getBrightness(LightLayer.SKY, BlockPos.containing(pos1)));
        int light2 = LightTexture.pack(entity.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(pos2)), entity.level().getBrightness(LightLayer.SKY, BlockPos.containing(pos2)));

        // vertical strip
        RenderUtils.drawFaceBetweenPoints(consumer, pPoseStack, scale, pos1, localForward1, localRight1, localUp1, light1, OverlayTexture.NO_OVERLAY, 0, vOffset1,
                                                           pos2, localForward2, localRight2, localUp2, light2, OverlayTexture.NO_OVERLAY, uOffset, vOffset2);
        // horizontal strip
        RenderUtils.drawFaceBetweenPoints(consumer, pPoseStack, scale, pos1, localForward1, localUp1, localRight1, light1, OverlayTexture.NO_OVERLAY, uOffset, vOffset1,
                                                           pos2, localForward2, localUp2, localRight2, light2, OverlayTexture.NO_OVERLAY, 2 * uOffset, vOffset2);
        return distance;
    }

    @Override
    public ResourceLocation getTextureLocation(WarhookEntity pEntity) {
        return WARHOOK_LOCATION;
    }
}
