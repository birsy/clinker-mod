package birsy.clinker.core.util;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

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

    public static Vector3d toJOML(Vec3 vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
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

    public static Vec3 transform(Vec3 vec, Matrix3f pMatrix) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;
        return new Vec3(pMatrix.m00 * x + pMatrix.m01 * y + pMatrix.m02 * z,
                        pMatrix.m10 * x + pMatrix.m11 * y + pMatrix.m12 * z,
                        pMatrix.m20 * x + pMatrix.m21 * y + pMatrix.m22 * z);
    }

    public static float distance(Vector3f a, Vector3f b) {
        float d0 = a.x() - b.x();
        float d1 = a.y() - b.y();
        float d2 = a.z() - b.z();
        return Mth.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static float lengthSquared(Vector3f vector3f) {
        return vector3f.x() * vector3f.x() + vector3f.y() * vector3f.y() + vector3f.z() * vector3f.z();
    }

    public static Vector3f projectOntoPlane(Vector3f point, Vector3f pNormal, Vector3f pPos) {
        point.sub(pPos);
        float dist = point.dot(pNormal);
        point.set(point.x() - (dist * pNormal.x()), point.y() - (dist * pNormal.y()), point.z() - (dist * pNormal.z()));
        return point;
    }

    public static Vector3f projectPointOntoLine(Vector3f point, Vector3f lineStart, Vector3f lineEnd) {
        // Calculate the direction vector of the line segment
        Vector3f lineDirection = lineEnd.copy();
        lineDirection.sub(lineStart);

        // Calculate the vector from the line start to the point
        Vector3f fromLineStartToPoint = point.copy();
        fromLineStartToPoint.sub(lineStart);

        // Calculate the projection of the point onto the line
        float projectionLength = fromLineStartToPoint.dot(lineDirection) / lengthSquared(lineDirection);

        // Create a vector representing the projection point
        Vector3f projection = lineDirection.copy();
        projection.mul(projectionLength);
        projection.add(lineStart);

        return projection;
    }
}
