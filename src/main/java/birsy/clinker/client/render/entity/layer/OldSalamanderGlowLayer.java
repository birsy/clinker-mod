package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.entity.model.AbstractSalamanderModel;
import birsy.clinker.common.world.entity.salamanderOLD.AbstractSalamanderPartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class OldSalamanderGlowLayer<T extends AbstractSalamanderPartEntity, M extends AbstractSalamanderModel<T>> extends RenderLayer<T, M> {
    private final RenderType GLOW;// = RenderType.entityTranslucent(new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body_glow.png"));
    private final RenderType CHARRED_GLOW;// = RenderType.entityTranslucent(new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/charred_salamander_body_glow.png"));
    private final RenderType FIRE;// = RenderType.entityTranslucent(new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body_fire_glow.png"));

    public OldSalamanderGlowLayer(RenderLayerParent<T, M> parent, ResourceLocation glow, ResourceLocation charredGlow, ResourceLocation fire) {
        super(parent);
        this.GLOW = ClinkerRenderTypes.entityUnlitTranslucent(glow);
        this.CHARRED_GLOW = ClinkerRenderTypes.entityUnlitTranslucent(charredGlow);
        this.FIRE = ClinkerRenderTypes.entityUnlitTranslucent(fire);
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

        VertexConsumer vertexconsumerCharredGlow = pBuffer.getBuffer(CHARRED_GLOW);
        float charredAmount = Mth.clamp(((float)(pLivingEntity.getOriginalSegmentID() - 2) * 2) / (float)pLivingEntity.getOriginalBodyLength(), 0.0F, 1.0F);
        this.getParentModel().renderToBuffer(pMatrixStack, vertexconsumerCharredGlow, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, charredAmount * flameAmount);
    }
}
