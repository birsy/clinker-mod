package birsy.clinker.client.render.entity;

import birsy.clinker.client.model.entity.GnomadMogulSkeletonFactory;
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

public class GnomadMogulRenderer extends InterpolatedEntityRenderer<GnomadEntity, PlaceholderGnomadSkeletonFactory.PlaceholderGnomadSkeleton> {
    private static final ResourceLocation MOGUL_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/gnomad_mogul.png");

    public GnomadMogulRenderer(EntityRendererProvider.Context context) {
        super(context, new GnomadMogulSkeletonFactory(), 2.0F);
    }

    @Override
    public void render(GnomadEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();
        poseStack.scale(0.95F, 0.95F, 0.95F);
        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
        poseStack.popPose();
    }

    public RenderType getRenderType(GnomadEntity entity) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    public ResourceLocation getTextureLocation(GnomadEntity entity) {
        return MOGUL_LOCATION;
    }
}
