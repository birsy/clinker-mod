package birsy.clinker.client.entity.gnomad.mogul;

import birsy.clinker.client.entity.gnomad.basic.GnomadSkin;
import birsy.clinker.client.entity.gnomad.mogul.layer.GnomadMogulWeaponRenderLayer;
import birsy.clinker.client.entity.layer.BasicSkinnedEntityLayer;
import birsy.clinker.client.entity.layer.DebugSurveyorWheelRenderer;
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

public class GnomadMogulRenderer extends NecromancerEntityRenderer<GnomadMogulEntity, GnomadMogulSkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation CLOAK_RENDERTYPE = Clinker.resource("entity/clinker_entity_layer");

    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/mogul/gnomad_mogul.png");
    private static final ResourceLocation ROBE_TEXTURE_LOCATION = Clinker.resource("textures/entity/gnomad/mogul/gnomad_mogul_robes.png");

    public GnomadMogulRenderer(EntityRendererProvider.Context context) {
        super(context, 2.0F);

        this.addLayer(new BasicSkinnedEntityLayer<>(this, e -> VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION), e -> GnomadMogulSkin.INSTANCE));
        this.addLayer(new BasicSkinnedEntityLayer<>(this,
                e -> VeilRenderType.get(CLOAK_RENDERTYPE, ROBE_TEXTURE_LOCATION), e -> GnomadMogulSkin.INSTANCE)
                .preRender((entity, skin, renderer) -> {
                    int packedRobeColor = entity.getRobeColor();
                    if (packedRobeColor == 0) packedRobeColor = 0x4d423c;
                    Vec3 robeColor = Vec3.fromRGB24(packedRobeColor);
                    renderer.setColor((float) robeColor.x, (float) robeColor.y, (float) robeColor.z, 1.0F);
                })
        );
        this.addLayer(new GnomadMogulWeaponRenderLayer(this));
        this.addLayer(new DebugSurveyorWheelRenderer<>(this, (entity) -> entity.getAnimator().stepCounter));
    }

    @Override
    public GnomadMogulSkeleton createSkeleton(GnomadMogulEntity parent) {
        return new GnomadMogulSkeleton();
    }

    @Override
    public Animator<GnomadMogulEntity, GnomadMogulSkeleton> createAnimator(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton) {
        return new GnomadMogulAnimator(parent, skeleton);
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
