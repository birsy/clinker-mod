package birsy.clinker.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;

import birsy.clinker.client.render.entity.model.GnomeModel;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class GnomeArrowLayer<T extends GnomeEntity, M extends GnomeModel<T>> extends RenderLayer<T, M> {
   private final EntityRenderDispatcher renderManager;
   private Arrow arrowEntity;

   public GnomeArrowLayer(LivingEntityRenderer<T, M> rendererIn) {
      super(rendererIn);
      this.renderManager = rendererIn.getDispatcher();
   }

   private int getArrowsInEntity(T entity) {
      return entity.getArrowCount();
   }

   private void renderStuckItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Entity entitylivingbaseIn, float xRand, float yRand, float zRand, float partialTicksIn) {
      float f = Mth.sqrt(xRand * xRand + zRand * zRand);
      this.arrowEntity = new Arrow(entitylivingbaseIn.level, entitylivingbaseIn.getX(), entitylivingbaseIn.getY(), entitylivingbaseIn.getZ());
      this.arrowEntity.yRot = (float)(Math.atan2(xRand, zRand) * (double) (180F / (float)Math.PI));
      this.arrowEntity.xRot = (float)(Math.atan2(yRand, f) * (double) (180F / (float)Math.PI));
      this.arrowEntity.yRotO = this.arrowEntity.yRot;
      this.arrowEntity.xRotO = this.arrowEntity.xRot;
      this.renderManager.render(this.arrowEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicksIn, matrixStackIn, bufferIn, packedLightIn);
   }

   public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      int i = this.getArrowsInEntity(entitylivingbaseIn);
      Random random = new Random(entitylivingbaseIn.getId());
      if (i > 0) {
         for(int j = 0; j < i; ++j) {
            matrixStackIn.pushPose();
            ModelPart modelrenderer = this.getParentModel().getRandomModelRenderer(random);
            ModelPart.Cube modelBox = modelrenderer.getRandomCube(random);
            modelrenderer.translateAndRotate(matrixStackIn);
            float xRand = random.nextFloat();
            float yRand = random.nextFloat();
            float zRand = random.nextFloat();
            
            float arrowPosX = Mth.lerp(xRand, modelBox.minX, modelBox.maxX) / 16.0F;
            float arrowPosY = Mth.lerp(yRand, modelBox.minY, modelBox.maxY) / 16.0F;
            float arrowPosZ = Mth.lerp(zRand, modelBox.minZ, modelBox.maxZ) / 16.0F;
            matrixStackIn.translate(arrowPosX, arrowPosY, arrowPosZ);
            xRand = -1.0F * (xRand * 2.0F - 1.0F);
            yRand = -1.0F * (yRand * 2.0F - 1.0F);
            zRand = -1.0F * (zRand * 2.0F - 1.0F);
            this.renderStuckItem(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, xRand, yRand, zRand, partialTicks);
            matrixStackIn.popPose();
         }

      }
   }
}
