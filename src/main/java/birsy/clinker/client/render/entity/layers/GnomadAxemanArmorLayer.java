package birsy.clinker.client.render.entity.layers;

import birsy.clinker.client.render.entity.model.GnomadArmorModel;
import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.client.render.entity.model.GnomadShieldModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanArmorLayer extends RenderLayer<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> {
    private final GnomadArmorModel armorModel = new GnomadArmorModel();

    public GnomadAxemanArmorLayer(RenderLayerParent<GnomadAxemanEntity, GnomadAxemanModel<GnomadAxemanEntity>> renderer) {
        super(renderer);

        this.getParentModel().gnomadHead.addChild(armorModel.gnomadHelmet);
        this.getParentModel().gnomadLeftArm.addChild(armorModel.gnomadLeftPauldron);
        this.getParentModel().gnomadRightArm.addChild(armorModel.gnomadRightPauldron);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GnomadAxemanEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
