package birsy.clinker.client;

import net.minecraft.util.Mth;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class CatenaryArc {
    private Vector3dc p1, p2;
    private double length;
    private boolean solved;
    private double a, p, q;

    public CatenaryArc(Vector3dc p1, Vector3dc p2, double length) {
        this.p1 = p1;
        this.p2 = p2;
        this.length = length;
        this.solved = false;
    }

    public double evaluateHeight(double percentage) {
        if (this.p1.distance(p2) > this.length) return Mth.lerp(percentage, p1.y(), p2.y());
        if (!solved) {
            boolean wasSuccessfulSolve = this.solve();
            if (!wasSuccessfulSolve) return Mth.lerp(percentage, p1.y(), p2.y());
        }
        double x = percentage * horizontalDistance();
        double y = this.a * Math.cosh((x - p) / (a)) + q;
        return y + this.p1.y();
    }

    public double evaluateHeightOffset(double percentage) {
        double yLinear = Mth.lerp(percentage, p1.y(), p2.y());
        if (this.p1.distance(p2) > this.length) return yLinear;
        double y = evaluateHeight(percentage);
        return y - yLinear;
    }

    public boolean solve() {
        //transforms to 2d space, with p1 at 0, 0
        double x1 = 0, y1 = 0;
        double x2 = horizontalDistance(), y2 = this.p2.y() - this.p1.y();
        double l = this.length;

        double h = x2 - x1;
        double v = y2 - y1;

        boolean wasSuccessfulSolve = this.solveForA(l, h, v, 32, 32);
        if (!wasSuccessfulSolve) return false;
        //Clinker.LOGGER.info(a);
        this.p = (x1 + x2 - this.a * Math.log((l + v) / (l - v))) / 2.0;
        this.q = (y1 + y2 - l * coth((h) / (2 * this.a))) / 2;

        this.solved = true;
        return true;
    }

    // a is transcendental, meaning you can't just extract it from either side of the equation and easily solve
    // an iterative approach must be taken instead.
    private boolean solveForA(double l, double h, double v, int maxCoarseIterations, int maxFineIterations) {
        // coarse search to find interval of a
        double IntervalSearchStep = 1;
        double v1 = Math.pow(l, 2) - Math.pow(v, 2);
        for (int i = 0; i < maxCoarseIterations + 1; i++) {
            this.a += IntervalSearchStep;
            boolean passedInterval = Math.sqrt(v1) >= 2 * this.a * Math.sinh(h / (2 * this.a));
            if (passedInterval) break;
            if (i >= maxCoarseIterations) return false;
        }

        // basic binary search
        double precision = 0.0001;
        double intervalMin = this.a - IntervalSearchStep;
        double intervalMax = this.a;
        for (int i = 0; i < maxFineIterations + 1; i++) {
            this.a = (intervalMin + intervalMax) / 2f;
            if (Math.sqrt(v1) < 2 * this.a * Math.sinh(h / (2 * this.a))) {
                intervalMin = this.a;
            } else {
                intervalMax = this.a;
            }

            if (intervalMax - intervalMin <= precision) break;
            if (i >= maxFineIterations) return false;
        }

        return true;
    }

    private double horizontalDistance() {
        return this.p1.mul(1, 0, 1, new Vector3d()).distance(this.p2);
    }

    public void setPointA(Vector3d p1) {
        this.p1 = p1;
        this.solved = false;
    }

    public void setPointB(Vector3d p2) {
        this.p2 = p2;
        this.solved = false;
    }

    public void setLength(double length) {
        this.length = length;
        this.solved = false;
    }

    private static double coth(double theta) {
        return Math.cosh(theta) / Math.sinh(theta);
    }
}
