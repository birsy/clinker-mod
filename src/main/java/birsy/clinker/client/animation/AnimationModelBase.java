package birsy.clinker.client.animation;

import birsy.clinker.client.render.Mesh;
import birsy.clinker.core.util.Quaterniond;
import com.mojang.math.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AnimationModelBase {
    private final BaseBone root;
    public Map<String, BaseBone> bones;

    public AnimationModelBase(BaseBone root) {
        this.root = root;
        this.bones = new HashMap<>();
        this.bones.put(this.root.identifier, this.root);
    }

    public AnimationSkeleton createSkeleton(AnimatedSkeletonParent parent) {
        AnimationSkeleton skeleton = new AnimationSkeleton(parent, this);
        AnimationBone modelBone = root.toBone(skeleton);
        skeleton.setRoot(modelBone);
        addChildrenToSkeleton(root, modelBone, skeleton);
        return skeleton;
    }

    private void addChildrenToSkeleton(BaseBone bone, AnimationBone parent, AnimationSkeleton skeleton) {
        if (bone.children == null) return;

        for (BaseBone child : bone.children) {
            AnimationBone aBone = child.toBone(skeleton);
            parent.addChild(aBone);
            skeleton.addBone(aBone);

            this.addChildrenToSkeleton(child, aBone, skeleton);
        }
    }

    public static class AnimatedModelBuilder {
        private final AnimationModelBase model;

        private AnimatedModelBuilder(AnimationModelBase model) {
            this.model = model;
        }

        public static AnimatedModelBuilder begin(BaseBone baseRoot) {
            AnimatedModelBuilder builder = new AnimatedModelBuilder(new AnimationModelBase(baseRoot));

            return builder;
        }

        public BaseBone addBone(String parent, BaseBone bone) {
            model.bones.get(parent).addChild(bone);
            return bone;
        }

        public AnimationModelBase build() {
            return model;
        }
    }

    protected static class BaseBone {
        public BaseBone parent;
        public List<BaseBone> children;

        public float x = 0, y = 0, z = 0;
        public float scaleX = 1, scaleY = 1, scaleZ = 1;
        public Quaterniond rotation;
        public final String identifier;
        public Mesh mesh;
        public boolean dynamicMesh = false;

        private BaseBone(String identifier) {
            this.identifier = identifier;
        }


        private BaseBone addChild(BaseBone bone) {
            if (this.children == null) this.children = new ArrayList<>();
            this.children.add(bone);
            bone.parent = this;
            return this;
        }

        public BaseBone setInitialPosition(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public BaseBone setInitialScale(float x, float y, float z) {
            this.scaleX = x;
            this.scaleY = y;
            this.scaleZ = z;
            return this;
        }

        public BaseBone setInitialRotation(Quaterniond quaternion) {
            this.rotation = quaternion;
            return this;
        }

        public BaseBone setInitialRotation(Quaternion quaternion) {
            this.rotation = new Quaterniond(quaternion);
            return this;
        }

        public BaseBone addMesh(Mesh mesh) {
            this.mesh = mesh;
            return this;
        }

        public BaseBone dynamicMesh() {
            this.dynamicMesh = true;
            return this;
        }

        public AnimationBone toBone(AnimationSkeleton skeleton) {
            AnimationBone bone = this.dynamicMesh ? new DynamicallyMeshedAnimationBone(this.identifier, skeleton, this.mesh) : new AnimationBone(this.identifier, skeleton);
            bone.x = this.x; bone.y = this.y; bone.z = this.z;
            bone.prevX = this.x; bone.prevY = this.y; bone.prevZ = this.z;
            bone.scaleX = this.scaleX; bone.scaleY = this.scaleY; bone.scaleZ = this.scaleZ;
            bone.prevScaleX = this.scaleX; bone.prevScaleY = this.scaleY; bone.prevScaleZ = this.scaleZ;
            bone.rotation = this.rotation;
            bone.prevRotation = this.rotation;

            return bone;
        }
    }
}
