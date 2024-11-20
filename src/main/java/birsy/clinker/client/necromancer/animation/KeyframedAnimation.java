package birsy.clinker.client.necromancer.animation;

import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

import java.util.*;

public abstract class KeyframedAnimation<P extends SkeletonParent, T extends Skeleton<P>> extends Animation<P, T> {
    final boolean looped;
    final boolean additive;
    final Map<String, BoneTimeline> timelines;

    protected KeyframedAnimation(boolean looped, boolean additive) {
        this.looped = looped;
        this.additive = additive;
        this.timelines = new HashMap<>();
    }

    public boolean running(P parent, T skeleton, float mixFactor, float time) {
        return mixFactor > 0;
    }

    public void apply(P parent, T skeleton, float mixFactor, float time) {
        if (this.looped) time = time % 1;

        for (Map.Entry<String, BoneTimeline> entry : this.timelines.entrySet()) {
            String boneID = entry.getKey();
            BoneTimeline timeline = entry.getValue();

            if (!skeleton.bones.containsKey(boneID)) continue;
            Bone bone = skeleton.bones.get(boneID);

            // special case for first or last keyframes
            Keyframe first = timeline.keyframes.get(0);
            Keyframe last = timeline.keyframes.get(timeline.keyframes.size() - 1);
            if (time < first.time) {
                if (this.looped) applyMixedKeyframes(bone, last, first, (time - (last.time - 1)) / (first.time - (last.time - 1)), mixFactor);
                else first.transform.apply(bone, mixFactor, additive);
                continue;
            } else if (time > last.time) {
                if (this.looped) applyMixedKeyframes(bone, last, first, (time - last.time) / ((first.time + 1) - last.time), mixFactor);
                else last.transform.apply(bone, mixFactor, additive);
                continue;
            }

            // todo: replace with binary search
            for (int i = 0; i < timeline.keyframes.size() - 1; i++) {
                Keyframe prev = timeline.keyframes.get(i);
                Keyframe next = timeline.keyframes.get(i + 1);
                if (next.time > time) {
                    applyMixedKeyframes(bone, prev, first, (time - prev.time) / (next.time - prev.time), mixFactor);
                    break;
                }
            }
        }
    }

    private void applyMixedKeyframes(Bone bone, Keyframe prevKeyframe, Keyframe nextKeyframe, float keyframeMixFactor, float poseMixFactor) {
        BoneTransform transform = BoneTransform.compose(prevKeyframe.transform, nextKeyframe.transform, keyframeMixFactor);
        transform.apply(bone, poseMixFactor, additive);
    }

    protected static class BoneTimeline {
        final List<Keyframe> keyframes;

        protected BoneTimeline(String boneID) {
            this.keyframes = new ArrayList<>();
        }
    }

    protected static class Keyframe {
        final float time;
        final BoneTransform transform;

        protected Keyframe(float time, BoneTransform transform) {
            this.time = time;
            this.transform = transform;
        }
    }

    private static final Quaternionf tempA = new Quaternionf();
    private static final Quaternionf tempB = new Quaternionf();
    protected record BoneTransform(float x, float y, float z, float qx, float qy, float qz, float qw, float sx, float sy, float sz) {
        void apply(Bone bone, float factor, boolean additive) {
            if (additive) {
                bone.x += this.x * factor;
                bone.y += this.y * factor;
                bone.z += this.z * factor;
                bone.xSize *= Mth.lerp(factor, 1, this.sx);
                bone.ySize *= Mth.lerp(factor, 1, this.sy);
                bone.zSize *= Mth.lerp(factor, 1, this.sz);
                tempA.set(this.qx, this.qy, this.qz, this.qw);
                tempA.slerp(tempB.identity(), 1 - factor);
                bone.rotation.premul(tempA);
            } else {
                bone.x = Mth.lerp(factor, bone.x, this.x);
                bone.y = Mth.lerp(factor, bone.y, this.y);
                bone.z = Mth.lerp(factor, bone.z, this.z);
                bone.xSize = Mth.lerp(factor, bone.xSize, this.sx);
                bone.ySize = Mth.lerp(factor, bone.ySize, this.sy);
                bone.zSize = Mth.lerp(factor, bone.zSize, this.sz);
                tempA.set(this.qx, this.qy, this.qz, this.qw);
                bone.rotation.slerp(tempA, factor);
            }
        }

        static BoneTransform compose(BoneTransform a, BoneTransform b, float factor) {
            float x = Mth.lerp(factor, a.x, b.x);
            float y = Mth.lerp(factor, a.y, b.y);
            float z = Mth.lerp(factor, a.z, b.z);
            float sx = Mth.lerp(factor, a.sx, b.sx);
            float sy = Mth.lerp(factor, a.sy, b.sy);
            float sz = Mth.lerp(factor, a.sz, b.sz);
            tempA.set(a.qx, a.qy, a.qz, a.qw);
            tempB.set(b.qx, b.qy, b.qz, b.qw);
            tempA.slerp(tempB, factor);

            return new BoneTransform(x, y, z, tempA.x, tempA.y, tempA.z, tempA.w, sx, sy, sz);
        }
    }
}
