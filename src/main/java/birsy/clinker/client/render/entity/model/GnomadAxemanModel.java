package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import birsy.clinker.client.render.util.BirsyBaseModel.Axis;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanModel<T extends GnomadAxemanEntity> extends BirsyBaseModel<T> implements ArmedModel, HeadedModel {
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

    public BirsyModelRenderer gnomadRightPauldron;
    public BirsyModelRenderer gnomadLeftPauldron;
    public BirsyModelRenderer gnomadHelmet;
    public BirsyModelRenderer gnomadRightPauldronRim;
    public BirsyModelRenderer gnomadRightPauldronBulb;
    public BirsyModelRenderer gnomadLeftPauldronRim;
    public BirsyModelRenderer gnomadLeftPauldronBulb;
    public BirsyModelRenderer gnomadVisor;
    
    public GnomadAxemanModel(float modelSize) {
        float scaleFactor = modelSize - 1F;
        
        this.texWidth = 64;
        this.texHeight = 64;

        /*
          Head!
         */
        this.gnomadNose = new BirsyModelRenderer(this, 50, 12);
        this.gnomadNose.setPos(0.0F, -1.5F, 0.0F);
        this.gnomadNose.addBox(-1.0F, -0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);
        this.setRotateAngle(gnomadNose, -0.35F, 0.0F, 0.0F);

        this.gnomadFaceTop = new BirsyModelRenderer(this, 33, 12);
        this.gnomadFaceTop.setPos(0.0F, -2.5F, 0.0F);
        this.gnomadFaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.gnomadFace = new BirsyModelRenderer(this, 32, 13);
        this.gnomadFace.setPos(0.0F, 0.0F, -7.0F);
        this.gnomadFace.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.gnomadFaceBottom = new BirsyModelRenderer(this, 33, 19);
        this.gnomadFaceBottom.setPos(0.0F, 3.5F, 0.0F);
        this.gnomadFaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.gnomadHat = new BirsyModelRenderer(this, 32, 22);
        this.gnomadHat.setPos(0.0F, -3.0F, -3.5F);
        this.gnomadHat.addBox(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);
        this.setRotateAngle(gnomadHat, 0.19547687289441354F, 0.0F, 0.19547687289441354F);

        this.gnomadHead = new BirsyModelRenderer(this, 28, 0);
        this.gnomadHead.setPos(0.0F, 0.0F, 1.0F);
        this.gnomadHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setPos(0.0F, 0.0F, -3.5F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.gnomadNeck = new BirsyModelRenderer(this, 46, 16);
        this.gnomadNeck.setPos(0.0F, 0.0F, -0.0F);
        this.gnomadNeck.addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);
        this.setRotateAngle(gnomadNeck, -0.3F, 0.0F, 0.0F);

        this.neckJoint = new BirsyModelRenderer(this, 0, 0);
        this.neckJoint.setPos(0.0F, -10.5F, -3.0F);
        this.neckJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);


        /*
         * Body!
         */
        this.gnomadBody = new BirsyModelRenderer(this, 0, 24);
        this.gnomadBody.setPos(0.0F, 13.0F, 0.0F);
        this.gnomadBody.addBox(-5.0F, -11.0F, -4.0F, 10.0F, 14.0F, 8.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);
        this.setRotateAngle(gnomadBody, 0.2F, 0.0F, 0.0F);

        this.gnomadGoldSack = new BirsyModelRenderer(this, 0, 0);
        this.gnomadGoldSack.setPos(0.0F, -4.0F, 4.0F);
        this.gnomadGoldSack.addBox(-4.5F, -6.0F, 0.0F, 9.0F, 12.0F, 5.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.gnomadTornBottom = new BirsyModelRenderer(this, 0, 46);
        this.gnomadTornBottom.setPos(0.0F, 3.0F, 4.0F);
        this.gnomadTornBottom.addBox(-5.0F, 0.0F, -8.0F, 10.0F, 10.0F, 8.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);


        /*
         * Arms!
         */
        this.armsJoint = new BirsyModelRenderer(this, 0, 0);
        this.armsJoint.setPos(0.0F, -8.0F, 0.0F);
        this.armsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.gnomadLeftArm = new BirsyModelRenderer(this, 48, 25);
        this.gnomadLeftArm.mirror = true;
        this.gnomadLeftArm.setPos(5.35F, 0.0F, 0.0F);
        this.gnomadLeftArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F + scaleFactor, -0.5F + scaleFactor, -0.25F + scaleFactor);

        this.gnomadLeftArmHolder = new BirsyModelRenderer(this, 40, 40);
        this.gnomadLeftArmHolder.setPos(5.35F, 5.0F, 0.0F);
        this.gnomadLeftArmHolder.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F + scaleFactor, -0.5F + scaleFactor, -0.25F + scaleFactor);

        this.gnomadRightArm = new BirsyModelRenderer(this, 48, 25);
        this.gnomadRightArm.setPos(-5.35F, 0.0F, 0.0F);
        this.gnomadRightArm.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);

        this.gnomadRightArmHolder = new BirsyModelRenderer(this, 40, 40);
        this.gnomadRightArmHolder.setPos(-5.35F, 5.0F, 0.0F);
        this.gnomadRightArmHolder.addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F + scaleFactor, -0.5F + scaleFactor, -0.25F + scaleFactor);


        /*
         * Legs!
         */
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setPos(0.0F, 3.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F + scaleFactor, 0.0F + scaleFactor, 0.0F + scaleFactor);

        this.gnomadLeftLeg = new BirsyModelRenderer(this, 56, 25);
        this.gnomadLeftLeg.mirror = true;
        this.gnomadLeftLeg.setPos(3.4F, 0.0F, 0.0F);
        this.gnomadLeftLeg.addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F + scaleFactor, -0.5F + scaleFactor, -0.25F + scaleFactor);

        this.gnomadRightLeg = new BirsyModelRenderer(this, 56, 25);
        this.gnomadRightLeg.setPos(-3.5F, 0.0F, 0.0F);
        this.gnomadRightLeg.addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F, -0.5F, -0.25F);

        /*
          Armor!
         */
        this.texWidth = 64;
        this.texHeight = 64;

        this.gnomadHelmet = new BirsyModelRenderer(this, 36, 39);
        this.gnomadHelmet.setPos(0.0F, 1.5F, -1.5F);
        this.gnomadHelmet.addBox(-4.0F, -5.0F, -6.0F, 8.0F, 5.0F, 6.0F, 0.25F, 0.25F, 0.25F);

        this.gnomadVisor = new BirsyModelRenderer(this, 36, 50);
        this.gnomadVisor.setPos(0.0F, -1.0F, -4.0F);
        this.gnomadVisor.addBox(-4.5F, -1.5F, -3.5F, 9.0F, 5.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadVisor.xRot = 0.4F;


        this.gnomadRightPauldron = new BirsyModelRenderer(this, 10, 17);
        this.gnomadRightPauldron.setPos(0.5F, 0.0F, 0.0F);
        this.gnomadRightPauldron.addBox(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadRightPauldron.yRot = (float) (Math.PI * 0.5F);

        this.gnomadRightPauldronRim = new BirsyModelRenderer(this, 46, 0);
        this.gnomadRightPauldronRim.setPos(0.0F, 2.5F, -1.0F);
        this.gnomadRightPauldronRim.addBox(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);

        this.gnomadRightPauldronBulb = new BirsyModelRenderer(this, 0, 17);
        this.gnomadRightPauldronBulb.setPos(0.0F, 0.0F, 0.0F);
        this.gnomadRightPauldronBulb.addBox(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, 0.25F, 0.25F, 0.25F);


        this.gnomadLeftPauldron = new BirsyModelRenderer(this, 10, 17);
        this.gnomadLeftPauldron.setPos(-0.5F, 0.0F, 0.0F);
        this.gnomadLeftPauldron.addBox(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadLeftPauldron.yRot = (float) (Math.PI * -0.5F);

        this.gnomadLeftPauldronRim = new BirsyModelRenderer(this, 46, 0);
        this.gnomadLeftPauldronRim.setPos(0.0F, 2.5F, -1.0F);
        this.gnomadLeftPauldronRim.addBox(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);

        this.gnomadLeftPauldronBulb = new BirsyModelRenderer(this, 0, 17);
        this.gnomadLeftPauldronBulb.setPos(0.0F, 0.0F, 0.0F);
        this.gnomadLeftPauldronBulb.addBox(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, 0.25F, 0.25F, 0.25F);

        /*
         * Parenting!
         */
        this.neckJoint.addChild(this.gnomadNeck);
        this.gnomadNeck.addChild(this.headJoint);
        this.headJoint.addChild(this.gnomadHead);

        this.gnomadHead.addChild(this.gnomadHat);
        this.gnomadHead.addChild(this.gnomadFace);

        this.gnomadFace.addChild(this.gnomadNose);
        this.gnomadFace.addChild(this.gnomadFaceTop);
        this.gnomadFace.addChild(this.gnomadFaceBottom);

        this.gnomadBody.addChild(this.neckJoint);
        this.gnomadBody.addChild(this.armsJoint);
        this.gnomadBody.addChild(this.legsJoint);
        this.gnomadBody.addChild(this.gnomadGoldSack);
        this.gnomadBody.addChild(this.gnomadTornBottom);

        this.legsJoint.addChild(this.gnomadLeftLeg);
        this.legsJoint.addChild(this.gnomadRightLeg);

        this.armsJoint.addChild(this.gnomadLeftArm);
        this.armsJoint.addChild(this.gnomadRightArm);

        this.gnomadRightPauldron.addChild(this.gnomadRightPauldronRim);
        this.gnomadLeftPauldronRim.addChild(this.gnomadLeftPauldronBulb);
        this.gnomadRightPauldronRim.addChild(this.gnomadRightPauldronBulb);
        this.gnomadLeftPauldron.addChild(this.gnomadLeftPauldronRim);
        this.gnomadHelmet.addChild(this.gnomadVisor);

        this.gnomadHead.addChild(this.gnomadHelmet);
        this.gnomadLeftArm.addChild(this.gnomadLeftPauldron);
        this.gnomadRightArm.addChild(this.gnomadRightPauldron);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ImmutableList.of(this.gnomadBody).forEach((modelRenderer) -> {
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public void setArmorVisibility(GnomadAxemanEntity entitylivingbaseIn) {
        this.gnomadHelmet.visible = entitylivingbaseIn.isWearingHelmet();
        this.gnomadLeftPauldron.visible = entitylivingbaseIn.isWearingLeftPauldron();
        this.gnomadRightPauldron.visible = entitylivingbaseIn.isWearingRightPauldron();
    }

	@Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
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
    	look(this.gnomadHead, netHeadYaw, headPitch, 1.0F, 1.0F);


        float rollAmount = (float) Math.toRadians(entityIn.getDodgeTime() * 1.5);
        this.gnomadBody.zRot =+ (entityIn.getDodgeDirection() ? rollAmount : -rollAmount);

        if (entityIn.isAggressive()) {
            AnimationUtils.swingWeaponDown(this.gnomadRightArm, this.gnomadLeftArm, entityIn, this.attackTime, ageInTicks);
        }

        applyJointRotation();
    }

    private void applyJointRotation() {
        this.gnomadBody.xRot += this.gnomadBody.defaultRotateAngleX;
        this.gnomadNeck.xRot += this.gnomadNeck.defaultRotateAngleX;

        this.gnomadTornBottom.xRot = -this.gnomadBody.xRot;
        this.armsJoint.xRot = -this.gnomadBody.xRot;
        this.legsJoint.xRot = -this.gnomadBody.xRot;
        this.neckJoint.xRot = -this.gnomadBody.xRot;
        this.headJoint.xRot = -this.gnomadNeck.xRot;

        this.gnomadRightArmHolder.copyModelAngles(this.gnomadRightArm);
        this.gnomadRightArmHolder.x = this.gnomadRightArm.x + this.armsJoint.x + this.gnomadBody.x;
        this.gnomadRightArmHolder.y = this.gnomadRightArm.y + this.armsJoint.y + this.gnomadBody.y;
        this.gnomadRightArmHolder.z = this.gnomadRightArm.z + this.armsJoint.z + this.gnomadBody.z + -1.4F;

        this.gnomadLeftArmHolder.copyModelAngles(this.gnomadLeftArm);
        this.gnomadLeftArmHolder.x = this.gnomadLeftArm.x + this.armsJoint.x + this.gnomadBody.x;
        this.gnomadLeftArmHolder.y = this.gnomadLeftArm.y + this.armsJoint.y + this.gnomadBody.y;
        this.gnomadLeftArmHolder.z = this.gnomadLeftArm.z + this.armsJoint.z + this.gnomadBody.z + -1.4F;
    }

    @Override
	public ModelPart getHead() {
		return this.gnomadHead;
	}

	@Override
	public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
        matrixStackIn.translate(sideIn == HumanoidArm.RIGHT ? 0.075 : -0.075, 0.075, -0.1);
		this.getArmForSide(sideIn).matrixStackFromModel(matrixStackIn);
	}
	
	protected BirsyModelRenderer getArmForSide(HumanoidArm side)
	{
		return side == HumanoidArm.LEFT ? this.gnomadLeftArm : this.gnomadRightArm;
	}
}
