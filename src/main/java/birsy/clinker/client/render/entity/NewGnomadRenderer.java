package birsy.clinker.client.render.entity;

import birsy.clinker.client.model.entity.PlaceholderGnomadSkeletonFactory;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class NewGnomadRenderer extends InterpolatedEntityRenderer<GnomadEntity, PlaceholderGnomadSkeletonFactory.PlaceholderGnomadSkeleton> {
    private static final ResourceLocation PLACEHOLDER_GNOMAD_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/gnomad_placeholder.png");

    public NewGnomadRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderGnomadSkeletonFactory(), 1.0F);
    }

    @Override
    public void render(GnomadEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();
        poseStack.scale(0.8F, 0.8F, 0.8F);
        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
        poseStack.popPose();
    }

    @Override
    public void renderModel(GnomadEntity pEntity, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        VertexConsumer vertexconsumer = pBuffer.getBuffer(this.getRenderType(pEntity));
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(pEntity, 0);

        if (pEntity.getSkeleton() != null) pEntity.getSkeleton().render(pPartialTicks, poseStack, vertexconsumer, pPackedLight, packedOverlay, 93.0F / 255.0F, 71.0F / 255.0F, 62.0F / 255.0F, 1.0F);
    }

    public RenderType getRenderType(GnomadEntity entity) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    public ResourceLocation getTextureLocation(GnomadEntity entity) {
        return PLACEHOLDER_GNOMAD_LOCATION;
    }
}
