package birsy.clinker.client.animation;

import birsy.clinker.client.animation.constraint.AnimationConstraint;
import birsy.clinker.core.util.Quaterniond;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AnimationBone {
    public final AnimationSkeleton skeleton;
    public AnimationBone parent;
    public List<AnimationBone> children;
    public Map<String, AnimationConstraint> constraints;

    public float x, y, z;
    public float prevX, prevY, prevZ;
    public float targetX, targetY, targetZ;

    public float scaleX, scaleY, scaleZ;
    public float prevScaleX, prevScaleY, prevScaleZ;

    public Quaterniond rotation;
    public Quaterniond prevRotation;
    public Quaterniond targetRotation;

    public boolean visible = true;
    public boolean skipRender = false;

    public final String identifier;



    public AnimationBone(String identifier, AnimationSkeleton skeleton) {
        this.skeleton = skeleton;
        this.identifier = identifier;
        this.children = new ArrayList<>();
        this.constraints = new HashMap<>();
    }

    protected void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.prevScaleX = this.scaleX;
        this.prevScaleY = this.scaleY;
        this.prevScaleZ = this.scaleZ;
        this.prevRotation = this.rotation.clone();
        resetTargets();
    }

    protected void resetTargets() {
        this.targetX = this.x;
        this.targetY = this.y;
        this.targetZ = this.z;
        this.targetRotation = this.rotation.clone();
    }

    protected void setToTarget() {
        this.x = this.targetX;
        this.y = this.targetY;
        this.z = this.targetZ;
        this.rotation = this.targetRotation.clone();
    }

    public float x(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevX, this.x);
    }
    public float y(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevY, this.y);
    }
    public float z(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevZ, this.z);
    }

    public float scaleX(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevScaleX, this.scaleX);
    }
    public float scaleY(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevScaleY, this.scaleY);
    }
    public float scaleZ(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevScaleZ, this.scaleZ);
    }

    public Quaterniond rotation(float partialTicks) {
        return prevRotation.slerp(rotation, partialTicks);
    }
    public Quaternion mojRotation(float partialTicks) {
        return prevRotation.slerp(rotation, partialTicks).toMojangQuaternion();
    }

    public void transform(PoseStack transform, float partialTicks) {
        transform.translate(this.x(partialTicks), this.y(partialTicks), this.z(partialTicks));
        transform.translate(this.scaleX(partialTicks), this.scaleY(partialTicks), this.scaleZ(partialTicks));
        transform.mulPose(this.mojRotation(partialTicks));
    }

    public void getPoseSpaceTransform(PoseStack transform, float partialTicks) {
        if (this.parent != null) this.parent.getPoseSpaceTransform(transform, partialTicks);
        this.transform(transform, partialTicks);
    }

    public void getGlobalTransform(PoseStack transform, float partialTicks) {
        this.getPoseSpaceTransform(transform, partialTicks);
        AnimatedSkeletonParent parent = this.skeleton.parent;
        Quaterniond orientation = parent.getSkeletonOrientation(partialTicks);
        transform.mulPose(orientation.toMojangQuaternion());

        Vec3 position = parent.getSkeletonPosition(partialTicks);
        transform.translate(position.x, position.y, position.z);
    }

    public Quaterniond getPoseSpaceRotation(Quaterniond quaterniond, float partialTicks) {
        if (this.parent != null) this.parent.getPoseSpaceRotation(quaterniond, partialTicks);
        quaterniond.mul(this.prevRotation.slerp(this.rotation, partialTicks));
        return quaterniond;
    }

    public Quaterniond getGlobalRotation(Quaterniond quaterniond, float partialTicks) {
        Quaterniond poseSpace = getPoseSpaceRotation(quaterniond, partialTicks);
        AnimatedSkeletonParent parent = this.skeleton.parent;
        return poseSpace.mul(parent.getSkeletonOrientation(partialTicks));
    }

    public Quaterniond getParentRotation(float partialTick) {
        if (this.parent != null) return this.parent.getPoseSpaceRotation(new Quaterniond(), partialTick);
        return new Quaterniond();
    }

    public void addChild(AnimationBone bone) {
        this.children.add(bone);
        this.skeleton.connections.add(new AnimationSkeleton.BoneConnection(this, bone));
        bone.parent = this;
    }

    public void setParent(AnimationBone bone) {
        this.parent = bone;
        this.skeleton.connections.add(new AnimationSkeleton.BoneConnection(this, bone));
        bone.children.add(bone);
    }
}
