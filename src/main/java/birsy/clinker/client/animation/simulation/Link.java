package birsy.clinker.client.animation.simulation;

import birsy.clinker.client.animation.ModelSkeleton;
import com.mojang.math.Vector3f;

public class Link {
    public final ModelSkeleton skeleton;
    public final Ligament joint1;
    public final Ligament joint2;
    private final float length;

    public Link(Ligament joint1, Ligament joint2, float length) {
        this.joint1 = joint1;
        this.joint2 = joint2;

        if (joint1.skeleton == joint2.skeleton) {
            skeleton = joint1.skeleton;
        } else {
            throw new IllegalArgumentException("Joints do not share associated parents!");
        }

        this.length = length;
    }

    public void shove(Vector3f shove) {
        this.joint1.shove(shove);
        this.joint2.shove(shove);
    }

    public void apply() {
        Vector3f pos1 = this.joint1.position;
        Vector3f pos2 = this.joint2.position;

        Vector3f center = pos1.copy();
        center.add(pos2);
        center.mul(0.5F);

        Vector3f direction = pos1.copy();
        direction.sub(pos2);
        direction.normalize();
        direction.mul(this.length * 0.5F);

        this.joint1.position = center.copy();
        this.joint1.position.add(direction);
        this.joint2.position = center;
        this.joint2.position.sub(direction);
    }
}
