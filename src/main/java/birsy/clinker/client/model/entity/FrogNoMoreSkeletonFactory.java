package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.SkeletonFactory;
import birsy.clinker.client.model.base.constraint.InverseKinematicsConstraint;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.client.model.base.AnimationProperties;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collections;

public class FrogNoMoreSkeletonFactory implements SkeletonFactory {
	private final ModelMesh[] meshes = new ModelMesh[39];

	public FrogNoMoreSkeletonFactory() {
		int texWidth = 256;
		int texHeight = 128;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		mesh0.addCube(18F, 12F, 22F, -9F, -6F, -11F, 0F, 0F, 0F, 161F, 0F, false);
		meshes[0] = mesh0;

		StaticMesh mesh1 = new StaticMesh(texWidth, texHeight);
		mesh1.addCube(16F, 10F, 8F, -8F, -5F, -8F, 0F, 0F, 0F, 66F, 0F, false);
		meshes[1] = mesh1;

		StaticMesh mesh2 = new StaticMesh(texWidth, texHeight);
		mesh2.addCube(34F, 2F, 3F, -17F, -2F, -3F, 0.05F, 0.05F, 0.05F, 87F, 32F, false);
		mesh2.addCube(34F, 5F, 19F, -17F, 0F, -19F, 0.06F, 0.06F, 0.06F, 0F, 18F, false);
		meshes[2] = mesh2;

		StaticMesh mesh3 = new StaticMesh(texWidth, texHeight);
		mesh3.addCube(22F, 7F, 11F, -11F, -7F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[3] = mesh3;

		StaticMesh mesh4 = new StaticMesh(texWidth, texHeight);
		mesh4.addCube(32F, 2F, 15F, -16F, -2F, -15F, 0F, 0F, 0F, 0F, 46F, false);
		mesh4.addCube(34F, 3F, 1F, -17F, -2F, -16F, 0F, 0F, 0F, 0F, 42F, false);
		mesh4.addCube(1F, 3F, 15F, -17F, -2F, -15F, 0F, 0F, 0F, 79F, 42F, true);
		mesh4.addCube(1F, 3F, 15F, 16F, -2F, -15F, 0F, 0F, 0F, 79F, 42F, false);
		meshes[4] = mesh4;

		StaticMesh mesh5 = new StaticMesh(texWidth, texHeight);
		mesh5.addCube(0F, 2F, 5F, 0F, -1F, -2.5F, 0F, 0F, 0F, 87F, 13F, false);
		mesh5.addCube(0F, 2F, 5F, 0F, -1F, -2.5F, 0F, 0F, 0F, 87F, 13F, true);
		meshes[5] = mesh5;

		StaticMesh mesh6 = new StaticMesh(texWidth, texHeight);
		mesh6.addCube(16F, 14F, 31F, -8F, -7F, 0F, 0F, 0F, 0F, 162F, 39F, false);
		meshes[6] = mesh6;

		StaticMesh mesh7 = new StaticMesh(texWidth, texHeight);
		meshes[7] = mesh7;

		StaticMesh mesh8 = new StaticMesh(texWidth, texHeight);
		mesh8.addCube(0F, 9F, 30F, 0F, -6F, -15F, 0F, 0F, 0F, 162F, 54F, false);
		meshes[8] = mesh8;

		StaticMesh mesh9 = new StaticMesh(texWidth, texHeight);
		mesh9.addCube(0F, 9.000000000000004F, 30F, 0.26047226650039546F, -5.977211629518311F, -15F, 0F, 0F, 0F, 162F, 54F, true);
		meshes[9] = mesh9;

		StaticMesh mesh10 = new StaticMesh(texWidth, texHeight);
		meshes[10] = mesh10;

		StaticMesh mesh11 = new StaticMesh(texWidth, texHeight);
		mesh11.addCube(0F, 5F, 20F, 0F, -1F, -10F, 0F, 0F, 0F, 161F, 14F, true);
		meshes[11] = mesh11;

		StaticMesh mesh12 = new StaticMesh(texWidth, texHeight);
		mesh12.addCube(0F, 5F, 20F, 0F, -1F, -10F, 0F, 0F, 0F, 161F, 14F, false);
		meshes[12] = mesh12;

		StaticMesh mesh13 = new StaticMesh(texWidth, texHeight);
		mesh13.addCube(2F, 7F, 3F, -2F, -5.5F, -1.5F, 0F, 0F, 0F, 122F, 0F, false);
		meshes[13] = mesh13;

		StaticMesh mesh14 = new StaticMesh(texWidth, texHeight);
		mesh14.addCube(2F, 12F, 2F, -1F, -11F, 0F, -0.01F, -0.01F, -0.01F, 114F, 0F, false);
		meshes[14] = mesh14;

		StaticMesh mesh15 = new StaticMesh(texWidth, texHeight);
		mesh15.addCube(2F, 5F, 5F, -1.5F, -5F, -1F, 0F, 0F, 0F, 114F, 14F, false);
		meshes[15] = mesh15;

		StaticMesh mesh16 = new StaticMesh(texWidth, texHeight);
		mesh16.addCube(2F, 5F, 5F, -0.5F, -5F, -1F, 0F, 0F, 0F, 100F, 18F, false);
		meshes[16] = mesh16;

		StaticMesh mesh17 = new StaticMesh(texWidth, texHeight);
		mesh17.addCube(1F, 4F, 4F, -0.5F, -3F, -1F, 0F, 0F, 0F, 123F, 10F, false);
		meshes[17] = mesh17;

		StaticMesh mesh18 = new StaticMesh(texWidth, texHeight);
		mesh18.addCube(2F, 7F, 3F, 0F, -5.5F, -1.5F, 0F, 0F, 0F, 122F, 0F, true);
		meshes[18] = mesh18;

		StaticMesh mesh19 = new StaticMesh(texWidth, texHeight);
		mesh19.addCube(2F, 12F, 2F, -1F, -11F, 0F, -0.01F, -0.01F, -0.01F, 114F, 0F, true);
		meshes[19] = mesh19;

		StaticMesh mesh20 = new StaticMesh(texWidth, texHeight);
		meshes[20] = mesh20;

		StaticMesh mesh21 = new StaticMesh(texWidth, texHeight);
		mesh21.addCube(1F, 4F, 4F, 0.5F, -3F, 1F, 0F, 0F, 0F, 123F, 10F, true);
		meshes[21] = mesh21;

		StaticMesh mesh22 = new StaticMesh(texWidth, texHeight);
		mesh22.addCube(2F, 5F, 5F, -1F, -5F, 0F, 0F, 0F, 0F, 100F, 18F, true);
		meshes[22] = mesh22;

		StaticMesh mesh23 = new StaticMesh(texWidth, texHeight);
		mesh23.addCube(2F, 5F, 5F, -1F, -5F, 0F, 0F, 0F, 0F, 114F, 14F, true);
		meshes[23] = mesh23;

		StaticMesh mesh24 = new StaticMesh(texWidth, texHeight);
		mesh24.addCube(3F, 7F, 3F, -2F, -5.5F, -1.5F, 0F, 0F, 0F, 140F, 0F, false);
		meshes[24] = mesh24;

		StaticMesh mesh25 = new StaticMesh(texWidth, texHeight);
		mesh25.addCube(2F, 12F, 2F, -1F, -11F, 0F, -0.01F, -0.01F, -0.01F, 132F, 0F, false);
		meshes[25] = mesh25;

		StaticMesh mesh26 = new StaticMesh(texWidth, texHeight);
		mesh26.addCube(2F, 7F, 5F, -1F, -6F, -1F, 0F, 0F, 0F, 146F, 14F, false);
		meshes[26] = mesh26;

		StaticMesh mesh27 = new StaticMesh(texWidth, texHeight);
		mesh27.addCube(2F, 7F, 5F, -1F, -6F, -1F, 0F, 0F, 0F, 132F, 14F, false);
		meshes[27] = mesh27;

		StaticMesh mesh28 = new StaticMesh(texWidth, texHeight);
		mesh28.addCube(1F, 4F, 4F, -0.5F, -3F, -1F, 0F, 0F, 0F, 123F, 10F, false);
		meshes[28] = mesh28;

		StaticMesh mesh29 = new StaticMesh(texWidth, texHeight);
		mesh29.addCube(3F, 7F, 3F, -1F, -5.5F, -1.5F, 0F, 0F, 0F, 140F, 0F, true);
		meshes[29] = mesh29;

		StaticMesh mesh30 = new StaticMesh(texWidth, texHeight);
		mesh30.addCube(2F, 12F, 2F, -1F, -11F, 0F, -0.01F, -0.01F, -0.01F, 132F, 0F, true);
		meshes[30] = mesh30;

		StaticMesh mesh31 = new StaticMesh(texWidth, texHeight);
		mesh31.addCube(2F, 7F, 5F, -1F, -6F, -1F, 0F, 0F, 0F, 132F, 14F, true);
		meshes[31] = mesh31;

		StaticMesh mesh32 = new StaticMesh(texWidth, texHeight);
		mesh32.addCube(2F, 7F, 5F, -1F, -6F, -1F, 0F, 0F, 0F, 146F, 14F, true);
		meshes[32] = mesh32;

		StaticMesh mesh33 = new StaticMesh(texWidth, texHeight);
		mesh33.addCube(1F, 4F, 4F, -0.5F, -3F, -1F, 0F, 0F, 0F, 123F, 10F, true);
		meshes[33] = mesh33;

		StaticMesh mesh34 = new StaticMesh(texWidth, texHeight);
		meshes[34] = mesh34;

		StaticMesh mesh35 = new StaticMesh(texWidth, texHeight);
		meshes[35] = mesh35;

		StaticMesh mesh36 = new StaticMesh(texWidth, texHeight);
		meshes[36] = mesh36;

		StaticMesh mesh37 = new StaticMesh(texWidth, texHeight);
		mesh37.addCube(3F, 3F, 1F, -1.5000000000000004F, -1.5F, -0.5F, 0F, 0F, 0F, 0F, 4F, false);
		meshes[37] = mesh37;

		StaticMesh mesh38 = new StaticMesh(texWidth, texHeight);
		mesh38.addCube(3F, 3F, 1F, -1.4999999999999996F, -1.5F, -0.5000000000000036F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[38] = mesh38;

	}

	public InterpolatedSkeleton create() {
		FrogNoMoreModel model = new FrogNoMoreModel();
		InterpolatedBone BodyBone = new InterpolatedBone("Body");
		BodyBone.setInitialTransform(0F, 6F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(BodyBone, meshes[0]);
		model.Body = BodyBone;

		InterpolatedBone NeckBone = new InterpolatedBone("Neck");
		NeckBone.setInitialTransform(0F, 0.5F, -11F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(NeckBone, meshes[1]);
		model.Neck = NeckBone;

		InterpolatedBone HeadBone = new InterpolatedBone("Head");
		HeadBone.setInitialTransform(0F, -0.5F, -8F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(HeadBone, meshes[2]);
		model.Head = HeadBone;

		InterpolatedBone UpperHeadBone = new InterpolatedBone("UpperHead");
		UpperHeadBone.setInitialTransform(0F, 5F, -8F, new Quaternionf().rotationZYX(0F, 0F, -0.26179938765F));
		model.addBone(UpperHeadBone, meshes[3]);
		model.UpperHead = UpperHeadBone;

		InterpolatedBone JawBone = new InterpolatedBone("Jaw");
		JawBone.setInitialTransform(0F, 0F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(JawBone, meshes[4]);
		model.Jaw = JawBone;

		InterpolatedBone NeckFrillsBone = new InterpolatedBone("NeckFrills");
		NeckFrillsBone.setInitialTransform(0F, 5F, -3.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(NeckFrillsBone, meshes[5]);
		model.NeckFrills = NeckFrillsBone;

		InterpolatedBone TailBone = new InterpolatedBone("Tail");
		TailBone.setInitialTransform(0F, 0F, 11F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(TailBone, meshes[6]);
		model.Tail = TailBone;

		InterpolatedBone TailFrillsBone = new InterpolatedBone("TailFrills");
		TailFrillsBone.setInitialTransform(0F, 5.5F, 17F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(TailFrillsBone, meshes[7]);
		model.TailFrills = TailFrillsBone;

		InterpolatedBone TailFrillsABone = new InterpolatedBone("TailFrillsA");
		TailFrillsABone.setInitialTransform(0F, 1.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(TailFrillsABone, meshes[8]);
		model.TailFrillsA = TailFrillsABone;

		InterpolatedBone TailFrillsBBone = new InterpolatedBone("TailFrillsB");
		TailFrillsBBone.setInitialTransform(0F, 1.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(TailFrillsBBone, meshes[9]);
		model.TailFrillsB = TailFrillsBBone;

		InterpolatedBone BodyFrillsBone = new InterpolatedBone("BodyFrills");
		BodyFrillsBone.setInitialTransform(0F, 5.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(BodyFrillsBone, meshes[10]);
		model.BodyFrills = BodyFrillsBone;

		InterpolatedBone BodyFrillsABone = new InterpolatedBone("BodyFrillsA");
		BodyFrillsABone.setInitialTransform(0F, 0.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(BodyFrillsABone, meshes[11]);
		model.BodyFrillsA = BodyFrillsABone;

		InterpolatedBone BodyFillsBBone = new InterpolatedBone("BodyFillsB");
		BodyFillsBBone.setInitialTransform(0F, 0.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(BodyFillsBBone, meshes[12]);
		model.BodyFillsB = BodyFillsBBone;

		InterpolatedBone UpperLeftArmBone = new InterpolatedBone("UpperLeftArm");
		UpperLeftArmBone.setInitialTransform(-9F, -4.5F, -6.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(UpperLeftArmBone, meshes[13]);
		model.UpperLeftArm = UpperLeftArmBone;

		InterpolatedBone LowerLeftArmBone = new InterpolatedBone("LowerLeftArm");
		LowerLeftArmBone.setInitialTransform(-1F, -5.5F, 0.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(LowerLeftArmBone, meshes[14]);
		model.LowerLeftArm = LowerLeftArmBone;

		InterpolatedBone LeftArmFinger1Bone = new InterpolatedBone("LeftArmFinger1");
		LeftArmFinger1Bone.setInitialTransform(-0.5F, 0F, 0F, new Quaternionf().rotationZYX(-0.26179938765F, 0F, 0F));
		model.addBone(LeftArmFinger1Bone, meshes[15]);
		model.LeftArmFinger1 = LeftArmFinger1Bone;

		InterpolatedBone LeftArmFinger2Bone = new InterpolatedBone("LeftArmFinger2");
		LeftArmFinger2Bone.setInitialTransform(0.5F, 0F, 0F, new Quaternionf().rotationZYX(0.26179938765F, 0F, 0F));
		model.addBone(LeftArmFinger2Bone, meshes[16]);
		model.LeftArmFinger2 = LeftArmFinger2Bone;

		InterpolatedBone LeftArmFinger3Bone = new InterpolatedBone("LeftArmFinger3");
		LeftArmFinger3Bone.setInitialTransform(0F, 0F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(LeftArmFinger3Bone, meshes[17]);
		model.LeftArmFinger3 = LeftArmFinger3Bone;

		InterpolatedBone UpperRightArmBone = new InterpolatedBone("UpperRightArm");
		UpperRightArmBone.setInitialTransform(9F, -4.5F, -6.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(UpperRightArmBone, meshes[18]);
		model.UpperRightArm = UpperRightArmBone;

		InterpolatedBone LowerRightArmBone = new InterpolatedBone("LowerRightArm");
		LowerRightArmBone.setInitialTransform(1F, -5.5F, 0.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(LowerRightArmBone, meshes[19]);
		model.LowerRightArm = LowerRightArmBone;

		InterpolatedBone RightHandBone = new InterpolatedBone("RightHand");
		RightHandBone.setInitialTransform(0F, -11F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(RightHandBone, meshes[20]);
		model.RightHand = RightHandBone;

		InterpolatedBone RightArmFinger3Bone = new InterpolatedBone("RightArmFinger3");
		RightArmFinger3Bone.setInitialTransform(-1F, 0F, -1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(RightArmFinger3Bone, meshes[21]);
		model.RightArmFinger3 = RightArmFinger3Bone;

		InterpolatedBone RightArmFinger2Bone = new InterpolatedBone("RightArmFinger2");
		RightArmFinger2Bone.setInitialTransform(-1F, 0F, -1F, new Quaternionf().rotationZYX(-0.26179938765F, 0F, 0F));
		model.addBone(RightArmFinger2Bone, meshes[22]);
		model.RightArmFinger2 = RightArmFinger2Bone;

		InterpolatedBone RightArmFinger1Bone = new InterpolatedBone("RightArmFinger1");
		RightArmFinger1Bone.setInitialTransform(1F, 0F, -1F, new Quaternionf().rotationZYX(0.26179938765F, 0F, 0F));
		model.addBone(RightArmFinger1Bone, meshes[23]);
		model.RightArmFinger1 = RightArmFinger1Bone;

		InterpolatedBone UpperLeftLegBone = new InterpolatedBone("UpperLeftLeg");
		UpperLeftLegBone.setInitialTransform(-9F, -4.5F, 7.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(UpperLeftLegBone, meshes[24]);
		model.UpperLeftLeg = UpperLeftLegBone;

		InterpolatedBone LowerLeftLegBone = new InterpolatedBone("LowerLeftLeg");
		LowerLeftLegBone.setInitialTransform(-1F, -5.5F, 0.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(LowerLeftLegBone, meshes[25]);
		model.LowerLeftLeg = LowerLeftLegBone;

		InterpolatedBone LeftLegFinger1Bone = new InterpolatedBone("LeftLegFinger1");
		LeftLegFinger1Bone.setInitialTransform(-1F, 0F, 0.5F, new Quaternionf().rotationZYX(-2.0114280404442795e-16F, -0.26179938765F, -1.5707963258999984F));
		model.addBone(LeftLegFinger1Bone, meshes[26]);
		model.LeftLegFinger1 = LeftLegFinger1Bone;

		InterpolatedBone LeftLegFinger2Bone = new InterpolatedBone("LeftLegFinger2");
		LeftLegFinger2Bone.setInitialTransform(1F, 0F, 0.5F, new Quaternionf().rotationZYX(1.1273500036904057e-16F, 0.17453292509999999F, -1.5707963258999984F));
		model.addBone(LeftLegFinger2Bone, meshes[27]);
		model.LeftLegFinger2 = LeftLegFinger2Bone;

		InterpolatedBone LeftLegFinger3Bone = new InterpolatedBone("LeftLegFinger3");
		LeftLegFinger3Bone.setInitialTransform(0F, 0.5F, 1F, new Quaternionf().rotationZYX(0F, 0F, -1.5707963259F));
		model.addBone(LeftLegFinger3Bone, meshes[28]);
		model.LeftLegFinger3 = LeftLegFinger3Bone;

		InterpolatedBone UpperRightLegBone = new InterpolatedBone("UpperRightLeg");
		UpperRightLegBone.setInitialTransform(9F, -4.5F, 7.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(UpperRightLegBone, meshes[29]);
		model.UpperRightLeg = UpperRightLegBone;

		InterpolatedBone LowerRightLegBone = new InterpolatedBone("LowerRightLeg");
		LowerRightLegBone.setInitialTransform(1F, -5.5F, 0.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(LowerRightLegBone, meshes[30]);
		model.LowerRightLeg = LowerRightLegBone;

		InterpolatedBone RightLegFinger2Bone = new InterpolatedBone("RightLegFinger2");
		RightLegFinger2Bone.setInitialTransform(-1F, 0F, 0.5F, new Quaternionf().rotationZYX(-2.0114280404442795e-16F, -0.26179938765F, -1.5707963258999984F));
		model.addBone(RightLegFinger2Bone, meshes[31]);
		model.RightLegFinger2 = RightLegFinger2Bone;

		InterpolatedBone RightLegFinger1Bone = new InterpolatedBone("RightLegFinger1");
		RightLegFinger1Bone.setInitialTransform(1F, 0F, 0.5F, new Quaternionf().rotationZYX(1.1273500036904057e-16F, 0.17453292509999999F, -1.5707963258999984F));
		model.addBone(RightLegFinger1Bone, meshes[32]);
		model.RightLegFinger1 = RightLegFinger1Bone;

		InterpolatedBone RightLegFinger3Bone = new InterpolatedBone("RightLegFinger3");
		RightLegFinger3Bone.setInitialTransform(0F, 0.5F, 1F, new Quaternionf().rotationZYX(0F, 0F, -1.5707963259F));
		model.addBone(RightLegFinger3Bone, meshes[33]);
		model.RightLegFinger3 = RightLegFinger3Bone;

		InterpolatedBone LeftHandBone = new InterpolatedBone("LeftHand");
		LeftHandBone.setInitialTransform(0F, -11F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(LeftHandBone, meshes[34]);
		model.LeftHand = LeftHandBone;

		InterpolatedBone RightFootBone = new InterpolatedBone("RightFoot");
		RightFootBone.setInitialTransform(0F, -11F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(RightFootBone, meshes[35]);
		model.RightFoot = RightFootBone;

		InterpolatedBone LeftFootBone = new InterpolatedBone("LeftFoot");
		LeftFootBone.setInitialTransform(0F, -11F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(LeftFootBone, meshes[36]);
		model.LeftFoot = LeftFootBone;

		InterpolatedBone LeftEyeBone = new InterpolatedBone("LeftEye");
		LeftEyeBone.setInitialTransform(-2.4999999999999996F, 0.5F, 6.5F, new Quaternionf().rotationZYX(0F, 0F, 1.5707963259F));
		model.addBone(LeftEyeBone, meshes[37]);
		model.LeftEye = LeftEyeBone;

		InterpolatedBone RightEyeBone = new InterpolatedBone("RightEye");
		RightEyeBone.setInitialTransform(2.4999999999999996F, 0.5F, 7.5000000000000036F, new Quaternionf().rotationZYX(0F, 0F, 1.5707963259F));
		model.addBone(RightEyeBone, meshes[38]);
		model.RightEye = RightEyeBone;

		BodyBone.addChild(NeckBone);
		BodyBone.addChild(TailBone);
		BodyBone.addChild(BodyFrillsBone);
		BodyBone.addChild(UpperLeftArmBone);
		BodyBone.addChild(UpperRightArmBone);
		BodyBone.addChild(UpperLeftLegBone);
		BodyBone.addChild(UpperRightLegBone);
		NeckBone.addChild(HeadBone);
		NeckBone.addChild(NeckFrillsBone);
		HeadBone.addChild(UpperHeadBone);
		HeadBone.addChild(JawBone);
		UpperHeadBone.addChild(LeftEyeBone);
		UpperHeadBone.addChild(RightEyeBone);
		TailBone.addChild(TailFrillsBone);
		TailFrillsBone.addChild(TailFrillsABone);
		TailFrillsBone.addChild(TailFrillsBBone);
		BodyFrillsBone.addChild(BodyFrillsABone);
		BodyFrillsBone.addChild(BodyFillsBBone);
		UpperLeftArmBone.addChild(LowerLeftArmBone);
		LowerLeftArmBone.addChild(LeftHandBone);
		UpperRightArmBone.addChild(LowerRightArmBone);
		LowerRightArmBone.addChild(RightHandBone);
		RightHandBone.addChild(RightArmFinger3Bone);
		RightHandBone.addChild(RightArmFinger2Bone);
		RightHandBone.addChild(RightArmFinger1Bone);
		UpperLeftLegBone.addChild(LowerLeftLegBone);
		LowerLeftLegBone.addChild(LeftFootBone);
		UpperRightLegBone.addChild(LowerRightLegBone);
		LowerRightLegBone.addChild(RightFootBone);
		LeftHandBone.addChild(LeftArmFinger1Bone);
		LeftHandBone.addChild(LeftArmFinger2Bone);
		LeftHandBone.addChild(LeftArmFinger3Bone);
		RightFootBone.addChild(RightLegFinger1Bone);
		RightFootBone.addChild(RightLegFinger2Bone);
		RightFootBone.addChild(RightLegFinger3Bone);
		LeftFootBone.addChild(LeftLegFinger1Bone);
		LeftFootBone.addChild(LeftLegFinger2Bone);
		LeftFootBone.addChild(LeftLegFinger3Bone);
		model.buildRoots();

		InverseKinematicsConstraint leftLegIK = new InverseKinematicsConstraint(LowerLeftLegBone, 2, 0, -10, 0, 0.05F);
		InverseKinematicsConstraint leftArmIK = new InverseKinematicsConstraint(LowerLeftArmBone, 2, 0, -10, 0, 0.05F);
		InverseKinematicsConstraint rightLegIK = new InverseKinematicsConstraint(LowerRightLegBone, 2, 0, -10, 0, 0.05F);
		InverseKinematicsConstraint rightArmIK = new InverseKinematicsConstraint(LowerRightArmBone, 2, 0, -10, 0, 0.05F);
		InverseKinematicsConstraint[] constraints = {leftArmIK, leftLegIK, rightArmIK, rightLegIK};
		for (InverseKinematicsConstraint constraint : constraints) {
			constraint.target = new Vector3f(constraint.points.get(0));
			constraint.target.y = 0;
		}
		model.IKConstraints = constraints;
		model.LeftLegIK = leftLegIK;
		model.LeftArmIK = leftArmIK;
		model.RightLegIK = rightLegIK;
		model.RightArmIK = rightArmIK;
		Collections.addAll(model.constraints, constraints);

		return model;
	}
	
	public static class FrogNoMoreModel extends InterpolatedSkeleton {
		protected InterpolatedBone Body;
		protected InterpolatedBone Neck;
		protected InterpolatedBone Head;
		protected InterpolatedBone UpperHead;
		protected InterpolatedBone Jaw;
		protected InterpolatedBone NeckFrills;
		protected InterpolatedBone Tail;
		protected InterpolatedBone TailFrills;
		protected InterpolatedBone TailFrillsA;
		protected InterpolatedBone TailFrillsB;
		protected InterpolatedBone BodyFrills;
		protected InterpolatedBone BodyFrillsA;
		protected InterpolatedBone BodyFillsB;
		protected InterpolatedBone UpperLeftArm;
		protected InterpolatedBone LowerLeftArm;
		protected InterpolatedBone LeftArmFinger1;
		protected InterpolatedBone LeftArmFinger2;
		protected InterpolatedBone LeftArmFinger3;
		protected InterpolatedBone UpperRightArm;
		protected InterpolatedBone LowerRightArm;
		protected InterpolatedBone RightHand;
		protected InterpolatedBone RightArmFinger3;
		protected InterpolatedBone RightArmFinger2;
		protected InterpolatedBone RightArmFinger1;
		protected InterpolatedBone UpperLeftLeg;
		protected InterpolatedBone LowerLeftLeg;
		protected InterpolatedBone LeftLegFinger1;
		protected InterpolatedBone LeftLegFinger2;
		protected InterpolatedBone LeftLegFinger3;
		protected InterpolatedBone UpperRightLeg;
		protected InterpolatedBone LowerRightLeg;
		protected InterpolatedBone RightLegFinger2;
		protected InterpolatedBone RightLegFinger1;
		protected InterpolatedBone RightLegFinger3;
		protected InterpolatedBone LeftHand;
		protected InterpolatedBone RightFoot;
		protected InterpolatedBone LeftFoot;
		protected InterpolatedBone LeftEye;
		protected InterpolatedBone RightEye;

		protected InverseKinematicsConstraint LeftLegIK;
		protected InverseKinematicsConstraint LeftArmIK;
		protected InverseKinematicsConstraint RightLegIK;
		protected InverseKinematicsConstraint RightArmIK;
		protected InverseKinematicsConstraint[] IKConstraints;

		@Override
		public void animate(AnimationProperties properties) {
			for (Object value : this.parts.values()) {
				if (value instanceof InterpolatedBone bone) {
					//bone.reset();
				}
			}

			float age = properties.getNumProperty("ageInTicks");

			this.Body.y = this.Body.initialY + 2;

			for (InverseKinematicsConstraint ikConstraint : IKConstraints) {
				boolean isLeft = ikConstraint == LeftArmIK || ikConstraint == LeftLegIK;

				Vector3f point = ikConstraint.points.get(0);
				ikConstraint.target = new Vector3f(point);
				ikConstraint.target.add(isLeft ? -8 : 8, 0, 0);
				ikConstraint.poleTarget.set(ikConstraint.target.x(), ikConstraint.target.y(), ikConstraint.target.z());
				ikConstraint.poleTarget.add(0, 3, 12);
			}

			float stepSpeed = age * 0.2F;
			float stepHeight = 4;
			float strideLength = -8;
			LeftLegIK.target.y = (((Mth.sin(stepSpeed) + 1) * 0.5F) * stepHeight + 2);
			LeftLegIK.target.z = (LeftLegIK.target.z() + Mth.cos(stepSpeed * 1.0F) * -strideLength);
			LeftArmIK.target.y = (((Mth.cos(stepSpeed) + 1) * 0.5F) * stepHeight + 2);
			LeftArmIK.target.z = (LeftArmIK.target.z() + Mth.sin(stepSpeed * 1.0F) * strideLength);
			RightLegIK.target.y = (((Mth.cos(stepSpeed) + 1) * 0.5F) * stepHeight + 2);
			RightLegIK.target.z = (RightLegIK.target.z() + Mth.sin(stepSpeed * 1.0F) * strideLength);
			RightArmIK.target.y = (((Mth.sin(stepSpeed) + 1) * 0.5F) * stepHeight + 2);
			RightArmIK.target.z = (RightArmIK.target.z() + Mth.cos(stepSpeed * 1.0F) * -strideLength);

			float aY = 0;
			float aZ = 0;
			for (InverseKinematicsConstraint ikConstraint : IKConstraints) {
				aY += ikConstraint.target.y();
				aZ += ikConstraint.target.z() - ikConstraint.points.get(0).z();
			}
			aY /= (float)IKConstraints.length;
			aZ /= (float)IKConstraints.length;

			this.Body.y += aY * 1;
			this.Body.z = this.Body.initialZ - aZ * 0.25F;


			Quaternionf footRotation = new Quaternionf().rotateAxis(3.1415F, 1, 0, 0);
			this.LeftFoot.setGlobalSpaceRotation(footRotation);
			this.RightFoot.setGlobalSpaceRotation(footRotation);
			Quaternionf handRotation = new Quaternionf().rotateAxis(1.57075F, 1, 0, 0);
			this.LeftHand.setGlobalSpaceRotation(handRotation);
			this.RightHand.setGlobalSpaceRotation(handRotation);
		}
	}
}