package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.client.render.entity.model.base.CappinModelPart;
import birsy.clinker.common.entity.gnomad.GnomadAxemanEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

@Deprecated
public class GnomadAxemanModel<T extends GnomadAxemanEntity> extends EntityModel<T> implements ArmedModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Clinker.MOD_ID, "gnomad_axeman"), "main");
	public float sitTransition;
	public float headShakingIntensity;

	public final CappinModelPart rootJoint;
	public final CappinModelPart gnomadBody;
	public final CappinModelPart gnomadGoldSack;
	public final CappinModelPart gnomadTornBottom;

	public final CappinModelPart neckJoint;
	public final CappinModelPart gnomadNeck;
	public final CappinModelPart headJoint;
	public final CappinModelPart gnomadHead;
	public final CappinModelPart gnomadHat;
	public final CappinModelPart gnomadFace;
	public final CappinModelPart gnomadNose;
	public final CappinModelPart gnomadFaceTop;
	public final CappinModelPart gnomadFaceBottom;

	public final CappinModelPart gnomadHelmet;
	public final CappinModelPart gnomadVisor;

	public final CappinModelPart armsJoint;
	public final CappinModelPart gnomadLeftArm;
	public final CappinModelPart gnomadLeftPauldron;
	public final CappinModelPart gnomadLeftPauldronRim;
	public final CappinModelPart gnomadLeftPauldronBulb;
	public final CappinModelPart gnomadRightArm;
	public final CappinModelPart gnomadRightPauldron;
	public final CappinModelPart gnomadRightPauldronRim;
	public final CappinModelPart gnomadRightPauldronBulb;

	public final CappinModelPart legsJoint;
	public final CappinModelPart gnomadLeftLeg;
	public final CappinModelPart gnomadRightLeg;

	public GnomadAxemanModel(ModelPart root) {
		this.rootJoint = CappinModelPart.fromModelPart(root.getChild("rootJoint"));
		this.gnomadBody = rootJoint.getChild("gnomadBody");
		this.gnomadGoldSack = gnomadBody.getChild("gnomadGoldSack");
		this.gnomadTornBottom = gnomadBody.getChild("gnomadTornBottom");

		this.neckJoint = gnomadBody.getChild("neckJoint");
		this.gnomadNeck = neckJoint.getChild("gnomadNeck");
		this.headJoint = gnomadNeck.getChild("headJoint");
		this.gnomadHead = headJoint.getChild("gnomadHead");
		this.gnomadHat = gnomadHead.getChild("gnomadHat");
		this.gnomadFace = gnomadHead.getChild("gnomadFace");
		this.gnomadNose = gnomadFace.getChild("gnomadNose");
		this.gnomadFaceTop = gnomadFace.getChild("gnomadFaceTop");
		this.gnomadFaceBottom = gnomadFace.getChild("gnomadFaceBottom");

		this.gnomadHelmet = gnomadHead.getChild("gnomadHelmet");
		this.gnomadVisor = gnomadHelmet.getChild("gnomadVisor");

		this.armsJoint = gnomadBody.getChild("armsJoint");
		this.gnomadLeftArm = armsJoint.getChild("gnomadLeftArm");
		this.gnomadLeftPauldron = gnomadLeftArm.getChild("gnomadLeftPauldron");
		this.gnomadLeftPauldronRim = gnomadLeftPauldron.getChild("gnomadLeftPauldronRim");
		this.gnomadLeftPauldronBulb = gnomadLeftPauldronRim.getChild("gnomadLeftPauldronBulb");
		this.gnomadRightArm = armsJoint.getChild("gnomadRightArm");
		this.gnomadRightPauldron = gnomadRightArm.getChild("gnomadRightPauldron");
		this.gnomadRightPauldronRim = gnomadRightPauldron.getChild("gnomadRightPauldronRim");
		this.gnomadRightPauldronBulb = gnomadRightPauldronRim.getChild("gnomadRightPauldronBulb");

		this.legsJoint = gnomadBody.getChild("legsJoint");
		this.gnomadLeftLeg = legsJoint.getChild("gnomadLeftLeg");
		this.gnomadRightLeg = legsJoint.getChild("gnomadRightLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition rootJoint = partdefinition.addOrReplaceChild("rootJoint", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition gnomadBody = rootJoint.addOrReplaceChild("gnomadBody", CubeListBuilder.create().texOffs(0, 24).addBox(-5.0F, -11.0F, -4.0F, 10.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.25F, 0.0F, 0.2F, 0.0F, 0.0F));
		PartDefinition gnomadGoldSack = gnomadBody.addOrReplaceChild("gnomadGoldSack", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -6.0F, 0.0F, 9.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 4.0F));
		PartDefinition gnomadTornBottom = gnomadBody.addOrReplaceChild("gnomadTornBottom", CubeListBuilder.create().texOffs(0, 46).addBox(-5.0F, 0.0F, -8.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(-0.1F, 0.0F, 0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 4.0F, -0.2F, 0.0F, 0.0F));

		PartDefinition neckJoint = gnomadBody.addOrReplaceChild("neckJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.575F, -3.0F, -0.2F, 0.0F, 0.0F));
		PartDefinition gnomadNeck = neckJoint.addOrReplaceChild("gnomadNeck", CubeListBuilder.create().texOffs(46, 16).addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3F, 0.0F, 0.0F));
		PartDefinition headJoint = gnomadNeck.addOrReplaceChild("headJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, 0.3F, 0.0F, 0.0F));
		PartDefinition gnomadHead = headJoint.addOrReplaceChild("gnomadHead", CubeListBuilder.create().texOffs(28, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition gnomadHat = gnomadHead.addOrReplaceChild("gnomadHat", CubeListBuilder.create().texOffs(32, 22).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.5F, 0.0169F, 0.0F, -0.0169F));
		PartDefinition gnomadFace = gnomadHead.addOrReplaceChild("gnomadFace", CubeListBuilder.create().texOffs(32, 13).addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -7.0F));
		PartDefinition gnomadNose = gnomadFace.addOrReplaceChild("gnomadNose", CubeListBuilder.create().texOffs(50, 12).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, -0.35F, 0.0F, 0.0F));
		PartDefinition gnomadFaceTop = gnomadFace.addOrReplaceChild("gnomadFaceTop", CubeListBuilder.create().texOffs(33, 12).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 0.0F));
		PartDefinition gnomadFaceBottom = gnomadFace.addOrReplaceChild("gnomadFaceBottom", CubeListBuilder.create().texOffs(33, 19).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.5F, 0.0F));

		PartDefinition gnomadHelmet = gnomadHead.addOrReplaceChild("gnomadHelmet", CubeListBuilder.create().texOffs(36, 39).addBox(-4.0F, -5.0F, -6.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 1.5F, -1.5F));
		PartDefinition gnomadVisor = gnomadHelmet.addOrReplaceChild("gnomadVisor", CubeListBuilder.create().texOffs(36, 50).addBox(-4.5F, -1.5F, -3.5F, 9.0F, 5.0F, 5.0F, new CubeDeformation(-0.30F, 0.0F, 0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.4F, 0.0F, 0.0F));

		PartDefinition armsJoint = gnomadBody.addOrReplaceChild("armsJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, -0.2F, 0.0F, 0.0F));
		PartDefinition gnomadLeftArm = armsJoint.addOrReplaceChild("gnomadLeftArm", CubeListBuilder.create().texOffs(48, 25).mirror().addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(-0.25F, -0.5F, -0.25F)).mirror(false), PartPose.offset(-5.35F, -0.125F, 0.0F));
		PartDefinition gnomadLeftPauldron = gnomadLeftArm.addOrReplaceChild("gnomadLeftPauldron", CubeListBuilder.create().texOffs(10, 17).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
		PartDefinition gnomadLeftPauldronRim = gnomadLeftPauldron.addOrReplaceChild("gnomadLeftPauldronRim", CubeListBuilder.create().texOffs(46, 0).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.5F, -1.0F));
		PartDefinition gnomadLeftPauldronBulb = gnomadLeftPauldronRim.addOrReplaceChild("gnomadLeftPauldronBulb", CubeListBuilder.create().texOffs(0, 17).addBox(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition gnomadRightArm = armsJoint.addOrReplaceChild("gnomadRightArm", CubeListBuilder.create().texOffs(48, 25).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(-0.25F, -0.5F, -0.25F)), PartPose.offset(5.35F, -0.125F, 0.0F));
		PartDefinition gnomadRightPauldron = gnomadRightArm.addOrReplaceChild("gnomadRightPauldron", CubeListBuilder.create().texOffs(10, 17).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition gnomadRightPauldronRim = gnomadRightPauldron.addOrReplaceChild("gnomadRightPauldronRim", CubeListBuilder.create().texOffs(46, 0).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.5F, -1.0F));
		PartDefinition gnomadRightPauldronBulb = gnomadRightPauldronRim.addOrReplaceChild("gnomadRightPauldronBulb", CubeListBuilder.create().texOffs(0, 17).addBox(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition legsJoint = gnomadBody.addOrReplaceChild("legsJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.2F, 0.0F, 0.0F));
		PartDefinition gnomadLeftLeg = legsJoint.addOrReplaceChild("gnomadLeftLeg", CubeListBuilder.create().texOffs(56, 25).mirror().addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(-0.25F, -0.5F, -0.25F)).mirror(false), PartPose.offset(-3.4F, 0.0F, 0.0F));
		PartDefinition gnomadRightLeg = legsJoint.addOrReplaceChild("gnomadRightLeg", CubeListBuilder.create().texOffs(56, 25).addBox(-1.0F, -0.15F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(-0.25F, -0.5F, -0.25F)), PartPose.offset(3.5F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public void resetAnimation() {
		AnimFunctions.setOffsetAndRotation(this.rootJoint, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadBody, 0.0F, -11.25F, 0.0F, 0.2F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadGoldSack, 0.0F, -4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadTornBottom, 0.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.neckJoint, 0.0F, -10.575F, -3.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadNeck, 0.0F, 0.0F, 0.0F, -0.3F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.headJoint, 0.0F, 0.0F, -3.5F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setScale(this.headJoint, 0.8F);
		AnimFunctions.setOffsetAndRotation(this.gnomadHead, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadHat, 0.0F, -3.0F, -3.5F, 0.0169F, 0.0F, -0.0169F);
		AnimFunctions.setOffsetAndRotation(this.gnomadFace, 0.0F, 0.0F, -7.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadNose, 0.0F, -1.5F, 0.0F, -0.35F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadFaceTop, 0.0F, -2.5F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadFaceBottom, 0.0F, 3.5F, 0.0F, 0.0F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.gnomadHelmet, 0.0F, 1.5F, -1.5F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadVisor, 0.0F, -1.0F, -4.0F, 0.4F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.armsJoint, 0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadLeftArm, -5.35F, -0.125F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadLeftPauldron, 0.5F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadLeftPauldronRim, 0.0F, 2.5F, -1.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadLeftPauldronBulb, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadRightArm, 5.35F, -0.125F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadRightPauldron, -0.5F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadRightPauldronRim, 0.0F, 2.5F, -1.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadRightPauldronBulb, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.legsJoint, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadLeftLeg, -3.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.gnomadRightLeg, 3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		resetAnimation();
		float f = limbSwing;
		float f1 = limbSwingAmount * 2F;

		float globalSpeed = 1.25F;
		float globalHeight = 0.75F;
		float globalDegree = 1.25F;

		float walkSpeed = 1.1F * globalSpeed;

		//IDLE
		AnimFunctions.swing(this.rootJoint, 0.025F * globalSpeed, 0.05f * globalDegree, false, 0.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);

		AnimFunctions.swing(this.gnomadRightArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.5F, -0.07F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
		AnimFunctions.swing(this.gnomadLeftArm, 0.125F * globalSpeed, 0.03f * globalDegree, false, 1.5F, 0.07F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
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

		AnimFunctions.swing(this.gnomadRightArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, -0.2F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.swing(this.gnomadLeftArm, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, AnimFunctions.Axis.Z);

		AnimFunctions.swing(this.rootJoint, walkSpeed, 0.06f * globalDegree, false, 0.0F, 0.2F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.rootJoint, 0.5F * walkSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Y);

		AnimFunctions.bob(this.rootJoint, 2.0F * walkSpeed, 2 * globalHeight, true, f, f1);
		AnimFunctions.bob(this.gnomadHead, 2.0F * walkSpeed, 0.5F * globalHeight, true, f, f1);

		AnimFunctions.look(this.gnomadNeck, netHeadYaw, headPitch, 100.0F, 1.0F);
		AnimFunctions.look(this.gnomadHead, netHeadYaw, headPitch, 1.0F, 1.0F);

		this.gnomadTornBottom.xRot -= this.gnomadBody.xRot;

		this.legsJoint.xRot -= this.gnomadBody.xRot;
		this.legsJoint.yRot -= this.gnomadBody.yRot;
		this.legsJoint.zRot -= this.gnomadBody.zRot;

		this.armsJoint.xRot -= this.gnomadBody.xRot;
		this.armsJoint.yRot -= this.gnomadBody.yRot;
		this.armsJoint.zRot -= this.gnomadBody.zRot;

		this.neckJoint.xRot -= this.gnomadBody.xRot;
		this.neckJoint.yRot -= this.gnomadBody.yRot;
		this.neckJoint.zRot -= this.gnomadBody.zRot;

		this.headJoint.xRot -= this.gnomadNeck.xRot;
		this.headJoint.yRot -= this.gnomadNeck.yRot;
		this.headJoint.zRot -= this.gnomadNeck.zRot;

		this.gnomadHelmet.visible = false;
		this.gnomadVisor.visible = false;
		this.gnomadLeftPauldron.visible = false;
		this.gnomadRightPauldron.visible = false;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		rootJoint.render(poseStack, buffer, packedLight, packedOverlay);
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