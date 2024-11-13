package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.entity.GnomadMogulSkeletonFactory;
import birsy.clinker.client.model.entity.MogulWarhookModel;
import birsy.clinker.client.render.entity.base.EntityRenderLayer;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class GnomadMogulWeaponLayer extends EntityRenderLayer<GnomadMogulEntity, GnomadMogulSkeletonFactory.GnomadMogulSkeleton> {
    private static final ResourceLocation WARHOOK_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/mogul_warhook.png");
    private static final ResourceLocation WARHOOK_CHAIN_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/mogul_warhook_chain.png");
    private static MogulWarhookModel.MogulWarhookSkeleton WARHOOK = MogulWarhookModel.skeleton;

    public GnomadMogulWeaponLayer(InterpolatedEntityRenderer<GnomadMogulEntity, GnomadMogulSkeletonFactory.GnomadMogulSkeleton> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, GnomadMogulEntity pLivingEntity, GnomadMogulSkeletonFactory.GnomadMogulSkeleton pSkeleton, float pPartialTicks) {
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0);
        pPoseStack.pushPose();
        pPoseStack.translate(0, 16 * pLivingEntity.getHeightOffset(pPartialTicks), 0);
        if (pSkeleton != null) {
            for (InterpolatedBone interpolatedBone : pSkeleton.MogulRightArmGrasp.parentChain) {
                interpolatedBone.transform(pPoseStack, pPartialTicks);
            }
            pSkeleton.MogulRightArmGrasp.transform(pPoseStack, pPartialTicks);
            pPoseStack.translate(-0.6, 0, 1);
            float scale = 1.2F;
            pPoseStack.scale(scale, scale, scale);

            WARHOOK.render(pPartialTicks, pPoseStack, pBuffer.getBuffer(RenderType.entityCutoutNoCull(WARHOOK_LOCATION)), pPackedLight, packedOverlay, 0.8F, 0.8F, 0.8F, 1);
        }
        pPoseStack.popPose();
    }
}
