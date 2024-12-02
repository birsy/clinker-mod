package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.*;
import birsy.clinker.core.util.noise.FastNoiseLite;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TestChunkGenerator2 extends ChunkGenerator {
    public static final MapCodec<TestChunkGenerator2> CODEC = RecordCodecBuilder.mapCodec((codec) ->
            codec.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder))
                    .apply(codec, codec.stable(TestChunkGenerator2::new)));

    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;

    private static final FastNoiseLite noise = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        return n;
    });


    private static final VoronoiGenerator voronoi = new VoronoiGenerator(0);
    private long seed;
    private final NoiseFieldWithOffset terrainField;
    private final NoiseFieldWithOffset fluidField;

    public TestChunkGenerator2(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.value();
        this.terrainField = new InterpolatedNoiseField(16, this.getGenDepth(), 16, 4, this.getGenDepth()/8, 4);
        this.fluidField = new InterpolatedNoiseField(16, this.getGenDepth(), 16, 4, 16, 4);
    }

    private void setNoiseSeed(long seed) {
        if (seed != noise.GetSeed()) {
            noise.SetSeed((int) seed);
        }
        if (seed != voronoi.getSeed()) {
            voronoi.setSeed(seed);
        }
    }

    // earliest possible reference to a seed
    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> pStructureSetLookup, RandomState pRandomState, long pSeed) {
        this.seed = pSeed;
        this.setNoiseSeed(this.seed);
        return super.createState(pStructureSetLookup, pRandomState, pSeed);
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        this.setNoiseSeed(this.seed);
        this.terrainField.setPosOffset(chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ());
        this.terrainField.fill(this::evaluateNoise);
        this.fillFromNoise(this.terrainField, chunk, this.settings.defaultBlock(), true);

        this.fluidField.setPosOffset(chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ());
        this.fluidField.fill(this::evaluateFluidNoise);
        this.fillFromNoise(this.fluidField, chunk, this.settings.defaultFluid(), false);

        this.updateHeightmaps(chunk);
        return CompletableFuture.completedFuture(chunk);
    }

