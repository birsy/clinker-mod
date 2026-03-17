package birsy.clinker.client.entity.slabcrab;

import birsy.clinker.common.world.entity.SlabCrabEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.necromancer.animation.Animator;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerSkinEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.Skin;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SlabCrabRenderer extends NecromancerEntityRenderer<SlabCrabEntity, SlabCrabSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/slab_crab.png");

    public SlabCrabRenderer(EntityRendererProvider.Context context) {
        super(context, 0.5F);
        this.addLayer(new NecromancerSkinEntityRenderLayer<>(this) {
            @Override
            public RenderType getRenderType(SlabCrabEntity entity) {
                return VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION);
            }
            @Override
            public Skin getSkin(SlabCrabEntity parent) {
                return SlabCrabSkin.INSTANCE;
            }
            @Override
            protected void renderSkin(SlabCrabEntity parent, SlabCrabSkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
                if (parent.hurtTime > 0 || !parent.isAlive()) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
                super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
            }
        });
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
