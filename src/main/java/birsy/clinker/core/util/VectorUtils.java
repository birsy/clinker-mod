package birsy.clinker.core.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class VectorUtils {
    public static Vec3 toMoj(Vector3d vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static Vec3 slerp(Vec3 start, Vec3 end, float percent) {
        double dot = start.dot(end);
        double theta = Math.acos(dot) * percent;
        Vec3 relativeVec = end.subtract(start.scale(dot));
        relativeVec = relativeVec.normalize();
        return start.scale(Math.cos(theta)).add(relativeVec.scale(Math.sin(theta)));
    }

    public static Vector3f slerp(Vector3fc start, Vector3fc end, float percent, Vector3f result) {
        float dot = Mth.clamp(start.dot(end), -0.999F, 0.999F), theta = (float) Math.acos(dot) * percent;

        float rX = end.x() - (start.x() * dot),
              rY = end.y() - (start.y() * dot),
              rZ = end.z() - (start.z() * dot);
        float rL = Mth.sqrt(rX * rX + rY * rY + rZ * rZ);
        rX /= rL; rY /= rL; rZ /= rL;

        float sinTheta = Mth.sin(theta), cosTheta = Mth.cos(theta);
        return result.set(start.x() * cosTheta + rX * sinTheta,
                          start.y() * cosTheta + rY * sinTheta,
                          start.z() * cosTheta + rZ * sinTheta);
    }

    public static Vector3f projectPointOntoLine(Vector3f point, Vector3f lineStart, Vector3f lineEnd) {
        Vector3f lineDirection = lineEnd.sub(lineStart, new Vector3f());
        Vector3f fromLineStartToPoint = point.sub(lineStart, new Vector3f());
        float projectionLength = fromLineStartToPoint.dot(lineDirection) / lineDirection.lengthSquared();
        Vector3f projection = lineDirection.mul(projectionLength, new Vector3f()).add(lineStart);
        return projection;
    }

    public static Vector3f approachSphere(Vector3f start, Vector3f end, float angleStep, Vector3f dest) {
        float dot = start.dot(end);
        dot = Mth.clamp(dot, -1.0f, 1.0f);
        float totalAngle = (float) Math.acos(dot);
        float actualStepAngle = Math.min(angleStep, totalAngle);

        if (totalAngle < Mth.EPSILON) {
            return dest.set(end);
        } else if (totalAngle > Mth.PI - Mth.EPSILON) {
            // just choose some axis
            Vector3f axis = Math.abs(start.x) < 0.9f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
            axis.cross(start).normalize();
            return dest.set(start).rotateAxis(actualStepAngle, axis.x, axis.y, axis.z);
        }

        float sinTotal = Mth.sin(totalAngle);
        float w1 = Mth.sin(totalAngle - actualStepAngle) / sinTotal;
        float w2 = Mth.sin(actualStepAngle) / sinTotal;

        return dest.set(start).mul(w1).add(end.x * w2, end.y * w2, end.z * w2);
    }

    public static Vector3f approachEuler(Vector3f start, Vector3f end, float angleStepDegrees, float lengthStep, Vector3f dest) {
        float startLength = start.length(), endLen = end.length();
        float length = Mth.approach(startLength, endLen, lengthStep);

        if (endLen < Mth.EPSILON) return dest.set(start).normalize(length);
        if (startLength < Mth.EPSILON) return dest.set(end).normalize(length);

        float startYaw = (float) Math.atan2(start.z(), start.x());
        float yaw = startYaw;
        if (Mth.lengthSquared(start.x(), start.z()) > Mth.EPSILON * startLength * startLength &&
            Mth.lengthSquared(end.x(), end.z()) > Mth.EPSILON * endLen * endLen) {
            float endYaw = (float) Math.atan2(end.z(), end.x());
            yaw = Mth.approachDegrees(startYaw * Mth.RAD_TO_DEG, endYaw * Mth.RAD_TO_DEG, angleStepDegrees) * Mth.DEG_TO_RAD;
        }

        float startPitch = (float) Math.acos(Mth.clamp(start.y() / startLength, -1f, 1f));
        float endPitch = (float) Math.acos(Mth.clamp(end.y() / endLen, -1f, 1f));
        float pitch = Mth.approachDegrees(startPitch * Mth.RAD_TO_DEG, endPitch * Mth.RAD_TO_DEG, angleStepDegrees) * Mth.DEG_TO_RAD;

        float xzScale = Mth.sin(pitch);
        return dest.set(Mth.cos(yaw) * xzScale * length, Mth.cos(pitch) * length, Mth.sin(yaw) * xzScale * length);
    }
    public static Vector3f approachEuler(Vector3f start, Vector3f end, float angleStepDegrees, Vector3f dest) {
        return approachEuler(start, end, angleStepDegrees, Float.MAX_VALUE, dest);
    }

    public static Vec3 reflect(Vec3 normal, Vec3 vec) {
        double x = normal.x();
        double y = normal.y();
        double z = normal.z();
        double dot = Math.fma(vec.x, x, Math.fma(vec.y, y, vec.z * z));
        return new Vec3(vec.x - (dot + dot) * x, vec.y - (dot + dot) * y, vec.z - (dot + dot) * z);
    }
}
