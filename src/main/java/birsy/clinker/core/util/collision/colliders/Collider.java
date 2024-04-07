package birsy.clinker.core.util.collision.colliders;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public interface Collider {
    Vector3d getPosition();
    Quaterniond getOrientation();
    Vector3d findFurthestPoint(Vector3d direction);
}
