package birsy.clinker.client.entity.gnomad.basic;

import birsy.clinker.client.entity.layer.BasicSkinnedEntityLayer;
import birsy.clinker.client.entity.layer.DebugSurveyorWheelRenderer;
import birsy.clinker.client.entity.layer.HeldItemsLayer;
import birsy.clinker.common.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.animation.Animator;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GnomadRenderer extends NecromancerEntityRenderer<GnomadEntity, GnomadSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/gnomad.png");

    public GnomadRenderer(EntityRendererProvider.Context context) {
        super(context, 0.5F);

        this.addLayer(new BasicSkinnedEntityLayer<>(this, e -> VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION), e -> GnomadSkin.INSTANCE));
        this.addLayer(new HeldItemsLayer<>(this));
        this.addLayer(new DebugSurveyorWheelRenderer<>(this, (entity) -> entity.getAnimator().stepCounter));
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
