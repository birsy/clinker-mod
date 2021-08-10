package birsy.clinker.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import birsy.clinker.client.render.entity.layers.WitherRevenantEyesLayer;
import birsy.clinker.client.render.entity.model.WitherRevenantModel;
import birsy.clinker.common.entity.monster.WitherRevenantEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;

public class WitherRevenantRenderer extends MobRenderer<WitherRevenantEntity, WitherRevenantModel<WitherRevenantEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/wither_revenant/wither_revenant.png");
	
	public WitherRevenantRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new WitherRevenantModel<>(), 0.7F);
		this.addLayer(new HeldItemLayer<>(this));
		this.addLayer(new WitherRevenantEyesLayer<WitherRevenantEntity, WitherRevenantModel<WitherRevenantEntity>>(this, 0, 1));
	}

	@Override
	public ResourceLocation getEntityTexture(WitherRevenantEntity entity) {
		return TEXTURE;
	}
	
	protected void preRenderCallback(WitherRevenantEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(1.3F, 1.3F, 1.3F);
	}
}
