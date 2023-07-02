package birsy.clinker.client.animation;

import birsy.clinker.core.util.Quaternionf;
import birsy.clinker.core.util.VectorUtils;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

public class OrientationSolver {
    // Given a set of points n long, and a set of transformed points n long find the rotation that,
    // when applied to the original points, most closely aligns the original points to the transformed
    // points.
    // This should only be used when n > 2, as there's a much more efficient solution for two points.
    //
    // Given:                                       Returns:
    // initialPoints[]  &  transformedPoints[]
    //
    //    0 ----- 1                0                a quaternion representing a rotation of 45 degrees
    //     \     /                /  ⟍              on the Z - axis.
    //      \   /                /     ⟍
    //        2                 2 ------ 1
    //
    public static Quaternionf solveOrientation(Vector3f[] initialPoints, Vector3f[] targetPoints, int maxIterations, float threshold) {
        // copy and normalize arrays
        Vector3f[] points = initialPoints.clone();
        Vector3f[] targets = targetPoints.clone();
        for (int i = 0; i < initialPoints.length; i++) {
            points[i] = points[i].copy();
            points[i].normalize();
            targets[i] = targets[i].copy();
            targets[i].normalize();
        }

        // iterate through every pair of points, finding the rotation that will match the point to its target and applying it
        // if i do this enough on every pair of points i could settle on a solution?
        Quaternionf quaternion = new Quaternionf();
        int solvedPoints = 0;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            for (int i = 0; i < initialPoints.length; i++) {
                Vector3f point = quaternion.transform(points[i].copy());
                point.normalize();
                Vector3f target = targets[i];
                target.normalize();

                // work out the axis of rotation
                Vector3f axis = target.copy();
                axis.sub(point);

                // if every point is within the threshold, the system is solved, and we can return the rotation.
                if (axis.x() * axis.x() + axis.y() * axis.y() + axis.z() * axis.z() < threshold * threshold) {
                    solvedPoints++;
                    if (solvedPoints == initialPoints.length) {
                        return quaternion;
                    }
                } else {
                    solvedPoints = 0;
                }

                // wish i could include mspaint diagrams in comments but im sure this works
                axis.cross(point);
                axis.normalize();


            }
        }

        return quaternion;
    }
}
