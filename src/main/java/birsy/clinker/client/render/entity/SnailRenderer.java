package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.SnailModel;
import birsy.clinker.common.entity.passive.SnailEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import ResourceLocation;

public class SnailRenderer extends MobRenderer<SnailEntity, SnailModel<SnailEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/arm.png");
	protected static final ResourceLocation GARY_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/arm.png");
	
	public SnailRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new SnailModel<>(), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(SnailEntity entity) {
		if (entity.hasCustomName() && "gary".equalsIgnoreCase(entity.getName().getContents())) {
			return GARY_TEXTURE;
		} else {
     		return TEXTURE;
 		}
	}

}
