package birsy.clinker.client.render.entity.model.base;

import birsy.clinker.core.util.Quaterniond;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AnimatedModelBone {
    public AnimatedModelBone parent;
    public List<AnimatedModelBone> children;

    public float x, y, z;
    public float prevX, prevY, prevZ;

    public float scaleX, scaleY, scaleZ;
    public float prevScaleX, prevScaleY, prevScaleZ;

    public Quaterniond prevRotation;
    public Quaterniond rotation;

    public boolean visible = true;
    public boolean skipRender = false;

    public final String identifier;

    public AnimatedModelBone(String identifier) {
        this.identifier = identifier;
    }

    private void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.prevScaleX = this.scaleX;
        this.prevScaleY = this.scaleY;
        this.prevScaleZ = this.scaleZ;
        this.prevRotation = this.rotation.clone();
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

    public void globalTransform(PoseStack transform, float partialTicks) {
        if (this.parent != null) this.parent.globalTransform(transform, partialTicks);
        this.transform(transform, partialTicks);
    }

    public Quaterniond getGlobalRotation(Quaterniond quaterniond) {
        if (this.parent != null) this.parent.getGlobalRotation(quaterniond);
        quaterniond.mul(this.rotation);
        return quaterniond;
    }

    public Quaterniond getParentRotation() {
        if (this.parent != null) return this.parent.getGlobalRotation(new Quaterniond());
        return new Quaterniond();
    }

    public void addChild(AnimatedModelBone bone) {
        this.children.add(bone);
        bone.parent = this;
    }

    public void setParent(AnimatedModelBone bone) {
        this.parent = bone;
        bone.children.add(bone);
    }
}
