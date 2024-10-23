package birsy.clinker.client.render.entity;

import birsy.clinker.common.world.entity.projectile.FlechetteEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class FlechetteRenderer extends EntityRenderer<FlechetteEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/flechette.png");

    public FlechetteRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(FlechetteEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(pEntity)));
        int packedOverlay = OverlayTexture.NO_OVERLAY;

        pPoseStack.pushPose();
        pPoseStack.translate(0, pEntity.getBbHeight() * 0.5F, 0);
        pPoseStack.mulPose(pEntity.getOrientation(pPartialTick));
        float scale = 0.7F;
        float scaleFactor = 1.0F - ((float) pEntity.tickCount / FlechetteEntity.MAX_LIFETIME);
        scaleFactor = Mth.sqrt(scaleFactor);
        scale *= scaleFactor;
        pPoseStack.scale(scale, scale, scale);

        float width = 1.5F/16.0F, height = 5.5F/16.0F;
        float u0 = 0.0F, u1 = width*2F, v0 = 0.0F, v1 = height*2F;
        for (int i = 0; i < 4; i++) {
            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(90 * i +  45));
            // draw quad
            Matrix4f matrix4f = pPoseStack.last().pose();
            Matrix3f matrix3f = pPoseStack.last().normal();

            consumer.vertex(matrix4f, width, height, 0F)
                    .color(1F,1F,1F,1F)
                    .uv(u0, v0)
                    .overlayCoords(packedOverlay).uv2(pPackedLight)
                    .normal(matrix3f, 0, 0, 1).endVertex();
            consumer.vertex(matrix4f, -width, height, 0F)
                    .color(1F,1F,1F,1F)
                    .uv(u1, v0)
                    .overlayCoords(packedOverlay).uv2(pPackedLight)
                    .normal(matrix3f, 0, 0, 1).endVertex();
            consumer.vertex(matrix4f, -width, -height, 0F)
                    .color(1F,1F,1F,1F)
                    .uv(u1, v1)
                    .overlayCoords(packedOverlay).uv2(pPackedLight)
                    .normal(matrix3f, 0, 0, 1).endVertex();
            consumer.vertex(matrix4f, width, -height, 0F)
                    .color(1F,1F,1F,1F)
                    .uv(u0, v1)
                    .overlayCoords(packedOverlay).uv2(pPackedLight)
                    .normal(matrix3f, 0, 0, 1).endVertex();

            pPoseStack.popPose();
        }

        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FlechetteEntity pEntity) {
        return TEXTURE_LOCATION;
    }
}
