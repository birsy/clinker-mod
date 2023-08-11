package birsy.clinker.core.util;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;

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

    public static Vector3f mul(Matrix4f mat, Vector3f vec) {
        float x = vec.x(), y = vec.y(), z = vec.z();
        float invW = 1.0f / Math.fma(mat.m03, x, Math.fma(mat.m13, y, Math.fma(mat.m23, z, mat.m33)));
        vec.set(Math.fma(mat.m00, x, Math.fma(mat.m10, y, Math.fma(mat.m20, z, mat.m30))) * invW,
                Math.fma(mat.m01, x, Math.fma(mat.m11, y, Math.fma(mat.m21, z, mat.m31))) * invW,
                Math.fma(mat.m02, x, Math.fma(mat.m12, y, Math.fma(mat.m22, z, mat.m32))) * invW);
        return vec;
    }

    public static float distance(Vector3f a, Vector3f b) {
        float d0 = a.x() - b.x();
        float d1 = a.y() - b.y();
        float d2 = a.z() - b.z();
        return Mth.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static Vector3f projectOntoPlane(Vector3f point, Vector3f pNormal, Vector3f pPos) {
        point.sub(pPos);
        float dist = point.dot(pNormal);
        point.set(point.x() - (dist * pNormal.x()), point.y() - (dist * pNormal.y()), point.z() - (dist * pNormal.z()));
        return point;
    }
}
