package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layers.GnomadAxemanArmorLayer;
import birsy.clinker.client.render.entity.layers.GnomadAxemanBuffLayer;
import birsy.clinker.client.render.entity.layers.GnomadAxemanShieldLayer;
import birsy.clinker.client.render.entity.layers.GnomeHeldItemLayer;
import birsy.clinker.client.render.entity.model.GnomadArmorModel;
import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class GnomadAxemanRenderer extends MobRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman.png");
	protected static final ResourceLocation SANTA_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman_santa.png");

	protected static final ResourceLocation BUFF_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman_healing.png");
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

        //If it's between Dec. 22 and 28, the gnomads look like Santa!
        if(month == 12 && day > 22 && day < 28) {
        	return SANTA_TEXTURE;
        }
        
        return TEXTURE;
	}

	@Override
	public void render(GnomadAxemanEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		this.entityModel.setArmorVisibility(entityIn);
		matrixStackIn.push();
		//matrixStackIn.scale(entityIn.getSize(), entityIn.getSize(), entityIn.getSize());
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		matrixStackIn.pop();
	}
}
