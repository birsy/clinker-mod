package birsy.clinker.core.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

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

    public static Vector3f projectPointOntoLine(Vector3f point, Vector3f lineStart, Vector3f lineEnd) {
        // Calculate the direction vector of the line segment
        Vector3f lineDirection = lineEnd.sub(lineStart, new Vector3f());

        // Calculate the vector from the line start to the point
        Vector3f fromLineStartToPoint = point.sub(lineStart, new Vector3f());

        // Calculate the projection of the point onto the line
        float projectionLength = fromLineStartToPoint.dot(lineDirection) / lineDirection.lengthSquared();

        // Create a vector representing the projection point
        Vector3f projection = lineDirection.mul(projectionLength, new Vector3f()).add(lineStart);
        return projection;
    }
}
