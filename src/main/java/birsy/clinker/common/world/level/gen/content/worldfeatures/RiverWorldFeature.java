package birsy.clinker.common.world.level.gen.content.worldfeatures;

import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesCaveDensity;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesFluids;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.registry.worldgen.ClinkerWorldFeatures;
import birsy.clinker.core.util.MathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RiverWorldFeature extends WorldFeature implements ModifiesCaveDensity, ModifiesFluids {
    final int centerX, centerZ;
    final CompiledRiver river;

    private RiverWorldFeature(int centerX, int centerZ, CompiledRiver river) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.river = river;
    }

    public static Optional<RiverWorldFeature> realize(@Nullable BlockPos center,
                                                      LevelAccessor level,
                                                      int minX, int minZ, int maxX, int maxZ, int metaChunkDepth,
                                                      RandomSource randomSource,
                                                      UncachedNoiseContext context,
                                                      WorldFeatureContext worldContext) {
        BlockPos.MutableBlockPos source = BlockPos.ZERO.mutable(), drain = BlockPos.ZERO.mutable();
        for (int i = 0; i < 32; i++) {
            source.set(randomSource.nextIntBetweenInclusive(minX, maxX), 35, randomSource.nextIntBetweenInclusive(minZ, maxZ));
            drain.set(randomSource.nextIntBetweenInclusive(minX, maxX), 10, randomSource.nextIntBetweenInclusive(minZ, maxZ));
            double distance = Mth.length(source.getX() - drain.getX(), source.getZ() - drain.getZ());
            if (distance >= 50) return Optional.of(realize(source, drain, level, minX, minZ, maxX, maxZ, metaChunkDepth, randomSource, context, worldContext));
        }
        return Optional.empty();
    }

    public static RiverWorldFeature realize(BlockPos source, BlockPos drain, LevelAccessor level,
                                            int minX, int minZ, int maxX, int maxZ, int metaChunkDepth,
                                            RandomSource randomSource,
                                            UncachedNoiseContext context,
                                            WorldFeatureContext worldContext) {
        Clinker.LOGGER.info("creating river from {} {} {} to {} {} {}", source.getX(), source.getY(), source.getZ(), drain.getX(), drain.getY(), drain.getZ());

        ObjectArrayList<BlockPos> riverCurve = generateRiverCurve(source, drain, 128, randomSource, context);
        ObjectArrayList<RiverNode> nodes = resampleCurve(riverCurve, 10);

        int centerX = (source.getX() + drain.getX()) / 2, centerZ = (source.getZ() + drain.getZ()) / 2;

        return new RiverWorldFeature(centerX, centerZ, new CompiledRiver(nodes.toArray(new RiverNode[0])));
    }

    private static ObjectArrayList<BlockPos> generateRiverCurve(BlockPos startPos, BlockPos endPos, int count, RandomSource randomSource, UncachedNoiseContext context) {
        int riverX = endPos.getX() - startPos.getX(),
            riverZ = endPos.getZ() - startPos.getZ();
        int perpRiverX = riverZ,
            perpRiverZ = -riverX;
        double riverLength = Mth.length(riverX, riverZ);

        int frequency = (int) Math.round(riverLength / 100.0);
        ObjectArrayList<BlockPos> nodePositions = new ObjectArrayList<>(count);

        nodePositions.add(startPos);
        int y = startPos.getY();
        for (int i = 1; i < count - 1; i++) {
            float factor = i / (count - 1.0F);
            float midFactor = Mth.clampedMap(factor, 0, 0.5F, 0, 1) * Mth.clampedMap(factor, 0.5F, 1, 1, 0);
            midFactor = (float) Mth.smoothstep(midFactor);

            int baseX = Mth.lerpDiscrete(factor, startPos.getX(), endPos.getX()),
                baseZ = Mth.lerpDiscrete(factor, startPos.getZ(), endPos.getZ());
            float riverWiggliness = (float)context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[7], baseX, 0, baseZ);
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

    private static ObjectArrayList<RiverNode> resampleCurve(ObjectArrayList<BlockPos> nodePositions, double step) {
        ObjectArrayList<RiverNode> result = new ObjectArrayList<>();
        // compute length
        int n = nodePositions.size();
        double[] lengths = new double[n];
        lengths[0] = 0;

        for (int i = 1; i < n; i++)
            lengths[i] = lengths[i - 1] + Math.sqrt(nodePositions.get(i - 1).distSqr(nodePositions.get(i)));
        double totalLength = lengths[n - 1];

        // resample curve so its evenly spaced
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

            double riverRadius = 15.0F;

            ceilingHeight = MathUtils.approach(ceilingHeight, y, (int)(step / 8.0));
            double progress = targetLength / totalLength;
            riverRadius *= Mth.clampedMap(progress, 0, 0.2, 0.3, 1);
            riverRadius *= Mth.clampedMap(progress, 0.8, 1, 1, 0.5);
            riverRadius = Mth.lerp(riverRadius, 3, 20);

            if (!result.isEmpty()) {
                RiverNode previous = result.getLast();
                double lateralDistanceToPrevious = Mth.lengthSquared(previous.x - x, previous.z - z);
                if (lateralDistanceToPrevious < 0.001) continue;
            }
            result.add(new RiverNode(x, y, z, riverRadius));
        }

        BlockPos last = nodePositions.getLast();
        result.add(new RiverNode(last.getX(), last.getY(), last.getZ(), result.getLast().radius()));

        return result;
    }

    @Override
    public void modifyCaveDensity(int minX, int minY, int minZ, int maxCaveHeight, NoiseFieldCache cache, NoiseField field, NoiseField maskField, WorldFeatureContext worldContext) {
        int riverRadius = 15;
        int riverHeight = 35;
        cache.noiseHolder.registerNoise("riverbed");

        RiverSample sample = new RiverSample();
        NoiseField lateralRiverDistanceField = NoiseFieldTypes.FINE_2D.create(0,0),
                   riverBedNoiseField = NoiseFieldTypes.FINE_2D.create(0,0);
        double[] lateralRiverDistanceArray = lateralRiverDistanceField.array(),
                 riverBedNoiseArray = riverBedNoiseField.array();
        lateralRiverDistanceField.byBlock(0, cache.chunkHeight,
                (index, x, y, z) -> {
                    sample.reset(x + minX, z + minZ, river);
                    river.sample(x + minX, z + minZ, sample);
                    lateralRiverDistanceArray[index] = sample.distance;
                    riverBedNoiseArray[index] = Math.abs(cache.noiseHolder.sample("riverbed", sample.u * 0.02, sample.v * 0.06)) * 2.0 - 1.0;
                }
        );

        double[] caveNoiseArray = field.array();
        field.byBlock((riverHeight - riverRadius) - minY, (riverHeight + riverRadius) - minY,
                (index, x, y, z) -> {
                    double lateralDistance = lateralRiverDistanceField.retrieve(x, y, z);
                    double riverShape = riverRadius - Mth.length(lateralDistance, (y + minY) - riverHeight);
                    riverShape = Math.min((y + minY) - (2 * riverBedNoiseField.retrieve(x, y, z) + riverHeight - 1), riverShape);

                    double caveNoise = caveNoiseArray[index];

                    caveNoiseArray[index] = Math.max(caveNoise, riverShape);
                }
        );
    }

    @Override
    public void prefillFluidNoiseFields(int chunkX, int chunkZ, PaddedNoiseFieldCache cache, WorldFeatureContext worldContext) {}
    private final ThreadLocal<RiverSample> sharedFluidLevelSample = ThreadLocal.withInitial(RiverSample::new);
    @Override
    public FluidLevel modifyFluidLevel(int x, int y, int z, int minX, int minY, int minZ, FluidLevel currentFluidLevel, NoiseContext context, NoiseField heightmap) {
        int riverRadius = 15;
        int riverHeight = 35;

        RiverSample sample = sharedFluidLevelSample.get();
        sample.reset(x, z, this.river);
        sample.segmentIndex = -2;
        sample.distance = Double.POSITIVE_INFINITY;
        river.sample(x, z, sample);

        if (sample.distance > riverRadius) return currentFluidLevel;
        if (Math.abs(y - riverHeight) > riverRadius * 2) return currentFluidLevel;

        return new FluidLevel(riverHeight, Blocks.WATER.defaultBlockState());
    }

    @Override public int getCenterX() { return centerX; }
    @Override public int getCenterZ() { return centerZ; }
    @Override public boolean within(int minX, int minZ, int maxX, int maxZ) { return river.bvhRoot.intersectsRecursive(minX, Integer.MIN_VALUE, minZ, maxX, Integer.MAX_VALUE, maxZ); }

    // class stuffs
    record RiverNode(int x, int y, int z, double radius) {}
    record RiverBoundingBox(RiverBoundingBox child1, RiverBoundingBox child2, int vertexIndex, int x1, int y1, int z1, int x2, int y2, int z2) {
        boolean intersects(int x1, int y1, int z1, int x2, int y2, int z2) {
            return this.x1 < x2 && this.x2 > x1 && this.y1 < y2 && this.y2 > y1 && this.z1 < z2 && this.z2 > z1;
        }
        boolean intersectsRecursive(int x1, int y1, int z1, int x2, int y2, int z2) {
            if (intersects(x1, y1, z1, x2, y2, z2)) {
                if (child1 != null) {
                    return this.child1.intersectsRecursive(x1, y1, z1, x2, y2, z2) || this.child2.intersectsRecursive(x1, y1, z1, x2, y2, z2);
                } else {
                    return true;
                }
            }
            return false;
        }
        double distanceToBoxSq(double x, double z) {
            double distX = Math.max(0, Math.max(x1 - x, x - x2)),
                   distZ = Math.max(0, Math.max(z1 - z, z - z2));
            return distX * distX + distZ * distZ;
        }
        static RiverBoundingBox generateTree(CompiledRiver river, int startIndex, int endIndex) {
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

            for (int i = startIndex; i < endIndex; i++) {
                int r = (int) Math.ceil(Math.max(river.radius[i], river.radius[i+1]));

                minX = Math.min(minX, (int) Math.min(river.posX[i + 0], river.posX[i+1]) - r);
                maxX = Math.max(maxX, (int) Math.max(river.posX[i + 0], river.posX[i+1]) + r);
                minZ = Math.min(minZ, (int) Math.min(river.posZ[i], river.posZ[i+1]) - r);
                maxZ = Math.max(maxZ, (int) Math.max(river.posZ[i], river.posZ[i+1]) + r);
                minY = -64; maxY = 320;
            }
            if (endIndex - startIndex == 1) {
                return new RiverBoundingBox(null, null, startIndex, minX, minY, minZ, maxX, maxY, maxZ);
            } else {
                int midIndex = (startIndex + endIndex) / 2;
                return new RiverBoundingBox(
                        generateTree(river, startIndex, midIndex),
                        generateTree(river, midIndex, endIndex),
                        -1, minX, minY, minZ, maxX, maxY, maxZ
                );
            }
        }
    }
    static class RiverSample {
        int segmentIndex;
        double u, v, distance, segmentFactor;
        RiverSample() {
            this.segmentIndex = -2;
            this.u = 0; this.v = 0;
            this.distance = Double.POSITIVE_INFINITY;
            this.segmentFactor = 0;
        }
        // reset between samples...
        public void reset(int x, int z, CompiledRiver river) {
            this.u = 0; this.v = 0;
            this.segmentFactor = 0;

            // ...except the index & distance. because we sample spatially similar areas,
            // the closest index to one is quite likely to be the closest index to another.

            // sometimes the search is unsuccessful, though. in that case we do have to reset
            if (this.segmentIndex < 0 || this.segmentIndex >= river.vertexCount - 1) {
                this.segmentIndex = -2;
                this.distance = Double.POSITIVE_INFINITY;
                return;
            }
            double posX0 = river.posX[this.segmentIndex + 0], posZ0 = river.posZ[this.segmentIndex + 0];
            double posX1 = river.posX[this.segmentIndex + 1], posZ1 = river.posZ[this.segmentIndex + 1];
            this.distance = CompiledRiver.lineSegmentDistanceSq(x, z, posX0, posZ0, posX1, posZ1);
        }
    }
    static class CompiledRiver {
        final int vertexCount;
        final double totalLength;
        final RiverBoundingBox bvhRoot;
        final double[] posX, posZ;
        final double[] dirX, dirZ;
        final double[] miterX, miterZ; // actually normalized so not really a proper miter vector
        final double[] miterScale0, miterScale1;
        final double[] segmentLength, distanceAlongRiver, radius; // various per-vertex river attributes

        CompiledRiver(RiverNode... vertices) {
            this.vertexCount = vertices.length;
            this.posX = new double[vertexCount]; this.posZ = new double[vertexCount];
            this.dirX = new double[vertexCount]; this.dirZ = new double[vertexCount];
            this.miterX = new double[vertexCount]; this.miterZ = new double[vertexCount];
            this.miterScale0 = new double[vertexCount]; this.miterScale1 = new double[vertexCount];
            this.segmentLength = new double[vertexCount];
            this.distanceAlongRiver = new double[vertexCount];
            this.radius = new double[vertexCount];

            double totalLength = 0;
            double lastDirX = 0, lastDirZ = 0,
                   lastPerpX = 0, lastPerpZ = 0;
            for (int i = 0; i < vertexCount - 1; i++) {
                int i0 = i + 0, i1 = i + 1;
                RiverNode start = vertices[i0], end = vertices[i1];

                this.posX[i0] = start.x; this.posZ[i0] = start.z;
                this.posX[i1] = end.x; this.posZ[i1] = end.z;

                double dirX = end.x - start.x, dirZ = end.z - start.z;
                double length = Math.sqrt(dirX * dirX + dirZ * dirZ);
                if (length == 0) {
                    // no NaNs here!!!
                    dirX = 0; dirZ = 0;
                } else {
                    dirX /= length; dirZ /= length;
                }
                this.dirX[i0] = dirX; this.dirZ[i0] = dirZ;
                double perpX = -dirZ, perpZ = dirX;

                this.segmentLength[i0] = length;
                this.distanceAlongRiver[i0] = totalLength;
                totalLength += length;
                this.radius[i0] = start.radius;

                if (i == 0) {
                    lastDirX = dirX; lastDirZ = dirZ;
                    lastPerpX = perpX; lastPerpZ = perpZ;
                }

                double miterX = lastPerpX + perpX, miterZ = lastPerpZ + perpZ;
                double miterLength = Mth.length(miterX, miterZ);
                if (miterLength == 0) {
                    // no NaNs here!!!
                    miterX = 0; miterZ = 0;
                } else {
                    miterX /= miterLength; miterZ /= miterLength;
                }
                this.miterX[i] = miterX; this.miterZ[i] = miterZ;
                double miterScale0 = (miterX * dirZ - miterZ * dirX);
                this.miterScale0[i] = miterScale0 == 0 ? 1.0 : 1.0 / miterScale0;
                double miterScale1 = (miterX * lastDirX - miterZ * lastDirZ);
                this.miterScale1[i] = miterScale1 == 0 ? 1.0 : 1.0 / miterScale1;

                lastDirX = dirX; lastDirZ = dirZ;
                lastPerpX = perpX; lastPerpZ = perpZ;
            }
            // last vertex
            int lastIndex = vertexCount - 1;
            RiverNode lastNode = vertices[lastIndex];
            this.posX[lastIndex] = lastNode.x; this.posZ[lastIndex] = lastNode.z;
            this.dirX[lastIndex] = lastDirX; this.dirZ[lastIndex] = lastDirZ;
            this.miterX[lastIndex] = lastPerpX; this.miterZ[lastIndex] = lastPerpZ;
            this.miterScale0[lastIndex] = this.miterScale1[lastIndex] = 1.0;
            this.segmentLength[lastIndex] = 0;
            this.distanceAlongRiver[lastIndex] = totalLength;
            this.radius[lastIndex] = lastNode.radius;

            this.totalLength = totalLength;
            this.bvhRoot = RiverBoundingBox.generateTree(this, 0, this.vertexCount - 1);
        }

        // returns -1 if point is behind the river start,
        // returns vertexCount - 1 if point is ahead of the river end.
        public int segmentIndex(int x, int z, RiverSample sample) {
            findClosestSegment(this.bvhRoot, this.bvhRoot.distanceToBoxSq(x, z), x, z, sample);
            sample.distance = Math.sqrt(sample.distance);
            int closestIndex = sample.segmentIndex;
            if (closestIndex == 0) {
                double offsetX = x - posX[0], offsetZ = z - posZ[0];
                double dot = offsetX * dirX[0] + offsetZ * dirZ[0];
                if (dot < 0.0) return -1; // behind start
            } else if (closestIndex == vertexCount - 2) {
                double offsetX = x - posX[vertexCount - 1], offsetZ = z - posZ[vertexCount - 1];
                double dot = offsetX * dirX[vertexCount - 1] + offsetZ * dirZ[vertexCount - 1];
                if (dot > 0.0) return vertexCount - 1; // past end
            }
            return closestIndex;
        }

        private static double lineSegmentDistanceSq(double px, double py, double ax, double ay, double bx, double by) {
            double bax = bx - ax, bay = by - ay;
            double rx = px - ax, ry = py - ay;
            double h = Mth.clamp((rx * bax + ry * bay) / (bax * bax + bay * bay), 0, 1);
            double dx = rx - bax * h, dy = ry - bay * h;
            return dx * dx + dy * dy;
        }

        private void findClosestSegment(RiverBoundingBox bb, double distToBox, double x, double z, RiverSample sample) {
            if (distToBox > sample.distance) return;
            if (bb.child1 == null) {
                double posX0 = this.posX[bb.vertexIndex + 0],
                       posZ0 = this.posZ[bb.vertexIndex + 0];
                double posX1 = this.posX[bb.vertexIndex + 1],
                       posZ1 = this.posZ[bb.vertexIndex + 1];
                double distance = lineSegmentDistanceSq(x, z, posX0, posZ0, posX1, posZ1);
                if (distance < sample.distance) {
                    sample.distance = distance;
                    sample.segmentIndex = bb.vertexIndex;
                }
                return;
            }
            double d1 = bb.child1.distanceToBoxSq(x, z), d2 = bb.child2.distanceToBoxSq(x, z);
            if (d1 < d2) {
                findClosestSegment(bb.child1, d1, x, z, sample);
                findClosestSegment(bb.child2, d2, x, z, sample);
            } else {
                findClosestSegment(bb.child2, d2, x, z, sample);
                findClosestSegment(bb.child1, d1, x, z, sample);
            }
        }

        public void sample(int x, int z, RiverSample sample) {
            int segmentIndex = segmentIndex(x, z, sample);

            // special cases
            if (segmentIndex == -1) {
                double posX = this.posX[0], posZ = this.posZ[0];
                double dirX = this.dirX[0], dirZ = this.dirZ[0];
                sample.segmentIndex = 0;
                sample.segmentFactor = 0;
                sample.u = (x - posX) * dirX + (z - posZ) * dirZ;
                sample.v = (x - posX) * dirZ - (z - posZ) * dirX;
                return;
            } else if (segmentIndex >= this.vertexCount - 1) {
                double posX = this.posX[segmentIndex], posZ = this.posZ[segmentIndex];
                double dirX = this.dirX[segmentIndex], dirZ = this.dirZ[segmentIndex];
                sample.segmentFactor = 1;
                sample.u = totalLength + (x - posX) * dirX + (z - posZ) * dirZ;
                sample.v = (x - posX) * dirZ - (z - posZ) * dirX;
                return;
            }

            double dirX = this.dirX[segmentIndex + 0],
                   dirZ = this.dirZ[segmentIndex + 0];
            double posX0 = posX[segmentIndex + 0],
                   posZ0 = posZ[segmentIndex + 0];
            double miterX0 = miterX[segmentIndex + 0],
                   miterZ0 = miterZ[segmentIndex + 0];
            double posX1 = posX[segmentIndex + 1],
                   posZ1 = posZ[segmentIndex + 1];
            double miterX1 = miterX[segmentIndex + 1],
                   miterZ1 = miterZ[segmentIndex + 1];

            // the numerator of miter0Factor also represents the lateral distance to the segment, somehow. honestly no idea how this works out.
            double lateralDistance = (x - posX0) * dirZ - (z - posZ0) * dirX;
            double miter0Factor = lateralDistance * miterScale0[segmentIndex];
            double miter0intersectionX = posX0 + miter0Factor * miterX0,
                   miter0intersectionZ = posZ0 + miter0Factor * miterZ0;

            double miter1Factor = ((x - posX1) * dirZ - (z - posZ1) * dirX) * miterScale1[segmentIndex + 1];
            double miter1intersectionX = posX1 + miter1Factor * miterX1,
                   miter1intersectionZ = posZ1 + miter1Factor * miterZ1;

            double miterXDiff = miter1intersectionX - miter0intersectionX,
                   miterZDiff = miter1intersectionZ - miter0intersectionZ;
            double segmentFactor = ((x - miter0intersectionX) * miterXDiff + (z - miter0intersectionZ) * miterZDiff) /
                                   (miterXDiff * miterXDiff + miterZDiff * miterZDiff);
            if (Double.isNaN(segmentFactor))
                Clinker.LOGGER.info("segmentFactor NaN! If you see this, tell the developers. Provide your world seed and coordinates!");

            sample.segmentFactor = segmentFactor;
            // distance along segment, kinda?
            sample.u = segmentFactor * segmentLength[segmentIndex] + distanceAlongRiver[segmentIndex];
            // lateral distance to the segment
            sample.v = lateralDistance;
        }
    }
}
