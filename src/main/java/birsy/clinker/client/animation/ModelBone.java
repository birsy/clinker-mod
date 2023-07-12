package birsy.clinker.client.animation;

import birsy.clinker.client.animation.constraint.AnimationConstraint;
import birsy.clinker.client.animation.simulation.Ligament;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ModelBone {
    public final ModelSkeleton skeleton;
    public ModelBone parent;
    public List<ModelBone> children;
    public Map<String, AnimationConstraint> constraints;

    protected Ligament[] associatedLigaments;
    protected Vector3f ligamentCenter;
    protected Vector3f[] relativeLigamentPositions;

    protected float softness = 0.0F;

    // in model space
    public float x, y, z;
    public float prevX, prevY, prevZ;
    // relative to parent
    public float targetX, targetY, targetZ;

    // only really supported on leaf bones
    public float scaleX, scaleY, scaleZ;
    public float prevScaleX, prevScaleY, prevScaleZ;

    // in model space
    public Quaternionf rotation;
    public Quaternionf prevRotation;
    // relative to parent
    public Quaternionf targetRotation;

    // this will still render all children
    public boolean skipRender = false;
    // this will not
    public boolean visible = true;

    public final String identifier;

    public ModelBone(String identifier, ModelSkeleton skeleton, Ligament... ligaments) {
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

    public Quaternionf rotation(float partialTicks) {
        return prevRotation.slerp(rotation, partialTicks);
    }
    public Quaternion mojRotation(float partialTicks) {
        return prevRotation.slerp(rotation, partialTicks).toMojangQuaternion();
    }

    public void transform(PoseStack transform, float partialTicks) {
        transform.translate(this.x(partialTicks), this.y(partialTicks), this.z(partialTicks));
        transform.scale(this.scaleX(partialTicks), this.scaleY(partialTicks), this.scaleZ(partialTicks));
        transform.mulPose(this.mojRotation(partialTicks));
    }

    public void getPoseSpaceTransform(PoseStack transform, float partialTicks) {
        if (this.parent != null) this.parent.getPoseSpaceTransform(transform, partialTicks);
        this.transform(transform, partialTicks);
    }

    public void getGlobalTransform(PoseStack transform, float partialTicks) {
        this.getPoseSpaceTransform(transform, partialTicks);
        ModelSkeletonParent parent = this.skeleton.parent;
        Quaternionf orientation = parent.getSkeletonOrientation(partialTicks);
        transform.mulPose(orientation.toMojangQuaternion());

        Vec3 position = parent.getSkeletonPosition(partialTicks);
        transform.translate(position.x, position.y, position.z);
    }

    public Quaternionf getPoseSpaceRotation(Quaternionf quaterniond, float partialTicks) {
        if (this.parent != null) this.parent.getPoseSpaceRotation(quaterniond, partialTicks);
        quaterniond.mul(this.prevRotation.slerp(this.rotation, partialTicks));
        return quaterniond;
    }

    public Quaternionf getGlobalRotation(Quaternionf quaterniond, float partialTicks) {
        Quaternionf poseSpace = getPoseSpaceRotation(quaterniond, partialTicks);
        ModelSkeletonParent parent = this.skeleton.parent;
        return poseSpace.mul(parent.getSkeletonOrientation(partialTicks));
    }

    public Quaternionf getParentRotation(float partialTick) {
        if (this.parent != null) return this.parent.getPoseSpaceRotation(new Quaternionf(), partialTick);
        return new Quaternionf();
    }

    public void addChild(ModelBone bone) {
        this.children.add(bone);
        bone.parent = this;
    }

    public void setParent(ModelBone bone) {
        this.parent = bone;
        bone.children.add(bone);
    }
}
