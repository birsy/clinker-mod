package birsy.clinker.client.render.entity.model.gnomad;

import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.client.render.entity.model.base.DynamicModel;
import birsy.clinker.client.render.entity.model.base.DynamicModelPart;
import birsy.clinker.common.world.entity.gnomad.GnomadArmor;
import birsy.clinker.common.world.entity.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadAxemanDynamicModel<T extends GnomadAxemanEntity> extends EntityModel<T> implements ArmedModel {
	public float sitTransition;
	public float headShakingIntensity;
	public DynamicModel skeleton;

	public DynamicModelPart rootJoint;
	public DynamicModelPart gnomadBody;
	public DynamicModelPart neckJoint;
	public DynamicModelPart armsJoint;
	public DynamicModelPart legsJoint;
	public DynamicModelPart gnomadGoldSack;
	public DynamicModelPart gnomadTornBottom;
	public DynamicModelPart gnomadNeck;
	public DynamicModelPart headJoint;
	public DynamicModelPart gnomadHead;
	public DynamicModelPart gnomadHat;
	public DynamicModelPart gnomadFace;
	public DynamicModelPart gnomadHelmet;
	public DynamicModelPart gnomadNose;
	public DynamicModelPart gnomadFaceTop;
	public DynamicModelPart gnomadFaceBottom;
	public DynamicModelPart gnomadVisor;
	public DynamicModelPart gnomadLeftArm;
	public DynamicModelPart gnomadRightArm;
	public DynamicModelPart gnomadLeftPauldron;
	public DynamicModelPart gnomadLeftPauldronRim;
	public DynamicModelPart gnomadLeftPauldronBulb;
	public DynamicModelPart gnomadRightPauldron;
	public DynamicModelPart gnomadRightPauldronRim;
	public DynamicModelPart gnomadRightPauldronBulb;
	public DynamicModelPart gnomadLeftLeg;
	public DynamicModelPart gnomadRightLeg;

	public GnomadAxemanDynamicModel() {
		this.skeleton = new DynamicModel(64, 64);
		
		this.gnomadFaceTop = new DynamicModelPart(this.skeleton, 33, 12);
		this.gnomadFaceTop.setInitialPosition(0.0F, -2.5F, 0.0F);
		this.gnomadFaceTop.addCube(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);

		this.gnomadLeftPauldronBulb = new DynamicModelPart(this.skeleton, 0, 17);
		this.gnomadLeftPauldronBulb.setInitialPosition(0.0F, 0.0F, 0.0F);
		this.gnomadLeftPauldronBulb.addCube(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, 0.25F, 0.25F, 0.25F);

		this.headJoint = new DynamicModelPart(this.skeleton, 0, 0);
		this.headJoint.setInitialPosition(0.0F, 0.0F, -3.5F);
		this.headJoint.addCube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		this.headJoint.setInitialRotation(0.3000000035018414F, 0.0F, 0.0F);

		this.gnomadLeftArm = new DynamicModelPart(this.skeleton, 48, 25);
		this.gnomadLeftArm.mirror = true;
		this.gnomadLeftArm.setInitialPosition(5.35F, -0.125F, 0.0F);
		this.gnomadLeftArm.addCube(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);

		this.gnomadRightLeg = new DynamicModelPart(this.skeleton, 56, 25);
		this.gnomadRightLeg.setInitialPosition(-3.5F, 0.0F, 0.0F);
		this.gnomadRightLeg.addCube(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F, -0.5F, -0.25F);

		this.gnomadLeftPauldronRim = new DynamicModelPart(this.skeleton, 46, 0);
		this.gnomadLeftPauldronRim.setInitialPosition(0.0F, 2.5F, -1.0F);
		this.gnomadLeftPauldronRim.addCube(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);

		this.gnomadGoldSack = new DynamicModelPart(this.skeleton, 0, 0);
		this.gnomadGoldSack.setInitialPosition(0.0F, -4.0F, 4.0F);
		this.gnomadGoldSack.addCube(-4.5F, -6.0F, 0.0F, 9.0F, 12.0F, 5.0F, 0.0F, 0.0F, 0.0F);

		this.legsJoint = new DynamicModelPart(this.skeleton, 0, 0);
		this.legsJoint.setInitialPosition(0.0F, 3.0F, 0.0F);
		this.legsJoint.addCube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		this.legsJoint.setInitialRotation( -0.20000000233456092F, 0.0F, 0.0F);

		this.gnomadHead = new DynamicModelPart(this.skeleton, 28, 0);
		this.gnomadHead.setInitialPosition(0.0F, 0.0F, 1.0F);
		this.gnomadHead.addCube(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);

		this.gnomadLeftLeg = new DynamicModelPart(this.skeleton, 56, 25);
		this.gnomadLeftLeg.mirror = true;
		this.gnomadLeftLeg.setInitialPosition(3.4F, 0.0F, 0.0F);
		this.gnomadLeftLeg.addCube(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, -0.25F, -0.5F, -0.25F);

		this.rootJoint = new DynamicModelPart(this.skeleton, 0, 0);
		this.rootJoint.setInitialPosition(0.0F, 24.0F, 0.0F);
		this.rootJoint.addCube(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);

		this.gnomadBody = new DynamicModelPart(this.skeleton, 0, 24);
		this.gnomadBody.setInitialPosition(0.0F, -11.25F, 0.0F);
		this.gnomadBody.addCube(-5.0F, -11.0F, -4.0F, 10.0F, 14.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadBody.setInitialRotation( 0.20001473294434044F, 0.0F, -0.0F);
		this.gnomadVisor = new DynamicModelPart(this.skeleton, 36, 50);
		this.gnomadVisor.setInitialPosition(0.0F, -1.0F, -4.0F);
		this.gnomadVisor.addCube(-4.5F, -1.5F, -3.5F, 9.0F, 5.0F, 5.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadVisor.setInitialRotation( 0.40000000466912183F, 0.0F, 0.0F);
		this.gnomadFace = new DynamicModelPart(this.skeleton, 32, 13);
		this.gnomadFace.setInitialPosition(0.0F, 0.0F, -7.0F);
		this.gnomadFace.addCube(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadNose = new DynamicModelPart(this.skeleton, 50, 12);
		this.gnomadNose.setInitialPosition(0.0F, -1.5F, 0.0F);
		this.gnomadNose.addCube(-1.0F, -0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadNose.setInitialRotation( -0.34999998744072486F, 0.0F, 0.0F);
		this.gnomadLeftPauldron = new DynamicModelPart(this.skeleton, 10, 17);
		this.gnomadLeftPauldron.setInitialPosition(-0.5F, 0.0F, 0.0F);
		this.gnomadLeftPauldron.addCube(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadLeftPauldron.setInitialRotation( 0.0F, -1.5707963267948966F, 0.0F);
		this.gnomadRightPauldron = new DynamicModelPart(this.skeleton, 10, 17);
		this.gnomadRightPauldron.setInitialPosition(0.5F, 0.0F, 0.0F);
		this.gnomadRightPauldron.addCube(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadRightPauldron.setInitialRotation( 0.0F, 1.5707963267948966F, 0.0F);
		this.neckJoint = new DynamicModelPart(this.skeleton, 0, 0);
		this.neckJoint.setInitialPosition(0.0F, -10.575F, -3.0F);
		this.neckJoint.addCube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		this.neckJoint.setInitialRotation( -0.20000000233456092F, 0.0F, 0.0F);
		this.gnomadNeck = new DynamicModelPart(this.skeleton, 46, 16);
		this.gnomadNeck.setInitialPosition(0.0F, 0.0F, -0.0F);
		this.gnomadNeck.addCube(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadNeck.setInitialRotation( -0.3000000035018414F, 0.0F, 0.0F);
		this.gnomadRightArm = new DynamicModelPart(this.skeleton, 48, 25);
		this.gnomadRightArm.setInitialPosition(-5.35F, -0.125F, 0.0F);
		this.gnomadRightArm.addCube(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, -0.25F, -0.5F, -0.25F);
		this.armsJoint = new DynamicModelPart(this.skeleton, 0, 0);
		this.armsJoint.setInitialPosition(0.0F, -8.0F, 0.0F);
		this.armsJoint.addCube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		this.armsJoint.setInitialRotation( -0.20000000233456092F, 0.0F, 0.0F);
		this.gnomadHelmet = new DynamicModelPart(this.skeleton, 36, 39);
		this.gnomadHelmet.setInitialPosition(0.0F, 1.5F, -1.5F);
		this.gnomadHelmet.addCube(-4.0F, -5.0F, -6.0F, 8.0F, 5.0F, 6.0F, 0.25F, 0.25F, 0.25F);
		this.gnomadFaceBottom = new DynamicModelPart(this.skeleton, 33, 19);
		this.gnomadFaceBottom.setInitialPosition(0.0F, 3.5F, 0.0F);
		this.gnomadFaceBottom.addCube(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadHat = new DynamicModelPart(this.skeleton, 32, 22);
		this.gnomadHat.setInitialPosition(0.0F, -3.0F, -3.5F);
		this.gnomadHat.addCube(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadHat.setInitialRotation( 0.01688540150551102F, 0.0F, 0.01688540150551102F);
		this.gnomadTornBottom = new DynamicModelPart(this.skeleton, 0, 46);
		this.gnomadTornBottom.setInitialPosition(0.0F, 3.0F, 4.0F);
		this.gnomadTornBottom.addCube(-5.0F, 0.0F, -8.0F, 10.0F, 10.0F, 8.0F, -0.05F, 0.0F, 0.0F);
		this.gnomadTornBottom.setInitialRotation( -0.20000000233456092F, 0.0F, 0.0F);
		this.gnomadRightPauldronRim = new DynamicModelPart(this.skeleton, 46, 0);
		this.gnomadRightPauldronRim.setInitialPosition(0.0F, 2.5F, -1.0F);
		this.gnomadRightPauldronRim.addCube(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
		this.gnomadRightPauldronBulb = new DynamicModelPart(this.skeleton, 0, 17);
		this.gnomadRightPauldronBulb.setInitialPosition(0.0F, 0.0F, 0.0F);
		this.gnomadRightPauldronBulb.addCube(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, 0.25F, 0.25F, 0.25F);
		this.gnomadFace.addChild(this.gnomadFaceTop);
		this.gnomadLeftPauldronRim.addChild(this.gnomadLeftPauldronBulb);
		this.gnomadNeck.addChild(this.headJoint);
		this.armsJoint.addChild(this.gnomadLeftArm);
		this.legsJoint.addChild(this.gnomadRightLeg);
		this.gnomadLeftPauldron.addChild(this.gnomadLeftPauldronRim);
		this.gnomadBody.addChild(this.gnomadGoldSack);
		this.gnomadBody.addChild(this.legsJoint);
		this.headJoint.addChild(this.gnomadHead);
		this.legsJoint.addChild(this.gnomadLeftLeg);
		this.rootJoint.addChild(this.gnomadBody);
		this.gnomadHelmet.addChild(this.gnomadVisor);
		this.gnomadHead.addChild(this.gnomadFace);
		this.gnomadFace.addChild(this.gnomadNose);
		this.gnomadLeftArm.addChild(this.gnomadLeftPauldron);
		this.gnomadRightArm.addChild(this.gnomadRightPauldron);
		this.gnomadBody.addChild(this.neckJoint);
		this.neckJoint.addChild(this.gnomadNeck);
		this.armsJoint.addChild(this.gnomadRightArm);
		this.gnomadBody.addChild(this.armsJoint);
		this.gnomadHead.addChild(this.gnomadHelmet);
		this.gnomadFace.addChild(this.gnomadFaceBottom);
		this.gnomadHead.addChild(this.gnomadHat);
		this.gnomadBody.addChild(this.gnomadTornBottom);
		this.gnomadRightPauldron.addChild(this.gnomadRightPauldronRim);
		this.gnomadRightPauldronRim.addChild(this.gnomadRightPauldronBulb);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.rootJoint.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.skeleton.resetPose();
		AnimFunctions.setScale(this.headJoint, 0.8F);
		this.skeleton.setDynamics(0.4F, 1.2F, 3.0F);
		float f = limbSwing;
		float f1 = limbSwingAmount * 2F;

		float globalSpeed = 1.25F;
		float globalHeight = 1.2F;
		float globalDegree = 1.25F;

		float walkSpeed = 1.1F * globalSpeed;

		//IDLE
		AnimFunctions.swing(this.rootJoint, 0.025F * globalSpeed, 0.05f * globalDegree, false, 0.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);

		AnimFunctions.swing(this.gnomadRightArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.07F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
		AnimFunctions.swing(this.gnomadLeftArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 1.5F, -0.07F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
		AnimFunctions.swing(this.gnomadRightArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.gnomadLeftArm, 0.12F * globalSpeed, 0.03f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);

		AnimFunctions.bob(this.gnomadRightArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F);
		AnimFunctions.bob(this.gnomadLeftArm, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F);

		AnimFunctions.bob(this.gnomadNeck, 0.125F * globalSpeed, 0.2f * globalHeight, false, ageInTicks, 0.5F);
		AnimFunctions.bob(this.gnomadBody, 0.15F * (0.125F * globalSpeed), 0.2f * globalHeight, true, ageInTicks, 0.5F);

		AnimFunctions.swing(this.gnomadHat, 0.125F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.gnomadHat, 0.125F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.Z);

		AnimFunctions.swing(this.gnomadGoldSack, 0.025F * walkSpeed, 0.02F * globalDegree, false, 0.0F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.gnomadGoldSack, 0.025F * walkSpeed, 0.02F * globalDegree, false, 0.0F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.Z);

		float easedHeadShakeIntensity = this.headShakingIntensity * this.headShakingIntensity;
		float headShakeSpeed = 0.7F * globalSpeed;
		this.gnomadHead.zRot += Mth.sin(ageInTicks * headShakeSpeed) * easedHeadShakeIntensity * 0.1F;
		this.gnomadFace.zRot += Mth.sin(0.5F + ageInTicks * headShakeSpeed) * easedHeadShakeIntensity  * 0.5F;
		this.gnomadHelmet.zRot += Mth.sin(0.5F + ageInTicks * headShakeSpeed) * easedHeadShakeIntensity  * 0.5F;

		if (entity.getArmor(GnomadArmor.GnomadArmorLocation.HELMET) == GnomadArmor.HELMET_CRUSADER) {
			this.gnomadHead.setScale(0.85F, 0.85F, 0.85F);
			this.gnomadFace.zRot = 0.0F;
		}

		//SITTING
		float sitLegXRotation = 1.0F * globalDegree;
		float sitLegYRotation = 0.2F * globalDegree;

		float sitArmXRotation = sitLegXRotation * 0.5F;
		float sitArmYRotation = sitLegYRotation * 0.5F;

		float sitClothRotation = 0.3F * sitLegXRotation;
		float sitBodyHeight = 8.0F * globalHeight;
		float sitBodyRotation = 0.2F * globalDegree;

		if (this.riding || this.sitTransition >= 1) {
			this.gnomadBody.y += sitBodyHeight;
			this.gnomadBody.xRot += sitBodyRotation;

			this.gnomadLeftArm.xRot -= sitArmXRotation;
			this.gnomadRightArm.xRot -= sitArmXRotation;
			this.gnomadLeftArm.yRot += sitArmYRotation;
			this.gnomadRightArm.yRot -= sitArmYRotation;

			this.gnomadLeftLeg.xRot -= sitLegXRotation;
			this.gnomadRightLeg.xRot -= sitLegXRotation;
			this.gnomadLeftLeg.yRot += sitLegYRotation;
			this.gnomadRightLeg.yRot -= sitLegYRotation;

			this.gnomadTornBottom.xRot -= sitClothRotation;
		} else {
			this.gnomadBody.y += Mth.lerp(this.sitTransition, 0, sitBodyHeight);

			this.gnomadLeftArm.xRot -= Mth.lerp(this.sitTransition, 0, sitArmXRotation);
			this.gnomadRightArm.xRot -= Mth.lerp(this.sitTransition, 0, sitArmXRotation);
			this.gnomadLeftArm.yRot += Mth.lerp(this.sitTransition, 0, sitArmYRotation);
			this.gnomadRightArm.yRot -= Mth.lerp(this.sitTransition, 0, sitArmYRotation);

			this.sitTransition = Mth.clamp(this.sitTransition, 0, 1);
			this.gnomadBody.xRot += Mth.lerp(this.sitTransition, 0, sitBodyRotation);

			this.gnomadLeftLeg.xRot -= Mth.lerp(this.sitTransition, 0, sitLegXRotation);
			this.gnomadRightLeg.xRot -= Mth.lerp(this.sitTransition, 0, sitLegXRotation);
			this.gnomadLeftLeg.yRot += Mth.lerp(this.sitTransition, 0, sitLegYRotation);
			this.gnomadRightLeg.yRot -= Mth.lerp(this.sitTransition, 0, sitLegYRotation);

			this.gnomadTornBottom.xRot -= Mth.lerp(this.sitTransition, 0, sitClothRotation);
		}

		//CROUCHING
		float crouch = 0.1F;
		float crouchBodyRotation = 1.17F;
		this.gnomadBody.xRot += Mth.lerp(crouch, -0.2F, crouchBodyRotation);
		this.gnomadTornBottom.xRot += Mth.lerp(crouch, 0.0F, crouchBodyRotation * 0.5F) * 0.5F;
		this.gnomadBody.y += Mth.lerp(Math.abs(crouch), 0.0F, 2.5F);
		this.gnomadBody.z += Mth.lerp(Math.abs(crouch), 0.0F, -2.5F);

		//this.gnomadBody.xRot += -0.2F; //MathUtils.minMaxSin(ageInTicks * 0.125F, -0.2F, crouchBodyRotation);
		//this.gnomadTornBottom.xRot += 0.0F;//MathUtils.minMaxSin(ageInTicks * 0.125F, -0.2F, crouchBodyRotation) * 0.5F;
		//this.gnomadBody.y += 0.0F;
		//WALK
		AnimFunctions.swingLimbs(this.gnomadLeftLeg, this.gnomadRightLeg, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);
		AnimFunctions.swingLimbs(this.gnomadRightArm, this.gnomadLeftArm, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);

		AnimFunctions.swing(this.gnomadRightArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.swing(this.gnomadLeftArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, -0.2F, f, f1, AnimFunctions.Axis.Z);

		AnimFunctions.swing(this.rootJoint, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.rootJoint, 0.5F * walkSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Y);

		AnimFunctions.bob(this.rootJoint, 2.0F * walkSpeed, 2 * globalHeight, true, f, f1);
		AnimFunctions.bob(this.gnomadHead, 2.0F * walkSpeed, 0.5F * globalHeight, true, f, f1);

		AnimFunctions.look(this.gnomadNeck, netHeadYaw, headPitch, 100.0F, 1.0F);
		AnimFunctions.look(this.gnomadHead, netHeadYaw, headPitch, 1.0F, 1.0F);

		this.gnomadTornBottom.xRot -= this.rootJoint.xRot;
		//this.gnomadTornBottom.xRot -= this.gnomadBody.xRot;

		//this.legsJoint.xRot -= this.gnomadBody.xRot;
		//this.legsJoint.yRot -= this.gnomadBody.yRot;
		//this.legsJoint.zRot -= this.gnomadBody.zRot;

		//this.armsJoint.xRot -= this.gnomadBody.xRot;
		//this.armsJoint.yRot -= this.gnomadBody.yRot;
		//this.armsJoint.zRot -= this.gnomadBody.zRot;

		this.neckJoint.xRot -= this.rootJoint.xRot;
		this.neckJoint.yRot -= this.rootJoint.yRot;
		this.neckJoint.zRot -= this.rootJoint.zRot;
		this.neckJoint.xRot -= this.gnomadBody.xRot;
		this.neckJoint.yRot -= this.gnomadBody.yRot;
		this.neckJoint.zRot -= this.gnomadBody.zRot;

		this.headJoint.xRot -= this.gnomadNeck.xRot;
		this.headJoint.yRot -= this.gnomadNeck.yRot;
		this.headJoint.zRot -= this.gnomadNeck.zRot;

		this.gnomadHat.visible = false;
		this.gnomadHelmet.visible = false;
		this.gnomadVisor.visible = false;
		this.gnomadLeftPauldron.visible = false;
		this.gnomadRightPauldron.visible = false;
	}

	@Override
	public void translateToHand(HumanoidArm pSide, PoseStack pPoseStack) {
		if (pSide == HumanoidArm.LEFT) {
			AnimFunctions.getGlobalTransForm(this.gnomadLeftArm, pPoseStack);
		} else {
			AnimFunctions.getGlobalTransForm(this.gnomadRightArm, pPoseStack);
		}
		pPoseStack.translate((pSide == HumanoidArm.LEFT ? -1 : 1) * (1.0F / 16.0F), (2.0F / 16.0F), 0);
	}
}