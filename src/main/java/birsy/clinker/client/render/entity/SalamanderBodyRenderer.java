package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layer.SalamanderBodyCharredLayer;
import birsy.clinker.client.render.entity.layer.SalamanderBodyGlowLayer;
import birsy.clinker.client.render.entity.model.SalamanderBodyModel;
import birsy.clinker.common.entity.Salamander.AbstractSalamanderPartEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SalamanderBodyRenderer extends SalamanderRenderer<AbstractSalamanderPartEntity, SalamanderBodyModel<AbstractSalamanderPartEntity>> {
    private static final ResourceLocation SALAMANDER_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body.png");

    public SalamanderBodyRenderer(EntityRendererProvider.Context context) {
        super(context, new SalamanderBodyModel<>(context.bakeLayer(SalamanderBodyModel.LAYER_LOCATION)));
        this.addLayer(new SalamanderBodyCharredLayer<>(this));
        this.addLayer(new SalamanderBodyGlowLayer<>(this));
    }

    @Override
    public void render(AbstractSalamanderPartEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(AbstractSalamanderPartEntity entity) {
        return SALAMANDER_TEXTURE;
    }
}
