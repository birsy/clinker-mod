package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.PointUVStaticMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.client.model.base.AnimationProperties;
import org.joml.Quaternionf;

public class MogulWarhookModel {
	private static final ModelMesh[] meshes = new ModelMesh[1];
	public static final MogulWarhookSkeleton skeleton;

	static {
		int texWidth = 32;
		int texHeight = 32;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		mesh0.addCube(1F, 4F, 2F, 0F, 16.5F, -6F, 0F, 0F, 0F, 6F, 23F, false);
		mesh0.addCube(2F, 14F, 3F, -0.5F, -9.5F, -1.5F, -0.2F, -0.2F, -0.2F, 6F, 0F, false);
		mesh0.addCube(1F, 29F, 2F, 0F, -17.5F, -1F, 0F, 0F, 0F, 0F, 0F, false);
		mesh0.addCube(1F, 1F, 4F, 0F, -18.5F, -2F, 0F, 0F, 0F, 16F, 23F, false);
		mesh0.addCube(1F, 1F, 4F, 0F, -21.5F, -2F, 0F, 0F, 0F, 22F, 24F, false);
		mesh0.addCube(1F, 2F, 1F, 0F, -20.5F, -2F, 0F, 0F, 0F, 16F, 4F, false);
		mesh0.addCube(1F, 2F, 1F, 0F, -20.5F, 1F, 0F, 0F, 0F, 25F, 4F, false);
		mesh0.addCube(2F, 1F, 6F, -0.5F, -15.5F, -3F, 0F, 0F, 0F, 6F, 23F, false);
		mesh0.addCube(1F, 2F, 7F, 0F, 9.5F, 1F, 0F, 0F, 0F, 16F, 0F, false);
		mesh0.addCube(1F, 9F, 2F, 0F, 11.5F, 6F, 0F, 0F, 0F, 18F, 9F, false);
		mesh0.addCube(1F, 2F, 2F, 0F, 18.5F, 4F, 0F, 0F, 0F, 16F, 0F, false);
		mesh0.addCube(1F, 2F, 2F, 0F, 18.5F, -4F, 0F, 0F, 0F, 25F, 0F, false);
		mesh0.addCube(1F, 2F, 10F, 0F, 20.5F, -4F, 0F, 0F, 0F, 6F, 11F, false);
		meshes[0] = mesh0;


		skeleton = (MogulWarhookSkeleton) create();
	}
	
	public static InterpolatedSkeleton create() {
		MogulWarhookSkeleton skeleton = new MogulWarhookSkeleton();
		InterpolatedBone MogulWarhookBone = new InterpolatedBone("MogulWarhook");
		MogulWarhookBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(MogulWarhookBone, meshes[0]);
		skeleton.MogulWarhook = MogulWarhookBone;
		
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class MogulWarhookSkeleton extends InterpolatedSkeleton {
		protected InterpolatedBone MogulWarhook;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}