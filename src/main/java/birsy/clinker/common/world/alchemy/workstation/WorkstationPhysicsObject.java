package birsy.clinker.common.world.alchemy.workstation;

import birsy.clinker.core.util.JomlConversions;
import birsy.clinker.core.util.MathUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class WorkstationPhysicsObject {
    public Vec3 position;
    protected Vec3 pPosition;
    public Quaterniond rotation;
    protected Quaterniond pRotation;
    protected Vec3 acceleration;
    protected Quaterniond torque;
    public boolean isHovered = false;

    public double mass;
    protected double inverseMass;
    //todo: figure out how to calculate this from collision shape
    protected Matrix3d inverseInertiaTensor;
    public SphereBoxCollider collider;

    public WorkstationPhysicsObject(Vec3 position, float mass, double sizeX, double sizeY, double sizeZ) {
        this.mass = mass;
        this.inverseMass = 1.0 / mass;

        this.inverseInertiaTensor = new Matrix3d();

        this.position = position;
        this.pPosition = position;
        this.rotation = new Quaterniond();
        this.pRotation = new Quaterniond();
        this.acceleration = new Vec3(0, 0, 0);
        this.torque = new Quaterniond();

        //MathUtils.min(sizeX, sizeY, sizeZ) * 0.5
        this.collider = new SphereBoxCollider(sizeX, sizeY, sizeZ, MathUtil.min(sizeX, sizeY, sizeZ) * 0.25);
    }

    public void integrate() {
        //update position
        Vec3 velocity = this.position.subtract(this.pPosition);
        velocity = velocity.add(acceleration);

        this.pPosition = this.position;
        this.position = this.position.add(velocity);
        this.acceleration = Vec3.ZERO;

        //update rotation
        Quaterniond rotationalVelocity = this.pRotation.conjugate(new Quaterniond()).mul(this.rotation).normalize();
        rotationalVelocity = rotationalVelocity.mul(torque);

        this.pRotation.set(this.rotation).normalize();
        this.rotation.mul(rotationalVelocity).normalize();
        this.torque.set(0, 0, 0, 1);

        this.collider.updateTransform(this.position, this.rotation);
    }

    // DONT FUCKING WORRY ABOUT ROTATION SHIT FOR NOW
    // todo: rotation shit
    public void push(Vector3d pos, Vector3d nudge) {
        this.position = this.position.add(JomlConversions.toMoj(nudge));
//        Vector3d currentPos = JomlConversions.toJOML(this.position);
//
//        nudge = this.rotation.transformInverse(nudge);
//        pos = this.rotation.transformInverse(pos.sub(currentPos));
//
//        Vector3d angularNudge = this.inverseInertiaTensor.transform(pos.cross(nudge, new Vector3d()));
//
//        double normalMass = -pos.cross(angularNudge, new Vector3d()).dot(nudge) / nudge.dot(nudge) + this.inverseMass;
//
//        if (nudge.dot(nudge) < Double.MIN_NORMAL) {
//            return;
//        }
//
//        double scalar = 1 / normalMass;
//
//        nudge = this.rotation.transform(nudge);
//        angularNudge = this.rotation.transform(angularNudge);
//
//        currentPos.add(nudge);
//
//        // update actual pose
//        this.position = JomlConversions.toMoj(currentPos);
//
//        // rotational push
//        this.pushRotate(angularNudge, scalar);
    }
    private void pushRotate(Vector3d angularNudge, double scalar) {
        Quaterniond q = new Quaterniond(
                angularNudge.x * scalar,
                angularNudge.y * scalar,
                angularNudge.z * scalar,
                0.0F);
        Quaterniond orientation = new Quaterniond(this.rotation);
        q.mul(orientation);

        orientation.set(orientation.x + 0.5 * q.x,  orientation.y + 0.5 * q.y,
                orientation.z + 0.5 * q.z,  orientation.w + 0.5 * q.w);
        orientation.normalize();

        this.rotation.set(orientation);
    }

    private Quaterniond clientRotation = new Quaterniond();
    public Quaterniond getRotation(float partialTicks) {
        return pRotation.slerp(rotation, partialTicks, clientRotation);
    }

    public Vec3 getPosition(float partialTicks) {
        return this.pPosition.lerp(this.pPosition, partialTicks);
    }

    public static class SphereBoxCollider {
        public List<SphereCollider> spheres;
        public AABB boundingBox;
        double cornerRadius;

        public SphereBoxCollider(double sizeX, double sizeY, double sizeZ, double cornerRadius) {
            this.cornerRadius = cornerRadius;
            this.spheres = new ArrayList<>();

            double minX = (sizeX * -0.5);
            double minY = (sizeY * -0.5);
            double minZ = (sizeZ * -0.5);
            double maxX = (sizeX * 0.5);
            double maxY = (sizeY * 0.5);
            double maxZ = (sizeZ * 0.5);

            double minCornerSphereX = minX + cornerRadius;
            double minCornerSphereY = minY + cornerRadius;
            double minCornerSphereZ = minZ + cornerRadius;
            double maxCornerSphereX = maxX - cornerRadius;
            double maxCornerSphereY = maxY - cornerRadius;
            double maxCornerSphereZ = maxZ - cornerRadius;

            //add spheres at the corners
            spheres.add(new SphereCollider(cornerRadius, minCornerSphereX, minCornerSphereY, minCornerSphereZ));
            spheres.add(new SphereCollider(cornerRadius, minCornerSphereX, minCornerSphereY, maxCornerSphereX));
            spheres.add(new SphereCollider(cornerRadius, minCornerSphereX, maxCornerSphereY, minCornerSphereZ));
            spheres.add(new SphereCollider(cornerRadius, minCornerSphereX, maxCornerSphereY, maxCornerSphereX));
            spheres.add(new SphereCollider(cornerRadius, maxCornerSphereZ, minCornerSphereY, minCornerSphereZ));
            spheres.add(new SphereCollider(cornerRadius, maxCornerSphereZ, minCornerSphereY, maxCornerSphereX));
            spheres.add(new SphereCollider(cornerRadius, maxCornerSphereZ, maxCornerSphereY, minCornerSphereZ));
            spheres.add(new SphereCollider(cornerRadius, maxCornerSphereZ, maxCornerSphereY, maxCornerSphereX));

            //todo: if the spheres don't cover enough radius, fill the insides with spheres.

            updateBoundingBox();
        }

        public void updateTransform(Vec3 position, Quaterniond orientation) {
            for (SphereCollider sphere : this.spheres) {
                sphere.position = JomlConversions.toMoj(orientation.transform(JomlConversions.toJOML(sphere.initialPosition))).add(position);
            }

            updateBoundingBox();
        }

        private void updateBoundingBox() {
            double minX = spheres.get(0).position.x, minY = spheres.get(0).position.y, minZ = spheres.get(0).position.z, maxX = spheres.get(0).position.x, maxY = spheres.get(0).position.y, maxZ = spheres.get(0).position.z;
            for (SphereCollider sphere : this.spheres) {
                if (sphere.position.x > maxX) maxX = sphere.position.x;
                if (sphere.position.y > maxY) maxY = sphere.position.y;
                if (sphere.position.z > maxZ) maxZ = sphere.position.z;
                if (sphere.position.x < minX) minX = sphere.position.x;
                if (sphere.position.y < minY) minY = sphere.position.y;
                if (sphere.position.z < minZ) minZ = sphere.position.z;
            }
            this.boundingBox = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(this.cornerRadius);
        }

        // returns the correction vector.
        @Nullable
        public CollisionManifold collide(AABB aabb) {
            if (!aabb.intersects(this.boundingBox)) return null;
            aabb = aabb.inflate(cornerRadius);
            for (SphereCollider sphere : this.spheres) {
                if (aabb.contains(sphere.position)) {
                    //we are colliding!
                    Vec3 pointOnSurface = MathUtil.closestPointOnAABB(sphere.position, aabb);

                    Vec3 point = sphere.position.subtract(pointOnSurface).normalize().scale(sphere.radius);
                    Vec3 adjustment = pointOnSurface.subtract(sphere.position);
                    return new CollisionManifold(point, adjustment);
                }
            }
            return null;
        }
    }


    public static class SphereCollider {
        final Vec3 initialPosition;
        public Vec3 position;
        public double radius;

        public SphereCollider(double radius, Vec3 position) {
            this.radius = radius;
            this.position = position;
            this.initialPosition = position;
        }

        public SphereCollider(double radius, double x, double y, double z) {
            this.radius = radius;
            this.position = new Vec3(x, y, z);
            this.initialPosition = new Vec3(x, y, z);
        }
    }

    record CollisionManifold(Vec3 point, Vec3 adjustment) {}
}
