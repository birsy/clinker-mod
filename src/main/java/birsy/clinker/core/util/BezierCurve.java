package birsy.clinker.core.util;

import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

/**
 * shameless stolen from stackoverflow
 * https://stackoverflow.com/questions/2742610/closest-point-on-a-cubic-bezier-curve
 * <3
 */
public class BezierCurve {
    public final Vec3[] controlPoints;
    public BezierCurve(Vec3... controlPoints) {
        this.controlPoints = controlPoints;
    }
    /** Calculate a point along a Bézier segment for a given parameter.
     * t      : Parameter [0,1] for how far along the curve the point should be
     * returns: A point on the curve given parameter t
     */
    public Vec3 bezierPoint(double t) {
        Vec3[] tmps = this.controlPoints;

        if (this.controlPoints.length < 2) { throw new ArithmeticException("At least 2 control points are required"); }
        for (int degree = this.controlPoints.length - 2; degree > 0; degree--) {
            for (var i = 0; i <= degree; i++) {
                tmps[i] = tmps[i].lerp(tmps[i+1], t);
            }
        }

        return tmps[0];
    }

    /** Brute-force method for finding the ~closest point on a Bézier curve to a point you supply.
     * curve  : Array of vectors representing control points for a Bézier curve
     * pt     : The point (vector) you want to find out to be near
     * returns: The parameter t representing the location of `out`
     */
    public Vec3 closestPoint(Vec3 pt) {
        int mIndex = 0, scans = 25; // More scans -> better chance of being correct
        double min = Double.POSITIVE_INFINITY;

        for (int i = scans + 1; i > 0; i--) {
            double d2 = pt.distanceToSqr(bezierPoint(i / scans));
            if (d2 < min) {
                min = d2;
                mIndex = i;
            }
        }

        double t0 = Math.max((float)(mIndex - 1) / (float)scans, 0);
        double t1 = Math.min((float)(mIndex + 1) / (float)scans, 1);

        Function<Double, Double> d2ForT = (n) -> pt.distanceToSqr(bezierPoint(n));
        return bezierPoint(MathUtils.localMinimum(t0, t1, d2ForT, 1e-4));
    }

    //STATIC FORMS OF THE FUNCTIONS
    /** Calculate a point along a Bézier segment for a given parameter.
     * curve  : Array of vectors representing control points for a Bézier curve
     * t      : Parameter [0,1] for how far along the curve the point should be
     * returns: A point on the curve given parameter t
     */
    public static Vec3 bezierPoint(BezierCurve curve, double t) {
        Vec3[] tmps = curve.controlPoints;

        if (curve.controlPoints.length < 2) { throw new ArithmeticException("At least 2 control points are required"); }
        for (int degree = curve.controlPoints.length - 1; degree > 0; degree--) {
            for (var i = 0; i <= degree; i++) {
                tmps[i] = tmps[i].lerp(tmps[i+1], t);
            }
        }

        return tmps[0];
    }

    /** Brute-force method for finding the ~closest point on a Bézier curve to a point you supply.
     * curve  : Array of vectors representing control points for a Bézier curve
     * pt     : The point (vector) you want to find out to be near
     * returns: The parameter t representing the location of `out`
     */
    public static Vec3 closestPoint(BezierCurve curve, Vec3 pt) {
        int mIndex = 0, scans = 25; // More scans -> better chance of being correct
        double min = Double.POSITIVE_INFINITY;

        for (int i = scans + 1; i > 0; i--) {
            double d2 = pt.distanceToSqr(bezierPoint(curve, i / scans));
            if (d2 < min) {
                min = d2;
                mIndex = i;
            }
        }

        double t0 = Math.max((float)(mIndex - 1) / (float)scans, 0);
        double t1 = Math.min((float)(mIndex + 1) / (float)scans, 1);

        Function<Double, Double> d2ForT = (n) -> pt.distanceToSqr(bezierPoint(curve, n));
        return bezierPoint(curve, MathUtils.localMinimum(t0, t1, d2ForT, 1e-4));
    }
}
