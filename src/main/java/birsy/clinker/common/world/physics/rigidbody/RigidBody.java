package birsy.clinker.common.world.physics.rigidbody;

import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;
import birsy.clinker.core.util.JomlConversions;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4d;
import org.joml.Matrix3d;
import org.joml.Quaterniond;

public class RigidBody {
    public Vec3 position;
    protected Vec3 pPosition;
    public Quaterniond rotation;
    protected Quaterniond pRotation;
    protected Vec3 acceleration;
    protected Quaterniond torque;


    public double mass;
    protected double inverseMass;
    //todo: figure out how to calculate this from collision shape
    protected Matrix3d inverseInertiaTensor;
    private Transform colliderTransform;
    public ICollisionShape collider;

    public RigidBody(float mass, ICollisionShape shape) {
        this.mass = mass;
        this.inverseMass = 1.0 / mass;

        this.inverseInertiaTensor = new Matrix3d();

        this.position = new Vec3(0, 0, 0);
        this.pPosition = new Vec3(0, 0, 0);
        this.rotation = new Quaterniond();
        this.pRotation = new Quaterniond();
        this.acceleration = new Vec3(0, 0, 0);
        this.torque = new Quaterniond();

        this.colliderTransform = new Transform(this.position, this.rotation);
        this.collider = shape;
    }

    public void integrate() {
        //update position
        Vec3 velocity = this.position.subtract(this.pPosition);
        velocity = velocity.add(acceleration);

        this.pPosition = this.position;
        this.position = this.position.add(velocity);
        this.acceleration = Vec3.ZERO;

        //update rotation
        Quaterniond rotationalVelocity = this.pRotation.difference(this.rotation);
        rotationalVelocity = rotationalVelocity.mul(torque);

        this.pRotation.set(this.rotation);
        this.rotation.mul(rotationalVelocity);
        this.torque.set(0, 0, 0, 1);

        this.colliderTransform.setPosition(this.position);
        this.colliderTransform.setOrientation(this.rotation);
        this.collider.setTransform(colliderTransform);
    }

    public void push(Vec3 force, Vec3 point) {
        this.acceleration = this.acceleration.add(force);

        Vec3 towardsPoint = this.position.subtract(point);
        Vec3 torqueVector = towardsPoint.cross(force);
        torqueVector = JomlConversions.toMojang(inverseInertiaTensor.transform(JomlConversions.toJOML(torqueVector)));
        torque = torque.mul(new Quaterniond(new AxisAngle4d(torqueVector.length(), JomlConversions.toJOML(torqueVector).normalize())));
    }

    private Quaterniond clientRotation = new Quaterniond();
    public Quaterniond getRotation(float partialTicks) {
        return pRotation.slerp(rotation, partialTicks, clientRotation);
    }

    public Vec3 getPosition(float partialTicks) {
        return this.pPosition.lerp(this.pPosition, partialTicks);
    }
}
