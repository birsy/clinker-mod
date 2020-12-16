package birsy.clinker.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.entity.WitherRevenantModel;
import birsy.clinker.common.entity.monster.WitherRevenantEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class WitherRevenantEyesLayer<T extends WitherRevenantEntity, M extends WitherRevenantModel<T>> extends AbstractEyesLayer<T, M> {
   private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(Clinker.MOD_ID, "textures/entity/wither_revenant/wither_revenant_eyes.png"));

   public WitherRevenantEyesLayer(IEntityRenderer<T, M> rendererIn, float minimumOpacityIn, float maximumOpacityIn) {
      super(rendererIn);
   }
   /**
   @Override
   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
	   IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.getRenderType());
	   float fade;
	   if (entitylivingbaseIn.isAggressive()) {fade = (0.5F * MathHelper.sin(entitylivingbaseIn.ticksExisted)) + 0.5F;} else {fade = 0F;}
	   this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);
   }
    */
   public RenderType getRenderType() {
      return RENDER_TYPE;
   }
}