//    private ChunkAccess doFill(Blender pBlender, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk, int pMinCellY, int pCellCountY) {
//        NoiseChunk noisechunk = pChunk.getOrCreateNoiseChunk(p_224255_ -> this.createNoiseChunk(p_224255_, pStructureManager, pBlender, pRandom));
//        Heightmap oceanFloorHeightmap = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
//        Heightmap worldSurfaceHeightmap = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
//        ChunkPos chunkpos = pChunk.getPos();
//
//        int minX = chunkpos.getMinBlockX();
//        int minZ = chunkpos.getMinBlockZ();
//
//        Aquifer aquifer = noisechunk.aquifer();
//        noisechunk.initializeForFirstCellX();
//        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
//        int noiseCellWidth = noisechunk.cellWidth();
//        int noiseCellHeight = noisechunk.cellHeight();
//        int cellsInChunkX = 16 / noiseCellWidth;
//        int cellsInChunkZ = 16 / noiseCellWidth;
//
//        for(int cellX = 0; cellX < cellsInChunkX; ++cellX) {
//            noisechunk.advanceCellX(cellX);
//
//            for(int cellZ = 0; cellZ < cellsInChunkZ; ++cellZ) {
//                int chunkSections = pChunk.getSectionsCount() - 1;
//                LevelChunkSection levelchunksection = pChunk.getSection(chunkSections);
//
//                for(int cellY = pCellCountY - 1; cellY >= 0; --cellY) {
//                    noisechunk.selectCellYZ(cellY, cellZ);
//
//                    for(int cellYBlocks = noiseCellHeight - 1; cellYBlocks >= 0; --cellYBlocks) {
//                        int y = (pMinCellY + cellY) * noiseCellHeight + cellYBlocks;
//                        int yInSection = y & 15;
//                        int sectionIndex = pChunk.getSectionIndex(y);
//
//                        if (chunkSections != sectionIndex) {
//                            chunkSections = sectionIndex;
//                            levelchunksection = pChunk.getSection(sectionIndex);
//                        }
//
//                        double yFactor = (double)cellYBlocks / (double)noiseCellHeight;
//                        noisechunk.updateForY(y, yFactor);
//
//                        for(int cellXBlocks = 0; cellXBlocks < noiseCellWidth; ++cellXBlocks) {
//                            int x = minX + cellX * noiseCellWidth + cellXBlocks;
//                            int xInSection = x & 15;
//                            double xFactor = (double)cellXBlocks / (double)noiseCellWidth;
//                            noisechunk.updateForX(x, xFactor);
//
//                            for(int cellZBlocks = 0; cellZBlocks < noiseCellWidth; ++cellZBlocks) {
//                                int z = minZ + cellZ * noiseCellWidth + cellZBlocks;
//                                int zInSection = z & 15;
//                                double zFactor = (double)cellZBlocks / (double)noiseCellWidth;
//                                noisechunk.updateForZ(z, zFactor);
//                                BlockState blockstate = noisechunk.getInterpolatedState();
//                                if (blockstate == null) {
//                                    blockstate = this.settings.defaultBlock();
//                                }
//
//                                if (blockstate != Blocks.AIR.defaultBlockState() && !SharedConstants.debugVoidTerrain(pChunk.getPos())) {
//                                    levelchunksection.setBlockState(xInSection, yInSection, zInSection, blockstate, false);
//                                    oceanFloorHeightmap.update(xInSection, y, zInSection, blockstate);
//                                    worldSurfaceHeightmap.update(xInSection, y, zInSection, blockstate);
//                                    if (aquifer.shouldScheduleFluidUpdate() && !blockstate.getFluidState().isEmpty()) {
//                                        blockpos$mutableblockpos.set(x, y, z);
//                                        pChunk.markPosForPostprocessing(blockpos$mutableblockpos);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            noisechunk.swapSlices();
//        }
//
//        noisechunk.stopInterpolation();
//        return pChunk;
//    }

    private float evaluateNoise(float currentValue, double x, double y, double z, Object... params) {
        float n1 = noise.GetNoise(x*3, y*3, z*3);
        float n2 = noise.GetNoise(x*3, y*3 - 1000, z*3);
        return (n1 * n1 + n2 * n2) - 0.15F;
    }

    private float evaluateFluidNoise(float currentValue, double x, double y, double z, Object... params) {
        return (float) noise.GetNoise(x, y*5 + 1000, z) - 0.2F;
    }

    private void fillFromNoise(NoiseFieldWithOffset field, ChunkAccess chunk, BlockState stateToPlace, boolean replaceCurrent) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMaxBuildHeight(); y >= chunk.getMinBuildHeight(); y--) {
                    pos.set(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());
                    if (!replaceCurrent && !chunk.getBlockState(pos).isEmpty()) continue;

                    float noiseVal = field.getValue(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());
                    if (noiseVal > 0) {
                        chunk.setBlockState(pos, stateToPlace, false);
                    } else if (replaceCurrent) {
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                    }
                }
            }
        }
    }

    private void updateHeightmaps(ChunkAccess chunk) {
        Heightmap[] heightmaps = {chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG), chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG)};
        boolean[] lastResult = {false, false};
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMaxBuildHeight(); y >= chunk.getMinBuildHeight(); y--) {
                    pos.set(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());
                    BlockState blockState = chunk.getBlockState(pos);

                    boolean shouldContinue = false;
                    for (int i = 0; i < heightmaps.length; i++) {
                        if (!lastResult[i]) {
                            lastResult[i] = heightmaps[i].update(x, y, z, blockState);
                            shouldContinue = shouldContinue || lastResult[i];
                        }
                    }

                    if (!shouldContinue) {
                        break;
                    }
                }
            }
        }
    }

    public int getBaseHeight(int x, int y, Heightmap.Types heightmap, LevelHeightAccessor level, RandomState random) {
        return 224;
    }
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor chunk, RandomState random) {
        BlockState[] states = new BlockState[chunk.getHeight()];
        int iY = 0;
        for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            states[iY] = this.settings.defaultBlock();
            iY++;
        }

        return new NoiseColumn(chunk.getMinBuildHeight(), states);
    }

    private static final Direction[] FLUID_CHECK_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN};
    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        // fucking uhh aquifers checking for their surroundings
        // time to see how painfully slow this is...
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < chunk.getHeight(); y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {

                    pos.set(x + chunk.getPos().getMinBlockX(), y + chunk.getMinBuildHeight(), z + chunk.getPos().getMinBlockZ());
                    FluidState currentState = level.getFluidState(pos);
                    if (currentState.isEmpty()) continue;
                    for (Direction fluidCheckDirection : FLUID_CHECK_DIRECTIONS) {
                        boolean shouldPlaceBlock = !level.getFluidState(pos.move(fluidCheckDirection)).is(currentState.getType()) && !level.getBlockState(pos).isFaceSturdy(level, pos, fluidCheckDirection.getOpposite(), SupportType.FULL);
                        //if (!shouldPlaceBlock) shouldPlaceBlock = !level.getFluidState(pos.move(fluidCheckDirection)).is(currentState.getType()) && !level.getBlockState(pos).isFaceSturdy(level, pos, fluidCheckDirection.getOpposite(), SupportType.FULL);

                        pos.set(x + chunk.getPos().getMinBlockX(), y + chunk.getMinBuildHeight(), z + chunk.getPos().getMinBlockZ());
                        if (shouldPlaceBlock) {
                            chunk.setBlockState(pos, this.settings.defaultBlock(), false);
                            break;
                        }
                    }

                }
            }
        }

        super.applyBiomeDecoration(level, chunk, structureManager);
    }

    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {}


    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {}
    public void applyCarvers(WorldGenRegion p_224166_, long seed, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess p_224171_, GenerationStep.Carving p_224172_) {}
    public void spawnOriginalMobs(WorldGenRegion pLevel) {}
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    public int getSpawnHeight(LevelHeightAccessor pLevel) {
        return pLevel.getMaxBuildHeight();
    }
    public int getMinY() {
        return -63;
    }
    public int getGenDepth() {
        return 384;
    }
    public int getSeaLevel() {
        return 150;
    }
}
