package birsy.clinker.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;

import birsy.clinker.client.render.model.entity.GnomeModel;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomeHeldBlockLayer extends LayerRenderer<GnomeEntity, GnomeModel<GnomeEntity>> {
   public GnomeHeldBlockLayer(IEntityRenderer<GnomeEntity, GnomeModel<GnomeEntity>> p_i50949_1_) {
      super(p_i50949_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, GnomeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      BlockState blockstate = entitylivingbaseIn.getHeldBlockState();
      if (blockstate != null) {
         matrixStackIn.push();
         matrixStackIn.translate(0.0D, 0.0D, 0.0D);
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(00.0F));
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(00.0F));
         matrixStackIn.translate(0.0D, 0.0D, 0.0D);
         matrixStackIn.scale(-0.5F, -0.5F, 0.5F);
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
         Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, null);
         matrixStackIn.pop();
      }
   }
}
