package birsy.clinker.common.world.physics.rigidbody.gjkepa;


import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;
import net.minecraft.world.phys.Vec3;

/**
 * A support point for use with the GJK & EPA algorithms.
 *
 * Contains both info on the point in Minkowski difference space and the individual points on both shapes that formed
 * them, and the support directions used to find them.
 */
public class Support {
    public Vec3 difference;
    public Vec3 pointA;
    public Vec3 pointB;
    public Vec3 direction;

    /**
     * Creates a new support point.
     * @param difference The point in Minkowski difference space.
     * @param pointA The point on shape A that formed the support point.
     * @param pointB The point on shape B that formed the support point.
     * @param direction The direction used to find the point.
     */
    public Support(Vec3 difference, Vec3 pointA, Vec3 pointB, Vec3 direction) {
        this.difference = difference;
        this.pointA = pointA;
        this.pointB = pointB;
        this.direction = direction;
    }

    /**
     * Calculate the Minkowski difference on two shapes given a direction vector.
     *
     * @param a The first shape.
     * @param b The second shape.
     * @param direction The direction vector.
     * @return The Minkowski difference.
     */
    public static Vec3 minkowskiDifference(ICollisionShape a, ICollisionShape b, Vec3 direction) {
        Vec3 oppositeDirection = direction.scale(-1);
        Vec3 support = a.support(direction);
        return support.subtract(b.support(oppositeDirection));
    }

    /**
     * Generates a support point from two shapes and a direction based on the Minkowski difference of the two support points.
     *
     * @param a The first shape.
     * @param b The second shape.
     * @param direction The direction to find the support point in.
     */
    public static Support generate(ICollisionShape a, ICollisionShape b, Vec3 direction) {
        // Get opposite direction for difference
        Vec3 oppositeDirection = direction.scale(-1);

        // Calculate support points
        Vec3 supportA = a.support(direction);
        Vec3 supportB = b.support(oppositeDirection);

        // Calculate difference
        Vec3 minkowskiDifference = supportA.subtract(supportB);

        return new Support(minkowskiDifference, supportA, supportB, direction);
    }
}