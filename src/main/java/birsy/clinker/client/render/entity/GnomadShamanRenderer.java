package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.model.entity.GnomadShamanModel;
import birsy.clinker.common.entity.monster.GnomadShamanEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class GnomadShamanRenderer extends MobRenderer<GnomadShamanEntity, GnomadShamanModel<GnomadShamanEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/shaman/gnomad_shaman.png");
	
	public GnomadShamanRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new GnomadShamanModel<>(), 0.7F);
		this.shadowSize = 0.5F;
	}
	
	@Override
	public ResourceLocation getEntityTexture(GnomadShamanEntity entity) {
		return TEXTURE;
	}
}
