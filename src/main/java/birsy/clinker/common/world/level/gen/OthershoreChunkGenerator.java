package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.system.fluid.BFSBorderFluidField;
import birsy.clinker.common.world.level.gen.system.fluid.FluidField;
import birsy.clinker.common.world.level.gen.system.fluid.FluidFieldFiller;
import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationSystem;
import birsy.clinker.common.world.level.gen.system.noise.*;
import birsy.clinker.common.world.level.gen.system.noise.field.*;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaperSystem;
import birsy.clinker.common.world.level.gen.system.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
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
    private static final int BIOME_BLUR_KERNEL_SIZE = 5;
    private static final double HALF_BIOME_BLUR_KERNEL_SIZE = BIOME_BLUR_KERNEL_SIZE / 2.0;
    private static final double[] BIOME_BLUR_KERNEL = Util.make(() -> {
        double gamma = HALF_BIOME_BLUR_KERNEL_SIZE / 3.0;
        double[] array = new double[BIOME_BLUR_KERNEL_SIZE * BIOME_BLUR_KERNEL_SIZE];
        for (int kX = 0; kX < BIOME_BLUR_KERNEL_SIZE; kX++) {
            double kernelX = kX + 0.5;
            for (int kZ = 0; kZ < BIOME_BLUR_KERNEL_SIZE; kZ++) {
                double kernelZ = kZ + 0.5;
                double distanceToCenter = Math.sqrt(
                        (kernelX - HALF_BIOME_BLUR_KERNEL_SIZE) * (kernelX - HALF_BIOME_BLUR_KERNEL_SIZE) +
                                (kernelZ - HALF_BIOME_BLUR_KERNEL_SIZE) * (kernelZ - HALF_BIOME_BLUR_KERNEL_SIZE)
                );
                array[kX + kZ * BIOME_BLUR_KERNEL_SIZE] = (1.0 / Math.sqrt(2 * Math.PI * gamma)) * Math.exp(-((distanceToCenter * distanceToCenter) / (2 * gamma * gamma)));
            }
        }
        return array;
    });
    private static final ResourceLocation BEDROCK_RANDOM = Clinker.resource("bedrock");

    private final SurfaceDecorationSystem surfaceDecorationSystem;
    private final SurfaceShaperSystem surfaceShaperSystem;

    private final Map<Holder<Biome>, Integer> biomeToIndex;

    public OthershoreChunkGenerator(HolderGetter<Biome> biomeGetter, OthershoreBiomeSource biomeSource) {
        super(biomeSource);
        this.surfaceDecorationSystem = new SurfaceDecorationSystem(8, OthershoreBiomeSource.SEA_HEIGHT, ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), biomeGetter);
        this.surfaceShaperSystem = new SurfaceShaperSystem(biomeGetter, biomeSource);

        Set<Holder<Biome>> possibleBiomes = this.getBiomeSource().possibleBiomes();
        this.biomeToIndex = new HashMap<>(possibleBiomes.size());
        int index = 0;
        for (Holder<Biome> biomeHolder : this.biomeSource.possibleBiomes()) {
            this.biomeToIndex.put(biomeHolder, index++);
        }
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

