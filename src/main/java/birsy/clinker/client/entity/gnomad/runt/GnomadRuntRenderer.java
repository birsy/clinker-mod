package birsy.clinker.client.entity.gnomad.runt;

import birsy.clinker.client.entity.gnomad.layer.HeldSuppliesLayer;
import birsy.clinker.client.entity.layer.BasicSkinnedEntityLayer;
import birsy.clinker.client.entity.layer.DebugSurveyorWheelRenderer;
import birsy.clinker.common.entity.gnomad.GnomadRuntEntity;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.animation.Animator;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GnomadRuntRenderer extends NecromancerEntityRenderer<GnomadRuntEntity, GnomadRuntSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/gnomad_runt.png");

    public GnomadRuntRenderer(EntityRendererProvider.Context context) {
        super(context, 0.2F);

        this.addLayer(new BasicSkinnedEntityLayer<>(this, e -> VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION), e -> GnomadRuntSkin.INSTANCE));
        this.addLayer(new HeldSuppliesLayer<>(this, (skeleton) -> skeleton.deliveryGrasp, (matrixStack) -> { matrixStack.translate(0, 0.0, -6); }));
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
