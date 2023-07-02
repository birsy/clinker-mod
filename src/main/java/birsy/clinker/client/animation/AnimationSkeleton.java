package birsy.clinker.client.animation;

import birsy.clinker.client.animation.constraint.AnimationConstraint;
import birsy.clinker.client.animation.constraint.ConstraintOrder;
import birsy.clinker.common.world.physics.CollidingParticle;
import birsy.clinker.common.world.physics.Constraint;
import birsy.clinker.common.world.physics.LinkConstraint;
import birsy.clinker.common.world.physics.ParticleParent;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Quaterniond;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class AnimationSkeleton {
    public final AnimatedSkeletonParent parent;
    public AnimationBone root;
    public final AnimationModelBase baseModel;
    public Map<String, AnimationBone> boneIdentifierMap;

    public final List<AnimationConstraint>[] constraints = new ArrayList[]{new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList()};
    public final Map<String, AnimationConstraint> constraintIdentifierMap;
    public final List<BoneConnection> connections;


    public AnimationSkeleton(AnimatedSkeletonParent parent, AnimationModelBase baseModel) {
        this.parent = parent;
        this.baseModel = baseModel;
        this.boneIdentifierMap = new HashMap<>();
        this.constraintIdentifierMap = new HashMap<>();
        this.connections = new ArrayList<>();
    }

    public void setRoot(AnimationBone root) {
        this.root = root;
        this.boneIdentifierMap.put(root.identifier, root);
    }

    public void addBone(AnimationBone bone) {
        if (boneIdentifierMap.containsKey(bone.identifier)) {
            Clinker.LOGGER.warn("! Bones with identical identifier " + bone.identifier);
            return;
        }

        this.boneIdentifierMap.put(bone.identifier, bone);
    }

    private void initializeConnectionLengths() {
        for (BoneConnection connection : connections) {
            AnimationBone bone1 = connection.bone1;
            AnimationBone bone2 = connection.bone2;
            connection.length = (float) new Vec3(bone1.targetX, bone1.targetY, bone1.targetZ).distanceTo(new Vec3(bone2.targetX, bone2.targetY, bone2.targetZ));
        }
    }

    public void get(String identifier) {
        this.boneIdentifierMap.get(identifier);
    }

    public void getBone(String identifier) {
        this.boneIdentifierMap.get(identifier);
    }

    public void traverseBoneTree(Consumer<AnimationBone> consumer) {
        consumer.accept(this.root);
        traverseBoneTree(this.root, consumer);
    }

    public void traverseBoneTree(AnimationBone root, Consumer<AnimationBone> consumer) {
        for (AnimationBone child : root.children) {
            consumer.accept(child);
            traverseBoneTree(child, consumer);
        }
    }

    public void tick(float deltaTime, int constraintIterations) {
        Collection<AnimationConstraint> constraints = this.constraintIdentifierMap.values();

        traverseBoneTree((animatedModelBone -> animatedModelBone.tick()));
        applyConstraints(constraints, constraintIterations, deltaTime);
        traverseBoneTree((animatedModelBone -> animatedModelBone.setToTarget()));
    }

    private void applyConstraints(Collection<AnimationConstraint> constraints, int constraintIterations, float deltaTime) {
        for (int i = 0; i < constraintIterations; i++) {
            for (AnimationConstraint constraint : constraints) {
                constraint.iterate(deltaTime);
            }

            //if all constraints are satisfied, don't bother iterating further.
            boolean shouldKeepIterating = false;
            for (AnimationConstraint constraint : constraints) {
                if (!constraint.isConstraintSatisfied()) {
                    shouldKeepIterating = true;
                    break;
                }
            }

            if (!shouldKeepIterating) break;
        }
    }

    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float partialTick) {
        this.renderBone(this.root, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha, partialTick);
    }

    public void renderBone(AnimationBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float partialTick) {
        if (!bone.visible) return;
        poseStack.pushPose();
        bone.transform(poseStack, partialTick);
        PoseStack.Pose pose = poseStack.last();

        if (!bone.skipRender) {
            if (bone instanceof DynamicallyMeshedAnimationBone meshedBone) {
                meshedBone.mesh.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            } else if (baseModel.bones.containsKey(bone.identifier)) {
                baseModel.bones.get(bone.identifier).mesh.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        for (AnimationBone child : bone.children) {
            renderBone(child, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha, partialTick);
        }

        poseStack.popPose();
    }

    protected Vec3 toWorldSpace(Vec3 position) {
        position = this.parent.getSkeletonOrientation().transform(position);
        position = position.add(this.parent.getSkeletonPosition());
        return position;
    }
    protected Vec3 toPrevWorldSpace(Vec3 position) {
        position = this.parent.getPreviousSkeletonOrientation().transform(position);
        position = position.add(this.parent.getPreviousSkeletonPosition());
        return position;
    }
    protected Quaterniond toWorldSpace(Quaterniond orientation) {
        return orientation.mul(this.parent.getSkeletonOrientation());
    }
    protected Quaterniond toPrevWorldSpace(Quaterniond orientation) {
        return orientation.mul(this.parent.getPreviousSkeletonOrientation());
    }

    protected Vec3 fromWorldSpace(Vec3 position) {
        position = position.subtract(this.parent.getSkeletonPosition());
        position = this.parent.getSkeletonOrientation().transformInverse(position);
        return position;
    }
    protected Vec3 fromPrevWorldSpace(Vec3 position) {
        position = position.subtract(this.parent.getPreviousSkeletonPosition());
        position = this.parent.getPreviousSkeletonOrientation().transformInverse(position);
        return position;
    }
    protected Quaterniond fromWorldSpace(Quaterniond orientation) {
        return orientation.mul(this.parent.getSkeletonOrientation().conjugate());
    }
    protected Quaterniond fromPrevWorldSpace(Quaterniond orientation) {
        return orientation.mul(this.parent.getPreviousSkeletonOrientation().conjugate());
    }

    protected static class BoneConnection {
        final AnimationBone bone1;
        final AnimationBone bone2;
        float length;

        public BoneConnection(AnimationBone bone1, AnimationBone bone2) {
            //ensures the same connection can't happen twice even if the orders are switched around.
            if (bone1.identifier.hashCode() > bone2.identifier.hashCode()) {
                this.bone1 = bone1;
                this.bone2 = bone2;
            } else {
                this.bone1 = bone2;
                this.bone2 = bone1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoneConnection that = (BoneConnection) o;
            return (bone1.equals(that.bone1) && bone2.equals(that.bone2)) || (bone1.equals(that.bone2) && bone2.equals(that.bone1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(bone1, bone2);
        }
    }
}
