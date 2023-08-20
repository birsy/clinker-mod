package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.constraint.Constraint;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.ModelFactory;
import birsy.clinker.client.model.base.constraint.InverseKinematicsConstraint;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.core.util.Quaternionf;
import net.minecraft.core.Direction;

import java.util.Collections;

public class MudScarabModelFactory implements ModelFactory {
	private final ModelMesh[] meshes = new ModelMesh[25];
	
	public MudScarabModelFactory() {
		int texWidth = 128;
		int texHeight = 128;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		meshes[0] = mesh0;
		
		StaticMesh mesh1 = new StaticMesh(texWidth, texHeight);
		meshes[1] = mesh1;
		
		StaticMesh mesh2 = new StaticMesh(texWidth, texHeight);
		meshes[2] = mesh2;
		
		StaticMesh mesh3 = new StaticMesh(texWidth, texHeight);
		mesh3.addCube(25F, 23F, 25F, -12.5F, 0F, -12.5F, 0F, 0F, 0F, 0F, 69F, false);
		meshes[3] = mesh3;
		
		StaticMesh mesh4 = new StaticMesh(texWidth, texHeight);
		mesh4.addCube(21F, 11F, 12F, -10.5F, -4F, -12F, 0F, 0F, 0F, 0F, 46F, false);
		meshes[4] = mesh4;
		
		StaticMesh mesh5 = new StaticMesh(texWidth, texHeight);
		mesh5.addCube(21F, 19F, 7F, -10.5F, -3F, 0F, 0F, 0F, 0F, 0F, 20F, false);
		meshes[5] = mesh5;
		
		StaticMesh mesh6 = new StaticMesh(texWidth, texHeight);
		mesh6.addCube(14F, 7F, 13F, -7F, -2.5F, -9F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[6] = mesh6;
		
		StaticMesh mesh7 = new StaticMesh(texWidth, texHeight);
		mesh7.addCube(1F, 3F, 4F, -0.5F, -2.5F, -4F, 0F, 0F, 0F, 0F, 0F, true);
		meshes[7] = mesh7;
		
		StaticMesh mesh8 = new StaticMesh(texWidth, texHeight);
		mesh8.addCube(1F, 3F, 4F, -0.5F, -2.5F, -4F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[8] = mesh8;
		
		StaticMesh mesh9 = new StaticMesh(texWidth, texHeight);
		mesh9.addCube(3F, 3F, 5F, -2F, -3F, -4F, 0F, 0F, 0F, 41F, 0F, true);
		meshes[9] = mesh9;
		
		StaticMesh mesh10 = new StaticMesh(texWidth, texHeight);
		mesh10.addCube(3F, 3F, 5F, -1F, -3F, -4F, 0F, 0F, 0F, 41F, 0F, false);
		meshes[10] = mesh10;
		
		StaticMesh mesh11 = new StaticMesh(texWidth, texHeight);
		meshes[11] = mesh11;
		
		StaticMesh mesh12 = new StaticMesh(texWidth, texHeight);
		mesh12.addCube(3F, 12F, 4F, -1.5F, -12F, -2F, 0F, 0F, 0F, 75F, 46F, true);
		meshes[12] = mesh12;
		
		StaticMesh mesh13 = new StaticMesh(texWidth, texHeight);
		mesh13.addCube(2F, 10F, 2F, -1F, -10F, -2F, 0F, 0F, 0F, 75F, 62F, true);
		meshes[13] = mesh13;
		
		StaticMesh mesh14 = new StaticMesh(texWidth, texHeight);
		mesh14.addCube(3F, 12F, 4F, -1.5F, -12F, -2F, 0F, 0F, 0F, 75F, 46F, false);
		meshes[14] = mesh14;
		
		StaticMesh mesh15 = new StaticMesh(texWidth, texHeight);
		mesh15.addCube(2F, 10F, 2F, -1F, -10F, -2F, 0F, 0F, 0F, 75F, 62F, false);
		meshes[15] = mesh15;
		
		StaticMesh mesh16 = new StaticMesh(texWidth, texHeight);
		mesh16.addCube(3F, 12F, 4F, -1.5F, -12F, -2F, 0F, 0F, 0F, 75F, 46F, true);
		meshes[16] = mesh16;
		
		StaticMesh mesh17 = new StaticMesh(texWidth, texHeight);
		mesh17.addCube(2F, 10F, 2F, -1F, -10F, -2F, 0F, 0F, 0F, 75F, 62F, true);
		meshes[17] = mesh17;
		
		StaticMesh mesh18 = new StaticMesh(texWidth, texHeight);
		mesh18.addCube(3F, 12F, 4F, -1.5F, -12F, -2F, 0F, 0F, 0F, 75F, 46F, false);
		meshes[18] = mesh18;
		
		StaticMesh mesh19 = new StaticMesh(texWidth, texHeight);
		mesh19.addCube(2F, 10F, 2F, -1F, -10F, -2F, 0F, 0F, 0F, 75F, 62F, false);
		meshes[19] = mesh19;
		
		StaticMesh mesh20 = new StaticMesh(texWidth, texHeight);
		mesh20.addCube(3F, 12F, 4F, -1.5F, -12F, -2F, 0F, 0F, 0F, 75F, 46F, false);
		meshes[20] = mesh20;
		
		StaticMesh mesh21 = new StaticMesh(texWidth, texHeight);
		mesh21.addCube(2F, 10F, 2F, -1F, -10F, -2F, 0F, 0F, 0F, 75F, 62F, false);
		meshes[21] = mesh21;
		
		StaticMesh mesh22 = new StaticMesh(texWidth, texHeight);
		mesh22.addCube(3F, 12F, 4F, -1.5F, -12F, -2F, 0F, 0F, 0F, 75F, 46F, true);
		meshes[22] = mesh22;
		
		StaticMesh mesh23 = new StaticMesh(texWidth, texHeight);
		mesh23.addCube(2F, 10F, 2F, -1F, -10F, -2F, 0F, 0F, 0F, 75F, 62F, true);
		meshes[23] = mesh23;
		
		StaticMesh mesh24 = new StaticMesh(texWidth, texHeight);
		mesh24.addCube(13F, 37F, 9F, -6.5F, -18.5F, 0F, 0F, 0F, 0F, 56F, 0F, false);
		meshes[24] = mesh24;
		
	}
	
	public InterpolatedSkeleton create() {
		MudScarabModel model = new MudScarabModel();
		//0, 0, 0
		InterpolatedBone rootJointBone = new InterpolatedBone("rootJoint");
		rootJointBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(rootJointBone, meshes[0]);
		model.rootJoint = rootJointBone;
		
		//0, 6.5, 0
		//0 - 0 = 0   |   6.5 - 0 = 6.5   |   0 - 0 = 0
		InterpolatedBone bodyJointBone = new InterpolatedBone("bodyJoint");
		bodyJointBone.setInitialTransform(0F, 6.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(bodyJointBone, meshes[1]);
		model.bodyJoint = bodyJointBone;
		
		//0, 7.5, 1
		//0 - 0 = 0   |   7.5 - 6.5 = 1   |   1 - 0 = 1
		InterpolatedBone shellJointBone = new InterpolatedBone("shellJoint");
		shellJointBone.setInitialTransform(0F, 1F, 1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(shellJointBone, meshes[2]);
		model.shellJoint = shellJointBone;
		
		//0, 7.5, 1
		//0 - 0 = 0   |   7.5 - 7.5 = 0   |   1 - 1 = 0
		InterpolatedBone middleShellBone = new InterpolatedBone("middleShell");
		middleShellBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(middleShellBone, meshes[3]);
		model.middleShell = middleShellBone;
		
		//0, 19.5, -11.5
		//0 - 0 = 0   |   19.5 - 7.5 = 12   |   -11.5 - 1 = -12.5
		InterpolatedBone frontShellBone = new InterpolatedBone("frontShell");
		frontShellBone.setInitialTransform(0F, 12F, -12.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(frontShellBone, meshes[4]);
		model.frontShell = frontShellBone;
		
		//0, 12.5, 13.5
		//0 - 0 = 0   |   12.5 - 7.5 = 5   |   13.5 - 1 = 12.5
		InterpolatedBone backShellBone = new InterpolatedBone("backShell");
		backShellBone.setInitialTransform(0F, 5F, 12.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(backShellBone, meshes[5]);
		model.backShell = backShellBone;
		
		//0, 10, -16.5
		//0 - 0 = 0   |   10 - 6.5 = 3.5   |   -16.5 - 0 = -16.5
		InterpolatedBone scarabHeadBone = new InterpolatedBone("scarabHead");
		scarabHeadBone.setInitialTransform(0F, 3.5F, -16.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(scarabHeadBone, meshes[6]);
		model.scarabHead = scarabHeadBone;
		
		//-5, 13, -25.5
		//-5 - 0 = -5   |   13 - 10 = 3   |   -25.5 - -16.5 = -9
		InterpolatedBone scarabLeftAntennaeBone = new InterpolatedBone("scarabLeftAntennae");
		scarabLeftAntennaeBone.setInitialTransform(-5F, 3F, -9F, new Quaternionf().rotationZYX(0F, 0.392699081475F, 0F));
		model.addBone(scarabLeftAntennaeBone, meshes[7]);
		model.scarabLeftAntennae = scarabLeftAntennaeBone;
		
		//5, 13, -25.5
		//5 - 0 = 5   |   13 - 10 = 3   |   -25.5 - -16.5 = -9
		InterpolatedBone scarabRightAntennaeBone = new InterpolatedBone("scarabRightAntennae");
		scarabRightAntennaeBone.setInitialTransform(5F, 3F, -9F, new Quaternionf().rotationZYX(0F, -0.392699081475F, 0F));
		model.addBone(scarabRightAntennaeBone, meshes[8]);
		model.scarabRightAntennae = scarabRightAntennaeBone;
		
		//5.5, 7.5, -23.5
		//5.5 - 0 = 5.5   |   7.5 - 10 = -2.5   |   -23.5 - -16.5 = -7
		InterpolatedBone scarabRightJawBone = new InterpolatedBone("scarabRightJaw");
		scarabRightJawBone.setInitialTransform(5.5F, -2.5F, -7F, new Quaternionf().rotationZYX(0F, 0.392699081475F, 0F));
		model.addBone(scarabRightJawBone, meshes[9]);
		model.scarabRightJaw = scarabRightJawBone;
		
		//-5.5, 7.5, -23.5
		//-5.5 - 0 = -5.5   |   7.5 - 10 = -2.5   |   -23.5 - -16.5 = -7
		InterpolatedBone scarabLeftJawBone = new InterpolatedBone("scarabLeftJaw");
		scarabLeftJawBone.setInitialTransform(-5.5F, -2.5F, -7F, new Quaternionf().rotationZYX(0F, -0.392699081475F, 0F));
		model.addBone(scarabLeftJawBone, meshes[10]);
		model.scarabLeftJaw = scarabLeftJawBone;
		
		//0, 6.5, 0
		//0 - 0 = 0   |   6.5 - 6.5 = 0   |   0 - 0 = 0
		InterpolatedBone legsJointBone = new InterpolatedBone("legsJoint");
		legsJointBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(legsJointBone, meshes[11]);
		model.legsJoint = legsJointBone;
		
		//7, 8.5, 0
		//7 - 0 = 7   |   8.5 - 6.5 = 2   |   0 - 0 = 0
		InterpolatedBone scarabMiddleRightUpperLegBone = new InterpolatedBone("scarabMiddleRightUpperLeg");
		scarabMiddleRightUpperLegBone.setInitialTransform(7F, 2F, 0F, new Quaternionf().rotationZYX(0F, -1.745329251F, 1.0471975506F));
		model.addBone(scarabMiddleRightUpperLegBone, meshes[12]);
		model.scarabMiddleRightUpperLeg = scarabMiddleRightUpperLegBone;
		
		//7, -3.5, 1.5
		//7 - 7 = 0   |   -3.5 - 8.5 = -12   |   1.5 - 0 = 1.5
		InterpolatedBone scarabMiddleRightLowerLegBone = new InterpolatedBone("scarabMiddleRightLowerLeg");
		scarabMiddleRightLowerLegBone.setInitialTransform(0F, -12F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0.4300491274464F));
		model.addBone(scarabMiddleRightLowerLegBone, meshes[13]);
		model.scarabMiddleRightLowerLeg = scarabMiddleRightLowerLegBone;
		
		//7, 8.5, 9.5
		//7 - 0 = 7   |   8.5 - 6.5 = 2   |   9.5 - 0 = 9.5
		InterpolatedBone scarabBackRightUpperLegBone = new InterpolatedBone("scarabBackRightUpperLeg");
		scarabBackRightUpperLegBone.setInitialTransform(7F, 2F, 9.5F, new Quaternionf().rotationZYX(0F, -2.7925268015999998F, 1.0471975506F));
		model.addBone(scarabBackRightUpperLegBone, meshes[14]);
		model.scarabBackRightUpperLeg = scarabBackRightUpperLegBone;
		
		//7, -3.5, 11
		//7 - 7 = 0   |   -3.5 - 8.5 = -12   |   11 - 9.5 = 1.5
		InterpolatedBone scarabBackRightLowerLegBone = new InterpolatedBone("scarabBackRightLowerLeg");
		scarabBackRightLowerLegBone.setInitialTransform(0F, -12F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0.4300491274464F));
		model.addBone(scarabBackRightLowerLegBone, meshes[15]);
		model.scarabBackRightLowerLeg = scarabBackRightLowerLegBone;
		
		//7, 8.5, -9.5
		//7 - 0 = 7   |   8.5 - 6.5 = 2   |   -9.5 - 0 = -9.5
		InterpolatedBone scarabFrontRightUpperLegBone = new InterpolatedBone("scarabFrontRightUpperLeg");
		scarabFrontRightUpperLegBone.setInitialTransform(7F, 2F, -9.5F, new Quaternionf().rotationZYX(0F, -0.6981317003999999F, 1.0471975506F));
		model.addBone(scarabFrontRightUpperLegBone, meshes[16]);
		model.scarabFrontRightUpperLeg = scarabFrontRightUpperLegBone;
		
		//7, -3.5, -8
		//7 - 7 = 0   |   -3.5 - 8.5 = -12   |   -8 - -9.5 = 1.5
		InterpolatedBone scarabFrontRightLowerLegBone = new InterpolatedBone("scarabFrontRightLowerLeg");
		scarabFrontRightLowerLegBone.setInitialTransform(0F, -12F, 1.5F, new Quaternionf().rotationZYX(-0.6981317003999999F, 0F, 0F));
		model.addBone(scarabFrontRightLowerLegBone, meshes[17]);
		model.scarabFrontRightLowerLeg = scarabFrontRightLowerLegBone;
		
		//-7, 8.5, 0
		//-7 - 0 = -7   |   8.5 - 6.5 = 2   |   0 - 0 = 0
		InterpolatedBone scarabMiddleLeftUpperLegBone = new InterpolatedBone("scarabMiddleLeftUpperLeg");
		scarabMiddleLeftUpperLegBone.setInitialTransform(-7F, 2F, 0F, new Quaternionf().rotationZYX(0F, 1.745329251F, 1.0471975506F));
		model.addBone(scarabMiddleLeftUpperLegBone, meshes[18]);
		model.scarabMiddleLeftUpperLeg = scarabMiddleLeftUpperLegBone;
		
		//-7, -3.5, 1.5
		//-7 - -7 = 0   |   -3.5 - 8.5 = -12   |   1.5 - 0 = 1.5
		InterpolatedBone scarabMiddleLeftLowerLegBone = new InterpolatedBone("scarabMiddleLeftLowerLeg");
		scarabMiddleLeftLowerLegBone.setInitialTransform(0F, -12F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0.4300491274464F));
		model.addBone(scarabMiddleLeftLowerLegBone, meshes[19]);
		model.scarabMiddleLeftLowerLeg = scarabMiddleLeftLowerLegBone;
		
		//-7, 8.5, -9.5
		//-7 - 0 = -7   |   8.5 - 6.5 = 2   |   -9.5 - 0 = -9.5
		InterpolatedBone scarabFrontLeftUpperLegBone = new InterpolatedBone("scarabFrontLeftUpperLeg");
		scarabFrontLeftUpperLegBone.setInitialTransform(-7F, 2F, -9.5F, new Quaternionf().rotationZYX(0F, 0.6981317003999999F, 1.0471975506F));
		model.addBone(scarabFrontLeftUpperLegBone, meshes[20]);
		model.scarabFrontLeftUpperLeg = scarabFrontLeftUpperLegBone;
		
		//-7, -3.5, -8
		//-7 - -7 = 0   |   -3.5 - 8.5 = -12   |   -8 - -9.5 = 1.5
		InterpolatedBone scarabFrontLeftLowerLegBone = new InterpolatedBone("scarabFrontLeftLowerLeg");
		scarabFrontLeftLowerLegBone.setInitialTransform(0F, -12F, 1.5F, new Quaternionf().rotationZYX(0.6981317003999999F, 0F, 0F));
		model.addBone(scarabFrontLeftLowerLegBone, meshes[21]);
		model.scarabFrontLeftLowerLeg = scarabFrontLeftLowerLegBone;
		
		//-7, 8.5, 9.5
		//-7 - 0 = -7   |   8.5 - 6.5 = 2   |   9.5 - 0 = 9.5
		InterpolatedBone scarabBackLeftUpperLegBone = new InterpolatedBone("scarabBackLeftUpperLeg");
		scarabBackLeftUpperLegBone.setInitialTransform(-7F, 2F, 9.5F, new Quaternionf().rotationZYX(0F, 2.7925268015999998F, 1.0471975506F));
		model.addBone(scarabBackLeftUpperLegBone, meshes[22]);
		model.scarabBackLeftUpperLeg = scarabBackLeftUpperLegBone;
		
		//-7, -3.5, 11
		//-7 - -7 = 0   |   -3.5 - 8.5 = -12   |   11 - 9.5 = 1.5
		InterpolatedBone scarabBackLeftLowerLegBone = new InterpolatedBone("scarabBackLeftLowerLeg");
		scarabBackLeftLowerLegBone.setInitialTransform(0F, -12F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0.4300491274464F));
		model.addBone(scarabBackLeftLowerLegBone, meshes[23]);
		model.scarabBackLeftLowerLeg = scarabBackLeftLowerLegBone;
		
		//0, 6.5, 0
		//0 - 0 = 0   |   6.5 - 6.5 = 0   |   0 - 0 = 0
		InterpolatedBone scarabBodyBone = new InterpolatedBone("scarabBody");
		scarabBodyBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, -1.5707963259F));
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

		InverseKinematicsConstraint backLeftIK = new InverseKinematicsConstraint(scarabBackLeftLowerLegBone, 2, 1, 10, 1, 0.05F);
		backLeftIK.target.set(0, -13.5F, 0);
		backLeftIK.poleTarget.set(-22F, -10, 0);
		InverseKinematicsConstraint middleLeftIK = new InverseKinematicsConstraint(scarabMiddleLeftLowerLegBone, 2, 1, 10, 1, 0.05F);
		middleLeftIK.target.set(0, -13.5F, 0);
		middleLeftIK.poleTarget.set(-22F, -10, 0);
		InverseKinematicsConstraint frontLeftIK = new InverseKinematicsConstraint(scarabFrontLeftLowerLegBone, 2, 1, 10, 1, 0.05F);
		frontLeftIK.target.set(0, -13.5F, 0);
		frontLeftIK.poleTarget.set(-22F, -10, 0);
		InverseKinematicsConstraint backRightIK = new InverseKinematicsConstraint(scarabBackRightLowerLegBone, 2, 1, 10, 1, 0.05F);
		backRightIK.target.set(0, -13.5F, 0);
		backRightIK.poleTarget.set(22F, -10, 0);
		InverseKinematicsConstraint middleRightIK = new InverseKinematicsConstraint(scarabMiddleRightLowerLegBone, 2, 1, 10, 1, 0.05F);
		middleRightIK.target.set(0, -13.5F, 0);
		middleRightIK.poleTarget.set(22F, -10, 0);
		InverseKinematicsConstraint frontRightIK = new InverseKinematicsConstraint(scarabFrontRightLowerLegBone, 2, 1, 10, 1, 0.05F);
		frontRightIK.target.set(0, -13.5F, 0);
		frontRightIK.poleTarget.set(22F, -10, 0);

		Collections.addAll(model.constraints, backLeftIK, middleLeftIK, frontLeftIK, backRightIK, middleRightIK, frontRightIK);

		return model;
	}
	
	public static class MudScarabModel extends InterpolatedSkeleton {
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
			for (Object value : this.parts.values()) {
				if (value instanceof InterpolatedBone bone) {
					bone.reset();
				}
			}

			float f = properties.getNumProperty("limbSwing");
			float f1 = 1.1F * properties.getNumProperty("limbSwingAmount");
			float ageInTicks = properties.getNumProperty("ageInTicks");
			float bodyYaw = properties.getNumProperty("bodyYaw");
			float netHeadYaw = properties.getNumProperty("netHeadYaw");
			float headPitch = properties.getNumProperty("headPitch");
			float globalSpeed = 1.25F;
			float globalHeight = 1.0F;
			float globalDegree = 1.85F;

			this.rootJoint.rotate((float) Math.toRadians(bodyYaw), Direction.Axis.Y);
			AnimFunctions.look(scarabHead, netHeadYaw, headPitch, 1.0F, 1.0F);
		}
	}
}