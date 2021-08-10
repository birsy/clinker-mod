package birsy.clinker.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import birsy.clinker.client.render.entity.layers.WitherRevenantEyesLayer;
import birsy.clinker.client.render.entity.model.WitherRevenantModel;
import birsy.clinker.common.entity.monster.WitherRevenantEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import ResourceLocation;

public class WitherRevenantRenderer extends MobRenderer<WitherRevenantEntity, WitherRevenantModel<WitherRevenantEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/wither_revenant/wither_revenant.png");
	
	public WitherRevenantRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new WitherRevenantModel<>(), 0.7F);
		this.addLayer(new ItemInHandLayer<>(this));
		this.addLayer(new WitherRevenantEyesLayer<WitherRevenantEntity, WitherRevenantModel<WitherRevenantEntity>>(this, 0, 1));
	}

	@Override
	public ResourceLocation getTextureLocation(WitherRevenantEntity entity) {
		return TEXTURE;
	}
	
	protected void scale(WitherRevenantEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(1.3F, 1.3F, 1.3F);
	}
}
