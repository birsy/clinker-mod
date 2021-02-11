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
public class WitherRevenantEyesLayer<T extends WitherRevenantEntity, M extends WitherRevenantModel<T>> extends AbstractFlashingEyesLayer<T, M> {
	private static final ResourceLocation EYE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/wither_revenant/wither_revenant_eyes.png");
	private static final RenderType RENDER_TYPE = RenderType.getEyes(EYE_TEXTURE);

	public WitherRevenantEyesLayer(IEntityRenderer<T, M> rendererIn, float minimumOpacityIn, float maximumOpacityIn) {
		super(rendererIn, minimumOpacityIn, maximumOpacityIn);
	}
   
	@Override
 	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(EYE_TEXTURE));
 		float fade = MathHelper.clamp((0.5F * MathHelper.sin(ageInTicks * 0.1F) + 0.5F + 0.2F), 0.3F, 1);
 		
 		if (entitylivingbaseIn.phase != WitherRevenantEntity.AIPhase.WANDERING) {
 			fade = MathHelper.clamp((0.5F * MathHelper.sin(ageInTicks) + 0.5F + 0.2F), 0, 1);
 		}
 		
 		this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);
	}
   
	public RenderType getRenderType() {
		return RENDER_TYPE;
	}
}
