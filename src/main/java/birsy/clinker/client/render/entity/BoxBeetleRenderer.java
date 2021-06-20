package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.BoxBeetleModel;
import birsy.clinker.common.entity.monster.beetle.BoxBeetleEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class BoxBeetleRenderer extends MobRenderer<BoxBeetleEntity, BoxBeetleModel<BoxBeetleEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/beetle/box_beetle.png");
	
	public BoxBeetleRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new BoxBeetleModel<>(), 0.7F);
	}

	@Override
	public void render(BoxBeetleEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStackIn.scale(entityIn.getSize() * 0.75F, entityIn.getSize() * 0.75F, entityIn.getSize() * 0.75F);

		if (entityIn.inFlight() || entityIn.isAirBorne) {
			this.getEntityModel().flightTransition = MathUtils.mapRange(0, 10, 1, 0, entityIn.getFlightOpenTransitionTicks(partialTicks));
		} else {
			this.getEntityModel().flightTransition = MathUtils.mapRange(0, 10, 0, 1, entityIn.getFlightCloseTransitionTicks(partialTicks));
		}

		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getEntityTexture(BoxBeetleEntity entity) {
		return TEXTURE;
	}

}
