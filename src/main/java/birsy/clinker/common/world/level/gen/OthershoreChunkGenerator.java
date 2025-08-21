package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.CacheType;
import birsy.clinker.common.world.level.gen.noise.NoiseCache;
import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.WorldSeedHolder;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OthershoreChunkGenerator extends ChunkGenerator {
    public static final MapCodec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(OthershoreChunkGenerator::getBiomeSource))
                      .apply(obj, obj.stable(OthershoreChunkGenerator::new))
    );
    private static final FastNoiseLite noise = new FastNoiseLite();
    static {
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        noise.SetFractalOctaves(0);
        noise.SetFrequency(1);
    }

    public OthershoreChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {}

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return 64;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        long seed = ((WorldSeedHolder)(Object)randomState).clinker$getWorldSeed();
        noise.SetSeed((int) (seed % Integer.MAX_VALUE));
        NoiseCache noiseCache = new NoiseCache(chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(), seed);

        NoiseComputer surfaceHeightComputer = new NoiseComputer("surface_height", CacheType.INTERPOLATED_2D_VERY_COARSE, (x, y, z, nCache) -> {
            return 64 + Math.sin(x * 0.1) * 4 + Math.cos(z * 0.12) * 1.5;
        });

        NoiseComputer caveComputer = new NoiseComputer("caves", CacheType.INTERPOLATED_FINE, (x, y, z, nCache) -> {
            double frequency = 1.0 / 64.0;
            double caveNoiseA = noise.GetNoise(x * frequency, y * frequency, z * frequency);
            double caveNoiseB = noise.GetNoise(x * frequency, y * frequency + chunk.getHeight(), z * frequency);

            double val = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
            val -= 8;
            return -val;
        });

        NoiseComputer finalDensityComputer = new NoiseComputer("final_density", CacheType.FINAL_DENSITY, (x, y, z, nCache) -> {
            double surfaceHeight = nCache.compute(x, y, z, surfaceHeightComputer);
            double value = y - surfaceHeight;
            double caves = nCache.compute(x, y, z, caveComputer);
            value = Math.max(value, caves);

            List<WorldFeature> worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                    .getWorldFeatures(chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());
            for (WorldFeature worldFeature : worldFeatures) {
                value = worldFeature.modifyTerrain(x, y, z, value);
            }
            return value * 0.1;
        });

        Heightmap heightmapOceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmapWorldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int yi = 0; yi < chunk.getHeight() - 1; yi++) {
            pos.setY(yi + chunk.getMinBuildHeight());
            for (int xi = 0; xi < 16; xi++) {
                pos.setX(xi + chunk.getPos().getMinBlockX());
                for (int zi = 0; zi < 16; zi++) {
                    pos.setZ(zi + chunk.getPos().getMinBlockZ());

                    double value = noiseCache.compute(pos.getX(), pos.getY(), pos.getZ(), finalDensityComputer);
                    BlockState state = value < 0 ? ClinkerBlocks.BRIMSTONE.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
                    chunk.setBlockState(pos, state, false);

                    heightmapOceanFloor.update(xi, yi, zi, state);
                    heightmapWorldSurface.update(xi, yi, zi, state);
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return 64;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        BlockState[] column = new BlockState[height.getHeight()];
        for (int yi = 0; yi < height.getHeight(); yi++) {
            int y = yi + height.getMinBuildHeight();
            column[yi] = y < 64 ? ClinkerBlocks.BRIMSTONE.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return new NoiseColumn(
                height.getMinBuildHeight(),
                column
        );
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {}

    @Override
    public void applyCarvers(
            WorldGenRegion level,
            long seed,
            RandomState random,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunk,
            GenerationStep.Carving step
    ) {}

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {}

    @Override
    public int getMinY() {
        return -63;
    }

    @Override
    public int getGenDepth() {
        return 512;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }
}
