package birsy.clinker.client.model;

import birsy.clinker.client.necromancer.animation.AnimationProperties;
import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.RenderFactory;
import birsy.clinker.client.necromancer.render.mesh.Mesh;
import birsy.clinker.client.necromancer.render.mesh.StaticMesh;
import org.joml.Quaternionf;

public class GnomadBannerSkeletonFactory implements RenderFactory {
	private final Mesh[] meshes = new Mesh[4];
	
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
	
	public Skeleton create() {
		GnomadBannerSkeleton skeleton = new GnomadBannerSkeleton();
		Bone ClothBone = new Bone("Cloth");
		ClothBone.setInitialTransform(0F, 73.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(ClothBone, meshes[0]);
		skeleton.Cloth = ClothBone;
		
		Bone ClothFrontBone = new Bone("ClothFront");
		ClothFrontBone.setInitialTransform(0F, 0.5F, -1F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(ClothFrontBone, meshes[1]);
		skeleton.ClothFront = ClothFrontBone;
		
		Bone ClothBackBone = new Bone("ClothBack");
		ClothBackBone.setInitialTransform(0F, 0.5F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(ClothBackBone, meshes[2]);
		skeleton.ClothBack = ClothBackBone;
		
		Bone BannerBone = new Bone("Banner");
		BannerBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(BannerBone, meshes[3]);
		skeleton.Banner = BannerBone;
		
		ClothBone.addChild(ClothFrontBone);
		ClothBone.addChild(ClothBackBone);
		BannerBone.addChild(ClothBone);
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class GnomadBannerSkeleton extends Skeleton {
		protected Bone Cloth;
		protected Bone ClothFront;
		protected Bone ClothBack;
		protected Bone Banner;
		
		@Override
		public void animate(AnimationProperties properties) {
			
		}
	}
}