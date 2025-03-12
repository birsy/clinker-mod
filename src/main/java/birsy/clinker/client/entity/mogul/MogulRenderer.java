package birsy.clinker.client.entity.mogul;

import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
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
import net.minecraft.world.phys.Vec3;

public class MogulRenderer extends NecromancerEntityRenderer<GnomadMogulEntity, MogulSkeleton> {
    private static final ResourceLocation RENDERTYPE = Clinker.resource("mogul_entity");
    private static final ResourceLocation CLOAK_RENDERTYPE = Clinker.resource("mogul_entity_cloak");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/mogul/gnomad_mogul.png");
    private static final ResourceLocation ROBE_TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/mogul/gnomad_mogul_robes.png");

    public MogulRenderer(EntityRendererProvider.Context context) {
        super(context, 2.0F);
        this.addLayer(new NecromancerSkinEntityRenderLayer<>(this) {
            @Override
            public RenderType getRenderType(GnomadMogulEntity entity) {
                return VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION);
            }
            @Override
            public Skin getSkin(GnomadMogulEntity parent) {
                return MogulSkin.MOGUL_SKIN;
            }
            @Override
            protected void renderSkin(GnomadMogulEntity parent, MogulSkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
                if (parent.hurtTime > 0) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
                super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
            }
        });
        this.addLayer(new NecromancerSkinEntityRenderLayer<>(this) {
            @Override
            public RenderType getRenderType(GnomadMogulEntity entity) {
                return VeilRenderType.get(CLOAK_RENDERTYPE, ROBE_TEXTURE_LOCATION);
            }
            @Override
            public Skin getSkin(GnomadMogulEntity parent) {
                return MogulSkin.MOGUL_SKIN;
            }
            @Override
            protected void renderSkin(GnomadMogulEntity parent, MogulSkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
                if (parent.hurtTime > 0) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
                Vec3 robeColor = Vec3.fromRGB24(parent.getRobeColor());
                renderer.setColor((float) robeColor.x, (float) robeColor.y, (float) robeColor.z, 1.0F);
                super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
            }
        });
    }

    @Override
    public MogulSkeleton createSkeleton(GnomadMogulEntity parent) {
        return new MogulSkeleton();
    }

    @Override
    public Animator<GnomadMogulEntity, MogulSkeleton> createAnimator(GnomadMogulEntity parent, MogulSkeleton skeleton) {
        return new MogulAnimator(parent, skeleton);
    }

    @Override
    public void render(GnomadMogulEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();
        poseStack.scale(0.95F, 0.95F, 0.95F);
        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(GnomadMogulEntity entity) {
        return TEXTURE_LOCATION;
    }
}
