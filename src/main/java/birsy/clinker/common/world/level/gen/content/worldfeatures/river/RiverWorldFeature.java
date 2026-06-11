package birsy.clinker.common.world.level.gen.content.worldfeatures.river;

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

public class RiverWorldFeature extends WorldFeature implements ModifiesCaveDensity, ModifiesFluids {
    static final int MAXIMUM_SHORELINE_SIZE = 24;
    final int centerX, centerZ;
    final River river;

    private RiverWorldFeature(int centerX, int centerZ, River river) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.river = river;
    }
    @Override public int getCenterX() { return this.centerX; }
    @Override public int getCenterZ() { return this.centerZ; }
    @Override public boolean within(int minX, int minZ, int maxX, int maxZ) { return this.river.within(minX, minZ, maxX, maxZ); }

    public static Optional<RiverWorldFeature> realize(@Nullable BlockPos center, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth,
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
    public static RiverWorldFeature realize(BlockPos source, BlockPos drain, LevelAccessor level,
                                            RandomSource randomSource, UncachedNoiseContext context,
                                            WorldFeatureContext worldContext) {
        Clinker.LOGGER.info("creating river from {} {} {} to {} {} {}", source.getX(), source.getY(), source.getZ(), drain.getX(), drain.getY(), drain.getZ());

        int centerX = (source.getX() + drain.getX()) / 2, centerZ = (source.getZ() + drain.getZ()) / 2;
        List<RiverPath.Node> path = new ArrayList<>();
        path.add(new RiverPath.Node(source.getX(), source.getY(), source.getZ(), 5, 1));
        path.add(new RiverPath.Node(drain.getX(), drain.getY(), drain.getZ(), 20, 2));
        path = RiverPath.resamplePath(path, 30);

        double pX = source.getZ() - drain.getZ(), pZ = drain.getX() - source.getX();
        double length = Mth.length(pX, pZ);
        pX /= length; pZ /= length;
        for (int i = 1; i < path.size() - 1; i++) {
            double offsetAmount = randomSource.nextGaussian() * 20;
            RiverPath.Node node = path.get(i);
            node.y = Mth.lerpDiscrete(randomSource.nextFloat(), path.get(i - 1).y, path.get(i + 1).y);
            node.x += (int) (pX * offsetAmount);
            node.z += (int) (pZ * offsetAmount);
        }

        River river = new River(MAXIMUM_SHORELINE_SIZE, RiverPath.resamplePath(path, 15));
        PacketDistributor.sendToAllPlayers(
                new ClientboundRiverDebugPacket(river.nodes.stream().map(node -> new RiverDebugRenderer.RiverDebugPoint(node.x(), node.y(), node.z(), node.radius(), node.depth())).toList())
        );
        return new RiverWorldFeature(centerX, centerZ, river);
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

        River.Sample sample = river.sample(new River.Sample(), x, z);
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
}
