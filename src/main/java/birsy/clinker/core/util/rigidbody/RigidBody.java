package birsy.clinker.core.util.rigidbody;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.rigidbody.colliders.ICollisionShape;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;

public class RigidBody implements ICollidable, IBody, IPhysicsBody, ITickableBody {
    public Transform previousTransform;
    public Transform transform;
    public Vec3 velocity;
    public Vec3 angularVelocity;

    private float inverseMass;
    public float mass;
    public Matrix3f inverseInertiaTensor;

    public ICollisionShape collisionShape;
    private Vec3 localCenterOfMass;

    public RigidBody(Vec3 initialPosition, Vec3 initialVelocity, Quaternion initialOrientation, Vec3 initialAngularVelocity, float mass, Matrix3f inertiaTensor, ICollisionShape collisionShape) {
        this.transform = new Transform(initialPosition, initialOrientation);
        this.previousTransform = this.transform.copy();
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
        //Clinker.LOGGER.info(point.subtract(this.getCenterOfMass()));
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
        Quaternion orientation = MathUtils.slerp(this.previousTransform.orientation, this.transform.orientation, partialTicks);
        orientation.normalize();
        return new Transform(this.previousTransform.position.lerp(this.transform.position, partialTicks), orientation);
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;
    }


    @Override
    public void tick(float deltaTime) {
        this.previousTransform = this.transform.copy();
    }
}