package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;

/**
 * centipedeMerchant - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class CentipedeMerchantModel<T extends Entity> extends EntityModel<T> {
    public ModelPart centipedeMerchantBody;
    public ModelPart centipedeMerchantLeftLeg1;
    public ModelPart centipedeMerchantRightLeg2;
    public ModelPart centipedeMerchantLeftLeg2;
    public ModelPart centipedeMerchantLeftLeg1_1;
    public ModelPart centipedeMerchantArms4;
    public ModelPart chest;
    public ModelPart centipedeMerchantNeck;
    public ModelPart centipedeMerchantLeftArm;
    public ModelPart centipedeMerchantRightArm;
    public ModelPart chestKey;
    public ModelPart centipedeMerchantHead;
    public ModelPart centipedeMerchantAnten;
    public ModelPart centipedeMerchantArms1;
    public ModelPart centipedeMerchantArms2;
    public ModelPart centipedeMerchantArms3;
    public ModelPart centipedeMerchantArms4_1;

    public CentipedeMerchantModel() {
        this.texWidth = 128;
        this.texHeight = 256;
        this.chest = new ModelPart(this, 40, 56);
        this.chest.setPos(0.0F, -28.0F, 6.0F);
        this.chest.addBox(-7.0F, -14.0F, -7.0F, 14.0F, 14.0F, 14.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(chest, 0.0F, -0.20071286397934787F, 0.0F);
        this.centipedeMerchantLeftArm = new ModelPart(this, 82, 56);
        this.centipedeMerchantLeftArm.setPos(-6.0F, -10.0F, -13.0F);
        this.centipedeMerchantLeftArm.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 9.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantLeftArm, -0.27663469309900396F, 0.0F, -0.5235987755982988F);
        this.centipedeMerchantAnten = new ModelPart(this, 0, 66);
        this.centipedeMerchantAnten.setPos(0.0F, -64.0F, 0.0F);
        this.centipedeMerchantAnten.addBox(-8.0F, -16.0F, -0.5F, 16.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantAnten, 0.3120648556092001F, 0.0F, 0.0F);
        this.centipedeMerchantArms1 = new ModelPart(this, 70, 94);
        this.centipedeMerchantArms1.setPos(0.0F, -45.0F, -7.5F);
        this.centipedeMerchantArms1.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms1, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantBody = new ModelPart(this, 0, 0);
        this.centipedeMerchantBody.setPos(0.0F, 5.0F, 0.0F);
        this.centipedeMerchantBody.addBox(-11.0F, -28.0F, -13.0F, 22.0F, 28.0F, 28.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantBody, -0.2181661564992912F, 0.0F, 0.0F);
        this.centipedeMerchantRightArm = new ModelPart(this, 90, 56);
        this.centipedeMerchantRightArm.setPos(6.0F, -10.0F, -13.0F);
        this.centipedeMerchantRightArm.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 9.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantRightArm, -0.27663469309900396F, 0.0F, 0.5235987755982988F);
        this.centipedeMerchantLeftLeg1 = new ModelPart(this, 0, 0);
        this.centipedeMerchantLeftLeg1.setPos(-7.75F, 2.0F, -9.0F);
        this.centipedeMerchantLeftLeg1.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantNeck = new ModelPart(this, 0, 84);
        this.centipedeMerchantNeck.setPos(0.0F, -24.0F, -10.0F);
        this.centipedeMerchantNeck.addBox(-10.0F, -64.0F, -7.5F, 20.0F, 64.0F, 15.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantNeck, 0.39269908169872414F, 0.0F, 0.0F);
        this.centipedeMerchantLeftLeg1_1 = new ModelPart(this, 100, 28);
        this.centipedeMerchantLeftLeg1_1.setPos(7.75F, 2.0F, -9.0F);
        this.centipedeMerchantLeftLeg1_1.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantRightLeg2 = new ModelPart(this, 72, 0);
        this.centipedeMerchantRightLeg2.setPos(-9.0F, 2.0F, 9.0F);
        this.centipedeMerchantRightLeg2.addBox(-3.0F, 0.0F, -1.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantArms4_1 = new ModelPart(this, 70, 124);
        this.centipedeMerchantArms4_1.setPos(0.0F, -10.0F, -7.5F);
        this.centipedeMerchantArms4_1.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms4_1, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantArms3 = new ModelPart(this, 70, 114);
        this.centipedeMerchantArms3.setPos(0.0F, -22.5F, -7.5F);
        this.centipedeMerchantArms3.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms3, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantArms2 = new ModelPart(this, 70, 104);
        this.centipedeMerchantArms2.setPos(0.0F, -35.0F, -7.5F);
        this.centipedeMerchantArms2.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(centipedeMerchantArms2, 0.20071286397934787F, 0.0F, 0.0F);
        this.centipedeMerchantLeftLeg2 = new ModelPart(this, 96, 0);
        this.centipedeMerchantLeftLeg2.setPos(9.0F, 2.0F, 9.0F);
        this.centipedeMerchantLeftLeg2.addBox(-3.0F, 0.0F, -1.0F, 6.0F, 22.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantArms4 = new ModelPart(this, 0, 56);
        this.centipedeMerchantArms4.setPos(0.0F, -15.0F, -9.0F);
        this.centipedeMerchantArms4.addBox(-9.0F, -0.5F, -9.0F, 18.0F, 1.0F, 9.0F, 0.5F, 0.5F, 0.5F);
        this.setRotateAngle(centipedeMerchantArms4, 0.20071286397934787F, 0.0F, 0.0F);
        this.chestKey = new ModelPart(this, 0, 0);
        this.chestKey.setPos(7.0F, -10.0F, 0.0F);
        this.chestKey.addBox(0.0F, -1.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.centipedeMerchantHead = new ModelPart(this, 86, 74);
        this.centipedeMerchantHead.setPos(0.0F, -53.0F, -3.8F);
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
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.centipedeMerchantBody, this.centipedeMerchantLeftLeg1, this.centipedeMerchantLeftLeg1_1, this.centipedeMerchantRightLeg2, this.centipedeMerchantLeftLeg2, this.centipedeMerchantArms4).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
