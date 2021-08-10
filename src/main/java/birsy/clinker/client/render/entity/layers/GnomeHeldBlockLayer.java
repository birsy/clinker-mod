package birsy.clinker.client.render.entity.layers;

import birsy.clinker.client.render.entity.model.GnomeModel;
import com.mojang.blaze3d.vertex.PoseStack;

import birsy.clinker.common.entity.merchant.GnomeEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomeHeldBlockLayer extends RenderLayer<GnomeEntity, GnomeModel<GnomeEntity>> {
   public GnomeHeldBlockLayer(RenderLayerParent<GnomeEntity, GnomeModel<GnomeEntity>> p_i50949_1_) {
      super(p_i50949_1_);
   }

   public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GnomeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      BlockState blockstate = entitylivingbaseIn.getHeldBlockState();
      if (blockstate != null) {
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.0D, 0.0D, 0.0D);
         matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(00.0F));
         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(00.0F));
         matrixStackIn.translate(0.0D, 0.0D, 0.0D);
         matrixStackIn.scale(-0.5F, -0.5F, 0.5F);
         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
         Minecraft.getInstance().getBlockRenderer().renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, null);
         matrixStackIn.popPose();
      }
   }
}
