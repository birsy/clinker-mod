package birsy.clinker.client.render.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * termite - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class TermiteModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer termiteHead;
    public ModelRenderer termiteNeck;
    public ModelRenderer termiteBody;
    public ModelRenderer termiteLeg1;
    public ModelRenderer termiteLeg1_1;
    public ModelRenderer termiteLeg1_2;
    public ModelRenderer termiteLeg1_3;
    public ModelRenderer termiteLeg1_4;
    public ModelRenderer termiteLeg1_5;
    public ModelRenderer termiteAntennae;
    public ModelRenderer termiteRightJaw;
    public ModelRenderer termiteLeftJaw;

    public TermiteModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.termiteRightJaw = new ModelRenderer(this, 0, 7);
        this.termiteRightJaw.setRotationPoint(-1.0F, 0.9F, -4.0F);
        this.termiteRightJaw.addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteRightJaw, 0.0F, 0.2617993877991494F, 0.0F);
        this.termiteLeg1 = new ModelRenderer(this, 12, 0);
        this.termiteLeg1.setRotationPoint(2.0F, 21.0F, -2.0F);
        this.termiteLeg1.addBox(0.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1, 0.0F, 0.7853981633974483F, 0.7853981633974483F);
        this.termiteLeg1_3 = new ModelRenderer(this, 16, 6);
        this.termiteLeg1_3.setRotationPoint(-2.0F, 21.0F, 0.0F);
        this.termiteLeg1_3.addBox(-6.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_3, 0.0F, 0.0F, -0.5811946275983063F);
        this.termiteNeck = new ModelRenderer(this, 0, 7);
        this.termiteNeck.setRotationPoint(0.0F, 22.0F, 1.0F);
        this.termiteNeck.addBox(-2.0F, -2.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.termiteLeg1_1 = new ModelRenderer(this, 12, 2);
        this.termiteLeg1_1.setRotationPoint(2.0F, 21.0F, 2.0F);
        this.termiteLeg1_1.addBox(0.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_1, 0.0F, -0.7853981633974483F, 0.7853981633974483F);
        this.termiteLeg1_2 = new ModelRenderer(this, 16, 4);
        this.termiteLeg1_2.setRotationPoint(-2.0F, 21.0F, -2.0F);
        this.termiteLeg1_2.addBox(-6.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_2, 0.0F, -0.7853981633974483F, -0.7853981633974483F);
        this.termiteLeg1_5 = new ModelRenderer(this, 16, 10);
        this.termiteLeg1_5.setRotationPoint(2.0F, 21.0F, 0.0F);
        this.termiteLeg1_5.addBox(0.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_5, 0.0F, 0.0F, 0.5811946275983063F);
        this.termiteBody = new ModelRenderer(this, 0, 18);
        this.termiteBody.setRotationPoint(0.0F, 21.5F, 3.5F);
        this.termiteBody.addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteBody, 0.7853981633974483F, 0.0F, 0.0F);
        this.termiteAntennae = new ModelRenderer(this, 15, 18);
        this.termiteAntennae.setRotationPoint(0.0F, -1.0F, -2.0F);
        this.termiteAntennae.addBox(-2.0F, -3.0F, 0.0F, 4.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteAntennae, 0.39269908169872414F, 0.0F, 0.0F);
        this.termiteHead = new ModelRenderer(this, 0, 0);
        this.termiteHead.setRotationPoint(0.0F, 22.0F, -3.0F);
        this.termiteHead.addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.termiteLeftJaw = new ModelRenderer(this, 0, 11);
        this.termiteLeftJaw.setRotationPoint(1.0F, 0.9F, -4.0F);
        this.termiteLeftJaw.addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeftJaw, 0.0F, -0.2617993877991494F, 0.0F);
        this.termiteLeg1_4 = new ModelRenderer(this, 16, 8);
        this.termiteLeg1_4.setRotationPoint(-2.0F, 21.0F, 2.0F);
        this.termiteLeg1_4.addBox(-6.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_4, 0.0F, 0.7853981633974483F, -0.7853981633974483F);
        this.termiteHead.addChild(this.termiteRightJaw);
        this.termiteHead.addChild(this.termiteAntennae);
        this.termiteHead.addChild(this.termiteLeftJaw);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.termiteLeg1, this.termiteLeg1_3, this.termiteNeck, this.termiteLeg1_1, this.termiteLeg1_2, this.termiteLeg1_5, this.termiteBody, this.termiteHead, this.termiteLeg1_4).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
