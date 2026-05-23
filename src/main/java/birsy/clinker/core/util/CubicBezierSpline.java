package birsy.clinker.core.util;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class CubicBezierSpline {
    final Vector3f p0 = new Vector3f(), p1 = new Vector3f(),  p2 = new Vector3f(), p3 = new Vector3f();

    public CubicBezierSpline(Vector3fc p0, Vector3fc p1, Vector3fc p2, Vector3fc p3) {
        this.p0.set(p0);
        this.p1.set(p1);
        this.p2.set(p2);
        this.p3.set(p3);
    }

    public CubicBezierSpline(float p0X, float p0Y, float p0Z,
                             float p1X, float p1Y, float p1Z,
                             float p2X, float p2Y, float p2Z,
                             float p3X, float p3Y, float p3Z) {
        this.p0.set(p0X, p0Y, p0Z);
        this.p1.set(p1X, p1Y, p1Z);
        this.p2.set(p2X, p2Y, p2Z);
        this.p3.set(p3X, p3Y, p3Z);
    }

    Vector3f at(float t, Vector3f result) {
        float u = 1.0F - t;
        float p0Fac = u * u * u;
        float p1Fac = 3 * u * u * t;
        float p2Fac = 3 * u * t * t;
        float p3Fac = t * t * t;
        return result.set(0)
                .add(p0.x * p0Fac, p0.y * p0Fac, p0.z * p0Fac)
                .add(p1.x * p1Fac, p1.y * p1Fac, p1.z * p1Fac)
                .add(p2.x * p2Fac, p2.y * p2Fac, p2.z * p2Fac)
                .add(p3.x * p3Fac, p3.y * p3Fac, p3.z * p3Fac);
    }

    public Vector3f derivativeAt(float t, Vector3f result) {
        float u = 1f - t;
        float d0 = 3 * u * u;
        float d1 = 6 * u * t;
        float d2 = 3 * t * t;
        return result.set(
                d0 * (p1.x - p0.x) + d1 * (p2.x - p1.x) + d2 * (p3.x - p2.x),
                d0 * (p1.y - p0.y) + d1 * (p2.y - p1.y) + d2 * (p3.y - p2.y),
                d0 * (p1.z - p0.z) + d1 * (p2.z - p1.z) + d2 * (p3.z - p2.z)
        );
    }

    public Vector3f derivative2At(float t, Vector3f result) {
        float u = 1f - t;
        float d0 = 6 * u;
        float d1 = 6 * t;
        return result.set(
                d0 * (p2.x - 2 * p1.x + p0.x) + d1 * (p3.x - 2 * p2.x + p1.x),
                d0 * (p2.y - 2 * p1.y + p0.y) + d1 * (p3.y - 2 * p2.y + p1.y),
                d0 * (p2.z - 2 * p1.z + p0.z) + d1 * (p3.z - 2 * p2.z + p1.z)
        );
    }

    // returns false if undefined!
    public boolean frenet(float t, Vector3f tangent, Vector3f normal, Vector3f binormal) {
        derivativeAt(t, tangent);
        if (tangent.lengthSquared() < 1e-10f) return false;
        tangent.normalize();

        derivative2At(t, normal);
        float dot = normal.dot(tangent);
        normal.set(normal.x - dot * tangent.x, normal.y - dot * tangent.y, normal.z - dot * tangent.z);
        if (normal.lengthSquared() < 1e-10f) return false;
        normal.normalize();

        tangent.cross(normal, binormal);

        return true;
    }

    private static final ThreadLocal<Vector3f> scratch0 = ThreadLocal.withInitial(Vector3f::new),
                                               scratch1 = ThreadLocal.withInitial(Vector3f::new);
    public void forEachEvenlySpaced(float spacing, int lutSamples, BiConsumer<Vector3fc, Float> action) {
        Vector3f s0 = scratch0.get(), s1 = scratch1.get();
        at(0, s0);

        float[] arcLengths = new float[lutSamples + 1];
        for (int i = 1; i <= lutSamples; i++) {
            float t = (float) i / lutSamples;
            at(t, s1);
            arcLengths[i] = arcLengths[i - 1] + s0.distance(s1);
            s0.set(s1);
        }
        float totalLength = arcLengths[lutSamples];

        float targetArc = 0f;
        while (targetArc < totalLength) {
            float t = arcLengthToT(arcLengths, lutSamples, targetArc);
            at(t, s0);
            action.accept(s0, t);
            targetArc += spacing;
        }
    }

    private float arcLengthToT(float[] arcLengths, int lutSamples, float arcLength) {
        if (arcLength <= 0f) return 0f;
        if (arcLength >= arcLengths[lutSamples]) return 1f;

        int index = Arrays.binarySearch(arcLengths, arcLength);
        if (index >= 0) return (float) index / lutSamples;

        int aboveIndex = -index - 1;
        int belowIndex = aboveIndex - 1;

        // lerp within the segment
        float segStart = arcLengths[belowIndex];
        float segEnd = arcLengths[aboveIndex];
        float segFrac = (arcLength - segStart) / (segEnd - segStart);
        return (belowIndex + segFrac) / lutSamples;
    }
}
