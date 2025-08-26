package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.surfaceshaper.SurfaceShapers;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OthershoreChunkGenerator extends ChunkGenerator {
    public static final MapCodec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(OthershoreChunkGenerator::getBiomeSource))
                      .apply(obj, obj.stable(OthershoreChunkGenerator::new))
    );
    private final Map<ResourceKey<Biome>, NoiseComputer> surfaceBiomeContributionComputers;
    private final NoiseComputer undergroundContributionComputer;

    public OthershoreChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);

        this.surfaceBiomeContributionComputers = HashMap.newHashMap(this.biomeSource.possibleBiomes().size());
        for (Holder<Biome> possibleBiome : this.biomeSource.possibleBiomes()) {
            NoiseComputer biomeComputer = new NoiseComputer(possibleBiome.toString(), CacheType.INTERPOLATED_2D_VERY_COARSE, (x, y, z, context) -> {
                if (this.getBiomeSource() instanceof OthershoreBiomeSource othershoreBiomeSource)
                    return othershoreBiomeSource.getNoiseBiome(x, context.chunk().getMaxBuildHeight() - 1, z, context.noiseComputerExecutor()) == possibleBiome ? 1 : 0;
                return 0;
            });
            this.surfaceBiomeContributionComputers.put(possibleBiome.getKey(), biomeComputer);
        }

        this.undergroundContributionComputer = new NoiseComputer("underground_contribution", CacheType.INTERPOLATED_VERY_COARSE, (x, y, z, context) -> {
            if (this.getBiomeSource() instanceof OthershoreBiomeSource othershoreBiomeSource)
                return othershoreBiomeSource.getNoiseBiome(x, context.chunk().getMaxBuildHeight() - 1, z, context.noiseComputerExecutor()).is(ClinkerBiomes.UNDERGROUND) ? 1 : 0;
            return 0;
        });
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
    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk) {
        if (this.biomeSource instanceof OthershoreBiomeSource othershoreBiomeSource) {
            return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("clinker_wgen_fill_biomes",
                    () -> doBiomeFillTask(othershoreBiomeSource, blender, randomState, structureManager, chunk)), Util.backgroundExecutor()
            );
        }
        return super.createBiomes(randomState, blender, structureManager, chunk);
    }

    private ChunkAccess doBiomeFillTask(OthershoreBiomeSource othershoreBiomeSource, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        ChunkPos chunkpos = chunk.getPos();
        LevelHeightAccessor levelheightaccessor = chunk.getHeightAccessorForGeneration();
        CachedNoiseComputerExecutor noiseExecutor = new CachedNoiseComputerExecutor(
                chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(),
                ((NoiseHolderHolder)(Object)randomState).clinker$noiseHolder(), chunk
        );

        for (int section = levelheightaccessor.getMinSection(); section < levelheightaccessor.getMaxSection(); section++) {
            LevelChunkSection levelchunksection = chunk.getSection(chunk.getSectionIndexFromSectionY(section));
            fillSectionBiomes(chunkpos.getMinBlockX(), section * SectionPos.SECTION_SIZE, chunkpos.getMinBlockZ(), othershoreBiomeSource, levelchunksection, noiseExecutor);
        }

        return chunk;
    }

    private void fillSectionBiomes(int minX, int minY, int minZ, OthershoreBiomeSource othershoreBiomeSource, LevelChunkSection levelchunksection, NoiseComputerExecutor noiseExecutor) {
        PalettedContainer<Holder<Biome>> palettedcontainer = levelchunksection.getBiomes().recreate();
        for (int qX = 0; qX < SectionPos.SECTION_SIZE / QuartPos.SIZE; qX++) {
            for (int qY = 0; qY < SectionPos.SECTION_SIZE / QuartPos.SIZE; qY++) {
                for (int qZ = 0; qZ < SectionPos.SECTION_SIZE / QuartPos.SIZE; qZ++) {
                    int x = qX * QuartPos.SIZE + minX,
                        y = qY * QuartPos.SIZE + minY,
                        z = qZ * QuartPos.SIZE + minZ;
                    Holder<Biome> biome = othershoreBiomeSource.getNoiseBiome(x, y, z, noiseExecutor);
                    palettedcontainer.getAndSetUnchecked(qX, qY, qZ, biome);
                }
            }
        }
        levelchunksection.biomes = palettedcontainer;
    }

        @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("clinker_wgen_fill_noise",
                () -> this.doNoiseFillTask(blender, randomState, structureManager, chunk)), Util.backgroundExecutor()
        );
    }


    public ChunkAccess doNoiseFillTask(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        CachedNoiseComputerExecutor noiseExecutor = new CachedNoiseComputerExecutor(
                chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(),
                ((NoiseHolderHolder)(Object)randomState).clinker$noiseHolder(), chunk
        );

        NoiseComputer caveComputer = new NoiseComputer("caves", CacheType.INTERPOLATED_FINE, (x, y, z, context) -> {
            NoiseComputerExecutor cache = context.noiseComputerExecutor();
            NoiseHolder noise = context.noiseHolder();
            noise.registerNoise("cave_a");
            noise.registerNoise("cave_b");
            double frequency = 1.0 / 48.0;
            double caveNoiseA = noise.sample("cave_a", x * frequency, y * frequency, z * frequency);
            double caveNoiseB = noise.sample("cave_b", x * frequency, y * frequency, z * frequency);

            double val = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
            val -= 10;
            return -val;
            //double undergroundContribution = cache.compute(x, y, z, undergroundContributionComputer);
            //return Mth.lerp(undergroundContribution, -20, -val);
        });

        NoiseComputer surfaceComputer = new NoiseComputer("surface_density", CacheType.INTERPOLATED_COARSE, (x, y, z, context) -> {
            NoiseComputerExecutor cache = context.noiseComputerExecutor();

            double surfaceDensity = 0;
            for (Holder<Biome> biome : this.biomeSource.possibleBiomes()) {
                double contribution = cache.compute(x, y, z, surfaceBiomeContributionComputers.get(biome.getKey()));
                if (contribution > 0) surfaceDensity += SurfaceShapers.retrieve(biome.getKey()).surfaceDensity(x, y, z, context);
            }
            return surfaceDensity;
        });

        NoiseComputer finalDensityComputer = new NoiseComputer("final_density", CacheType.FINAL_DENSITY, (x, y, z, context) -> {
            NoiseComputerExecutor cache = context.noiseComputerExecutor();
            double surfaceNoise = cache.compute(x, y, z, surfaceComputer);
            double caveNoise = cache.compute(x, y, z, caveComputer);
            double density = Math.max(surfaceNoise, caveNoise);

            List<WorldFeature> worldFeatures = ((MetaChunkMapHolder)(Object)randomState).clinker$metaChunkMap()
                    .getWorldFeatures(chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());
            for (WorldFeature worldFeature : worldFeatures) {
                //density = worldFeature.modifyTerrain(x, y, z, density);
            }
            return density;
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

                    double value = noiseExecutor.compute(pos.getX(), pos.getY(), pos.getZ(), finalDensityComputer);
                    BlockState state = value < 0 ? ClinkerBlocks.BRIMSTONE.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
                    chunk.setBlockState(pos, state, false);

                    heightmapOceanFloor.update(xi, yi, zi, state);
                    heightmapWorldSurface.update(xi, yi, zi, state);
                }
            }
        }

        return chunk;
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
