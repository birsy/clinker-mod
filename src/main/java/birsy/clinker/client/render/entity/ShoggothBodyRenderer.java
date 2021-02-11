package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.model.entity.LesserShoggothBodyModel;
import birsy.clinker.common.entity.monster.ShoggothBodyEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ShoggothBodyRenderer extends MobRenderer<ShoggothBodyEntity, LesserShoggothBodyModel<ShoggothBodyEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/shoggoth.png");
	
	public ShoggothBodyRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new LesserShoggothBodyModel<>(), 0.7F);
	}

	@Override
	protected void preRenderCallback(ShoggothBodyEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		float wiggle = 1 + (MathHelper.sin(entitylivingbaseIn.ticksExisted + partialTickTime) * 0.05F);
		//matrixStackIn.scale(wiggle, wiggle * 1.02F, wiggle);
	}

	@Override
	public ResourceLocation getEntityTexture(ShoggothBodyEntity entity)
	{
     		return TEXTURE;
	}
}

