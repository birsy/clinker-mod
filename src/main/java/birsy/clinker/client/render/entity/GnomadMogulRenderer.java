package birsy.clinker.client.render.entity;

import birsy.clinker.client.model.entity.GnomadMogulSkeletonFactory;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.client.render.entity.layer.GnomadMogulRobesLayer;
import birsy.clinker.client.render.entity.layer.GnomadMogulWeaponLayer;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GnomadMogulRenderer extends InterpolatedEntityRenderer<GnomadMogulEntity, GnomadMogulSkeletonFactory.GnomadMogulSkeleton> {
    private static final ResourceLocation MOGUL_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/mogul/gnomad_mogul.png");

    public GnomadMogulRenderer(EntityRendererProvider.Context context) {
        super(context, new GnomadMogulSkeletonFactory(), 2.0F);
        this.addLayer(new GnomadMogulRobesLayer(this));
        this.addLayer(new GnomadMogulWeaponLayer(this));
    }

    @Override
    public void render(GnomadMogulEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();
        poseStack.scale(0.95F, 0.95F, 0.95F);
        //poseStack.scale(1, 0.1F, 1);

        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
        poseStack.popPose();
    }

    @Override
    public void renderModel(GnomadMogulEntity pEntity, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 16 * pEntity.getHeightOffset(pPartialTicks), 0);
        super.renderModel(pEntity, pPartialTicks, poseStack, pBuffer, pPackedLight);
        poseStack.popPose();
    }

    public RenderType getRenderType(GnomadMogulEntity entity) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    public ResourceLocation getTextureLocation(GnomadMogulEntity entity) {
        return MOGUL_LOCATION;
    }
}
