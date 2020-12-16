package birsy.clinker.client.render.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.BirsyBaseModel;
import birsy.clinker.client.render.model.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.AbstractGnomadEntity;
import birsy.clinker.common.entity.monster.GnomadAxemanEntity;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NewGnomadModel<T extends GnomadAxemanEntity> extends BirsyBaseModel<T> implements IHasArm, IHasHead {
	public BirsyModelRenderer gnomadBody;
    public BirsyModelRenderer gnomadRightArmHolder;
    public BirsyModelRenderer gnomadLeftArmHolder;
    public BirsyModelRenderer gnomadTornBottom;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer armsJoint;
    public BirsyModelRenderer neckJoint;
    public BirsyModelRenderer gnomadGoldSack;
    public BirsyModelRenderer gnomadLeftLeg;
    public BirsyModelRenderer gnomadRightLeg;
    public BirsyModelRenderer gnomadLeftArm;
    public BirsyModelRenderer gnomadRightArm;
    public BirsyModelRenderer gnomadNeck;
    public BirsyModelRenderer headJoint;
    public BirsyModelRenderer gnomadHead;
    public BirsyModelRenderer gnomadFace;
    public BirsyModelRenderer gnomadHat;
    public BirsyModelRenderer gnomadNose;
    public BirsyModelRenderer gnomadFaceBottom;
    public BirsyModelRenderer gnomadFaceTop;

    public NewGnomadModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.gnomadNose = new BirsyModelRenderer(this, 50, 12);
        this.gnomadNose.setRotationPoint(0.0F, -1.5F, 0.0F);
        this.gnomadNose.addBox(-1.0F, -0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomadNose, -0.3441789165090569F, 0.0F, 0.0F);
        this.gnomadHead = new BirsyModelRenderer(this, 32, 0);
        this.gnomadHead.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.gnomadHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.armsJoint = new BirsyModelRenderer(this, 0, 0);
        this.armsJoint.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.armsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.gnomadRightLeg = new BirsyModelRenderer(this, 56, 25);
        this.gnomadRightLeg.setRotationPoint(-3.5F, 0.0F, 0.0F);
        this.gnomadRightLeg.addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.gnomadRightArm = new BirsyModelRenderer(this, 48, 25);
        this.gnomadRightArm.setRotationPoint(-5.35F, 0.0F, 0.0F);
        this.gnomadRightArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.gnomadFaceBottom = new BirsyModelRenderer(this, 33, 19);
        this.gnomadFaceBottom.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.gnomadFaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.neckJoint = new BirsyModelRenderer(this, 0, 0);
        this.neckJoint.setRotationPoint(0.0F, -10.5F, -3.0F);
        this.neckJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(neckJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.gnomadLeftArm = new BirsyModelRenderer(this, 48, 25);
        this.gnomadLeftArm.mirror = true;
        this.gnomadLeftArm.setRotationPoint(5.35F, 0.0F, 0.0F);
        this.gnomadLeftArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setRotationPoint(0.0F, 0.0F, -3.5F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headJoint, 0.3490658503988659F, 0.0F, 0.0F);
        this.gnomadHat = new BirsyModelRenderer(this, 32, 22);
        this.gnomadHat.setRotationPoint(0.0F, -3.0F, -3.5F);
        this.gnomadHat.addBox(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomadHat, 0.19547687289441354F, 0.0F, 0.19547687289441354F);
        this.gnomadBody = new BirsyModelRenderer(this, 0, 24);
        this.gnomadBody.setRotationPoint(0.0F, 13.0F, 0.0F);
        this.gnomadBody.addBox(-5.0F, -11.0F, -4.0F, 10.0F, 14.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomadBody, 0.17453292519943295F, 0.0F, 0.0F);
        this.gnomadLeftLeg = new BirsyModelRenderer(this, 56, 25);
        this.gnomadLeftLeg.mirror = true;
        this.gnomadLeftLeg.setRotationPoint(3.4F, 0.0F, 0.0F);
        this.gnomadLeftLeg.addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.gnomadNeck = new BirsyModelRenderer(this, 46, 16);
        this.gnomadNeck.setRotationPoint(0.0F, 0.0F, -0.0F);
        this.gnomadNeck.addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomadNeck, -0.3490658503988659F, 0.0F, 0.0F);
        this.gnomadFace = new BirsyModelRenderer(this, 32, 13);
        this.gnomadFace.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.gnomadFace.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadGoldSack = new BirsyModelRenderer(this, 0, 0);
        this.gnomadGoldSack.setRotationPoint(0.0F, -4.0F, 4.0F);
        this.gnomadGoldSack.addBox(-4.5F, -6.0F, 0.0F, 9.0F, 12.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadLeftArmHolder = new BirsyModelRenderer(this, 40, 40);
        this.gnomadLeftArmHolder.setRotationPoint(5.35F, 5.0F, 0.0F);
        this.gnomadLeftArmHolder.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.gnomadRightArmHolder = new BirsyModelRenderer(this, 40, 40);
        this.gnomadRightArmHolder.setRotationPoint(-5.35F, 5.0F, 0.0F);
        this.gnomadRightArmHolder.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
        this.gnomadTornBottom = new BirsyModelRenderer(this, 0, 46);
        this.gnomadTornBottom.setRotationPoint(0.0F, 3.0F, 4.0F);
        this.gnomadTornBottom.addBox(-5.0F, 0.0F, -8.0F, 10.0F, 10.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(gnomadTornBottom, -0.17453292519943295F, 0.0F, 0.0F);
        this.gnomadFaceTop = new BirsyModelRenderer(this, 33, 12);
        this.gnomadFaceTop.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.gnomadFaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadFace.addChild(this.gnomadNose);
        this.headJoint.addChild(this.gnomadHead);
        this.gnomadBody.addChild(this.armsJoint);
        this.legsJoint.addChild(this.gnomadRightLeg);
        this.armsJoint.addChild(this.gnomadRightArm);
        this.gnomadFace.addChild(this.gnomadFaceBottom);
        this.gnomadBody.addChild(this.legsJoint);
        this.gnomadBody.addChild(this.neckJoint);
        this.armsJoint.addChild(this.gnomadLeftArm);
        this.gnomadNeck.addChild(this.headJoint);
        this.gnomadHead.addChild(this.gnomadHat);
        this.legsJoint.addChild(this.gnomadLeftLeg);
        this.neckJoint.addChild(this.gnomadNeck);
        this.gnomadHead.addChild(this.gnomadFace);
        this.gnomadBody.addChild(this.gnomadGoldSack);
        this.gnomadBody.addChild(this.gnomadTornBottom);
        this.gnomadFace.addChild(this.gnomadFaceTop);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.gnomadBody).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

	@Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	resetParts(this.gnomadBody, this.gnomadTornBottom, this.legsJoint, this.armsJoint, this.neckJoint, this.gnomadGoldSack, this.gnomadLeftLeg, this.gnomadRightLeg, this.gnomadLeftArm, this.gnomadRightArm, this.gnomadNeck, this.headJoint, this.gnomadHead, this.gnomadFace, this.gnomadHat, this.gnomadNose, this.gnomadFaceBottom, this.gnomadFaceTop);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount * 2F;
    	
    	float globalSpeed = 1.25F;
    	float globalHeight = 0.75F;
    	float globalDegree = 1.25F;
    	
    	float walkSpeed = 0.5F * globalSpeed;
    	
    	//IDLE
    	swing(this.gnomadBody, 0.125F * globalSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	swing(this.gnomadRightArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.07F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.gnomadLeftArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 1.5F, -0.07F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.gnomadRightArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	swing(this.gnomadLeftArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	bob(this.gnomadRightArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	bob(this.gnomadLeftArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	
    	bob(this.neckJoint, 0.125F * globalSpeed, 0.2f * globalHeight, false, ageInTicks, 0.5F, true);
    	bob(this.gnomadBody, 0.5F * (0.125F * globalSpeed), 0.2f * globalHeight, true, ageInTicks, 0.5F, true);
    	
    	rotVar(this.gnomadHat, entityIn, -0.01F, 0.01F, Axis.X);
    	rotVar(this.gnomadHat, entityIn, -0.01F, 0.01F, Axis.Y);
    	rotVar(this.gnomadHat, entityIn, -0.05F, 0.05F, Axis.Z);
    	
    	swing(this.gnomadHat, 0.125F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.X);
    	swing(this.gnomadHat, 0.125F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.Z);
    	
    	//WALK
    	swingLimbs(this.gnomadLeftLeg, this.gnomadRightLeg, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	swingLimbs(this.gnomadRightArm, this.gnomadLeftArm, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	
    	swing(this.gnomadRightArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, Axis.Z);
    	swing(this.gnomadLeftArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, -0.2F, f, f1, Axis.Z);
    	
    	swing(this.gnomadBody, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, Axis.X);
    	swing(this.gnomadBody, 0.5F * walkSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, f, f1, Axis.Y);
    	
    	bob(this.gnomadBody, 2.0F * walkSpeed, 2 * globalHeight, true, f, f1, true);
    	bob(this.gnomadHead, 2.0F * walkSpeed, 0.5F * globalHeight, true, f, f1, true);
    	
    	look(this.gnomadNeck, netHeadYaw, headPitch, 2.0F, 2.0F);
    	look(this.gnomadHead, netHeadYaw, headPitch, 2.0F, 2.0F);
    	
    	AbstractGnomadEntity.ArmPose AbstractGnomadEntity$armpose = entityIn.getArmPose();
        if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.ATTACKING) {
           ModelHelper.func_239103_a_(this.gnomadRightArm, this.gnomadLeftArm, entityIn, this.swingProgress, ageInTicks);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.SPELLCASTING || AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CELEBRATING) {
           this.gnomadRightArm.rotationPointZ =+ 0.0F;
           this.gnomadRightArm.rotationPointX =+ -5.0F;
           this.gnomadLeftArm.rotationPointZ =+ 0.0F;
           this.gnomadLeftArm.rotationPointX =+ 5.0F;
           this.gnomadRightArm.rotateAngleX =+ MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
           this.gnomadLeftArm.rotateAngleX =+ MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
           this.gnomadRightArm.rotateAngleZ =+ 2.3561945F;
           this.gnomadLeftArm.rotateAngleZ =+ -2.3561945F;
           this.gnomadRightArm.rotateAngleY =+ 0.0F;
           this.gnomadLeftArm.rotateAngleY =+ 0.0F;
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.BOW_AND_ARROW) {
           this.gnomadRightArm.rotateAngleY =+ -0.1F + this.gnomadHead.rotateAngleY;
           this.gnomadRightArm.rotateAngleX =+ (-(float)Math.PI / 2F) + this.gnomadHead.rotateAngleX;
           this.gnomadLeftArm.rotateAngleX =+ -0.9424779F + this.gnomadHead.rotateAngleX;
           this.gnomadLeftArm.rotateAngleY =+ this.gnomadHead.rotateAngleY - 0.4F;
           this.gnomadLeftArm.rotateAngleZ =+ ((float)Math.PI / 2F);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CROSSBOW_HOLD) {
           ModelHelper.func_239104_a_(this.gnomadRightArm, this.gnomadLeftArm, this.gnomadHead, true);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CROSSBOW_CHARGE) {
           ModelHelper.func_239102_a_(this.gnomadRightArm, this.gnomadLeftArm, entityIn, true);
        }
    	
    	this.gnomadBody.rotateAngleX += this.gnomadBody.defaultRotateAngleX;
    	this.gnomadNeck.rotateAngleX += this.gnomadNeck.defaultRotateAngleX;
    	
    	this.gnomadTornBottom.rotateAngleX = -this.gnomadBody.rotateAngleX;
    	this.armsJoint.rotateAngleX = -this.gnomadBody.rotateAngleX;
    	this.legsJoint.rotateAngleX = -this.gnomadBody.rotateAngleX;
    	this.neckJoint.rotateAngleX = -this.gnomadBody.rotateAngleX;
    	this.headJoint.rotateAngleX = -this.gnomadNeck.rotateAngleX;
    	
    	this.gnomadRightArmHolder.copyModelAngles(this.gnomadRightArm);
    	this.gnomadRightArmHolder.rotationPointX = this.gnomadRightArm.rotationPointX + this.armsJoint.rotationPointX + this.gnomadBody.rotationPointX;
    	this.gnomadRightArmHolder.rotationPointY = this.gnomadRightArm.rotationPointY + this.armsJoint.rotationPointY + this.gnomadBody.rotationPointY;
    	this.gnomadRightArmHolder.rotationPointZ = this.gnomadRightArm.rotationPointZ + this.armsJoint.rotationPointZ + this.gnomadBody.rotationPointZ + -1.4F;
    	
    	this.gnomadLeftArmHolder.copyModelAngles(this.gnomadLeftArm);
    	this.gnomadLeftArmHolder.rotationPointX = this.gnomadLeftArm.rotationPointX + this.armsJoint.rotationPointX + this.gnomadBody.rotationPointX;
    	this.gnomadLeftArmHolder.rotationPointY = this.gnomadLeftArm.rotationPointY + this.armsJoint.rotationPointY + this.gnomadBody.rotationPointY;
    	this.gnomadLeftArmHolder.rotationPointZ = this.gnomadLeftArm.rotationPointZ + this.armsJoint.rotationPointZ + this.gnomadBody.rotationPointZ + -1.4F;
    	
        if (entityIn.isSitting) {
            this.gnomadRightArm.rotateAngleX = (-(float)Math.PI / 5F);
            this.gnomadRightArm.rotateAngleY = 0.0F;
            this.gnomadRightArm.rotateAngleZ = 0.0F;
            this.gnomadLeftArm.rotateAngleX = (-(float)Math.PI / 5F);
            this.gnomadLeftArm.rotateAngleY = 0.0F;
            this.gnomadLeftArm.rotateAngleZ = 0.0F;
            this.gnomadRightLeg.rotateAngleX = -1.4137167F;
            this.gnomadRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.gnomadRightLeg.rotateAngleZ = 0.07853982F;
            this.gnomadLeftLeg.rotateAngleX = -1.4137167F;
            this.gnomadLeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
            this.gnomadLeftLeg.rotateAngleZ = -0.07853982F;
         }
    }
	
    @Override
	public ModelRenderer getModelHead() {
		return this.gnomadHead;
	}

	@Override
	public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
		this.getArmForSide(sideIn).translateRotate(matrixStackIn);
	}
	
	protected ModelRenderer getArmForSide(HandSide side)
	{
		return side == HandSide.LEFT ? this.gnomadLeftArmHolder : this.gnomadRightArmHolder;
	}
}
