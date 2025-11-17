package birsy.clinker.common.world.level.gen.worldfeature.worldfeatures;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunk;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class UndergroundRiverWorldFeature extends WorldFeature {
    private int radius = 10;
    private RiverSegment[] segments;
    private RiverBoundingBox boundingBoxHierarchy;
    private NoiseComputer riverDistanceComputer, riverHeightComputer;

    public UndergroundRiverWorldFeature(int depth, int separationRadius) {
        super(depth, separationRadius);
    }

    @Override
    public int getCenterX() { return (segments[0].startX() + segments[segments.length - 1].endX()) / 2; }
    @Override
    public int getCenterZ() { return (segments[0].startX() + segments[segments.length - 1].endX()) / 2; }
    @Override
    public boolean within(int minX, int minZ, int maxX, int maxZ) {
        return this.boundingBoxHierarchy.intersectsRecursive(minX, Integer.MIN_VALUE, minZ, maxX, Integer.MAX_VALUE, maxZ);
    }

    @Override
    public boolean plan(MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
        RiverNode start = new RiverNode(0.0F,
                          randomSource.nextInt(metaChunk.minX(), metaChunk.maxX()), randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ()), 30, true),
                  end = new RiverNode(0.0F,
                          randomSource.nextInt(metaChunk.minX(), metaChunk.maxX()), randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ()), 30, true);

        ArrayList<RiverNode> nodes = new ArrayList<>();
        Collections.addAll(nodes, start, end);

        // recursive split
        for (int i = 0; i < 16; i++) {
            int index = randomSource.nextInt(0, nodes.size() - 1);
            RiverNode first = nodes.get(index), second = nodes.get(index + 1);
            int xOffset = first.x - second.x, zOffset = first.z - second.z;
            double randomScale = (randomSource.nextDouble() * 2.0 - 1.0) / Mth.sqrt(2);
            RiverNode newNode = new RiverNode(
                    Mth.lerp(0.5, first.progress(), second.progress),
                    (int) ((first.x + second.x) / 2 + zOffset * randomScale),
                    (int) ((first.z + second.z) / 2 - xOffset * randomScale),
                    (int) Mth.lerp(0.5, first.height(), second.height),
                    randomSource.nextBoolean() ? first.underground() : second.underground
            );
            nodes.add(index + 1, newNode);
        }

        this.segments = new RiverSegment[nodes.size() - 1];
        for (int i = 0; i < nodes.size() - 1; i++) {
            this.segments[i] = new RiverSegment(nodes.get(i), nodes.get(i + 1));
        }

        this.boundingBoxHierarchy = RiverBoundingBox.fromSegments(this.segments, 0, this.segments.length);
        this.boundingBoxHierarchy.expand(radius, radius, radius);

        this.riverDistanceComputer = new NoiseComputer("river_distance_" + randomSource.nextInt(), CacheType.INTERPOLATED_2D_COARSE,
                (x, y, z, noiseContext) -> {
                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z);
                    return coordinates.horizontalDistance();
                }
        );
        this.riverHeightComputer = new NoiseComputer("river_height_" + randomSource.nextInt(), CacheType.INTERPOLATED_COARSE,
                (x, y, z, noiseContext) -> {
                    if (y < this.boundingBoxHierarchy.y1 || y > this.boundingBoxHierarchy.y2) return 10000;
                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z);
                    return y - coordinates.riverHeight();
                }
        );

        Clinker.LOGGER.info("River generated starting at at {} {} {}", start.x, start.height, start.z);
        return true;
    }

    @Override
    public double modifyTerrain(int x, int y, int z, double currentNoiseValue, NoiseComputerContext context) {
        double distance = context.noiseComputerExecutor().compute(x, y, z, this.riverDistanceComputer);
        double height = context.noiseComputerExecutor().compute(x, y, z, this.riverHeightComputer);
        if (Math.sqrt(distance * distance + height * height) < radius)
            return 10000;
        return currentNoiseValue;
    }

    public RiverSpaceCoordinates getRiverSpaceCoordinates(int x, int y, int z) {
        MutableRiverSpaceCoordinates best = new MutableRiverSpaceCoordinates(1000, 0);
        sampleDistanceFromRiverRecursive(this.boundingBoxHierarchy, x, y, z, best);
        return best.toImmutable();
    }

    private void sampleDistanceFromRiverRecursive(RiverBoundingBox box, int x, int y, int z, MutableRiverSpaceCoordinates best) {
        if (!box.containsHorizontal(x, z)) return; // don't take y into consideration
        if (box.riverSegmentIndex >= 0) {
            RiverSegment segment = this.segments[box.riverSegmentIndex];

            int horizontalDistance = (int) lineSegmentDistance(x, z, segment.startX(), segment.startZ(), segment.endX(), segment.endZ());

            if (horizontalDistance < best.horizontalDistance) {
                best.horizontalDistance = horizontalDistance;

                double gradient = (x - segment.endX()) * (segment.startX() - segment.endX()) +
                        (z - segment.endZ()) * (segment.startZ() - segment.endZ());
                gradient /= (segment.startX() - segment.endX()) * (segment.startX() - segment.endX()) +
                        (segment.startZ() - segment.endZ()) * (segment.startZ() - segment.endZ());
                double surfaceHeight = Mth.clampedMap(gradient, 0, 1, segment.endHeight(), segment.startHeight());
                best.surfaceHeight = surfaceHeight;//Math.floor(surfaceHeight / 12.0) * 12;
            }
        } else {
            sampleDistanceFromRiverRecursive(box.childA, x, y, z, best);
            sampleDistanceFromRiverRecursive(box.childB, x, y, z, best);
        }
    }

    private static double lineSegmentDistance(double px, double py, double ax, double ay, double bx, double by) {
        double bax = bx - ax, bay = by - ay;
        double h = ((px - ax) * bax + (py - ay) * bay) / (bax * bax + bay * bay);
        if (h < 0) h = 0;
        else if (h > 1) h = 1;
        double dx = (px - ax) - bax * h;
        double dy = (py - ay) - bay * h;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public record RiverSpaceCoordinates(int horizontalDistance, double riverHeight) {}
    protected static class MutableRiverSpaceCoordinates {
        int horizontalDistance;
        double surfaceHeight;
        MutableRiverSpaceCoordinates(int horiz, double surfaceHeight) { this.horizontalDistance = horiz; this.surfaceHeight = surfaceHeight; }
        RiverSpaceCoordinates toImmutable() { return new RiverSpaceCoordinates(horizontalDistance, (int) surfaceHeight); }
    }

    private record RiverNode(double progress, int x, int z, int height, boolean underground) {}
    private record RiverSegment(RiverNode start, RiverNode end) {
        double startProgress() { return start.progress(); }
        double endProgress() { return end.progress(); }
        int startX() { return start.x(); }
        int endX() { return end.x(); }
        int startZ() { return start.z(); }
        int endZ() { return end.z(); }
        int startHeight() { return start.height(); }
        int endHeight() { return end.height(); }
        boolean startUnderground() {return start.underground(); }
        boolean endUnderground() { return end.underground(); }
    }

    private static class RiverBoundingBox {
        int riverSegmentIndex; // -1 for non-leaf nodes
        int x1, y1, z1, x2, y2, z2;
        RiverBoundingBox childA, childB;

        RiverBoundingBox(int riverSegmentIndex, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.riverSegmentIndex = riverSegmentIndex;
            this.x1 = x1; this.y1 = y1; this.z1 = z1;
            this.x2 = x2; this.y2 = y2; this.z2 = z2;
        }

        RiverBoundingBox expand(int x, int y, int z) {
            this.x1 -= x; this.y1 -= y; this.z1 -= z;
            this.x2 += x; this.y2 += y; this.z2 += z;
            if (this.childA != null) this.childA.expand(x, y, z);
            if (this.childB != null) this.childB.expand(x, y, z);
            return this;
        }

        boolean contains(int x, int y, int z) {
            return x >= x1 && x <= x2 &&
                    y >= y1 && y <= y2 &&
                    z >= z1 && z <= z2;
        }

        boolean containsHorizontal(int x, int z) {
            return x >= x1 && x <= x2 &&
                    z >= z1 && z <= z2;
        }

        boolean intersects(int x1, int y1, int z1, int x2, int y2, int z2) {
            return this.x1 < x2 && this.x2 > x1 && this.y1 < y2 && this.y2 > y1 && this.z1 < z2 && this.z2 > z1;
        }

        boolean intersectsRecursive(int x1, int y1, int z1, int x2, int y2, int z2) {
            if (this.intersects(x1, y1, z1, x2, y2, z2)) {
                if (this.childA != null && this.childA.intersectsRecursive(x1, y1, z1, x2, y2, z2)) {
                    return true;
                } else if (this.childB != null && this.childB.intersectsRecursive(x1, y1, z1, x2, y2, z2)) {
                    return true;
                }
                return true;
            }
            return false;
        }

        static RiverBoundingBox fromSegments(RiverSegment[] segments, int start, int end) {
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

            for (int i = start; i < end; i++) {
                RiverSegment seg = segments[i];
                minX = Math.min(minX, Math.min(seg.startX(), seg.endX()));
                minY = Math.min(minY, Math.min(Mth.floor(seg.startHeight()), Mth.floor(seg.endHeight())));
                minZ = Math.min(minZ, Math.min(seg.startZ(), seg.endZ()));
                maxX = Math.max(maxX, Math.max(seg.startX(), seg.endX()));
                maxY = Math.max(maxY, Math.max(Mth.ceil(seg.startHeight()), Mth.ceil(seg.endHeight())));
                maxZ = Math.max(maxZ, Math.max(seg.startZ(), seg.endZ()));
            }

            RiverBoundingBox box = new RiverBoundingBox(-1, minX, minY, minZ, maxX, maxY, maxZ);

            if (end - start == 1) {
                // leaf node
                box.riverSegmentIndex = start;
            } else {
                int mid = (start + end) / 2;
                box.childA = fromSegments(segments, start, mid);
                box.childB = fromSegments(segments, mid, end);
            }

            return box;
        }
    }

}
