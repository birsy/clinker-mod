package birsy.clinker.common.world.level.gen.content.worldfeatures;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesSurfaceDecoration;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

import javax.annotation.Nullable;
import java.util.Optional;

public class OreVeinWorldFeature extends WorldFeature implements ModifiesSurfaceDecoration {
    final BlockState oreState, coreState;
    final int centerX, centerY, centerZ;
    final int horizontalRadius, verticalRadius;

    public OreVeinWorldFeature(BlockState oreState, BlockState coreState, int centerX, int centerY, int centerZ, int horizontalRadius, int verticalRadius) {
        this.oreState = oreState;
        this.coreState = coreState;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
    }

    @Override public int getCenterX() { return centerX; }

    @Override public int getCenterZ() { return centerZ; }

    @Override
    public boolean within(int minX, int minZ, int maxX, int maxZ) {
        int checkRadius = Mth.ceil(this.horizontalRadius) + 16;
        if (centerX < minX - checkRadius || centerX > maxX + checkRadius || centerZ < minZ - checkRadius || centerZ > maxZ + checkRadius)
            return false;
        return true;
    }

    public record Configuration(BlockState ore, BlockState core, IntProvider height, IntProvider horizontalRadius, IntProvider verticalRadius) {}
    public static WorldFeatureType.WorldFeatureFactory<OreVeinWorldFeature> fromConfig(Configuration configuration) {
        return (center, level, minX, minZ, maxX, maxZ, metaChunkDepth, randomSource, context, worldContext) ->
                realize(configuration, center, level, minX, minZ, maxX, maxZ, metaChunkDepth, randomSource, context, worldContext);
    }
    protected static Optional<OreVeinWorldFeature> realize(Configuration config,
                                                        @Nullable BlockPos center,
                                                        LevelAccessor level,
                                                        int minX, int minZ, int maxX, int maxZ, int metaChunkDepth,
                                                        RandomSource randomSource,
                                                        UncachedNoiseContext context,
                                                        WorldFeatureContext worldContext) {
        int centerX, centerY, centerZ;
        if (center != null) {
            centerX = center.getX();
            centerY = center.getY();
            centerZ = center.getZ();
        } else {
            centerX = randomSource.nextIntBetweenInclusive(minX, maxX);
            centerZ = randomSource.nextIntBetweenInclusive(minZ, maxZ);
            centerY = config.height.sample(randomSource);
        }
        int hRadius = config.horizontalRadius.sample(randomSource),
            vRadius = config.verticalRadius.sample(randomSource);
        return Optional.of(new OreVeinWorldFeature(config.ore, config.core, centerX, centerY, centerZ, hRadius, vRadius));
    }

    @Override
    public void modifySurfaceDecoration(NoiseFieldCache cache, WorldGenLevel level, ChunkAccess chunk, RandomState randomState, WorldFeatureContext worldContext) {
        int minY = centerY - verticalRadius, maxY = centerY + verticalRadius;

        ChunkPos chunkPos = chunk.getPos();
        int maxWorldHeight = level.getMinBuildHeight();
        for (int x = 0; x < 16; x++) {
            int wX = x + chunkPos.getMinBlockX();
            for (int z = 0; z < 16; z++) {
                int wZ = z + chunkPos.getMinBlockZ();
                int height = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, wX, wZ);
                if (height > maxWorldHeight) maxWorldHeight = height;
            }
        }
        if (maxWorldHeight < minY) return;
        int maxHeight = Math.min(maxWorldHeight, maxY + 16),
            minHeight = Math.max(level.getMinBuildHeight(), minY - 16);

        NoiseField oreVeinANoise = cache.fillNoiseField(minHeight, maxHeight, ClinkerNoiseComputers.ORE_VEIN_A),
                   oreVeinBNoise = cache.fillNoiseField(minHeight, maxHeight, ClinkerNoiseComputers.ORE_VEIN_B);

        RandomSource ditherRandom = randomState.random.at(centerX, centerY, centerZ);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            int wX = x + chunkPos.getMinBlockX();

            for (int z = 0; z < 16; z++) {
                int wZ = z + chunkPos.getMinBlockZ();

                double horizontalDistance = Mth.length(wX - centerX, wZ - centerZ);
                double horizontalVeinRadiusTaper = Mth.clampedMap(horizontalDistance, horizontalRadius * 0.7, horizontalRadius, 1, 0);
                if (horizontalVeinRadiusTaper <= 0) continue;

                for (int wY = maxHeight; wY > minHeight; wY--) {
                    if (ditherRandom.nextInt(2) == 0) continue;

                    double veinRadius = 6.5;
                    // horizontal tapering
                    veinRadius *= horizontalVeinRadiusTaper;
                    // vertical tapering
                    veinRadius *= Mth.clampedMap(Mth.abs(wY - centerY), verticalRadius * 0.25, verticalRadius, 1, 0);

                    double noiseA = oreVeinANoise.retrieve(wX - cache.minX, wY - cache.minY, wZ - cache.minZ),
                           noiseB = oreVeinBNoise.retrieve(wX - cache.minX, wY - cache.minY, wZ - cache.minZ);
                    double veinNoise = Math.sqrt(noiseA * noiseA + noiseB * noiseB) / ClinkerNoiseComputers.ORE_VEIN_FREQUENCY;
                    if (veinNoise == 0) continue; // hack: skip unfilled areas?
                    veinNoise += ditherRandom.triangle(0, 1.2);
                    if (veinNoise > veinRadius) continue;

                    pos.set(wX, wY, wZ);
                    if (!chunk.getBlockState(pos).is(ClinkerBlocks.BRIMSTONE.get())) continue;

                    BlockState state = veinNoise <= 0.75 && ditherRandom.nextInt(3) == 0 ? coreState : oreState;
                    chunk.setBlockState(pos, state, false);
                }
            }
        }
    }
}
