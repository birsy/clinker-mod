package birsy.clinker.client.model.entity;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.animation.AnimationProperties;
import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.RenderFactory;
import birsy.clinker.client.necromancer.render.mesh.Mesh;
import birsy.clinker.client.necromancer.render.mesh.StaticMesh;
import org.joml.Quaternionf;

public class PresentSkeletonFactory implements RenderFactory {
	private final Mesh[] meshes = new Mesh[3];
	
	public PresentSkeletonFactory() {
		int texWidth = 64;
		int texHeight = 64;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		mesh0.addCube(0F, 4F, 14F, 0F, -2F, -7F, 0F, 0F, 0F, 0F, 29F, false);
		mesh0.addCube(14F, 4F, 0F, -7F, -2F, 0F, 0F, 0F, 0F, 0F, 43F, false);
		meshes[0] = mesh0;
		
		StaticMesh mesh1 = new StaticMesh(texWidth, texHeight);
		mesh1.addCube(16F, 4F, 16F, -8F, 0F, -15F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[1] = mesh1;
		
		StaticMesh mesh2 = new StaticMesh(texWidth, texHeight);
		mesh2.addCube(14F, 9F, 14F, -7F, 0F, -7F, 0F, 0F, 0F, 0F, 20F, false);
		meshes[2] = mesh2;
	}
	
	public Skeleton create() {
		PresentSkeleton skeleton = new PresentSkeleton();
		Bone bowBone = new Bone("bow");
		bowBone.setInitialTransform(0F, 6F, -7F, new Quaternionf().rotationZYX(0F, 0.78539816295F, 0F));
		skeleton.addBone(bowBone, meshes[0]);
		skeleton.bow = bowBone;
		
		Bone lidBone = new Bone("lid");
		lidBone.setInitialTransform(0F, 8.5F, 7F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(lidBone, meshes[1]);
		skeleton.lid = lidBone;
		
		Bone presentBone = new Bone("present");
		presentBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(presentBone, meshes[2]);
		skeleton.present = presentBone;
		
		lidBone.addChild(bowBone);
		presentBone.addChild(lidBone);
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class PresentSkeleton extends Skeleton {
		protected Bone bow;
		protected Bone lid;
		protected Bone present;
		
		@Override
		public void animate(AnimationProperties properties) {
			for (Object value : this.bones.values()) {
				if (value instanceof Bone bone) {
					bone.reset();
				}
			}
		}
	}
}