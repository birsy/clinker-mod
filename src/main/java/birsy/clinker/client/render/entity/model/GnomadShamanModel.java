package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.AnimFunctions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.gnomad.GnomadShamanEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * NewNewshamanShamanModel - doclg
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomadShamanModel<T extends GnomadShamanEntity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer shamanBody;
    public BirsyModelRenderer shamanTornBottom;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer armsJoint;
    public BirsyModelRenderer neckJoint;
    public BirsyModelRenderer shamanPouch;
    public BirsyModelRenderer shamanLeftLeg;
    public BirsyModelRenderer shamanRightLeg;
    public BirsyModelRenderer shamanLeftArm;
    public BirsyModelRenderer shamanRightArm;
    public BirsyModelRenderer shamanStaff;
    public BirsyModelRenderer shamanStaffRing;
    public BirsyModelRenderer shamanStaffCross;
    public BirsyModelRenderer shamanStaffLowerCross;
    public BirsyModelRenderer shamanStaffRingInside;
    public BirsyModelRenderer shamanNeck;
    public BirsyModelRenderer headJoint;
    public BirsyModelRenderer shamanHead;
    public BirsyModelRenderer shamanFace;
    public BirsyModelRenderer shamanHatBrim;
    public BirsyModelRenderer shamanBeard;
    public BirsyModelRenderer shamanNose;
    public BirsyModelRenderer shamanFaceBottom;
    public BirsyModelRenderer shamanFaceTop;
    public BirsyModelRenderer shamanHat;

    public GnomadShamanModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.shamanStaff = new BirsyModelRenderer(this, 60, 37);
        this.shamanStaff.setRotationPoint(0.0F, 10.25F, 0.0F);
        this.shamanStaff.addBox(-0.5F, -16.0F, -0.5F, 1.0F, 26.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shamanStaff, 1.5707963267948966F, 0.0F, 0.0F);
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.shamanBeard = new BirsyModelRenderer(this, 28, 22);
        this.shamanBeard.setRotationPoint(0.0F, 3.0F, -6.0F);
        this.shamanBeard.addBox(-4.0F, 0.0F, -0.5F, 8.0F, 7.0F, 1.0F, -0.5F, 0.0F, 0.0F);
        this.shamanStaffRingInside = new BirsyModelRenderer(this, 36, 36);
        this.shamanStaffRingInside.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shamanStaffRingInside.addBox(-1.5F, -4.0F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shamanNeck = new BirsyModelRenderer(this, 46, 16);
        this.shamanNeck.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shamanNeck.addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shamanNeck, -0.3490658503988659F, 0.0F, 0.0F);
        this.shamanPouch = new BirsyModelRenderer(this, 19, 15);
        this.shamanPouch.setRotationPoint(2.0F, -0.5F, 4.0F);
        this.shamanPouch.addBox(-2.0F, -1.5F, 0.0F, 4.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shamanPouch, 0.11728612207217244F, 0.0F, 0.0F);
        this.shamanFaceTop = new BirsyModelRenderer(this, 33, 12);
        this.shamanFaceTop.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.shamanFaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shamanFace = new BirsyModelRenderer(this, 32, 13);
        this.shamanFace.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.shamanFace.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shamanTornBottom = new BirsyModelRenderer(this, 0, 46);
        this.shamanTornBottom.setRotationPoint(0.0F, 3.0F, 4.0F);
        this.shamanTornBottom.addBox(-5.0F, 0.0F, -8.0F, 10.0F, 10.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shamanTornBottom, -0.17453292519943295F, 0.0F, 0.0F);
        this.shamanLeftArm = new BirsyModelRenderer(this, 48, 25);
        this.shamanLeftArm.mirror = true;
        this.shamanLeftArm.setRotationPoint(5.35F, 0.0F, 0.0F);
        this.shamanLeftArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.shamanStaffRing = new BirsyModelRenderer(this, 36, 30);
        this.shamanStaffRing.setRotationPoint(0.0F, -16.0F, 0.0F);
        this.shamanStaffRing.addBox(-2.5F, -5.0F, -0.5F, 5.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shamanBody = new BirsyModelRenderer(this, 0, 24);
        this.shamanBody.setRotationPoint(0.0F, 13.0F, 0.0F);
        this.shamanBody.addBox(-5.0F, -11.0F, -4.0F, 10.0F, 14.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shamanBody, 0.17453292519943295F, 0.0F, 0.0F);
        this.shamanRightArm = new BirsyModelRenderer(this, 48, 25);
        this.shamanRightArm.setRotationPoint(-5.35F, 0.0F, 0.0F);
        this.shamanRightArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.shamanHatBrim = new BirsyModelRenderer(this, 0, 18);
        this.shamanHatBrim.setRotationPoint(0.0F, -3.0F, -3.0F);
        this.shamanHatBrim.addBox(-3.5F, -1.0F, -2.5F, 7.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.shamanHat = new BirsyModelRenderer(this, 0, 10);
        this.shamanHat.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.shamanHat.addBox(-2.0F, -3.5F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.shamanLeftLeg = new BirsyModelRenderer(this, 56, 25);
        this.shamanLeftLeg.mirror = true;
        this.shamanLeftLeg.setRotationPoint(3.4F, 0.0F, 0.0F);
        this.shamanLeftLeg.addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setScale(0.8F);
        this.headJoint.setRotationPoint(0.0F, 0.0F, -3.5F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headJoint, 0.3490658503988659F, 0.0F, 0.0F);
        this.armsJoint = new BirsyModelRenderer(this, 0, 0);
        this.armsJoint.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.armsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.shamanRightLeg = new BirsyModelRenderer(this, 56, 25);
        this.shamanRightLeg.setRotationPoint(-3.5F, 0.0F, 0.0F);
        this.shamanRightLeg.addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.shamanStaffCross = new BirsyModelRenderer(this, 36, 40);
        this.shamanStaffCross.setRotationPoint(0.0F, -14.0F, 0.0F);
        this.shamanStaffCross.addBox(-2.5F, 0.0F, -0.5F, 5.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.neckJoint = new BirsyModelRenderer(this, 0, 0);
        this.neckJoint.setRotationPoint(0.0F, -10.5F, -3.0F);
        this.neckJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(neckJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.shamanFaceBottom = new BirsyModelRenderer(this, 33, 19);
        this.shamanFaceBottom.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.shamanFaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shamanStaffLowerCross = new BirsyModelRenderer(this, 36, 42);
        this.shamanStaffLowerCross.setRotationPoint(0.0F, -11.0F, 0.0F);
        this.shamanStaffLowerCross.addBox(-1.5F, 0.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shamanNose = new BirsyModelRenderer(this, 50, 12);
        this.shamanNose.setRotationPoint(0.0F, -1.5F, 0.0F);
        this.shamanNose.addBox(-1.0F, -0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shamanNose, -0.3441789165090569F, 0.0F, 0.0F);
        this.shamanHead = new BirsyModelRenderer(this, 32, 0);
        this.shamanHead.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.shamanHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.shamanRightArm.addChild(this.shamanStaff);
        this.shamanBody.addChild(this.legsJoint);
        this.shamanHead.addChild(this.shamanBeard);
        this.shamanStaffRing.addChild(this.shamanStaffRingInside);
        this.neckJoint.addChild(this.shamanNeck);
        this.shamanBody.addChild(this.shamanPouch);
        this.shamanFace.addChild(this.shamanFaceTop);
        this.shamanHead.addChild(this.shamanFace);
        this.shamanBody.addChild(this.shamanTornBottom);
        this.armsJoint.addChild(this.shamanLeftArm);
        this.shamanStaff.addChild(this.shamanStaffRing);
        this.armsJoint.addChild(this.shamanRightArm);
        this.shamanHead.addChild(this.shamanHatBrim);
        this.shamanHatBrim.addChild(this.shamanHat);
        this.legsJoint.addChild(this.shamanLeftLeg);
        this.shamanNeck.addChild(this.headJoint);
        this.shamanBody.addChild(this.armsJoint);
        this.legsJoint.addChild(this.shamanRightLeg);
        this.shamanStaff.addChild(this.shamanStaffCross);
        this.shamanBody.addChild(this.neckJoint);
        this.shamanFace.addChild(this.shamanFaceBottom);
        this.shamanStaff.addChild(this.shamanStaffLowerCross);
        this.shamanFace.addChild(this.shamanNose);
        this.headJoint.addChild(this.shamanHead);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.shamanBody).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) 
    {
        AnimFunctions.resetParts(this.shamanBody, this.shamanTornBottom, this.legsJoint, this.armsJoint, this.neckJoint, this.shamanPouch, this.shamanLeftLeg, this.shamanRightLeg, this.shamanLeftArm, this.shamanRightArm, this.shamanStaff, this.shamanStaffRing, this.shamanStaffCross, this.shamanStaffLowerCross, this.shamanStaffRingInside, this.shamanNeck, this.headJoint, this.shamanHead, this.shamanFace, this.shamanHatBrim, this.shamanBeard, this.shamanNose, this.shamanFaceBottom, this.shamanFaceTop, this.shamanHat);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount * 2F;
    	
    	float globalSpeed = 1.25F;
    	float globalHeight = 0.75F;
    	float globalDegree = 1.25F;
    	
    	float walkSpeed = 0.5F * globalSpeed;
    	
    	//IDLE
    	AnimFunctions.swing(this.shamanBody, 0.125F * globalSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
    	
    	AnimFunctions.swing(this.shamanRightArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.07F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
    	AnimFunctions.swing(this.shamanLeftArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 1.5F, -0.07F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
    	AnimFunctions.swing(this.shamanRightArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
    	AnimFunctions.swing(this.shamanLeftArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
    	
    	AnimFunctions.bob(this.shamanRightArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	AnimFunctions.bob(this.shamanLeftArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	
    	AnimFunctions.bob(this.neckJoint, 0.125F * globalSpeed, 0.2f * globalHeight, false, ageInTicks, 0.5F, true);
    	AnimFunctions.bob(this.shamanBody, 0.5F * (0.125F * globalSpeed), 0.2f * globalHeight, true, ageInTicks, 0.5F, true);
    	
    	AnimFunctions.swing(this.shamanHat, 0.125F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.X);
    	AnimFunctions.swing(this.shamanHat, 0.125F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
    	
    	//WALK
    	AnimFunctions.swingLimbs(this.shamanLeftLeg, this.shamanRightLeg, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	AnimFunctions.swingLimbs(this.shamanRightArm, this.shamanLeftArm, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	
    	AnimFunctions.swing(this.shamanRightArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, AnimFunctions.Axis.Z);
    	AnimFunctions.swing(this.shamanLeftArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, -0.2F, f, f1, AnimFunctions.Axis.Z);
    	
    	AnimFunctions.swing(this.shamanBody, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, AnimFunctions.Axis.X);
    	AnimFunctions.swing(this.shamanBody, 0.5F * walkSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Y);
    	
    	AnimFunctions.bob(this.shamanBody, 2.0F * walkSpeed, 2 * globalHeight, true, f, f1, true);
    	AnimFunctions.bob(this.shamanHead, 2.0F * walkSpeed, 0.5F * globalHeight, true, f, f1, true);
    	
    	AnimFunctions.look(this.shamanNeck, netHeadYaw, headPitch, 2.0F, 2.0F);
    	AnimFunctions.look(this.shamanHead, netHeadYaw, headPitch, 2.0F, 2.0F);
    	
    	this.shamanBody.rotateAngleX += this.shamanBody.defaultRotateAngleX;
    	this.shamanNeck.rotateAngleX += this.shamanNeck.defaultRotateAngleX;
    	
    	this.shamanTornBottom.rotateAngleX = -this.shamanBody.rotateAngleX;
    	this.armsJoint.rotateAngleX = -this.shamanBody.rotateAngleX;
    	this.legsJoint.rotateAngleX = -this.shamanBody.rotateAngleX;
    	this.neckJoint.rotateAngleX = -this.shamanBody.rotateAngleX;
    	this.headJoint.rotateAngleX = -this.shamanNeck.rotateAngleX;
    }
}
