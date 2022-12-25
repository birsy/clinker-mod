package birsy.clinker.core.util.rigidbody;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.rigidbody.Colliders.Collider;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;

public class RigidBody {
    // Position of the rigidbody in world space
    public Vec3 position;

    // Velocity of the rigidbody in world space
    public Vec3 velocity;

    // Angular velocity of the rigidbody in world space
    public Vec3 angularVelocity;

    // Orientation of the rigidbody in world space
    public Quaternion orientation;

    // Inverse mass of the rigidbody
    public float inverseMass;

    // Inverse inertia tensor of the rigidbody
    public Matrix3f inverseInertiaTensor;

    // Box representing the collision volume of the rigidbody
    public Collider collisionVolume;

    public RigidBody(Vec3 initialPosition, Vec3 initialVelocity, Quaternion initialOrientation, Vec3 initialAngularVelocity, float mass, Matrix3f inertiaTensor, Collider collisionVolume) {
        this.position = initialPosition;
        this.velocity = initialVelocity;
        this.angularVelocity = initialAngularVelocity;
        this.orientation = initialOrientation;
        this.inverseMass = 1.0f / mass;
        inertiaTensor.invert();
        this.inverseInertiaTensor = inertiaTensor;
        this.collisionVolume = collisionVolume;
    }

    // Apply a force to the rigidbody at a given point in world space
    public void applyImpulse(Vec3 force, Vec3 point) {
        // Update the linear velocity of the rigidbody
        velocity = velocity.add(force.scale(inverseMass));

        // Update the angular velocity of the rigidbody
        Vec3 r = point.subtract(position);
        angularVelocity = angularVelocity.add(MathUtils.multiply(r.cross(force), inverseInertiaTensor));
    }

    // Integrate the rigidbody's motion over a given time step
    public void integrate(float dt) {
        // Update the position of the rigidbody using its linear velocity
        position = position.add(velocity.scale(dt));

        // Update the orientation of the rigidbody using its angular velocity
        orientation = MathUtils.addVector(orientation, angularVelocity.scale(dt));

        // Normalize the orientation quaternion to avoid drift
        orientation.normalize();
    }
}