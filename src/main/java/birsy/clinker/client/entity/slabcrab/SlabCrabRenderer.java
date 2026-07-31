package birsy.clinker.client.entity.slabcrab;

import birsy.clinker.client.entity.layer.BasicSkinnedEntityLayer;
import birsy.clinker.common.entity.SlabCrabEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.necromancer.animation.Animator;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SlabCrabRenderer extends NecromancerEntityRenderer<SlabCrabEntity, SlabCrabSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/slab_crab.png");

    public SlabCrabRenderer(EntityRendererProvider.Context context) {
        super(context, 0.5F);
        this.addLayer(new BasicSkinnedEntityLayer<>(this, e -> VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION), e -> SlabCrabSkin.INSTANCE));
    }

    @Override
    public SlabCrabSkeleton createSkeleton(SlabCrabEntity parent) {
        return new SlabCrabSkeleton();
    }

    @Override
    public Animator<SlabCrabEntity, SlabCrabSkeleton> createAnimator(SlabCrabEntity parent, SlabCrabSkeleton skeleton) {
        return new SlabCrabAnimator(parent, skeleton);
    }

    @Override
    public void render(SlabCrabEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SlabCrabEntity entity) {
        return TEXTURE_LOCATION;
    }
}