//    @Override
//    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk) {
//        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("clinker_wgen_fill_biomes",
//                () -> doBiomeFillTask(this.getBiomeSource(), blender, randomState, structureManager, chunk)), Util.backgroundExecutor()
//        );
//    }
//
//    private ChunkAccess doBiomeFillTask(OthershoreBiomeSource othershoreBiomeSource, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
//        ChunkPos chunkpos = chunk.getPos();
//
//        LevelHeightAccessor levelheightaccessor = chunk.getHeightAccessorForGeneration();
//        SeededNoiseHolder noiseHolder = ((SeededNoiseHolderHolder)(Object)randomState).clinker$noiseHolder();
//        OldCachedNoiseComputerExecutor noiseExecutor = new OldCachedNoiseComputerExecutor(chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getHeight(), noiseHolder);
//        NoiseComputerContext context = new NoiseComputerContext(noiseExecutor, noiseHolder);
//
//        Collection<WorldFeature> worldFeatures = ((MetaChunkMapHolder)(Object) randomState).clinker$metaChunkMap()
//                .getWorldFeatures(chunk.getLevel(), chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());
//
//        for (int section = levelheightaccessor.getMinSection(); section < levelheightaccessor.getMaxSection(); section++) {
//            LevelChunkSection levelchunksection = chunk.getSection(chunk.getSectionIndexFromSectionY(section));
//            PalettedContainer<Holder<Biome>> palettedcontainer = levelchunksection.getBiomes().recreate();
//
//            for (int qX = 0; qX < SectionPos.SECTION_SIZE / QuartPos.SIZE; qX++) {
//                int x = qX * QuartPos.SIZE + chunkpos.getMinBlockX();
//                for (int qY = 0; qY < SectionPos.SECTION_SIZE / QuartPos.SIZE; qY++) {
//                    int y = qY * QuartPos.SIZE +section * SectionPos.SECTION_SIZE;
//                    for (int qZ = 0; qZ < SectionPos.SECTION_SIZE / QuartPos.SIZE; qZ++) {
//                        int z = qZ * QuartPos.SIZE + chunkpos.getMinBlockZ();
//
//                        Holder<Biome> biome = othershoreBiomeSource.getNoiseBiome(x, y, z, noiseExecutor);
//                        for (WorldFeature worldFeature : worldFeatures) {
//                            worldFeature.modifyBiome(x, y, z, biome, context);
//                        }
//                        palettedcontainer.getAndSetUnchecked(qX, qY, qZ, biome);
//                    }
//                }
//            }
//            levelchunksection.biomes = palettedcontainer;
//        }
//
//        return chunk;
//    }


    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("clinker_wgen_fill_noise",
                () -> this.doNoiseFillTask(blender, randomState, structureManager, chunk)), Util.backgroundExecutor()
        );
    }

    public ChunkAccess doNoiseFillTask(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        int minX = chunk.getPos().getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunk.getPos().getMinBlockZ();
        int chunkHeight = chunk.getHeight();
        SeededNoiseHolder noiseHolder = ((SeededNoiseHolderHolder) (Object) randomState).clinker$noiseHolder();
        NoiseFieldCache noiseFieldCache = new NoiseFieldCache(
                minX, minY, minZ, chunkHeight,
                noiseHolder
        );
        Collection<WorldFeature> worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                .getWorldFeatures(chunk.getLevel(), minX, minZ);

        // density field
        int[] minAndMaxSurfaceHeight = {chunk.getMaxBuildHeight(), chunk.getMinBuildHeight()};
        NoiseField surfaceHeightField = computeSurfaceHeight(noiseFieldCache, minAndMaxSurfaceHeight);
        NoiseField finalDensityField = createFinalDensityField(
                chunk, noiseHolder, noiseFieldCache, worldFeatures,
                surfaceHeightField, minAndMaxSurfaceHeight[0], minAndMaxSurfaceHeight[1],
                minX, minY, minZ
        );

        // fluid field
        int cellWidth = 4, cellHeight = 16;
        FluidFieldNoiseFieldCache fluidNoiseFieldCache = new FluidFieldNoiseFieldCache(minX, minY, minZ, chunkHeight, noiseHolder, cellWidth);
        NoiseField waterfallPresence = fluidNoiseFieldCache.fillNoiseField(ClinkerNoiseComputers.WATERFALL_PRESENCE.get());
        for (WorldFeature worldFeature : worldFeatures)
            worldFeature.modifyWaterfallPresenceField(minX, minY, minZ, fluidNoiseFieldCache, waterfallPresence);

        NoiseField fluidFieldSurfaceHeight = fluidNoiseFieldCache.fillNoiseField(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT.get());
        final FluidFieldFiller fluidFiller = (x, y, z, context) -> {
            double surfaceHeight = fluidFieldSurfaceHeight.retrieve(x - minX, y - minY, z - minZ);
            // sea level
            if (y > surfaceHeight - cellHeight) return new FluidLevel(70, Blocks.WATER.defaultBlockState());
            // the aquifer
            if (y < 0) return new FluidLevel(-40, Blocks.WATER.defaultBlockState());
            return FluidLevel.EMPTY;
        };

        FluidField finalFluidField = new BFSBorderFluidField(randomState, chunk,
                fluidNoiseFieldCache,
                fluidFiller,
                worldFeatures,
                cellWidth, cellHeight, 1
        );
        finalFluidField.precomputeValues(finalDensityField, waterfallPresence);

        this.fillFromFields(finalDensityField, finalFluidField, waterfallPresence, chunk);
        return chunk;
    }

    private void fillFromFields(NoiseField densityField, FluidField fluidField, NoiseField waterfallPresence, ChunkAccess chunk) {
        Heightmap heightmapOceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmapWorldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int yi = 0; yi < chunk.getHeight() - 1; yi++) {
            pos.setY(yi + chunk.getMinBuildHeight());
            for (int zi = 0; zi < 16; zi++) {
                pos.setZ(zi + chunk.getPos().getMinBlockZ());
                for (int xi = 0; xi < 16; xi++) {
                    pos.setX(xi + chunk.getPos().getMinBlockX());

                    double density = densityField.retrieve(xi, yi, zi);
                    density = MathUtils.smoothMinExpo(density, fluidField.getBorderDensity(xi, yi, zi), 3);

                    boolean isSolid = density <= 0;
                    BlockState state = isSolid ?
                            ClinkerBlocks.BRIMSTONE.get().defaultBlockState() :
                            fluidField.getFluidState(pos.getX(), pos.getY(), pos.getZ());
                    if (!state.isAir()) {
                        chunk.setBlockState(pos, state, false);
                        // update any placed fluid blocks in waterfalls, so they flow!
                        if (!state.getFluidState().isEmpty() && waterfallPresence.retrieve(xi, yi, zi) > 0) {
                            chunk.markPosForPostprocessing(pos);
                        }
                        // update heightmaps
                        heightmapOceanFloor.update(xi, pos.getY(), zi, state);
                        heightmapWorldSurface.update(xi, pos.getY(), zi, state);
                    }
                }
            }
        }
    }

    private NoiseField computeSurfaceHeight(NoiseFieldCache cache, int[] surfaceHeightBounds) {
        NoiseField field = cache.fillNoiseField(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT.get());
        field.byIndex((index) -> {
            double value = field.array()[index];
            surfaceHeightBounds[0] = Math.min(Mth.floor(value), surfaceHeightBounds[0]);
            surfaceHeightBounds[1] = Math.max(Mth.ceil(value), surfaceHeightBounds[1]);
        });
        return field;
    }

    private NoiseField createFinalDensityField(ChunkAccess chunk, SeededNoiseHolder noiseHolder, NoiseFieldCache cache,
                                               Collection<WorldFeature> worldFeaturesInChunk,
                                               NoiseField baseSurfaceHeight,
                                               int minSurfaceHeight, int maxSurfaceHeight,
                                               int minX, int minY, int minZ) {
//        if (true) {
//            NoiseField finalDensityField = new InterpolatedNoiseField(chunk.getHeight(), 1, 1, 0);
//            double[] finalDensityFieldArray = finalDensityField.array();
//            Arrays.fill(finalDensityFieldArray, 100);
//            return finalDensityField;
//        }
        int chunkHeight = chunk.getHeight();
        Set<Holder<Biome>> surfaceBiomes = this.getSurfaceBiomesInChunk(chunk);

        NoiseField surfaceDensityField = surfaceShaperSystem.generateSurfaceField(
                chunk, noiseHolder, cache,
                surfaceBiomes, worldFeaturesInChunk,
                baseSurfaceHeight, minSurfaceHeight, maxSurfaceHeight,
                minX, minY, minZ, chunkHeight
        );

        // compute cave density
        int maxCaveHeight = maxSurfaceHeight + 32;
        NoiseField caveDensityField = cache.fillNoiseField(minY, maxCaveHeight, ClinkerNoiseComputers.CAVES.get());
        double[] caveDensityFieldArray = caveDensityField.array();
        caveDensityField.byIndex(maxCaveHeight - minY + 1, chunkHeight - 1, (index) -> caveDensityFieldArray[index] = -1000);
        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifyCaveDensityField(minX, minY, minZ, cache, caveDensityField);

        // combine cave density and surface density
        // special 2x2x2 cell size for extra vertical detail...
        NoiseField finalDensityField = new InterpolatedNoiseField(chunkHeight, 1, 1, 0);
        double[] finalDensityFieldArray = finalDensityField.array();
        finalDensityField.byBlock(0, maxCaveHeight - minY, (index, x, y, z) -> {
            double surfaceDensity = surfaceDensityField.retrieve(x, y, z);
            double caveDensity = caveDensityField.retrieve(x, y, z);
            finalDensityFieldArray[index] = -MathUtils.smoothMinExpo(-surfaceDensity, -caveDensity, 8);
        });
        finalDensityField.byBlock(maxCaveHeight - minY + 1, chunkHeight - 1,
                (index, x, y, z) -> finalDensityFieldArray[index] = surfaceDensityField.retrieve(x, y, z)
        );

        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifyFinalDensityField(minX, minY, minZ, cache, finalDensityField);

        return finalDensityField;
    }

    private Set<Holder<Biome>> getSurfaceBiomesInChunk(ChunkAccess chunkAccess) {
        OthershoreBiomeSource source = this.getBiomeSource();
        Set<Holder<Biome>> set = new HashSet<>(5);
        int startX = QuartPos.fromSection(chunkAccess.getPos().x),
            startZ = QuartPos.fromSection(chunkAccess.getPos().z);
        int endH = QuartPos.fromBlock(16);
        for (int x = 0; x < endH; x++) {
            for (int z = 0; z < endH; z++) {
                set.add(source.getSurfaceBiome(startX + x, startZ + z));
            }
        }
        return set;
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
        NoiseFieldCache noiseFieldCache = new NoiseFieldCache(minX, minY, minZ, chunkHeight, noiseHolder);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(minX, minY, minZ);

        createBedrockLayer(chunk, randomState, pos, minX, minY, minZ);

        NoiseField field = noiseFieldCache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[3]);
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

    private void createBedrockLayer(ChunkAccess chunk, RandomState random, BlockPos.MutableBlockPos pos, int minX, int minY, int minZ) {
        PositionalRandomFactory randomFactory = random.getOrCreateRandomFactory(BEDROCK_RANDOM);
        // bedrock
        // transition
        for (int y = 1; y < 4; y++) {
            pos.setY(minY + y);
            double bedrockFactor = 1.0 - (y / 4.0);
            for (int x = 0; x < 16; x++) {
                pos.setX(minX + x);
                for (int z = 0; z < 16; z++) {
                    pos.setZ(minZ + z);
                    double randomValue = randomFactory.at(pos.getX(), pos.getY(), pos.getZ()).nextDouble();
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

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (level.getChunkSource() instanceof ServerChunkCache chunkCache) {
            int[] minAndMaxSurfaceHeight = {chunk.getMaxBuildHeight(), chunk.getMinBuildHeight()};
            SeededNoiseHolder noiseHolder = ((SeededNoiseHolderHolder)(Object)chunkCache.randomState()).clinker$noiseHolder();
            NoiseFieldCache cache = new NoiseFieldCache(
                    chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(),
                    chunk.getHeight(), noiseHolder
            );
            surfaceDecorationSystem.applySurfaceDecorations(
                    level, chunk, chunkCache.randomState(), cache,
                    computeSurfaceHeight(cache, minAndMaxSurfaceHeight),
                    minAndMaxSurfaceHeight[0], minAndMaxSurfaceHeight[1],
                    getBiomesInChunk(chunk));
        }
        super.applyBiomeDecoration(level, chunk, structureManager);
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
