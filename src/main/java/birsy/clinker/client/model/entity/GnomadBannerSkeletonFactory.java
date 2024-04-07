package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.SkeletonFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import org.joml.Quaternionf;

public class GnomadBannerSkeletonFactory implements SkeletonFactory {
	private final ModelMesh[] meshes = new ModelMesh[4];
	
	public GnomadBannerSkeletonFactory() {
		int texWidth = 128;
		int texHeight = 128;
		StaticMesh mesh0 = new StaticMesh(texWidth, texHeight);
		mesh0.addCube(4F, 1F, 4F, -12F, -0.5F, -2F, 0F, 0F, 0F, 16F, 0F, false);
		mesh0.addCube(4F, 1F, 4F, -2F, -0.5F, -2F, 0F, 0F, 0F, 16F, 0F, false);
		mesh0.addCube(4F, 1F, 4F, 8F, -0.5F, -2F, 0F, 0F, 0F, 16F, 0F, false);
		meshes[0] = mesh0;
		
		StaticMesh mesh1 = new StaticMesh(texWidth, texHeight);
		mesh1.addCube(24F, 72F, 1F, -12F, -72F, -1F, 0F, 0F, 0F, 0F, 9F, false);
		meshes[1] = mesh1;
		
		StaticMesh mesh2 = new StaticMesh(texWidth, texHeight);
		mesh2.addCube(24F, 72F, 1F, -12F, -72F, -1F, 0F, 0F, 0F, 50F, 9F, false);
		meshes[2] = mesh2;
		
		StaticMesh mesh3 = new StaticMesh(texWidth, texHeight);
		mesh3.addCube(2F, 3F, 2F, -6F, 73.5F, -1F, 0F, 0F, 0F, 8F, 0F, false);
		mesh3.addCube(4F, 4F, 2F, -2F, -18.5F, -1F, 0F, 0F, 0F, 84F, 3F, false);
		mesh3.addCube(2F, 86F, 2F, -1F, -14.5F, -1F, 0.25F, 0.25F, 0.25F, 108.5F, 0F, false);
		mesh3.addCube(2F, 86F, 2F, -1F, -14.5F, -1F, 0F, 0F, 0F, 100F, 0F, false);
		mesh3.addCube(6F, 1F, 2F, -3F, -12.5F, -1F, 0F, 0F, 0F, 84F, 0F, false);
		mesh3.addCube(36F, 2F, 2F, -18F, 71.5F, -1F, 0F, 0F, 0F, 0F, 5F, false);
		mesh3.addCube(2F, 3F, 2F, 4F, 73.5F, -1F, 0F, 0F, 0F, 0F, 0F, false);
		meshes[3] = mesh3;
		
	}
	
	public InterpolatedSkeleton create() {
		GnomadBannerSkeleton skeleton = new GnomadBannerSkeleton();
		InterpolatedBone ClothBone = new InterpolatedBone("Cloth");
		ClothBone.setInitialTransform(0F, 73.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(ClothBone, meshes[0]);
		skeleton.Cloth = ClothBone;
		
		InterpolatedBone ClothFrontBone = new InterpolatedBone("ClothFront");
		ClothFrontBone.setInitialTransform(0F, 0.5F, -1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(ClothFrontBone, meshes[1]);
		skeleton.ClothFront = ClothFrontBone;
		
		InterpolatedBone ClothBackBone = new InterpolatedBone("ClothBack");
		ClothBackBone.setInitialTransform(0F, 0.5F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(ClothBackBone, meshes[2]);
		skeleton.ClothBack = ClothBackBone;
		
		InterpolatedBone BannerBone = new InterpolatedBone("Banner");
		BannerBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(BannerBone, meshes[3]);
		skeleton.Banner = BannerBone;
		
		ClothBone.addChild(ClothFrontBone);
		ClothBone.addChild(ClothBackBone);
		BannerBone.addChild(ClothBone);
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class GnomadBannerSkeleton extends InterpolatedSkeleton {
		protected InterpolatedBone Cloth;
		protected InterpolatedBone ClothFront;
		protected InterpolatedBone ClothBack;
		protected InterpolatedBone Banner;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}