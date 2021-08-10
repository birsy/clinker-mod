package birsy.clinker.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;
import EntityModel;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFlashingEyesLayer<T extends Entity, M extends EntityModel<T>> extends EyesLayer<T, M> {
	private float maximumOpacity;
	private float minimumOpacity;
	
	public AbstractFlashingEyesLayer(RenderLayerParent<T, M> rendererIn, float minimumOpacityIn, float maximumOpacityIn) {
		super(rendererIn);
		maximumOpacityIn = maximumOpacity;
		minimumOpacityIn = minimumOpacity;
	}

  	@Override
  	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
  		VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.renderType());
  		float fade = (0.5F * Mth.sin(entitylivingbaseIn.tickCount)) + 0.5F;
  		this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);
  	}
}
