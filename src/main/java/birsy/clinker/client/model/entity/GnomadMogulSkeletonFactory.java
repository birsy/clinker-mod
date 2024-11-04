package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.SkeletonFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class GnomadMogulSkeletonFactory implements SkeletonFactory {
	private final ModelMesh[] meshes = new ModelMesh[21];
	
	public GnomadMogulSkeletonFactory() {
		int texWidth = 256;
		int texHeight = 256;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		meshes[0] = mesh0;
		
		StaticMesh mesh1 = new StaticMesh(texWidth, texHeight);
		mesh1.addCube(36F, 6F, 36F, -18F, 20F, -19.5F, 0F, 0F, 0F, 112F, 0F, false);
		mesh1.addCube(36F, 20F, 42F, -18F, 0F, -19.5F, 0F, 0F, 0F, 100F, 42F, false);
		meshes[1] = mesh1;
		
		StaticMesh mesh2 = new StaticMesh(texWidth, texHeight);
		mesh2.addCube(36F, 25F, 2F, -18F, -24F, 0F, -0.1F, 0F, 0F, 104F, 131F, false);
		meshes[2] = mesh2;
		
		StaticMesh mesh3 = new StaticMesh(texWidth, texHeight);
		mesh3.addCube(36F, 25F, 2F, -18F, -24F, -2F, -0.1F, 0F, 0F, 180F, 131F, false);
		meshes[3] = mesh3;
		
		StaticMesh mesh4 = new StaticMesh(texWidth, texHeight);
		mesh4.addCube(42F, 25F, 2F, -21F, -24F, 0F, 0F, 0F, -0.1F, 168F, 104F, false);
		meshes[4] = mesh4;
		
		StaticMesh mesh5 = new StaticMesh(texWidth, texHeight);
		mesh5.addCube(42F, 25F, 2F, -21F, -24F, 0F, 0F, 0F, -0.1F, 80F, 104F, false);
		meshes[5] = mesh5;
		
		StaticMesh mesh6 = new StaticMesh(texWidth, texHeight);
		mesh6.addCube(5F, 13F, 6F, -3F, -13.5F, -3F, 0F, 0F, 0F, 0F, 113F, false);
		mesh6.addCube(4F, 19F, 4F, -2.5F, -32.5F, -1.5F, 0F, 0F, 0F, 0F, 132F, false);
		mesh6.addCube(4F, 10.5F, 4F, -2.5F, -24.5F, -1.5F, 0.4F, 0.4F, 0.4F, 16F, 133F, false);
		meshes[6] = mesh6;
		
		StaticMesh mesh7 = new StaticMesh(texWidth, texHeight);
		mesh7.addCube(1F, 4F, 2F, 0F, 16.5F, -6F, 0F, 0F, 0F, 6F, 23F, false);
		mesh7.addCube(2F, 14F, 3F, -0.5F, -9.5F, -1.5F, -0.2F, -0.2F, -0.2F, 6F, 0F, false);
		mesh7.addCube(1F, 29F, 2F, 0F, -17.5F, -1F, 0F, 0F, 0F, 0F, 0F, false);
		mesh7.addCube(1F, 1F, 4F, 0F, -18.5F, -2F, 0F, 0F, 0F, 16F, 23F, false);
		mesh7.addCube(1F, 1F, 4F, 0F, -21.5F, -2F, 0F, 0F, 0F, 22F, 24F, false);
		mesh7.addCube(1F, 2F, 1F, 0F, -20.5F, -2F, 0F, 0F, 0F, 16F, 4F, false);
		mesh7.addCube(1F, 2F, 1F, 0F, -20.5F, 1F, 0F, 0F, 0F, 25F, 4F, false);
		mesh7.addCube(2F, 1F, 6F, -0.5F, -15.5F, -3F, 0F, 0F, 0F, 6F, 23F, false);
		mesh7.addCube(1F, 2F, 7F, 0F, 9.5F, 1F, 0F, 0F, 0F, 16F, 0F, false);
		mesh7.addCube(1F, 9F, 2F, 0F, 11.5F, 6F, 0F, 0F, 0F, 18F, 9F, false);
		mesh7.addCube(1F, 2F, 2F, 0F, 18.5F, 4F, 0F, 0F, 0F, 16F, 0F, false);
		mesh7.addCube(1F, 2F, 2F, 0F, 18.5F, -4F, 0F, 0F, 0F, 25F, 0F, false);
		mesh7.addCube(1F, 2F, 10F, 0F, 20.5F, -4F, 0F, 0F, 0F, 6F, 11F, false);
		meshes[7] = mesh7;
		
		StaticMesh mesh8 = new StaticMesh(texWidth, texHeight);
		mesh8.addCube(5F, 24F, 6F, -2.5F, -24F, -3F, 0F, 0F, 0F, 32F, 113F, false);
		mesh8.addCube(5F, 9.5F, 6F, -2.5F, -24F, -3F, 0.25F, 0.25F, 0.25F, 54F, 128F, false);
		meshes[8] = mesh8;
		
		StaticMesh mesh9 = new StaticMesh(texWidth, texHeight);
		mesh9.addCube(5F, 24F, 6F, -2.5F, -24F, -3F, 0F, 0F, 0F, 32F, 113F, true);
		mesh9.addCube(5F, 9.5F, 6F, -2.5F, -24F, -3F, 0.25F, 0.25F, 0.25F, 54F, 128F, true);
		meshes[9] = mesh9;
		
		StaticMesh mesh10 = new StaticMesh(texWidth, texHeight);
		mesh10.addCube(5F, 13F, 6F, -2.5F, -13.5F, -3F, 0F, 0F, 0F, 0F, 113F, true);
		mesh10.addCube(4F, 10.5F, 4F, -2F, -24.5F, -1.5F, 0.4F, 0.4F, 0.4F, 16F, 133F, true);
		mesh10.addCube(4F, 19F, 4F, -2F, -32.5F, -1.5F, 0F, 0F, 0F, 0F, 132F, true);
		meshes[10] = mesh10;
		
		StaticMesh mesh11 = new StaticMesh(texWidth, texHeight);
		mesh11.addCube(12F, 9F, 14F, -6F, -3.25F, -10F, 0F, 0F, 0F, 64F, 0F, false);
		meshes[11] = mesh11;
		
		StaticMesh mesh12 = new StaticMesh(texWidth, texHeight);
		mesh12.addCube(18F, 16F, 14F, -9F, -5F, -10.5F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[12] = mesh12;
		
		StaticMesh mesh13 = new StaticMesh(texWidth, texHeight);
		mesh13.addCube(18F, 4F, 4F, -9F, -11.666666666666664F, -3.5F, 0F, 0F, 0F, 2F, 48F, false);
		mesh13.addCube(22F, 16F, 4F, -11F, -7.666666666666664F, -3.5F, 0F, 0F, 0F, 0F, 32F, false);
		mesh13.addCube(18F, 2F, 4F, -9F, 8.333333333333336F, -3.5F, 0F, 0F, 0F, 2F, 30F, false);
		meshes[13] = mesh13;
		
		StaticMesh mesh14 = new StaticMesh(texWidth, texHeight);
		mesh14.addCube(6F, 6F, 6F, -3F, -6F, 0F, 0F, 0F, 0F, 52F, 30F, false);
		meshes[14] = mesh14;
		
		StaticMesh mesh15 = new StaticMesh(texWidth, texHeight);
		mesh15.addCube(20F, 8F, 18F, -10F, -2.25F, -10F, 0F, 0F, 0F, 0F, 63F, false);
		mesh15.addCube(14F, 3F, 2F, -7F, 5.75F, -9F, 0F, 0F, 0F, 0F, 56F, false);
		meshes[15] = mesh15;
		
		StaticMesh mesh16 = new StaticMesh(texWidth, texHeight);
		mesh16.addCube(16F, 6F, 12F, -8F, -0.25F, 0F, 0F, 0F, 0F, 58F, 63F, false);
		mesh16.addCube(10F, 5F, 12F, -5F, 5.75F, 0F, 0F, 0F, 0F, 58F, 46F, false);
		meshes[16] = mesh16;
		
		StaticMesh mesh17 = new StaticMesh(texWidth, texHeight);
		mesh17.addCube(5F, 9F, 0F, -2.5F, 0F, -7.105427357601002e-15F, 0F, 0F, 0F, 0F, 63F, false);
		meshes[17] = mesh17;
		
		StaticMesh mesh18 = new StaticMesh(texWidth, texHeight);
		mesh18.addCube(20F, 10F, 2F, -10F, -10F, -1F, 0F, 0F, 0F, 0F, 89F, false);
		meshes[18] = mesh18;
		
		StaticMesh mesh19 = new StaticMesh(texWidth, texHeight);
		mesh19.addCube(15F, 10F, 2F, -7.5F, -10F, -1F, 0F, 0F, 0F, 34F, 101F, false);
		meshes[19] = mesh19;
		
		StaticMesh mesh20 = new StaticMesh(texWidth, texHeight);
		mesh20.addCube(15F, 10F, 2F, -7.5F, -10F, -1F, 0F, 0F, 0F, 0F, 101F, false);
		meshes[20] = mesh20;
		
	}
	
	public InterpolatedSkeleton create() {
		GnomadMogulSkeleton skeleton = new GnomadMogulSkeleton();
		InterpolatedBone MogulRootBone = new InterpolatedBone("MogulRoot");
		MogulRootBone.setInitialTransform(0F, 0F, -1.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulRootBone, meshes[0]);
		skeleton.MogulRoot = MogulRootBone;
		
		InterpolatedBone MogulBodyBone = new InterpolatedBone("MogulBody");
		MogulBodyBone.setInitialTransform(0F, 24F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulBodyBone, meshes[1]);
		skeleton.MogulBody = MogulBodyBone;
		
		InterpolatedBone MogulFrontRobeBone = new InterpolatedBone("MogulFrontRobe");
		MogulFrontRobeBone.setInitialTransform(0F, 0F, -19.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulFrontRobeBone, meshes[2]);
		skeleton.MogulFrontRobe = MogulFrontRobeBone;
		
		InterpolatedBone MogulBackRobeBone = new InterpolatedBone("MogulBackRobe");
		MogulBackRobeBone.setInitialTransform(0F, 0F, 22.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulBackRobeBone, meshes[3]);
		skeleton.MogulBackRobe = MogulBackRobeBone;
		
		InterpolatedBone MogulLeftRobeBone = new InterpolatedBone("MogulLeftRobe");
		MogulLeftRobeBone.setInitialTransform(-18F, 0F, 1.5F, new Quaternionf().rotationZYX(-0.17453292509999999F, 1.5707963259F, 0F));
		skeleton.addBone(MogulLeftRobeBone, meshes[4]);
		skeleton.MogulLeftRobe = MogulLeftRobeBone;
		
		InterpolatedBone MogulRightRobeBone = new InterpolatedBone("MogulRightRobe");
		MogulRightRobeBone.setInitialTransform(18F, 0F, 1.5F, new Quaternionf().rotationZYX(0.17453292509999999F, -1.5707963259F, 0F));
		skeleton.addBone(MogulRightRobeBone, meshes[5]);
		skeleton.MogulRightRobe = MogulRightRobeBone;
		
		InterpolatedBone MogulRightArmBone = new InterpolatedBone("MogulRightArm");
		MogulRightArmBone.setInitialTransform(19F, 16F, -8F, new Quaternionf().rotationZYX(0.15514701159461386F, 0F, 0.34906585019999997F));
		skeleton.addBone(MogulRightArmBone, meshes[6]);
		skeleton.MogulRightArm = MogulRightArmBone;
		
		InterpolatedBone MogulWarhookBone = new InterpolatedBone("MogulWarhook");
		MogulWarhookBone.setInitialTransform(-0.5F, -30F, -4F, new Quaternionf().rotationZYX(0F, 0F, -1.5707963258999997F));
		skeleton.addBone(MogulWarhookBone, meshes[7]);
		skeleton.MogulWarhook = MogulWarhookBone;
		
		InterpolatedBone MogulRightLegBone = new InterpolatedBone("MogulRightLeg");
		MogulRightLegBone.setInitialTransform(10.5F, 0F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulRightLegBone, meshes[8]);
		skeleton.MogulRightLeg = MogulRightLegBone;
		
		InterpolatedBone MogulLeftLegBone = new InterpolatedBone("MogulLeftLeg");
		MogulLeftLegBone.setInitialTransform(-10.5F, 0F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulLeftLegBone, meshes[9]);
		skeleton.MogulLeftLeg = MogulLeftLegBone;
		
		InterpolatedBone MogulLeftArmBone = new InterpolatedBone("MogulLeftArm");
		MogulLeftArmBone.setInitialTransform(-18F, 16F, -8F, new Quaternionf().rotationZYX(-0.11875624137966828F, 0.05519076687280652F, 0.43305050170380455F));
		skeleton.addBone(MogulLeftArmBone, meshes[10]);
		skeleton.MogulLeftArm = MogulLeftArmBone;
		
		InterpolatedBone MogulNeckBone = new InterpolatedBone("MogulNeck");
		MogulNeckBone.setInitialTransform(0F, 18.25F, -19F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulNeckBone, meshes[11]);
		skeleton.MogulNeck = MogulNeckBone;
		
		InterpolatedBone MogulHeadBone = new InterpolatedBone("MogulHead");
		MogulHeadBone.setInitialTransform(0F, 4.25F, -9.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulHeadBone, meshes[12]);
		skeleton.MogulHead = MogulHeadBone;
		
		InterpolatedBone MogulFaceBone = new InterpolatedBone("MogulFace");
		MogulFaceBone.setInitialTransform(0F, 2.6666666666666643F, -10.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulFaceBone, meshes[13]);
		skeleton.MogulFace = MogulFaceBone;
		
		InterpolatedBone MogulNoseBone = new InterpolatedBone("MogulNose");
		MogulNoseBone.setInitialTransform(0F, 5.333333333333336F, -3.5F, new Quaternionf().rotationZYX(0F, 0F, 0.5235987752999999F));
		skeleton.addBone(MogulNoseBone, meshes[14]);
		skeleton.MogulNose = MogulNoseBone;
		
		InterpolatedBone MogulHelmetBaseBone = new InterpolatedBone("MogulHelmetBase");
		MogulHelmetBaseBone.setInitialTransform(0F, 10.25F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulHelmetBaseBone, meshes[15]);
		skeleton.MogulHelmetBase = MogulHelmetBaseBone;
		
		InterpolatedBone MogulHelmetUpperBone = new InterpolatedBone("MogulHelmetUpper");
		MogulHelmetUpperBone.setInitialTransform(0F, 6F, -6F, new Quaternionf().rotationZYX(0F, 0F, 0.08726646254999999F));
		skeleton.addBone(MogulHelmetUpperBone, meshes[16]);
		skeleton.MogulHelmetUpper = MogulHelmetUpperBone;
		
		InterpolatedBone MogulHelmetOrnamentBone = new InterpolatedBone("MogulHelmetOrnament");
		MogulHelmetOrnamentBone.setInitialTransform(0F, 10.75F, 6.000000000000007F, new Quaternionf().rotationZYX(0.08726646255000002F, -4.3368086874712995e-19F, 0.08726646254999999F));
		skeleton.addBone(MogulHelmetOrnamentBone, meshes[17]);
		skeleton.MogulHelmetOrnament = MogulHelmetOrnamentBone;
		
		InterpolatedBone MogulBackHelmetFlapBone = new InterpolatedBone("MogulBackHelmetFlap");
		MogulBackHelmetFlapBone.setInitialTransform(0F, -1.25F, 8F, new Quaternionf().rotationZYX(0F, 0F, -1.1344640131500001F));
		skeleton.addBone(MogulBackHelmetFlapBone, meshes[18]);
		skeleton.MogulBackHelmetFlap = MogulBackHelmetFlapBone;
		
		InterpolatedBone MogulLeftHelmetFlapBone = new InterpolatedBone("MogulLeftHelmetFlap");
		MogulLeftHelmetFlapBone.setInitialTransform(-10F, -1.25F, 0.5F, new Quaternionf().rotationZYX(-0.6981317003999999F, 1.5707963259F, 0F));
		skeleton.addBone(MogulLeftHelmetFlapBone, meshes[19]);
		skeleton.MogulLeftHelmetFlap = MogulLeftHelmetFlapBone;
		
		InterpolatedBone MogulRightHelmetFlapBone = new InterpolatedBone("MogulRightHelmetFlap");
		MogulRightHelmetFlapBone.setInitialTransform(10F, -1.25F, 0.5F, new Quaternionf().rotationZYX(0.6981317003999999F, -1.5707963259F, 0F));
		skeleton.addBone(MogulRightHelmetFlapBone, meshes[20]);
		skeleton.MogulRightHelmetFlap = MogulRightHelmetFlapBone;
		
		MogulRootBone.addChild(MogulBodyBone);
		MogulBodyBone.addChild(MogulFrontRobeBone);
		MogulBodyBone.addChild(MogulBackRobeBone);
		MogulBodyBone.addChild(MogulLeftRobeBone);
		MogulBodyBone.addChild(MogulRightRobeBone);
		MogulBodyBone.addChild(MogulRightArmBone);
		MogulBodyBone.addChild(MogulRightLegBone);
		MogulBodyBone.addChild(MogulLeftLegBone);
		MogulBodyBone.addChild(MogulLeftArmBone);
		MogulBodyBone.addChild(MogulNeckBone);
		MogulRightArmBone.addChild(MogulWarhookBone);
		MogulNeckBone.addChild(MogulHeadBone);
		MogulHeadBone.addChild(MogulFaceBone);
		MogulHeadBone.addChild(MogulHelmetBaseBone);
		MogulFaceBone.addChild(MogulNoseBone);
		MogulHelmetBaseBone.addChild(MogulHelmetUpperBone);
		MogulHelmetBaseBone.addChild(MogulBackHelmetFlapBone);
		MogulHelmetBaseBone.addChild(MogulLeftHelmetFlapBone);
		MogulHelmetBaseBone.addChild(MogulRightHelmetFlapBone);
		MogulHelmetUpperBone.addChild(MogulHelmetOrnamentBone);
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class GnomadMogulSkeleton extends InterpolatedSkeleton {
		protected InterpolatedBone MogulRoot;
		protected InterpolatedBone MogulBody;
		protected InterpolatedBone MogulFrontRobe;
		protected InterpolatedBone MogulBackRobe;
		protected InterpolatedBone MogulLeftRobe;
		protected InterpolatedBone MogulRightRobe;
		protected InterpolatedBone MogulRightArm;
		protected InterpolatedBone MogulWarhook;
		protected InterpolatedBone MogulRightLeg;
		protected InterpolatedBone MogulLeftLeg;
		protected InterpolatedBone MogulLeftArm;
		protected InterpolatedBone MogulNeck;
		protected InterpolatedBone MogulHead;
		protected InterpolatedBone MogulFace;
		protected InterpolatedBone MogulNose;
		protected InterpolatedBone MogulHelmetBase;
		protected InterpolatedBone MogulHelmetUpper;
		protected InterpolatedBone MogulHelmetOrnament;
		protected InterpolatedBone MogulBackHelmetFlap;
		protected InterpolatedBone MogulLeftHelmetFlap;
		protected InterpolatedBone MogulRightHelmetFlap;

		@Override
		public void animate(AnimationProperties properties) {
			for (Object value : this.parts.values()) if (value instanceof InterpolatedBone bone) bone.reset();

			GnomadMogulEntity entity = (GnomadMogulEntity) properties.getProperty("entity");
			float ageInTicks = properties.getNumProperty("ageInTicks");
			float bodyYaw = properties.getNumProperty("bodyYaw");
			float netHeadYaw = properties.getNumProperty("netHeadYaw");
			float headPitch = properties.getNumProperty("headPitch");

			float globalSpeed = 1.0F;
			float globalHeight = 1.0F;
			float globalDegree = 1.0F;

			MogulRoot.rotateDeg(bodyYaw, Direction.Axis.Y);

			MogulFrontRobe.shouldRender = true;
			MogulRightRobe.shouldRender = true;
			MogulBackRobe.shouldRender  = true;
			MogulLeftRobe.shouldRender  = true;

			float legBobAmountH = 12;
			float legBobAmountV = 12;
			float timeOffset = Mth.PI * 0.5F;
			float verticalOffset = 0F;

			Vector3fc walkVector = entity.getWalkVector(1.0F);
			float f = entity.getCumulativeWalk() * 2.0F;
			float f1 = walkVector.length() * 5.0F;

			float walkDirZ = entity.getWalkAmount(1.0F);
			float walkDirX = -entity.getStrafeAmount(1.0F);
			if (walkVector.lengthSquared() > 0) {
				float length = walkVector.length();
				walkDirX /= length;
				walkDirZ /= length;
			}

			float strafeFactor = Mth.abs(walkDirX);
			float walkFactor = Mth.abs(walkDirZ);

			float previousFAmount = f1;
			// WALKING
			{
				f1 = previousFAmount * walkFactor;
				float sign = Mth.sign(walkDirZ);
				float legHeightOffset = 0.35F * legBobAmountV * f1 * globalDegree;

				MogulLeftLeg.z += Mth.cos(f + timeOffset) * legBobAmountH * f1 * globalDegree * sign;
				float leftLegY = (Mth.sin(f + timeOffset) - verticalOffset) * legBobAmountV * f1 * globalDegree;
				if (leftLegY < 0) leftLegY *= 0.5F;
				MogulLeftLeg.y += leftLegY + legHeightOffset;
				MogulLeftLeg.rotateDeg(-30 * f1, Direction.Axis.X);
				MogulLeftLeg.rotateDeg(Mth.sin(f + timeOffset) * f1 * globalDegree * -20 * sign, Direction.Axis.X);
				MogulLeftLeg.rotateDeg(Mth.cos(f) * f1 * globalDegree * -20, Direction.Axis.X);

				MogulRightLeg.z += Mth.sin(f) * legBobAmountH * f1 * globalDegree * sign;
				float rightLegY = (-Mth.cos(f) - verticalOffset) * legBobAmountV * f1 * globalDegree;
				if (rightLegY < 0) rightLegY *= 0.5F;
				MogulRightLeg.y += rightLegY + legHeightOffset;
				MogulRightLeg.rotateDeg(-30 * f1, Direction.Axis.X);
				MogulRightLeg.rotateDeg(Mth.sin(f + timeOffset) * f1 * globalDegree * 20 * sign, Direction.Axis.X);
				MogulRightLeg.rotateDeg(Mth.cos(f) * f1 * globalDegree * 20, Direction.Axis.X);

				MogulLeftArm.rotateDeg(-58 * f1 * (-sign * 0.5F + 0.5F), Direction.Axis.X);
				MogulLeftArm.rotateDeg(Mth.sin(f + timeOffset) * f1 * globalDegree * 20 * sign, Direction.Axis.X);
				MogulLeftArm.rotateDeg(Mth.cos(f) * f1 * globalDegree * 20 * sign, Direction.Axis.X);
				float armCircleTime = f + (Mth.HALF_PI - 1);
				MogulLeftArm.z += Mth.cos(armCircleTime) * f1 * globalDegree * 5 * sign - 1 * f1 * globalDegree;

				MogulRightArm.rotateDeg(-58 * f1 * (-sign * 0.5F + 0.5F), Direction.Axis.X);
				MogulRightArm.rotateDeg(Mth.sin(f + timeOffset) * f1 * globalDegree * -20 * sign, Direction.Axis.X);
				MogulRightArm.rotateDeg(Mth.cos(f) * f1 * globalDegree * -20 * sign, Direction.Axis.X);
				MogulRightArm.z += Mth.sin(armCircleTime) * f1 * globalDegree * 5 * sign - 1 * f1 * globalDegree;

				MogulRoot.y += Mth.sin(f * 2) * f1 * globalDegree;
				MogulRoot.z += Mth.sin(f * 2 - 1F) * f1 * globalDegree * 2 * sign;
				MogulRoot.rotateDeg(-10 * f1 * globalDegree * sign, Direction.Axis.X);

				MogulFrontRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -30, -10) * f1 * globalDegree * sign, Direction.Axis.X);
				MogulBackRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -10, -30) * f1 * globalDegree * sign, Direction.Axis.X);

				MogulNeck.rotateDeg(10 * f1 * globalDegree * sign, Direction.Axis.X);
			}
			// STRAFING
			{
				f1 = previousFAmount * strafeFactor;
				float sign = -walkDirX;
				float legHeightOffset = -0.2F * legBobAmountV * f1 * globalDegree;
				float sideIntensity = 0.2F;
				MogulLeftLeg.x += Mth.cos(f + timeOffset) * legBobAmountH * f1 * globalDegree * 0.5F * sign;
				float leftLegY = (-Mth.sin(f + timeOffset) - verticalOffset) * legBobAmountV * f1 * globalDegree;
				if (leftLegY < 0) leftLegY *= 0.2F;
				MogulLeftLeg.y += leftLegY + legHeightOffset;
				MogulLeftLeg.rotateDeg(-30 * f1 * sign, Direction.Axis.Z);
				MogulLeftLeg.rotateDeg(Mth.sin(f + timeOffset) * f1 * globalDegree * -20 * sideIntensity * sign, Direction.Axis.Z);
				MogulLeftLeg.rotateDeg(Mth.cos(f) * f1 * globalDegree * -20 * sideIntensity * sign, Direction.Axis.Z);

				MogulRightLeg.x += Mth.sin(f) * legBobAmountH * f1 * globalDegree * 0.5F * sign;
				float rightLegY =  (Mth.cos(f) - verticalOffset) * legBobAmountV * f1 * globalDegree;
				if (rightLegY < 0) rightLegY *= 0.2F;
				MogulRightLeg.y += rightLegY + legHeightOffset;
				MogulRightLeg.rotateDeg(-30 * f1 * sign, Direction.Axis.Z);
				MogulRightLeg.rotateDeg(Mth.sin(f + timeOffset) * f1 * globalDegree * 20 * sideIntensity * sign, Direction.Axis.Z);
				MogulRightLeg.rotateDeg(Mth.cos(f) * f1 * globalDegree * 20 * sideIntensity * sign, Direction.Axis.Z);

				MogulRoot.rotateDeg(-10 * f1 * globalDegree * sign, Direction.Axis.Z);

				MogulRoot.y += Mth.sin(f * 2) * f1 * globalDegree;
				MogulRoot.x += Mth.sin(f * 2 - 1F) * f1 * globalDegree * -2 * sign;
				MogulRoot.rotateDeg(-10 * f1 * globalDegree, Direction.Axis.X);

				//MogulLeftRobe .rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -5, 10) * f1 * globalDegree, Direction.Axis.X);
				MogulLeftRobe .rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -5, 20) * f1 * globalDegree, Direction.Axis.X);
				MogulLeftRobe .rotateDeg(Mth.cos(f - 0.3F) * f1 * globalDegree * 10 * (sign*0.5F + 0.5F), Direction.Axis.X);
				MogulRightRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, 20, -5) * f1 * globalDegree, Direction.Axis.X);
				MogulRightRobe.rotateDeg(Mth.sin(f + Mth.HALF_PI + 0.3F) * f1 * globalDegree * 10 * (-sign*0.5F + 0.5F), Direction.Axis.X);

				MogulLeftArm .rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -20, -10) * f1 * globalDegree, Direction.Axis.Z);
				MogulRightArm.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, 10, 20) * f1 * globalDegree, Direction.Axis.Z);

				MogulNeck.rotateDeg(10 * f1 * globalDegree * sign, Direction.Axis.Z);
				}
			f1 = previousFAmount;

			// IDLE
			MogulRightArm.y += Mth.sin(ageInTicks * globalSpeed * 0.02F) * globalDegree * 0.5F;
			MogulLeftArm.y += Mth.sin(ageInTicks * globalSpeed * 0.02F) * globalDegree * 0.5F;

			MogulLeftArm.rotateDeg(Mth.sin(ageInTicks * globalSpeed * 0.055F) * globalDegree * 1.0F, Direction.Axis.X);
			MogulLeftArm.rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.06F) * globalDegree * 1.0F, Direction.Axis.Z);
			MogulRightArm.rotateDeg(Mth.sin(ageInTicks * globalSpeed * 0.062F) * globalDegree * 1.0F, Direction.Axis.X);
			MogulRightArm.rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.059F) * globalDegree * 1.0F, Direction.Axis.Z);

			MogulNeck.rotateDeg(Mth.sin(ageInTicks * globalSpeed * 0.062F) * globalDegree * 1.0F, Direction.Axis.X);
			MogulNeck.rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.059F) * globalDegree * 1.0F, Direction.Axis.Z);

			MogulLeftHelmetFlap .rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.06F) * globalDegree * 3.0F - 8.0F, Direction.Axis.X);
			MogulRightHelmetFlap.rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.061F) * globalDegree * 3.0F - 8.0F, Direction.Axis.X);

			MogulLeftRobe .rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.059F) * globalDegree * 1.5F, Direction.Axis.X);
			MogulRightRobe.rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.060F) * globalDegree * 1.5F, Direction.Axis.X);
			MogulFrontRobe.rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.061F) * globalDegree * 1.5F, Direction.Axis.X);
			MogulBackRobe .rotateDeg(Mth.cos(ageInTicks * globalSpeed * 0.062F) * globalDegree * 1.5F, Direction.Axis.X);

			MogulNeck.rotateDeg(netHeadYaw * 0.5F, Direction.Axis.Y);
			MogulHead.rotateDeg(netHeadYaw * 0.5F, Direction.Axis.Y);
			MogulNeck.rotateDeg(headPitch * 0.5F, Direction.Axis.X);
			MogulHead.rotateDeg(headPitch * 0.5F, Direction.Axis.X);
		}
	}
}