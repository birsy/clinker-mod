package birsy.clinker.client.animation;

import birsy.clinker.client.animation.constraint.AnimationConstraint;
import birsy.clinker.client.animation.simulation.Ligament;
import birsy.clinker.client.animation.simulation.Link;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ModelSkeleton {
    public final BaseModel baseModel;

    public final ModelSkeletonParent parent;
    public ModelBone root;
    public Map<String, ModelBone> boneIdentifierMap;

    public final List<Ligament> ligaments = new ArrayList<>();
    public final List<Link> links = new ArrayList<>();

    public final List<AnimationConstraint>[] constraints = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    public final Map<String, AnimationConstraint> constraintIdentifierMap;

    public ModelSkeleton(ModelSkeletonParent parent, BaseModel baseModel) {
        this.parent = parent;
        this.baseModel = baseModel;
        this.boneIdentifierMap = new HashMap<>();
        this.constraintIdentifierMap = new HashMap<>();
    }

    public void setRoot(ModelBone root) {
        this.root = root;
        this.boneIdentifierMap.put(root.identifier, root);
    }

    public void addBone(ModelBone bone) {
        if (boneIdentifierMap.containsKey(bone.identifier)) {
            Clinker.LOGGER.warn("! Bones with identical identifier " + bone.identifier);
            return;
        }

        this.boneIdentifierMap.put(bone.identifier, bone);
    }

    public void get(String identifier) {
        this.boneIdentifierMap.get(identifier);
    }

    public void getBone(String identifier) {
        this.boneIdentifierMap.get(identifier);
    }

    public void traverseBoneTree(Consumer<ModelBone> consumer) {
        consumer.accept(this.root);
        traverseBoneTree(this.root, consumer);
    }

    public void traverseBoneTree(ModelBone root, Consumer<ModelBone> consumer) {
        for (ModelBone child : root.children) {
            consumer.accept(child);
            traverseBoneTree(child, consumer);
        }
    }

    public void tick(float deltaTime, int constraintIterations) {
        traverseBoneTree((bone) -> {
            bone.tick();
            this.baseModel.resetToInitialTransform(bone);
        });

        this.applyAnimations();

        //apply forces to ligaments
        traverseBoneTree((bone) -> {
            for (Ligament ligament : bone.associatedLigaments) {
                //position relative to parent, with rotations applied.
                Vector3f targetPosition = this.baseModel.getRelativeLigamentPosition(bone, ligament).copy();
                targetPosition = bone.targetRotation.transform(targetPosition);
                targetPosition.add(bone.targetX, bone.targetY, bone.targetZ);

                if (bone.softness == 0) {
                    ligament.locked = true;
                    ligament.position = targetPosition.copy();
                } else {
                    ligament.locked = false;
                    targetPosition.sub(ligament.position);
                    targetPosition.mul(bone.softness);
                    ligament.accelerate(targetPosition);
                }
            }
        });

        //simulate
        for (Ligament ligament : ligaments) {
            ligament.tick(deltaTime);
        }
        for (int i = 0; i < constraintIterations; i++) {
            for (Link link : links) {
                link.apply();
            }
        }

        //convert ligaments to positions / rotations
        traverseBoneTree((bone) -> {
            Vector3f[] initialLigaments = this.baseModel.getInitialLigamentPositions(bone.identifier);
            Vector3f[] transformedLigaments = bone.relativeLigamentPositions;
            Quaternionf rotation = OrientationSolver.solveOrientation(initialLigaments, transformedLigaments, 32, 0.05F);

            Vector3f offset = this.baseModel.getInitialLigamentOffset(bone.identifier);
            offset = rotation.transform(offset);

            bone.rotation = rotation;
            bone.x = bone.ligamentCenter.x() + offset.x();
            bone.y = bone.ligamentCenter.y() + offset.y();
            bone.z = bone.ligamentCenter.z() + offset.z();
        });
    }

    public void applyAnimations() {
    }

//    private void applyConstraints(Collection<AnimationConstraint> constraints, int constraintIterations, float deltaTime) {
//        for (int i = 0; i < constraintIterations; i++) {
//            for (AnimationConstraint constraint : constraints) {
//                constraint.iterate(deltaTime);
//            }
//
//            // if all constraints are satisfied, don't bother iterating further.
//            boolean shouldKeepIterating = false;
//            for (AnimationConstraint constraint : constraints) {
//                if (!constraint.isConstraintSatisfied()) {
//                    shouldKeepIterating = true;
//                    break;
//                }
//            }
//
//            if (!shouldKeepIterating) break;
//        }
//    }

    //TODO: kill me
    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float partialTick) {
        this.renderBone(this.root, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha, partialTick);
    }

    public void renderBone(ModelBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float partialTick) {
        if (!bone.visible) return;
        poseStack.pushPose();
        bone.transform(poseStack, partialTick);
        PoseStack.Pose pose = poseStack.last();

        if (!bone.skipRender) {
            if (bone instanceof DynamicMeshModelBone meshedBone) {
                meshedBone.mesh.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            } else if (baseModel.bones.containsKey(bone.identifier)) {
                baseModel.bones.get(bone.identifier).render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
        poseStack.popPose();

        for (ModelBone child : bone.children) {
            renderBone(child, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha, partialTick);
        }
    }

//    protected Vec3 toWorldSpace(Vec3 position) {
//        position = this.parent.getSkeletonOrientation().transform(position);
//        position = position.add(this.parent.getSkeletonPosition());
//        return position;
//    }
//    protected Vec3 toPrevWorldSpace(Vec3 position) {
//        position = this.parent.getPreviousSkeletonOrientation().transform(position);
//        position = position.add(this.parent.getPreviousSkeletonPosition());
//        return position;
//    }
    protected Quaternionf toWorldSpace(Quaternionf orientation) {
        return orientation.mul(this.parent.getSkeletonOrientation());
    }
    protected Quaternionf toPrevWorldSpace(Quaternionf orientation) {
        return orientation.mul(this.parent.getPreviousSkeletonOrientation());
    }

//    protected Vec3 fromWorldSpace(Vec3 position) {
//        position = position.subtract(this.parent.getSkeletonPosition());
//        position = this.parent.getSkeletonOrientation().transformInverse(position);
//        return position;
//    }
//    protected Vec3 fromPrevWorldSpace(Vec3 position) {
//        position = position.subtract(this.parent.getPreviousSkeletonPosition());
//        position = this.parent.getPreviousSkeletonOrientation().transformInverse(position);
//        return position;
//    }
    protected Quaternionf fromWorldSpace(Quaternionf orientation) {
        return orientation.mul(this.parent.getSkeletonOrientation().conjugate());
    }
    protected Quaternionf fromPrevWorldSpace(Quaternionf orientation) {
        return orientation.mul(this.parent.getPreviousSkeletonOrientation().conjugate());
    }
}
