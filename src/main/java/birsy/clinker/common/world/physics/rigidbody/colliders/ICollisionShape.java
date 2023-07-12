package birsy.clinker.common.world.physics.rigidbody.colliders;

import birsy.clinker.common.world.physics.rigidbody.Transform;
import net.minecraft.nbt.CompoundTag;
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

    /**
     * Returns the maximum distance a point gets from the transform center.
     *
     * @return the maximum distance a point gets from the transform center.
     */
    double getRadius();

    default CompoundTag serialize() {
        return this.serialize(new CompoundTag());
    }
    CompoundTag serialize(CompoundTag tag);
    ICollisionShape deserialize(CompoundTag tag);
}
