package birsy.clinker.common.world.level.gen.worldfeature.worldfeatures;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunk;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class UndergroundRiverWorldFeature extends WorldFeature {
    private int radius = 10;
    private RiverSegment[] segments;
    private RiverBoundingBox boundingBoxHierarchy;
    private NoiseComputer riverDistanceComputer, riverSurfaceHeightComputer, riverCeilingHeightComputer;

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
        List<BlockPos> riverShape = generateRiverCurve(
                new BlockPos(randomSource.nextInt(metaChunk.minX(), metaChunk.maxX()), 35, randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ())),
                new BlockPos(randomSource.nextInt(metaChunk.minX(), metaChunk.maxX()), 10, randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ())),
                64, metaChunk, randomSource, context
        );
        List<RiverNode> riverNodes = resampleCurve(riverShape, 10);

        this.segments = new RiverSegment[riverNodes.size() - 1];
        for (int i = 0; i < riverNodes.size() - 1; i++) {
            this.segments[i] = new RiverSegment(riverNodes.get(i), riverNodes.get(i + 1));
        }

        this.boundingBoxHierarchy = RiverBoundingBox.fromSegments(this.segments, 0, this.segments.length);
        this.boundingBoxHierarchy.expand(radius * 2, radius * 2, radius * 2);

        this.riverDistanceComputer = new NoiseComputer("river_distance_" + randomSource.nextInt(), CacheType.INTERPOLATED_2D_COARSE,
                (x, y, z, noiseContext) -> {
                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
                    return coordinates.horizontalDistance();
                }
        );
        this.riverSurfaceHeightComputer = new NoiseComputer("river_surface_height_" + randomSource.nextInt(), CacheType.INTERPOLATED_2D_FINE,
                (x, y, z, noiseContext) -> {
                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
                    return coordinates.riverHeight();
                }
        );
        this.riverCeilingHeightComputer = new NoiseComputer("river_ceiling_height_" + randomSource.nextInt(), CacheType.INTERPOLATED_2D_COARSE,
                (x, y, z, noiseContext) -> {
                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
                    return coordinates.ceilingHeight();
                }
        );

        Clinker.LOGGER.info("River generated starting at {} {} {} and ending at {} {} {}",
                riverShape.getFirst().getX(), riverShape.getFirst().getY(), riverShape.getFirst().getZ(),
                riverShape.getLast().getX(), riverShape.getLast().getY(), riverShape.getLast().getZ());
        return true;
    }

    private static List<BlockPos> generateRiverCurve(BlockPos startPos, BlockPos endPos, int count, MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
        int riverX = endPos.getX() - startPos.getX(),
            riverZ = endPos.getZ() - startPos.getZ();
        int perpRiverX = riverZ,
            perpRiverZ = -riverX;
        double riverLength = Mth.length(riverX, riverZ);

        int frequency = (int) Math.round(riverLength / 60.0);
        List<BlockPos> nodePositions = new ArrayList<>(count);

        nodePositions.add(startPos);
        int y = startPos.getY();
        for (int i = 1; i < count - 1; i++) {
            float factor = i / (count - 1.0F);
            float midFactor = Mth.clampedMap(factor, 0, 0.5F, 0, 1) * Mth.clampedMap(factor, 0.5F, 1, 1, 0);
            midFactor = (float) Mth.smoothstep(midFactor);

            int baseX = Mth.lerpDiscrete(factor, startPos.getX(), endPos.getX()),
                baseZ = Mth.lerpDiscrete(factor, startPos.getZ(), endPos.getZ());
            float riverWiggliness = (float)context.noiseComputerExecutor().compute(baseX, 0, baseZ, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
            riverWiggliness = riverWiggliness * 0.5F + 0.5F;
            riverWiggliness *= midFactor;

            float localX = factor - Mth.sin(frequency * factor * Mth.TWO_PI) * riverWiggliness * 0.3F * 0.2F,
                  localZ = Mth.sin(frequency * factor * Mth.PI) * riverWiggliness * 0.3F;
            double xOffset = riverX * localX + perpRiverX * localZ,
                   zOffset = riverZ * localX + perpRiverZ * localZ;

            // some logic for computing the y...
            // honestly could not really tell you how this worked.
            int startY = startPos.getY(),
                endY = endPos.getY();
            int straightLineY = Mth.lerpDiscrete(factor, startY, endY);
            int difference = y - straightLineY;
            if (Math.abs(difference) > 4 && difference > randomSource.nextInt(24))
                y = straightLineY;

            BlockPos nodePos = new BlockPos(startPos.getX() + (int) xOffset, y, startPos.getZ() + (int) zOffset);
            nodePositions.add(nodePos);
        }
        nodePositions.add(endPos);
        return nodePositions;
    }

    private static List<RiverNode> resampleCurve(List<BlockPos> nodePositions, double step) {
        if (nodePositions.size() < 2) return Collections.emptyList();

        // compute length
        int n = nodePositions.size();
        double[] lengths = new double[n];
        lengths[0] = 0;

        for (int i = 1; i < n; i++)
            lengths[i] = lengths[i - 1] + Math.sqrt(nodePositions.get(i - 1).distSqr(nodePositions.get(i)));
        double totalLength = lengths[n - 1];

        // resample curve so its evenly spaced
        List<RiverNode> result = new ArrayList<>();
        int ceilingHeight = nodePositions.getFirst().getY();
        for (double targetLength = 0; targetLength <= totalLength; targetLength += step) {
            int x, y, z;

            int closestIndex = Arrays.binarySearch(lengths, targetLength);
            if (closestIndex < 0) closestIndex = -closestIndex - 1;

            if (closestIndex <= 0) {
                x = nodePositions.getFirst().getX(); y = nodePositions.getFirst().getY(); z = nodePositions.getFirst().getZ();
            } else if (closestIndex >= n) {
                x = nodePositions.getLast().getX(); y = nodePositions.getLast().getY(); z = nodePositions.getLast().getZ();
            } else {
                BlockPos a = nodePositions.get(closestIndex - 1);
                BlockPos b = nodePositions.get(closestIndex);

                double segStart = lengths[closestIndex - 1];
                double segEnd = lengths[closestIndex];
                float f = (float) ((targetLength - segStart) / (segEnd - segStart));

                x = Mth.lerpDiscrete(f, a.getX(), b.getX());
                y = f < 0.5 ? a.getY() : b.getY();
                z = Mth.lerpDiscrete(f, a.getZ(), b.getZ());
            }

            ceilingHeight = MathUtils.approach(ceilingHeight, y, (int)(step / 8.0));
            double progress = targetLength / totalLength;
            result.add(new RiverNode(progress, x, z, y, ceilingHeight,  true));
        }

        BlockPos last = nodePositions.getLast();
        result.add(new RiverNode(1.0, last.getX(), last.getZ(), last.getY(), ceilingHeight, true));

        return result;
    }

    @Override
    public double modifyTerrain(int x, int y, int z, double currentNoiseValue, NoiseComputerContext context) {
        double riverDistance = context.noiseComputerExecutor().compute(x, y, z, this.riverDistanceComputer) * 0.5;
        double waterHeight = context.noiseComputerExecutor().compute(x, y, z, this.riverSurfaceHeightComputer),
               ceilingHeight = context.noiseComputerExecutor().compute(x, y, z, this.riverCeilingHeightComputer);
        // out of range
        if (waterHeight <= -9999 || ceilingHeight <= -9999 || riverDistance >= 9999)
            return currentNoiseValue;

        double yDist = 0;
        if (y < waterHeight) {
            yDist = y - waterHeight;
        } else if (y > ceilingHeight) {
            yDist = y - ceilingHeight;
        }
        return Math.max(currentNoiseValue, radius - Math.sqrt(riverDistance * riverDistance + yDist * yDist));
    }

    @Override
    public FluidLevel modifyFluidLevel(int x, int y, int z, FluidLevel currentFluidLevel, NoiseComputerContext context) {
        RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
        int yDiff = y - (int)coordinates.riverHeight();
        if (yDiff > radius * 3 || yDiff < -radius * 2) return currentFluidLevel;
        return new FluidLevel((int) coordinates.riverHeight, Blocks.WATER.defaultBlockState());
    }

    @Override
    public double modifyWaterfallPresence(int x, int y, int z, double currentValue, NoiseComputerContext context) {
        RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
        int yDiff = y - (int) coordinates.riverHeight();
        if (yDiff > radius * 3 || yDiff < -radius * 2 || coordinates.horizontalDistance > radius) return currentValue;
        return Mth.clampedMap(coordinates.horizontalDistance, 0, radius, 1.5, 0.5);
    }

    public RiverSpaceCoordinates getRiverSpaceCoordinates(int x, int y, int z, boolean ignoreY) {
        MutableRiverSpaceCoordinates best = new MutableRiverSpaceCoordinates(1000, -1000, -1000);
        sampleDistanceFromRiverRecursive(this.boundingBoxHierarchy, x, y, z, ignoreY, best);
        return best.toImmutable();
    }

    private void sampleDistanceFromRiverRecursive(RiverBoundingBox box, int x, int y, int z, boolean ignoreY, MutableRiverSpaceCoordinates best) {
        if (ignoreY) {
            if (!box.containsHorizontal(x, z)) return; // don't take y into consideration
        } else {
            if (box.contains(x, y, z)) return;
        }

        if (box.riverSegmentIndex >= 0) {
            RiverSegment segment = this.segments[box.riverSegmentIndex];

            int horizontalDistance = (int) lineSegmentDistance(x, z, segment.startX(), segment.startZ(), segment.endX(), segment.endZ());

            if (horizontalDistance < best.horizontalDistance) {
                best.horizontalDistance = horizontalDistance;

                double gradient = (x - segment.endX()) * (segment.startX() - segment.endX()) +
                                  (z - segment.endZ()) * (segment.startZ() - segment.endZ());
                gradient /= (segment.startX() - segment.endX()) * (segment.startX() - segment.endX()) +
                            (segment.startZ() - segment.endZ()) * (segment.startZ() - segment.endZ());
                double ceilingHeight = Mth.clampedLerp(segment.endCeilingHeight(), segment.startCeilingHeight(), gradient);
                best.surfaceHeight = segment.startWaterHeight();
                best.ceilingHeight = ceilingHeight;
            }
        } else {
            sampleDistanceFromRiverRecursive(box.childA, x, y, z, ignoreY, best);
            sampleDistanceFromRiverRecursive(box.childB, x, y, z, ignoreY, best);
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

    private record RiverSpaceCoordinates(int horizontalDistance, double riverHeight, double ceilingHeight) {}
    private static class MutableRiverSpaceCoordinates {
        int horizontalDistance;
        double surfaceHeight, ceilingHeight;
        MutableRiverSpaceCoordinates(int horiz, double surfaceHeight, double ceilingHeight) { this.horizontalDistance = horiz; this.surfaceHeight = surfaceHeight; this.ceilingHeight = ceilingHeight; }
        RiverSpaceCoordinates toImmutable() { return new RiverSpaceCoordinates(horizontalDistance, surfaceHeight, ceilingHeight); }
    }

    private record RiverNode(double progress, int x, int z, int waterHeight, int ceilingSurfaceHeight, boolean underground) {}
    private record RiverSegment(RiverNode start, RiverNode end) {
        double startProgress() { return start.progress(); }
        double endProgress() { return end.progress(); }
        int startX() { return start.x(); }
        int endX() { return end.x(); }
        int startZ() { return start.z(); }
        int endZ() { return end.z(); }
        int startWaterHeight() { return start.waterHeight(); }
        int endWaterHeight() { return end.waterHeight(); }
        int startCeilingHeight() { return start.ceilingSurfaceHeight(); }
        int endCeilingHeight() { return end.ceilingSurfaceHeight(); }
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
                int minWaterHeight = Math.min(Mth.floor(seg.startWaterHeight()), Mth.floor(seg.endWaterHeight())),
                    minCeilingHeight = Math.min(Mth.floor(seg.startCeilingHeight()), Mth.floor(seg.endCeilingHeight()));
                int maxWaterHeight = Math.max(Mth.ceil(seg.startWaterHeight()), Mth.ceil(seg.endWaterHeight())),
                    maxCeilingHeight = Math.max(Mth.ceil(seg.startCeilingHeight()), Mth.ceil(seg.endCeilingHeight()));
                minX = Math.min(minX, Math.min(seg.startX(), seg.endX()));
                minY = Math.min(minY, Math.min(minCeilingHeight, minWaterHeight));
                minZ = Math.min(minZ, Math.min(seg.startZ(), seg.endZ()));
                maxX = Math.max(maxX, Math.max(seg.startX(), seg.endX()));
                maxY = Math.max(maxY, Math.max(maxCeilingHeight, maxWaterHeight));
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
