package birsy.clinker.core.util;

import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VectorUtils {
    public static float[] toArray(Vector3f vec) {
        return new float[]{vec.x(), vec.y(), vec.z()};
    }
    public static double[] toArrayd(Vector3f vec) {
        return new double[]{vec.x(), vec.y(), vec.z()};
    }
    public static double[] toArray(Vec3 vec) {
        return new double[]{vec.x(), vec.y(), vec.z()};
    }

    public static Vec3 slerp(Vec3 start, Vec3 end, float percent) {
        double dot = start.dot(end);
        double theta = Math.acos(dot) * percent;
        Vec3 RelativeVec = end.subtract(start.scale(dot));
        RelativeVec = RelativeVec.normalize();

        return start.scale(Math.cos(theta)).add(RelativeVec.scale(Math.sin(theta)));
    }
}
