package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layer.GnomadItemBeltLayer;
import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.client.render.entity.model.MudScarabModel;
import birsy.clinker.client.render.entity.model.PlaceholderModel;
import birsy.clinker.common.entity.GnomadAxemanEntity;
import birsy.clinker.common.entity.MudScarabEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class GnomadAxemanRenderer extends ClinkerEntityRenderer<GnomadAxemanEntity, PlaceholderModel<GnomadAxemanEntity>> {
    private static final ResourceLocation[] GNOMAD_AXEMAN_LOCATIONS = Util.make(new ResourceLocation[GnomadAxemanEntity.VARIANT_AMOUNT], (array) -> {
        for (int i = 0; i < array.length; i++) {
            array[i] = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/gnomad_axeman" + "_" + i + ".png");
        }
    });

    public GnomadAxemanRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderModel<>(/*context.bakeLayer(GnomadAxemanModel.LAYER_LOCATION)*/), 0.5F);
        //this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        //this.addLayer(new GnomadItemBeltLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public void render(GnomadAxemanEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        //this.getModel().sitTransition = pEntity.getSitTicks(pPartialTicks);
        //this.getModel().headShakingIntensity = pEntity.getHeadShakingIntensity(pPartialTicks);
        this.getModel().update();
        this.getModel().setColors(1.0F, 1.0F, 1.0F, 1.0F);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(GnomadAxemanEntity entity) {
        return PLACEHOLDER_TEXTURE;//GNOMAD_AXEMAN_LOCATIONS[entity.getVariant()];
    }
}
