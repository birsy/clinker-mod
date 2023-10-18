package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.model.entity.SalamanderSkeletonFactory;
import birsy.clinker.client.render.entity.base.EntityRenderLayer;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SalamanderOverlayLayer extends EntityRenderLayer<NewSalamanderEntity, SalamanderSkeletonFactory.SalamanderSkeleton> {
    private static final ResourceLocation SALAMANDER_HEAD_GLOW_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head_glow.png");
    private static final ResourceLocation SALAMANDER_HEAD_FIRE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head_fire_glow.png");

    private static final ResourceLocation SALAMANDER_BODY_GLOW_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body_glow.png");
    private static final ResourceLocation SALAMANDER_BODY_FIRE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body_fire_glow.png");
    private static final ResourceLocation SALAMANDER_BODY_CHARRED_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/charred_salamander_body.png");
    private static final ResourceLocation SALAMANDER_BODY_CHARRED_GLOW_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/charred_salamander_body_glow.png");

    private static final ResourceLocation SALAMANDER_TAIL_GLOW_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_tail_glow.png");
    private static final ResourceLocation SALAMANDER_TAIL_FIRE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_tail_fire_glow.png");

    public SalamanderOverlayLayer(InterpolatedEntityRenderer<NewSalamanderEntity, SalamanderSkeletonFactory.SalamanderSkeleton> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, NewSalamanderEntity pLivingEntity, SalamanderSkeletonFactory.SalamanderSkeleton skeleton, float pPartialTicks) {
        int overlayCoords = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0);
        float age = pLivingEntity.tickCount + pPartialTicks;
        for (SalamanderSkeletonFactory.SalamanderSegmentSkeleton segment : skeleton.segments) {
            VertexConsumer vertexconsumer;
            float segmentOffset = 1.0F;
            float speed = 0.05F;
            float flameAmount = (Mth.sin((age * speed) + (segment.index * segmentOffset)) + 1.0F) / 2.0F;
            flameAmount *= flameAmount * flameAmount;
            float glowAmount = Mth.lerp(0.25F, flameAmount, 1.0F);

            if (segment instanceof SalamanderSkeletonFactory.SalamanderHeadSkeleton head) {
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(SALAMANDER_HEAD_GLOW_TEXTURE));
                head.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, 1.0F);
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(SALAMANDER_HEAD_FIRE_TEXTURE));
                head.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, flameAmount);

            } else if (segment instanceof SalamanderSkeletonFactory.SalamanderBodySkeleton body) {
                float charDistance = Mth.clamp(MathUtils.mapRange(2.0F, pLivingEntity.segments.size() - 2.0F, 0, 1, body.index), 0, 1);

                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(SALAMANDER_BODY_CHARRED_TEXTURE));
                body.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, charDistance);
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(SALAMANDER_BODY_CHARRED_GLOW_TEXTURE));
                body.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, charDistance * glowAmount);
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(SALAMANDER_BODY_GLOW_TEXTURE));
                body.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, glowAmount);
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(SALAMANDER_BODY_FIRE_TEXTURE));
                body.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, flameAmount);

            } else if (segment instanceof SalamanderSkeletonFactory.SalamanderTailSkeleton tail) {
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(SALAMANDER_TAIL_GLOW_TEXTURE));
                tail.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, glowAmount);
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(SALAMANDER_TAIL_FIRE_TEXTURE));
                tail.render(pPartialTicks, pPoseStack, vertexconsumer, pPackedLight, overlayCoords, 1.0F, 1.0F, 1.0F, flameAmount);

            }
        }
    }
}
