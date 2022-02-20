package birsy.clinker.client.render.entity.model;// Made with Blockbench 4.0.1
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


import birsy.clinker.common.entity.MudScarabEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MudScarabModel<T extends MudScarabEntity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Clinker.MOD_ID, "mud_scarab"), "main");
	private final ModelPart rootJoint;
	private final ModelPart bodyJoint;
	private final ModelPart shellJoint;
	private final ModelPart middleShell;
	private final ModelPart frontShell;
	private final ModelPart backShell;
	private final ModelPart scarabHead;
	private final ModelPart scarabLeftAntennae;
	private final ModelPart scarabRightAntennae;
	private final ModelPart scarabRightJaw;
	private final ModelPart scarabLeftJaw;
	private final ModelPart legsJoint;
	private final ModelPart scarabMiddleRightUpperLeg;
	private final ModelPart scarabMiddleRightLowerLeg;
	private final ModelPart scarabBackRightUpperLeg;
	private final ModelPart scarabBackRightLowerLeg;
	private final ModelPart scarabFrontRightUpperLeg;
	private final ModelPart scarabFrontRightLowerLeg;
	private final ModelPart scarabMiddleLeftUpperLeg;
	private final ModelPart scarabMiddleLeftLowerLeg;
	private final ModelPart scarabFrontLeftUpperLeg;
	private final ModelPart scarabFrontLeftLowerLeg;
	private final ModelPart scarabBackLeftUpperLeg;
	private final ModelPart scarabBackLeftLowerLeg;
	private final ModelPart scarabBody;

	public MudScarabModel(ModelPart root) {
		this.rootJoint = root.getChild("rootJoint");
		this.bodyJoint = this.rootJoint.getChild("bodyJoint");
		this.shellJoint = this.bodyJoint.getChild("shellJoint");
		this.middleShell = this.shellJoint.getChild("middleShell");
		this.frontShell = this.shellJoint.getChild("frontShell");
		this.backShell = this.shellJoint.getChild("backShell");
		this.scarabHead = this.bodyJoint.getChild("scarabHead");
		this.scarabLeftAntennae = this.scarabHead.getChild("scarabLeftAntennae");
		this.scarabRightAntennae = this.scarabHead.getChild("scarabRightAntennae");
		this.scarabRightJaw = this.scarabHead.getChild("scarabRightJaw");
		this.scarabLeftJaw = this.scarabHead.getChild("scarabLeftJaw");
		this.legsJoint = this.bodyJoint.getChild("legsJoint");
		this.scarabMiddleRightUpperLeg = this.legsJoint.getChild("scarabMiddleRightUpperLeg");
		this.scarabMiddleRightLowerLeg = this.scarabMiddleRightUpperLeg.getChild("scarabMiddleRightLowerLeg");
		this.scarabBackRightUpperLeg = this.legsJoint.getChild("scarabBackRightUpperLeg");
		this.scarabBackRightLowerLeg = this.scarabBackRightUpperLeg.getChild("scarabBackRightLowerLeg");
		this.scarabFrontRightUpperLeg = this.legsJoint.getChild("scarabFrontRightUpperLeg");
		this.scarabFrontRightLowerLeg = this.scarabFrontRightUpperLeg.getChild("scarabFrontRightLowerLeg");
		this.scarabMiddleLeftUpperLeg = this.legsJoint.getChild("scarabMiddleLeftUpperLeg");
		this.scarabMiddleLeftLowerLeg = this.scarabMiddleLeftUpperLeg.getChild("scarabMiddleLeftLowerLeg");
		this.scarabFrontLeftUpperLeg = this.legsJoint.getChild("scarabFrontLeftUpperLeg");
		this.scarabFrontLeftLowerLeg = this.scarabFrontLeftUpperLeg.getChild("scarabFrontLeftLowerLeg");
		this.scarabBackLeftUpperLeg = this.legsJoint.getChild("scarabBackLeftUpperLeg");
		this.scarabBackLeftLowerLeg = this.scarabBackLeftUpperLeg.getChild("scarabBackLeftLowerLeg");
		this.scarabBody = this.bodyJoint.getChild("scarabBody");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition rootJoint = partdefinition.addOrReplaceChild("rootJoint", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition bodyJoint = rootJoint.addOrReplaceChild("bodyJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.5F, 0.0F));
		PartDefinition shellJoint = bodyJoint.addOrReplaceChild("shellJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 1.0F));
		PartDefinition middleShell = shellJoint.addOrReplaceChild("middleShell", CubeListBuilder.create().texOffs(0, 69).addBox(-12.5F, -23.0F, -12.5F, 25.0F, 23.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition frontShell = shellJoint.addOrReplaceChild("frontShell", CubeListBuilder.create().texOffs(0, 46).addBox(-10.5F, -7.0F, -12.0F, 21.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, -12.5F));
		PartDefinition backShell = shellJoint.addOrReplaceChild("backShell", CubeListBuilder.create().texOffs(0, 20).addBox(-10.5F, -16.0F, 0.0F, 21.0F, 19.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 12.5F));
		PartDefinition scarabHead = bodyJoint.addOrReplaceChild("scarabHead", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -4.5F, -9.0F, 14.0F, 7.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, -16.5F));
		PartDefinition scarabLeftAntennae = scarabHead.addOrReplaceChild("scarabLeftAntennae", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -4.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, -3.0F, -9.0F, 0.0F, -0.3927F, 0.0F));
		PartDefinition scarabRightAntennae = scarabHead.addOrReplaceChild("scarabRightAntennae", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-0.5F, -0.5F, -4.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -3.0F, -9.0F, 0.0F, 0.3927F, 0.0F));
		PartDefinition scarabRightJaw = scarabHead.addOrReplaceChild("scarabRightJaw", CubeListBuilder.create().texOffs(41, 0).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.5F, 2.5F, -7.0F, 0.0F, -0.3927F, 0.0F));
		PartDefinition scarabLeftJaw = scarabHead.addOrReplaceChild("scarabLeftJaw", CubeListBuilder.create().texOffs(41, 0).mirror().addBox(-2.0F, 0.0F, -4.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 2.5F, -7.0F, 0.0F, 0.3927F, 0.0F));
		PartDefinition legsJoint = bodyJoint.addOrReplaceChild("legsJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition scarabMiddleRightUpperLeg = legsJoint.addOrReplaceChild("scarabMiddleRightUpperLeg", CubeListBuilder.create().texOffs(75, 46).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -2.0F, 0.0F, -1.0472F, 1.7453F, 0.0F));
		PartDefinition scarabMiddleRightLowerLeg = scarabMiddleRightUpperLeg.addOrReplaceChild("scarabMiddleRightLowerLeg", CubeListBuilder.create().texOffs(75, 62).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F));
		PartDefinition scarabBackRightUpperLeg = legsJoint.addOrReplaceChild("scarabBackRightUpperLeg", CubeListBuilder.create().texOffs(75, 46).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -2.0F, 9.5F, -1.0472F, 2.7925F, 0.0F));
		PartDefinition scarabBackRightLowerLeg = scarabBackRightUpperLeg.addOrReplaceChild("scarabBackRightLowerLeg", CubeListBuilder.create().texOffs(75, 62).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F));
		PartDefinition scarabFrontRightUpperLeg = legsJoint.addOrReplaceChild("scarabFrontRightUpperLeg", CubeListBuilder.create().texOffs(75, 46).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -2.0F, -9.5F, -1.0472F, 0.6981F, 0.0F));
		PartDefinition scarabFrontRightLowerLeg = scarabFrontRightUpperLeg.addOrReplaceChild("scarabFrontRightLowerLeg", CubeListBuilder.create().texOffs(75, 62).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 12.0F, 1.5F, 0.0F, 0.0F, -0.6981F));
		PartDefinition scarabMiddleLeftUpperLeg = legsJoint.addOrReplaceChild("scarabMiddleLeftUpperLeg", CubeListBuilder.create().texOffs(75, 46).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -2.0F, 0.0F, -1.0472F, -1.7453F, 0.0F));
		PartDefinition scarabMiddleLeftLowerLeg = scarabMiddleLeftUpperLeg.addOrReplaceChild("scarabMiddleLeftLowerLeg", CubeListBuilder.create().texOffs(75, 62).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F));
		PartDefinition scarabFrontLeftUpperLeg = legsJoint.addOrReplaceChild("scarabFrontLeftUpperLeg", CubeListBuilder.create().texOffs(75, 46).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -2.0F, -9.5F, -1.0472F, -0.6981F, 0.0F));
		PartDefinition scarabFrontLeftLowerLeg = scarabFrontLeftUpperLeg.addOrReplaceChild("scarabFrontLeftLowerLeg", CubeListBuilder.create().texOffs(75, 62).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 1.5F, 0.0F, 0.0F, 0.6981F));
		PartDefinition scarabBackLeftUpperLeg = legsJoint.addOrReplaceChild("scarabBackLeftUpperLeg", CubeListBuilder.create().texOffs(75, 46).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -2.0F, 9.5F, -1.0472F, -2.7925F, 0.0F));
		PartDefinition scarabBackLeftLowerLeg = scarabBackLeftUpperLeg.addOrReplaceChild("scarabBackLeftLowerLeg", CubeListBuilder.create().texOffs(75, 62).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F));
		PartDefinition scarabBody = bodyJoint.addOrReplaceChild("scarabBody", CubeListBuilder.create().texOffs(56, 0).addBox(-6.5F, -18.5F, 0.0F, 13.0F, 37.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	private void resetAnimation() {
		AnimFunctions.setOffsetAndRotation(this.rootJoint, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.bodyJoint, 0.0F, -6.5F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.shellJoint, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.middleShell, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.frontShell, 0.0F, -12.0F, -12.5F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.backShell, 0.0F, -5.0F, 12.5F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabHead, 0.0F, -3.5F, -16.5F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabLeftAntennae, 5.0F, -3.0F, -9.0F, 0.0F, -0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabRightAntennae, -5.0F, -3.0F, -9.0F, 0.0F, 0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabLeftJaw, -4.5F, 2.5F, -7.0F, 0.0F, -0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabRightJaw, 4.5F, 2.5F, -7.0F, 0.0F, 0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.legsJoint, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabMiddleRightUpperLeg, -7.0F, -2.0F, 0.0F, -1.0472F, 1.7453F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabMiddleRightLowerLeg, 0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabBackRightUpperLeg, -7.0F, -2.0F, 9.5F, -1.0472F, 2.7925F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabBackRightLowerLeg, 0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabFrontRightUpperLeg, -7.0F, -2.0F, -9.5F, -1.0472F, 0.6981F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabFrontRightLowerLeg, 0.0F, 12.0F, 1.5F, 0.0F, 0.0F, -0.6981F);
		AnimFunctions.setOffsetAndRotation(this.scarabMiddleLeftUpperLeg, 7.0F, -2.0F, 0.0F, -1.0472F, -1.7453F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabMiddleLeftLowerLeg, 0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabFrontLeftUpperLeg, 7.0F, -2.0F, -9.5F, -1.0472F, -0.6981F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabFrontLeftLowerLeg, 0.0F, 12.0F, 1.5F, 0.0F, 0.0F, 0.6981F);
		AnimFunctions.setOffsetAndRotation(this.scarabBackLeftUpperLeg, 7.0F, -2.0F, 9.5F, -1.0472F, -2.7925F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabBackLeftLowerLeg, 0.0F, 12.0F, 1.5F, -0.43F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.scarabBody,0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		resetAnimation();
		AnimFunctions.desyncAnimations(entity, ageInTicks);

		float f = limbSwing;
		float f1 = 1.1F * limbSwingAmount;

		float globalSpeed = 1.25F;
		float globalHeight = 1.0F;
		float globalDegree = 1.85F;

		/**
		 * IDLE ANIMATIONS
		 */
		AnimFunctions.look(scarabHead, netHeadYaw, headPitch, 1.0F, 1.0F);
		AnimFunctions.swing(scarabHead, 0.075f * globalSpeed, 0.02f * globalDegree, false, 1.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(scarabHead, 0.04f * globalSpeed, 0.02f * globalDegree, false, 3.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(scarabHead, 0.05f * globalSpeed, 0.02f * globalDegree, true, 12.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);

		AnimFunctions.swing(scarabLeftAntennae, 0.075f * globalSpeed, 0.2f * globalDegree, false, 1.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(scarabRightAntennae, 0.08f * globalSpeed, 0.2f * globalDegree,false, 2.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(scarabLeftAntennae, 0.04f * globalSpeed, 0.2f * globalDegree, false, 3.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(scarabRightAntennae, 0.055f * globalSpeed, 0.2f * globalDegree, true, 5.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(scarabLeftAntennae, 0.045f * globalSpeed, 0.2f * globalDegree, false, 7.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
		AnimFunctions.swing(scarabRightAntennae, 0.05f * globalSpeed, 0.2f * globalDegree, true, 12.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);

		AnimFunctions.swing(scarabLeftJaw, 0.04f * globalSpeed, 0.2f * globalDegree, false, 123.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(scarabRightJaw, 0.055f * globalSpeed, 0.2f * globalDegree, true, 32.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Y);

		AnimFunctions.swing(scarabBody, 0.075f * globalSpeed, 0.02f * globalDegree, false, 5.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(scarabBody, 0.04f * globalSpeed, 0.02f * globalDegree, true, 1.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(scarabBody, 0.05f * globalSpeed, 0.02f * globalDegree, false, 13.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);

		AnimFunctions.bob(shellJoint, 0.08f * globalSpeed, 0.3f * globalHeight, false, ageInTicks, 0.5F);
		AnimFunctions.swing(shellJoint, 0.04f * globalSpeed, 0.06f * globalDegree, false, 5.0F, 0.0F, ageInTicks, 0.5F, AnimFunctions.Axis.Z);


		/**
		 * WALK ANIMATIONS
		 */
		float walkSpeed = globalSpeed * 1.4F;

		this.bodyJoint.y = this.bodyJoint.y + (1.5F * f1);
		this.bodyJoint.xRot = this.bodyJoint.xRot - (0.07F * f1);

		AnimFunctions.swing(rootJoint, walkSpeed, 0.02f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.bob(rootJoint, 2 * walkSpeed, 0.7f * globalHeight, true, f, f1);

		AnimFunctions.swing(shellJoint, walkSpeed, 0.01f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.bob(shellJoint, 2 * walkSpeed, 0.3f * globalHeight, false, f, f1);

		final float shellOffset = 2.0F;
		AnimFunctions.swing(frontShell, walkSpeed, 0.06f * globalDegree, false, -1 * shellOffset, 0.0F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.swing(middleShell, walkSpeed, 0.03f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.swing(backShell, walkSpeed, 0.06f * globalDegree, false, shellOffset, 0.0F, f, f1, AnimFunctions.Axis.Z);

		AnimFunctions.swing(scarabHead, walkSpeed, 0.06f * globalDegree, false, -1 * shellOffset, 0.0F, f, f1, AnimFunctions.Axis.Z);


		AnimFunctions.swing(scarabFrontRightUpperLeg, walkSpeed, 0.6f * globalDegree, false, 0.0F, 1.0F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.clampedSwing(scarabFrontRightUpperLeg, walkSpeed, 0.5f * globalDegree, false, (float) Math.PI, 0.0F, f, f1, false, AnimFunctions.Axis.X);
		AnimFunctions.clampedSwing(scarabFrontRightLowerLeg, walkSpeed, 0.2f * globalDegree, false, (float) Math.PI + 0.7F, -0.3F, f, f1, false, AnimFunctions.Axis.X);

		AnimFunctions.swing(scarabMiddleRightUpperLeg, walkSpeed, 0.6f * globalDegree, true, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.clampedSwing(scarabMiddleRightUpperLeg, walkSpeed, 0.5f * globalDegree, false, 0.0F, 0.0F, f, f1, false, AnimFunctions.Axis.X);
		AnimFunctions.clampedSwing(scarabMiddleRightLowerLeg, walkSpeed, 0.1f * globalDegree, false, 0.0F, 0.0F, f, f1, false, AnimFunctions.Axis.X);

		AnimFunctions.swing(scarabBackRightUpperLeg, walkSpeed, 0.6f * globalDegree, false, 0.0F, -1.0F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.clampedSwing(scarabBackRightUpperLeg, walkSpeed, 0.5f * globalDegree, false, (float) Math.PI - 0.2F, 0.0F, f, f1, false, AnimFunctions.Axis.X);
		AnimFunctions.clampedSwing(scarabBackRightLowerLeg, walkSpeed, 0.2f * globalDegree, false, (float) Math.PI - 0.2F, 0.0F, f, f1, false, AnimFunctions.Axis.X);


		AnimFunctions.swing(scarabFrontLeftUpperLeg, walkSpeed, 0.6f * globalDegree, false, 0.0F, -1.0F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.clampedSwing(scarabFrontLeftUpperLeg, walkSpeed, 0.5f * globalDegree, false, 0.0F, 0.0F, f, f1, false, AnimFunctions.Axis.X);
		AnimFunctions.clampedSwing(scarabFrontLeftLowerLeg, walkSpeed, 0.2f * globalDegree, false, -0.7F, -0.3F, f, f1, false, AnimFunctions.Axis.X);

		AnimFunctions.swing(scarabMiddleLeftUpperLeg, walkSpeed, 0.6f * globalDegree, true, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.clampedSwing(scarabMiddleLeftUpperLeg, walkSpeed, 0.5f * globalDegree, false, (float) Math.PI, 0.0F, f, f1, false, AnimFunctions.Axis.X);
		AnimFunctions.clampedSwing(scarabMiddleLeftLowerLeg, walkSpeed, 0.1f * globalDegree, false, (float) Math.PI, 0.0F, f, f1, false, AnimFunctions.Axis.X);

		AnimFunctions.swing(scarabBackLeftUpperLeg, walkSpeed, 0.6f * globalDegree, false, 0.0F, 1.0F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.clampedSwing(scarabBackLeftUpperLeg, walkSpeed, 0.5f * globalDegree, false, 0.2F, 0.0F, f, f1, false, AnimFunctions.Axis.X);
		AnimFunctions.clampedSwing(scarabBackLeftLowerLeg, walkSpeed, 0.2f * globalDegree, false, 0.2F, 0.0F, f, f1, false, AnimFunctions.Axis.X);
	}

	@Override
	public void prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
		if (pEntity.deathTime > 0) {
			float f = ((float) pEntity.deathTime + pPartialTick - 1.0F) / 20.0F * 1.6F;
			f = Mth.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			rootJoint.y = rootJoint.y + (f * 30);
		}

		super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		rootJoint.render(poseStack, buffer, packedLight, packedOverlay);
	}
}