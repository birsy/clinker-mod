package birsy.clinker.common.world.physics.rigidbody;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;
import com.mojang.math.Matrix3f;
import net.minecraft.world.phys.Vec3;

public class VerletRigidBody implements ICollidable, IBody, IPhysicsBody, ITickableBody {
    public Transform lastTickTransform;

    public Transform pTransform;
    public Transform transform;

    public Vec3 acceleration;
    public Vec3 torque;

    private float inverseMass;
    public float mass;
    public Matrix3f inverseInertiaTensor;

    public ICollisionShape collisionShape;
    private Vec3 localCenterOfMass;

    public VerletRigidBody(Vec3 initialPosition, Quaterniond initialOrientation, float mass, Matrix3f inertiaTensor, ICollisionShape collisionShape) {
        this.transform = new Transform(initialPosition, initialOrientation);
        this.pTransform = this.transform.copy();

        this.lastTickTransform = this.transform.copy();
        this.acceleration = Vec3.ZERO;
        this.torque = Vec3.ZERO;

        this.mass = mass;
        this.inverseMass = 1.0f / mass;
        inertiaTensor.invert();

        this.inverseInertiaTensor = inertiaTensor;
        this.collisionShape = collisionShape;
        this.collisionShape.setTransform(this.transform);
        this.localCenterOfMass = collisionShape.getCenter();
    }

    // integrate the rigidbody's motion over a given time step
    public void integrate(float dt) {
        Vec3 velocity = this.transform.position.subtract(this.pTransform.position);
        this.pTransform = this.transform.copy();

        // update positions
        this.transform.setPosition(this.transform.position.add(velocity).add(acceleration.scale(dt * dt)));
        this.collisionShape.setTransform(this.transform);

        // todo: rotations (again...)

        // resetting forces
        this.acceleration = Vec3.ZERO;
        this.torque = Vec3.ZERO;
    }

    public void accelerate(Vec3 acc, Vec3 point) {
        this.acceleration = this.acceleration.add(acc);

        Vec3 r = point.subtract(this.getCenterOfMass());
        Vec3 t = r.cross(acc);
        this.torque = this.torque.add(t);
    }

    //i dont remember whjat this is
    public void applyImpulse(Vec3 nudge, Vec3 pos) {
        // clamping the force to avoid very unstable behavior.
        float maximumLength = 0.001F;
        if (nudge.length() > maximumLength) {
            nudge = nudge.normalize().scale(maximumLength);
        }

        // pos is global
        // correction is global
        nudge = this.transform.rotateInverse(nudge);
        pos = this.transform.rotateInverse(pos.subtract(this.transform.getPosition()));

        Vec3 angularNudge = MathUtils.multiply(pos.cross(nudge), inverseInertiaTensor);
        double normalMass = -1 * pos.cross(angularNudge).dot(nudge) / nudge.dot(nudge) + inverseMass;

        if(nudge.dot(nudge) < Double.MIN_NORMAL) return;

        double stepSize = 1 / normalMass;

        nudge = this.transform.rotate(nudge);
        angularNudge = this.transform.rotate(angularNudge);

        this.transform.addPosition(nudge.scale(stepSize * inverseMass));
        //this.rotate(angularNudge, stepSize);
    }


        @Override
    public Vec3 getCenterOfMass() {
        return this.transform.position;
    }

    @Override
    public float mass() {
        return mass;
    }


    public ICollisionShape getCollisionShape() {
        return this.collisionShape;
    }

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    public Transform getTransform(float partialTicks) {
        return this.lastTickTransform.lerp(this.transform, partialTicks);
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;
    }


    @Override
    public void tick(float deltaTime) {
        this.lastTickTransform = this.transform.copy();
    }
}