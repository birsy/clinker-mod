package birsy.clinker.core.util.collision.colliders;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public class MeshCollider implements Collider {
    Vector3d[] verticies;
    Vector3d position;
    Quaterniond orientation;
    private boolean cachedRotatedVerticies = false;
    Vector3d[] rotatedVerticies;

    public MeshCollider(Vector3d position, Quaterniond orientation, Vector3d... verticies) {
        this.verticies = verticies;
        this.position = position;
        this.orientation = orientation;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    @Override
    public Vector3d getPosition() {
        return position;
    }

    public void setOrientation(Quaterniond orientation) {
        if (!this.orientation.equals(orientation, 0.0001)) {
            this.cachedRotatedVerticies = false;
            this.orientation = orientation;
        }
    }

    @Override
    public Quaterniond getOrientation() {
        return orientation;
    }

    public Vector3d[] getRotatedVerticies() {
        if (cachedRotatedVerticies && this.rotatedVerticies != null) return this.rotatedVerticies;
        rotatedVerticies = new Vector3d[verticies.length];
        Quaterniond orientation = this.getOrientation();
        for (int i = 0; i < this.verticies.length; i++) {
            rotatedVerticies[i] = orientation.transform(this.verticies[i], new Vector3d());
        }
        this.cachedRotatedVerticies = true;
        return rotatedVerticies;
    }

    @Override
    public Vector3d findFurthestPoint(Vector3d direction) {
        Vector3d maxPoint = new Vector3d();
        double maxDistance = -Float.MAX_VALUE;

        for (Vector3d vertex : this.getRotatedVerticies()) {
            double distance = vertex.dot(direction);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxPoint.set(vertex);
            }
        }

        return maxPoint.add(this.getPosition());
    }
}
