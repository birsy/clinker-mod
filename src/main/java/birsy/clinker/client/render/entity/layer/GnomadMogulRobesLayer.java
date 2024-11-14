package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.model.entity.GnomadMogulSkeletonFactory;
import birsy.clinker.client.render.entity.base.EntityRenderLayer;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class GnomadMogulRobesLayer extends EntityRenderLayer<GnomadMogulEntity, GnomadMogulSkeletonFactory.GnomadMogulSkeleton> {
    private static final ResourceLocation MOGUL_ROBES_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/mogul/gnomad_mogul_robes.png");

    public GnomadMogulRobesLayer(InterpolatedEntityRenderer<GnomadMogulEntity, GnomadMogulSkeletonFactory.GnomadMogulSkeleton> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, GnomadMogulEntity pLivingEntity, GnomadMogulSkeletonFactory.GnomadMogulSkeleton pSkeleton, float pPartialTicks) {
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0);
        Vec3 robeColor = Vec3.fromRGB24(pLivingEntity.getRobeColor());
        pPoseStack.pushPose();
        pPoseStack.translate(0, 16 * pLivingEntity.getHeightOffset(pPartialTicks), 0);
        if (pLivingEntity.isSitting()) pPoseStack.scale(1, 0.5F, 1);
        if (pSkeleton != null) pSkeleton.render(pPartialTicks, pPoseStack, pBuffer.getBuffer(RenderType.entityCutoutNoCullZOffset(MOGUL_ROBES_LOCATION)), pPackedLight, packedOverlay,
                (float) robeColor.x, (float) robeColor.y, (float) robeColor.z, 1.0F);
        pPoseStack.popPose();
    }
}
