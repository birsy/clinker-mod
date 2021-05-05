package birsy.clinker.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;

import birsy.clinker.client.render.entity.model.GnomeModel;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class GnomeArrowLayer<T extends GnomeEntity, M extends GnomeModel<T>> extends LayerRenderer<T, M> {
   private final EntityRendererManager renderManager;
   private ArrowEntity arrowEntity;

   public GnomeArrowLayer(LivingRenderer<T, M> rendererIn) {
      super(rendererIn);
      this.renderManager = rendererIn.getRenderManager();
   }

   private int getArrowsInEntity(T entity) {
      return entity.getArrowCountInEntity();
   }

   private void func_225632_a_(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Entity entitylivingbaseIn, float xRand, float yRand, float zRand, float partialTicksIn) {
      float f = MathHelper.sqrt(xRand * xRand + zRand * zRand);
      this.arrowEntity = new ArrowEntity(entitylivingbaseIn.world, entitylivingbaseIn.getPosX(), entitylivingbaseIn.getPosY(), entitylivingbaseIn.getPosZ());
      this.arrowEntity.rotationYaw = (float)(Math.atan2(xRand, zRand) * (double) (180F / (float)Math.PI));
      this.arrowEntity.rotationPitch = (float)(Math.atan2(yRand, f) * (double) (180F / (float)Math.PI));
      this.arrowEntity.prevRotationYaw = this.arrowEntity.rotationYaw;
      this.arrowEntity.prevRotationPitch = this.arrowEntity.rotationPitch;
      this.renderManager.renderEntityStatic(this.arrowEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicksIn, matrixStackIn, bufferIn, packedLightIn);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      int i = this.getArrowsInEntity(entitylivingbaseIn);
      Random random = new Random(entitylivingbaseIn.getEntityId());
      if (i > 0) {
         for(int j = 0; j < i; ++j) {
            matrixStackIn.push();
            ModelRenderer modelrenderer = this.getEntityModel().getRandomModelRenderer(random);
            ModelRenderer.ModelBox modelBox = modelrenderer.getRandomCube(random);
            modelrenderer.translateRotate(matrixStackIn);
            float xRand = random.nextFloat();
            float yRand = random.nextFloat();
            float zRand = random.nextFloat();
            
            float arrowPosX = MathHelper.lerp(xRand, modelBox.posX1, modelBox.posX2) / 16.0F;
            float arrowPosY = MathHelper.lerp(yRand, modelBox.posY1, modelBox.posY2) / 16.0F;
            float arrowPosZ = MathHelper.lerp(zRand, modelBox.posZ1, modelBox.posZ2) / 16.0F;
            matrixStackIn.translate(arrowPosX, arrowPosY, arrowPosZ);
            xRand = -1.0F * (xRand * 2.0F - 1.0F);
            yRand = -1.0F * (yRand * 2.0F - 1.0F);
            zRand = -1.0F * (zRand * 2.0F - 1.0F);
            this.func_225632_a_(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, xRand, yRand, zRand, partialTicks);
            matrixStackIn.pop();
         }

      }
   }
}
