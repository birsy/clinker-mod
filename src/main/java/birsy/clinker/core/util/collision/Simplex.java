package birsy.clinker.core.util.collision;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class Simplex {
    Vector3d[] points = new Vector3d[4];
    int dimensions;

    public Simplex() {
        this.dimensions = 0;
    }

    public void addPoint(Vector3d point) {
        this.points = new Vector3d[]{point, points[0], points[1], points[2]};
        this.dimensions = Math.min(this.dimensions + 1, 4);
    }

    public Vector3d getPoint(int index) {
        return points[index];
    }

    public Simplex set(Vector3d[] points) {
        this.points = points;
        return this;
    }

    public int dimensions() {
        return dimensions;
    }
}
