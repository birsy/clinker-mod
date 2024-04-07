package birsy.clinker.core.util.collision.colliders;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public class BoxCollider extends MeshCollider {
    Vector3d size;

    public BoxCollider(Vector3d position, Quaterniond orientation, Vector3d size) {
        super(position, orientation,
                size.mul( 1,  1,  1, new Vector3d()),
                size.mul( 1,  1, -1, new Vector3d()),
                size.mul( 1, -1,  1, new Vector3d()),
                size.mul( 1, -1, -1, new Vector3d()),
                size.mul(-1,  1,  1, new Vector3d()),
                size.mul(-1,  1, -1, new Vector3d()),
                size.mul(-1, -1,  1, new Vector3d()),
                size.mul(-1, -1, -1, new Vector3d()));
        this.size = size;
    }

    public Vector3d getSize() {
        return new Vector3d(size);
    }
}
