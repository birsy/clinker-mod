package birsy.clinker.common.world.level.gen.content.worldfeatures.river;

import birsy.clinker.client.render.debug.RiverDebugRenderer;
import birsy.clinker.common.networking.packet.debug.ClientboundRiverDebugPacket;
import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesCaveDensity;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesFluids;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesWaterfallPresence;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.util.MathUtils;
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

public class RiverWorldFeature extends WorldFeature implements ModifiesCaveDensity, ModifiesFluids, ModifiesWaterfallPresence {
    static final int MAXIMUM_SHORELINE_SIZE = 24;

    final double randomOffset;
    final int centerX, centerZ;
    final River river;

    private RiverWorldFeature(int centerX, int centerZ, River river, double randomOffset) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.river = river;
        this.randomOffset = randomOffset;
    }
    @Override public int getCenterX() { return this.centerX; }
    @Override public int getCenterZ() { return this.centerZ; }
    @Override public boolean within(int minX, int minZ, int maxX, int maxZ) { return this.river.within(minX, minZ, maxX, maxZ); }

    public static Optional<RiverWorldFeature> realize(@Nullable BlockPos center, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth,
                                                      RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext) {
        BlockPos.MutableBlockPos source = BlockPos.ZERO.mutable(), drain = BlockPos.ZERO.mutable();
        for (int i = 0; i < 32; i++) {
            source.set(randomSource.nextIntBetweenInclusive(minX, maxX), 35, randomSource.nextIntBetweenInclusive(minZ, maxZ));
            drain.set(randomSource.nextIntBetweenInclusive(minX, maxX), 5, randomSource.nextIntBetweenInclusive(minZ, maxZ));
            double distance = Mth.length(source.getX() - drain.getX(), source.getZ() - drain.getZ());
            if (distance >= 50) return Optional.of(realize(source, drain, level, randomSource, context, worldContext));
        }
        return Optional.empty();
    }
    public static RiverWorldFeature realize(BlockPos source, BlockPos drain, LevelAccessor level,
                                            RandomSource randomSource, UncachedNoiseContext context,
                                            WorldFeatureContext worldContext) {
        Clinker.LOGGER.info("creating river from {} {} {} to {} {} {}", source.getX(), source.getY(), source.getZ(), drain.getX(), drain.getY(), drain.getZ());

        int centerX = (source.getX() + drain.getX()) / 2, centerZ = (source.getZ() + drain.getZ()) / 2;
        List<RiverPath.Node> path = new ArrayList<>();
        path.add(new RiverPath.Node(source.getX(), source.getY(), source.getZ(), 8, 2));
        path.add(new RiverPath.Node(drain.getX(), drain.getY(), drain.getZ(), 20, 8));
        path = RiverPath.resamplePath(path, 30);

        double pX = source.getZ() - drain.getZ(), pZ = drain.getX() - source.getX();
        double length = Mth.length(pX, pZ);
        pX /= length; pZ /= length;
        for (int i = 1; i < path.size() - 1; i++) {
            double offsetAmount = randomSource.nextGaussian() * 20;
            RiverPath.Node node = path.get(i);
            node.y = Mth.lerpDiscrete(randomSource.nextFloat() * randomSource.nextFloat(), path.get(i - 1).y, path.get(i + 1).y);
            node.x += (int) (pX * offsetAmount);
            node.z += (int) (pZ * offsetAmount);
        }

        // enforce a minimum cascade height!
        for (int i = 1; i < path.size(); i++) {
            RiverPath.Node node = path.get(i);
            RiverPath.Node previousNode = path.get(i - 1);
            if (Math.abs(node.y - previousNode.y) < 5) node.y = previousNode.y;
        }

        River river = new River(MAXIMUM_SHORELINE_SIZE, RiverPath.resamplePath(path, 15));
        PacketDistributor.sendToAllPlayers(
                new ClientboundRiverDebugPacket(river.nodes.stream().map(node -> new RiverDebugRenderer.RiverDebugPoint(node.x(), node.y(), node.z(), node.radius(), node.depth())).toList())
        );
        return new RiverWorldFeature(centerX, centerZ, river, randomSource.nextDouble());
    }

    @Override
    public void modifyCaveDensity(int minX, int minY, int minZ, int maxCaveHeight, NoiseFieldCache cache, NoiseField field, NoiseField maskField, WorldFeatureContext worldContext) {
        River.Sample sample = new River.Sample();

        NoiseField trueRiverDistanceField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] trueRiverDistanceArray = trueRiverDistanceField.array();
        NoiseField miteredDistanceToRiverField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] mitredDistanceToRiverArray = miteredDistanceToRiverField.array();
        NoiseField distanceAlongRiverField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] distanceAlongRiverArray = distanceAlongRiverField.array();
        NoiseField riverRadiusField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] riverRadiusArray = riverRadiusField.array();
        NoiseField riverDepthField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] riverDepthArray = riverDepthField.array();

        NoiseField riverNoiseField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] riverNoiseArray = riverNoiseField.array();

        NoiseField riverCeilingHeightField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] riverCeilingHeightArray = riverCeilingHeightField.array();
        NoiseField riverWaterHeightField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, 0);
        double[] riverWaterHeightArray = riverWaterHeightField.array();

        NoiseField sampleOffsetField = cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[5]);
        NoiseField shorelineRadiusField = cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[5]);

        cache.noiseHolder.registerNoise("riverbed");
        miteredDistanceToRiverField.byBlock((index, x, y, z) -> {
            double offset = sampleOffsetField.retrieve(x, y, z) * 8;
            river.sample(sample, x + cache.minX + offset, z + cache.minZ + offset);

            trueRiverDistanceArray[index] = sample.trueDistance;
            mitredDistanceToRiverArray[index] = sample.miteredDistanceFromRiver;
            distanceAlongRiverArray[index] = sample.distanceAlongRiver;
            riverCeilingHeightArray[index] = sample.ceilingHeight;
            riverWaterHeightArray[index] = sample.riverHeight;
            riverRadiusArray[index] = sample.radius;
            riverDepthArray[index] = sample.depth;

            double noiseFrequency = 0.05;
            riverNoiseArray[index] = cache.noiseHolder.sample("riverbed",
                    sample.distanceAlongRiver * noiseFrequency * 0.1,
                    sample.miteredDistanceFromRiver * noiseFrequency + this.randomOffset * 1000.0
            );
        });

        double[] array = field.array();
        field.byBlock(river.minY - 5 - cache.minY, river.maxY + 5 - cache.minY, (index, x, y, z) -> {
            double dist = array[index];
            double trueDistanceToRiver = trueRiverDistanceField.retrieve(x, y, z);
            double miteredDistanceToRiver = miteredDistanceToRiverField.retrieve(x, y, z);
            double distanceAlongRiver = distanceAlongRiverField.retrieve(x, y, z);
            double riverNoise = riverNoiseField.retrieve(x, y, z);
            double riverRadius = riverRadiusField.retrieve(x, y, z);
            double riverDepth = riverDepthField.retrieve(x, y, z);
            double riverCeilingHeight = riverCeilingHeightField.retrieve(x, y, z);
            double riverWaterHeight = riverWaterHeightField.retrieve(x, y, z);
            double riverFloorY = riverWaterHeight - riverDepth;

            double gY = y + cache.minY;
            double yDist;
            if (gY > riverCeilingHeight) yDist = gY - riverCeilingHeight;
            else if (gY < riverWaterHeight) yDist = (gY - riverWaterHeight) * (riverRadius / riverDepth);
            else yDist = 0;

            double shorelineRadius = shorelineRadiusField.retrieve(x, y, z);
            shorelineRadius = Mth.clampedMap(shorelineRadius, -0.5, 1, 0, Math.min(MAXIMUM_SHORELINE_SIZE - 8, riverRadius * 1.8));
            shorelineRadius = Mth.clampedMap(gY - riverWaterHeight, 0, 2, riverRadius, riverRadius + shorelineRadius);
            shorelineRadius *= Mth.clampedMap(gY - riverWaterHeight, 0, -5, 1, 0.8);
            double xzDist = trueDistanceToRiver * (riverRadius / shorelineRadius);

            double riverSolid = Math.max((riverFloorY - 4) - gY, gY - riverWaterHeight);
            riverSolid = Math.max(riverSolid, trueDistanceToRiver - shorelineRadius);
            dist = MathUtils.smoothMinExpo(riverSolid, dist, 3.0);

            double riverCarve = riverRadius - Mth.length(xzDist, yDist);
            double riverRocks = Math.sqrt(Math.abs(riverNoise)) * Math.min(riverDepth - 1, 4);
            riverCarve = Math.min(riverCarve, gY - (riverFloorY + riverRocks));

            dist = -MathUtils.smoothMinExpo(-riverCarve, -dist, 3.0);
            array[index] = dist;
        });
    }

    @Override
    public void prefillFluidNoiseFields(int chunkX, int chunkZ, PaddedNoiseFieldCache cache, WorldFeatureContext worldContext) {}
    @Override
    public FluidLevel modifyFluidLevel(int x, int y, int z, int minX, int minY, int minZ, FluidLevel currentFluidLevel, NoiseContext context, NoiseField heightmap) {
        if (y > river.maxY + 8 || y < river.minY - 8) return currentFluidLevel;

        River.Sample sample = river.sample(new River.Sample(), x, z);
        double distanceToRiver = sample.trueDistance;
        double riverRadius = sample.radius;
        if (distanceToRiver > riverRadius + 4) return currentFluidLevel;

        double riverCeilingHeight = sample.ceilingHeight, riverWaterHeight = sample.riverHeight;
        double yDist;
        if (y > riverCeilingHeight) yDist = y - riverCeilingHeight;
        else if (y < riverWaterHeight) yDist = y - riverWaterHeight;
        else yDist = 0;

        if (yDist > riverRadius + 24) return currentFluidLevel;
        if (yDist < sample.depth - 24) return currentFluidLevel;
        return new FluidLevel((int) Math.round(riverWaterHeight), Blocks.WATER.defaultBlockState());
    }

    @Override
    public void modifyWaterfallPresence(int minX, int minY, int minZ, PaddedNoiseFieldCache cache, NoiseField field, WorldFeatureContext worldContext) {
        River.Sample sample = new River.Sample();

        NoiseField riverDistanceField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, cache.paddingSize + 1);
        double[] riverDistanceArray = riverDistanceField.array();
        NoiseField riverRadiusField = NoiseFieldTypes.COARSE_2D.create(cache.chunkHeight, cache.paddingSize + 1);
        double[] riverRadiusArray = riverRadiusField.array();
        riverDistanceField.byBlock((index, x, y, z) -> {
            river.sample(sample, x + cache.minX, z + cache.minZ);
            riverDistanceArray[index] = sample.trueDistance;
            riverRadiusArray[index] = sample.radius;
        });

        double[] array = field.array();
        field.byBlock((index, x, y, z) -> {
            double riverDistance = riverDistanceField.retrieve(x, y, z);
            double riverRadius = riverRadiusField.retrieve(x, y, z);
            double riverWaterfallPresence = Mth.clampedMap(riverDistance, riverRadius * 0.5, riverRadius, 2, 0);
            array[index] = Math.max(array[index], riverWaterfallPresence);
        });
    }
}
