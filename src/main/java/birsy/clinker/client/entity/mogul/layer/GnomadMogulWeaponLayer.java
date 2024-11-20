package birsy.clinker.client.entity.mogul.layer;

import birsy.clinker.client.entity.mogul.MogulRenderer;
import birsy.clinker.client.entity.mogul.MogulSkeleton;
import birsy.clinker.client.entity.mogul.MogulWeaponModels;
import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.render.NecromancerEntityRenderLayer;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

public class GnomadMogulWeaponLayer extends NecromancerEntityRenderLayer<GnomadMogulEntity, MogulSkeleton> {
    public GnomadMogulWeaponLayer(MogulRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, GnomadMogulEntity pLivingEntity, MogulSkeleton pSkeleton, float pPartialTicks) {
        pPoseStack.pushPose();
        pPoseStack.translate(0, 16 * pLivingEntity.getHeightOffset(pPartialTicks), 0);
        if (pSkeleton != null) {
            int packedOverlay = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0);
            for (Bone bone : pSkeleton.MogulRightArmGrasp.parentChain) {
                bone.transform(pPoseStack, pPartialTicks);
            }
            pSkeleton.MogulRightArmGrasp.transform(pPoseStack, pPartialTicks);
            pPoseStack.translate(-0.6, 0, 1);
            float scale = 1.2F;
            pPoseStack.scale(scale, scale, scale);
            MogulWeaponModels.WARHOOK.render(
                    pSkeleton.MogulRightArmGrasp,
                    pPoseStack,
                    pBuffer.getBuffer(RenderType.entityCutoutNoCull(MogulWeaponModels.WARHOOK_LOCATION)),
                    pPackedLight, packedOverlay,
                    0.8F, 0.8F, 0.8F, 1);
        }
        pPoseStack.popPose();
    }
}
