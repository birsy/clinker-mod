package birsy.clinker.core.util;

import com.mojang.math.Vector3f;
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
}
