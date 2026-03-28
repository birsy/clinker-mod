package birsy.clinker.client.entity.gnomad.basic;

import birsy.clinker.client.entity.layer.HeldItemsLayer;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
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

public class GnomadRenderer extends NecromancerEntityRenderer<GnomadEntity, GnomadSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/gnomad.png");

    public GnomadRenderer(EntityRendererProvider.Context context) {
        super(context, 0.5F);

        this.addLayer(new NecromancerSkinEntityRenderLayer<>(this) {
            @Override
            public RenderType getRenderType(GnomadEntity entity) {
                return VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION);
            }

            @Override
            public Skin getSkin(GnomadEntity parent) {
                return GnomadSkin.INSTANCE;
            }

            @Override
            protected void renderSkin(GnomadEntity parent, GnomadSkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
                if (parent.hurtTime > 0 || !parent.isAlive()) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
                super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
            }
        });
        this.addLayer(new HeldItemsLayer<>(this));
    }

    @Override
    public GnomadSkeleton createSkeleton(GnomadEntity parent) {
        return new GnomadSkeleton();
    }

    @Override
    public Animator<GnomadEntity, GnomadSkeleton> createAnimator(GnomadEntity parent, GnomadSkeleton skeleton) {
        return new GnomadAnimator(parent, skeleton);
    }

    @Override
    public ResourceLocation getTextureLocation(GnomadEntity entity) {
        return TEXTURE_LOCATION;
    }
}
