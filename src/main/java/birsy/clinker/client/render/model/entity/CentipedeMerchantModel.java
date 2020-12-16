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
 * centipedeMerchant - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class CentipedeMerchantModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer centipedeMerchantBody;
    public ModelRenderer centipedeMerchantLeftLeg1;
    public ModelRenderer centipedeMerchantRightLeg2;
    public ModelRenderer centipedeMerchantLeftLeg2;
    public ModelRenderer centipedeMerchantLeftLeg1_1;
    public ModelRenderer centipedeMerchantArms4;
    public ModelRenderer chest;
    public ModelRenderer centipedeMerchantNeck;
    public ModelRenderer centipedeMerchantLeftArm;
    public ModelRenderer centipedeMerchantRightArm;
    public ModelRenderer chestKey;
    public ModelRenderer centipedeMerchantHead;
    public ModelRenderer centipedeMerchantAnten;
    public ModelRenderer centipedeMerchantArms1;
    public ModelRenderer centipedeMerchantArms2;
    public ModelRenderer centipedeMerchantArms3;
    public ModelRenderer centipedeMerchantArms4_1;

    public CentipedeMerchantModel() {
        this.textureWidth = 128;
        this.textureHeight = 256;
        this.chest = new ModelRenderer(this, 40, 56);
        this.chest.setRotationPoint(0.0F, -28.0F, 6.0F);
        this.chest.addBox(-7.0F, -14.0F, -7.0F, 14.0F, 14.0F, 14.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(chest, 0.0F, -0.20071286397934787F, 0.0F);
        this.centipedeMerchantLeftArm = new ModelRenderer(this, 82, 56);
        this.centipedeMerchantLeftArm.setRotationPoint(-6.0F, -10.0F, -13.0F);
        this.centipedeMerchantLeftArm.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 9.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantLeftArm, -0.27663469309900396F, 0.0F, -0.5235987755982988F);
        this.centipedeMerchantAnten = new ModelRenderer(this, 0, 66);
        this.centipedeMerchantAnten.setRotationPoint(0.0F, -64.0F, 0.0F);
        this.centipedeMerchantAnten.addBox(-8.0F, -16.0F, -0.5F, 16.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantAnten, 0.3120648556092001F, 0.0F, 0.0F);
        this.centipedeMerchantArms1 = new ModelRenderer(this, 70, 94);
        this.centipedeMerchantArms1.setRotationPoint(0.0F, -45.0F, -7.5F);
        this.centipedeMerchantArms1.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms1, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantBody = new ModelRenderer(this, 0, 0);
        this.centipedeMerchantBody.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.centipedeMerchantBody.addBox(-11.0F, -28.0F, -13.0F, 22.0F, 28.0F, 28.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantBody, -0.2181661564992912F, 0.0F, 0.0F);
        this.centipedeMerchantRightArm = new ModelRenderer(this, 90, 56);
        this.centipedeMerchantRightArm.setRotationPoint(6.0F, -10.0F, -13.0F);
        this.centipedeMerchantRightArm.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 9.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantRightArm, -0.27663469309900396F, 0.0F, 0.5235987755982988F);
        this.centipedeMerchantLeftLeg1 = new ModelRenderer(this, 0, 0);
        this.centipedeMerchantLeftLeg1.setRotationPoint(-7.75F, 2.0F, -9.0F);
        this.centipedeMerchantLeftLeg1.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantNeck = new ModelRenderer(this, 0, 84);
        this.centipedeMerchantNeck.setRotationPoint(0.0F, -24.0F, -10.0F);
        this.centipedeMerchantNeck.addBox(-10.0F, -64.0F, -7.5F, 20.0F, 64.0F, 15.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantNeck, 0.39269908169872414F, 0.0F, 0.0F);
        this.centipedeMerchantLeftLeg1_1 = new ModelRenderer(this, 100, 28);
        this.centipedeMerchantLeftLeg1_1.setRotationPoint(7.75F, 2.0F, -9.0F);
        this.centipedeMerchantLeftLeg1_1.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantRightLeg2 = new ModelRenderer(this, 72, 0);
        this.centipedeMerchantRightLeg2.setRotationPoint(-9.0F, 2.0F, 9.0F);
        this.centipedeMerchantRightLeg2.addBox(-3.0F, 0.0F, -1.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantArms4_1 = new ModelRenderer(this, 70, 124);
        this.centipedeMerchantArms4_1.setRotationPoint(0.0F, -10.0F, -7.5F);
        this.centipedeMerchantArms4_1.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms4_1, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantArms3 = new ModelRenderer(this, 70, 114);
        this.centipedeMerchantArms3.setRotationPoint(0.0F, -22.5F, -7.5F);
        this.centipedeMerchantArms3.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms3, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantArms2 = new ModelRenderer(this, 70, 104);
        this.centipedeMerchantArms2.setRotationPoint(0.0F, -35.0F, -7.5F);
        this.centipedeMerchantArms2.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms2, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantLeftLeg2 = new ModelRenderer(this, 96, 0);
        this.centipedeMerchantLeftLeg2.setRotationPoint(9.0F, 2.0F, 9.0F);
        this.centipedeMerchantLeftLeg2.addBox(-3.0F, 0.0F, -1.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantArms4 = new ModelRenderer(this, 0, 56);
        this.centipedeMerchantArms4.setRotationPoint(0.0F, -15.0F, -9.0F);
        this.centipedeMerchantArms4.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.5F, 0.5F, 0.5F);
        this.setRotateAngle(centipedeMerchantArms4, 0.20071286397934787F, 0.0F, 0.0F);
        this.chestKey = new ModelRenderer(this, 0, 0);
        this.chestKey.setRotationPoint(7.0F, -10.0F, 0.0F);
        this.chestKey.addBox(0.0F, -1.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantHead = new ModelRenderer(this, 86, 74);
        this.centipedeMerchantHead.setRotationPoint(0.0F, -53.0F, -3.8F);
        this.centipedeMerchantHead.addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantHead, -0.17453292519943295F, 0.0F, 0.0F);
        this.centipedeMerchantBody.addChild(this.chest);
        this.centipedeMerchantBody.addChild(this.centipedeMerchantLeftArm);
        this.centipedeMerchantNeck.addChild(this.centipedeMerchantAnten);
        this.centipedeMerchantNeck.addChild(this.centipedeMerchantArms1);
        this.centipedeMerchantBody.addChild(this.centipedeMerchantRightArm);
        this.centipedeMerchantBody.addChild(this.centipedeMerchantNeck);
        this.centipedeMerchantNeck.addChild(this.centipedeMerchantArms4_1);
        this.centipedeMerchantNeck.addChild(this.centipedeMerchantArms3);
        this.centipedeMerchantNeck.addChild(this.centipedeMerchantArms2);
        this.chest.addChild(this.chestKey);
        this.centipedeMerchantNeck.addChild(this.centipedeMerchantHead);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.centipedeMerchantBody, this.centipedeMerchantLeftLeg1, this.centipedeMerchantLeftLeg1_1, this.centipedeMerchantRightLeg2, this.centipedeMerchantLeftLeg2, this.centipedeMerchantArms4).forEach((modelRenderer) -> { 
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
