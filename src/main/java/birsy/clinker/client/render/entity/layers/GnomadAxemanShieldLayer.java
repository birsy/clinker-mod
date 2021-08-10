package birsy.clinker.client.render.entity.layers;

import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.client.render.entity.model.GnomadShieldModel;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanShieldLayer extends RenderLayer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> {
    private ResourceLocation SHIELD_TEXTURE;
    private GnomadShieldModel gnomadShieldModel = new GnomadShieldModel(20.0F);
    private float shieldRotation;
    private float shieldRadius;
    private float previousShieldRadius;
    private float nextShieldRadius;
    private float shieldRadiusTransitionAnimation;

    public GnomadAxemanShieldLayer(RenderLayerParent<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> renderer, ResourceLocation buffTexture) {
        super(renderer);
        SHIELD_TEXTURE = buffTexture;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GnomadAxemanEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.getShieldNumber() > 0) {
            float f = entitylivingbaseIn.tickCount + partialTicks;
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.energySwirl(SHIELD_TEXTURE, 0.0F, 0.0F));
            this.gnomadShieldModel.setRotation(f * 0.1F, (Mth.sin(f * 0.1F)) * 3 + 17, f, entitylivingbaseIn.getShieldNumber());
            this.gnomadShieldModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.25F, 0.25F, 0.25F * (entitylivingbaseIn.shieldTransitionAnimation + partialTicks), 1.0F);
        }
    }
}
