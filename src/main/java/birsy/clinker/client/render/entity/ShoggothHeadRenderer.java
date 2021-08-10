package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.LesserShoggothHeadModel;
import birsy.clinker.common.entity.monster.ShoggothHeadEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import ResourceLocation;

public class ShoggothHeadRenderer extends MobRenderer<ShoggothHeadEntity, LesserShoggothHeadModel<ShoggothHeadEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/shoggoth.png");
	
	public ShoggothHeadRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new LesserShoggothHeadModel<>(), 0.7F);
	}

	@Override
	protected void scale(ShoggothHeadEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
		float wiggle = 1 + (Mth.sin(entitylivingbaseIn.tickCount + partialTickTime) * 0.05F);
		//matrixStackIn.scale(wiggle, wiggle * 1.02F, wiggle);

		/**
		float f = entitylivingbaseIn.mitosisTicks + partialTickTime;
		float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		f = f * f;
		f = f * f;
		float f2 = (1.0F + f * 0.4F) * f1;
		float f3 = (1.0F + f * 0.1F) / f1;
		matrixStackIn.scale(f2, f3, f2);
		 */
	}

	@Override
	public ResourceLocation getTextureLocation(ShoggothHeadEntity entity)
	{
     		return TEXTURE;
	}
}

