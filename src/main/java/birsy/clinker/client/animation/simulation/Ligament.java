package birsy.clinker.client.animation.simulation;

import birsy.clinker.client.animation.ModelSkeleton;
import birsy.clinker.common.world.physics.particle.LinkConstraint;
import com.mojang.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Ligament {
    public final ModelSkeleton skeleton;
    public Vector3f pPosition;
    public Vector3f position;
    public boolean locked = false;

    private Vector3f pushes;
    private Vector3f acceleration;
    public double radius;
    public final List<LinkConstraint> linkConstraints;

    public Ligament (ModelSkeleton skeleton) {
        this.skeleton = skeleton;
        this.linkConstraints = new ArrayList<>();
        this.pushes = Vector3f.ZERO;
        this.pPosition = Vector3f.ZERO;
        this.position = Vector3f.ZERO;
        this.acceleration = Vector3f.ZERO;
    }

    public void tick (float deltaTime) {
        if (locked) return;

        Vector3f velocity = this.getDeltaMovement();

        velocity.add(pushes);
        this.pushes = Vector3f.ZERO;

        this.pPosition = this.position;
        this.position.add(velocity);
        this.acceleration.mul(deltaTime * deltaTime);
        this.position.add(this.acceleration);

        this.acceleration = Vector3f.ZERO;
    }

    public void accelerate (Vector3f amount) {
        this.acceleration.add(amount);
    }

    protected void shove(Vector3f amount) {
        this.shove(amount.x(), amount.y(), amount.z());
    }

    protected void shove(float x, float y, float z) {
        pushes.add(x, y, z);
    }

    protected Vector3f getDeltaMovement () {
        Vector3f velocity = this.position.copy();
        velocity.sub(this.pPosition);
        return velocity;
    }
}
