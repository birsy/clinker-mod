package birsy.clinker.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;

import birsy.clinker.client.render.entity.model.GnomeModel;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomeHatLayer extends RenderLayer<GnomeEntity, GnomeModel<GnomeEntity>> {
   private static final ResourceLocation GNOME_HAT = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnome/gnome_hat.png");

   public GnomeHatLayer(RenderLayerParent<GnomeEntity, GnomeModel<GnomeEntity>> rendererIn) {
      super(rendererIn);
   }
   
   public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GnomeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!entitylivingbaseIn.isInvisible()) {
         float[] color = entitylivingbaseIn.getHatColor(entitylivingbaseIn).getTextureDiffuseColors();
         renderColoredCutoutModel(this.getParentModel(), GNOME_HAT, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, color[0], color[1], color[2]);
      }
   }
}
