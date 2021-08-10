package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;

/**
 * ShoggothModel - birse
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class LesserShoggothHeadModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer shoggothBase;
    public BirsyModelRenderer headJoint;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer shoggothHead;
    public BirsyModelRenderer shoggothEye;
    public BirsyModelRenderer shoggothMouth;
    public BirsyModelRenderer shoggothRightLegHolder;
    public BirsyModelRenderer shoggothLeftLegHolder;
    public BirsyModelRenderer shoggothFrontLegHolder;
    public BirsyModelRenderer shoggothBackLegHolder;
    public BirsyModelRenderer shoggothRightLeg;
    public BirsyModelRenderer shoggothLeftLeg;
    public BirsyModelRenderer shoggothFrontLeg;
    public BirsyModelRenderer shoggothBackLeg;

    public LesserShoggothHeadModel() {
        this.texWidth = 128;
        this.texHeight = 128;
        this.shoggothBase = new BirsyModelRenderer(this, 0, 81);
        this.shoggothBase.setPos(0.0F, 11.0F, 0.0F);
        this.shoggothBase.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothBase, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothRightLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothRightLegHolder.setPos(-11.31F, 0.0F, 0.0F);
        this.shoggothRightLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothRightLegHolder, 1.7453292129831807E-4F, 0.0F, 0.0F);
        this.shoggothBackLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothBackLegHolder.setPos(0.0F, 0.0F, 11.31F);
        this.shoggothBackLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.shoggothFrontLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothFrontLegHolder.setPos(0.0F, 0.0F, -11.31F);
        this.shoggothFrontLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setPos(0.0F, -6.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legsJoint, 0.0F, -0.7853981633974483F, 0.0F);
        this.shoggothFrontLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothFrontLeg.setPos(0.0F, 0.0F, 0.0F);
        this.shoggothFrontLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothFrontLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothBackLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothBackLeg.setPos(0.0F, 0.0F, 0.0F);
        this.shoggothBackLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothBackLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothEye = new BirsyModelRenderer(this, 0, 0);
        this.shoggothEye.setPos(0.0F, -18.0F, -1.0F);
        this.shoggothEye.addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.shoggothLeftLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothLeftLeg.setPos(0.0F, 0.0F, 0.0F);
        this.shoggothLeftLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothLeftLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothHead = new BirsyModelRenderer(this, 0, 32);
        this.shoggothHead.setPos(0.0F, -11.0F, 0.0F);
        this.shoggothHead.addBox(-12.0F, -20.0F, -11.5F, 24.0F, 26.0F, 23.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothHead, 0.0F, 0.0F, -0.2617993877991494F);
        this.shoggothLeftLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothLeftLegHolder.setPos(11.31F, 0.0F, 0.0F);
        this.shoggothLeftLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setPos(0.0F, 0.0F, 0.0F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headJoint, 0.0F, -0.7853981633974483F, 0.0F);
        this.shoggothRightLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothRightLeg.setPos(0.0F, 0.0F, 0.0F);
        this.shoggothRightLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothRightLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothMouth = new BirsyModelRenderer(this, 64, 0);
        this.shoggothMouth.setPos(5.0F, -5.0F, 2.0F);
        this.shoggothMouth.addBox(-8.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothMouth, 0.0F, -1.5707963267948966F, 0.0F);
        this.legsJoint.addChild(this.shoggothRightLegHolder);
        this.legsJoint.addChild(this.shoggothBackLegHolder);
        this.legsJoint.addChild(this.shoggothFrontLegHolder);
        this.shoggothBase.addChild(this.legsJoint);
        this.shoggothFrontLegHolder.addChild(this.shoggothFrontLeg);
        this.shoggothBackLegHolder.addChild(this.shoggothBackLeg);
        this.shoggothHead.addChild(this.shoggothEye);
        this.shoggothLeftLegHolder.addChild(this.shoggothLeftLeg);
        this.headJoint.addChild(this.shoggothHead);
        this.legsJoint.addChild(this.shoggothLeftLegHolder);
        this.shoggothBase.addChild(this.headJoint);
        this.shoggothRightLegHolder.addChild(this.shoggothRightLeg);
        this.shoggothHead.addChild(this.shoggothMouth);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ImmutableList.of(this.shoggothBase).forEach((ModelRenderer) -> {
            ModelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
