package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layers.GnomeHeldItemLayer;
import birsy.clinker.client.render.entity.model.CopterGnomadModel;
import birsy.clinker.common.entity.monster.gnomad.GnomadHelicopterEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import ResourceLocation;

public class CopterGnomadRenderer extends MobRenderer<GnomadHelicopterEntity, CopterGnomadModel<GnomadHelicopterEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/propellor_gnomad.png");
	
	public CopterGnomadRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new CopterGnomadModel<>(), 0.7F);
		this.addLayer(new GnomeHeldItemLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(GnomadHelicopterEntity entity) {
		return TEXTURE;
	}
}
