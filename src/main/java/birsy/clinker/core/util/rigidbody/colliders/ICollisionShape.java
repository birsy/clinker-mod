package birsy.clinker.core.util.rigidbody.colliders;

import birsy.clinker.core.util.rigidbody.Transform;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Interface for convex shapes, desgined for use with GJK.
 *
 * @author RyanHCode
 */
public interface ICollisionShape {
    /**
     * Returns the smallest AABB that contains the entire shape.
     *
     * @return The smallest AABB that contains the entire shape.
     */
    AABB getBounds();

    /**
     * Given a direction vector, return the furthest point in that direction.
     *
     * @param direction The direction vector.
     * @return The furthest point in that direction.
     */
    Vec3 support(Vec3 direction);

    /**
     * Returns the center of the shape.
     *
     * @return The center of the shape.
     */
    Vec3 getCenter();

    void setTransform(Transform transform);

    Transform getTransform();

    Vec3 applyTransform(Vec3 vec);
}
