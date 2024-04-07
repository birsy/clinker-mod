package birsy.clinker.client;

import org.joml.Vector3d;

public class BezierCurve {
    final Vector3d[] points = new Vector3d[4];

    public BezierCurve(Vector3d... points) {
        for (int i = 0; i < this.points.length; i++) {
            this.points[i] = points[i];
        }
    }

    public Vector3d evaluate(double t) {
        Vector3d lerp01 = points[0].lerp(points[1], t, new Vector3d());
        Vector3d lerp12 = points[1].lerp(points[2], t, new Vector3d());
        Vector3d lerp23 = points[2].lerp(points[3], t, new Vector3d());

        Vector3d lerp0112 = lerp01.lerp(lerp12, t);
        Vector3d lerp1223 = lerp23.lerp(lerp12, 1 - t);

        return  lerp0112.lerp(lerp1223, t);
    }
}
