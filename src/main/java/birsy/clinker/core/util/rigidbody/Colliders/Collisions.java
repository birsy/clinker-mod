package birsy.clinker.core.util.rigidbody.Colliders;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.rigidbody.RigidBody;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Collisions {
    private static final double EPSILON = 0.00001;

    public static boolean BoxBoxIntersection0(BoxCollider c1, BoxCollider c2) {
        // Calculate the orientation of this box in world space
        Quaternion worldOrientation = new Quaternion(c1.orientation);
        worldOrientation.mul(MathUtils.inverse(c2.orientation));

        // Calculate the minimum and maximum values for each axis of this box in world space
        Vec3 min = MathUtils.rotate(worldOrientation, new Vec3(-c1.dimensions.x / 2, -c1.dimensions.y / 2, -c1.dimensions.z / 2)).add(c1.position);
        Vec3 max = MathUtils.rotate(worldOrientation, new Vec3(c1.dimensions.x / 2, c1.dimensions.y / 2, c1.dimensions.z / 2)).add(c1.position);

        // Calculate the minimum and maximum values for each axis of the other box in world space
        Vec3 otherMin = MathUtils.rotate(c2.orientation, new Vec3(-c2.dimensions.x / 2, -c2.dimensions.y / 2, -c2.dimensions.z / 2)).add(c2.position);
        Vec3 otherMax = MathUtils.rotate(c2.orientation, new Vec3(c2.dimensions.x / 2, c2.dimensions.y / 2, c2.dimensions.z / 2)).add(c2.position);

        // Check if the two boxes are intersecting along each axis
        return (min.x <= otherMax.x && max.x >= otherMin.x) &&
                (min.y <= otherMax.y && max.y >= otherMin.y) &&
                (min.z <= otherMax.z && max.z >= otherMin.z);
    }

    public static boolean BoxBoxIntersection(BoxCollider box1, BoxCollider box2) {
        List<Vec3> vertices1 = box1.getVertices();
        List<Vec3> vertices2 = box2.getVertices();

        List<Vec3> normals1 = box1.getNormals();
        List<Vec3> normals2 = box2.getNormals();

        for (Vec3 normal : normals1) {
            double min1 = Double.MAX_VALUE;
            double max1 = Double.MIN_VALUE;
            for (Vec3 vertex : vertices1) {
                double projection = vertex.dot(normal);
                min1 = Math.min(min1, projection);
                max1 = Math.max(max1, projection);
            }

            double min2 = Double.MAX_VALUE;
            double max2 = Double.MIN_VALUE;
            for (Vec3 vertex : vertices2) {
                double projection = vertex.dot(normal);
                min2 = Math.min(min2, projection);
                max2 = Math.max(max2, projection);
            }

            if (max1 < min2 || max2 < min1) {
                return false;
            }
        }

        for (Vec3 normal : normals2) {
            double min1 = Double.MAX_VALUE;
            double max1 = Double.MIN_VALUE;
            for (Vec3 vertex : vertices1) {
                double projection = vertex.dot(normal);
                min1 = Math.min(min1, projection);
                max1 = Math.max(max1, projection);
            }

            double min2 = Double.MAX_VALUE;
            double max2 = Double.MIN_VALUE;
            for (Vec3 vertex : vertices2) {
                double projection = vertex.dot(normal);
                min2 = Math.min(min2, projection);
                max2 = Math.max(max2, projection);
            }

            if (max1 < min2 || max2 < min1) {
                return false;
            }
        }

        return true;
    }
    
    public static boolean BoxSphereIntersection(BoxCollider c1, SphereCollider c2) {
        // Calculate the orientation of this box in world space
        Quaternion worldOrientation = new Quaternion(c1.orientation);
        worldOrientation.mul(MathUtils.inverse(c2.orientation));

        // Calculate the minimum and maximum values for each axis of this box in world space
        Vec3 min = MathUtils.rotate(worldOrientation, new Vec3(-c1.dimensions.x / 2, -c1.dimensions.y / 2, -c1.dimensions.z / 2)).add(c1.position);
        Vec3 max = MathUtils.rotate(worldOrientation, new Vec3(c1.dimensions.x / 2, c1.dimensions.y / 2, c1.dimensions.z / 2)).add(c1.position);

        // Calculate the distance between the center of the sphere and the closest point on the box
        Vec3 closestPoint = MathUtils.rotate(MathUtils.inverse(worldOrientation), c2.position.subtract(c1.position));
        closestPoint = new Vec3(
                Math.min(closestPoint.x, c1.dimensions.x / 2),
                Math.min(closestPoint.y, c1.dimensions.y / 2),
                Math.min(closestPoint.z, c1.dimensions.z / 2)
        );

        // Check if the distance between the center of the sphere and the closest point on the box is less than the radius of the sphere
        return c2.position.subtract(closestPoint).lengthSqr() <= c2.radius * c2.radius;
    }

    public static boolean SphereSphereIntersection(SphereCollider c1, SphereCollider c2) {
        return c1.position.distanceTo(c2.position) < (c1.radius + c2.radius);
    }

    //TODO : this.
    /*public void processCollision(RigidBody body1, RigidBody body2) {
        // Calculate the normal vector between the two colliding bodies
        Vec3 normal = body1.position.subtract(body2.position).normalize();

        // Calculate the relative velocity of the two colliding bodies along the normal vector
        Vec3 relativeVelocity = body1.velocity.add(body1.angularVelocity.cross(body1.getCollisionVolume().getContactPoint())).subtract(
                body2.velocity.add(body2.angularVelocity.cross(body2.getCollisionVolume().getContactPoint())));
        float relativeVelocityAlongNormal = (float) relativeVelocity.dot(normal);

        // Check if the relative velocity of the two bodies is moving towards each other
        if (relativeVelocityAlongNormal < 0) {
            // Calculate the impulse needed to stop the bodies from penetrating
            float impulse = -(1 + 0.8f) * relativeVelocityAlongNormal / (body1.inverseMass + body2.inverseMass);

            // Calculate the contact point on each body
            Vec3 contactPoint1 = body1.getCollisionVolume().getContactPoint();
            Vec3 contactPoint2 = body2.getCollisionVolume().getContactPoint();

            // Calculate the change in velocity for each body
            Vec3 velocityChange1 = normal.scale(impulse * body1.inverseMass);
            Vec3 velocityChange2 = normal.scale(impulse * body2.inverseMass);

            body1.applyImpulse(velocityChange1);

            // Calculate the change in angular velocity for each body
            Vec3 angularVelocityChange1 = body1.inverseInertiaTensor.transform(contactPoint1.cross(normal).multiply(impulse));
            Vec3 angularVelocityChange2 = body2.inverseInertiaTensor.transform(contactPoint2.cross(normal).multiply(impulse));

            // Apply the changes in velocity and angular velocity to each body
            body1.setVelocity(body1.velocity.add(velocityChange1));
            body1.setAngularVelocity(body1.angularVelocity.add(angularVelocityChange1));
            body2.setVelocity(body2.velocity.add(velocityChange2));
            body2.setAngularVelocity(body2.angularVelocity.add(angularVelocityChange2));
        }
    }*/
}
