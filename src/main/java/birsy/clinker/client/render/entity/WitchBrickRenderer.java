package birsy.clinker.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import birsy.clinker.client.render.entity.model.WitchBrickModel;
import birsy.clinker.common.entity.monster.WitchBrickEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class WitchBrickRenderer extends MobRenderer<WitchBrickEntity, WitchBrickModel<WitchBrickEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/witch_brick.png");
	
	public WitchBrickRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new WitchBrickModel<>(), 0.7F);
	}

	@Override
	public ResourceLocation getEntityTexture(WitchBrickEntity entity) {
		return TEXTURE;
	}
	
	protected void applyRotations(WitchBrickEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
		if (!((double)entityLiving.limbSwingAmount < 0.01D)) {
			//float f = 13.0F;
			float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
			float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
			matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(6.5F * f2));
			matrixStackIn.rotate(Vector3f.YN.rotationDegrees(6.5F * f2));
		}
	}
}
