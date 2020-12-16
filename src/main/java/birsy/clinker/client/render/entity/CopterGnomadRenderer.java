package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.layers.GnomeHeldItemLayer;
import birsy.clinker.client.render.model.entity.CopterGnomadModel;
import birsy.clinker.common.entity.monster.GnomadHelicopterEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CopterGnomadRenderer extends MobRenderer<GnomadHelicopterEntity, CopterGnomadModel<GnomadHelicopterEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/propellor_gnomad.png");
	
	public CopterGnomadRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new CopterGnomadModel<>(), 0.7F);
		this.addLayer(new GnomeHeldItemLayer<>(this));
	}

	@Override
	public ResourceLocation getEntityTexture(GnomadHelicopterEntity entity) {
		return TEXTURE;
	}
}
