package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.biome.BiomeList;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesSurfaceDecoration;
import birsy.clinker.common.world.level.gen.system.sampling.Synthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.SynthesizerCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationSystem;
import birsy.clinker.common.world.level.gen.system.sampling.field.*;
import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSet;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesBiome;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerWorldFeatureCapabilities;
import birsy.clinker.core.util.noise.FastNoiseLite;
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
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(),
            minY = chunk.getMinBuildHeight(),
            minZ = chunkPos.getMinBlockZ();
        int chunkHeight = chunk.getHeight();

        SynthesizerCache synthesizerCache = new SynthesizerCache(minX, minY, minZ, chunkHeight, randomState.getOrCreateRandomFactory(Clinker.resource("clinkergen")));
        Synthesizer testSynthesizer = Synthesizer.builder()
                .addNoises(seed -> new FastNoiseLite((int) seed))
                .build(InterpolatingFieldResolution.VERY_COARSE,
                       (x, y, z, dependencyValues, noises) -> noises[0].GetNoise(x, y, z)
                );

        InterpolatingField finalDensityField = synthesizerCache.forThisChunk(testSynthesizer, 0, minY, chunk.getMaxBuildHeight());
        this.fillFromFields(finalDensityField, chunk);
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
        // todo: heightmap stuff
        Heightmap worldSurfaceHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG),
                  oceanFloorHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);

        //InterpolatingFieldSampler sampler = InterpolatingFieldSampler.create(field, new InterpolatingField(0, 0, chunkHeight, 0));
        for (int y = chunk.getHeight() - 1; y >= 0; y--) {
            int globalY = y + minY;
            pos.setY(globalY);
            int sectionY = SectionPos.sectionRelative(globalY);
            int sectionIndex = chunk.getSectionIndex(globalY);
            LevelChunkSection section = chunk.getSection(sectionIndex);

            for (int z = 0; z < 16; z++) {
                int globalZ = z + minZ;
                pos.setZ(globalZ);
                for (int x = 0; x < 16; x++) {
                    int globalX = x + minX;
                    pos.setX(globalX);

                    if (field.retrieve(x, y, z) <= 0) {
                        chunk.setBlockState(pos, BRIMSTONE, false);
                        //section.setBlockState(x, sectionY, z, BRIMSTONE, false);
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
                    //sampler.advanceX();
                }
                //sampler.advanceZ();
            }
            // we actually iterate in reverse y order, so we set the slice directly
            //sampler.setSlice(y);
        }
//        int minX = chunk.getPos().getMinBlockX(), minY = chunk.getMinBuildHeight(), minZ = chunk.getPos().getMinBlockZ();
//        for (int yi = chunk.getHeight() - 1; yi >= 0; yi--) {
//            int y = yi + minY;
//            pos.setY(y);
//            int sectionY = SectionPos.sectionRelative(y);
//            int sectionIndex = chunk.getSectionIndex(y);
//            LevelChunkSection section = chunk.getSection(sectionIndex);
//
//            for (int zi = 0; zi < 16; zi++) {
//                int z = zi + minZ;
//                pos.setZ(z);
//
//                for (int xi = 0; xi < 16; xi++) {
//                    int x = xi + minX;
//                    pos.setX(x);
//
//                    double density = densityField.retrieve(xi, yi, zi);
//                    density = MathUtils.smoothMinExpo(density, fluidField.getBorderDensity(xi, yi, zi), 3);
//
//                    boolean isSolid = density <= 0;
//                    BlockState state = isSolid ? BRIMSTONE : fluidField.getFluidState(x, y, z);
//                    if (state != null && !state.isAir()) {
//                        section.setBlockState(xi, sectionY, zi, state, false);
//                        // update any placed state blocks in waterfalls, so they flow!
//                        if (!isSolid && waterfallPresence.retrieve(xi, yi, zi) > 0) {
//                            chunk.markPosForPostprocessing(pos);
//                        }
//
//                        // fill heightmaps
//                        int index = xi + zi * 16;
//                        if (!filledWorldSurfaceHeight[index]) {
//                            worldSurfaceHeightmap.update(xi, pos.getY(), zi, state);
//                            filledWorldSurfaceHeight[index] = true;
//                        }
//                        if (!filledOceanFloorHeight[index] && isSolid) {
//                            oceanFloorHeightmap.update(xi, pos.getY(), zi, state);
//                            filledOceanFloorHeight[index] = true;
//                        }
//                    }
//                }
//            }
//        }
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
