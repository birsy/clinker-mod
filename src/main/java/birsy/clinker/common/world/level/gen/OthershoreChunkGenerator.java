package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.fluid.AxialBlurBorderFluidMap;
import birsy.clinker.common.world.level.gen.fluid.FluidMap;
import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.surfaceshaper.SurfaceShapers;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
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
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.material.FluidState;

import java.util.Collection;
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
    private final SurfaceBuilder surfaceBuilder;

    public OthershoreChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        this.surfaceBuilder = new SurfaceBuilder(8, OthershoreBiomeSource.SEA_HEIGHT, ClinkerBlocks.BRIMSTONE.get().defaultBlockState());
        this.surfaceBiomeContributionComputers = HashMap.newHashMap(this.biomeSource.possibleBiomes().size());

        // compute a blur kernel
        int kernelSize = 5;
        double kernelCenter = kernelSize * 0.5;
        double[] kernel = new double[kernelSize * kernelSize];
        for (int kX = 0; kX < kernelSize; kX++) {
            double kernelX = kX + 0.5;
            for (int kZ = 0; kZ < kernelSize; kZ++) {
                double kernelZ = kZ + 0.5;
                double distanceToCenter = Math.sqrt((kernelX - kernelCenter) * (kernelX - kernelCenter) + (kernelZ - kernelCenter) * (kernelZ - kernelCenter));
                double gamma = kernelCenter / 3.0;
                kernel[kX + kZ * kernelSize] = (1.0 / Math.sqrt(2 * Math.PI * gamma)) * Math.exp(-((distanceToCenter * distanceToCenter) / (2 * gamma * gamma)));
            }
        }

        for (Holder<Biome> possibleBiome : this.biomeSource.possibleBiomes()) {
            NoiseComputer biomeComputer = new NoiseComputer(possibleBiome.toString(), CacheType.INTERPOLATED_2D_COARSE, (x, y, z, context) -> {
                if (this.getBiomeSource() instanceof OthershoreBiomeSource othershoreBiomeSource) {
                    double totalContribution = 0;
                    for (int i = 0; i < kernel.length; i++) {
                        double weight = kernel[i];
                        int offsetX = (Math.floorMod(i, kernelSize) - (int) Math.floor(kernelCenter)) * 4,
                            offsetZ = (Math.floorDiv(i, kernelSize) - (int) Math.floor(kernelCenter)) * 4;
                        totalContribution += othershoreBiomeSource
                                .getNoiseBiome(x + offsetX,  440, z + offsetZ, context.noiseComputerExecutor()) == possibleBiome ? weight : 0;
                    }

                    return totalContribution;
                }

                return 0;
            });
            this.surfaceBiomeContributionComputers.put(possibleBiome.getKey(), biomeComputer);
        }

        this.undergroundContributionComputer = new NoiseComputer("underground_contribution", CacheType.INTERPOLATED_VERY_COARSE, (x, y, z, context) -> {
            if (this.getBiomeSource() instanceof OthershoreBiomeSource othershoreBiomeSource) {
                Holder<Biome> currentBiome = othershoreBiomeSource.getNoiseBiome(x, y, z, context.noiseComputerExecutor());
                return currentBiome.is(ClinkerBiomes.UNDERGROUND) || currentBiome.is(ClinkerBiomes.AQUIFER) ? 1 : 0;
            }
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
        NoiseHolder noiseHolder = ((NoiseHolderHolder)(Object)randomState).clinker$noiseHolder();
        CachedNoiseComputerExecutor noiseExecutor = new CachedNoiseComputerExecutor(chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(), noiseHolder);
        NoiseComputerContext context = new NoiseComputerContext(noiseExecutor, noiseHolder);

        Collection<WorldFeature> worldFeatures = ((MetaChunkMapHolder)(Object) randomState).clinker$metaChunkMap()
                .getWorldFeatures(chunk.getLevel(), chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());

        for (int section = levelheightaccessor.getMinSection(); section < levelheightaccessor.getMaxSection(); section++) {
            LevelChunkSection levelchunksection = chunk.getSection(chunk.getSectionIndexFromSectionY(section));
            PalettedContainer<Holder<Biome>> palettedcontainer = levelchunksection.getBiomes().recreate();

            for (int qX = 0; qX < SectionPos.SECTION_SIZE / QuartPos.SIZE; qX++) {
                int x = qX * QuartPos.SIZE + chunkpos.getMinBlockX();
                for (int qY = 0; qY < SectionPos.SECTION_SIZE / QuartPos.SIZE; qY++) {
                    int y = qY * QuartPos.SIZE +section * SectionPos.SECTION_SIZE;
                    for (int qZ = 0; qZ < SectionPos.SECTION_SIZE / QuartPos.SIZE; qZ++) {
                        int z = qZ * QuartPos.SIZE + chunkpos.getMinBlockZ();

                        Holder<Biome> biome = othershoreBiomeSource.getNoiseBiome(x, y, z, noiseExecutor);
                        for (WorldFeature worldFeature : worldFeatures) {
                            worldFeature.modifyBiome(x, y, z, biome, context);
                        }
                        palettedcontainer.getAndSetUnchecked(qX, qY, qZ, biome);
                    }
                }
            }
            levelchunksection.biomes = palettedcontainer;
        }

        return chunk;
    }


        @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("clinker_wgen_fill_noise",
                () -> this.doNoiseFillTask(blender, randomState, structureManager, chunk)), Util.backgroundExecutor()
        );
    }


    public ChunkAccess doNoiseFillTask(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        NoiseHolder noiseHolder = ((NoiseHolderHolder)(Object)randomState).clinker$noiseHolder();
        CachedNoiseComputerExecutor noiseExecutor = new CachedNoiseComputerExecutor(
                chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(),
                noiseHolder
        );

        FluidMap fluidMap = new AxialBlurBorderFluidMap(randomState, chunk, new NoiseComputerContext(noiseExecutor, noiseHolder), OthershoreNoiseComputers.FLUID_FILLER);

        NoiseComputer surfaceComputer = new NoiseComputer("surface_density", CacheType.INTERPOLATED_COARSE, (x, y, z, context) -> {
            NoiseComputerExecutor cache = context.noiseComputerExecutor();

            double surfaceDensity = 0;
            double totalContribution = 0;
            for (Holder<Biome> biome : this.biomeSource.possibleBiomes()) {
                double contribution = cache.compute(x, y, z, surfaceBiomeContributionComputers.get(biome.getKey()));
                if (contribution > 0) {
                    surfaceDensity += SurfaceShapers.retrieve(biome.getKey()).surfaceDensity(x, y, z, contribution, context) * contribution;
                    totalContribution += contribution;
                }
            }

            return surfaceDensity / totalContribution;
        });

        NoiseComputer finalDensityComputer = new NoiseComputer("final_density", CacheType.FINAL_DENSITY, (x, y, z, context) -> {
            NoiseComputerExecutor cache = context.noiseComputerExecutor();
            double surfaceNoise = cache.compute(x, y, z, surfaceComputer);
            double density = surfaceNoise;

            double caveNoise = cache.compute(x, y, z, OthershoreNoiseComputers.CAVES);
            caveNoise = Mth.lerp(cache.compute(x, y, z, undergroundContributionComputer), -10, caveNoise);
            density = Math.max(density, caveNoise);

            Collection<WorldFeature> worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                    .getWorldFeatures(chunk.getLevel(), chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());
            for (WorldFeature worldFeature : worldFeatures) {
                density = worldFeature.modifyTerrain(x, y, z, density, context);
            }
            return density;
        });

        NoiseComputer waterfallPresenceComputer = new NoiseComputer("waterfall_presence", CacheType.INTERPOLATED_COARSE, (x, y, z, context) -> {
            double presence = -1;
            Collection<WorldFeature> worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                    .getWorldFeatures(chunk.getLevel(), chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());
            for (WorldFeature worldFeature : worldFeatures) {
                presence = worldFeature.modifyWaterfallPresence(x, y, z, presence, context);
            }
            return presence;
        });

        NoiseComputer flattenedFinalDensity = new NoiseComputer("final_density_flattened", CacheType.DIRECT, finalDensityComputer::compute);
        fluidMap.precomputeValues(flattenedFinalDensity, waterfallPresenceComputer);

        Heightmap heightmapOceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmapWorldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int yi = 0; yi < chunk.getHeight() - 1; yi++) {
            pos.setY(yi + chunk.getMinBuildHeight());
            for (int xi = 0; xi < 16; xi++) {
                pos.setX(xi + chunk.getPos().getMinBlockX());
                for (int zi = 0; zi < 16; zi++) {
                    pos.setZ(zi + chunk.getPos().getMinBlockZ());

                    double terrainDensity = noiseExecutor.compute(pos.getX(), pos.getY(), pos.getZ(), flattenedFinalDensity);
                    if (terrainDensity >= 0)
                        terrainDensity = MathUtils.smoothMinExpo(terrainDensity, fluidMap.getBorderDensity(xi, yi, zi), 3);

                    BlockState state = terrainDensity < 0 ?
                            ClinkerBlocks.BRIMSTONE.get().defaultBlockState() :
                            fluidMap.getFluidState(pos.getX(), pos.getY(), pos.getZ());
                    if (state != Blocks.AIR.defaultBlockState()) {
                        chunk.setBlockState(pos, state, false);

                        // update any placed fluid blocks in waterfalls, so they flow!
                        if (noiseExecutor.compute(pos.getX(), pos.getY(), pos.getZ(), waterfallPresenceComputer) > 0 &&
                                !state.getFluidState().isEmpty()) {
                            chunk.markPosForPostprocessing(pos);
                        }
                        heightmapOceanFloor.update(xi, pos.getY(), zi, state);
                        heightmapWorldSurface.update(xi, pos.getY(), zi, state);
                    }
                }
            }
        }

        return chunk;
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (level.getChunkSource() instanceof ServerChunkCache chunkCache) {
            NoiseHolder noiseHolder = ((NoiseHolderHolder)(Object)chunkCache.randomState()).clinker$noiseHolder();
            NoiseComputerExecutor executor = new CachedNoiseComputerExecutor(chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(), noiseHolder);
            NoiseComputerContext context = new NoiseComputerContext(executor, noiseHolder);

            surfaceBuilder.applySurfaceDecorators(level, chunk, context, chunkCache.randomState());
        }
        super.applyBiomeDecoration(level, chunk, structureManager);
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
