package birsy.clinker.client.render.entity.model;// Made with Blockbench 4.0.1
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.client.render.entity.model.base.CappinModelPart;
import birsy.clinker.common.entity.Salamander.AbstractSalamanderPartEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Random;

public class SalamanderTailModel<T extends AbstractSalamanderPartEntity> extends AbstractSalamanderModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Clinker.MOD_ID, "salamander_tail_model"), "main");
	private final CappinModelPart salamanderTailRoot;
	private final CappinModelPart salamanderTailThin;
	private final CappinModelPart thinDanglyBitsParent;
	private final CappinModelPart[] thinDanglyBits;
	private final CappinModelPart salamanderThinTailFurTop;
	private final CappinModelPart salamanderThinTailFurRight;
	private final CappinModelPart salamanderThinTailFurLeft;
	private final CappinModelPart salamanderTailThick;
	private final CappinModelPart thickDanglyBitsParent;
	private final CappinModelPart[] thickDanglyBits;
	private final CappinModelPart salamanderThickTailFurTop;
	private final CappinModelPart salamanderThickTailFurRight;
	private final CappinModelPart salamanderThickTailFurLeft;

	private static final int danglyBitsAmount = 24;


	public SalamanderTailModel(ModelPart root) {
		this.salamanderTailRoot = CappinModelPart.fromModelPart(root.getChild("salamanderTailRoot"));
		this.salamanderTailThin = salamanderTailRoot.getChild("salamanderTailThin");
		this.thinDanglyBitsParent = salamanderTailThin.getChild("thinDanglyBitsParent");
		this.thinDanglyBits = new CappinModelPart[danglyBitsAmount];
		Arrays.setAll(this.thinDanglyBits, (index) -> this.thinDanglyBitsParent.getChild(getDanglyBitPartName(false, index)));

		this.salamanderThinTailFurTop = salamanderTailThin.getChild("salamanderThinTailFurTop");
		this.salamanderThinTailFurRight = salamanderThinTailFurTop.getChild("salamanderThinTailFurRight");
		this.salamanderThinTailFurLeft = salamanderThinTailFurTop.getChild("salamanderThinTailFurLeft");
		this.salamanderTailThick = salamanderTailRoot.getChild("salamanderTailThick");
		this.thickDanglyBitsParent = salamanderTailThick.getChild("thickDanglyBitsParent");
		this.thickDanglyBits = new CappinModelPart[danglyBitsAmount];
		Arrays.setAll(this.thickDanglyBits, (index) -> this.thickDanglyBitsParent.getChild(getDanglyBitPartName(true, index)));

		this.salamanderThickTailFurTop = salamanderTailThick.getChild("salamanderThickTailFurTop");
		this.salamanderThickTailFurRight = salamanderThickTailFurTop.getChild("salamanderThickTailFurRight");
		this.salamanderThickTailFurLeft = salamanderThickTailFurTop.getChild("salamanderThickTailFurLeft");
	}

	private static String getDanglyBitPartName(boolean isThick, int index) {
		return (isThick ? "thick" : "thin") + "DanglyBit" + index;
	}


	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		Random random = new Random();

		PartDefinition tailRoot = partdefinition.addOrReplaceChild("salamanderTailRoot", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition salamanderTailThin = tailRoot.addOrReplaceChild("salamanderTailThin", CubeListBuilder.create().texOffs(48, 0).addBox(-2.5F, -2.5F, -8.0F, 5.0F, 5.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition thinDanglyBitsParent = salamanderTailThin.addOrReplaceChild("thinDanglyBitsParent", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -7.0F, 4.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.4F, 0.0F));
		for (int i = 0; i < danglyBitsAmount; i++) {
			int texture = random.nextInt(7);
			PartDefinition danglyBit = thinDanglyBitsParent.addOrReplaceChild(getDanglyBitPartName(false, i), CubeListBuilder.create().texOffs(88, 0 + (texture * 4)).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
					PartPose.offsetAndRotation(MathUtils.getRandomFloatBetween(random, -2.0F, 2.0F), MathUtils.getRandomFloatBetween(random, -0.5F, 0.5F), MathUtils.getRandomFloatBetween(random, -6.0F, 6.0F), 0.0F, random.nextFloat() * 6.283F, 0.0F));
		}

		PartDefinition salamanderThinTailFurTop = salamanderTailThin.addOrReplaceChild("salamanderThinTailFurTop", CubeListBuilder.create().texOffs(50, 20).addBox(-3.0F, -1.0F, -8.0F, 6.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition salamanderThinTailFurRight = salamanderThinTailFurTop.addOrReplaceChild("salamanderThinTailFurRight", CubeListBuilder.create().texOffs(50, 37).addBox(-8.0F, 0.0F, 0.0F, 15.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -1.0F, 0.0F, -0.1745F, -1.5708F, 0.0F));

		PartDefinition salamanderThinTailFurLeft = salamanderThinTailFurTop.addOrReplaceChild("salamanderThinTailFurLeft", CubeListBuilder.create().texOffs(50, 37).addBox(-7.0F, 0.0F, 0.0F, 15.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -1.0F, 0.0F, -0.1745F, 1.5708F, 0.0F));

		PartDefinition salamanderTailThick = tailRoot.addOrReplaceChild("salamanderTailThick", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition thickDanglyBitsParent = salamanderTailThick.addOrReplaceChild("thickDanglyBitsParent", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -7.5F, 6.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.9F, 0.0F));
		for (int i = 0; i < danglyBitsAmount; i++) {
			int texture = random.nextInt(7);
			PartDefinition danglyBit = thickDanglyBitsParent.addOrReplaceChild(getDanglyBitPartName(true, i), CubeListBuilder.create().texOffs(88, 0 + (texture * 4)).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
					PartPose.offsetAndRotation(MathUtils.getRandomFloatBetween(random, -2.0F, 2.0F), MathUtils.getRandomFloatBetween(random, -0.5F, 0.5F), MathUtils.getRandomFloatBetween(random, -7.0F, 7.0F), 0.0F, random.nextFloat() * 6.283F, 0.0F));
		}

		PartDefinition salamanderThickTailFurTop = salamanderTailThick.addOrReplaceChild("salamanderThickTailFurTop", CubeListBuilder.create().texOffs(0, 24).addBox(-4.5F, -1.0F, -8.0F, 9.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 0.0F));

		PartDefinition salamanderThickTailFurRight = salamanderThickTailFurTop.addOrReplaceChild("salamanderThickTailFurRight", CubeListBuilder.create().texOffs(0, 42).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -1.0F, 0.0F, -0.1745F, -1.5708F, 0.0F));

		PartDefinition salamanderThickTailFurLeft = salamanderThickTailFurTop.addOrReplaceChild("salamanderThickTailFurLeft", CubeListBuilder.create().texOffs(0, 42).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -1.0F, 0.0F, -0.1745F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	public void resetAnimation() {
		AnimFunctions.setOffsetAndRotation(this.salamanderTailRoot,0.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderTailThin,0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.thinDanglyBitsParent,0.0F, 1.4F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderThinTailFurTop,0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderThinTailFurRight,3.0F, -1.0F, 0.0F, -0.1745F, -1.5708F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderThinTailFurLeft,-3.0F, -1.0F, 0.0F, -0.1745F, 1.5708F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderTailThick, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.thickDanglyBitsParent,0.0F, 1.9F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderThickTailFurTop,0.0F, -3.5F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderThickTailFurRight,4.5F, -1.0F, 0.0F, -0.1745F, -1.5708F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderThickTailFurLeft,-4.5F, -1.0F, 0.0F, -0.1745F, 1.5708F, 0.0F);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		resetAnimation();
		float offsetAge = ageInTicks + (entity.getOriginalSegmentID() * 12.0F);
		float f = limbSwing + (entity.getOriginalSegmentID() * 12.0F);
		float f1 = limbSwingAmount;

		float globalSpeed = 0.8F;
		float globalHeight = 1.0F;
		float globalDegree = 1.0F;

		AnimFunctions.look(this.salamanderTailRoot, rotationY, rotationX, 1.0F, 1.0F);

		AnimFunctions.bob(this.salamanderTailRoot, globalSpeed * 0.1F, 0.5F * globalHeight, false, offsetAge, 0.5F);

		AnimFunctions.swing(this.salamanderThickTailFurRight, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderThickTailFurLeft, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderThinTailFurRight, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderThinTailFurLeft, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.X);

		for (int i = 0; i < thickDanglyBits.length; i++) {
			CappinModelPart bit = thickDanglyBits[i];
			bit.setRotation(0.0F, MathUtils.awfulRandom(i + 0.23F) * 6.283F, 0.0F);
			bit.setScale(0.9F, 0.9F, 0.9F);

			AnimFunctions.cancelRotation(bit, true, false, true);
			AnimFunctions.swing(bit, globalSpeed * 0.1F + (i * 0.01F), 0.05f * globalDegree, true, 1.0F + (i * 0.5F), 0.25F, offsetAge + (i * 0.5F), 0.5F, AnimFunctions.Axis.X);
		}

		for (int i = 0; i < thinDanglyBits.length; i++) {
			CappinModelPart bit = thinDanglyBits[i];
			bit.setRotation(0.0F, MathUtils.awfulRandom(i + 0.23F) * 6.283F, 0.0F);
			bit.setScale(0.9F, 0.9F, 0.9F);

			AnimFunctions.cancelRotation(bit, true, false, true);
			AnimFunctions.swing(bit, globalSpeed * 0.1F + (i * 0.01F), 0.05f * globalDegree, true, 1.0F + (i * 0.5F), 0.25F, offsetAge + (i * 0.5F), 0.5F, AnimFunctions.Axis.X);
		}

		//walkin
		float walkSpeed = 0.8F * globalSpeed;
		AnimFunctions.bob(this.salamanderTailRoot, 2.0F * walkSpeed, 0.5F * globalHeight, false, 1.0F, f, f1);
		AnimFunctions.swing(this.salamanderTailRoot, walkSpeed, 0.05f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Z);

		this.salamanderTailRoot.y -= 2 * f1;
		this.salamanderThinTailFurTop.visible = false;
		this.salamanderThickTailFurTop.visible = false;
	}

	public void setTailThickness(boolean isThick) {
		this.salamanderTailThick.visible = isThick;
		this.salamanderTailThin.visible = !isThick;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		salamanderTailRoot.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}