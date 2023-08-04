package birsy.clinker.core.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SDFs {
    public static double capsule(Vec3 p, Vec3 a, Vec3 b, float r ) {
        Vec3 pa = p.subtract(a), ba = b.subtract(a);
        double h = clamp( dot(pa,ba) / dot(ba,ba), 0.0, 1.0 );
        return length(pa.subtract(ba.scale(h))) - r;
    }

    public static double line(Vec3 p, Vec3 a, Vec3 b) {
        return capsule(p, a, b, 0);
    }

    private static double clamp(double n, double min, double max) {
        return Mth.clamp(n, min, max);
    }

    private static double dot(Vec3 a, Vec3 b) {
        return a.dot(b);
    }

    private static double length(Vec3 a) {
        return a.length();
    }
}
