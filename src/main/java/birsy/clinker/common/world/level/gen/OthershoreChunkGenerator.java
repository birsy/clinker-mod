package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.biome.BiomeList;
import birsy.clinker.common.world.level.gen.system.fluid.BFSBorderFluidField;
import birsy.clinker.common.world.level.gen.system.fluid.FluidField;
import birsy.clinker.common.world.level.gen.system.fluid.FluidFieldFiller;
import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesSurfaceDecoration;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationSystem;
import birsy.clinker.common.world.level.gen.system.noise.*;
import birsy.clinker.common.world.level.gen.system.noise.field.*;
import birsy.clinker.common.world.level.gen.system.biome.BiomeBlender;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaperSystem;
import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSet;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesBiome;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesCaveDensity;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesFinalDensity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.registry.worldgen.ClinkerWorldFeatureCapabilities;
import birsy.clinker.core.util.MathUtils;
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
    public static final MapCodec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(RegistryOps.retrieveGetter(Registries.BIOME),
                            OthershoreBiomeSource.CODEC.fieldOf("biome_source")
                                    .forGetter(OthershoreChunkGenerator::getBiomeSource))
                    .apply(obj, obj.stable(OthershoreChunkGenerator::new))
    );
    private static final ResourceLocation BEDROCK_RANDOM = Clinker.resource("bedrock");
    private static final BlockState BRIMSTONE = ClinkerBlocks.BRIMSTONE.get().defaultBlockState();

    final BiomeList biomeList;

    final BiomeBlender biomeBlender;
    final SurfaceShaperSystem surfaceShaperSystem;
    final SurfaceDecorationSystem surfaceDecorationSystem;
    final WorldFeatureContext worldContext;

    private static final Map<Holder<Biome>, Integer> biomeSeaHeight = new HashMap<>();

    public OthershoreChunkGenerator(HolderGetter<Biome> biomeGetter, OthershoreBiomeSource biomeSource) {
        super(biomeSource);
        this.biomeList = biomeSource.biomeList;
        this.biomeBlender = new BiomeBlender(this.biomeList, biomeSource);
        this.surfaceShaperSystem = new SurfaceShaperSystem(biomeGetter, this.biomeList);
        this.surfaceDecorationSystem = new SurfaceDecorationSystem(ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), biomeGetter);

        this.worldContext = new WorldFeatureContext(biomeList, biomeBlender, surfaceShaperSystem);
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

    private static int fluidCellWidth() { return 8; }
    private static int fluidCellHeight() { return 16; }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("clinker_wgen_fill_biomes",
                () -> doBiomeFillTask(this.getBiomeSource(), blender, randomState, structureManager, chunk)), Util.backgroundExecutor()
        );
    }

    private ChunkAccess doBiomeFillTask(
            OthershoreBiomeSource othershoreBiomeSource,
            Blender blender,
            RandomState randomState,
            StructureManager structureManager,
            ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunkPos.getMinBlockZ();
        int chunkHeight = chunk.getHeight();
        LevelHeightAccessor levelheightaccessor = chunk.getHeightAccessorForGeneration();
        SeededNoiseHolder noiseHolder = ((SeededNoiseHolderHolder) (Object) randomState).clinker$noiseHolder();

        NoiseFieldCache noiseFieldCache = new NoiseFieldCache(minX, minY, minZ, chunkHeight, noiseHolder);

        WorldFeatureSet worldFeatures =
                ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                        .getWorldFeatures(chunk.getLevel(), minX, minZ, this.worldContext);

        // prefill noise fields
        List<ModifiesBiome> biomeModifyingWorldFeatures = worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_BIOME.get());
        for (ModifiesBiome worldFeature : biomeModifyingWorldFeatures)
            worldFeature.prefillBiomeNoiseFields(chunkPos.x, chunkPos.z, noiseFieldCache, worldContext);
        othershoreBiomeSource.prefillNoiseFields(noiseFieldCache);

        BiomeCache2d surfaceBiomes = this.getSurfaceBiomeCacheForChunk(minX, minZ);
        BiomeBlender.ChunkBiomeBlendingInfo blendingInfo = this.biomeBlender.generateChunkBiomeBlendingInfo(surfaceBiomes, minX, minZ, 0);
        SurfaceShaperSystem.ChunkSurfaceHeightmap heightmapInfo = this.surfaceShaperSystem.generateHeightmap(
                noiseFieldCache,
                worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_HEIGHTMAP.get()),
                surfaceBiomes,
                blendingInfo,
                worldContext,
                minX, minZ
        );

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
                                surfaceBiomes, heightmapInfo, noiseFieldCache.context
                        );
                        for (ModifiesBiome worldFeature : biomeModifyingWorldFeatures)
                            worldFeature.modifyBiome(globalBlockX, globalBlockY, globalBlockZ, minX, minY, minZ, biome, noiseFieldCache.context);
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

    private ChunkAccess doNoiseFillTask(
            Blender blender,
            RandomState randomState,
            StructureManager structureManager,
            ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunkPos.getMinBlockZ();
        int chunkHeight = chunk.getHeight();
        int fluidCellWidth = fluidCellWidth(), fluidCellHeight = fluidCellHeight();
        SeededNoiseHolder noiseHolder = ((SeededNoiseHolderHolder) (Object) randomState).clinker$noiseHolder();
        NoiseFieldCache noiseFieldCache =
                new NoiseFieldCache(minX, minY, minZ, chunkHeight, noiseHolder);
        PaddedNoiseFieldCache biomeAndFluidCache =
                new PaddedNoiseFieldCache(minX, minY, minZ, chunkHeight, noiseHolder, fluidCellWidth);
        WorldFeatureSet worldFeatures =
                ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                .getWorldFeatures(chunk.getLevel(), minX, minZ, this.worldContext);

        // density combinedHeightmapField
        BiomeCache2d surfaceBiomes = this.getSurfaceBiomeCacheForChunk(minX, minZ);
        BiomeBlender.ChunkBiomeBlendingInfo chunkBiomeBlendingInfo = this.biomeBlender.generateChunkBiomeBlendingInfo(surfaceBiomes, minX, minZ, fluidCellWidth);
        SurfaceShaperSystem.ChunkSurfaceHeightmap heightmapInfo = this.surfaceShaperSystem.generateHeightmap(
                biomeAndFluidCache,
                worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_HEIGHTMAP.get()),
                surfaceBiomes,
                chunkBiomeBlendingInfo,
                worldContext,
                minX, minZ,
                fluidCellWidth
        );

        NoiseField finalDensityField = createFinalDensityField(
                chunk, noiseHolder, noiseFieldCache, biomeAndFluidCache, worldFeatures,
                surfaceBiomes, chunkBiomeBlendingInfo, heightmapInfo,
                minX, minY, minZ
        );

        // fluid combinedHeightmapField
        int cellWidth = fluidCellWidth, cellHeight = fluidCellHeight;

        final FluidFieldFiller fluidFiller = (x, y, z, context) -> {
            double surfaceHeight = heightmapInfo.combinedHeightmapField().retrieve(x - minX, y - minY, z - minZ);
            // sea level
            int seaLevel = biomeSeaHeight.getOrDefault(surfaceBiomes.retrieve(QuartPos.fromBlock(x), QuartPos.fromBlock(z)), OthershoreGenerationConstants.SEA_HEIGHT);
            if (y > surfaceHeight - cellHeight) return new FluidLevel(seaLevel, Blocks.WATER.defaultBlockState());
            // the aquifer
            if (y < 0) return new FluidLevel(-40, Blocks.WATER.defaultBlockState());
            return FluidLevel.EMPTY;
        };

        BFSBorderFluidField finalFluidField = new BFSBorderFluidField(randomState, chunk,
                biomeAndFluidCache, fluidFiller,
                worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_FLUIDS.get()),
                worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_WATERFALL_PRESENCE.get()),
                worldContext, heightmapInfo.combinedHeightmapField(), cellWidth, cellHeight, 1
        );
        finalFluidField.precomputeValues(finalDensityField);

        this.fillFromFields(finalDensityField, finalFluidField, finalFluidField.waterfallPresenceField, chunk);
        return chunk;
    }

    private void fillFromFields(NoiseField densityField, FluidField fluidField, NoiseField waterfallPresence, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        boolean[] filledWorldSurfaceHeight = new boolean[16 * 16],
                  filledOceanFloorHeight = new boolean[16 * 16];
        Arrays.fill(filledWorldSurfaceHeight, false);
        Arrays.fill(filledOceanFloorHeight, false);

        Heightmap worldSurfaceHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG),
                  oceanFloorHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);

        int minX = chunk.getPos().getMinBlockX(), minY = chunk.getMinBuildHeight(), minZ = chunk.getPos().getMinBlockZ();
        for (int yi = chunk.getHeight() - 1; yi >= 0; yi--) {
            int y = yi + minY;
            pos.setY(y);
            int sectionY = SectionPos.sectionRelative(y);
            int sectionIndex = chunk.getSectionIndex(y);
            LevelChunkSection section = chunk.getSection(sectionIndex);

            for (int zi = 0; zi < 16; zi++) {
                int z = zi + minZ;
                pos.setZ(z);

                for (int xi = 0; xi < 16; xi++) {
                    int x = xi + minX;
                    pos.setX(x);

                    double density = densityField.retrieve(xi, yi, zi);
                    density = MathUtils.smoothMinExpo(density, fluidField.getBorderDensity(xi, yi, zi), 3);

                    boolean isSolid = density <= 0;
                    BlockState state = isSolid ? BRIMSTONE : fluidField.getFluidState(x, y, z);
                    if (state != null && !state.isAir()) {
                        section.setBlockState(xi, sectionY, zi, state, false);
                        // update any placed state blocks in waterfalls, so they flow!
                        if (!isSolid && waterfallPresence.retrieve(xi, yi, zi) > 0) {
                            chunk.markPosForPostprocessing(pos);
                        }
                        
                        // fill heightmaps
                        int index = xi + zi * 16;
                        if (!filledWorldSurfaceHeight[index]) {
                            worldSurfaceHeightmap.update(xi, pos.getY(), zi, state);
                            filledWorldSurfaceHeight[index] = true;
                        }
                        if (!filledOceanFloorHeight[index] && isSolid) {
                            oceanFloorHeightmap.update(xi, pos.getY(), zi, state);
                            filledOceanFloorHeight[index] = true;
                        }
                    }
                }
            }
        }

    }

    private NoiseField createFinalDensityField(ChunkAccess chunk, SeededNoiseHolder noiseHolder,
                                               NoiseFieldCache cache, PaddedNoiseFieldCache biomeCache,
                                               WorldFeatureSet worldFeaturesInChunk,
                                               BiomeCache2d surfaceBiomes,
                                               BiomeBlender.ChunkBiomeBlendingInfo chunkBiomeBlendingInfo,
                                               SurfaceShaperSystem.ChunkSurfaceHeightmap heightmapInfo,
                                               int minX, int minY, int minZ) {
        int chunkHeight = chunk.getHeight();

        NoiseField heightmap = heightmapInfo.combinedHeightmapField();
        NoiseField heightmapGradient = surfaceShaperSystem.generateHeightmapGradientSquaredField(heightmap);
        NoiseField distanceToHeightmap = surfaceShaperSystem.generateApproximateDistanceToHeightmap(chunkHeight, minY, heightmap, heightmapGradient);
        NoiseField surfaceDensityField = surfaceShaperSystem.generateSurfaceDensity(
                cache,
                worldFeaturesInChunk.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_SURFACE_DENSITY.get()),
                surfaceBiomes, chunkBiomeBlendingInfo,
                heightmapInfo, heightmapGradient, distanceToHeightmap, this.worldContext,
                minX, minY, minZ, chunkHeight
        );

        // compute cave density
        int maxCaveHeight = heightmapInfo.maximum() + 32;
        int localMaxCaveHeight = maxCaveHeight - minY + 1;
        NoiseField caveDensityField = cache.fillNoiseField(minY, maxCaveHeight, ClinkerNoiseComputers.CAVES.get());
        double[] caveDensityFieldArray = caveDensityField.array();
        caveDensityField.byBlock(localMaxCaveHeight, chunkHeight - 1,
                (index, x, y, z) -> { if (y > localMaxCaveHeight) caveDensityFieldArray[index] = -100; }
        );
        NoiseField caveEntranceMaskField =
                cache.fillNoiseField(minY, maxCaveHeight, ClinkerNoiseComputers.CAVE_ENTRANCE_MASK.get());
        for (ModifiesCaveDensity worldFeature : worldFeaturesInChunk.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_CAVE_DENSITY.get()))
            worldFeature.modifyCaveDensity(minX, minY, minZ, maxCaveHeight, cache, caveDensityField, caveEntranceMaskField, worldContext);
        // combine mask and height
        caveDensityField.byBlock(
                0, localMaxCaveHeight,
                (index, x, y, z) -> {
                    double dist = distanceToHeightmap.retrieve(x, y, z) + 24;
                    dist -= 32 * caveEntranceMaskField.retrieve(x, y, z);
                    caveDensityFieldArray[index] =
                            MathUtils.smoothMinExpo(caveDensityFieldArray[index], -dist, 5);
                }
        );

        // combine cave density and surface density
        // special 2x2x2 cell size for extra vertical detail...
        NoiseField finalDensityField = new NoiseField(chunkHeight, 1, 1, 0);
        double[] finalDensityFieldArray = finalDensityField.array();
        finalDensityField.byBlock(0, maxCaveHeight - minY, (index, x, y, z) -> {
            double surfaceDensity = surfaceDensityField.retrieve(x, y, z);
            double caveDensity = caveDensityField.retrieve(x, y, z);
            finalDensityFieldArray[index] = -MathUtils.smoothMinExpo(-surfaceDensity, -caveDensity, 8);
        });
        finalDensityField.byBlock(maxCaveHeight - minY + 1, chunkHeight - 1,
                (index, x, y, z) -> finalDensityFieldArray[index] = surfaceDensityField.retrieve(x, y, z)
        );

        for (ModifiesFinalDensity worldFeature : worldFeaturesInChunk.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_FINAL_DENSITY.get()))
            worldFeature.modifyFinalDensity(minX, minY, minZ, cache, finalDensityField, worldContext);

        return finalDensityField;
    }

    private Set<Holder<Biome>> getBiomesInChunk(ChunkAccess chunkAccess) {
        Set<Holder<Biome>> set = new HashSet<>(5);
        int endH = QuartPos.fromBlock(16);
        int startV = QuartPos.fromBlock(chunkAccess.getMinBuildHeight()),
            endV = QuartPos.fromBlock(chunkAccess.getMaxBuildHeight());
        for (int x = 0; x < endH; x++) {
            for (int z = 0; z < endH; z++) {
                for (int y = startV; y < endV; y++) {
                    set.add(chunkAccess.getNoiseBiome(x, y, z));
                }
            }
        }
        return set;
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        int minX = chunk.getPos().getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunk.getPos().getMinBlockZ();
        int chunkHeight = chunk.getHeight();
        SeededNoiseHolder noiseHolder = ((SeededNoiseHolderHolder) (Object) randomState).clinker$noiseHolder();
        NoiseFieldCache cache = new NoiseFieldCache(minX, minY, minZ, chunkHeight, noiseHolder);

        WorldFeatureSet worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                .getWorldFeatures(chunk.getLevel(), minX, minZ, worldContext);
        BiomeCache2d surfaceBiomes = getSurfaceBiomeCacheForChunk(minX, minZ);
        BiomeBlender.ChunkBiomeBlendingInfo chunkBiomeBlendingInfo = biomeBlender.generateChunkBiomeBlendingInfo(surfaceBiomes, minX, minZ, 0);
        SurfaceShaperSystem.ChunkSurfaceHeightmap heightmapInfo = surfaceShaperSystem.generateHeightmap(
                cache,
                worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_HEIGHTMAP.get()),
                surfaceBiomes, chunkBiomeBlendingInfo,
                worldContext,
                minX, minZ
        );
        NoiseField heightmapGradient = surfaceShaperSystem.generateHeightmapGradientSquaredField(heightmapInfo.combinedHeightmapField());
        surfaceDecorationSystem.decorate(
                cache, heightmapInfo.combinedHeightmapField(), heightmapGradient,
                level, chunk, randomState
        );

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(minX, minY, minZ);
        PositionalRandomFactory randomFactory = randomState.getOrCreateRandomFactory(BEDROCK_RANDOM);

        for (ModifiesSurfaceDecoration modifiesSurfaceDecoration : worldFeatures.byCapability(ClinkerWorldFeatureCapabilities.MODIFIES_SURFACE_DECORATION.get())) {
            modifiesSurfaceDecoration.modifySurfaceDecoration(
                    cache, level, chunk, randomState, worldContext
            );
        }

        createBarrierrockLayer(chunk, randomFactory, cache, pos, minX, minY, minZ);
        createBedrockLayer(chunk, randomFactory, cache, pos, minX, minY, minZ);
    }

    private void createBedrockLayer(
            ChunkAccess chunk, PositionalRandomFactory random, NoiseFieldCache cache,
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
    private void createBarrierrockLayer(
            ChunkAccess chunk, PositionalRandomFactory random, NoiseFieldCache cache,
            BlockPos.MutableBlockPos pos, int minX, int minY, int minZ) {
        NoiseField field = cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[3]);
        for (int x = 0; x < 16; x++) {
            pos.setX(minX + x);
            for (int z = 0; z < 16; z++) {
                pos.setZ(minZ + z);
                double noiseValue = Math.abs(field.retrieve(x, 0, z));
                for (int y = -3; y <= 3; y++) {
                    pos.setY(y);
                    double mixFactor = 1.0 - (Math.abs(y) / 4.0);
                    if (!chunk.getBlockState(pos).isSolid()) {
                        mixFactor -= 0.5;
                    }
                    if (noiseValue < mixFactor) {
                        chunk.setBlockState(pos, ClinkerBlocks.BARRIERROCK.get().defaultBlockState(), false);
                    }
                }
            }
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        super.applyBiomeDecoration(level, chunk, structureManager);
    }

    private BiomeCache2d getSurfaceBiomeCacheForChunk(int minX, int minZ) {
        int sectionQuartSize = QuartPos.fromSection(1);
        int minQX = QuartPos.fromBlock(minX), minQZ = QuartPos.fromBlock(minZ);
        int padding = this.biomeBlender.requiredBiomeCachePadding() + fluidCellWidth();
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
