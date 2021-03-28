package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layers.TorAntEyesLayer;
import birsy.clinker.client.render.entity.model.TorAntModel;
import birsy.clinker.common.entity.passive.TorAntEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.util.ResourceLocation;

public class TorAntRenderer extends MobRenderer<TorAntEntity, TorAntModel<TorAntEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/tor_ant/tor_ant.png");
	
	public TorAntRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new TorAntModel<>(), 0.7F);
		this.addLayer(new SaddleLayer<>(this, new TorAntModel<>(), new ResourceLocation(Clinker.MOD_ID, "textures/entity/tor_ant/tor_ant_saddle.png")));
		this.addLayer(new TorAntEyesLayer<TorAntEntity, TorAntModel<TorAntEntity>>(this));
	}

	@Override
	public ResourceLocation getEntityTexture(TorAntEntity entity) {
		return TEXTURE;
	}

}
