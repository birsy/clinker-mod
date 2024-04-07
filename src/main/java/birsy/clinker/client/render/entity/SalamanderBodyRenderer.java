package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layer.SalamanderBodyCharredLayer;
import birsy.clinker.client.render.entity.layer.OldSalamanderGlowLayer;
import birsy.clinker.client.render.entity.model.SalamanderBodyModel;
import birsy.clinker.common.world.entity.salamanderOLD.AbstractSalamanderPartEntity;
import birsy.clinker.common.world.entity.salamanderOLD.SalamanderBodyEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SalamanderBodyRenderer extends SalamanderRenderer<AbstractSalamanderPartEntity, SalamanderBodyModel<AbstractSalamanderPartEntity>> {
    private static final ResourceLocation SALAMANDER_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body.png");
    private static final ResourceLocation GLOW = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body_glow.png");
    private static final ResourceLocation CHARRED_GLOW = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/charred_salamander_body_glow.png");
    private static final ResourceLocation FIRE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body_fire_glow.png");

    private final SalamanderTailRenderer tailRenderer;

    public SalamanderBodyRenderer(EntityRendererProvider.Context context) {
        super(context, new SalamanderBodyModel<>(context.bakeLayer(SalamanderBodyModel.LAYER_LOCATION)));
        this.addLayer(new SalamanderBodyCharredLayer<>(this));
        this.addLayer(new OldSalamanderGlowLayer<>(this, GLOW, CHARRED_GLOW, FIRE));

        this.tailRenderer = new SalamanderTailRenderer(context);
    }

    @Override
    public void render(AbstractSalamanderPartEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity instanceof SalamanderBodyEntity) {
            int tailAmount = ((SalamanderBodyEntity) pEntity).getTailAmount();
            if (tailAmount == 0) {
                super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
            } else {
                this.tailRenderer.getModel().setTailThickness(tailAmount <= 1);
                this.tailRenderer.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
            }
        } else {
            super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        }
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(AbstractSalamanderPartEntity entity) {
        return SALAMANDER_TEXTURE;
    }
}
