package birsy.clinker.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.client.render.entity.model.WitherRevenantModel;
import birsy.clinker.common.entity.monster.WitherRevenantEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import ResourceLocation;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class WitherRevenantEyesLayer<T extends WitherRevenantEntity, M extends WitherRevenantModel<T>> extends AbstractFlashingEyesLayer<T, M> {
	private static final ResourceLocation EYE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/wither_revenant/wither_revenant_eyes.png");
	private static final RenderType RENDER_TYPE = RenderType.eyes(EYE_TEXTURE);

	public WitherRevenantEyesLayer(RenderLayerParent<T, M> rendererIn, float minimumOpacityIn, float maximumOpacityIn) {
		super(rendererIn, minimumOpacityIn, maximumOpacityIn);
	}
   
	@Override
 	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(EYE_TEXTURE));
 		float fade = Mth.clamp((0.5F * Mth.sin(ageInTicks * 0.1F) + 0.5F + 0.2F), 0.3F, 1);
 		
 		if (entitylivingbaseIn.phase != WitherRevenantEntity.AIPhase.WANDERING) {
 			fade = Mth.clamp((0.5F * Mth.sin(ageInTicks) + 0.5F + 0.2F), 0, 1);
 		}
 		
 		this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);
	}
   
	public RenderType renderType() {
		return RENDER_TYPE;
	}
}
