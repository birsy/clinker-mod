package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MoleGatorModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer rootJoint;
    public BirsyModelRenderer gatorBody;
    public BirsyModelRenderer gatorTail;
    public BirsyModelRenderer gatorSpineA;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer headJoint;
    public BirsyModelRenderer gatorSpineB;
    public BirsyModelRenderer gatorFrontLeftLeg;
    public BirsyModelRenderer gatorBackLeftLeg;
    public BirsyModelRenderer gatorFrontRightLeg;
    public BirsyModelRenderer gatorBackRightLeg;
    public BirsyModelRenderer gatorHead;
    public BirsyModelRenderer gatorJaw;
    public BirsyModelRenderer gatorHeadSpineA;
    public BirsyModelRenderer gatorHeadSpineB;

    public MoleGatorModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.gatorBackLeftLeg = new BirsyModelRenderer(this, 0, 35);
        this.gatorBackLeftLeg.mirror = true;
        this.gatorBackLeftLeg.setRotationPoint(4.0F, 0.0F, 8.0F);
        this.gatorBackLeftLeg.addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorBackLeftLeg, 0.0F, -0.3490658503988659F, -0.2617993877991494F);
        this.gatorBackRightLeg = new BirsyModelRenderer(this, 0, 35);
        this.gatorBackRightLeg.setRotationPoint(-4.0F, 0.0F, 8.0F);
        this.gatorBackRightLeg.addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorBackRightLeg, 0.0F, 0.3490658503988659F, 0.2617993877991494F);
        this.gatorJaw = new BirsyModelRenderer(this, 26, 0);
        this.gatorJaw.setRotationPoint(0.0F, 0.5F, 1.0F);
        this.gatorJaw.addBox(-3.5F, 0.0F, -9.0F, 7.0F, 2.0F, 10.0F, -0.5F, -0.1F, 0.0F);
        this.setRotateAngle(gatorJaw, 0.17453292519943295F, 0.0F, 0.0F);
        this.gatorBody = new BirsyModelRenderer(this, 0, 35);
        this.gatorBody.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.gatorBody.addBox(-5.5F, -8.0F, -9.5F, 11.0F, 10.0F, 19.0F, 0.0F, 0.0F, 0.0F);
        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setRotationPoint(0.0F, 0.0F, -9.5F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gatorHeadSpineA = new BirsyModelRenderer(this, 0, 8);
        this.gatorHeadSpineA.mirror = true;
        this.gatorHeadSpineA.setRotationPoint(0.0F, -3.5F, 2.0F);
        this.gatorHeadSpineA.addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorHeadSpineA, 0.0F, 0.0F, -0.3490658503988659F);
        this.gatorTail = new BirsyModelRenderer(this, 20, 12);
        this.gatorTail.setRotationPoint(0.0F, -2.0F, 9.5F);
        this.gatorTail.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 18.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorTail, -0.2617993877991494F, 0.0F, 0.0F);
        this.gatorSpineB = new BirsyModelRenderer(this, 0, 4);
        this.gatorSpineB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.gatorSpineB.addBox(0.0F, -6.0F, -8.5F, 0.0F, 6.0F, 17.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorSpineB, 0.0F, 0.0F, 0.6981317007977318F);
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.gatorSpineA = new BirsyModelRenderer(this, 0, 4);
        this.gatorSpineA.setRotationPoint(0.0F, -8.0F, -1.0F);
        this.gatorSpineA.addBox(0.0F, -6.0F, -8.5F, 0.0F, 6.0F, 17.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorSpineA, 0.0F, 0.0F, -0.3490658503988659F);
        this.rootJoint = new BirsyModelRenderer(this, 0, 0);
        this.rootJoint.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rootJoint.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gatorHead = new BirsyModelRenderer(this, 0, 0);
        this.gatorHead.setRotationPoint(0.0F, 2.0F, -2.0F);
        this.gatorHead.addBox(-3.5F, -3.5F, -10.0F, 7.0F, 5.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.gatorFrontRightLeg = new BirsyModelRenderer(this, 0, 35);
        this.gatorFrontRightLeg.mirror = true;
        this.gatorFrontRightLeg.setRotationPoint(-4.0F, 0.0F, -5.6F);
        this.gatorFrontRightLeg.addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorFrontRightLeg, 0.0F, -0.3490658503988659F, 0.2617993877991494F);
        this.gatorFrontLeftLeg = new BirsyModelRenderer(this, 0, 35);
        this.gatorFrontLeftLeg.setRotationPoint(4.0F, 0.0F, -5.6F);
        this.gatorFrontLeftLeg.addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorFrontLeftLeg, 0.0F, 0.3490658503988659F, -0.2617993877991494F);
        this.gatorHeadSpineB = new BirsyModelRenderer(this, 0, 8);
        this.gatorHeadSpineB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.gatorHeadSpineB.addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gatorHeadSpineB, 0.0F, 0.0F, 0.6981317007977318F);
        this.legsJoint.addChild(this.gatorBackLeftLeg);
        this.legsJoint.addChild(this.gatorBackRightLeg);
        this.gatorHead.addChild(this.gatorJaw);
        this.rootJoint.addChild(this.gatorBody);
        this.gatorBody.addChild(this.headJoint);
        this.gatorHead.addChild(this.gatorHeadSpineA);
        this.gatorBody.addChild(this.gatorTail);
        this.gatorBody.addChild(this.legsJoint);
        this.gatorBody.addChild(this.gatorSpineA);
        this.headJoint.addChild(this.gatorHead);
        this.legsJoint.addChild(this.gatorFrontRightLeg);
        this.legsJoint.addChild(this.gatorFrontLeftLeg);
        this.gatorSpineA.addChild(this.gatorSpineB);
        this.gatorHeadSpineA.addChild(this.gatorHeadSpineB);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        this.rootJoint.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
