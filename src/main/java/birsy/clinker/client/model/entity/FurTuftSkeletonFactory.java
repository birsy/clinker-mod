package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.SkeletonFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.client.model.base.AnimationProperties;
import org.joml.Quaternionf;

public class FurTuftSkeletonFactory implements SkeletonFactory {
	private final ModelMesh[] hairMeshes = new ModelMesh[7];
	
	public FurTuftSkeletonFactory() {
		int texWidth = 128;
		int texHeight = 64;
		StaticMesh hairMesh0 = new StaticMesh(texWidth, texHeight);
		hairMesh0.addCube(3F, 4F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 122F, 0F, false);
		hairMeshes[0] = hairMesh0;
		
		StaticMesh hairMesh1 = new StaticMesh(texWidth, texHeight);
		hairMesh1.addCube(3F, 4F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 122F, 4F, false);
		hairMeshes[1] = hairMesh1;
		
		StaticMesh hairMesh2 = new StaticMesh(texWidth, texHeight);
		hairMesh2.addCube(3F, 4F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 122F, 8F, false);
		hairMeshes[2] = hairMesh2;
		
		StaticMesh hairMesh3 = new StaticMesh(texWidth, texHeight);
		hairMesh3.addCube(3F, 4F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 122F, 12F, false);
		hairMeshes[3] = hairMesh3;
		
		StaticMesh hairMesh4 = new StaticMesh(texWidth, texHeight);
		hairMesh4.addCube(3F, 4F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 122F, 16F, false);
		hairMeshes[4] = hairMesh4;
		
		StaticMesh hairMesh5 = new StaticMesh(texWidth, texHeight);
		hairMesh5.addCube(3F, 4F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 122F, 20F, false);
		hairMeshes[5] = hairMesh5;
		
		StaticMesh hairMesh6 = new StaticMesh(texWidth, texHeight);
		hairMesh6.addCube(3F, 4F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 122F, 24F, false);
		hairMeshes[6] = hairMesh6;
		
	}
	
	public InterpolatedSkeleton create() {
		FurTuftSkeleton skeleton = new FurTuftSkeleton();
		InterpolatedBone furTuft1Bone = new InterpolatedBone("furTuft1");
		furTuft1Bone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(furTuft1Bone, hairMeshes[0]);
		skeleton.furTuft1 = furTuft1Bone;
		
		InterpolatedBone furTuft2Bone = new InterpolatedBone("furTuft2");
		furTuft2Bone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(furTuft2Bone, hairMeshes[1]);
		skeleton.furTuft2 = furTuft2Bone;
		
		InterpolatedBone furTuft3Bone = new InterpolatedBone("furTuft3");
		furTuft3Bone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(furTuft3Bone, hairMeshes[2]);
		skeleton.furTuft3 = furTuft3Bone;
		
		InterpolatedBone furTuft4Bone = new InterpolatedBone("furTuft4");
		furTuft4Bone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(furTuft4Bone, hairMeshes[3]);
		skeleton.furTuft4 = furTuft4Bone;
		
		InterpolatedBone furTuft5Bone = new InterpolatedBone("furTuft5");
		furTuft5Bone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(furTuft5Bone, hairMeshes[4]);
		skeleton.furTuft5 = furTuft5Bone;
		
		InterpolatedBone furTuft6Bone = new InterpolatedBone("furTuft6");
		furTuft6Bone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(furTuft6Bone, hairMeshes[5]);
		skeleton.furTuft6 = furTuft6Bone;
		
		InterpolatedBone furTuft7Bone = new InterpolatedBone("furTuft7");
		furTuft7Bone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(furTuft7Bone, hairMeshes[6]);
		skeleton.furTuft7 = furTuft7Bone;
		
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class FurTuftSkeleton extends InterpolatedSkeleton {
		protected InterpolatedBone furTuft1;
		protected InterpolatedBone furTuft2;
		protected InterpolatedBone furTuft3;
		protected InterpolatedBone furTuft4;
		protected InterpolatedBone furTuft5;
		protected InterpolatedBone furTuft6;
		protected InterpolatedBone furTuft7;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}