package birsy.clinker.common.world.level.gen.content.worldfeatures.river;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class RiverPath {
    public static final class Node {
        public int x, y, z;
        public float radius, depth;
        public Node(int x, int y, int z, float radius, float depth) {
            this.x = x; this.y = y; this.z = z;
            this.radius = radius; this.depth = depth;
        }
    }

    public static List<RiverPath.Node> resamplePath(List<RiverPath.Node> points, int distance) {
        List<RiverPath.Node> result = new ArrayList<>();
        result.add(points.getFirst());

        // how far along the current segment until the next sample is due
        float distanceUntilNext = distance;

        for (int i = 0; i < points.size() - 1; i++) {
            RiverPath.Node point = points.get(i), nextPoint = points.get(i + 1);
            float segmentLength = (float) Mth.length(nextPoint.x - point.x, nextPoint.z - point.z);

            // too short, skip!
            if (segmentLength < 0.001f) {
                distanceUntilNext -= segmentLength;
                continue;
            }
            while (distanceUntilNext < segmentLength) {
                float t = distanceUntilNext / segmentLength;
                result.add(new RiverPath.Node(
                        Math.round(Mth.lerp(t, point.x, nextPoint.x)),
                        t <= 0.5 ? point.y : nextPoint.y,
                        Math.round(Mth.lerp(t, point.z, nextPoint.z)),
                        Mth.lerp(t, point.radius, nextPoint.radius),
                        Mth.lerp(t, point.depth,  nextPoint.depth)
                ));
                distanceUntilNext += distance;
            }

            distanceUntilNext -= segmentLength;
        }

        result.add(points.getLast());
        return result;
    }
}
