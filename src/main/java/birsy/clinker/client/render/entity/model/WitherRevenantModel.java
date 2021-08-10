package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.WitherRevenantEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import birsy.clinker.client.render.util.BirsyBaseModel.Axis;

/**
 * WitherRevenantModel - doclg
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class WitherRevenantModel<T extends WitherRevenantEntity> extends BirsyBaseModel<T> implements ArmedModel, HeadedModel {
    public BirsyModelRenderer movementBase;
    public BirsyModelRenderer witherRevenantRightLeg;
    public BirsyModelRenderer witherRevenantLeftLeg;
    public BirsyModelRenderer witherRevenantRibs;
    public BirsyModelRenderer witherRevenantRightFoot;
    public BirsyModelRenderer witherRevenantRightBoot;
    public BirsyModelRenderer witherRevenantLeftFoot;
    public BirsyModelRenderer witherRevenantLeftBoot;
    public BirsyModelRenderer witherRevenantTorso;
    public BirsyModelRenderer witherRevenantLeftArm;
    public BirsyModelRenderer witherRevenantRightArm;
    public BirsyModelRenderer neckJoint;
    public BirsyModelRenderer witherRevenantChestplate;
    public BirsyModelRenderer witherRevenantLeftPad;
    public BirsyModelRenderer witherRevenantLeftHand;
    public BirsyModelRenderer witherRevenantRightPad;
    public BirsyModelRenderer witherRevenantRightHand;
    public BirsyModelRenderer witherRevenantHead;
    public BirsyModelRenderer witherRevenantRidges;

    public WitherRevenantModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        
        this.witherRevenantLeftFoot = new BirsyModelRenderer(this, 32, 33);
        this.witherRevenantLeftFoot.mirror = true;
        this.witherRevenantLeftFoot.setPos(0.0F, 5.6F, 0.0F);
        this.witherRevenantLeftFoot.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.25F, 0.5F, 0.25F);
        this.witherRevenantChestplate = new BirsyModelRenderer(this, 0, 41);
        this.witherRevenantChestplate.setPos(0.0F, -12.0F, 0.0F);
        this.witherRevenantChestplate.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 9.0F, 4.0F, 0.5F, 0.25F, 0.25F);
        this.witherRevenantRightBoot = new BirsyModelRenderer(this, 12, 32);
        this.witherRevenantRightBoot.setPos(0.0F, 6.2F, 0.0F);
        this.witherRevenantRightBoot.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantRightPad = new BirsyModelRenderer(this, 0, 30);
        this.witherRevenantRightPad.setPos(0.5F, -1.0F, 0.0F);
        this.witherRevenantRightPad.addBox(-3.0F, -1.5F, -1.5F, 3.0F, 7.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantRightArm = new BirsyModelRenderer(this, 8, 16);
        this.witherRevenantRightArm.setPos(-5.0F, -10.0F, 0.0F);
        this.witherRevenantRightArm.addBox(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.movementBase = new BirsyModelRenderer(this, 0, 0);
        this.movementBase.setPos(0.0F, 24.0F, 0.0F);
        this.movementBase.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantRightFoot = new BirsyModelRenderer(this, 32, 33);
        this.witherRevenantRightFoot.setPos(0.0F, 5.6F, 0.0F);
        this.witherRevenantRightFoot.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.25F, 0.5F, 0.25F);
        this.witherRevenantLeftBoot = new BirsyModelRenderer(this, 12, 32);
        this.witherRevenantLeftBoot.mirror = true;
        this.witherRevenantLeftBoot.setPos(0.0F, 6.2F, 0.0F);
        this.witherRevenantLeftBoot.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantTorso = new BirsyModelRenderer(this, 40, 16);
        this.witherRevenantTorso.setPos(0.0F, -12.0F, 0.0F);
        this.witherRevenantTorso.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantRidges = new BirsyModelRenderer(this, 32, 0);
        this.witherRevenantRidges.setPos(0.0F, 0.0F, 0.0F);
        this.witherRevenantRidges.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, -0.25F, 0.0F);
        this.witherRevenantRightHand = new BirsyModelRenderer(this, 24, 33);
        this.witherRevenantRightHand.setPos(-1.0F, 5.0F, 0.0F);
        this.witherRevenantRightHand.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.25F, 0.5F, 0.25F);
        this.witherRevenantHead = new BirsyModelRenderer(this, 0, 0);
        this.witherRevenantHead.setPos(0.0F, 0.0F, 0.0F);
        this.witherRevenantHead.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, -0.25F, -0.25F, -0.25F);
        this.witherRevenantRibs = new BirsyModelRenderer(this, 16, 16);
        this.witherRevenantRibs.setPos(0.0F, -12.0F, 0.0F);
        this.witherRevenantRibs.addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, -0.25F, -0.05F, -0.25F);
        this.witherRevenantLeftArm = new BirsyModelRenderer(this, 8, 16);
        this.witherRevenantLeftArm.mirror = true;
        this.witherRevenantLeftArm.setPos(5.0F, -10.0F, 0.0F);
        this.witherRevenantLeftArm.addBox(-0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.neckJoint = new BirsyModelRenderer(this, 0, 0);
        this.neckJoint.setPos(0.0F, -12.0F, 0.0F);
        this.neckJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantLeftPad = new BirsyModelRenderer(this, 0, 30);
        this.witherRevenantLeftPad.mirror = true;
        this.witherRevenantLeftPad.setPos(-0.5F, -1.0F, 0.0F);
        this.witherRevenantLeftPad.addBox(0.0F, -1.5F, -1.5F, 3.0F, 7.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantRightLeg = new BirsyModelRenderer(this, 0, 16);
        this.witherRevenantRightLeg.setPos(-2.5F, -12.0F, 0.0F);
        this.witherRevenantRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantLeftLeg = new BirsyModelRenderer(this, 0, 16);
        this.witherRevenantLeftLeg.mirror = true;
        this.witherRevenantLeftLeg.setPos(2.5F, -12.0F, 0.0F);
        this.witherRevenantLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.witherRevenantLeftHand = new BirsyModelRenderer(this, 24, 33);
        this.witherRevenantLeftHand.mirror = true;
        this.witherRevenantLeftHand.setPos(1.0F, 5.0F, 0.0F);
        this.witherRevenantLeftHand.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.25F, 0.5F, 0.25F);
        this.witherRevenantLeftLeg.addChild(this.witherRevenantLeftFoot);
        this.witherRevenantRibs.addChild(this.witherRevenantChestplate);
        this.witherRevenantRightLeg.addChild(this.witherRevenantRightBoot);
        this.witherRevenantRightArm.addChild(this.witherRevenantRightPad);
        this.witherRevenantRibs.addChild(this.witherRevenantRightArm);
        this.witherRevenantRightLeg.addChild(this.witherRevenantRightFoot);
        this.witherRevenantLeftLeg.addChild(this.witherRevenantLeftBoot);
        this.witherRevenantRibs.addChild(this.witherRevenantTorso);
        this.witherRevenantHead.addChild(this.witherRevenantRidges);
        this.witherRevenantRightArm.addChild(this.witherRevenantRightHand);
        this.neckJoint.addChild(this.witherRevenantHead);
        this.movementBase.addChild(this.witherRevenantRibs);
        this.witherRevenantRibs.addChild(this.witherRevenantLeftArm);
        this.witherRevenantRibs.addChild(this.neckJoint);
        this.witherRevenantLeftArm.addChild(this.witherRevenantLeftPad);
        this.movementBase.addChild(this.witherRevenantRightLeg);
        this.movementBase.addChild(this.witherRevenantLeftLeg);
        this.witherRevenantLeftArm.addChild(this.witherRevenantLeftHand);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.movementBase).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

	@Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    	resetParts(this.movementBase, this.witherRevenantRightLeg, this.witherRevenantLeftLeg, this.witherRevenantRibs, this.witherRevenantRightFoot, this.witherRevenantRightBoot, this.witherRevenantLeftFoot, this.witherRevenantLeftBoot, this.witherRevenantTorso, this.witherRevenantLeftArm, this.witherRevenantRightArm, this.neckJoint, this.witherRevenantChestplate, this.witherRevenantLeftPad, this.witherRevenantLeftHand, this.witherRevenantRightPad, this.witherRevenantRightHand, this.witherRevenantHead, this.witherRevenantRidges);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount * 1.5F;
    	
    	float globalSpeed = 1.0F;
    	float globalHeight = 1.25F;
    	float globalDegree = 1.25F;
    	
    	//IDLE
    	swing(this.witherRevenantRibs, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.0F, 0.1F, ageInTicks, 0.5F, Axis.X);
    	
    	swing(this.witherRevenantRightArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.07F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.witherRevenantLeftArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 1.5F, -0.07F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.witherRevenantRightArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	swing(this.witherRevenantLeftArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	bob(this.witherRevenantRightArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	bob(this.witherRevenantLeftArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
    	
    	bob(this.neckJoint, 0.125F * globalSpeed, 0.2f * globalHeight, false, ageInTicks, 0.5F, true);
    	bob(this.witherRevenantRibs, 0.5F * (0.125F * globalSpeed), 0.2f * globalHeight, true, ageInTicks, 0.5F, true);
    	
    	this.witherRevenantRightArm.xRot =- this.witherRevenantRibs.xRot;
    	this.witherRevenantLeftArm.xRot =- this.witherRevenantRibs.xRot;
    	
    	//WALK
    	float walkSpeed = 0.5F * globalSpeed;
    	
    	swingLimbs(this.witherRevenantLeftLeg, this.witherRevenantRightLeg, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	swingLimbs(this.witherRevenantRightArm, this.witherRevenantLeftArm, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
    	
    	swing(this.witherRevenantRightArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, Axis.Z);
    	swing(this.witherRevenantLeftArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, -0.2F, f, f1, Axis.Z);
    	
    	swing(this.witherRevenantRibs, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, Axis.X);
    	swing(this.witherRevenantRibs, 0.5F * walkSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, f, f1, Axis.Y);
    	
    	this.neckJoint.xRot =- 0.8F * this.witherRevenantRibs.xRot;
    	this.neckJoint.yRot =- 0.8F * this.witherRevenantRibs.yRot;
    	
    	bob(this.movementBase, 2.0F * walkSpeed, 2 * globalHeight, true, f, f1, true);
    	bob(this.witherRevenantHead, 2.0F * walkSpeed, 0.5F * globalHeight, true, f, f1, true);
    	
    	look(this.witherRevenantHead, netHeadYaw, headPitch, 1.1F, 1.1F);
    	look(this.witherRevenantRibs, netHeadYaw, headPitch, 6.0F, 6.0F);
    }
    
    @Override
	public ModelPart getHead() {
		return this.witherRevenantHead;
	}

	@Override
	public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
		this.getArmForSide(sideIn).matrixStackFromModel(matrixStackIn);
	}
	
	protected BirsyModelRenderer getArmForSide(HumanoidArm side) {
		return side == HumanoidArm.LEFT ? this.witherRevenantLeftArm : this.witherRevenantRightArm;
	}
}
