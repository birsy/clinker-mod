package birsy.clinker.core.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Optional;

public class CollisionUtils {
    public static Vector3d toWorldSpace(Vector3d position, Quaterniond orientation, Vector3d vec) {
        Vector3d v = orientation.transform(vec, new Vector3d());
        return v.add(position);
    }
    public static Vector3d toLocalSpace(Vector3d position, Quaterniond orientation, Vector3d vec) {
        Vector3d v = vec.sub(position, new Vector3d());
        return orientation.transformInverse(v);
    }
    public static Optional<Vector3d> raycast(OrientableBoundingBox box, Vector3d from, Vector3d to) {
        //transform it to local space
        from = toLocalSpace(box.position, box.orientation, from);
        to = toLocalSpace(box.position, box.orientation, to);

        //preform the raycast on an unrotated AABB
        AABB aabb = new AABB(box.size.x * 0.5, box.size.y * 0.5, box.size.z * 0.5,
                             box.size.x * -0.5, box.size.y * -0.5, box.size.z * -0.5);
        Optional<Vec3> vec = aabb.clip(new Vec3(from.x, from.y, from.z), new Vec3(to.x, to.y, to.z));

        //transform it back to world space
        return vec.map(vec3 -> toWorldSpace(box.position, box.orientation, new Vector3d(vec3.x(), vec3.y(), vec3.z())));
    }

    public record OrientableBoundingBox(Vector3d size, Vector3d position, Quaterniond orientation) {}
}
