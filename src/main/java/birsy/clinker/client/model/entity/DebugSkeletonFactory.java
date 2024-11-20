package birsy.clinker.client.model.entity;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.RenderFactory;
import birsy.clinker.client.necromancer.render.mesh.Mesh;
import birsy.clinker.client.necromancer.render.mesh.StaticMesh;
import birsy.clinker.client.necromancer.animation.AnimationProperties;
import org.joml.Quaternionf;

public class DebugSkeletonFactory implements RenderFactory {
	private final Mesh[] meshes = new Mesh[3];
	
	public DebugSkeletonFactory() {
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
	
	public Skeleton create() {
		DebugModel model = new DebugModel();
		Bone boneBone = new Bone("bone");
		boneBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		model.addBone(boneBone, meshes[0]);
		model.bone = boneBone;
		
		Bone bone2Bone = new Bone("bone2");
		bone2Bone.setInitialTransform(2F, 15.999999999999996F, 0F, new Quaternionf().rotationZYX(-0.305432618925F, 1.0035643193249997F, -1.0331507847256883e-16F));
		model.addBone(bone2Bone, meshes[1]);
		model.bone2 = bone2Bone;
		
		Bone bone3Bone = new Bone("bone3");
		bone3Bone.setInitialTransform(-2.9999999999999996F, 16.000000000000004F, 2.220446049250313e-16F, new Quaternionf().rotationZYX(-0.4565252416371948F, 0.5194469456676657F, -0.2391313556367249F));
		model.addBone(bone3Bone, meshes[2]);
		model.bone3 = bone3Bone;
		
		boneBone.addChild(bone2Bone);
		bone2Bone.addChild(bone3Bone);
		model.buildRoots();
		return model;
	}
	
	public static class DebugModel extends Skeleton {
		protected Bone bone;
		protected Bone bone2;
		protected Bone bone3;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}