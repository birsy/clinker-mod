package birsy.clinker.client.render.entity.layer;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.util.Random;

public class BlinkLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static ResourceLocation blinkTexture;
    private static float blinkDuration;
    private static float blinkFrequency;

    //Simple, hacky blink renderer that works without any additional code to the entity itself.
    public BlinkLayer(RenderLayerParent<T, M> parent, ResourceLocation blinkTexture, float blinkDuration, float blinkFrequency) {
        super(parent);
        this.blinkTexture = blinkTexture;
        this.blinkDuration = blinkDuration;
        this.blinkFrequency = blinkFrequency;
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!pLivingEntity.isInvisible()) {
            float random = MathUtils.awfulRandom((float)(Math.round(pAgeInTicks * 0.3F + (pLivingEntity.getId() * 1234.0F))));
            boolean blinking = true;//random > 0.95;
            if (blinking || pLivingEntity.getPose() == Pose.SLEEPING || pLivingEntity.invulnerableTime > 15) {
                RenderType BLINK = RenderType.entityCutout(blinkTexture);
                VertexConsumer vertexconsumerBlink = pBuffer.getBuffer(BLINK);
                this.getParentModel().renderToBuffer(pMatrixStack, vertexconsumerBlink, pPackedLight, LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

}
