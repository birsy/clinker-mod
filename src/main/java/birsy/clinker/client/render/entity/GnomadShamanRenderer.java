package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.GnomadShamanModel;
import birsy.clinker.common.entity.monster.gnomad.GnomadShamanEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import ResourceLocation;

public class GnomadShamanRenderer extends MobRenderer<GnomadShamanEntity, GnomadShamanModel<GnomadShamanEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/shaman/gnomad_shaman.png");
	
	public GnomadShamanRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new GnomadShamanModel<>(), 0.7F);
		this.shadowRadius = 0.5F;
	}
	
	@Override
	public ResourceLocation getTextureLocation(GnomadShamanEntity entity) {
		return TEXTURE;
	}
}
