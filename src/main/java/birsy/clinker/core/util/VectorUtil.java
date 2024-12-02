package birsy.clinker.core.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class VectorUtil {
    public static Vec3 slerp(Vec3 start, Vec3 end, float percent) {
        double dot = start.dot(end);
        double theta = Math.acos(dot) * percent;
        Vec3 relativeVec = end.subtract(start.scale(dot));
        relativeVec = relativeVec.normalize();
        return start.scale(Math.cos(theta)).add(relativeVec.scale(Math.sin(theta)));
    }

    public static Vector3f projectPointOntoLine(Vector3f point, Vector3f lineStart, Vector3f lineEnd) {
        Vector3f lineDirection = lineEnd.sub(lineStart, new Vector3f());
        Vector3f fromLineStartToPoint = point.sub(lineStart, new Vector3f());
        float projectionLength = fromLineStartToPoint.dot(lineDirection) / lineDirection.lengthSquared();
        Vector3f projection = lineDirection.mul(projectionLength, new Vector3f()).add(lineStart);
        return projection;
    }

    public static Vec3 reflect(Vec3 normal, Vec3 vec) {
        double x = normal.x();
        double y = normal.y();
        double z = normal.z();
        double dot = Math.fma(vec.x, x, Math.fma(vec.y, y, vec.z * z));
        return new Vec3(vec.x - (dot + dot) * x, vec.y - (dot + dot) * y, vec.z - (dot + dot) * z);
    }
}
