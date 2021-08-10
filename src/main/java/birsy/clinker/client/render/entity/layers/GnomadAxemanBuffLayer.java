package birsy.clinker.client.render.entity.layers;

import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanBuffLayer extends RenderLayer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> {
    private ResourceLocation BUFF_TEXTURE;
    private final GnomadAxemanModel<GnomadAxemanEntity> gnomadAxemanModel = new GnomadAxemanModel<>(1.0F);

    public GnomadAxemanBuffLayer(RenderLayerParent<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> renderer, ResourceLocation buffTexture) {
        super(renderer);
        BUFF_TEXTURE = buffTexture;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GnomadAxemanEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.isBuffed()) {
            float f = (float)entitylivingbaseIn.tickCount + partialTicks;
            this.gnomadAxemanModel.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getParentModel().copyPropertiesTo(this.gnomadAxemanModel);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.energySwirl(this.BUFF_TEXTURE, f * 0.01F, f * 0.01F));
            this.gnomadAxemanModel.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.gnomadAxemanModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }
}
