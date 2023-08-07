package birsy.clinker.client.animation;

import birsy.clinker.core.util.MutableVec3;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SimulatedSkeleton {
    public final List<Joint> joints;
    public final List<Bone> bones;

    public SimulatedSkeleton() {
        this.joints = new ArrayList<>();
        this.bones = new ArrayList<>();
    }

    public void simulate(Vec3 position, Vec3 previousPosition, int iterations) {
        Vec3 deltaPosition = position.subtract(previousPosition);
        for (Joint joint : this.joints) {
            joint.update(deltaPosition);
        }
        for (int i = 0; i < iterations; i++) {
            for (Bone bone : bones) {
                bone.constrain();
            }
        }
    }

    public static class Joint {
        public boolean locked;
        public final MutableVec3 position, previousPosition;
        private final MutableVec3 velocity;

        public Joint(float x, float y, float z) {
            this.position = new MutableVec3(x, y, z);
            this.previousPosition = this.position.clone();
            this.velocity = new MutableVec3();
        }

        public void update(Vec3 deltaPosition) {
            this.previousPosition.sub(deltaPosition);
            MutableVec3 velocity = this.velocity();
            this.previousPosition.set(this.position);
            if (!this.locked) this.position.add(velocity);
        }

        public MutableVec3 velocity() {
            return velocity.set(this.position.num[0] - this.previousPosition.num[0],
                                this.position.num[1] - this.previousPosition.num[1],
                                this.position.num[2] - this.previousPosition.num[2]);
        }

        public MutableVec3 position(float partialTick) {
            return this.previousPosition.clone().lerp(position, partialTick);
        }
    }

    public static class Bone {
        public final Joint a, b;
        public float length;

        public Bone(Joint a, Joint b, float length) {
            this.a = a;
            this.b = b;
            this.length = length;
        }

        public Bone(Joint a, Joint b) {
            this.a = a;
            this.b = b;
            this.length = this.a.position.distance(b.position);
        }

        public void constrain() {
            if (a.locked && b.locked) return;

            MutableVec3 pos1 = this.a.position;
            MutableVec3 pos2 = this.b.position;

            MutableVec3 center = pos1.clone().add(pos2).mul(0.5F);
            MutableVec3 direction = pos1.clone().sub(pos2).normalize().mul(this.length * 0.5F);

            if (!a.locked) this.a.position.set(center.clone().add(direction));
            if (!b.locked) this.b.position.set(center.sub(direction));
        }
    }
}
