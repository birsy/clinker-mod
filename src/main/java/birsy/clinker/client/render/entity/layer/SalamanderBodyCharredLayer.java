package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.render.entity.model.SalamanderBodyModel;
import birsy.clinker.common.world.entity.salamanderOLD.AbstractSalamanderPartEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SalamanderBodyCharredLayer<T extends AbstractSalamanderPartEntity, M extends SalamanderBodyModel<T>> extends RenderLayer<T, M> {
    private static final RenderType CHARRED = RenderType.entityTranslucent(new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/charred_salamander_body.png"));

    public SalamanderBodyCharredLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!pLivingEntity.isInvisible()) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(CHARRED);
            float charredAmount = Mth.clamp(((float)(pLivingEntity.getOriginalSegmentID() - 2) * 2) / (float)pLivingEntity.getOriginalBodyLength(), 0.0F, 1.0F);
            this.getParentModel().renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F), 1.0F, 1.0F, 1.0F, charredAmount);
        }
    }
}
