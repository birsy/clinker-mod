package birsy.clinker.client.render.entity.model;

import birsy.clinker.common.entity.Salamander.SalamanderHeadEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SalamanderHeadModel<T extends SalamanderHeadEntity> extends AbstractSalamanderModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Clinker.MOD_ID, "salamander_head_model"), "main");
	private final CappinModelPart salamanderHeadRoot;
	private final CappinModelPart salamanderHead;
	private final CappinModelPart salamanderForehead;
	private final CappinModelPart salamanderHeadInside;
	private final CappinModelPart salamanderLeftFrill;
	private final CappinModelPart salamanderRightFrill;
	private final CappinModelPart salamanderTopFrill;
	private final CappinModelPart salamanderRightEyeSocket;
	private final CappinModelPart salamanderRightEye;
	private final CappinModelPart salamanderLeftEyeSocket;
	private final CappinModelPart salamanderLeftEye;
	private static final int danglyBitsAmount = 16;
	//private final CappinModelPart[] danglyBits;
	private final CappinModelPart salamanderJaw;
	private final CappinModelPart salamanderJawInside;

	public SalamanderHeadModel(ModelPart root) {
		this.salamanderHeadRoot = CappinModelPart.fromModelPart(root.getChild("salamanderHeadRoot"));
		this.salamanderHead = this.salamanderHeadRoot.getChild("salamanderHead");
		this.salamanderForehead = this.salamanderHead.getChild("salamanderForehead");
		this.salamanderHeadInside = this.salamanderHead.getChild("salamanderHeadInside");

		this.salamanderLeftFrill = this.salamanderForehead.getChild("salamanderLeftFrill");
		this.salamanderRightFrill = this.salamanderForehead.getChild("salamanderRightFrill");
		this.salamanderTopFrill = this.salamanderForehead.getChild("salamanderTopFrill");

		this.salamanderRightEyeSocket = this.salamanderHead.getChild("salamanderRightEyeSocket");
		this.salamanderRightEye = this.salamanderRightEyeSocket.getChild("salamanderRightEye");

		this.salamanderLeftEyeSocket = this.salamanderHead.getChild("salamanderLeftEyeSocket");
		this.salamanderLeftEye = this.salamanderLeftEyeSocket.getChild("salamanderLeftEye");

		this.salamanderJaw = this.salamanderHead.getChild("salamanderJaw");
		//this.danglyBits = new CappinModelPart[danglyBitsAmount];
		//Arrays.setAll(this.danglyBits, (index) -> this.salamanderJaw.getChild(getDanglyBitPartName(index)));

		this.salamanderJawInside = this.salamanderJaw.getChild("salamanderJawInside");
	}

	private static String getDanglyBitPartName(int index) {
		return "danglyBit" + index;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition salamanderHeadRoot = partdefinition.addOrReplaceChild("salamanderHeadRoot", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition salamanderHead = salamanderHeadRoot.addOrReplaceChild("salamanderHead", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -2.5F, -16.0F, 12.0F, 5.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.0F));
		PartDefinition salamanderForehead = salamanderHead.addOrReplaceChild("salamanderForehead", CubeListBuilder.create().texOffs(0, 23).addBox(-4.0F, -2.0F, -8.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, -6.0F));
		PartDefinition salamanderHeadInside = salamanderHead.addOrReplaceChild("salamanderHeadInside", CubeListBuilder.create().texOffs(62, 6).addBox(-6.0F, 0.5F, -16.0F, 12.0F, 2.0F, 15.0F, new CubeDeformation(-0.05F, -0.05F, 0.0F)), PartPose.offset(0.0F, -0.06F, 0.0F));

		PartDefinition salamanderLeftFrill = salamanderForehead.addOrReplaceChild("salamanderLeftFrill", CubeListBuilder.create().texOffs(44, -1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -2.0F, 8.0F, 0.0F, -0.7854F, 0.0F));
		PartDefinition salamanderRightFrill = salamanderForehead.addOrReplaceChild("salamanderRightFrill", CubeListBuilder.create().texOffs(44, -1).mirror().addBox(0.0F, -1.0F, -1.0F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, -2.0F, 8.0F, 0.0F, 0.7854F, 0.0F));
		PartDefinition salamanderTopFrill = salamanderForehead.addOrReplaceChild("salamanderTopFrill", CubeListBuilder.create().texOffs(39, 0).mirror().addBox(-6.0F, 0.0F, 0.0F, 12.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, 8.0F, 0.7854F, 0.0F, 0.0F));

		float eyeLength = (float) Math.sqrt(13);
		PartDefinition salamanderRightEyeSocket = salamanderHead.addOrReplaceChild("salamanderRightEyeSocket", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, 0.0F, -3.5F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -2.5F, -3.5F));
		PartDefinition salamanderRightEye = salamanderRightEyeSocket.addOrReplaceChild("salamanderRightEye", CubeListBuilder.create().texOffs(1, 2).addBox(-2.5F, 0.0F, -1.0F, 6.0F, eyeLength, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 0.0F, 0.588F, 1.5708F, 0.0F));

		PartDefinition salamanderLeftEyeSocket = salamanderHead.addOrReplaceChild("salamanderLeftEyeSocket", CubeListBuilder.create().texOffs(0, 7).mirror().addBox(0.0F, 0.0F, -3.5F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, -2.5F, -3.5F));
		PartDefinition salamanderLeftEye = salamanderLeftEyeSocket.addOrReplaceChild("salamanderLeftEye", CubeListBuilder.create().texOffs(0, 2).addBox(-2.5F, 0.0F, 0.0F, 6.0F, eyeLength, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.0F, 0.0F, -0.588F, 1.5708F, 0.0F));

		PartDefinition salamanderJaw = salamanderHead.addOrReplaceChild("salamanderJaw", CubeListBuilder.create().texOffs(0, 41).addBox(-6.0F, -4.0F, -12.5F, 12.0F, 4.0F, 13.0F, new CubeDeformation(0.25F, 0.0F, 0.0F)), PartPose.offset(0.0F, 2.5F, -3.0F));
		PartDefinition salamanderJawInside = salamanderJaw.addOrReplaceChild("salamanderJawInside", CubeListBuilder.create().texOffs(52, 41).addBox(-6.0F, -4.0F, -12.5F, 12.0F, 4.0F, 13.0F, new CubeDeformation(0.15F, -0.1F, -0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	private void resetAnimation() {
		AnimFunctions.setOffsetAndRotation(this.salamanderHeadRoot,0.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderHead, 0.0F, 0.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderForehead, 0.0F, -2.5F, -6.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderHeadInside, 0.0F, -0.06F, 0.0F, 0.0F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderLeftFrill, -4.0F, -2.0F, 8.0F, 0.0F, -0.7854F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightFrill, 4.0F, -2.0F, 8.0F, 0.0F, 0.7854F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderTopFrill, 0.0F, -1.0F, 8.0F, 0.7854F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderRightEyeSocket, 6.0F, -2.5F, -3.5F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightEye, -2.0F, -1.0F, 0.0F, 0.588F, 1.5708F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderLeftEyeSocket, -6.0F, -2.5F, -3.5F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLeftEye, 2.0F, -1.0F, 0.0F, -0.588F, 1.5708F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderJaw, 0.0F, 2.5F, -3.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderJawInside,0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		resetAnimation();
		AnimFunctions.look(this.salamanderHeadRoot, rotationY, rotationX, 1.0F, 1.0F);

		float f = limbSwing;
		float f1 = limbSwingAmount;
		float offsetAge = ageInTicks + (entity.getSegmentID() * 12.0F);

		float globalSpeed = 0.8F;
		float globalHeight = 1.0F;
		float globalDegree = 1.0F;

		AnimFunctions.bob(this.salamanderHeadRoot, globalSpeed * 0.1F, 0.5F * globalHeight, false, offsetAge, 0.5F);
		AnimFunctions.swing(this.salamanderJaw, globalSpeed * 0.05F, 0.05f * globalDegree, false, 1.0F, 0.1F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderJaw, globalSpeed * 0.08F, 0.01f * globalDegree, false, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(this.salamanderJaw, globalSpeed * 0.06F, 0.01f * globalDegree, false, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.Z);

		AnimFunctions.swing(salamanderRightFrill, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, -0.25F, offsetAge, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(salamanderLeftFrill, globalSpeed * 0.11F, 0.05f * globalDegree, true, 0.9F, -0.25F, offsetAge, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(salamanderTopFrill, globalSpeed * 0.09F, 0.05f * globalDegree, true, 1.1F, -0.25F, offsetAge, 0.5F, AnimFunctions.Axis.X);

		this.salamanderHead.xRot += 0.3 * Mth.clamp(f1, 0.0F, 0.5F);

		AnimFunctions.look(this.salamanderHeadRoot, netHeadYaw, headPitch, 1.0F, 1.0F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		salamanderHeadRoot.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}