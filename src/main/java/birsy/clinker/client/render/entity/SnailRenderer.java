package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.model.armModel;
import birsy.clinker.common.entity.passive.SnailEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class SnailRenderer extends MobRenderer<SnailEntity, armModel<SnailEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/arm.png");
	protected static final ResourceLocation GARY_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/arm.png");
	
	public SnailRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new armModel<>(), 0.7F);
	}

	@Override
	public ResourceLocation getEntityTexture(SnailEntity entity)
	{
		if (entity.hasCustomName() && "Gary".equals(entity.getName().getUnformattedComponentText())) {
			return GARY_TEXTURE;
		} else {
     		return TEXTURE;
 		}
	}

}
