package birsy.clinker.core.util;

import com.mojang.math.Vector3f;

public class AxisAnglef {
    Vector3f axis;
    float angle;
    public AxisAnglef(Vector3f axis, float angle) {
        this.axis = axis;
        this.angle = angle;
    }

    public AxisAnglef(float x, float y, float z, float angle) {
        this.axis = new Vector3f(x, y, z);
        this.angle = angle;
    }

    public float x() {
        return axis.x();
    }
    public float y() {
        return axis.y();
    }
    public float z() {
        return axis.z();
    }
}
