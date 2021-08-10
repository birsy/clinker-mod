package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.GnomeBratModel;
import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;

public class GnomeBratRenderer extends MobRenderer<GnomeBratEntity, GnomeBratModel<GnomeBratEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomebrat.png");
	
	public GnomeBratRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new GnomeBratModel<>(), 0.7F);
		this.addLayer(new HeldItemLayer<>(this));
	}

	@Override
	public ResourceLocation getEntityTexture(GnomeBratEntity entity) {
		return TEXTURE;
	}

}
