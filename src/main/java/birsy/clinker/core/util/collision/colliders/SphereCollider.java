package birsy.clinker.core.util.collision.colliders;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public class SphereCollider implements Collider {
    Vector3d position;
    private static Quaterniond noOrientation = new Quaterniond();

    public SphereCollider(double x, double y, double z) {
        this(new Vector3d(x, y, z));
    }

    public SphereCollider(Vector3d position) {
        this.position = position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    @Override
    public Vector3d getPosition() {
        return this.position;
    }

    @Override
    public Quaterniond getOrientation() {
        return noOrientation;
    }

    @Override
    public Vector3d findFurthestPoint(Vector3d direction) {
        return direction.add(this.getPosition());
    }
}
