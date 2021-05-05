package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GnomadArmorModel - birse
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomadArmorModel extends Model {
    public BirsyModelRenderer gnomadRightPauldron;
    public BirsyModelRenderer gnomadLeftPauldron;
    public BirsyModelRenderer gnomadHelmet;
    public BirsyModelRenderer gnomadRightPauldronRim;
    public BirsyModelRenderer gnomadRightPauldronBulb;
    public BirsyModelRenderer gnomadLeftPauldronRim;
    public BirsyModelRenderer gnomadLeftPauldronBulb;
    public BirsyModelRenderer gnomadVisor;

    public GnomadArmorModel() {
        super(RenderType::getArmorCutoutNoCull);

        this.textureWidth = 64;
        this.textureHeight = 64;

        this.gnomadHelmet = new BirsyModelRenderer(this, 36, 39);
        this.gnomadHelmet.setRotationPoint(0.0F, 1.5F, -1.5F);
        this.gnomadHelmet.addBox(-4.0F, -5.0F, -6.0F, 8.0F, 5.0F, 6.0F, 0.25F, 0.25F, 0.25F);

        this.gnomadVisor = new BirsyModelRenderer(this, 36, 50);
        this.gnomadVisor.setRotationPoint(0.0F, -1.0F, -4.0F);
        this.gnomadVisor.addBox(-4.5F, -1.5F, -3.5F, 9.0F, 5.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadVisor.rotateAngleX = 0.4F;


        this.gnomadRightPauldron = new BirsyModelRenderer(this, 10, 17);
        this.gnomadRightPauldron.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.gnomadRightPauldron.addBox(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadRightPauldron.rotateAngleY = (float) (Math.PI * 0.5F);

        this.gnomadRightPauldronRim = new BirsyModelRenderer(this, 46, 0);
        this.gnomadRightPauldronRim.setRotationPoint(0.0F, 2.5F, -1.0F);
        this.gnomadRightPauldronRim.addBox(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);

        this.gnomadRightPauldronBulb = new BirsyModelRenderer(this, 0, 17);
        this.gnomadRightPauldronBulb.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.gnomadRightPauldronBulb.addBox(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, 0.25F, 0.25F, 0.25F);


        this.gnomadLeftPauldron = new BirsyModelRenderer(this, 10, 17);
        this.gnomadLeftPauldron.setRotationPoint(-0.5F, 0.0F, 0.0F);
        this.gnomadLeftPauldron.addBox(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadLeftPauldron.rotateAngleY = (float) (Math.PI * -0.5F);

        this.gnomadLeftPauldronRim = new BirsyModelRenderer(this, 46, 0);
        this.gnomadLeftPauldronRim.setRotationPoint(0.0F, 2.5F, -1.0F);
        this.gnomadLeftPauldronRim.addBox(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);

        this.gnomadLeftPauldronBulb = new BirsyModelRenderer(this, 0, 17);
        this.gnomadLeftPauldronBulb.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.gnomadLeftPauldronBulb.addBox(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, 0.25F, 0.25F, 0.25F);


        this.gnomadRightPauldron.addChild(this.gnomadRightPauldronRim);
        this.gnomadLeftPauldronRim.addChild(this.gnomadLeftPauldronBulb);
        this.gnomadRightPauldronRim.addChild(this.gnomadRightPauldronBulb);
        this.gnomadLeftPauldron.addChild(this.gnomadLeftPauldronRim);
        this.gnomadHelmet.addChild(this.gnomadVisor);
    }

    public void setArmorVisibility(GnomadAxemanEntity entitylivingbaseIn) {
        this.gnomadHelmet.showModel = entitylivingbaseIn.isWearingHelmet();
        this.gnomadLeftPauldron.showModel = entitylivingbaseIn.isWearingLeftPauldron();
        this.gnomadRightPauldron.showModel = entitylivingbaseIn.isWearingRightPauldron();
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.gnomadHelmet, this.gnomadRightPauldron, this.gnomadLeftPauldron).forEach((BirsyModelRenderer) -> { 
            BirsyModelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }
}
