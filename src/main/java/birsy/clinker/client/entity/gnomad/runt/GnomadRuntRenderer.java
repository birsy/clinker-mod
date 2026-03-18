package birsy.clinker.client.entity.gnomad.runt;

import birsy.clinker.client.entity.gnomad.layer.HeldSuppliesLayer;
import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingSupplierEntity;
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

public class GnomadRuntRenderer extends NecromancerEntityRenderer<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/gnomad_runt.png");

    public GnomadRuntRenderer(EntityRendererProvider.Context context) {
        super(context, 0.2F);

        this.addLayer(new NecromancerSkinEntityRenderLayer<>(this) {
            @Override
            public RenderType getRenderType(SquadTestingSupplierEntity entity) {
                return VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION);
            }

            @Override
            public Skin getSkin(SquadTestingSupplierEntity parent) {
                return GnomadRuntSkin.INSTANCE;
            }

            @Override
            protected void renderSkin(SquadTestingSupplierEntity parent, GnomadRuntSkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
                if (parent.hurtTime > 0 || !parent.isAlive()) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
                super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
            }
        });
        this.addLayer(new HeldSuppliesLayer<>(this));
    }

    @Override
    public GnomadRuntSkeleton createSkeleton(SquadTestingSupplierEntity parent) {
        return new GnomadRuntSkeleton();
    }

    @Override
    public Animator<SquadTestingSupplierEntity, GnomadRuntSkeleton> createAnimator(SquadTestingSupplierEntity parent, GnomadRuntSkeleton skeleton) {
        return new GnomadRuntAnimator(parent, skeleton);
    }

    @Override
    public ResourceLocation getTextureLocation(SquadTestingSupplierEntity entity) {
        return TEXTURE_LOCATION;
    }
}
