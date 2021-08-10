package birsy.clinker.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import birsy.clinker.client.render.entity.model.WitchBrickModel;
import birsy.clinker.common.entity.monster.WitchBrickEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

import ResourceLocation;

public class WitchBrickRenderer extends MobRenderer<WitchBrickEntity, WitchBrickModel<WitchBrickEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/witch_brick.png");
	
	public WitchBrickRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new WitchBrickModel<>(), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(WitchBrickEntity entity) {
		return TEXTURE;
	}
	
	protected void setupRotations(WitchBrickEntity entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
		if (!((double)entityLiving.animationSpeed < 0.01D)) {
			//float f = 13.0F;
			float f1 = entityLiving.animationPosition - entityLiving.animationSpeed * (1.0F - partialTicks) + 6.0F;
			float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
			matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(6.5F * f2));
		}
	}
}
