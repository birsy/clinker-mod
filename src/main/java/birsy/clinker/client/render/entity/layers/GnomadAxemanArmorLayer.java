package birsy.clinker.client.render.entity.layers;

import birsy.clinker.client.render.entity.model.GnomadArmorModel;
import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.client.render.entity.model.GnomadShieldModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanArmorLayer extends LayerRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> {
    private final GnomadArmorModel armorModel = new GnomadArmorModel();

    public GnomadAxemanArmorLayer(IEntityRenderer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> renderer) {
        super(renderer);

        this.getEntityModel().gnomadHead.addChild(armorModel.gnomadHelmet);
        this.getEntityModel().gnomadLeftArm.addChild(armorModel.gnomadLeftPauldron);
        this.getEntityModel().gnomadRightArm.addChild(armorModel.gnomadRightPauldron);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, GnomadAxemanEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
