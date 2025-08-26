package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OthershoreChunkGenerator extends ChunkGenerator {
    public static final MapCodec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(OthershoreChunkGenerator::getBiomeSource))
                      .apply(obj, obj.stable(OthershoreChunkGenerator::new))
    );

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
                ((NoiseHolderHolder)(Object)randomState).clinker$noiseHolder());

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

    protected static final NoiseComputer SURFACE_HEIGHT_COMPUTER = new NoiseComputer("surface_height", CacheType.INTERPOLATED_2D_VERY_COARSE, (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("base_plateaus", 2, 4.0, 0.7, 0.0);
        noise.registerNoise("base_upper_shelf");
        noise.registerNoise("base_seas");
        noise.registerNoise("base_erosion");

        double scale = 1.0;
        double frequency = (1 / 300.0) / scale;
        double val;
        double erosion = noise.sample("base_erosion", x * frequency, z * frequency);
        erosion = Mth.clampedMap(erosion, -1, 1, 0.5, 1);

        double plateaus = noise.sample("base_plateaus", x * frequency * 0.25, z * frequency * 0.25) + 0.2;
        plateaus = plateaus * (1 / erosion);
        plateaus = MathUtils.smoothMinExpo(plateaus, 1, 0.2);
        plateaus = -MathUtils.smoothMinExpo(-plateaus, 1, 0.5);
        val = plateaus;

        double upperShelf = noise.sample("base_upper_shelf", x * frequency * 0.8, z * frequency * 0.8) - 0.5;
        upperShelf = upperShelf * (1 / (erosion * 0.25));
        upperShelf = Math.clamp(upperShelf, 0, 1);
        upperShelf = upperShelf * 0.5 + 0.5;
        val = Mth.clampedLerp(Mth.clampedMap(val, -1, 1, -1, -0.2), 1, upperShelf + Math.min(plateaus, 0) * 5);
        val = Mth.clampedMap(val, -1, 1, -0.8, 1);

        double seas = noise.sample("base_seas", x * frequency * 0.2, z * frequency * 0.2) - 0.5;
        seas = seas * (1 / (erosion * 0.2));
        seas = Math.clamp(seas, -1, 1);
        val = Mth.clampedLerp(val, -1, (seas * 0.5 + 0.5));

        return Mth.clampedMap(val, -1, 1, 50 * scale, 300 * scale);
    });

    public ChunkAccess doNoiseFillTask(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        CachedNoiseComputerExecutor noiseExecutor = new CachedNoiseComputerExecutor(
                chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(),
                ((NoiseHolderHolder)(Object)randomState).clinker$noiseHolder());

        NoiseComputer caveComputer = new NoiseComputer("caves", CacheType.INTERPOLATED_COARSE, (x, y, z, context) -> {
//            NoiseHolder noise = context.noiseHolder();
//            noise.registerNoise("cave_a");
//            noise.registerNoise("cave_b");
//            double frequency = 1.0 / 48.0;
//            double caveNoiseA = noise.sample("cave_a", x * frequency, y * frequency, z * frequency);
//            double caveNoiseB = noise.sample("cave_b", x * frequency, y * frequency, z * frequency);
//
//            double val = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
//            val -= 10;
//            return -val;
            NoiseHolder noise = context.noiseHolder();
            noise.registerNoise("terrain_3d");
            double hFrequency = 1.0 / 64.0;
            double vFrequency = 1.0 / 64.0;
            double value = noise.sample("terrain_3d", x * hFrequency, y * vFrequency, z * hFrequency);
            value += noise.sample("terrain_3d", x * hFrequency * 2, y * vFrequency * 2, z * hFrequency * 2) * 0.5;
            return value;
        });

        NoiseComputer finalDensityComputer = new NoiseComputer("final_density", CacheType.FINAL_DENSITY, (x, y, z, context) -> {
            NoiseComputerExecutor cache = context.noiseComputerExecutor();
            double surfaceHeight = cache.compute(x, y, z, SURFACE_HEIGHT_COMPUTER);
            double roundedVal = Math.round(surfaceHeight / 5) * 5.0;
            //roundedVal = Math.max(roundedVal, Math.round(surfaceHeight / 8) * 8.0);
            //roundedVal = Math.max(roundedVal, Math.round(surfaceHeight / 16) * 16);
            //surfaceHeight = roundedVal;
            // surfaceHeight = Math.floor(surfaceHeight / 16) * 16.0;
            double worldNoise = cache.compute(x, y, z, caveComputer);
            double value = y - (surfaceHeight);

            //value = Math.max(value, caves);

            List<WorldFeature> worldFeatures = ((MetaChunkMapHolder)(Object)randomState).clinker$metaChunkMap()
                    .getWorldFeatures(chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());
            for (WorldFeature worldFeature : worldFeatures) {
                //value = worldFeature.modifyTerrain(x, y, z, value);
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
