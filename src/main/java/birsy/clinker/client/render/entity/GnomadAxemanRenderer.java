package birsy.clinker.client.render.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import birsy.clinker.client.render.layers.GnomadAxemanBuffLayer;
import birsy.clinker.client.render.layers.GnomadAxemanShieldLayer;
import birsy.clinker.client.render.layers.GnomeHeldItemLayer;
import birsy.clinker.client.render.model.entity.GnomadAxemanModel;
import birsy.clinker.common.entity.monster.GnomadAxemanEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class GnomadAxemanRenderer extends MobRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman.png");
	protected static final ResourceLocation SANTA_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman_santa.png");
	protected static final ResourceLocation BUFF_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman_armor.png");
	protected static final ResourceLocation SHIELD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman_shield.png");

	public GnomadAxemanRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new GnomadAxemanModel<>(1), 0.7F);
		this.addLayer(new GnomadAxemanBuffLayer(this, BUFF_TEXTURE));
		this.addLayer(new GnomadAxemanShieldLayer(this, SHIELD_TEXTURE));
		this.addLayer(new GnomeHeldItemLayer<>(this));
		this.shadowSize = 0.5F;
	}
	
	@Override
	public ResourceLocation getEntityTexture(GnomadAxemanEntity entity) {
		LocalDate localdate = LocalDate.now();
        int day = localdate.get(ChronoField.DAY_OF_MONTH);
        int month = localdate.get(ChronoField.MONTH_OF_YEAR);
        
        if(month == 12 && day > 0 && day < 30) {
        	return SANTA_TEXTURE;
        }
        
        return TEXTURE;
	}
}
