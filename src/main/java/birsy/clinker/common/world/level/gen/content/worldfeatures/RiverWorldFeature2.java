package birsy.clinker.common.world.level.gen.content.worldfeatures;

import birsy.clinker.client.render.debug.RiverDebugRenderer;
import birsy.clinker.common.networking.packet.debug.ClientboundRiverDebugPacket;
import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesCaveDensity;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesFluids;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.core.Clinker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RiverWorldFeature2 extends WorldFeature implements ModifiesCaveDensity, ModifiesFluids {
    static final int MAXIMUM_SHORELINE_SIZE = 24;
    final int centerX, centerZ;
    final River river;

    private RiverWorldFeature2(int centerX, int centerZ, River river) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.river = river;
    }
    @Override public int getCenterX() { return this.centerX; }
    @Override public int getCenterZ() { return this.centerZ; }
    @Override public boolean within(int minX, int minZ, int maxX, int maxZ) { return this.river.within(minX, minZ, maxX, maxZ); }

    public static Optional<RiverWorldFeature2> realize(@Nullable BlockPos center, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth,
                                                       RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext) {
        BlockPos.MutableBlockPos source = BlockPos.ZERO.mutable(), drain = BlockPos.ZERO.mutable();
        for (int i = 0; i < 32; i++) {
            source.set(randomSource.nextIntBetweenInclusive(minX, maxX), 35, randomSource.nextIntBetweenInclusive(minZ, maxZ));
            drain.set(randomSource.nextIntBetweenInclusive(minX, maxX), 10, randomSource.nextIntBetweenInclusive(minZ, maxZ));
            double distance = Mth.length(source.getX() - drain.getX(), source.getZ() - drain.getZ());
            if (distance >= 50) return Optional.of(realize(source, drain, level, randomSource, context, worldContext));
        }
        return Optional.empty();
    }
    public static RiverWorldFeature2 realize(BlockPos source, BlockPos drain, LevelAccessor level,
                                             RandomSource randomSource, UncachedNoiseContext context,
                                             WorldFeatureContext worldContext) {
        Clinker.LOGGER.info("creating river from {} {} {} to {} {} {}", source.getX(), source.getY(), source.getZ(), drain.getX(), drain.getY(), drain.getZ());

        int centerX = (source.getX() + drain.getX()) / 2, centerZ = (source.getZ() + drain.getZ()) / 2;
        List<PathNode> path = new ArrayList<>();
        path.add(new PathNode(source.getX(), source.getY(), source.getZ(), 5, 1));
        path.add(new PathNode(drain.getX(), drain.getY(), drain.getZ(), 20, 2));
        path = resamplePath(path, 30);

        double pX = source.getZ() - drain.getZ(), pZ = drain.getX() - source.getX();
        double length = Mth.length(pX, pZ);
        pX /= length; pZ /= length;
        for (int i = 1; i < path.size() - 1; i++) {
            double offsetAmount = randomSource.nextGaussian() * 20;
            PathNode node = path.get(i);
            node.y = Mth.lerpDiscrete(randomSource.nextFloat(), path.get(i - 1).y, path.get(i + 1).y);
            node.x += (int) (pX * offsetAmount);
            node.z += (int) (pZ * offsetAmount);
        }

        River river = new River(resamplePath(path, 15));
        PacketDistributor.sendToAllPlayers(
                new ClientboundRiverDebugPacket(river.nodes.stream().map(node -> new RiverDebugRenderer.RiverDebugPoint(node.x, node.y, node.z, node.radius, node.depth)).toList())
        );
        return new RiverWorldFeature2(centerX, centerZ, river);
    }

    static List<PathNode> resamplePath(List<PathNode> points, int distance) {
        List<PathNode> result = new ArrayList<>();
        result.add(points.getFirst());

        // how far along the current segment until the next sample is due
        float distanceUntilNext = distance;

        for (int i = 0; i < points.size() - 1; i++) {
            PathNode point = points.get(i), nextPoint = points.get(i + 1);
            float segmentLength = (float) Mth.length(nextPoint.x - point.x, nextPoint.z - point.z);

            // too short, skip!
            if (segmentLength < 0.001f) {
                distanceUntilNext -= segmentLength;
                continue;
            }
            while (distanceUntilNext < segmentLength) {
                float t = distanceUntilNext / segmentLength;
                result.add(new PathNode(
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

    @Override
    public void modifyCaveDensity(int minX, int minY, int minZ, int maxCaveHeight, NoiseFieldCache cache, NoiseField field, NoiseField maskField, WorldFeatureContext worldContext) {
        RiverSample sample = new RiverSample();

        NoiseField trueRiverDistanceField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] trueRiverDistanceArray = trueRiverDistanceField.array();
        NoiseField miteredDistanceToRiverField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] mitredDistanceToRiverArray = miteredDistanceToRiverField.array();
        NoiseField distanceAlongRiverField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] distanceAlongRiverArray = distanceAlongRiverField.array();
        NoiseField riverRadiusField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] riverRadiusArray = riverRadiusField.array();
        NoiseField riverCeilingHeightField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] riverCeilingHeightArray = riverCeilingHeightField.array();

        miteredDistanceToRiverField.byBlock((index, x, y, z) -> {
            river.sample(sample, x + cache.minX, z + cache.minZ);
            trueRiverDistanceArray[index] = sample.trueDistance;
            mitredDistanceToRiverArray[index] = sample.miteredDistanceFromRiver;
            distanceAlongRiverArray[index] = sample.distanceAlongRiver;
            riverRadiusArray[index] = sample.radius;
            riverCeilingHeightArray[index] = sample.ceilingHeight;
        });

        NoiseField riverWaterHeightField = NoiseFieldTypes.FINE_2D.create(cache.chunkHeight, 0);
        double[] riverWaterHeightArray = riverWaterHeightField.array();
        riverWaterHeightField.byBlock((index, x, y, z) -> {
            river.sample(sample, x + cache.minX, z + cache.minZ);
            riverWaterHeightArray[index] = sample.riverHeight;
        });

        double[] array = field.array();
        field.byBlock(river.minY - 5 - cache.minY, river.maxY + 5 - cache.minY, (index, x, y, z) -> {
            double dist = array[index];
            double trueDistanceToRiver = trueRiverDistanceField.retrieve(x, y, z);
            double miteredDistanceToRiver = miteredDistanceToRiverField.retrieve(x, y, z);
            double distanceAlongRiver = distanceAlongRiverField.retrieve(x, y, z);
            double riverRadius = riverRadiusField.retrieve(x, y, z);
            double riverCeilingHeight = riverCeilingHeightField.retrieve(x, y, z);
            double riverWaterHeight = riverWaterHeightField.retrieve(x, y, z);

            double gY = y + cache.minY;
            double yDist;
            if (gY > riverCeilingHeight) yDist = gY - riverCeilingHeight;
            else if (gY < riverWaterHeight) yDist = gY - riverWaterHeight;
            else yDist = 0;
            double riverTubeDist = Mth.length(trueDistanceToRiver, yDist);

            dist = Math.max(riverRadius - riverTubeDist, dist);
            array[index] = dist;
        });
    }

    @Override
    public void prefillFluidNoiseFields(int chunkX, int chunkZ, PaddedNoiseFieldCache cache, WorldFeatureContext worldContext) {

    }
    @Override
    public FluidLevel modifyFluidLevel(int x, int y, int z, int minX, int minY, int minZ, FluidLevel currentFluidLevel, NoiseContext context, NoiseField heightmap) {
        if (y > river.maxY + 8 || y < river.minY - 8) return currentFluidLevel;

        RiverSample sample = river.sample(new RiverSample(), x, z);
        double distanceToRiver = sample.trueDistance;
        double riverRadius = sample.radius;
        if (distanceToRiver > riverRadius + 4) return currentFluidLevel;

        double riverCeilingHeight = sample.ceilingHeight, riverWaterHeight = sample.riverHeight;
        double yDist;
        if (y > riverCeilingHeight) yDist = y - riverCeilingHeight;
        else if (y < riverWaterHeight) yDist = y - riverWaterHeight;
        else yDist = 0;

        // todo: replace with depth if negative
        if (yDist > riverRadius + 4) return currentFluidLevel;

        return new FluidLevel((int) Math.round(riverWaterHeight), Blocks.WATER.defaultBlockState());
    }

    static final class PathNode {
        private int x, y, z;
        private float radius, depth;
        public PathNode(int x, int y, int z, float radius, float depth) {
            this.x = x; this.y = y; this.z = z;
            this.radius = radius; this.depth = depth;
        }
    }
    record RiverNode(int x, int y, int z, double ceilingY, float radius, float depth, float distanceAlongRiver, float miterX, float miterZ, float directionX, float directionZ) {
        double perpendicularDistance(double sX, double sZ) { return (sX - x) * (-directionZ) + (sZ - z) * directionX; }
        double miterDot(double sX, double sZ) { return (sX - x) * miterX + (sZ - z) * miterZ; }
    }
    // water flowing downhill is a little janky, so instead its all waterfalls where the river height suddenly jumps to the next river below
    // but, it looks kinda weird with the ceiling when that happens, so that's interpolated between sections.
    static final class RiverSample {
        private int index = -1;
        private double trueDistance = Double.MAX_VALUE;
        private float interpolationFactor;
        private int riverHeight;
        private double ceilingHeight;
        private float miteredDistanceFromRiver, distanceAlongRiver;
        private float radius, depth;
    }
    static final class RiverBoundingBox {
        @Nullable private final RiverBoundingBox parent;
        private RiverBoundingBox child1, child2;
        private final int index;
        private final int x1, z1, x2, z2;

        RiverBoundingBox(@Nullable RiverBoundingBox parent, RiverBoundingBox child1, RiverBoundingBox child2, int index, int x1, int z1, int x2, int z2) {
            this.parent = parent;
            this.child1 = child1; this.child2 = child2;
            this.index = index;
            this.x1 = x1; this.z1 = z1;
            this.x2 = x2; this.z2 = z2;
        }

        static RiverBoundingBox generateTree(River river, @Nullable RiverBoundingBox parent, int startIndex, int endIndex) {
            int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

            for (int i = startIndex; i <= endIndex; i++) {
                RiverNode node = river.nodes.get(i);
                int radius = (int) Math.ceil(node.radius) + MAXIMUM_SHORELINE_SIZE;
                minX = Math.min(minX, node.x - radius);
                maxX = Math.max(maxX, node.x + radius);
                minZ = Math.min(minZ, node.z - radius);
                maxZ = Math.max(maxZ, node.z + radius);
            }

            if (endIndex - startIndex == 1) {
                RiverBoundingBox box = new RiverBoundingBox(parent, null, null, startIndex, minX, minZ, maxX, maxZ);
                return box;
            } else {
                int midIndex = (startIndex + endIndex) / 2;
                RiverBoundingBox box = new RiverBoundingBox(parent, null, null, -1, minX, minZ, maxX, maxZ);
                box.child1 = generateTree(river, box, startIndex, midIndex);
                box.child2 = generateTree(river, box, midIndex, endIndex);
                return box;
            }
        }

        boolean intersects(int x1, int z1, int x2, int z2) {
            return this.x1 < x2 && this.x2 > x1 && this.z1 < z2 && this.z2 > z1;
        }
        boolean intersectsRecursive(int x1, int z1, int x2, int z2) {
            if (intersects(x1, z1, x2, z2)) {
                if (child1 != null) {
                    return this.child1.intersectsRecursive(x1, z1, x2, z2) || this.child2.intersectsRecursive(x1, z1, x2, z2);
                } else {
                    return true;
                }
            }
            return false;
        }
        double distanceToBoxSq(double x, double z) {
            double distX = Math.max(0, Math.max(x1 - x, x - x2)), distZ = Math.max(0, Math.max(z1 - z, z - z2));
            return distX * distX + distZ * distZ;
        }
        boolean isLeaf() {
            return this.index >= 0;
        }
    }
    static class River {
        final List<RiverNode> nodes;
        final RiverBoundingBox bvh;
        final int minY, maxY;
        River(List<PathNode> path) {
            int pathSize = path.size();

            float[] directionX = new float[pathSize - 1], directionZ = new float[pathSize - 1];
            for (int i = 0; i < pathSize - 1; i++) {
                PathNode point = path.get(i), nextPoint = path.get(i + 1);
                float dX = nextPoint.x - point.x, dZ = nextPoint.z - point.z;
                float length = (float) Mth.length(dX, dZ);
                if (length < 0.0001f) { directionX[i] = 1; directionZ[i] = 0; continue; }
                directionX[i] = dX / length; directionZ[i] = dZ / length;
            }

            this.nodes = new ArrayList<>(pathSize);
            float totalDistanceAlongRiver = 0;
            float ceilingHeight = path.getFirst().y;
            int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
            for (int i = 0; i < pathSize; i++) {
                PathNode point = path.get(i);
                boolean first = i == 0, last = i == pathSize - 1;
                PathNode prevPoint = first ? point : path.get(i - 1);

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
                nodes.add(new RiverNode(
                        point.x, point.y, point.z,
                        ceilingHeight, point.radius, point.depth,
                        totalDistanceAlongRiver,
                        miterX, miterZ, dirX, dirZ
                ));
            }
            this.minY = minY;
            this.maxY = maxY;
            this.bvh = RiverBoundingBox.generateTree(this, null,0, nodes.size() - 1);
        }

        private boolean within(int x1, int z1, int x2, int z2) {
            return bvh.intersectsRecursive(x1, z1, x2, z2);
        }

        // line segment distance i stole
        private double segmentDistanceSq(int i, double px, double pz) {
            RiverNode a = nodes.get(i), b = nodes.get(i + 1);
            double bax = b.x() - a.x(), bay = b.z() - a.z();
            double rx = px - a.x(), ry = pz - a.z();
            double h = Mth.clamp((rx * bax + ry * bay) / (bax * bax + bay * bay), 0, 1);
            double dx = rx - bax * h, dy = ry - bay * h;
            return dx * dx + dy * dy;
        }

        private void computeSample(RiverSample out, double sX, double sZ, int index) {
            RiverNode node = nodes.get(index), nextNode = nodes.get(index + 1);

            double projectedLength = (nextNode.x() - node.x()) * node.miterX() + (nextNode.z() - node.z()) * node.miterZ();
            double alpha;
            if (Math.abs(projectedLength) > 0.0001) {
                alpha = node.miterDot(sX, sZ) / projectedLength;
            } else {
                // 180 degree fallback!
                double dx = nextNode.x() - node.x(), dz = nextNode.z() - node.z();
                double segLen = Math.sqrt(dx * dx + dz * dz);
                alpha = segLen < 0.0001 ? 0 : ((sX - node.x()) * node.directionX() + (sZ - node.z()) * node.directionZ()) / segLen;
            }
            alpha = Mth.clamp(alpha, 0.0, 1.0);

            out.index = index;
            out.trueDistance = Math.sqrt(out.trueDistance);
            out.interpolationFactor = (float) alpha;
            out.miteredDistanceFromRiver = (float) node.perpendicularDistance(sX, sZ);
            out.distanceAlongRiver = (float) Mth.lerp(alpha, node.distanceAlongRiver(), nextNode.distanceAlongRiver());
            out.radius = (float) Mth.lerp(alpha, node.radius(), nextNode.radius());
            out.depth = (float) Mth.lerp(alpha, node.depth(),  nextNode.depth());
            out.riverHeight = nextNode.y();
            out.ceilingHeight = Mth.lerp(alpha, node.ceilingY(), nextNode.ceilingY());
        }

        private void findClosestSegment(RiverBoundingBox box, double px, double pz, RiverSample sample) {
            if (box.distanceToBoxSq(px, pz) >= sample.trueDistance) return;
            if (box.isLeaf()) {
                double boxDist = segmentDistanceSq(box.index, px, pz);
                if (boxDist < sample.trueDistance) {
                    sample.trueDistance = boxDist;
                    sample.index = box.index;
                }
                return;
            }
            RiverBoundingBox near = box.child1, far = box.child2;
            if (near.distanceToBoxSq(px, pz) > far.distanceToBoxSq(px, pz)) {
                RiverBoundingBox tmp = near; near = far; far = tmp;
            }
            findClosestSegment(near, px, pz, sample);
            findClosestSegment(far,  px, pz, sample);
        }

        // mutates the sample object to save on garbage collection stuffs
        RiverSample sample(RiverSample sample, double x, double z) {
            // seed trueDistance from the previous result so the bvh gets a tight initial bound,
            // then let findClosestSegment beat it if anything is closer
            sample.trueDistance = sample.index >= 0 ? segmentDistanceSq(sample.index, x, z) : Double.MAX_VALUE;
            findClosestSegment(bvh, x, z, sample);
            computeSample(sample, x, z, sample.index);
            return sample;
        }
    }
}
