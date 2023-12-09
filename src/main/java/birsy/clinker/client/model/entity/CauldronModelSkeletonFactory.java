package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.SkeletonFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import org.joml.Quaternionf;

public class CauldronModelSkeletonFactory implements SkeletonFactory {
	private final ModelMesh[] meshes = new ModelMesh[2];
	
	public CauldronModelSkeletonFactory() {
		int texWidth = 64;
		int texHeight = 64;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		mesh0.addCube(3F, 2F, 3F, -7F, -4.444444444444445F, 4F, 0F, 0F, 0F, 48F, 38F, false);
		mesh0.addCube(16F, 14F, 2F, -8F, -2.4444444444444446F, -8F, 0F, 0F, 0F, 0F, 26F, false);
		mesh0.addCube(16F, 14F, 2F, -8F, -2.4444444444444446F, 6F, 0F, 0F, 0F, 0F, 42F, false);
		mesh0.addCube(2F, 14F, 12F, -8F, -2.4444444444444446F, -6F, 0F, 0F, 0F, 0F, 0F, false);
		mesh0.addCube(2F, 14F, 12F, 6F, -2.4444444444444446F, -6F, 0F, 0F, 0F, 28F, 0F, false);
		mesh0.addCube(12F, 1F, 12F, -6F, -2.4444444444444446F, -6F, 0F, 0F, 0F, 24F, 26F, false);
		mesh0.addCube(3F, 2F, 3F, 4F, -4.444444444444445F, 4F, 0F, 0F, 0F, 36F, 38F, false);
		mesh0.addCube(3F, 2F, 3F, 4F, -4.444444444444445F, -7F, 0F, 0F, 0F, 36F, 43F, false);
		mesh0.addCube(3F, 2F, 3F, -7F, -4.444444444444445F, -7F, 0F, 0F, 0F, 48F, 43F, false);
		meshes[0] = mesh0;

		StaticMesh mesh1 = new StaticMesh(texWidth, texHeight);
		meshes[1] = mesh1;
	}
	
	public InterpolatedSkeleton create() {
		CauldronModelSkeleton skeleton = new CauldronModelSkeleton();
		InterpolatedBone centerOfMassBone = new InterpolatedBone("centerOfMass");
		centerOfMassBone.setInitialTransform(0F, 4.444444444444445F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(centerOfMassBone, meshes[0]);
		skeleton.centerOfMass = centerOfMassBone;

		InterpolatedBone bottomBone = new InterpolatedBone("bottom");
		bottomBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(bottomBone, meshes[1]);
		skeleton.bottom = bottomBone;

		bottomBone.addChild(centerOfMassBone);
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class CauldronModelSkeleton extends InterpolatedSkeleton {
		protected InterpolatedBone centerOfMass;
		protected InterpolatedBone bottom;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}