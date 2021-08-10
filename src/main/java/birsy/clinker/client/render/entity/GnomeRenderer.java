package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layers.GnomeArmorLayer;
import birsy.clinker.client.render.entity.layers.GnomeArrowLayer;
import birsy.clinker.client.render.entity.layers.GnomeHatLayer;
import birsy.clinker.client.render.entity.layers.GnomeHeldItemLayer;
import birsy.clinker.client.render.entity.model.GnomeModel;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import ResourceLocation;

public class GnomeRenderer<T extends GnomeEntity, M extends GnomeModel<T>> extends MobRenderer<GnomeEntity, GnomeModel<GnomeEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnome/gnome.png");
	
	public GnomeRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new GnomeModel<>(0), 0.7F);
		this.addLayer(new GnomeHeldItemLayer<>(this));
		this.addLayer(new GnomeArmorLayer<>(this, new GnomeModel<>(0.25F), new GnomeModel<>(0.5F)));
		this.addLayer(new GnomeHatLayer(this));
		this.addLayer(new GnomeArrowLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(GnomeEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(GnomeEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		if (entityIn.hasCustomName()) {
			if (entityIn.getCustomName().getString().equalsIgnoreCase("chrinkla")) {
				matrixStackIn.scale(0.75F, 0.75F, 0.75F);
			}
		}
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}
}


