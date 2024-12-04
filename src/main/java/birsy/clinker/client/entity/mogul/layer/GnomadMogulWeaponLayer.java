package birsy.clinker.client.entity.mogul.layer;

import birsy.clinker.client.entity.mogul.MogulRenderer;
import birsy.clinker.client.entity.mogul.MogulSkeleton;
import birsy.clinker.client.entity.mogul.MogulWeaponModels;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class GnomadMogulWeaponLayer extends NecromancerEntityRenderLayer<GnomadMogulEntity, MogulSkeleton> {
    public GnomadMogulWeaponLayer(MogulRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, GnomadMogulEntity pLivingEntity, MogulSkeleton pSkeleton, float pPartialTicks) {
        pPoseStack.pushPose();
        pPoseStack.translate(0, 16 * pLivingEntity.getHeightOffset(pPartialTicks), 0);
        if (pSkeleton != null) {
            for (Bone bone : pSkeleton.MogulRightArmGrasp.parentChain) bone.transform(pPoseStack, pPartialTicks);
            pSkeleton.MogulRightArmGrasp.transform(pPoseStack, pPartialTicks);
            pPoseStack.translate(-0.6, 0, 0);
            float scale = 1.2F;
            pPoseStack.scale(scale, scale, scale);
            MogulWeaponModels.WARHOOK.render(
                    pPoseStack,
                    pBuffer.getBuffer(RenderType.entityCutoutNoCull(MogulWeaponModels.WARHOOK_TEXTURE_LOCATION)),
                    pPackedLight, this.renderer.getOverlayCoords(pLivingEntity),
                    0.8F, 0.8F, 0.8F, 1);
        }
        pPoseStack.popPose();
    }
}
