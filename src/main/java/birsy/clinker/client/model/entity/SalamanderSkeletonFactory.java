package birsy.clinker.client.model.entity;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.RenderFactory;
import birsy.clinker.client.necromancer.constraint.InverseKinematicsConstraint;
import birsy.clinker.client.necromancer.render.mesh.Mesh;
import birsy.clinker.client.necromancer.render.mesh.SalamanderHairMesh;
import birsy.clinker.client.necromancer.render.mesh.StaticMesh;
import birsy.clinker.client.necromancer.animation.AnimationProperties;
import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.core.util.JomlConversions;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaterniond;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SalamanderSkeletonFactory implements RenderFactory {
	private final Mesh[] headMeshes = new Mesh[14];
	private final Mesh[] bodyMeshes = new Mesh[26];
	private final Mesh[] hairMeshes = new Mesh[14];
	private final Random random = new Random();
	public SalamanderSkeletonFactory() {
		int texWidth = 128;
		int texHeight = 64;
		StaticMesh headMesh0 = new StaticMesh(texWidth, texHeight);
		headMeshes[0] = headMesh0;

		StaticMesh headMesh1 = new StaticMesh(texWidth, texHeight);
		headMesh1.addCube(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		headMeshes[1] = headMesh1;

		StaticMesh headMesh2 = new StaticMesh(texWidth, texHeight);
		headMesh2.addCube(12F, 5F, 18F, -6F, -2.5F, -16F, 0F, 0F, 0F, 0F, 0F, false);
		headMeshes[2] = headMesh2;

		StaticMesh headMesh3 = new StaticMesh(texWidth, texHeight);
		headMesh3.addCube(8F, 2F, 13F, -4F, 0F, -5F, 0F, 0F, 0F, 3F, 23F, false);
		headMeshes[3] = headMesh3;

		StaticMesh headMesh4 = new StaticMesh(texWidth, texHeight);
		headMesh4.addCube(0F, 10F, 6F, 0F, -9F, -1F, 0F, 0F, 0F, 44F, -1F, true);
		headMeshes[4] = headMesh4;

		StaticMesh headMesh5 = new StaticMesh(texWidth, texHeight);
		headMesh5.addCube(0F, 10F, 6F, 0F, -9F, -1F, 0F, 0F, 0F, 44F, -1F, false);
		headMeshes[5] = headMesh5;

		StaticMesh headMesh6 = new StaticMesh(texWidth, texHeight);
		headMesh6.addCube(12F, 0F, 5F, -6F, 0F, 0F, 0F, 0F, 0F, 39F, 0F, true);
		headMeshes[6] = headMesh6;

		StaticMesh headMesh7 = new StaticMesh(texWidth, texHeight);
		headMesh7.addCube(2F, 2F, 6F, 0F, -2F, -3.5F, 0F, 0F, 0F, 0F, 7F, false);
		headMeshes[7] = headMesh7;

		StaticMesh headMesh8 = new StaticMesh(texWidth, texHeight);
		headMesh8.addCube(6F, 3.5999999999999996F, 1F, -3.5F, -3.5999999999999996F, -1F, 0F, 0F, 0F, 1F, 2F, false);
		headMeshes[8] = headMesh8;

		StaticMesh headMesh9 = new StaticMesh(texWidth, texHeight);
		headMesh9.addCube(12F, 2F, 15F, -6F, -2.5F, -16F, -0.03F, -0.03F, -0.03F, 62F, 6F, false);
		headMeshes[9] = headMesh9;

		StaticMesh headMesh10 = new StaticMesh(texWidth, texHeight);
		headMesh10.addCube(12F, 4F, 13F, -6F, -2F, -12.5F, -0.05F, -0.05F, -0.05F, 0F, 41F, false);
		headMeshes[10] = headMesh10;

		StaticMesh headMesh11 = new StaticMesh(texWidth, texHeight);
		headMesh11.addCube(12F, 4F, 13F, -6F, 0F, -12.5F, -0.06F, -0.06F, -0.06F, 52F, 41F, false);
		headMeshes[11] = headMesh11;

		StaticMesh headMesh12 = new StaticMesh(texWidth, texHeight);
		headMesh12.addCube(2F, 2F, 6F, -2F, -2F, -3.5F, 0F, 0F, 0F, 0F, 7F, true);
		headMeshes[12] = headMesh12;

		StaticMesh headMesh13 = new StaticMesh(texWidth, texHeight);
		headMesh13.addCube(6F, 3.5999999999999996F, 1F, -3.5F, -3.5999999999999996F, 0F, 0F, 0F, 0F, 0F, 2F, false);
		headMeshes[13] = headMesh13;


		StaticMesh bodyMesh0 = new StaticMesh(texWidth, texHeight);
		bodyMesh0.addCube(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		bodyMeshes[0] = bodyMesh0;
		
		StaticMesh bodyMesh1 = new StaticMesh(texWidth, texHeight);
		bodyMesh1.addCube(9F, 1F, 16F, -4.5F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, false);
		bodyMeshes[1] = bodyMesh1;
		
		StaticMesh bodyMesh2 = new StaticMesh(texWidth, texHeight);
		bodyMesh2.addCube(9F, 2F, 16F, -4.5F, -2F, -8F, 0F, 0F, 0F, 0F, 17F, false);
		bodyMeshes[2] = bodyMesh2;
		
		StaticMesh bodyMesh3 = new StaticMesh(texWidth, texHeight);
		bodyMesh3.addCube(11F, 1F, 16F, -5.5F, -0.5F, -8F, -0.5F, -0.5F, -0.5F, 0F, 17F, false);
		bodyMeshes[3] = bodyMesh3;
		
		StaticMesh bodyMesh4 = new StaticMesh(texWidth, texHeight);
		bodyMesh4.addCube(1F, 11F, 16F, 0F, -5.5F, -8F, 0F, 0F, 0F, 0F, 35F, true);
		bodyMeshes[4] = bodyMesh4;
		
		StaticMesh bodyMesh5 = new StaticMesh(texWidth, texHeight);
		bodyMesh5.addCube(1F, 11F, 16F, -1F, -5.5F, -8F, 0F, 0F, 0F, 0F, 35F, false);
		bodyMeshes[5] = bodyMesh5;
		
		StaticMesh bodyMesh6 = new StaticMesh(texWidth, texHeight);
		bodyMesh6.addCube(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		bodyMeshes[6] = bodyMesh6;
		
		StaticMesh bodyMesh7 = new StaticMesh(texWidth, texHeight);
		bodyMesh7.addCube(3F, 3F, 8F, -1.5F, -1.5F, -8F, 0F, 0F, 0F, 72F, 34F, false);
		bodyMeshes[7] = bodyMesh7;
		
		StaticMesh bodyMesh8 = new StaticMesh(texWidth, texHeight);
		bodyMesh8.addCube(3F, 3F, 8F, -1.5F, -1.5F, -8F, 0F, 0F, 0F, 72F, 34F, false);
		bodyMeshes[8] = bodyMesh8;
		
		StaticMesh bodyMesh9 = new StaticMesh(texWidth, texHeight);
		bodyMesh9.addCube(9F, 8F, 8F, -4.5F, -4F, -8F, 0F, 0F, 0F, 88F, 18F, false);
		bodyMeshes[9] = bodyMesh9;
		
		StaticMesh bodyMesh10 = new StaticMesh(texWidth, texHeight);
		bodyMesh10.addCube(9F, 8F, 8F, -4.5F, -4F, -8F, 0F, 0F, 0F, 88F, 18F, false);
		bodyMeshes[10] = bodyMesh10;
		
		StaticMesh bodyMesh11 = new StaticMesh(texWidth, texHeight);
		bodyMesh11.addCube(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		bodyMeshes[11] = bodyMesh11;
		
		StaticMesh bodyMesh12 = new StaticMesh(texWidth, texHeight);
		bodyMesh12.addCube(4F, 10F, 4F, -3F, -8F, -2F, 0F, 0F, 0F, 40F, 35F, true);
		bodyMeshes[12] = bodyMesh12;
		
		StaticMesh bodyMesh13 = new StaticMesh(texWidth, texHeight);
		bodyMesh13.addCube(2F, 11F, 2F, -1F, -10F, -1F, 0F, 0F, 0F, 40F, 49F, false);
		bodyMeshes[13] = bodyMesh13;
		
		StaticMesh bodyMesh14 = new StaticMesh(texWidth, texHeight);
		bodyMesh14.addCube(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, false);
		bodyMeshes[14] = bodyMesh14;
		
		StaticMesh bodyMesh15 = new StaticMesh(texWidth, texHeight);
		bodyMesh15.addCube(2F, 5F, 6F, -1F, -4F, -6F, 0F, 0F, 0F, 56F, 34F, false);
		bodyMeshes[15] = bodyMesh15;
		
		StaticMesh bodyMesh16 = new StaticMesh(texWidth, texHeight);
		bodyMesh16.addCube(2F, 5F, 6F, -1F, -4F, -6F, 0F, 0F, 0F, 56F, 34F, true);
		bodyMeshes[16] = bodyMesh16;
		
		StaticMesh bodyMesh17 = new StaticMesh(texWidth, texHeight);
		bodyMesh17.addCube(2F, 4F, 4F, -1F, -3F, 0F, 0F, 0F, 0F, 56F, 45F, false);
		bodyMeshes[17] = bodyMesh17;
		
		StaticMesh bodyMesh18 = new StaticMesh(texWidth, texHeight);
		bodyMesh18.addCube(4F, 10F, 4F, -1F, -8F, -2F, 0F, 0F, 0F, 40F, 35F, false);
		bodyMeshes[18] = bodyMesh18;
		
		StaticMesh bodyMesh19 = new StaticMesh(texWidth, texHeight);
		bodyMesh19.addCube(2F, 11F, 2F, -1F, -10F, -1F, 0F, 0F, 0F, 40F, 49F, true);
		bodyMeshes[19] = bodyMesh19;
		
		StaticMesh bodyMesh20 = new StaticMesh(texWidth, texHeight);
		bodyMesh20.addCube(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, true);
		bodyMeshes[20] = bodyMesh20;
		
		StaticMesh bodyMesh21 = new StaticMesh(texWidth, texHeight);
		bodyMesh21.addCube(2F, 5F, 6F, -1F, -4F, -6F, 0F, 0F, 0F, 56F, 34F, false);
		bodyMeshes[21] = bodyMesh21;
		
		StaticMesh bodyMesh22 = new StaticMesh(texWidth, texHeight);
		bodyMesh22.addCube(2F, 5F, 6F, -1F, -4F, -6F, 0F, 0F, 0F, 56F, 34F, true);
		bodyMeshes[22] = bodyMesh22;
		
		StaticMesh bodyMesh23 = new StaticMesh(texWidth, texHeight);
		bodyMesh23.addCube(2F, 4F, 4F, -1F, -3F, 0F, 0F, 0F, 0F, 56F, 45F, false);
		bodyMeshes[23] = bodyMesh23;

		StaticMesh bodyMesh24 = new StaticMesh(texWidth, texHeight);
		bodyMesh24.addCube(8F, 8F, 16F, -4F, -4F, -8F, 0F, 0F, 0F, 0F, 0F, false);
		bodyMeshes[24] = bodyMesh24;

		StaticMesh bodyMesh25 = new StaticMesh(texWidth, texHeight);
		bodyMesh25.addCube(5F, 5F, 15F, -2.5F, -2.5F, -7.5F, 0F, 0F, 0F, 48F, 0F, false);
		bodyMeshes[25] = bodyMesh25;

		SalamanderHairMesh hairMesh0 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh0.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 0F, false);
		hairMeshes[0] = hairMesh0;

		SalamanderHairMesh hairMesh1 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh1.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 4F, false);
		hairMeshes[1] = hairMesh1;

		SalamanderHairMesh hairMesh2 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh2.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 8F, false);
		hairMeshes[2] = hairMesh2;

		SalamanderHairMesh hairMesh3 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh3.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 12F, false);
		hairMeshes[3] = hairMesh3;

		SalamanderHairMesh hairMesh4 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh4.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 16F, false);
		hairMeshes[4] = hairMesh4;

		SalamanderHairMesh hairMesh5 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh5.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 20F, false);
		hairMeshes[5] = hairMesh5;

		SalamanderHairMesh hairMesh6 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh6.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 24F, false);
		hairMeshes[6] = hairMesh6;

		SalamanderHairMesh hairMesh7 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh7.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 0F, true);
		hairMeshes[7] = hairMesh7;

		SalamanderHairMesh hairMesh8 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh8.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 4F, true);
		hairMeshes[8] = hairMesh8;

		SalamanderHairMesh hairMesh9 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh9.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 8F, true);
		hairMeshes[9] = hairMesh9;

		SalamanderHairMesh hairMesh10 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh10.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 12F, true);
		hairMeshes[10] = hairMesh10;

		SalamanderHairMesh hairMesh11 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh11.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 16F, true);
		hairMeshes[11] = hairMesh11;

		SalamanderHairMesh hairMesh12 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh12.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 20F, true);
		hairMeshes[12] = hairMesh12;

		SalamanderHairMesh hairMesh13 = new SalamanderHairMesh(texWidth, texHeight);
		hairMesh13.createFace(3F, 4F, -1.5F, 0F, 0F, 0F, 0F, 122F, 24F, true);
		hairMeshes[13] = hairMesh13;

		for (Mesh hairMesh : hairMeshes) {
			if (hairMesh instanceof SalamanderHairMesh mesh) {
				mesh.wiggleAmount = 0.8F;
				mesh.wiggleSpeed = 0.02F;
			}
		}
	}

	public NewSalamanderEntity entity;
	@Override
	public Skeleton create() {
		int segments = entity.segments.size();
		SalamanderSkeleton skeleton = new SalamanderSkeleton();
		random.setSeed(entity.getUUID().getMostSignificantBits());

		for (int i = 0; i < entity.segments.size(); i++) {
			NewSalamanderEntity.SalamanderSegment segment = entity.segments.get(i);
			if (segment.isHead()) {
				skeleton.segments.add((SalamanderSegmentSkeleton) this.createHead(i, segments));
			} else if (segment.girth > 1){
				skeleton.segments.add((SalamanderSegmentSkeleton) this.createTail(i, segments, segment.girth > 2));
			} else {
				skeleton.segments.add((SalamanderSegmentSkeleton) this.createBody(i, segments, segment.hasLegs, segment.girth > 0));
			}
		}

		return skeleton;
	}



	public Skeleton createHead(int index, int totalSegments) {
		SalamanderHeadSkeleton skeleton = new SalamanderHeadSkeleton(index);
		Bone RootBoneBone = new Bone("RootBone");
		RootBoneBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(RootBoneBone, headMeshes[0]);
		skeleton.RootBone = RootBoneBone;

		Bone salamanderHeadRootBone = new Bone("salamanderHeadRoot");
		salamanderHeadRootBone.setInitialTransform(0F, -0.5F, -8F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderHeadRootBone, headMeshes[1]);
		skeleton.salamanderHeadRoot = salamanderHeadRootBone;

		Bone salamanderHeadBone = new Bone("salamanderHead");
		salamanderHeadBone.setInitialTransform(0F, 0F, 6F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderHeadBone, headMeshes[2]);
		skeleton.salamanderHead = salamanderHeadBone;

		Bone salamanderForeheadBone = new Bone("salamanderForehead");
		salamanderForeheadBone.setInitialTransform(0F, 2.5F, -6F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderForeheadBone, headMeshes[3]);
		skeleton.salamanderForehead = salamanderForeheadBone;

		Bone salamanderLeftFrillBone = new Bone("salamanderLeftFrill");
		salamanderLeftFrillBone.setInitialTransform(4F, 2F, 8F, new Quaternionf().rotationZYX(0F, 0.78539816295F, 0F));
		skeleton.addBone(salamanderLeftFrillBone, headMeshes[4]);
		skeleton.salamanderLeftFrill = salamanderLeftFrillBone;

		Bone salamanderRightFrillBone = new Bone("salamanderRightFrill");
		salamanderRightFrillBone.setInitialTransform(-4F, 2F, 8F, new Quaternionf().rotationZYX(0F, -0.78539816295F, 0F));
		skeleton.addBone(salamanderRightFrillBone, headMeshes[5]);
		skeleton.salamanderRightFrill = salamanderRightFrillBone;

		Bone salamanderTopFrillBone = new Bone("salamanderTopFrill");
		salamanderTopFrillBone.setInitialTransform(0F, 1F, 8F, new Quaternionf().rotationZYX(0F, 0F, -0.78539816295F));
		skeleton.addBone(salamanderTopFrillBone, headMeshes[6]);
		skeleton.salamanderTopFrill = salamanderTopFrillBone;

		Bone salamanderRightEyeSocketBone = new Bone("salamanderRightEyeSocket");
		salamanderRightEyeSocketBone.setInitialTransform(-6F, 2.5F, -3.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderRightEyeSocketBone, headMeshes[7]);
		skeleton.salamanderRightEyeSocket = salamanderRightEyeSocketBone;

		Bone salamanderRightEyeBone = new Bone("salamanderRightEye");
		salamanderRightEyeBone.setInitialTransform(2F, 1F, 0F, new Quaternionf().rotationZYX(0F, -1.5707963259F, -0.5880014246619F));
		skeleton.addBone(salamanderRightEyeBone, headMeshes[8]);
		skeleton.salamanderRightEye = salamanderRightEyeBone;

		Bone salamanderHeadInsideBone = new Bone("salamanderHeadInside");
		salamanderHeadInsideBone.setInitialTransform(0F, 0.0600000000000005F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderHeadInsideBone, headMeshes[9]);
		skeleton.salamanderHeadInside = salamanderHeadInsideBone;

		Bone salamanderJawBone = new Bone("salamanderJaw");
		salamanderJawBone.setInitialTransform(0F, -0.5F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderJawBone, headMeshes[10]);
		skeleton.salamanderJaw = salamanderJawBone;

		Bone salamanderJawInsideBone = new Bone("salamanderJawInside");
		salamanderJawInsideBone.setInitialTransform(0F, -2F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderJawInsideBone, headMeshes[11]);
		skeleton.salamanderJawInside = salamanderJawInsideBone;

		Bone salamanderLeftEyeSocketBone = new Bone("salamanderLeftEyeSocket");
		salamanderLeftEyeSocketBone.setInitialTransform(6F, 2.5F, -3.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderLeftEyeSocketBone, headMeshes[12]);
		skeleton.salamanderLeftEyeSocket = salamanderLeftEyeSocketBone;

		Bone salamanderLeftEyeBone = new Bone("salamanderLeftEye");
		salamanderLeftEyeBone.setInitialTransform(-2F, 1F, 0F, new Quaternionf().rotationZYX(0F, -1.5707963259F, 0.5880014246619F));
		skeleton.addBone(salamanderLeftEyeBone, headMeshes[13]);
		skeleton.salamanderLeftEye = salamanderLeftEyeBone;

		RootBoneBone.addChild(salamanderHeadRootBone);
		salamanderHeadRootBone.addChild(salamanderHeadBone);
		salamanderHeadBone.addChild(salamanderForeheadBone);
		salamanderHeadBone.addChild(salamanderRightEyeSocketBone);
		salamanderHeadBone.addChild(salamanderHeadInsideBone);
		salamanderHeadBone.addChild(salamanderJawBone);
		salamanderHeadBone.addChild(salamanderLeftEyeSocketBone);
		salamanderForeheadBone.addChild(salamanderLeftFrillBone);
		salamanderForeheadBone.addChild(salamanderRightFrillBone);
		salamanderForeheadBone.addChild(salamanderTopFrillBone);
		salamanderRightEyeSocketBone.addChild(salamanderRightEyeBone);
		salamanderJawBone.addChild(salamanderJawInsideBone);
		salamanderLeftEyeSocketBone.addChild(salamanderLeftEyeBone);
		skeleton.buildRoots();
		return skeleton;
	}
	public static class SalamanderHeadSkeleton extends SalamanderSegmentSkeleton {
		protected Bone RootBone;
		protected Bone salamanderHeadRoot;
		protected Bone salamanderHead;
		protected Bone salamanderForehead;
		protected Bone salamanderLeftFrill;
		protected Bone salamanderRightFrill;
		protected Bone salamanderTopFrill;
		protected Bone salamanderRightEyeSocket;
		protected Bone salamanderRightEye;
		protected Bone salamanderHeadInside;
		protected Bone salamanderJaw;
		protected Bone salamanderJawInside;
		protected Bone salamanderLeftEyeSocket;
		protected Bone salamanderLeftEye;
		private Random random = new Random();
		public boolean blink = false;
		private int blinkTime = 0;

		public SalamanderHeadSkeleton(int index) {
			super(index);
		}

		@Override
		public void animate(AnimationProperties properties) {
			if (blink) {
				blink = false;
				blinkTime = 0;
			} else {
				if (random.nextInt(40, 200) > blinkTime) {
					blink = true;
				}
				blinkTime++;
			}

			for (Object value : this.bones.values()) {
				if (value instanceof Bone bone) {
					bone.reset();
				}
			}

			NewSalamanderEntity entity = (NewSalamanderEntity) properties.getProperty("entity");
			NewSalamanderEntity.SalamanderSegment segment = entity.segments.get(this.index);
			Quaterniond rotation = segment.getOrientation(1.0F);
			Vec3 position = segment.joint2.getPosition(1.0F).subtract(entity.getPosition(1.0F)).scale(16);

			for (Object r : this.roots) {
				if (r instanceof Bone root) {
					root.x = (float) position.x();
					root.y = (float) position.y() + 4;
					root.z = (float) position.z() - 1.5F;
					root.rotation.set((float) rotation.x(), (float) rotation.y(), (float) rotation.z(), (float) rotation.w());
				}
			}

			this.salamanderHeadRoot.y += Mth.sin(properties.getNumProperty("ageInTicks") * 0.07F + index * 0.5F) * 0.5F;
			this.salamanderJaw.rotation.rotateX(Mth.sin(properties.getNumProperty("ageInTicks") * 0.01F) * 0.05F - 0.05F);
		}
	}

	public Skeleton createBody(int index, int totalSegments, boolean legs, boolean isTailStart) {
		SalamanderBodySkeleton skeleton = new SalamanderBodySkeleton(index);
		skeleton.hasLegs = legs;
		Bone bodyRootBone = new Bone("bodyRoot");
		bodyRootBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		if (isTailStart) {
			bodyRootBone.initialXSize = 0.85F;
			bodyRootBone.initialYSize = 0.85F;
		}
		skeleton.addBone(bodyRootBone, bodyMeshes[0]);
		skeleton.bodyRoot = bodyRootBone;
		
		Bone salamanderBodyTopBone = new Bone("salamanderBodyTop");
		salamanderBodyTopBone.setInitialTransform(0F, 4.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderBodyTopBone, bodyMeshes[1]);
		skeleton.salamanderBodyTop = salamanderBodyTopBone;
		
		Bone salamanderBodyBottomBone = new Bone("salamanderBodyBottom");
		salamanderBodyBottomBone.setInitialTransform(0F, -3.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderBodyBottomBone, bodyMeshes[2]);
		skeleton.salamanderBodyBottom = salamanderBodyBottomBone;
		
		Bone danglyBitsParentBone = new Bone("danglyBitsParent");
		danglyBitsParentBone.setInitialTransform(0F, -1F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(danglyBitsParentBone, bodyMeshes[3]);
		skeleton.danglyBitsParent = danglyBitsParentBone;
		
		Bone salamanderBodyLeftBone = new Bone("salamanderBodyLeft");
		salamanderBodyLeftBone.setInitialTransform(4.5F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderBodyLeftBone, bodyMeshes[4]);
		skeleton.salamanderBodyLeft = salamanderBodyLeftBone;
		
		Bone salamanderBodyRightBone = new Bone("salamanderBodyRight");
		salamanderBodyRightBone.setInitialTransform(-4.5F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderBodyRightBone, bodyMeshes[5]);
		skeleton.salamanderBodyRight = salamanderBodyRightBone;
		
		Bone salamanderBoneBone = new Bone("salamanderBone");
		salamanderBoneBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderBoneBone, bodyMeshes[6]);
		skeleton.salamanderBone = salamanderBoneBone;
		
		Bone salamanderBoneFrontBone = new Bone("salamanderBoneFront");
		salamanderBoneFrontBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderBoneFrontBone, bodyMeshes[7]);
		skeleton.salamanderBoneFront = salamanderBoneFrontBone;
		
		Bone salamanderBoneBackBone = new Bone("salamanderBoneBack");
		salamanderBoneBackBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 3.1415926518F, 0F));
		skeleton.addBone(salamanderBoneBackBone, bodyMeshes[8]);
		skeleton.salamanderBoneBack = salamanderBoneBackBone;
		
		Bone salamanderInnardsFrontBone = new Bone("salamanderInnardsFront");
		salamanderInnardsFrontBone.setInitialTransform(0F, 0.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
		skeleton.addBone(salamanderInnardsFrontBone, bodyMeshes[9]);
		skeleton.salamanderInnardsFront = salamanderInnardsFrontBone;
		
		Bone salamanderInnardsBackBone = new Bone("salamanderInnardsBack");
		salamanderInnardsBackBone.setInitialTransform(0F, 0.5F, 0F, new Quaternionf().rotationZYX(0F, 3.1415926518F, 0F));
		skeleton.addBone(salamanderInnardsBackBone, bodyMeshes[10]);
		skeleton.salamanderInnardsBack = salamanderInnardsBackBone;

		if (legs) {
			Bone legsRootBone = new Bone("legsRoot");
			legsRootBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			if (isTailStart) {
				legsRootBone.initialXSize = 1.17647059F;
				legsRootBone.initialYSize = 1.17647059F;
			}
			skeleton.addBone(legsRootBone, bodyMeshes[11]);
			skeleton.legsRoot = legsRootBone;

			Bone salamanderRightUpperLegBone = new Bone("salamanderRightUpperLeg");
			salamanderRightUpperLegBone.setInitialTransform(-6.5F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderRightUpperLegBone, bodyMeshes[12]);
			skeleton.salamanderRightUpperLeg = salamanderRightUpperLegBone;

			Bone salamanderRightLowerLegBone = new Bone("salamanderRightLowerLeg");
			salamanderRightLowerLegBone.setInitialTransform(-1F, -9F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderRightLowerLegBone, bodyMeshes[13]);
			skeleton.salamanderRightLowerLeg = salamanderRightLowerLegBone;

			Bone salamanderRightFootBone = new Bone("salamanderRightFoot");
			salamanderRightFootBone.setInitialTransform(0F, -10F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderRightFootBone, bodyMeshes[14]);
			skeleton.salamanderRightFoot = salamanderRightFootBone;

			Bone salamanderRightRightClawBone = new Bone("salamanderRightRightClaw");
			salamanderRightRightClawBone.setInitialTransform(0F, -1F, 0F, new Quaternionf().rotationZYX(0F, 0.392699081475F, 0F));
			skeleton.addBone(salamanderRightRightClawBone, bodyMeshes[15]);
			skeleton.salamanderRightRightClaw = salamanderRightRightClawBone;

			Bone salamanderRightLeftClawBone = new Bone("salamanderRightLeftClaw");
			salamanderRightLeftClawBone.setInitialTransform(0F, -1F, 0F, new Quaternionf().rotationZYX(0F, -0.392699081475F, 0F));
			skeleton.addBone(salamanderRightLeftClawBone, bodyMeshes[16]);
			skeleton.salamanderRightLeftClaw = salamanderRightLeftClawBone;

			Bone salamanderRightBackClawBone = new Bone("salamanderRightBackClaw");
			salamanderRightBackClawBone.setInitialTransform(0F, -1F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderRightBackClawBone, bodyMeshes[17]);
			skeleton.salamanderRightBackClaw = salamanderRightBackClawBone;

			Bone salamanderLeftUpperLegBone = new Bone("salamanderLeftUpperLeg");
			salamanderLeftUpperLegBone.setInitialTransform(6.5F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderLeftUpperLegBone, bodyMeshes[18]);
			skeleton.salamanderLeftUpperLeg = salamanderLeftUpperLegBone;

			Bone salamanderLeftLowerLegBone = new Bone("salamanderLeftLowerLeg");
			salamanderLeftLowerLegBone.setInitialTransform(1F, -9F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderLeftLowerLegBone, bodyMeshes[19]);
			skeleton.salamanderLeftLowerLeg = salamanderLeftLowerLegBone;

			Bone salamanderLeftFootBone = new Bone("salamanderLeftFoot");
			salamanderLeftFootBone.setInitialTransform(0F, -10F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderLeftFootBone, bodyMeshes[20]);
			skeleton.salamanderLeftFoot = salamanderLeftFootBone;

			Bone salamanderLeftRightClawBone = new Bone("salamanderLeftRightClaw");
			salamanderLeftRightClawBone.setInitialTransform(0F, -1F, 0F, new Quaternionf().rotationZYX(0F, 0.392699081475F, 0F));
			skeleton.addBone(salamanderLeftRightClawBone, bodyMeshes[21]);
			skeleton.salamanderLeftRightClaw = salamanderLeftRightClawBone;

			Bone salamanderLeftLeftClawBone = new Bone("salamanderLeftLeftClaw");
			salamanderLeftLeftClawBone.setInitialTransform(0F, -1F, 0F, new Quaternionf().rotationZYX(0F, -0.392699081475F, 0F));
			skeleton.addBone(salamanderLeftLeftClawBone, bodyMeshes[22]);
			skeleton.salamanderLeftLeftClaw = salamanderLeftLeftClawBone;

			Bone salamanderLeftBackClawBone = new Bone("salamanderLeftBackClaw");
			salamanderLeftBackClawBone.setInitialTransform(0F, -1F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
			skeleton.addBone(salamanderLeftBackClawBone, bodyMeshes[23]);
			skeleton.salamanderLeftBackClaw = salamanderLeftBackClawBone;

			bodyRootBone.addChild(legsRootBone);

			legsRootBone.addChild(salamanderRightUpperLegBone);
			legsRootBone.addChild(salamanderLeftUpperLegBone);
			salamanderRightUpperLegBone.addChild(salamanderRightLowerLegBone);
			salamanderRightLowerLegBone.addChild(salamanderRightFootBone);
			salamanderRightFootBone.addChild(salamanderRightRightClawBone);
			salamanderRightFootBone.addChild(salamanderRightLeftClawBone);
			salamanderRightFootBone.addChild(salamanderRightBackClawBone);
			salamanderLeftUpperLegBone.addChild(salamanderLeftLowerLegBone);
			salamanderLeftLowerLegBone.addChild(salamanderLeftFootBone);

			salamanderLeftFootBone.addChild(salamanderLeftRightClawBone);
			salamanderLeftFootBone.addChild(salamanderLeftLeftClawBone);
			salamanderLeftFootBone.addChild(salamanderLeftBackClawBone);
		}


		float factor = 1 - ((float)index / (float)totalSegments), nextFactor = 1 - ((float)(index + 1) / (float)totalSegments);
		float minScale = 0.7F, maxScale = 1.25F;

		float frontScale = MathUtils.map(minScale, maxScale, MathUtils.ease(factor, MathUtils.EasingType.easeInQuad));
		float backScale = MathUtils.map(minScale, maxScale, MathUtils.ease(nextFactor, MathUtils.EasingType.easeInQuad));

		float minRot = (float) Math.toRadians(80), maxRot = (float) Math.toRadians(2);
		float frontRot = MathUtils.map(minRot, maxRot, MathUtils.ease(factor, MathUtils.EasingType.easeInQuad));
		float backRot = MathUtils.map(minRot, maxRot, MathUtils.ease(nextFactor, MathUtils.EasingType.easeInQuad));

		this.addHair(new AABB(-5.5, -5.5, -8, 5.5, 5.5, 8), frontScale, backScale, 0.7F, -1.0F, frontRot, backRot, skeleton, bodyRootBone);

		bodyRootBone.addChild(salamanderBodyTopBone);
		bodyRootBone.addChild(salamanderBodyBottomBone);
		bodyRootBone.addChild(salamanderBodyLeftBone);
		bodyRootBone.addChild(salamanderBodyRightBone);
		bodyRootBone.addChild(salamanderBoneBone);
		bodyRootBone.addChild(salamanderInnardsFrontBone);
		bodyRootBone.addChild(salamanderInnardsBackBone);
		salamanderBodyBottomBone.addChild(danglyBitsParentBone);
		salamanderBoneBone.addChild(salamanderBoneFrontBone);
		salamanderBoneBone.addChild(salamanderBoneBackBone);

		skeleton.buildRoots();

		if (legs) {
			InverseKinematicsConstraint leftLegIK = new InverseKinematicsConstraint(skeleton.salamanderLeftLowerLeg, 2, 0, -10, 0, 0.05F);
			InverseKinematicsConstraint rightLegIK = new InverseKinematicsConstraint(skeleton.salamanderRightLowerLeg, 2, 0, -10, 0, 0.05F);
			Collections.addAll(skeleton.constraints, leftLegIK, rightLegIK);
			skeleton.ikConstraints = new InverseKinematicsConstraint[]{leftLegIK, rightLegIK};
			skeleton.leftLegIK = leftLegIK;
			skeleton.rightLegIK = rightLegIK;
		}

		return skeleton;
	}
	public static class SalamanderBodySkeleton extends SalamanderSegmentSkeleton {
		protected Bone bodyRoot;
		protected Bone salamanderBodyTop;
		protected Bone salamanderBodyBottom;
		protected Bone danglyBitsParent;
		protected Bone salamanderBodyLeft;
		protected Bone salamanderBodyRight;
		protected Bone salamanderBone;
		protected Bone salamanderBoneFront;
		protected Bone salamanderBoneBack;
		protected Bone salamanderInnardsFront;
		protected Bone salamanderInnardsBack;

		protected boolean hasLegs;
		protected Bone legsRoot;
		protected Bone salamanderRightUpperLeg;
		protected Bone salamanderRightLowerLeg;
		protected Bone salamanderRightFoot;
		protected Bone salamanderRightRightClaw;
		protected Bone salamanderRightLeftClaw;
		protected Bone salamanderRightBackClaw;
		protected Bone salamanderLeftUpperLeg;
		protected Bone salamanderLeftLowerLeg;
		protected Bone salamanderLeftFoot;
		protected Bone salamanderLeftRightClaw;
		protected Bone salamanderLeftLeftClaw;
		protected Bone salamanderLeftBackClaw;

		protected InverseKinematicsConstraint leftLegIK;
		protected InverseKinematicsConstraint rightLegIK;
		protected InverseKinematicsConstraint[] ikConstraints;

		public SalamanderBodySkeleton(int index) {
			super(index);
		}

		@Override
		public void animate(AnimationProperties properties) {
			for (Object value : this.bones.values()) {
				if (value instanceof Bone bone) {
					bone.reset();
				}
			}
			super.animate(properties);

			this.bodyRoot.y += 2;
			this.bodyRoot.y += Mth.sin(properties.getNumProperty("ageInTicks") * 0.07F + index * 0.5F) * 0.5F;

			if (hasLegs) {
				float rot = 70;
				this.salamanderRightUpperLeg.rotate((float) Math.toRadians(-rot), Direction.Axis.X);
				this.salamanderRightLowerLeg.rotate((float) Math.toRadians(rot * 2), Direction.Axis.X);
				this.salamanderLeftUpperLeg.rotate((float) Math.toRadians(-rot), Direction.Axis.X);
				this.salamanderLeftLowerLeg.rotate((float) Math.toRadians(rot * 2), Direction.Axis.X);

				NewSalamanderEntity entity = (NewSalamanderEntity) properties.getProperty("entity");

				for (InverseKinematicsConstraint ikConstraint : ikConstraints) {
					boolean isLeft = ikConstraint == leftLegIK;
					Vector3f point = ikConstraint.points.get(0);

					Vec3 footPos = isLeft ? entity.segments.get(index).leftLeg.getFootPosition() : entity.segments.get(index).rightLeg.getFootPosition();
					footPos = footPos.subtract(entity.position());
					footPos = footPos.scale(16);
					Vec3 footNormal = isLeft ? entity.segments.get(index).leftLeg.footNormal : entity.segments.get(index).rightLeg.footNormal;
					footPos = footPos.add(footNormal.scale(2));
					ikConstraint.target.set((float) footPos.x(), (float) footPos.y(), (float) footPos.z());
					Vec3 poleTargetOffset = new Vec3(0, 0, 80);
					poleTargetOffset = JomlConversions.toMojang(((NewSalamanderEntity)properties.getProperty("entity")).segments.get(index).getOrientation(1.0F).transform(JomlConversions.toJOML(poleTargetOffset)));

					Vector3f middle = new Vector3f(point);
					middle.add(new Vector3f((float) footPos.x(), (float) footPos.y(), (float) footPos.z()));
					middle.mul(0.5F);

					ikConstraint.poleTarget.set(middle.x() + (float)poleTargetOffset.x(), middle.y() + (float)poleTargetOffset.y(), middle.z() + (float)poleTargetOffset.z());
				}

				this.salamanderLeftFoot.shouldRender = true;
				this.salamanderLeftRightClaw.shouldRender = true;
				this.salamanderLeftLeftClaw.shouldRender = true;
				this.salamanderLeftBackClaw.shouldRender = true;

				this.salamanderLeftFoot.setGlobalSpaceRotation(new Quaternionf());
				this.salamanderRightFoot.setGlobalSpaceRotation(new Quaternionf());
			}
		}
	}

	private void addHair(AABB aabb, float sizeFront, float sizeBack, float minimumScale, float sizeUnderside, float frontRotation, float backRotation, SalamanderSegmentSkeleton skeleton, Bone parent) {
		Direction[] sides = {Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST};
		for (int i = 0; i < 150; i++) {
			Direction face = sides[random.nextInt(sides.length)];
			Vector3f hairPosition = new Vector3f();
			switch (face) {
				case UP: {
					hairPosition.set((float) random.nextDouble(aabb.minX, aabb.maxX), (float) aabb.maxY, (float) random.nextDouble(aabb.minZ, aabb.maxZ));
					break;
				}
				case DOWN: {
					hairPosition.set((float) random.nextDouble(aabb.minX, aabb.maxX), (float) aabb.minY, (float) random.nextDouble(aabb.minZ, aabb.maxZ));
					break;
				}
				case EAST: {
					hairPosition.set((float) aabb.maxX, (float) random.nextDouble(aabb.minY, aabb.maxY), (float) random.nextDouble(aabb.minZ, aabb.maxZ));
					break;
				}
				case WEST: {
					hairPosition.set((float) aabb.minX, (float) random.nextDouble(aabb.minY, aabb.maxY), (float) random.nextDouble(aabb.minZ, aabb.maxZ));
					break;
				}
			}


			float scale = (float) (MathUtils.mapRange(aabb.minZ, aabb.maxZ, sizeFront, sizeBack, hairPosition.z()) * MathUtils.mapRange(aabb.minY, aabb.maxY, sizeUnderside, 1.0, hairPosition.y()));
			if (scale <= minimumScale) continue;

			float zAngle = 0;
			switch (face) {
				case EAST: {
					zAngle = -Mth.HALF_PI;
					break;
				}
				case DOWN: {
					zAngle = Mth.PI;
					break;
				}
				case WEST: {
					zAngle = Mth.HALF_PI;
					break;
				}
			}
			float maxHangAngle = (float) Math.toRadians(-30);
			zAngle += MathUtils.mapRange(aabb.minX, aabb.maxX, -maxHangAngle, maxHangAngle, hairPosition.x());
			zAngle += random.nextGaussian(0, (float) Math.toRadians(6));

			SalamanderFurBone furTuftBone = new SalamanderFurBone("furTuft" + i, face.step());
			Quaternionf rotation = new Quaternionf().rotationXYZ(
					(float) MathUtils.mapRange(aabb.minZ, aabb.maxZ, frontRotation, backRotation, hairPosition.z()),
					(float) MathUtils.mapRange(aabb.minX, aabb.maxX, -maxHangAngle, maxHangAngle, hairPosition.x()),
					zAngle);
			furTuftBone.setInitialTransform(hairPosition.x(), hairPosition.y(), hairPosition.z(), rotation);
			furTuftBone.initialXSize = scale;
			furTuftBone.initialYSize = scale;
			furTuftBone.initialZSize = scale;
			furTuftBone.xSize = scale;
			furTuftBone.ySize = scale;
			furTuftBone.zSize = scale;
			furTuftBone.pXSize = scale;
			furTuftBone.pYSize = scale;
			furTuftBone.pZSize = scale;
			skeleton.addBone(furTuftBone, hairMeshes[random.nextInt(hairMeshes.length)]);
			skeleton.furTufts.add(furTuftBone);

			parent.addChild(furTuftBone);
		}

	}

	public Skeleton createTail(int index, int totalSegments, boolean small) {
		SalamanderTailSkeleton skeleton = new SalamanderTailSkeleton(index);

		Bone tailBone = new Bone("tail");
		tailBone.setInitialTransform(0F, 0F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));

		float factor = 1 - ((float)index / (float)totalSegments), nextFactor = 1 - ((float)(index + 1) / (float)totalSegments);
		float minScale = 0.7F, maxScale = 1.25F;

		float frontScale = MathUtils.map(minScale, maxScale, MathUtils.ease(factor, MathUtils.EasingType.easeInQuad));
		float backScale = MathUtils.map(minScale, maxScale, MathUtils.ease(nextFactor, MathUtils.EasingType.easeInQuad));

		float minRot = (float) Math.toRadians(80), maxRot = (float) Math.toRadians(2);
		float frontRot = MathUtils.map(minRot, maxRot, MathUtils.ease(factor, MathUtils.EasingType.easeInQuad));
		float backRot = MathUtils.map(minRot, maxRot, MathUtils.ease(nextFactor, MathUtils.EasingType.easeInQuad));

		if (small) {
			skeleton.addBone(tailBone, bodyMeshes[25]);
			this.addHair(new AABB(-2.5, -2.5, -7.5, 2.5, 2.5, 7.5), maxScale, maxScale, minScale, 1.0F, frontRot, maxRot, skeleton, tailBone);
		} else {
			skeleton.addBone(tailBone, bodyMeshes[24]);
			this.addHair(new AABB(-4, -4, -8, 4, 4, 8), frontScale, maxScale, minScale, 0.5F, frontRot, backRot, skeleton, tailBone);
		}

		skeleton.tail = tailBone;

		skeleton.buildRoots();
		return skeleton;
	}
	public static class SalamanderTailSkeleton extends SalamanderSegmentSkeleton {
		protected Bone tail;

		public SalamanderTailSkeleton(int index) {
			super(index);
		}

		@Override
		public void animate(AnimationProperties properties) {
			super.animate(properties);
			this.tail.y += 2;
			this.tail.y += Mth.sin(properties.getNumProperty("ageInTicks") * 0.07F + index * 0.5F) * 0.5F;
			//Mth.sin(properties.getNumProperty("ageInTicks") * 0.02F + index * 0.5F) * 0.2F
		}
	}

	public static class SalamanderSegmentSkeleton extends Skeleton {
		public int index;
		protected List<Bone> furTufts = new ArrayList<>();

		public SalamanderSegmentSkeleton(int index) {
			this.index = index;
		}

		@Override
		public void animate(AnimationProperties properties) {
			for (Object value : this.bones.values()) {
				if (value instanceof Bone bone) {
					bone.reset();
				}
			}
			NewSalamanderEntity entity = (NewSalamanderEntity) properties.getProperty("entity");
			NewSalamanderEntity.SalamanderSegment segment = entity.segments.get(this.index);
			Quaterniond rotation = segment.getOrientation(1.0F);
			Vec3 position = segment.getCenter(1.0F).subtract(entity.getPosition(1.0F)).scale(16);

			for (Object r : this.roots) {
				if (r instanceof Bone root) {
					root.x = (float) position.x();
					root.y = (float) position.y();
					root.z = (float) position.z();
					root.rotation.set((float) rotation.x(), (float) rotation.y(), (float) rotation.z(), (float) rotation.w());
				}
			}
		}
	}

	public static class SalamanderSkeleton extends Skeleton {
		public List<SalamanderSegmentSkeleton> segments = new ArrayList<>();

		public SalamanderSkeleton() {
		}

		@Override
		public void tick(AnimationProperties properties) {
			super.tick(properties);
			for (SalamanderSegmentSkeleton segment : segments) {
				segment.tick(properties);
				if (segment instanceof SalamanderBodySkeleton body) {
					if (body.hasLegs) {
						NewSalamanderEntity entity = (NewSalamanderEntity) properties.getProperty("entity");
						NewSalamanderEntity.SalamanderSegment seg = entity.segments.get(body.index);
						Quaterniond segRot = seg.getOrientation(1.0F);
						Quaternionf rot = new Quaternionf((float) segRot.x(), (float) segRot.y(), (float) segRot.z(), (float) segRot.w());
						Vector3f up = rot.transform(new Vector3f(0, 1, 0));
						Vec3 leftFootNormal = seg.leftLeg.getFootNormal();
						Vec3 rightFootNormal = seg.rightLeg.getFootNormal();
						body.salamanderLeftFoot.setGlobalSpaceRotation(new Quaternionf(rot).rotateTo(up, new Vector3f((float) leftFootNormal.x, (float) leftFootNormal.y, (float) leftFootNormal.z)));
						body.salamanderRightFoot.setGlobalSpaceRotation(new Quaternionf(rot).rotateTo(up, new Vector3f((float) rightFootNormal.x, (float) rightFootNormal.y, (float) rightFootNormal.z)));
					}
				}
			}
		}

		@Override
		public void animate(AnimationProperties properties) {}

		@Override
		public void render(float partialTick, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
			super.render(partialTick, pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		}
	}

	public static class SalamanderFurBone extends Bone {
		public Vector3f normal;
        public Quaternionf rot = new Quaternionf();
		public float brightness = 1.0F;

		public SalamanderFurBone(String identifier, Vector3f normal) {
			super(identifier);
			this.normal = normal;
		}

        public void transform(PoseStack pPoseStack, float partialTick) {
            pPoseStack.translate(Mth.lerp(partialTick, pX, x), Mth.lerp(partialTick, pY, y), Mth.lerp(partialTick, pZ, z));
            pPoseStack.scale(Mth.lerp(partialTick, pXSize, xSize), Mth.lerp(partialTick, pYSize, ySize), Mth.lerp(partialTick, pZSize, zSize));
            this.rot = pRotation.slerp(rotation, partialTick, this.rot);
            this.rot.normalize();
            //pPoseStack.mulPose(this.currentRotation.toMojangQuaternion());
        }
	}
}