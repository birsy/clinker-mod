package birsy.clinker.client.entity.mogul;

import birsy.clinker.client.necromancer.render.NecromancerEntityRenderer;
import birsy.clinker.client.necromancer.render.Skin;
import birsy.clinker.client.entity.mogul.layer.MogulRobesLayer;
import birsy.clinker.client.entity.mogul.layer.GnomadMogulWeaponLayer;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MogulRenderer extends NecromancerEntityRenderer<GnomadMogulEntity, MogulSkeleton> {
    protected static final MogulSkin SKIN = new MogulSkin();
    protected static final ResourceLocation MOGUL_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/mogul/gnomad_mogul.png");

    public MogulRenderer(EntityRendererProvider.Context context) {
        super(context, MogulSkeleton::new, MogulAnimator::new, 2.0F);
        this.addLayer(new MogulRobesLayer(this));
        this.addLayer(new GnomadMogulWeaponLayer(this));
    }

    @Override
    public Skin<MogulSkeleton> getSkin(GnomadMogulEntity parent) {
        return SKIN;
    }

    @Override
    public void render(GnomadMogulEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();
        poseStack.scale(0.95F, 0.95F, 0.95F);
        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
        poseStack.popPose();
    }

    @Override
    public void renderSkin(GnomadMogulEntity entity, MogulSkeleton skeleton, Skin<MogulSkeleton> skin, int ticksExisted, float partialTicks, PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        poseStack.pushPose();
        poseStack.translate(0, 16 * entity.getHeightOffset(partialTicks), 0);
        if (entity.isSitting()) poseStack.scale(1, 0.5F, 1);
        super.renderSkin(entity, skeleton, skin, ticksExisted, partialTicks, poseStack, consumer, packedLight, packedOverlay, r, g, b, a);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(GnomadMogulEntity entity, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public ResourceLocation getTextureLocation(GnomadMogulEntity entity) {
        return MOGUL_LOCATION;
    }
}
