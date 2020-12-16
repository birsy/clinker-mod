package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.layers.GnomeArmorLayer;
import birsy.clinker.client.render.layers.GnomeArrowLayer;
import birsy.clinker.client.render.layers.GnomeHatLayer;
import birsy.clinker.client.render.layers.GnomeHeldItemLayer;
import birsy.clinker.client.render.model.entity.GnomeModel2;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class GnomeRenderer<T extends GnomeEntity, M extends GnomeModel2<T>> extends MobRenderer<GnomeEntity, GnomeModel2<GnomeEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnome/gnome.png");
	
	public GnomeRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new GnomeModel2<>(0), 0.7F);
		this.addLayer(new GnomeHeldItemLayer<>(this));
		this.addLayer(new GnomeArmorLayer<>(this, new GnomeModel2<>(0.25F), new GnomeModel2<>(0.5F)));
		this.addLayer(new GnomeHatLayer(this));
		this.addLayer(new GnomeArrowLayer<>(this));
	}

	@Override
	public ResourceLocation getEntityTexture(GnomeEntity entity) {
		return TEXTURE;
	}
}


