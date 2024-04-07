package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.SkeletonFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.common.world.entity.UrnEntity;
import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class PresentSkeletonFactory implements SkeletonFactory {
	private final ModelMesh[] meshes = new ModelMesh[3];
	
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
	
	public InterpolatedSkeleton create() {
		PresentSkeleton skeleton = new PresentSkeleton();
		InterpolatedBone bowBone = new InterpolatedBone("bow");
		bowBone.setInitialTransform(0F, 6F, -7F, new Quaternionf().rotationZYX(0F, 0.78539816295F, 0F));
		skeleton.addBone(bowBone, meshes[0]);
		skeleton.bow = bowBone;
		
		InterpolatedBone lidBone = new InterpolatedBone("lid");
		lidBone.setInitialTransform(0F, 8.5F, 7F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(lidBone, meshes[1]);
		skeleton.lid = lidBone;
		
		InterpolatedBone presentBone = new InterpolatedBone("present");
		presentBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(presentBone, meshes[2]);
		skeleton.present = presentBone;
		
		lidBone.addChild(bowBone);
		presentBone.addChild(lidBone);
		skeleton.buildRoots();
		return skeleton;
	}
	
	public static class PresentSkeleton extends InterpolatedSkeleton {
		protected InterpolatedBone bow;
		protected InterpolatedBone lid;
		protected InterpolatedBone present;
		
		@Override
		public void animate(AnimationProperties properties) {
			for (Object value : this.parts.values()) {
				if (value instanceof InterpolatedBone bone) {
					bone.reset();
				}
			}
		}
	}
}