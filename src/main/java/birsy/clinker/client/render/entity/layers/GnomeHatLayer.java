package birsy.clinker.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;

import birsy.clinker.client.render.entity.model.GnomeModel;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomeHatLayer extends LayerRenderer<GnomeEntity, GnomeModel<GnomeEntity>> {
   private static final ResourceLocation GNOME_HAT = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnome/gnome_hat.png");

   public GnomeHatLayer(IEntityRenderer<GnomeEntity, GnomeModel<GnomeEntity>> rendererIn) {
      super(rendererIn);
   }
   
   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, GnomeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!entitylivingbaseIn.isInvisible()) {
         float[] afloat = entitylivingbaseIn.getHatColor(entitylivingbaseIn).getColorComponentValues();
         renderCutoutModel(this.getEntityModel(), GNOME_HAT, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, afloat[0], afloat[1], afloat[2]);
      }
   }
}
