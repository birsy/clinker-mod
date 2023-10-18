package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.layer.BlinkLayer;
import birsy.clinker.client.render.entity.layer.OldSalamanderGlowLayer;
import birsy.clinker.client.render.entity.model.SalamanderHeadModel;
import birsy.clinker.common.world.entity.salamander.SalamanderHeadEntity;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SalamanderHeadRenderer extends SalamanderRenderer<SalamanderHeadEntity, SalamanderHeadModel<SalamanderHeadEntity>> {
    private static final ResourceLocation SALAMANDER_HEAD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head.png");
    private static final ResourceLocation SALAMANDER_HEAD_BLINK_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head_blink.png");
    private static final ResourceLocation GLOW = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head_glow.png");
    private static final ResourceLocation FIRE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_head/salamander_head_fire_glow.png");
    private final SalamanderBodyRenderer bodyRenderer;

    public SalamanderHeadRenderer(EntityRendererProvider.Context context) {
        super(context, new SalamanderHeadModel<>(context.bakeLayer(SalamanderHeadModel.LAYER_LOCATION)));
        this.bodyRenderer = new SalamanderBodyRenderer(context);
        this.addLayer(new BlinkLayer<>(this, SALAMANDER_HEAD_BLINK_TEXTURE, 10.0F, 0.1F));
        this.addLayer(new OldSalamanderGlowLayer<>(this, GLOW, GLOW, FIRE));
    }


    @Override
    public void render(SalamanderHeadEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isDecaptiatedStump()) {
            this.bodyRenderer.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        } else {
            super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        }
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(SalamanderHeadEntity entity) {
        return SALAMANDER_HEAD_TEXTURE;
    }
}
