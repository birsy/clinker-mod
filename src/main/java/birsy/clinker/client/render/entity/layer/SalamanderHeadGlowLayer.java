package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.entity.model.SalamanderHeadModel;
import birsy.clinker.common.entity.Salamander.SalamanderHeadEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SalamanderHeadGlowLayer<T extends SalamanderHeadEntity, M extends SalamanderHeadModel<T>> extends RenderLayer<T, M> {
    private static final RenderType GLOW = ClinkerRenderTypes.entityTranslucentUnlit(new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head_glow.png"));
    private static final RenderType FIRE = ClinkerRenderTypes.entityTranslucentUnlit(new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head_fire_glow.png"));

    public SalamanderHeadGlowLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        VertexConsumer vertexconsumerFire = pBuffer.getBuffer(FIRE);
        float segmentOffset = -1.0F;
        float speed = 0.15F;
        float flameAmount = (Mth.sin(((pAgeInTicks + pPartialTicks) * speed) + (((-1 * pLivingEntity.getOriginalSegmentID()) + pLivingEntity.getOriginalBodyLength()) * segmentOffset)) + 1.0F) / 2.0F;
        flameAmount *= flameAmount;
        this.getParentModel().renderToBuffer(pMatrixStack, vertexconsumerFire, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, flameAmount);

        VertexConsumer vertexconsumerGlow = pBuffer.getBuffer(GLOW);
        this.getParentModel().renderToBuffer(pMatrixStack, vertexconsumerGlow, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, flameAmount);
    }
}
