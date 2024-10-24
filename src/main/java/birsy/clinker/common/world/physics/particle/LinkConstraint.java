package birsy.clinker.common.world.physics.particle;

import birsy.clinker.core.util.JomlConversions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class LinkConstraint<C extends CollidingParticle> extends Constraint {
    public final C joint1;
    public final C joint2;
    public double pLength;
    public double length;
    public double stiffness;

    public LinkConstraint(ParticleParent parent, C joint1, C joint2, double length) {
        super(parent);
        this.joint1 = joint1;
        this.joint1.addConstraint(this);
        this.joint2 = joint2;
        this.joint2.addConstraint(this);
        this.pLength = length;
        this.length = length;
    }

    public void tick() {
        this.pLength = this.length;
    }

    public void remove() {
        this.joint1.removeConstraint(this);
        this.joint2.removeConstraint(this);
        this.parent.removeConstraint(this);
    }

    public Vec3 getCenter(double partialTick) {
        return joint1.getPosition(partialTick).add(joint2.getPosition(partialTick)).scale(0.5);
    }

    public Vec3 getVector(double partialTick) {
        return joint1.getPosition(partialTick).subtract(joint2.getPosition(partialTick));
    }

    public Vec3 getDirection(double partialTick) {
        return this.getVector(partialTick).normalize();
    }

    public double getLength(double partialTick) {
        return Mth.lerp(partialTick, pLength, length);
    }

    public Quaterniond getOrientation(double partialTick, Vec3 forward) {
        return new Quaterniond().lookAlong(JomlConversions.toJOML(joint1.getPosition(partialTick).subtract(joint2.getPosition(partialTick))), new Vector3d(0, 1, 0)).normalize();
    }

    public void push(Vec3 push) {
        this.joint1.push(push);
        this.joint2.push(push);
    }

    public void apply() {
        Vec3 pos1 = this.joint1.physicsNextPosition;
        Vec3 pos2 = this.joint2.physicsNextPosition;

        Vec3 center = pos1.add(pos2).scale(0.5);
        Vec3 direction = pos1.subtract(pos2).normalize();

        this.joint1.physicsNextPosition = center.add(direction.scale(this.length * 0.5));
        this.joint2.physicsNextPosition = center.subtract(direction.scale(this.length * 0.5));
    }
}
