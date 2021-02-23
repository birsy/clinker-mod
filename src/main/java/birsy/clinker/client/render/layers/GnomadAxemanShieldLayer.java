package birsy.clinker.client.render.layers;

import birsy.clinker.client.render.model.entity.GnomadAxemanModel;
import birsy.clinker.client.render.model.entity.GnomadShieldModel;
import birsy.clinker.common.entity.monster.GnomadAxemanEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanShieldLayer extends LayerRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> {
    private ResourceLocation SHIELD_TEXTURE;
    private GnomadShieldModel gnomadShieldModel = new GnomadShieldModel(20.0F);
    private float shieldRotation;
    private float shieldRadius;
    private float previousShieldRadius;
    private float nextShieldRadius;
    private float shieldRadiusTransitionAnimation;

    public GnomadAxemanShieldLayer(IEntityRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> renderer, ResourceLocation buffTexture) {
        super(renderer);
        SHIELD_TEXTURE = buffTexture;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, GnomadAxemanEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.getShieldNumber() > 0) {
            float f = entitylivingbaseIn.ticksExisted + partialTicks;
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEnergySwirl(SHIELD_TEXTURE, 0.0F, 0.0F));
            this.gnomadShieldModel.setRotation(f * 0.1F, (MathHelper.sin(f * 0.1F)) * 3 + 17, f, entitylivingbaseIn.getShieldNumber());
            this.gnomadShieldModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.25F, 0.25F, 0.25F * (entitylivingbaseIn.shieldTransitionAnimation + partialTicks), 1.0F);
        }
    }
}
