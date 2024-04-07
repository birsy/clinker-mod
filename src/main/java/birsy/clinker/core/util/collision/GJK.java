package birsy.clinker.core.util.collision;

import birsy.clinker.core.util.collision.colliders.Collider;
import org.joml.Vector3d;

/**
 * adapted from https://winter.dev/articles/gjk-algorithm
 * thanks :3
 */
public class GJK {
    public static boolean checkCollision(Collider colliderA, Collider colliderB) {
        // get initial support point in any direction.
        // entirely arbitrary. can probably be smarter about this.
        // todo: try to be smarter about this!
        Vector3d support = GJK.support(colliderA, colliderB, new Vector3d(1, 0, 0));

        // simplex is an array of points, max count is 4
        Simplex points = new Simplex();
        points.addPoint(support);

        // new direction is towards the origin
        Vector3d direction = support.negate(new Vector3d());

        while (true) {
            support = GJK.support(colliderA, colliderB, direction);

            if (support.dot(direction) <= 0) return false; // no collision
            points.addPoint(support);

            if (nextSimplex(points, direction)) return true;
        }
    }

    private static boolean nextSimplex(Simplex points, Vector3d direction) {
        switch (points.dimensions()) {
            case 2: return line       (points, direction);
            case 3: return triangle   (points, direction);
            case 4: return tetrahedron(points, direction);
        }

        throw new IllegalStateException("Simplex has less than two points! This should never occur.");
    }

    private static boolean line(Simplex points, Vector3d direction) {
        Vector3d a = points.getPoint(0);
        Vector3d b = points.getPoint(1);

        Vector3d ab = b.sub(a, new Vector3d());
        Vector3d ao = a.negate(new Vector3d());

        if (sameDirection(ab, ao)) {
            ab.cross(ao, new Vector3d()).cross(ab, direction);
        } else {
            points.set(new Vector3d[]{a});
            direction.set(ao);
        }

        return false;
    }

    private static boolean triangle(Simplex points, Vector3d direction) {
        Vector3d a = points.getPoint(0);
        Vector3d b = points.getPoint(1);
        Vector3d c = points.getPoint(2);

        Vector3d ab = b.sub(a, new Vector3d());
        Vector3d ac = c.sub(a, new Vector3d());
        Vector3d ao = a.negate(new Vector3d());

        Vector3d abc = ab.cross(ac, new Vector3d());

        if (sameDirection(abc.cross(ac, new Vector3d()), ao)) {
            if (sameDirection(ac, ao)) {
                points.set(new Vector3d[]{a, c});
                ac.cross(ao, new Vector3d()).cross(ac, direction);
            } else {
                return line(points.set(new Vector3d[]{a, b}), direction);
            }
        } else {
            if (sameDirection(abc.cross(ab, new Vector3d()), ao)) {
                return line(points.set(new Vector3d[]{a, b}), direction);
            } else {
                if (sameDirection(abc, ao)) {
                    direction.set(abc);
                } else {
                    points.set(new Vector3d[]{a, c, b});
                    abc.negate(direction);
                }
            }
        }

        return false;
    }

    private static boolean tetrahedron(Simplex points, Vector3d direction) {
        Vector3d a = points.getPoint(0);
        Vector3d b = points.getPoint(1);
        Vector3d c = points.getPoint(2);
        Vector3d d = points.getPoint(3);

        Vector3d ab = b.sub(a, new Vector3d());
        Vector3d ac = c.sub(a, new Vector3d());
        Vector3d ad = d.sub(a, new Vector3d());
        Vector3d ao = a.negate(new Vector3d());

        Vector3d abc = ab.cross(ac, new Vector3d());
        Vector3d acd = ac.cross(ad, new Vector3d());
        Vector3d adb = ad.cross(ab, new Vector3d());

        if (sameDirection(abc, ao)) return triangle(points.set(new Vector3d[]{a, b, c}), direction);
        if (sameDirection(acd, ao)) return triangle(points.set(new Vector3d[]{a, c, d}), direction);
        if (sameDirection(adb, ao)) return triangle(points.set(new Vector3d[]{a, d, b}), direction);

        return true;
    }

    private static boolean sameDirection(Vector3d direction, Vector3d ao) {
        return direction.dot(ao) > 0;
    }

    private static Vector3d support(Collider colliderA, Collider colliderB, Vector3d direction) {
        return colliderA.findFurthestPoint(direction).sub(colliderB.findFurthestPoint(direction.mul(-1)));
    }
}
