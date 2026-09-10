package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.content.synthesizers.OthershoreCaveSynthesizers;
import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.biome.BiomeList;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesSurfaceDecoration;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerCache;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationSystem;
import birsy.clinker.common.world.level.gen.system.sampling.field.*;
import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSet;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesBiome;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerWorldFeatureCapabilities;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.profiling.RunningAverageTracker;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class OthershoreChunkGenerator extends ChunkGenerator {
    public static final RunningAverageTracker TIME_TRACKER = new RunningAverageTracker();

    public static final MapCodec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(RegistryOps.retrieveGetter(Registries.BIOME),
                            OthershoreBiomeSource.CODEC.fieldOf("biome_source")
                                    .forGetter(OthershoreChunkGenerator::getBiomeSource))
                    .apply(obj, obj.stable(OthershoreChunkGenerator::new))
    );
    private static final ResourceLocation BEDROCK_RANDOM = Clinker.resource("bedrock");
    private static final BlockState BRIMSTONE = ClinkerBlocks.BRIMSTONE.get().defaultBlockState();

    final BiomeList biomeList;
    final SurfaceDecorationSystem surfaceDecorationSystem;
    final WorldFeatureContext worldContext;

    private static final Map<Holder<Biome>, Integer> biomeSeaHeight = new HashMap<>();

    public OthershoreChunkGenerator(HolderGetter<Biome> biomeGetter, OthershoreBiomeSource biomeSource) {
        super(biomeSource);
        this.biomeList = biomeSource.biomeList;
        this.surfaceDecorationSystem = new SurfaceDecorationSystem(ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), biomeGetter);
        this.worldContext = new WorldFeatureContext(biomeList);
        biomeSource.initFromChunkGenerator(this);
    }

    @Override
    public OthershoreBiomeSource getBiomeSource() {
        return (OthershoreBiomeSource) super.getBiomeSource();
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return 64;
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("clinker_wgen_fill_biomes",
                () -> doBiomeFillTask(this.getBiomeSource(), blender, randomState, structureManager, chunk)), Util.backgroundExecutor()
        );
    }

    private ChunkAccess doBiomeFillTask(OthershoreBiomeSource othershoreBiomeSource, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunkPos.getMinBlockZ();
        int chunkHeight = chunk.getHeight();
        LevelHeightAccessor levelheightaccessor = chunk.getHeightAccessorForGeneration();

        WorldFeatureSet worldFeatures =
                ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                        .getWorldFeatures(chunk.getLevel(), minX, minZ, this.worldContext);

        // prefill noise fields
        List<ModifiesBiome> biomeModifyingWorldFeatures = worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_BIOME.get());
        for (ModifiesBiome worldFeature : biomeModifyingWorldFeatures)
            worldFeature.prefillBiomeNoiseFields(chunkPos.x, chunkPos.z, worldContext);

        BiomeCache2d surfaceBiomes = this.getSurfaceBiomeCacheForChunk(minX, minZ);

        int sectionQuartSize = QuartPos.fromSection(1);
        for (int section = levelheightaccessor.getMinSection(); section < levelheightaccessor.getMaxSection(); section++) {
            LevelChunkSection levelchunksection = chunk.getSection(chunk.getSectionIndexFromSectionY(section));
            PalettedContainer<Holder<Biome>> palettedcontainer = levelchunksection.getBiomes().recreate();

            for (int qX = 0; qX < sectionQuartSize; qX++) {
                int globalBlockX = QuartPos.toBlock(qX) + minX;
                int globalQuartX = QuartPos.fromBlock(globalBlockX);
                for (int qY = 0; qY < sectionQuartSize; qY++) {
                    int globalBlockY = qY * QuartPos.SIZE + section * SectionPos.SECTION_SIZE;
                    int globalQuartY = QuartPos.fromBlock(globalBlockY);
                    for (int qZ = 0; qZ < sectionQuartSize; qZ++) {
                        int globalBlockZ = QuartPos.toBlock(qZ) + minZ;
                        int globalQuartZ = QuartPos.fromBlock(globalBlockZ);

                        Holder<Biome> biome = othershoreBiomeSource.getNoiseBiome(
                                globalQuartX, globalQuartY, globalQuartZ,
                                surfaceBiomes
                        );
                        for (ModifiesBiome worldFeature : biomeModifyingWorldFeatures)
                            worldFeature.modifyBiome(globalBlockX, globalBlockY, globalBlockZ, minX, minY, minZ, biome);
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

    private ChunkAccess doNoiseFillTask(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        long startTime = System.nanoTime();

        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunkPos.getMinBlockZ();
        int chunkHeight = chunk.getHeight();

        SynthesizerCache synthesizerCache = new SynthesizerCache(minX, minY, minZ, chunkHeight, randomState.getOrCreateRandomFactory(Clinker.resource("clinkernoisegen")));

        int seaLevel = 100;
        int amplitude = 24, range = amplitude + 10;
        Synthesizer ySynthesizer = Synthesizer.builder()
            .build(InterpolatingFieldResolution.VERY_COARSE,
                (ctx) -> {
                    return ctx.y() - seaLevel;
                }
            );

        // the ultimate goal of biome construction will be to create the master Surface Synthesizer...
        Synthesizer testSurfaceSynthesizer = Synthesizer.builder()
            .withDependencies(ySynthesizer)
            .withNoises(FNLNoiseProvider.create("base"))
            .withRange(seaLevel - range, seaLevel + range, 100.0)
            .build(InterpolatingFieldResolution.COARSE_Y,
               (ctx) -> {
                    double n = ctx.noise(0).sample(ctx.x() / 64.0, ctx.y() / 64.0, ctx.z() / 64.0) * amplitude;
                    return ctx.dependentValue(0) + n;
               }
            );
        // and combine that with the cave synthesizer to create the Final Density Synthesizer:tm:
        Synthesizer finalDensitySynthesizer = Synthesizer.builder()
            .withDependencies(testSurfaceSynthesizer, OthershoreCaveSynthesizers.CAVES)
            .withRange(Integer.MIN_VALUE, seaLevel + range, 100.0)
            .build(InterpolatingFieldResolution.COARSE_Y,
                (ctx) -> {
                    return -MathUtils.smoothMinExpo(-ctx.dependentValue(0), -ctx.dependentValue(1), 8.0);
                }
            );

        InterpolatingField finalDensityField = synthesizerCache
                .sampleThisChunk(finalDensitySynthesizer, 0, minY, chunkHeight);
        this.fillFromFields(finalDensityField, chunk);

        // terrible profiling
        TIME_TRACKER.recordTime(System.nanoTime() - startTime);
        if (randomState.random.at(minX, minY, minZ).nextInt(100) == 0) Clinker.LOGGER.info("avg. noise gen time: {} ms", TIME_TRACKER.getAverage() / 1_000_000.0);

        return chunk;
    }

    private void fillFromFields(InterpolatingField field, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunkPos.getMinBlockZ();
        int chunkHeight = chunk.getHeight();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        boolean[] filledWorldSurfaceHeight = new boolean[16 * 16],
                  filledOceanFloorHeight = new boolean[16 * 16];
        Arrays.fill(filledWorldSurfaceHeight, false);
        Arrays.fill(filledOceanFloorHeight, false);
        Heightmap worldSurfaceHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG),
                  oceanFloorHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);

        // create a placeholder noise field just for proper cell size, etc.
        InterpolatingFieldSampler sampler = InterpolatingFieldSampler.create(field, new InterpolatingField(0, 0, chunkHeight, 0));
        int y = chunk.getHeight() - 1;
        int globalY = y + minY;
        int sectionY = SectionPos.sectionRelative(globalY);
        int sectionIndex = chunk.getSectionIndex(globalY);
        LevelChunkSection section = chunk.getSection(sectionIndex);

        for (; y >= 0; y--) {
            pos.setY(globalY);

            // we actually iterate in reverse y order (for heightmap computation)
            // so we set the slice directly instead of advancing
            sampler.setSlice(y);

            // update section, if necessary
            int nextSectionIndex = chunk.getSectionIndex(globalY);
            if (nextSectionIndex != sectionIndex) {
                sectionIndex = nextSectionIndex;
                sectionY = SectionPos.sectionRelative(globalY);
                section = chunk.getSection(sectionIndex);
            }

            for (int z = 0; z < 16; z++) {
                int globalZ = z + minZ;
                pos.setZ(globalZ);

                for (int x = 0; x < 16; x++) {
                    int globalX = x + minX;
                    pos.setX(globalX);

                    double noise = sampler.sample();
                    if (noise <= 0) {
                        section.setBlockState(x, sectionY, z, BRIMSTONE, false);
                        // fill heightmaps
                        int heightmapIndex = x + z * 16;
                        if (!filledWorldSurfaceHeight[heightmapIndex]) {
                            worldSurfaceHeightmap.update(x, pos.getY(), z, BRIMSTONE);
                            filledWorldSurfaceHeight[heightmapIndex] = true;
                        }
                        if (!filledOceanFloorHeight[heightmapIndex]) {
                            oceanFloorHeightmap.update(x, pos.getY(), z, BRIMSTONE);
                            filledOceanFloorHeight[heightmapIndex] = true;
                        }
                    }
                    sampler.advanceX();
                }
                sampler.advanceZ();
            }
            globalY--;
            sectionY--;
        }
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        int minX = chunk.getPos().getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunk.getPos().getMinBlockZ();
        int chunkHeight = chunk.getHeight();

        WorldFeatureSet worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                .getWorldFeatures(chunk.getLevel(), minX, minZ, worldContext);
        BiomeCache2d surfaceBiomes = getSurfaceBiomeCacheForChunk(minX, minZ);

        surfaceDecorationSystem.decorate(level, chunk, randomState);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(minX, minY, minZ);
        PositionalRandomFactory randomFactory = randomState.getOrCreateRandomFactory(BEDROCK_RANDOM);

        for (ModifiesSurfaceDecoration modifiesSurfaceDecoration : worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_SURFACE_DECORATION.get())) {
            modifiesSurfaceDecoration.modifySurfaceDecoration(level, chunk, randomState, worldContext);
        }

        createBarrierrockLayer(chunk, randomFactory, pos, minX, minY, minZ);
        createBedrockLayer(chunk, randomFactory, pos, minX, minY, minZ);
    }

    private void createBedrockLayer(ChunkAccess chunk, PositionalRandomFactory random,
            BlockPos.MutableBlockPos pos, int minX, int minY, int minZ) {
        // bedrock
        // transition
        for (int y = 1; y < 4; y++) {
            pos.setY(minY + y);
            double bedrockFactor = 1.0 - (y / 4.0);
            for (int x = 0; x < 16; x++) {
                pos.setX(minX + x);
                for (int z = 0; z < 16; z++) {
                    pos.setZ(minZ + z);
                    double randomValue = random.at(pos.getX(), pos.getY(), pos.getZ()).nextDouble();
                    if (randomValue < bedrockFactor) {
                        chunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
                    }
                }
            }
        }
        // base
        pos.setY(minY);
        for (int x = 0; x < 16; x++) {
            pos.setX(minX + x);
            for (int z = 0; z < 16; z++) {
                pos.setZ(minZ + z);
                chunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
            }
        }
    }
    private void createBarrierrockLayer(ChunkAccess chunk, PositionalRandomFactory random, BlockPos.MutableBlockPos pos, int minX, int minY, int minZ) {
//        InterpolatingField field = cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[3]);
//        for (int x = 0; x < 16; x++) {
//            pos.setX(minX + x);
//            for (int z = 0; z < 16; z++) {
//                pos.setZ(minZ + z);
//                double noiseValue = Math.abs(field.retrieve(x, 0, z));
//                for (int y = -3; y <= 3; y++) {
//                    pos.setY(y);
//                    double mixFactor = 1.0 - (Math.abs(y) / 4.0);
//                    if (!chunk.getBlockState(pos).isSolid()) {
//                        mixFactor -= 0.5;
//                    }
//                    if (noiseValue < mixFactor) {
//                        chunk.setBlockState(pos, ClinkerBlocks.BARRIERROCK.get().defaultBlockState(), false);
//                    }
//                }
//            }
//        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        super.applyBiomeDecoration(level, chunk, structureManager);
    }

    private BiomeCache2d getSurfaceBiomeCacheForChunk(int minX, int minZ) {
        int sectionQuartSize = QuartPos.fromSection(1);
        int minQX = QuartPos.fromBlock(minX), minQZ = QuartPos.fromBlock(minZ);
        int padding = 0;
        return this.getBiomeSource().createSurfaceBiomeCache(
                minQX - padding, minQZ - padding,
                minQX + sectionQuartSize + padding, minQZ + sectionQuartSize + padding);
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
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return 64;
    }

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
