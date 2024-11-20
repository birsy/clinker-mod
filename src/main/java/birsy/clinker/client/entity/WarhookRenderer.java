package birsy.clinker.client.entity;

import birsy.clinker.client.entity.mogul.MogulWeaponModels;
import birsy.clinker.common.world.entity.projectile.WarhookEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class WarhookRenderer extends EntityRenderer<WarhookEntity> {
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

        consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(MogulWeaponModels.WARHOOK_LOCATION));
        pPoseStack.pushPose();
        pPoseStack.translate(0, 1.5F / 16.0F, 0);

        pPoseStack.mulPose(rotation);
        pPoseStack.translate(0, -1, 0);
        pPoseStack.scale(1.0F / 16.0F, 1.0F / 16.0F, 1.0F / 16.0F);
        MogulWeaponModels.WARHOOK.render(null, pPoseStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }



    @Override
    public ResourceLocation getTextureLocation(WarhookEntity pEntity) {
        return MogulWeaponModels.WARHOOK_LOCATION;
    }
}
