package birsy.clinker.client.entity.gnomad.runt;

import birsy.clinker.client.entity.gnomad.layer.HeldSuppliesLayer;
import birsy.clinker.client.entity.layer.DebugSurveyorWheelRenderer;
import birsy.clinker.common.world.entity.gnomad.GnomadRuntEntity;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.animation.Animator;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerSkinEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.Skin;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GnomadRuntRenderer extends NecromancerEntityRenderer<GnomadRuntEntity, GnomadRuntSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/gnomad_runt.png");

    public GnomadRuntRenderer(EntityRendererProvider.Context context) {
        super(context, 0.2F);

        this.addLayer(new NecromancerSkinEntityRenderLayer<>(this) {
            @Override
            public RenderType getRenderType(GnomadRuntEntity entity) {
                return VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION);
            }

            @Override
            public Skin getSkin(GnomadRuntEntity parent) {
                return GnomadRuntSkin.INSTANCE;
            }

            @Override
            protected void renderSkin(GnomadRuntEntity parent, GnomadRuntSkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
                if (parent.hurtTime > 0 || !parent.isAlive()) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
                super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
            }
        });
        this.addLayer(new HeldSuppliesLayer<>(this));
        this.addLayer(new DebugSurveyorWheelRenderer<>(this, (entity) -> entity.getAnimator().stepCounter));
    }

    @Override
    public GnomadRuntSkeleton createSkeleton(GnomadRuntEntity parent) {
        return new GnomadRuntSkeleton();
    }

    @Override
    public Animator<GnomadRuntEntity, GnomadRuntSkeleton> createAnimator(GnomadRuntEntity parent, GnomadRuntSkeleton skeleton) {
        return new GnomadRuntAnimator(parent, skeleton);
    }

    @Override
    public ResourceLocation getTextureLocation(GnomadRuntEntity entity) {
        return TEXTURE_LOCATION;
    }
}
