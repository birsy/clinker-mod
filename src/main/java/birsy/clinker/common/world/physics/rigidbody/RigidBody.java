package birsy.clinker.common.world.physics.rigidbody;

import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;
import com.mojang.math.Matrix3f;
import net.minecraft.world.phys.Vec3;

public class RigidBody implements ICollidable, IBody, IPhysicsBody, ITickableBody {
    public Transform lastTickTransform;
    public Transform transform;
    public Vec3 velocity;
    public Vec3 angularVelocity;

    private float inverseMass;
    public float mass;
    public Matrix3f inverseInertiaTensor;

    public ICollisionShape collisionShape;
    private Vec3 localCenterOfMass;

    public RigidBody(Vec3 initialPosition, Vec3 initialVelocity, Quaterniond initialOrientation, Vec3 initialAngularVelocity, float mass, Matrix3f inertiaTensor, ICollisionShape collisionShape) {
        this.transform = new Transform(initialPosition, initialOrientation);
        this.lastTickTransform = this.transform.copy();
        this.velocity = initialVelocity;
        this.angularVelocity = initialAngularVelocity;
        this.inverseMass = 1.0f / mass;
        this.mass = mass;
        inertiaTensor.invert();
        this.inverseInertiaTensor = inertiaTensor;
        this.collisionShape = collisionShape;
        this.collisionShape.setTransform(this.transform);
        this.localCenterOfMass = collisionShape.getCenter();
    }

    // Apply a force to the rigidbody at a given point in world space
    public void applyImpulse(Vec3 force, Vec3 point) {
        velocity = velocity.add(force.scale(inverseMass));

        Vec3 r = point.subtract(this.getCenterOfMass());
        Vec3 torque = r.cross(force);
        this.angularVelocity = this.angularVelocity.add(torque);
    }

    @Override
    public void accelerate(Vec3 force, Vec3 point) {
        applyImpulse(force.scale(mass), point);
    }

    @Override
    public Vec3 getCenterOfMass() {
        return this.transform.position;
    }

    @Override
    public float mass() {
        return mass;
    }

    // Integrate the rigidbody's motion over a given time step
    public void integrate(float dt) {
        this.transform.addPosition(velocity.scale(dt));
        this.transform.addOrientation(angularVelocity.scale(dt));
        this.collisionShape.setTransform(this.transform);
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