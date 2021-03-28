package birsy.clinker.client.render.entity.layers;

import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.common.entity.monster.GnomadAxemanEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanBuffLayer extends LayerRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> {
    private ResourceLocation BUFF_TEXTURE;
    private final GnomadAxemanModel<GnomadAxemanEntity> gnomadAxemanModel = new GnomadAxemanModel<>(2.0F);

    public GnomadAxemanBuffLayer(IEntityRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> renderer, ResourceLocation buffTexture) {
        super(renderer);
        BUFF_TEXTURE = buffTexture;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, GnomadAxemanEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.isBuffed()) {
            float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
            this.gnomadAxemanModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getEntityModel().copyModelAttributesTo(this.gnomadAxemanModel);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEnergySwirl(this.BUFF_TEXTURE, f * 0.01F, f * 0.01F));
            this.gnomadAxemanModel.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.gnomadAxemanModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }
}
