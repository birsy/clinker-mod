package birsy.clinker.client.render.model.entity;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.BirsyBaseModel;
import birsy.clinker.client.render.model.BirsyModelRenderer;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomeModel2<T extends GnomeEntity> extends BirsyBaseModel<T> implements IHasArm, IHasHead {
	
	private List<ModelRenderer> modelRenderers = Lists.newArrayList();
	
    public BirsyModelRenderer gnomeBody;
    public BirsyModelRenderer gnomeRightArmHolder;
    public BirsyModelRenderer gnomeLeftArmHolder;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer armsJoint;
    public BirsyModelRenderer neckJoint;
    public BirsyModelRenderer gnomeOveralls;
    public BirsyModelRenderer gnomeLeftLeg;
    public BirsyModelRenderer gnomeRightLeg;
    public BirsyModelRenderer gnomeLeftArm;
    public BirsyModelRenderer gnomeRightArm;
    public BirsyModelRenderer gnomeNeck;
    public BirsyModelRenderer headJoint;
    public BirsyModelRenderer gnomeHead;
    public BirsyModelRenderer gnomeFace;
    public BirsyModelRenderer gnomeHatBottom;
    public BirsyModelRenderer gnomeBeard;
    public BirsyModelRenderer gnomeNose;
    public BirsyModelRenderer gnomeFaceBottom;
    public BirsyModelRenderer gnomeFaceTop;
    public BirsyModelRenderer gnomeHatMiddle;
    public BirsyModelRenderer gnomeHatTop;

    public GnomeModel2(float modelSize) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        
        this.gnomeBody = new BirsyModelRenderer(this, 0, 14);
        this.gnomeBody.setRotationPoint(0.0F, 12.25F, 0.0F);
        this.gnomeBody.addBox(-5.0F, -11.0F, -4.0F, 10.0F, 14.0F, 8.0F, modelSize-0.5F, modelSize, modelSize-0.5F);
        this.setRotateAngle(gnomeBody, 0.17453292519943295F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeBody);
       
        this.gnomeOveralls = new BirsyModelRenderer(this, 0, 36);
        this.gnomeOveralls.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.gnomeOveralls.addBox(-5.0F, -11.0F, -4.0F, 10.0F, 14.0F, 8.0F, -0.25F, 0.25F, -0.25F);
        modelRenderers.add(this.gnomeOveralls);
        
        
        this.armsJoint = new BirsyModelRenderer(this, 0, 0);
        this.armsJoint.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.armsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        modelRenderers.add(this.armsJoint);
        
        this.gnomeRightArm = new BirsyModelRenderer(this, 48, 25);
        this.gnomeRightArm.setRotationPoint(-5.35F, 0.0F, 0.0F);
        this.gnomeRightArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize-0.25F, modelSize-0.5F, modelSize-0.25F);
        modelRenderers.add(this.gnomeRightArm);
        
        this.gnomeRightArmHolder = new BirsyModelRenderer(this, 40, 40);
        this.gnomeRightArmHolder.setRotationPoint(-5.35F, 5.0F, 0.0F);
        this.gnomeRightArmHolder.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        modelRenderers.add(this.gnomeRightArmHolder);
        
        this.gnomeLeftArm = new BirsyModelRenderer(this, 48, 25);
        this.gnomeLeftArm.mirror = true;
        this.gnomeLeftArm.setRotationPoint(5.35F, 0.0F, 0.0F);
        this.gnomeLeftArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize-0.25F, modelSize-0.5F, modelSize-0.25F);
        modelRenderers.add(this.gnomeLeftArm);
        
        this.gnomeLeftArmHolder = new BirsyModelRenderer(this, 40, 40);
        this.gnomeLeftArmHolder.setRotationPoint(5.35F, 5.0F, 0.0F);
        this.gnomeLeftArmHolder.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        modelRenderers.add(this.gnomeLeftArmHolder);
        
        
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        modelRenderers.add(this.legsJoint);
        
        this.gnomeRightLeg = new BirsyModelRenderer(this, 56, 25);
        this.gnomeRightLeg.setRotationPoint(-3.0F, 0.0F, 0.0F);
        this.gnomeRightLeg.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 10.0F, 2.0F, modelSize-0.25F, modelSize-0.5F, modelSize-0.25F);
        modelRenderers.add(this.gnomeRightLeg);
        
        this.gnomeLeftLeg = new BirsyModelRenderer(this, 56, 25);
        this.gnomeLeftLeg.mirror = true;
        this.gnomeLeftLeg.setRotationPoint(3.0F, 0.0F, 0.0F);
        this.gnomeLeftLeg.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 10.0F, 2.0F, modelSize-0.25F, modelSize-0.5F, modelSize-0.25F);
        modelRenderers.add(this.gnomeLeftLeg);
        
        
        
        this.neckJoint = new BirsyModelRenderer(this, 0, 0);
        this.neckJoint.setRotationPoint(0.0F, -10.5F, -3.0F);
        this.neckJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(neckJoint, -0.17453292519943295F, 0.0F, 0.0F);
        modelRenderers.add(this.neckJoint);
     
        this.gnomeNeck = new BirsyModelRenderer(this, 46, 16);
        this.gnomeNeck.setRotationPoint(0.0F, 0.0F, -0.0F);
        this.gnomeNeck.addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomeNeck, -0.3490658503988659F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeNeck);
        
        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setRotationPoint(0.0F, 0.0F, -3.5F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headJoint, 0.3490658503988659F, 0.0F, 0.0F);
        modelRenderers.add(this.headJoint);
        
        this.gnomeHead = new BirsyModelRenderer(this, 32, 0);
        this.gnomeHead.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.gnomeHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeHead);
        
        this.gnomeFaceTop = new BirsyModelRenderer(this, 33, 12);
        this.gnomeFaceTop.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.gnomeFaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeFaceTop);
        
        this.gnomeFace = new BirsyModelRenderer(this, 32, 13);
        this.gnomeFace.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.gnomeFace.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeFace);
        
        this.gnomeNose = new BirsyModelRenderer(this, 50, 12);
        this.gnomeNose.setRotationPoint(0.0F, -1.5F, 0.0F);
        this.gnomeNose.addBox(-1.0F, -0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomeNose, -0.3441789165090569F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeNose);
        
        this.gnomeFaceBottom = new BirsyModelRenderer(this, 33, 19);
        this.gnomeFaceBottom.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.gnomeFaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeFaceBottom);  
        
        this.gnomeBeard = new BirsyModelRenderer(this, 28, 36);
        this.gnomeBeard.setRotationPoint(0.0F, 3.0F, -6.5F);
        this.gnomeBeard.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 7.0F, 1.0F, -0.25F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeBeard);
        
        
        this.gnomeHatTop = new BirsyModelRenderer(this, 0, 7);
        this.gnomeHatTop.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.gnomeHatTop.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomeHatTop, -0.23457224414434488F, 0.0F, 0.3127630032889644F);
        modelRenderers.add(this.gnomeHatTop); 
        
        this.gnomeHatMiddle = new BirsyModelRenderer(this, 0, 0);
        this.gnomeHatMiddle.setRotationPoint(0.0F, -1.0F, -0.0F);
        this.gnomeHatMiddle.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomeHatMiddle, -0.23457224414434488F, 0.0F, 0.19547687289441354F);
        modelRenderers.add(this.gnomeHatMiddle);
        
        this.gnomeHatBottom = new BirsyModelRenderer(this, 12, 7);
        this.gnomeHatBottom.setRotationPoint(0.0F, -3.0F, -3.0F);
        this.gnomeHatBottom.addBox(-2.5F, -1.0F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        modelRenderers.add(this.gnomeHatBottom);

        
        
        this.gnomeBody.addChild(this.neckJoint);
        this.legsJoint.addChild(this.gnomeRightLeg);
        this.gnomeFace.addChild(this.gnomeNose);
        this.gnomeHead.addChild(this.gnomeBeard);
        this.gnomeBody.addChild(this.armsJoint);
        this.gnomeHatMiddle.addChild(this.gnomeHatTop);
        this.gnomeFace.addChild(this.gnomeFaceBottom);
        this.gnomeBody.addChild(this.legsJoint);
        this.gnomeNeck.addChild(this.headJoint);
        this.gnomeHead.addChild(this.gnomeHatBottom);
        this.legsJoint.addChild(this.gnomeLeftLeg);
        this.armsJoint.addChild(this.gnomeLeftArm);
        this.neckJoint.addChild(this.gnomeNeck);
        this.gnomeHead.addChild(this.gnomeFace);
        this.gnomeBody.addChild(this.gnomeOveralls);
        this.headJoint.addChild(this.gnomeHead);
        this.gnomeHatBottom.addChild(this.gnomeHatMiddle);
        this.armsJoint.addChild(this.gnomeRightArm);
        this.gnomeFace.addChild(this.gnomeFaceTop);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.gnomeLeftArmHolder, this.gnomeBody, this.gnomeRightArmHolder).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	resetParts(this.gnomeBody, this.legsJoint, this.armsJoint, this.neckJoint, this.gnomeLeftLeg, this.gnomeRightLeg, this.gnomeLeftArm, this.gnomeRightArm, this.gnomeNeck, this.headJoint, this.gnomeHead, this.gnomeFace, this.gnomeNose, this.gnomeFaceBottom, this.gnomeFaceTop, this.gnomeHatBottom, this.gnomeHatMiddle, this.gnomeHatTop);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount * 2F;
    	
    	float globalSpeed = 1.25F;
    	float globalHeight = 0.75F;
    	float globalDegree = 1.25F;
    	
    	float walkSpeed = 0.5F * globalSpeed;
    	
    	//IDLE
    	swing(this.gnomeBody, 0.125F * globalSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	swing(this.gnomeRightArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.07F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.gnomeLeftArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 1.5F, -0.07F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.gnomeRightArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	swing(this.gnomeLeftArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	bob(this.gnomeRightArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	bob(this.gnomeLeftArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	
    	bob(this.neckJoint, 0.125F * globalSpeed, 0.2f * globalHeight, false, ageInTicks, 0.5F, true);
    	bob(this.gnomeBody, 0.5F * (0.125F * globalSpeed), 0.2f * globalHeight, true, ageInTicks, 0.5F, true);
    	
    	swing(this.gnomeHatMiddle, 0.1F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.X);
    	swing(this.gnomeHatMiddle, 0.1F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.Z);
    	
    	swing(this.gnomeBeard, 0.1F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.X);
    	swing(this.gnomeBeard, 0.1F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.Z);
    	
    	//WALK
    	swingLimbs(this.gnomeLeftLeg, this.gnomeRightLeg, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	swingLimbs(this.gnomeRightArm, this.gnomeLeftArm, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	
    	swing(this.gnomeRightArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, Axis.Z);
    	swing(this.gnomeLeftArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, -0.2F, f, f1, Axis.Z);
    	
    	swing(this.gnomeBody, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, Axis.X);
    	swing(this.gnomeBody, 0.5F * walkSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, f, f1, Axis.Y);
    	
    	bob(this.gnomeBody, 2.0F * walkSpeed, 2 * globalHeight, true, f, f1, true);
    	bob(this.gnomeHead, 2.0F * walkSpeed, 0.5F * globalHeight, true, f, f1, true);
    	
    	look(this.gnomeNeck, netHeadYaw, headPitch, 2.0F, 2.0F);
    	look(this.gnomeHead, netHeadYaw, headPitch, 2.0F, 2.0F);
    	
    	GnomeEntity.ArmPose GnomeEntity$armpose = entityIn.getArmPose();
        if (GnomeEntity$armpose == GnomeEntity.ArmPose.ATTACKING) {
           ModelHelper.func_239103_a_(this.gnomeRightArm, this.gnomeLeftArm, entityIn, this.swingProgress, ageInTicks);
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.SPELLCASTING || GnomeEntity$armpose == GnomeEntity.ArmPose.CELEBRATING) {
           this.gnomeRightArm.rotationPointZ =+ 0.0F;
           this.gnomeRightArm.rotationPointX =+ -5.0F;
           this.gnomeLeftArm.rotationPointZ =+ 0.0F;
           this.gnomeLeftArm.rotationPointX =+ 5.0F;
           this.gnomeRightArm.rotateAngleX =+ MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
           this.gnomeLeftArm.rotateAngleX =+ MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
           this.gnomeRightArm.rotateAngleZ =+ 2.3561945F;
           this.gnomeLeftArm.rotateAngleZ =+ -2.3561945F;
           this.gnomeRightArm.rotateAngleY =+ 0.0F;
           this.gnomeLeftArm.rotateAngleY =+ 0.0F;
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.BOW_AND_ARROW) {
           this.gnomeRightArm.rotateAngleY =+ -0.1F + this.gnomeHead.rotateAngleY;
           this.gnomeRightArm.rotateAngleX =+ (-(float)Math.PI / 2F) + this.gnomeHead.rotateAngleX;
           this.gnomeLeftArm.rotateAngleX =+ -0.9424779F + this.gnomeHead.rotateAngleX;
           this.gnomeLeftArm.rotateAngleY =+ this.gnomeHead.rotateAngleY - 0.4F;
           this.gnomeLeftArm.rotateAngleZ =+ ((float)Math.PI / 2F);
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.CROSSBOW_HOLD) {
           ModelHelper.func_239104_a_(this.gnomeRightArm, this.gnomeLeftArm, this.gnomeHead, true);
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.CROSSBOW_CHARGE) {
           ModelHelper.func_239102_a_(this.gnomeRightArm, this.gnomeLeftArm, entityIn, true);
        }
    	
    	this.gnomeBody.rotateAngleX += this.gnomeBody.defaultRotateAngleX;
    	this.gnomeNeck.rotateAngleX += this.gnomeNeck.defaultRotateAngleX;
    	
    	this.armsJoint.rotateAngleX = -this.gnomeBody.rotateAngleX;
    	this.legsJoint.rotateAngleX = -this.gnomeBody.rotateAngleX;
    	this.neckJoint.rotateAngleX = -this.gnomeBody.rotateAngleX;
    	this.headJoint.rotateAngleX = -this.gnomeNeck.rotateAngleX;
    	
    	this.gnomeRightArmHolder.copyModelAngles(this.gnomeRightArm);
    	this.gnomeRightArmHolder.rotationPointX = this.gnomeRightArm.rotationPointX + this.armsJoint.rotationPointX + this.gnomeBody.rotationPointX;
    	this.gnomeRightArmHolder.rotationPointY = this.gnomeRightArm.rotationPointY + this.armsJoint.rotationPointY + this.gnomeBody.rotationPointY;
    	this.gnomeRightArmHolder.rotationPointZ = this.gnomeRightArm.rotationPointZ + this.armsJoint.rotationPointZ + this.gnomeBody.rotationPointZ + -1.4F;
    	
    	this.gnomeLeftArmHolder.copyModelAngles(this.gnomeLeftArm);
    	this.gnomeLeftArmHolder.rotationPointX = this.gnomeLeftArm.rotationPointX + this.armsJoint.rotationPointX + this.gnomeBody.rotationPointX;
    	this.gnomeLeftArmHolder.rotationPointY = this.gnomeLeftArm.rotationPointY + this.armsJoint.rotationPointY + this.gnomeBody.rotationPointY;
    	this.gnomeLeftArmHolder.rotationPointZ = this.gnomeLeftArm.rotationPointZ + this.armsJoint.rotationPointZ + this.gnomeBody.rotationPointZ + -1.4F;
    	
        if (this.isSitting) {
            this.gnomeRightArm.rotateAngleX = (-(float)Math.PI / 5F);
            this.gnomeRightArm.rotateAngleY = 0.0F;
            this.gnomeRightArm.rotateAngleZ = 0.0F;
            this.gnomeLeftArm.rotateAngleX = (-(float)Math.PI / 5F);
            this.gnomeLeftArm.rotateAngleY = 0.0F;
            this.gnomeLeftArm.rotateAngleZ = 0.0F;
            this.gnomeRightLeg.rotateAngleX = -1.4137167F;
            this.gnomeRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.gnomeRightLeg.rotateAngleZ = 0.07853982F;
            this.gnomeLeftLeg.rotateAngleX = -1.4137167F;
            this.gnomeLeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
            this.gnomeLeftLeg.rotateAngleZ = -0.07853982F;
         }
    }

    public void setModelAttributes(GnomeModel2<T> model) {
		super.copyModelAttributesTo(model);
		
		model.gnomeBody.copyModelAngles(this.gnomeBody);
		model.gnomeOveralls.copyModelAngles(this.gnomeBody);
		model.legsJoint.copyModelAngles(this.legsJoint);
		model.armsJoint.copyModelAngles(this.armsJoint);
		model.neckJoint.copyModelAngles(this.neckJoint);
		model.gnomeLeftLeg.copyModelAngles(this.gnomeLeftLeg);
		model.gnomeRightLeg.copyModelAngles(this.gnomeRightLeg);
		model.gnomeLeftArm.copyModelAngles(this.gnomeLeftArm);
		model.gnomeRightArm.copyModelAngles(this.gnomeRightArm);
		model.gnomeNeck.copyModelAngles(this.gnomeNeck);
		model.headJoint.copyModelAngles(this.headJoint);
		model.gnomeHead.copyModelAngles(this.gnomeHead);
		model.gnomeFace.copyModelAngles(this.gnomeFace);
		model.gnomeNose.copyModelAngles(this.gnomeNose);
		model.gnomeFaceBottom.copyModelAngles(this.gnomeFaceBottom);
		model.gnomeFaceTop.copyModelAngles(this.gnomeFaceTop);
		model.gnomeHatBottom.copyModelAngles(this.gnomeHatBottom);
		model.gnomeHatMiddle.copyModelAngles(this.gnomeHatMiddle);
		model.gnomeHatTop.copyModelAngles(this.gnomeHatTop);
	}
    
    public void setVisible(boolean visible) {
    	this.gnomeBody.showModel = visible;
    	this.gnomeOveralls.showModel = visible;
		this.legsJoint.showModel = visible;
		this.armsJoint.showModel = visible;
		this.neckJoint.showModel = visible;
		this.gnomeLeftLeg.showModel = visible;
		this.gnomeRightLeg.showModel = visible;
		this.gnomeLeftArm.showModel = visible;
		this.gnomeRightArm.showModel = visible;
		this.gnomeNeck.showModel = visible;
		this.headJoint.showModel = visible;
		this.gnomeHead.showModel = visible;
		this.gnomeFace.showModel = visible;
		this.gnomeNose.showModel = visible;
		this.gnomeFaceBottom.showModel = visible;
		this.gnomeFaceTop.showModel = visible;
		this.gnomeHatBottom.showModel = visible;
		this.gnomeHatMiddle.showModel = visible;
		this.gnomeHatTop.showModel = visible;
    }
    
	public ModelRenderer getRandomModelRenderer(Random randomIn) {
		return this.modelRenderers.get(randomIn.nextInt(this.modelRenderers.size()));
	}
    
    @Override
	public ModelRenderer getModelHead() {
		return this.gnomeHead;
	}

	@Override
	public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
		this.getArmForSide(sideIn).translateRotate(matrixStackIn);
	}
	
	protected ModelRenderer getArmForSide(HandSide side)
	{
		return side == HandSide.LEFT ? this.gnomeLeftArmHolder : this.gnomeRightArmHolder;
	}
}
