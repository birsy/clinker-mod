package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.HyenaModel;
import birsy.clinker.common.entity.monster.HyenaEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import ResourceLocation;

public class HyenaRenderer extends MobRenderer<HyenaEntity, HyenaModel<HyenaEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/hyena.png");
	
	public HyenaRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new HyenaModel<>(), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(HyenaEntity entity) {
		return TEXTURE;
	}

}
