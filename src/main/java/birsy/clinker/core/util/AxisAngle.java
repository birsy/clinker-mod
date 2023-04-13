package birsy.clinker.core.util;

import net.minecraft.world.phys.Vec3;

public class AxisAngle {
    Vec3 axis;
    double angle;
    public AxisAngle(Vec3 axis, double angle) {
        this.axis = axis;
        this.angle = angle;
    }

    public AxisAngle(double x, double y, double z, double angle) {
        this.axis = new Vec3(x, y, z);
        this.angle = angle;
    }

    public double x() {
        return axis.x();
    }
    public double y() {
        return axis.y();
    }
    public double z() {
        return axis.z();
    }
}
