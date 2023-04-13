package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layer.SalamanderGlowLayer;
import birsy.clinker.client.render.entity.model.SalamanderTailModel;
import birsy.clinker.common.world.entity.salamander.AbstractSalamanderPartEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SalamanderTailRenderer extends SalamanderRenderer<AbstractSalamanderPartEntity, SalamanderTailModel<AbstractSalamanderPartEntity>> {
    private static final ResourceLocation SALAMANDER_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_tail.png");
    private static final ResourceLocation GLOW = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_tail_glow.png");
    private static final ResourceLocation FIRE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_tail_fire_glow.png");

    public SalamanderTailRenderer(EntityRendererProvider.Context context) {
        super(context, new SalamanderTailModel<>(context.bakeLayer(SalamanderTailModel.LAYER_LOCATION)));
        this.addLayer(new SalamanderGlowLayer<>(this, GLOW, GLOW, FIRE));
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
