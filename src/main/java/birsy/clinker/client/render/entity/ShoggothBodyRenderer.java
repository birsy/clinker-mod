package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.LesserShoggothBodyModel;
import birsy.clinker.common.entity.monster.ShoggothBodyEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import ResourceLocation;

public class ShoggothBodyRenderer extends MobRenderer<ShoggothBodyEntity, LesserShoggothBodyModel<ShoggothBodyEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/shoggoth.png");
	
	public ShoggothBodyRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new LesserShoggothBodyModel<>(), 0.7F);
	}

	@Override
	protected void scale(ShoggothBodyEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
		float wiggle = 1 + (Mth.sin(entitylivingbaseIn.tickCount + partialTickTime) * 0.05F);
		//matrixStackIn.scale(wiggle, wiggle * 1.02F, wiggle);
	}

	@Override
	public ResourceLocation getTextureLocation(ShoggothBodyEntity entity)
	{
     		return TEXTURE;
	}
}

