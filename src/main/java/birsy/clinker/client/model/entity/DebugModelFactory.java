package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.ModelFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.core.util.Quaternionf;

public class DebugModelFactory implements ModelFactory {
	private final ModelMesh[] meshes = new ModelMesh[3];
	
	public DebugModelFactory() {
		int texWidth = 64;
		int texHeight = 128;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		mesh0.addCube(16F, 16F, 16F, -8F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[0] = mesh0;
		
		StaticMesh mesh1 = new StaticMesh(texWidth, texHeight);
		mesh1.addCube(8F, 16F, 13F, -4F, 0F, -6.5F, 0F, 0F, 0F, 0F, 32F, false);
		meshes[1] = mesh1;
		
		StaticMesh mesh2 = new StaticMesh(texWidth, texHeight);
		mesh2.addCube(7F, 7F, 12F, -3.5F, 0F, -6F, 0F, 0F, 0F, 0F, 61F, false);
		meshes[2] = mesh2;
		
	}
	
	public InterpolatedSkeleton create() {
		DebugModel model = new DebugModel();
		InterpolatedBone boneBone = new InterpolatedBone("bone");
		boneBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(boneBone, meshes[0]);
		model.bone = boneBone;
		
		InterpolatedBone bone2Bone = new InterpolatedBone("bone2");
		bone2Bone.setInitialTransform(2F, 15.999999999999996F, 0F, new Quaternionf().rotationZYX(-0.305432618925F, 1.0035643193249997F, -1.0331507847256883e-16F));
		model.addBone(bone2Bone, meshes[1]);
		model.bone2 = bone2Bone;
		
		InterpolatedBone bone3Bone = new InterpolatedBone("bone3");
		bone3Bone.setInitialTransform(-2.9999999999999996F, 16.000000000000004F, 2.220446049250313e-16F, new Quaternionf().rotationZYX(-0.4565252416371948F, 0.5194469456676657F, -0.2391313556367249F));
		model.addBone(bone3Bone, meshes[2]);
		model.bone3 = bone3Bone;
		
		boneBone.addChild(bone2Bone);
		bone2Bone.addChild(bone3Bone);
		model.buildRoots();
		return model;
	}
	
	public static class DebugModel extends InterpolatedSkeleton {
		protected InterpolatedBone bone;
		protected InterpolatedBone bone2;
		protected InterpolatedBone bone3;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}