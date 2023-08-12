package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.ModelFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.common.world.entity.MudScarabEntity;
import birsy.clinker.core.util.Quaternionf;

public class MudScarabModelFactory implements ModelFactory {
	private final ModelMesh[] meshes = new ModelMesh[25];
	
	public MudScarabModelFactory() {
		StaticMesh mesh0 = new StaticMesh();
		mesh0.addCube(1F, 1F, 1F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[0] = mesh0;
		
		StaticMesh mesh1 = new StaticMesh();
		mesh1.addCube(0F, 0F, 0F, 0F, 6.5F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[1] = mesh1;
		
		StaticMesh mesh2 = new StaticMesh();
		mesh2.addCube(0F, 0F, 0F, 0F, 7.5F, 1F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[2] = mesh2;
		
		StaticMesh mesh3 = new StaticMesh();
		mesh3.addCube(25F, 23F, 25F, -12.5F, 7.5F, -11.5F, 0F, 0F, 0F, 0F, 69F, false);
		meshes[3] = mesh3;
		
		StaticMesh mesh4 = new StaticMesh();
		mesh4.addCube(21F, 11F, 12F, -10.5F, 15.5F, -23.5F, 0F, 0F, 0F, 0F, 46F, false);
		meshes[4] = mesh4;
		
		StaticMesh mesh5 = new StaticMesh();
		mesh5.addCube(21F, 19F, 7F, -10.5F, 9.5F, 13.5F, 0F, 0F, 0F, 0F, 20F, false);
		meshes[5] = mesh5;
		
		StaticMesh mesh6 = new StaticMesh();
		mesh6.addCube(14F, 7F, 13F, -7F, 7.5F, -25.5F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[6] = mesh6;
		
		StaticMesh mesh7 = new StaticMesh();
		mesh7.addCube(1F, 3F, 4F, -5.5F, 10.5F, -29.5F, 0F, 0F, 0F, 0F, 0F, true);
		meshes[7] = mesh7;
		
		StaticMesh mesh8 = new StaticMesh();
		mesh8.addCube(1F, 3F, 4F, 4.5F, 10.5F, -29.5F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[8] = mesh8;
		
		StaticMesh mesh9 = new StaticMesh();
		mesh9.addCube(3F, 3F, 5F, 3.5F, 4.5F, -27.5F, 0F, 0F, 0F, 41F, 0F, true);
		meshes[9] = mesh9;
		
		StaticMesh mesh10 = new StaticMesh();
		mesh10.addCube(3F, 3F, 5F, -6.5F, 4.5F, -27.5F, 0F, 0F, 0F, 41F, 0F, false);
		meshes[10] = mesh10;
		
		StaticMesh mesh11 = new StaticMesh();
		mesh11.addCube(0F, 0F, 0F, 0F, 6.5F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[11] = mesh11;
		
		StaticMesh mesh12 = new StaticMesh();
		mesh12.addCube(3F, 12F, 4F, 5.5F, -3.5F, -2F, 0F, 0F, 0F, 75F, 46F, true);
		meshes[12] = mesh12;
		
		StaticMesh mesh13 = new StaticMesh();
		mesh13.addCube(2F, 10F, 2F, 6F, -13.5F, -0.5F, 0F, 0F, 0F, 75F, 62F, true);
		meshes[13] = mesh13;
		
		StaticMesh mesh14 = new StaticMesh();
		mesh14.addCube(3F, 12F, 4F, 5.5F, -3.5F, 7.5F, 0F, 0F, 0F, 75F, 46F, false);
		meshes[14] = mesh14;
		
		StaticMesh mesh15 = new StaticMesh();
		mesh15.addCube(2F, 10F, 2F, 6F, -13.5F, 9F, 0F, 0F, 0F, 75F, 62F, false);
		meshes[15] = mesh15;
		
		StaticMesh mesh16 = new StaticMesh();
		mesh16.addCube(3F, 12F, 4F, 5.5F, -3.5F, -11.5F, 0F, 0F, 0F, 75F, 46F, true);
		meshes[16] = mesh16;
		
		StaticMesh mesh17 = new StaticMesh();
		mesh17.addCube(2F, 10F, 2F, 6F, -13.5F, -10F, 0F, 0F, 0F, 75F, 62F, true);
		meshes[17] = mesh17;
		
		StaticMesh mesh18 = new StaticMesh();
		mesh18.addCube(3F, 12F, 4F, -8.5F, -3.5F, -2F, 0F, 0F, 0F, 75F, 46F, false);
		meshes[18] = mesh18;
		
		StaticMesh mesh19 = new StaticMesh();
		mesh19.addCube(2F, 10F, 2F, -8F, -13.5F, -0.5F, 0F, 0F, 0F, 75F, 62F, false);
		meshes[19] = mesh19;
		
		StaticMesh mesh20 = new StaticMesh();
		mesh20.addCube(3F, 12F, 4F, -8.5F, -3.5F, -11.5F, 0F, 0F, 0F, 75F, 46F, false);
		meshes[20] = mesh20;
		
		StaticMesh mesh21 = new StaticMesh();
		mesh21.addCube(2F, 10F, 2F, -8F, -13.5F, -10F, 0F, 0F, 0F, 75F, 62F, false);
		meshes[21] = mesh21;
		
		StaticMesh mesh22 = new StaticMesh();
		mesh22.addCube(3F, 12F, 4F, -8.5F, -3.5F, 7.5F, 0F, 0F, 0F, 75F, 46F, true);
		meshes[22] = mesh22;
		
		StaticMesh mesh23 = new StaticMesh();
		mesh23.addCube(2F, 10F, 2F, -8F, -13.5F, 9F, 0F, 0F, 0F, 75F, 62F, true);
		meshes[23] = mesh23;
		
		StaticMesh mesh24 = new StaticMesh();
		mesh24.addCube(13F, 37F, 9F, -6.5F, -12F, 0F, 0F, 0F, 0F, 56F, 0F, false);
		meshes[24] = mesh24;
		
	}
	
	public InterpolatedSkeleton create() {
		MudScarabModel model = new MudScarabModel();
		InterpolatedBone rootJointBone = new InterpolatedBone("rootJoint");
		rootJointBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(rootJointBone, meshes[0]);
		model.rootJoint = rootJointBone;
		
		InterpolatedBone bodyJointBone = new InterpolatedBone("bodyJoint");
		bodyJointBone.setInitialTransform(0F, 6.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(bodyJointBone, meshes[1]);
		model.bodyJoint = bodyJointBone;
		
		InterpolatedBone shellJointBone = new InterpolatedBone("shellJoint");
		shellJointBone.setInitialTransform(0F, 7.5F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(shellJointBone, meshes[2]);
		model.shellJoint = shellJointBone;
		
		InterpolatedBone middleShellBone = new InterpolatedBone("middleShell");
		middleShellBone.setInitialTransform(0F, 7.5F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(middleShellBone, meshes[3]);
		model.middleShell = middleShellBone;
		
		InterpolatedBone frontShellBone = new InterpolatedBone("frontShell");
		frontShellBone.setInitialTransform(0F, 19.5F, -11.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(frontShellBone, meshes[4]);
		model.frontShell = frontShellBone;
		
		InterpolatedBone backShellBone = new InterpolatedBone("backShell");
		backShellBone.setInitialTransform(0F, 12.5F, 13.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(backShellBone, meshes[5]);
		model.backShell = backShellBone;
		
		InterpolatedBone scarabHeadBone = new InterpolatedBone("scarabHead");
		scarabHeadBone.setInitialTransform(0F, 10F, -16.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(scarabHeadBone, meshes[6]);
		model.scarabHead = scarabHeadBone;
		
		InterpolatedBone scarabLeftAntennaeBone = new InterpolatedBone("scarabLeftAntennae");
		scarabLeftAntennaeBone.setInitialTransform(-5F, 13F, -25.5F, new Quaternionf().rotationZYX(0F, 22.5F, 0F));
		model.addBone(scarabLeftAntennaeBone, meshes[7]);
		model.scarabLeftAntennae = scarabLeftAntennaeBone;
		
		InterpolatedBone scarabRightAntennaeBone = new InterpolatedBone("scarabRightAntennae");
		scarabRightAntennaeBone.setInitialTransform(5F, 13F, -25.5F, new Quaternionf().rotationZYX(0F, -22.5F, 0F));
		model.addBone(scarabRightAntennaeBone, meshes[8]);
		model.scarabRightAntennae = scarabRightAntennaeBone;
		
		InterpolatedBone scarabRightJawBone = new InterpolatedBone("scarabRightJaw");
		scarabRightJawBone.setInitialTransform(5.5F, 7.5F, -23.5F, new Quaternionf().rotationZYX(0F, 22.5F, 0F));
		model.addBone(scarabRightJawBone, meshes[9]);
		model.scarabRightJaw = scarabRightJawBone;
		
		InterpolatedBone scarabLeftJawBone = new InterpolatedBone("scarabLeftJaw");
		scarabLeftJawBone.setInitialTransform(-5.5F, 7.5F, -23.5F, new Quaternionf().rotationZYX(0F, -22.5F, 0F));
		model.addBone(scarabLeftJawBone, meshes[10]);
		model.scarabLeftJaw = scarabLeftJawBone;
		
		InterpolatedBone legsJointBone = new InterpolatedBone("legsJoint");
		legsJointBone.setInitialTransform(0F, 6.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(legsJointBone, meshes[11]);
		model.legsJoint = legsJointBone;
		
		InterpolatedBone scarabMiddleRightUpperLegBone = new InterpolatedBone("scarabMiddleRightUpperLeg");
		scarabMiddleRightUpperLegBone.setInitialTransform(7F, 8.5F, 0F, new Quaternionf().rotationZYX(60F, -100F, 0F));
		model.addBone(scarabMiddleRightUpperLegBone, meshes[12]);
		model.scarabMiddleRightUpperLeg = scarabMiddleRightUpperLegBone;
		
		InterpolatedBone scarabMiddleRightLowerLegBone = new InterpolatedBone("scarabMiddleRightLowerLeg");
		scarabMiddleRightLowerLegBone.setInitialTransform(7F, -3.5F, 1.5F, new Quaternionf().rotationZYX(24.64F, 0F, 0F));
		model.addBone(scarabMiddleRightLowerLegBone, meshes[13]);
		model.scarabMiddleRightLowerLeg = scarabMiddleRightLowerLegBone;
		
		InterpolatedBone scarabBackRightUpperLegBone = new InterpolatedBone("scarabBackRightUpperLeg");
		scarabBackRightUpperLegBone.setInitialTransform(7F, 8.5F, 9.5F, new Quaternionf().rotationZYX(60F, -160F, 0F));
		model.addBone(scarabBackRightUpperLegBone, meshes[14]);
		model.scarabBackRightUpperLeg = scarabBackRightUpperLegBone;
		
		InterpolatedBone scarabBackRightLowerLegBone = new InterpolatedBone("scarabBackRightLowerLeg");
		scarabBackRightLowerLegBone.setInitialTransform(7F, -3.5F, 11F, new Quaternionf().rotationZYX(24.64F, 0F, 0F));
		model.addBone(scarabBackRightLowerLegBone, meshes[15]);
		model.scarabBackRightLowerLeg = scarabBackRightLowerLegBone;
		
		InterpolatedBone scarabFrontRightUpperLegBone = new InterpolatedBone("scarabFrontRightUpperLeg");
		scarabFrontRightUpperLegBone.setInitialTransform(7F, 8.5F, -9.5F, new Quaternionf().rotationZYX(60F, -40F, 0F));
		model.addBone(scarabFrontRightUpperLegBone, meshes[16]);
		model.scarabFrontRightUpperLeg = scarabFrontRightUpperLegBone;
		
		InterpolatedBone scarabFrontRightLowerLegBone = new InterpolatedBone("scarabFrontRightLowerLeg");
		scarabFrontRightLowerLegBone.setInitialTransform(7F, -3.5F, -8F, new Quaternionf().rotationZYX(0F, 0F, -40F));
		model.addBone(scarabFrontRightLowerLegBone, meshes[17]);
		model.scarabFrontRightLowerLeg = scarabFrontRightLowerLegBone;
		
		InterpolatedBone scarabMiddleLeftUpperLegBone = new InterpolatedBone("scarabMiddleLeftUpperLeg");
		scarabMiddleLeftUpperLegBone.setInitialTransform(-7F, 8.5F, 0F, new Quaternionf().rotationZYX(60F, 100F, 0F));
		model.addBone(scarabMiddleLeftUpperLegBone, meshes[18]);
		model.scarabMiddleLeftUpperLeg = scarabMiddleLeftUpperLegBone;
		
		InterpolatedBone scarabMiddleLeftLowerLegBone = new InterpolatedBone("scarabMiddleLeftLowerLeg");
		scarabMiddleLeftLowerLegBone.setInitialTransform(-7F, -3.5F, 1.5F, new Quaternionf().rotationZYX(24.64F, 0F, 0F));
		model.addBone(scarabMiddleLeftLowerLegBone, meshes[19]);
		model.scarabMiddleLeftLowerLeg = scarabMiddleLeftLowerLegBone;
		
		InterpolatedBone scarabFrontLeftUpperLegBone = new InterpolatedBone("scarabFrontLeftUpperLeg");
		scarabFrontLeftUpperLegBone.setInitialTransform(-7F, 8.5F, -9.5F, new Quaternionf().rotationZYX(60F, 40F, 0F));
		model.addBone(scarabFrontLeftUpperLegBone, meshes[20]);
		model.scarabFrontLeftUpperLeg = scarabFrontLeftUpperLegBone;
		
		InterpolatedBone scarabFrontLeftLowerLegBone = new InterpolatedBone("scarabFrontLeftLowerLeg");
		scarabFrontLeftLowerLegBone.setInitialTransform(-7F, -3.5F, -8F, new Quaternionf().rotationZYX(0F, 0F, 40F));
		model.addBone(scarabFrontLeftLowerLegBone, meshes[21]);
		model.scarabFrontLeftLowerLeg = scarabFrontLeftLowerLegBone;
		
		InterpolatedBone scarabBackLeftUpperLegBone = new InterpolatedBone("scarabBackLeftUpperLeg");
		scarabBackLeftUpperLegBone.setInitialTransform(-7F, 8.5F, 9.5F, new Quaternionf().rotationZYX(60F, 160F, 0F));
		model.addBone(scarabBackLeftUpperLegBone, meshes[22]);
		model.scarabBackLeftUpperLeg = scarabBackLeftUpperLegBone;
		
		InterpolatedBone scarabBackLeftLowerLegBone = new InterpolatedBone("scarabBackLeftLowerLeg");
		scarabBackLeftLowerLegBone.setInitialTransform(-7F, -3.5F, 11F, new Quaternionf().rotationZYX(24.64F, 0F, 0F));
		model.addBone(scarabBackLeftLowerLegBone, meshes[23]);
		model.scarabBackLeftLowerLeg = scarabBackLeftLowerLegBone;
		
		InterpolatedBone scarabBodyBone = new InterpolatedBone("scarabBody");
		scarabBodyBone.setInitialTransform(0F, 6.5F, 0F, new Quaternionf().rotationZYX(-90F, 0F, 0F));
		model.addBone(scarabBodyBone, meshes[24]);
		model.scarabBody = scarabBodyBone;
		
		rootJointBone.addChild(bodyJointBone);
		bodyJointBone.addChild(shellJointBone);
		bodyJointBone.addChild(scarabHeadBone);
		bodyJointBone.addChild(legsJointBone);
		bodyJointBone.addChild(scarabBodyBone);
		shellJointBone.addChild(middleShellBone);
		shellJointBone.addChild(frontShellBone);
		shellJointBone.addChild(backShellBone);
		scarabHeadBone.addChild(scarabLeftAntennaeBone);
		scarabHeadBone.addChild(scarabRightAntennaeBone);
		scarabHeadBone.addChild(scarabRightJawBone);
		scarabHeadBone.addChild(scarabLeftJawBone);
		legsJointBone.addChild(scarabMiddleRightUpperLegBone);
		legsJointBone.addChild(scarabBackRightUpperLegBone);
		legsJointBone.addChild(scarabFrontRightUpperLegBone);
		legsJointBone.addChild(scarabMiddleLeftUpperLegBone);
		legsJointBone.addChild(scarabFrontLeftUpperLegBone);
		legsJointBone.addChild(scarabBackLeftUpperLegBone);
		scarabMiddleRightUpperLegBone.addChild(scarabMiddleRightLowerLegBone);
		scarabBackRightUpperLegBone.addChild(scarabBackRightLowerLegBone);
		scarabFrontRightUpperLegBone.addChild(scarabFrontRightLowerLegBone);
		scarabMiddleLeftUpperLegBone.addChild(scarabMiddleLeftLowerLegBone);
		scarabFrontLeftUpperLegBone.addChild(scarabFrontLeftLowerLegBone);
		scarabBackLeftUpperLegBone.addChild(scarabBackLeftLowerLegBone);
		model.buildRoots();
		return model;
	}
	
	public static class MudScarabModel extends InterpolatedSkeleton<MudScarabEntity> {
		protected InterpolatedBone rootJoint;
		protected InterpolatedBone bodyJoint;
		protected InterpolatedBone shellJoint;
		protected InterpolatedBone middleShell;
		protected InterpolatedBone frontShell;
		protected InterpolatedBone backShell;
		protected InterpolatedBone scarabHead;
		protected InterpolatedBone scarabLeftAntennae;
		protected InterpolatedBone scarabRightAntennae;
		protected InterpolatedBone scarabRightJaw;
		protected InterpolatedBone scarabLeftJaw;
		protected InterpolatedBone legsJoint;
		protected InterpolatedBone scarabMiddleRightUpperLeg;
		protected InterpolatedBone scarabMiddleRightLowerLeg;
		protected InterpolatedBone scarabBackRightUpperLeg;
		protected InterpolatedBone scarabBackRightLowerLeg;
		protected InterpolatedBone scarabFrontRightUpperLeg;
		protected InterpolatedBone scarabFrontRightLowerLeg;
		protected InterpolatedBone scarabMiddleLeftUpperLeg;
		protected InterpolatedBone scarabMiddleLeftLowerLeg;
		protected InterpolatedBone scarabFrontLeftUpperLeg;
		protected InterpolatedBone scarabFrontLeftLowerLeg;
		protected InterpolatedBone scarabBackLeftUpperLeg;
		protected InterpolatedBone scarabBackLeftLowerLeg;
		protected InterpolatedBone scarabBody;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}