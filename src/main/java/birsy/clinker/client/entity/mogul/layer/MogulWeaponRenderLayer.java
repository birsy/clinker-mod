package birsy.clinker.client.entity.mogul.layer;

import birsy.clinker.client.entity.mogul.MogulRenderer;
import birsy.clinker.client.entity.mogul.MogulSkeleton;
import birsy.clinker.client.entity.mogul.MogulWeaponSkins;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerSkinEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.Skin;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.Nullable;

public class MogulWeaponRenderLayer extends NecromancerSkinEntityRenderLayer<GnomadMogulEntity, MogulSkeleton> {
    private static final Skin WARHOOK_SKIN =
            MogulWeaponSkins.createWarhookModel("MogulRightArmGrasp", MogulSkeleton.STATIC_INSTANCE);

    public MogulWeaponRenderLayer(NecromancerEntityRenderer<GnomadMogulEntity, MogulSkeleton> renderer) {
        super(renderer);
    }

    @Override
    public @Nullable RenderType getRenderType(GnomadMogulEntity parent) {
        return VeilRenderType.get(MogulRenderer.RENDERTYPE, MogulWeaponSkins.WARHOOK_TEXTURE_LOCATION);
    }

    @Override
    public @Nullable Skin getSkin(GnomadMogulEntity parent) {
        return WARHOOK_SKIN;
    }

    @Override
    protected void renderSkin(GnomadMogulEntity parent, MogulSkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        if (parent.hurtTime > 0 || !parent.isAlive()) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);

        super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
    }
}
