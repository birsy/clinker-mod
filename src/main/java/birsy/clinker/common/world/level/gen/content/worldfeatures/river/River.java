package birsy.clinker.common.world.level.gen.content.worldfeatures.river;

import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class River {
    public final List<Node> nodes;
    public final BoundingBox bvh;
    public final int minY, maxY;

    River(int padding, List<RiverPath.Node> path) {
        int pathSize = path.size();

        float[] directionX = new float[pathSize - 1], directionZ = new float[pathSize - 1];
        for (int i = 0; i < pathSize - 1; i++) {
            RiverPath.Node point = path.get(i), nextPoint = path.get(i + 1);
            float dX = nextPoint.x - point.x, dZ = nextPoint.z - point.z;
            float length = (float) Mth.length(dX, dZ);
            if (length < 0.0001f) { directionX[i] = 1; directionZ[i] = 0; continue; } // zero length fallback...
            directionX[i] = dX / length; directionZ[i] = dZ / length;
        }

        this.nodes = new ArrayList<>(pathSize);
        float totalDistanceAlongRiver = 0;
        float ceilingHeight = path.getFirst().y;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < pathSize; i++) {
            RiverPath.Node point = path.get(i);
            boolean first = i == 0, last = i == pathSize - 1;
            RiverPath.Node prevPoint = first ? point : path.get(i - 1);

            float segmentDistance = (float) Mth.length(point.x - prevPoint.x, point.z - prevPoint.z);
            ceilingHeight = Mth.approach(ceilingHeight, point.y, segmentDistance / 2.0F);
            totalDistanceAlongRiver += segmentDistance;

            float dirX = last ? directionX[pathSize - 2] : directionX[i],
                  dirZ = last ? directionZ[pathSize - 2] : directionZ[i];
            float miterX, miterZ;
            if (first) {
                miterX = directionX[0];
                miterZ = directionZ[0];
            } else if (last) {
                miterX = directionX[pathSize - 2];
                miterZ = directionZ[pathSize - 2];
            } else {
                miterX = directionX[i - 1] + directionX[i];
                miterZ = directionZ[i - 1] + directionZ[i];
                float length = (float) Mth.length(miterX, miterZ);
                // 180 degree turn
                if (length < 0.000001F) {
                    miterX = dirX;
                    miterZ = dirZ;
                } else {
                    miterX /= length;
                    miterZ /= length;
                }
            }

            minY = Math.min(minY, (int) Math.floor(point.y - point.depth));
            maxY = Math.max(maxY, (int) Math.ceil(point.y + point.radius));
            nodes.add(new Node(
                    point.x, point.y, point.z,
                    ceilingHeight, point.radius, point.depth,
                    totalDistanceAlongRiver,
                    miterX, miterZ, dirX, dirZ
            ));
        }
        this.minY = minY;
        this.maxY = maxY;
        this.bvh = BoundingBox.generateTree(this, null,0, nodes.size() - 1, padding);
    }

    public boolean within(int x1, int z1, int x2, int z2) {
        return bvh.intersectsRecursive(x1, z1, x2, z2);
    }
    // mutates the sample object to save on garbage collection stuffs
    Sample sample(Sample sample, double x, double z) {
        // seed trueDistance from the previous result so the bvh gets a tight initial bound,
        // then let findClosestSegment beat it if anything is closer
        sample.trueDistance = sample.index >= 0 ? segmentDistanceSq(x, z, sample.index) : Double.MAX_VALUE;
        findClosestSegment(bvh, sample, x, z);
        computeSample(sample, x, z, sample.index);
        return sample;
    }

    // line segment distance i stole
    private double segmentDistanceSq(double x, double z, int index) {
        Node a = nodes.get(index), b = nodes.get(index + 1);
        double bax = b.x() - a.x(), bay = b.z() - a.z();
        double rx = x - a.x(), ry = z - a.z();
        double h = Mth.clamp((rx * bax + ry * bay) / (bax * bax + bay * bay), 0, 1);
        double dx = rx - bax * h, dy = ry - bay * h;
        return dx * dx + dy * dy;
    }

    private void computeSample(Sample sample, double x, double z, int index) {
        Node node = nodes.get(index), nextNode = nodes.get(index + 1);

        double projectedLength = (nextNode.x() - node.x()) * node.miterX() + (nextNode.z() - node.z()) * node.miterZ();
        float alpha;
        if (Math.abs(projectedLength) > 0.0001) {
            alpha = (float) (node.miterDot(x, z) / projectedLength);
        } else {
            // 180 degree fallback!
            double dX = nextNode.x() - node.x(), dZ = nextNode.z() - node.z();
            double segLen = Math.sqrt(dX * dX + dZ * dZ);
            alpha = (float) (segLen < 0.0001 ? 0 : ((x - node.x()) * node.directionX() + (z - node.z()) * node.directionZ()) / segLen);
        }
        alpha = Mth.clamp(alpha, 0.0F, 1.0F);

        sample.index = index;
        sample.trueDistance = Math.sqrt(sample.trueDistance);
        sample.interpolationFactor = alpha;
        sample.miteredDistanceFromRiver = (float) node.perpendicularDistance(x, z);
        sample.distanceAlongRiver = Mth.lerp(alpha, node.distanceAlongRiver(), nextNode.distanceAlongRiver());
        sample.radius = Mth.lerp(alpha, node.radius(), nextNode.radius());
        sample.depth = Mth.lerp(alpha, node.depth(), nextNode.depth());
        sample.riverHeight = nextNode.y();
        sample.ceilingHeight = Mth.lerp(alpha, node.ceilingY(), nextNode.ceilingY());
        float dirX = Mth.lerp(alpha, node.directionX(), nextNode.directionX()), dirZ = Mth.lerp(alpha, node.directionZ(), nextNode.directionZ());
        float length = (float) Mth.length(dirX, dirZ);
        if (length < 0.0001) { length = 1; dirX = 1; dirZ = 0; }
        sample.dirX = dirX / length; sample.dirZ = dirZ / length;
    }

    private void findClosestSegment(BoundingBox box, Sample sample, double x, double z) {
        if (box.distanceToBoxSq(x, z) >= sample.trueDistance) return;
        if (box.isLeaf()) {
            double boxDist = segmentDistanceSq(x, z, box.index);
            if (boxDist < sample.trueDistance) {
                sample.trueDistance = boxDist;
                sample.index = box.index;
            }
            return;
        }
        BoundingBox near = box.child1, far = box.child2;
        if (near.distanceToBoxSq(x, z) > far.distanceToBoxSq(x, z)) {
            BoundingBox tmp = near; near = far; far = tmp;
        }
        findClosestSegment(near, sample, x, z);
        findClosestSegment(far, sample, x, z);
    }
    
    public record Node(int x, int y, int z, double ceilingY, float radius, float depth, float distanceAlongRiver, float miterX, float miterZ, float directionX, float directionZ) {
        double perpendicularDistance(double sX, double sZ) { return (sX - x) * (-directionZ) + (sZ - z) * directionX; }
        double miterDot(double sX, double sZ) { return (sX - x) * miterX + (sZ - z) * miterZ; }
    }
    public static final class BoundingBox {
        @Nullable
        public final BoundingBox parent;
        public BoundingBox child1, child2;
        public final int index;
        public final int x1, z1, x2, z2;

        BoundingBox(@Nullable BoundingBox parent, BoundingBox child1, BoundingBox child2, int index, int x1, int z1, int x2, int z2) {
            this.parent = parent;
            this.child1 = child1; this.child2 = child2;
            this.index = index;
            this.x1 = x1; this.z1 = z1;
            this.x2 = x2; this.z2 = z2;
        }

        static BoundingBox generateTree(River river, @Nullable BoundingBox parent, int startIndex, int endIndex, int padding) {
            int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

            for (int i = startIndex; i <= endIndex; i++) {
                Node node = river.nodes.get(i);
                int radius = (int) Math.ceil(node.radius) + padding;
                minX = Math.min(minX, node.x - radius);
                maxX = Math.max(maxX, node.x + radius);
                minZ = Math.min(minZ, node.z - radius);
                maxZ = Math.max(maxZ, node.z + radius);
            }

            if (endIndex - startIndex == 1) {
                return new BoundingBox(parent, null, null, startIndex, minX, minZ, maxX, maxZ);
            } else {
                int midIndex = (startIndex + endIndex) / 2;
                BoundingBox box = new BoundingBox(parent, null, null, -1, minX, minZ, maxX, maxZ);
                box.child1 = generateTree(river, box, startIndex, midIndex, padding);
                box.child2 = generateTree(river, box, midIndex, endIndex, padding);
                return box;
            }
        }
        public boolean intersects(int x1, int z1, int x2, int z2) {
            return this.x1 < x2 && this.x2 > x1 && this.z1 < z2 && this.z2 > z1;
        }
        public boolean intersectsRecursive(int x1, int z1, int x2, int z2) {
            if (intersects(x1, z1, x2, z2)) {
                if (child1 != null) {
                    return this.child1.intersectsRecursive(x1, z1, x2, z2) || this.child2.intersectsRecursive(x1, z1, x2, z2);
                } else {
                    return true;
                }
            }
            return false;
        }
        public double distanceToBoxSq(double x, double z) {
            double distX = Math.max(0, Math.max(x1 - x, x - x2)), distZ = Math.max(0, Math.max(z1 - z, z - z2));
            return distX * distX + distZ * distZ;
        }
        public boolean isLeaf() {
            return this.index >= 0;
        }
    }
    public static final class Sample {
        public int index = -1;
        public double trueDistance = Double.MAX_VALUE;
        public float interpolationFactor;
        public int riverHeight;
        public double ceilingHeight;
        public float miteredDistanceFromRiver, distanceAlongRiver;
        public float radius, depth;
        public float dirX, dirZ;
    }
}
