package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.chunk.biome.SurfaceDecorator;
import birsy.clinker.common.world.level.gen.chunk.biome.SurfaceDecorators;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.noise.CachedFastNoise;
import birsy.clinker.core.util.noise.FastNoiseLite;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TestChunkGenerator extends ChunkGenerator {
    public static final Codec<TestChunkGenerator> CODEC = RecordCodecBuilder.create((codec) ->
            codec.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder))
                    .apply(codec, codec.stable(TestChunkGenerator::new)));

    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;
    private final Aquifer.FluidPicker globalFluidPicker;

    private static CachedFastNoise noise = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        n.SetFrequency(0.04F);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        n.SetFractalOctaves(1);
        n.SetFractalLacunarity(0.5);
        n.SetFractalGain(1.7F);
        n.SetFractalWeightedStrength(0.0F);
        return new CachedFastNoise(n);
    });
    private VoronoiGenerator voronoi;
    private static long seed;
    private float[][][] terrainShapeSamplePoints;

    public TestChunkGenerator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.value();
        this.globalFluidPicker = (a, b, c) -> new Aquifer.FluidStatus(this.settings.seaLevel(), this.settings.defaultFluid());

        this.voronoi = new VoronoiGenerator(0);
    }
    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> pStructureSetLookup, RandomState pRandomState, long pSeed) {
        seed = pSeed;
        return super.createState(pStructureSetLookup, pRandomState, pSeed);
    }
    private void setNoiseSeed(int seed) {
        if (seed != this.noise.getNoise().GetSeed()) {
            this.noise.getNoise().SetSeed(seed);
            this.noise.invalidateCache();
        }
        if (seed != this.voronoi.getSeed()) {
            this.voronoi.setSeed(seed);
        }
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender densityBlender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        //fillNoiseSampleArrays(chunk);
        Heightmap[] heightmaps = {chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG), chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG)};
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMaxBuildHeight(); y >= chunk.getMinBuildHeight(); y--) {
                    pos.set(x, y, z);
                    float sample = sampleDensity(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());//sampleDensityFromArray(terrainShapeSamplePoints, x, y, z);

                    BlockState state;
                    if (sample > 0) {
                        state = settings.defaultBlock();
                    } else {
                        state = Blocks.AIR.defaultBlockState();// y > this.getSeaLevel() ? Blocks.AIR.defaultBlockState() : Blocks.WATER.defaultBlockState();
                    }

                    for (Heightmap heightmap : heightmaps) {
                        heightmap.update(x, y, z, state);
                    }

                    chunk.setBlockState(pos, state, false);
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
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

    private static final int MAX_BLOCKS = 4;

    private void applySurfaceDecorators(WorldGenLevel level, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int offset = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                //when it encounters a new surface, check the biome and generate the corresponding surface.
                int startHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x + offset, z + offset);
                pos.set(x + chunk.getPos().getMinBlockX() + offset, startHeight, z + chunk.getPos().getMinBlockZ() + offset);

                boolean isInSolid = false;
                boolean visibleToSun = true;
                while (pos.getY() > chunk.getMinBuildHeight() + 5) {
                    if (level.getBlockState(pos).isCollisionShapeFullBlock(level, pos)) {
                        if (!isInSolid) {
                            ResourceLocation biome = level.getBiome(pos).unwrapKey().get().location();
                            SurfaceDecorator decorator = SurfaceDecorators.getSurfaceDecorator(biome);

                            // probably really slow. too bad!
                            int maxElevationIncrease = 0;
                            int maxElevationDecrease = 0;
                            directions:
                            for (Direction direction : Direction.Plane.HORIZONTAL) {
                                BlockPos.MutableBlockPos neighborPos = pos.immutable().mutable().move(direction);
                                boolean movingUp = level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                Direction moveDirection = movingUp ? Direction.UP : Direction.DOWN;

                                for (int i = 0; i < MAX_BLOCKS + 1; i++) {
                                    neighborPos.move(moveDirection);

                                    // no blocks will return a greater difference in height than this, can safely return out.
                                    if (i == MAX_BLOCKS) {
                                        if (movingUp) { maxElevationIncrease = MAX_BLOCKS; }
                                        else { maxElevationDecrease = MAX_BLOCKS; }
                                        break;
                                    }

                                    boolean isTop = movingUp ? !level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos) : level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                    if (isTop) {
                                        if (movingUp) { maxElevationIncrease = Math.max(maxElevationIncrease, i); }
                                        else { maxElevationDecrease = Math.max(maxElevationDecrease, i + 1); }
                                        break;
                                    }
                                }
                            }

                            //decorator.setSeed(level.getSeed());
                            decorator.buildSurface(pos, this.getSeaLevel(), visibleToSun, 0, maxElevationIncrease, maxElevationDecrease, chunk, this.settings);
                            isInSolid = true;
                            visibleToSun = false;
                        }
                    } else {
                        isInSolid = false;
                    }

                    pos.move(Direction.DOWN);

                }
            }
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_) {
        this.applySurfaceDecorators(p_223087_, p_223088_);
        super.applyBiomeDecoration(p_223087_, p_223088_, p_223089_);
    }

    // we don't actually use this for surface decorators since they don't have access to the neighboring chunks.
    // instead, use applyBiomeDecoration.
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
    }

    public void fillNoiseSampleArrays(ChunkAccess chunk) {
        int hSamplePoints = (int) Math.ceil(16 * 0.3F);
        int vSamplePoints = (int) Math.ceil(this.getGenDepth() * 0.15F);

        float hOffset = (16.0F / (float) hSamplePoints);
        float vOffset = ((float)this.getGenDepth() / (float) vSamplePoints);

        this.terrainShapeSamplePoints = new float[hSamplePoints + 1][vSamplePoints][hSamplePoints + 1];
        for (int sX = 0; sX < hSamplePoints + 1; sX++) {
            for (int sZ = 0; sZ < hSamplePoints + 1; sZ++) {
                for (int sY = 0; sY < vSamplePoints; sY++) {

                    float cX = sX * hOffset;
                    float cY = sY * vOffset;
                    float cZ = sZ * hOffset;

                    float x = cX + chunk.getPos().getMinBlockX();
                    float z = cZ + chunk.getPos().getMinBlockZ();
                    float y = cY + chunk.getMinBuildHeight();

                    terrainShapeSamplePoints[sX][sY][sZ] = sampleDensity(x, y, z);
                }
            }
        }
    }

    public float sampleDensityFromArray(float[][][] densityArray, int localX, int localY, int localZ) {
        int maxXZ = 16;
        int maxY = this.getGenDepth();
        float xzSampleRes = (float)(densityArray.length-1) / (float)maxXZ;
        float ySampleRes =  (float)(densityArray[0].length-1) / (float)maxY;

        float sampleX = localX * xzSampleRes;
        float sxFrac = Mth.frac(sampleX);
        int sxF = Mth.floor(sampleX);
        int sxC = Mth.ceil(sampleX);
        float sampleY = (localY - this.getMinY()) * ySampleRes;
        float syFrac = Mth.frac(sampleY);
        int syF = Mth.floor(sampleY);
        int syC = Mth.ceil(sampleY);
        float sampleZ = localZ * xzSampleRes;
        float szFrac = Mth.frac(sampleZ);
        int szF = Mth.floor(sampleZ);
        int szC = Mth.ceil(sampleZ);

        float xLerp1 = Mth.lerp(sxFrac, densityArray[sxF][syF][szF], densityArray[sxC][syF][szF]);
        float xLerp2 = Mth.lerp(sxFrac, densityArray[sxF][syF][szC], densityArray[sxC][syF][szC]);
        float zLerp1 = Mth.lerp(szFrac, xLerp1, xLerp2);

        float xLerp3 = Mth.lerp(sxFrac, densityArray[sxF][syC][szF], densityArray[sxC][syC][szF]);
        float xLerp4 = Mth.lerp(sxFrac, densityArray[sxF][syC][szC], densityArray[sxC][syC][szC]);
        float zLerp2 = Mth.lerp(szFrac, xLerp3, xLerp4);

        float yLerp = Mth.lerp(Mth.lerp(Mth.clamp((float)(localY - this.getSeaLevel()) / 64F, 0F, 1F), syFrac * syFrac * syFrac, syFrac), zLerp1, zLerp2);

        return yLerp;
    }

    private float sampleDensity(float x, float y, float z) {
        float heightFactor = 0.1F;

        float freq = 0.8F;
        float noise = (float) this.noise.get(x * freq, y * freq, z * freq);
        noise = noise * noise * Mth.sign(noise);
        return (noise) - (((y - this.getSeaLevel())) * heightFactor);
    }

    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {}
    public void applyCarvers(WorldGenRegion p_224166_, long seed, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess p_224171_, GenerationStep.Carving p_224172_) {}
    public void spawnOriginalMobs(WorldGenRegion pLevel) {}
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    private NoiseChunk createNoiseChunk(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState random) {
        return NoiseChunk.forChunk(chunk, random, Beardifier.forStructuresInChunk(structureManager, chunk.getPos()), this.settings, this.globalFluidPicker, blender);
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
        return 100;
    }

    public static void register() {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(Clinker.MOD_ID, "test_chunk_generator"), CODEC);
    }
}
