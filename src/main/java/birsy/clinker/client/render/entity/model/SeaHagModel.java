package birsy.clinker.client.render.entity.model;// Made with Blockbench 4.1.3
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.client.render.entity.model.base.CappinModelPart;
import birsy.clinker.common.entity.SeaHagEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class SeaHagModel<T extends SeaHagEntity> extends EntityModel<T> {
	//0 - Front
	//1 - Left
	//2 - Back
	//3 - Right
	private float[][] legRotations = new float[4][4];
	public final CappinModelPart[] legs;

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Clinker.MOD_ID, "sea_hag"), "main");
	private final CappinModelPart rootJoint;
	private final CappinModelPart body;
	private final CappinModelPart torso;
	private final CappinModelPart head;
	private final CappinModelPart frontLegRotationJoint;
	private final CappinModelPart frontLeg;
	private final CappinModelPart backLegRotationJoint;
	private final CappinModelPart backLeg;
	private final CappinModelPart leftLegRotationJoint;
	private final CappinModelPart leftLeg;
	private final CappinModelPart rightLegRotationJoint;
	private final CappinModelPart rightLeg;

	public SeaHagModel(ModelPart root) {
		this.rootJoint = CappinModelPart.fromModelPart(root.getChild("rootJoint"));
		this.body = this.rootJoint.getChild("body");
		this.torso = this.body.getChild("torso");
		this.head = this.torso.getChild("head");
		this.frontLegRotationJoint = this.body.getChild("frontLegRotationJoint");
		this.frontLeg = this.frontLegRotationJoint.getChild("frontLeg");
		this.backLegRotationJoint = this.body.getChild("backLegRotationJoint");
		this.backLeg = this.backLegRotationJoint.getChild("backLeg");
		this.leftLegRotationJoint = this.body.getChild("leftLegRotationJoint");
		this.leftLeg = this.leftLegRotationJoint.getChild("leftLeg");
		this.rightLegRotationJoint = this.body.getChild("rightLegRotationJoint");
		this.rightLeg = this.rightLegRotationJoint.getChild("rightLeg");

		this.legs = new CappinModelPart[]{frontLeg, leftLeg, backLeg, rightLeg};
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition rootJoint = partdefinition.addOrReplaceChild("rootJoint", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition body = rootJoint.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 42).addBox(-17.5F, -46.0F, -17.5F, 35.0F, 46.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(124, 0).addBox(-15.5F, -26.0F, -15.5F, 31.0F, 31.0F, 31.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -46.0F, 0.0F));
		PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-13.5F, -21.0F, -10.5F, 27.0F, 21.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -26.0F, 0.0F));
		PartDefinition frontLegRotationJoint = body.addOrReplaceChild("frontLegRotationJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -20F, -17.5F));
		PartDefinition frontLeg = frontLegRotationJoint.addOrReplaceChild("frontLeg", CubeListBuilder.create().texOffs(96, 0).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 29.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition backLegRotationJoint = body.addOrReplaceChild("backLegRotationJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20F, 17.5F, 0.0F, -3.1416F, 0.0F));
		PartDefinition backLeg = backLegRotationJoint.addOrReplaceChild("backLeg", CubeListBuilder.create().texOffs(96, 0).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 29.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition leftLegRotationJoint = body.addOrReplaceChild("leftLegRotationJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.5F, -20F, 0.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition leftLeg = leftLegRotationJoint.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(96, 0).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 29.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition rightLegRotationJoint = body.addOrReplaceChild("rightLegRotationJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-17.5F, -20F, 0.0F, 0.0F, 1.5708F, 0.0F));
		PartDefinition rightLeg = rightLegRotationJoint.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(96, 0).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 29.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 256, 128);
	}

	private void resetAnimation() {
		AnimFunctions.setOffsetAndRotation(this.rootJoint,0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.body, 0.0F, -9.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.torso, 0.0F, -46.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.head, 0.0F, -26.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.frontLegRotationJoint, 0.0F, -20F, -17.5F, 0, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.frontLeg, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.backLegRotationJoint, 0.0F, -20F, 17.5F, 0, 0, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.backLeg, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.leftLegRotationJoint, 17.5F, -20F, 0.0F, 0, -0, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.leftLeg, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.rightLegRotationJoint, -17.5F, -20F, 0.0F, 0, 0, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.rightLeg, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		resetAnimation();
		float footSize = 0;//3.5F / 16.0F;
		for (int i = 0; i < legs.length; i++) {
			int l = (i + 2) % 4;
			CappinModelPart leg = legs[l];
			leg.setRotation(legRotations[i][0], legRotations[i][1], legRotations[i][2]);
			leg.setScale(1.0F, ((1.0F / 28.0F) * 16.0F) * legRotations[i][3], 1.0F);
		}
		/*this.frontLeg.setRotation(legRotations[0][0], legRotations[0][1], legRotations[0][2]);
		this.frontLeg.setScale(1.0F, (1.0F / 28.0F) * 16.0F * (legRotations[0][3] + footSize), 1.0F);
		this.rightLeg.setRotation(legRotations[3][0], legRotations[3][1], legRotations[3][2]);
		this.rightLeg.setScale(1.0F, (1.0F / 28.0F) * 16.0F * (legRotations[3][3] + footSize), 1.0F);
		this.backLeg.setRotation(legRotations[2][0], legRotations[2][1], legRotations[2][2]);
		this.backLeg.setScale(1.0F, (1.0F / 28.0F) * 16.0F * (legRotations[2][3] + footSize), 1.0F);
		this.leftLeg.setRotation(legRotations[1][0], legRotations[1][1], legRotations[1][2]);
		this.leftLeg.setScale(1.0F, (1.0F / 28.0F) * 16.0F * (legRotations[1][3] + footSize), 1.0F);*/

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		rootJoint.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, 0.1F);
	}


	public void setLegRotation(int leg, float pitch, float yaw, float roll, float scale) {
		legRotations[leg] = new float[]{pitch, yaw, roll, scale};

	}
}