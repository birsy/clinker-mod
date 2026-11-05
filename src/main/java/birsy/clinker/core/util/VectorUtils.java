package birsy.clinker.core.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.joml.Math;

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
        float dot = Mth.clamp(start.dot(end), -0.999F, 0.999F), theta = Math.acos(dot) * percent;

        float rX = end.x() - (start.x() * dot),
              rY = end.y() - (start.y() * dot),
              rZ = end.z() - (start.z() * dot);
        float rL = Math.sqrt(rX * rX + rY * rY + rZ * rZ);
        rX /= rL; rY /= rL; rZ /= rL;

        float sinTheta = Math.sin(theta), cosTheta = Math.cos(theta);
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

    public static Vector3f approachSphere(Vector3f start, Vector3f end, float angleAmount, Vector3f dest) {
        float dot = start.dot(end);
        dot = Mth.clamp(dot, -1.0f, 1.0f);
        float totalAngle = Math.acos(dot);
        float actualStepAngle = Math.min(angleAmount, totalAngle);

        if (totalAngle < 1e-6f) {
            return dest.set(end);
        } else if (totalAngle > Mth.PI - 1e-3f) {
            // pick a perpendicular axis
            Vector3f axis = Math.abs(start.x) < 0.9f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
            axis.cross(start).normalize();
            return dest.set(start).rotateAxis(actualStepAngle, axis.x, axis.y, axis.z);
        }

        float sinTotal = Math.sin(totalAngle);
        float w1 = Math.sin(totalAngle - actualStepAngle) / sinTotal;
        float w2 = Math.sin(actualStepAngle) / sinTotal;

        return dest.set(start).mul(w1).add(end.x * w2, end.y * w2, end.z * w2);
    }

    public static Vec3 reflect(Vec3 normal, Vec3 vec) {
        double x = normal.x();
        double y = normal.y();
        double z = normal.z();
        double dot = Math.fma(vec.x, x, Math.fma(vec.y, y, vec.z * z));
        return new Vec3(vec.x - (dot + dot) * x, vec.y - (dot + dot) * y, vec.z - (dot + dot) * z);
    }
}
