package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.client.render.entity.model.base.CappinModelPart;
import birsy.clinker.common.world.entity.salamander.AbstractSalamanderPartEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.Random;

public class SalamanderBodyModel<T extends AbstractSalamanderPartEntity> extends AbstractSalamanderModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Clinker.MOD_ID, "salamander_body_model"), "main");
	private static final int danglyBitsAmount = 32;

	private final CappinModelPart salamanderBodyRoot;
	private final CappinModelPart salamanderBodyTop;
	private final CappinModelPart salamanderFurTop;

	private final CappinModelPart salamanderFurLeft;
	private final CappinModelPart[] salamanderFurLeftTufts;
	private final CappinModelPart salamanderFurRight;
	private final CappinModelPart[] salamanderFurRightTufts;

	private final CappinModelPart salamanderBodyBottom;
	private final CappinModelPart salamanderDanglyBitsParent;
	private final CappinModelPart[] danglyBits;

	private final CappinModelPart salamanderBodyLeft;
	private final CappinModelPart salamanderBodyRight;
	private final CappinModelPart salamanderBone;
	private final CappinModelPart salamanderBoneFront;
	private final CappinModelPart salamanderBoneBack;
	private final CappinModelPart salamanderInnardsFront;
	private final CappinModelPart salamanderInnardsBack;
	private final CappinModelPart salamanderLegsRoot;
	private final CappinModelPart salamanderRightUpperLeg;
	private final CappinModelPart salamanderRightLowerLeg;
	private final CappinModelPart salamanderRightFoot;
	private final CappinModelPart salamanderRightRightClaw;
	private final CappinModelPart salamanderRightLeftClaw;
	private final CappinModelPart salamanderRightBackClaw;
	private final CappinModelPart salamanderLeftUpperLeg;
	private final CappinModelPart salamanderLeftLowerLeg;
	private final CappinModelPart salamanderLeftFoot;
	private final CappinModelPart salamanderLeftRightClaw;
	private final CappinModelPart salamanderLeftLeftClaw;
	private final CappinModelPart salamanderLeftBackClaw;

	public SalamanderBodyModel(ModelPart root) {
		this.salamanderBodyRoot = CappinModelPart.fromModelPart(root.getChild("salamanderBodyRoot"));
		this.salamanderBodyTop = this.salamanderBodyRoot.getChild("salamanderBodyTop");
		this.salamanderFurTop = this.salamanderBodyTop.getChild("salamanderFurTop");

		this.salamanderFurLeft = this.salamanderFurTop.getChild("salamanderFurLeft");
		this.salamanderFurLeftTufts = new CappinModelPart[11];
		Arrays.setAll(this.salamanderFurLeftTufts, (index) -> this.salamanderFurLeft.getChild(getFurPartName(index, "Left")));

		this.salamanderFurRight = this.salamanderFurTop.getChild("salamanderFurRight");
		this.salamanderFurRightTufts = new CappinModelPart[11];
		Arrays.setAll(this.salamanderFurRightTufts, (index) -> this.salamanderFurRight.getChild(getFurPartName(index, "Right")));

		this.salamanderBodyBottom = this.salamanderBodyRoot.getChild("salamanderBodyBottom");

		this.salamanderDanglyBitsParent = this.salamanderBodyBottom.getChild("salamanderDanglyBitsParent");
		this.danglyBits = new CappinModelPart[danglyBitsAmount];
		Arrays.setAll(this.danglyBits, (index) -> this.salamanderDanglyBitsParent.getChild(getDanglyBitPartName(index)));

		this.salamanderBodyLeft = this.salamanderBodyRoot.getChild("salamanderBodyLeft");
		this.salamanderBodyRight = this.salamanderBodyRoot.getChild("salamanderBodyRight");
		this.salamanderBone = this.salamanderBodyRoot.getChild("salamanderBone");
		this.salamanderBoneFront = this.salamanderBone.getChild("salamanderBoneFront");
		this.salamanderBoneBack = this.salamanderBone.getChild("salamanderBoneBack");
		this.salamanderInnardsFront = this.salamanderBodyRoot.getChild("salamanderInnardsFront");
		this.salamanderInnardsBack = this.salamanderBodyRoot.getChild("salamanderInnardsBack");
		this.salamanderLegsRoot = this.salamanderBodyRoot.getChild("salamanderLegsRoot");
		this.salamanderRightUpperLeg = this.salamanderLegsRoot.getChild("salamanderRightUpperLeg");
		this.salamanderRightLowerLeg = this.salamanderRightUpperLeg.getChild("salamanderRightLowerLeg");
		this.salamanderRightFoot = this.salamanderRightLowerLeg.getChild("salamanderRightFoot");
		this.salamanderRightRightClaw = this.salamanderRightFoot.getChild("salamanderRightRightClaw");
		this.salamanderRightLeftClaw = this.salamanderRightFoot.getChild("salamanderRightLeftClaw");
		this.salamanderRightBackClaw = this.salamanderRightFoot.getChild("salamanderRightBackClaw");
		this.salamanderLeftUpperLeg = this.salamanderLegsRoot.getChild("salamanderLeftUpperLeg");
		this.salamanderLeftLowerLeg = this.salamanderLeftUpperLeg.getChild("salamanderLeftLowerLeg");
		this.salamanderLeftFoot = this.salamanderLeftLowerLeg.getChild("salamanderLeftFoot");
		this.salamanderLeftRightClaw = this.salamanderLeftFoot.getChild("salamanderLeftRightClaw");
		this.salamanderLeftLeftClaw = this.salamanderLeftFoot.getChild("salamanderLeftLeftClaw");
		this.salamanderLeftBackClaw = this.salamanderLeftFoot.getChild("salamanderLeftBackClaw");
	}

	private static String getFurPartName(int index, String side) {
		return "salamanderFur" + side + "Tuft" + index;
	}

	private static String getDanglyBitPartName(int index) {
		return "danglyBit" + index;
	}
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition salamanderBodyRoot = partdefinition.addOrReplaceChild("salamanderBodyRoot", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));
		PartDefinition salamanderBodyTop = salamanderBodyRoot.addOrReplaceChild("salamanderBodyTop", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -1.0F, -8.0F, 9.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, 0.0F));
		PartDefinition salamanderFurTop = salamanderBodyTop.addOrReplaceChild("salamanderFurTop", CubeListBuilder.create().texOffs(50, 0).addBox(-6.0F, -1.0F, -8.0F, 12.0F, 2.0F, 16.0F, new CubeDeformation(0.0F, 0.0F, 0.5F)), PartPose.offset(0.0F, -0.75F, 0.0F));

		PartDefinition salamanderFurLeft = salamanderFurTop.addOrReplaceChild("salamanderFurLeft", CubeListBuilder.create().texOffs(50, 18).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 14.0F, 2.0F, new CubeDeformation(0.6F, 0.0F, 0.0F)), PartPose.offsetAndRotation(-6.0F, -1.0F, 0.0F, -0.2346F, 1.5708F, 0.0F));
		PartDefinition salamanderFurLeftTuft0 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft0", CubeListBuilder.create().texOffs(48, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1955F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft1 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft1", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 1.0F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft2 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft2", CubeListBuilder.create().texOffs(60, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft3 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft3", CubeListBuilder.create().texOffs(48, 0).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, -0.2346F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft4 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft4", CubeListBuilder.create().texOffs(60, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, -0.0782F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft5 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft5", CubeListBuilder.create().texOffs(48, 5).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 5.0F, 0.0F, -0.1955F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft6 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft6", CubeListBuilder.create().texOffs(54, 5).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.5F, 6.0F, 0.0F, -0.2346F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft7 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft7", CubeListBuilder.create().texOffs(48, 5).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 7.0F, 0.0F, -0.1955F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft8 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft8", CubeListBuilder.create().texOffs(48, 10).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft9 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft9", CubeListBuilder.create().texOffs(54, 10).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 8.5F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurLeftTuft10 = salamanderFurLeft.addOrReplaceChild("salamanderFurLeftTuft10", CubeListBuilder.create().texOffs(48, 10).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.6F, 9.0F, 0.0F, -0.2346F, 0.0F, 0.0F));

		PartDefinition salamanderFurRight = salamanderFurTop.addOrReplaceChild("salamanderFurRight", CubeListBuilder.create().texOffs(50, 18).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 14.0F, 2.0F, new CubeDeformation(0.6F, 0.0F, 0.0F)), PartPose.offsetAndRotation(6.0F, -1.0F, 0.0F, -0.2346F, -1.5708F, 0.0F));
		PartDefinition salamanderFurRightTuft0 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft0", CubeListBuilder.create().texOffs(48, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1955F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft1 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft1", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 1.0F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft2 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft2", CubeListBuilder.create().texOffs(60, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft3 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft3", CubeListBuilder.create().texOffs(48, 0).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, -0.2346F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft4 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft4", CubeListBuilder.create().texOffs(60, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, -0.0782F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft5 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft5", CubeListBuilder.create().texOffs(48, 5).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 5.0F, 0.0F, -0.1955F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft6 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft6", CubeListBuilder.create().texOffs(54, 5).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.5F, 6.0F, 0.0F, -0.2346F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft7 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft7", CubeListBuilder.create().texOffs(48, 5).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 7.0F, 0.0F, -0.1955F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft8 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft8", CubeListBuilder.create().texOffs(48, 10).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft9 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft9", CubeListBuilder.create().texOffs(54, 10).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 8.5F, 0.0F, -0.1173F, 0.0F, 0.0F));
		PartDefinition salamanderFurRightTuft10 = salamanderFurRight.addOrReplaceChild("salamanderFurRightTuft10", CubeListBuilder.create().texOffs(48, 10).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.6F, 9.0F, 0.0F, -0.2346F, 0.0F, 0.0F));

		PartDefinition salamanderBodyBottom = salamanderBodyRoot.addOrReplaceChild("salamanderBodyBottom", CubeListBuilder.create().texOffs(0, 17).addBox(-4.5F, 0.0F, -8.0F, 9.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.5F, 0.0F));
		PartDefinition salamanderDanglyBitsParent = salamanderBodyBottom.addOrReplaceChild("salamanderDanglyBitsParent", CubeListBuilder.create().texOffs(0, 17).addBox(-5.5F, -0.5F, -8.0F, 11.0F, 1.0F, 16.0F, new CubeDeformation(-0.5F, -0.5F, -0.5F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		Random random = new Random();
		for (int i = 0; i < danglyBitsAmount; i++) {
			int texture = random.nextInt(7);
			PartDefinition danglyBit = salamanderDanglyBitsParent.addOrReplaceChild(getDanglyBitPartName(i), CubeListBuilder.create().texOffs(34, 35 + (texture * 4)).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
					PartPose.offsetAndRotation(MathUtils.getRandomFloatBetween(random, -4.5F, 4.5F), MathUtils.getRandomFloatBetween(random, -0.5F, 0.5F), MathUtils.getRandomFloatBetween(random, -7.0F, 7.0F), 0.0F, random.nextFloat() * 6.283F, 0.0F));
		}

		PartDefinition salamanderBodyLeft = salamanderBodyRoot.addOrReplaceChild("salamanderBodyLeft", CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-1.0F, -5.5F, -8.0F, 1.0F, 11.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 0.0F, 0.0F));
		PartDefinition salamanderBodyRight = salamanderBodyRoot.addOrReplaceChild("salamanderBodyRight", CubeListBuilder.create().texOffs(0, 35).addBox(0.0F, -5.5F, -8.0F, 1.0F, 11.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.5F, 0.0F, 0.0F));
		PartDefinition salamanderBone = salamanderBodyRoot.addOrReplaceChild("salamanderBone", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 0.0F));
		PartDefinition salamanderBoneFront = salamanderBone.addOrReplaceChild("salamanderBoneFront", CubeListBuilder.create().texOffs(72, 34).addBox(-1.5F, -1.5F, -8.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition salamanderBoneBack = salamanderBone.addOrReplaceChild("salamanderBoneBack", CubeListBuilder.create().texOffs(72, 34).addBox(-1.5F, -1.5F, -8.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -3.1416F, 0.0F));
		PartDefinition salamanderInnardsFront = salamanderBodyRoot.addOrReplaceChild("salamanderInnardsFront", CubeListBuilder.create().texOffs(88, 18).addBox(-4.5F, -4.0F, -8.0F, 9.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));
		PartDefinition salamanderInnardsBack = salamanderBodyRoot.addOrReplaceChild("salamanderInnardsBack", CubeListBuilder.create().texOffs(88, 18).addBox(-4.5F, -4.0F, -8.0F, 9.0F, 8.0F, 8.0F, new CubeDeformation(0.0F, 0.0F, -0.005F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, -3.1416F, 0.0F));
		PartDefinition salamanderLegsRoot = salamanderBodyRoot.addOrReplaceChild("salamanderLegsRoot", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition salamanderRightUpperLeg = salamanderLegsRoot.addOrReplaceChild("salamanderRightUpperLeg", CubeListBuilder.create().texOffs(40, 35).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, 0.0F, 0.0F, 1.7453F, 0.0F, 0.0F));
		PartDefinition salamanderRightLowerLeg = salamanderRightUpperLeg.addOrReplaceChild("salamanderRightLowerLeg", CubeListBuilder.create().texOffs(40, 49).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 9.0F, 0.0F, -2.4435F, 0.0F, 0.0F));
		PartDefinition salamanderRightFoot = salamanderRightLowerLeg.addOrReplaceChild("salamanderRightFoot", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F));
		PartDefinition salamanderRightRightClaw = salamanderRightFoot.addOrReplaceChild("salamanderRightRightClaw", CubeListBuilder.create().texOffs(56, 34).mirror().addBox(-1.0F, -1.0F, -6.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -0.3927F, 0.0F));
		PartDefinition salamanderRightLeftClaw = salamanderRightFoot.addOrReplaceChild("salamanderRightLeftClaw", CubeListBuilder.create().texOffs(56, 34).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.3927F, 0.0F));
		PartDefinition salamanderRightBackClaw = salamanderRightFoot.addOrReplaceChild("salamanderRightBackClaw", CubeListBuilder.create().texOffs(56, 45).mirror().addBox(-1.0F, -1.0F, 0.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		PartDefinition salamanderLeftUpperLeg = salamanderLegsRoot.addOrReplaceChild("salamanderLeftUpperLeg", CubeListBuilder.create().texOffs(40, 35).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.5F, 0.0F, 0.0F, 1.7453F, 0.0F, 0.0F));
		PartDefinition salamanderLeftLowerLeg = salamanderLeftUpperLeg.addOrReplaceChild("salamanderLeftLowerLeg", CubeListBuilder.create().texOffs(40, 49).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 9.0F, 0.0F, -2.4435F, 0.0F, 0.0F));
		PartDefinition salamanderLeftFoot = salamanderLeftLowerLeg.addOrReplaceChild("salamanderLeftFoot", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F));
		PartDefinition salamanderLeftRightClaw = salamanderLeftFoot.addOrReplaceChild("salamanderLeftRightClaw", CubeListBuilder.create().texOffs(56, 34).mirror().addBox(-1.0F, -1.0F, -6.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -0.3927F, 0.0F));
		PartDefinition salamanderLeftLeftClaw = salamanderLeftFoot.addOrReplaceChild("salamanderLeftLeftClaw", CubeListBuilder.create().texOffs(56, 34).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.3927F, 0.0F));
		PartDefinition salamanderLeftBackClaw = salamanderLeftFoot.addOrReplaceChild("salamanderLeftBackClaw", CubeListBuilder.create().texOffs(56, 45).mirror().addBox(-1.0F, -1.0F, 0.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	private void resetAnimation() {
		AnimFunctions.setOffsetAndRotation(this.salamanderBodyRoot, 0.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		this.salamanderBodyRoot.setScale(1.0F, 1.0F, 1.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderBodyTop,0.0F, -4.5F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurTop,0.0F, -0.75F, 0.0F,0.0F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeft, -6.0F, -1.0F, 0.0F, -0.2346F, 1.5708F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[0], 0.0F, 0.0F, 0.0F, -0.1955F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[1], -4.0F, 1.0F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[2], 5.0F, 2.0F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[3], 6.0F, 0.0F, 0.0F, -0.2346F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[4], -6.0F, 0.0F, 0.0F, -0.0782F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[5], -2.0F, 5.0F, 0.0F, -0.1955F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[6], 6.5F, 6.0F, 0.0F, -0.2346F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[7], -6.0F, 7.0F, 0.0F, -0.1955F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[8], 0.0F, 8.0F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[9], 4.0F, 8.5F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurLeftTufts[10],-5.6F, 9.0F, 0.0F, -0.2346F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderFurRight, 6.0F, -1.0F, 0.0F, -0.2346F, -1.5708F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[0], 0.0F, 0.0F, 0.0F, -0.1955F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[1], -4.0F, 1.0F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[2], 5.0F, 2.0F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[3], 6.0F, 0.0F, 0.0F, -0.2346F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[4], -6.0F, 0.0F, 0.0F, -0.0782F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[5], -2.0F, 5.0F, 0.0F, -0.1955F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[6], 6.5F, 6.0F, 0.0F, -0.2346F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[7], -6.0F, 7.0F, 0.0F, -0.1955F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[8], 0.0F, 8.0F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[9], 4.0F, 8.5F, 0.0F, -0.1173F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderFurRightTufts[10], -5.6F, 9.0F, 0.0F, -0.2346F, 0.0F, 0.0F);

		AnimFunctions.setOffsetAndRotation(this.salamanderBodyBottom, 0.0F, 3.5F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderDanglyBitsParent, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderBodyLeft, -4.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderBodyRight, 4.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderBone, 0.0F, -1.5F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderBoneFront, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderBoneBack, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderInnardsFront, 0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderInnardsBack, 0.0F, -0.5F, 0.0F, 0.0F, -3.1416F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLegsRoot, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		//AnimFunctions.setOffsetAndRotation(this.salamanderRightUpperLeg, 6.5F, 0.0F, 0.0F, 1.7453F, 0.0F, 0.0F);
		//AnimFunctions.setOffsetAndRotation(this.salamanderRightLowerLeg, 1.0F, 9.0F, 0.0F, -2.4435F, 0.0F, 0.0F);
		//AnimFunctions.setOffsetAndRotation(this.salamanderRightFoot, 0.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightUpperLeg, 6.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightLowerLeg, 1.0F, 9.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightFoot, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightRightClaw, 0.0F, 1.0F, 0.0F, 0.0F, -0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightLeftClaw, 0.0F, 1.0F, 0.0F, 0.0F, 0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderRightBackClaw, 0.0F, 1.0F, 0.0F, 0.3491F, 0.0F, 0.0F);
		//AnimFunctions.setOffsetAndRotation(this.salamanderLeftUpperLeg, -6.5F, 0.0F, 0.0F, 1.7453F, 0.0F, 0.0F);
		//AnimFunctions.setOffsetAndRotation(this.salamanderLeftLowerLeg, -1.0F, 9.0F, 0.0F, -2.4435F, 0.0F, 0.0F);
		//AnimFunctions.setOffsetAndRotation(this.salamanderLeftFoot, 0.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLeftUpperLeg, -6.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLeftLowerLeg, -1.0F, 9.0F, 0.0F, -0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLeftFoot, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLeftRightClaw, 0.0F, 1.0F, 0.0F, 0.0F, -0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLeftLeftClaw, 0.0F, 1.0F, 0.0F, 0.0F, 0.3927F, 0.0F);
		AnimFunctions.setOffsetAndRotation(this.salamanderLeftBackClaw, 0.0F, 1.0F, 0.0F, 0.3491F, 0.0F, 0.0F);
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

		//sittin
		AnimFunctions.look(this.salamanderBodyRoot, rotationY, rotationX, 1.0F, 1.0F);

		AnimFunctions.bob(this.salamanderBodyRoot, globalSpeed * 0.1F, 0.5F * globalHeight, false, offsetAge, 0.5F);
		AnimFunctions.swing(this.salamanderLeftUpperLeg, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.25F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderLeftLowerLeg, globalSpeed * 0.1F, 0.05f * globalDegree, false, 1.0F, 0.25F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderRightUpperLeg, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.25F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderRightLowerLeg, globalSpeed * 0.1F, 0.05f * globalDegree, false, 1.0F, 0.25F, offsetAge, 0.5F, AnimFunctions.Axis.X);

		AnimFunctions.swing(this.salamanderFurRight, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderFurLeft, globalSpeed * 0.1F, 0.05f * globalDegree, true, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.X);

		if (this.salamanderFurTop.visible) {
			for (int i = 0; i < salamanderFurLeftTufts.length; i++) {
				CappinModelPart leftTuft = salamanderFurLeftTufts[i];
				CappinModelPart rightTuft = salamanderFurRightTufts[i];
				AnimFunctions.swing(leftTuft, globalSpeed * 0.1F + (i * 0.01F), 0.05f * globalDegree, true, 1.0F + (i * 0.5F), 0.25F, offsetAge + (i * 0.5F), 0.5F, AnimFunctions.Axis.X);
				AnimFunctions.swing(rightTuft, globalSpeed * 0.1F + (i * 0.01F), 0.05f * globalDegree, true, 1.0F + (i * 0.5F), 0.25F, offsetAge + (i * 0.5F), 0.5F, AnimFunctions.Axis.X);
			}
		}

		for (int i = 0; i < danglyBits.length; i++) {
			CappinModelPart bit = danglyBits[i];
			bit.setRotation(0.0F, MathUtils.awfulRandom(i + 0.23F) * 6.283F, 0.0F);
			bit.setScale(0.9F, 0.9F, 0.9F);

			AnimFunctions.cancelRotation(bit, true, false, true);
			AnimFunctions.swing(bit, globalSpeed * 0.1F + (i * 0.01F), 0.05f * globalDegree, true, 1.0F + (i * 0.5F), 0.25F, offsetAge + (i * 0.5F), 0.5F, AnimFunctions.Axis.X);
		}

		AnimFunctions.swing(this.salamanderBoneFront, globalSpeed * 0.05F, 0.05f * globalDegree, false, 1.0F, 0.25F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderBoneFront, globalSpeed * 0.08F, 0.05f * globalDegree, false, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(this.salamanderBoneFront, globalSpeed * 0.06F, 0.05f * globalDegree, false, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.Z);
		AnimFunctions.swing(this.salamanderBoneBack, globalSpeed * 0.05F, 0.05f * globalDegree, false, 1.0F, 0.25F, offsetAge, 0.5F, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderBoneBack, globalSpeed * 0.08F, 0.05f * globalDegree, false, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.Y);
		AnimFunctions.swing(this.salamanderBoneBack, globalSpeed * 0.06F, 0.05f * globalDegree, false, 1.0F, 0.0F, offsetAge, 0.5F, AnimFunctions.Axis.Z);

		float walkFactor = MathUtils.invert(Mth.clamp(limbSwingAmount, 0.0F, 1.0F));
		this.salamanderRightUpperLeg.xRot += 1.0;
		this.salamanderRightUpperLeg.xRot += -0.8 * walkFactor;
		this.salamanderRightLowerLeg.xRot += -2.0;
		this.salamanderRightLowerLeg.xRot += 0.6 * walkFactor;
		//this.salamanderRightFoot.xRot += 0.8;
		this.salamanderLeftUpperLeg.xRot += 1.0;
		this.salamanderLeftUpperLeg.xRot += -0.8 * walkFactor;
		this.salamanderLeftLowerLeg.xRot += -2.0;
		this.salamanderLeftLowerLeg.xRot += 0.6 * walkFactor;
		//this.salamanderLeftFoot.xRot += 0.8;

		//walkin
		float walkSpeed = 0.8F * globalSpeed;
		AnimFunctions.bob(this.salamanderBodyRoot, 2.0F * walkSpeed, 0.5F * globalHeight, false, 1.0F, f, f1);
		AnimFunctions.swing(this.salamanderBodyRoot, walkSpeed, 0.05f * globalDegree, false, 0.0F, 0.0F, f, f1, AnimFunctions.Axis.Z);

		this.salamanderBodyRoot.y -= 2 * f1;
		this.salamanderLegsRoot.y -= 4 * f1;
		AnimFunctions.swing(this.salamanderLeftUpperLeg, walkSpeed, 1.3f * globalDegree, false, 0.0F, 1.0F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderLeftUpperLeg, walkSpeed, 0.1f * globalDegree, false, 0.0F, -0.5F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.swing(this.salamanderLeftLowerLeg, walkSpeed, 0.8f * globalDegree, false, 1.5F, -0.5F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderLeftLowerLeg, walkSpeed, 0.3f * globalDegree, false, 1.5F, -0.5F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.cancelRotation(this.salamanderLeftFoot, true, true, false, 2);
		AnimFunctions.swing(this.salamanderLeftFoot, walkSpeed, 0.2f * globalDegree, false, 1.5F, 0.0F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderLeftRightClaw, walkSpeed, 0.1f * globalDegree, false, 1.5F, -0.5F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderLeftLeftClaw, walkSpeed, 0.1f * globalDegree, false, 1.5F, -0.5F, f, f1, AnimFunctions.Axis.X);

		AnimFunctions.swing(this.salamanderRightUpperLeg, walkSpeed, 1.3f * globalDegree, true, 0.0F, -1.0F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderRightUpperLeg, walkSpeed, 0.1f * globalDegree, true, 0.0F, -0.5F, f, f1, AnimFunctions.Axis.Y);
		AnimFunctions.swing(this.salamanderRightLowerLeg, walkSpeed, 0.8f * globalDegree, true, 1.5F, 0.5F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderRightLowerLeg, walkSpeed, 0.3f * globalDegree, true, -1.5F, -0.5F, f, f1, AnimFunctions.Axis.Z);
		AnimFunctions.cancelRotation(this.salamanderRightFoot, true, true, false, 2);
		AnimFunctions.swing(this.salamanderRightFoot, walkSpeed, 0.2f * globalDegree, true, 1.5F, 0.0F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderRightLeftClaw, walkSpeed, 0.1f * globalDegree, true, 1.5F, 0.5F, f, f1, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.salamanderRightRightClaw, walkSpeed, 0.1f * globalDegree, true, 1.5F, 0.5F, f, f1, AnimFunctions.Axis.X);

		/*float tailLength = 4;
		boolean isTail = false;
		if (entity.getOriginalBodyLength() - entity.getOriginalSegmentID() < tailLength) {
			float tailAmount = (float) (entity.getOriginalBodyLength() - entity.getOriginalSegmentID()) / tailLength;
			float thickness = MathUtils.map(0.5F, 1.0F, tailAmount);
			this.salamanderBodyRoot.setScale(thickness, thickness, 1.0F);
			isTail = true;
		}*/

		this.salamanderFurTop.visible = false;
		this.salamanderLegsRoot.visible = entity.hasLegs();
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		salamanderBodyRoot.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}