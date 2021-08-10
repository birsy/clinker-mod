package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.GnomeBratModel;
import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import ResourceLocation;

public class GnomeBratRenderer extends MobRenderer<GnomeBratEntity, GnomeBratModel<GnomeBratEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomebrat.png");
	
	public GnomeBratRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new GnomeBratModel<>(), 0.7F);
		this.addLayer(new ItemInHandLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(GnomeBratEntity entity) {
		return TEXTURE;
	}

}
