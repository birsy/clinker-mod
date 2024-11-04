package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.BasicNoiseField;
import birsy.clinker.common.world.level.gen.InterpolatedNoiseField;
import birsy.clinker.common.world.level.gen.NoiseCache;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
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
import net.minecraft.world.level.*;
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
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TerrainBuilderTestChunkGenerator extends ChunkGenerator implements LevelNoiseProvidable {
    public static final Codec<TerrainBuilderTestChunkGenerator> CODEC = RecordCodecBuilder.create((codec) ->
            codec.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder))
                    .apply(codec, codec.stable(TerrainBuilderTestChunkGenerator::new)));

    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;

    private static final CachedFastNoise NOISE = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        n.SetFrequency(0.04F);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        n.SetFractalOctaves(1);
        n.SetFractalLacunarity(0.5F);
        n.SetFractalGain(1.7F);
        n.SetFractalWeightedStrength(0.0F);
        return new CachedFastNoise(n);
    });
    private static final VoronoiGenerator VORONOI = new VoronoiGenerator(0);

    public TerrainBuilderTestChunkGenerator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pBiomeSource);
        this.settingsHolder = settings;

        this.settings = this.settingsHolder.value();

        //NOISEField = new InterpolatedNoiseField(16, 256, 16, 4, 256 / 4, 4);
        //this.sampler = new NoiseCache(noiseField, 0);
        //this.terrainBuilder = new TerrainBuilder(4, (distance) -> (1.0F), sampler);
        //this.surfaceBuilder = new SurfaceBuilder(8, 150, this.settings.defaultBlock(), sampler);
    }

    private LevelAccessor noiseLevel;
    private long seed;
    @Override
    public void provideLevelAndSeed(LevelAccessor level, long seed) {
        this.noiseLevel = level;
        this.seed = seed;
    }
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender densityBlender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {

        ChunkPos chunkPos = chunk.getPos();
        InterpolatedNoiseField noiseField = new InterpolatedNoiseField(16, 256, 16, 6, 256 / 6, 6);
        NoiseCache sampler = new NoiseCache(noiseField, 0);
        TerrainBuilder terrainBuilder = new TerrainBuilder(4, (distance) -> (1.0F), sampler);

        noiseField.setPosOffset(chunkPos.getMinBlockX(), chunk.getMinBuildHeight(), chunkPos.getMinBlockZ());
        terrainBuilder.populateNoiseField(noiseLevel, chunk, this.seed, noiseField);
        noiseField.fill((currentValue, x, y, z, params) -> (float) MathUtils.smoothMinExpo(sampleCaveNoise(x, y, z), currentValue, 0.05F));
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMaxBuildHeight(); y >= chunk.getMinBuildHeight(); y--) {
                    pos.set(x + chunkPos.getMinBlockX(), y, z + chunkPos.getMinBlockZ());
                    float noiseVal = noiseField.getValue(x + chunkPos.getMinBlockX(), y, z + chunkPos.getMinBlockZ());
                    if (noiseVal > 0) {
                        chunk.setBlockState(pos, this.settings.defaultBlock(), false);
                    } else {
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                    }
                }
            }
        }
        this.updateHeightmaps(chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    void updateHeightmaps(ChunkAccess chunk) {
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

    public float sampleCaveNoise(double x, double y, double z) {
        int maxHeight = 240;
        int minHeight = 64;

        double aHFreq = 1.0;
        double aquiferMidHeight  = -40;
        double aquiferUpperRange = aquiferMidHeight + MathUtils.mapRange(-1.0F, 1.0F, 10.0F, 45.0F, NOISE.get(x * aHFreq, z * aHFreq));
        double aquiferLowerRange = aquiferMidHeight - 20;

        double seaLevel = this.getSeaLevel();
        double caveClamping = Mth.clamp(MathUtils.mapRange(aquiferUpperRange - 10, aquiferUpperRange + 15, 0, 1, y), 0, 1);
        caveClamping *= Mth.clamp(MathUtils.mapRange(seaLevel - 20, seaLevel + 5, 1, 0.2, y), 0, 1);

        //Stalagmite Noise;
        double stalagHFreq = (1.0 / 16.0);
        double stalagVFreq = (1.0 / 34.0);
        VORONOI.setOffsetAmount(1.0F);
        VoronoiGenerator.VoronoiInfo stalagInfo = VORONOI.get3(x * stalagHFreq, y * stalagVFreq, z * stalagHFreq);
        Vec3 stalagVec = stalagInfo.localPos();
        double yFac = ((Math.abs(stalagVec.y()) + 0.5));
        yFac *= yFac;
        double stalag = stalagVec.multiply(1, 0, 1).length() * yFac;
        double stalagThreshold = MathUtils.map(0.5, 1.0, MathUtils.bias(stalagInfo.hash(), 0.8F)) * Mth.sqrt(3) * stalagHFreq;
        stalag -= stalagThreshold;
        stalag *= -1;

        //Spaghetti Cave Noise
        double sFreq = 0.4;
        double sHFreq = sFreq * 0.5;
        double sVFreq = sFreq * 1.3;

        double sHFreq2 = 0.8;
        double sCave1 = NOISE.get(x * sHFreq * sHFreq2, y * sVFreq, z * sHFreq * sHFreq2);
        double sCave2 = NOISE.get(x * sHFreq, y * sVFreq + 3000.0, z * sHFreq);
        double sCave = sCave1 * sCave1 + sCave2 * sCave2;
        double threshold = 0.06 * caveClamping;

        sCave -= threshold;

        //Aquifer Cave Noise
        double aCave = 1.0;
        if (y < aquiferUpperRange + 10) {
            double aFreq = 6.0;
            double altNoise = NOISE.get(x * aFreq, z * aFreq);

            double aTopThres = MathUtils.mapRange(aquiferLowerRange, aquiferMidHeight, 0.0F, 1.0F, y);
            double aquiferSizeThrottling = y < aquiferMidHeight ?
                    Mth.lerp(0.0, MathUtils.ease((float) aTopThres, MathUtils.EasingType.easeOutBounce), MathUtils.ease((float) aTopThres, MathUtils.EasingType.easeOutQuad)) :
                    Mth.lerp(MathUtils.bias((altNoise + 1) / 2, 0.2), MathUtils.ease((float) MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutQuad),
                            MathUtils.ease((float) MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutBounce));
            double aquiferSizeThreshold = 0.5 * aquiferSizeThrottling;

            double aquiferCaveNoiseValue = NOISE.get(x, z);
            aCave = y > aquiferUpperRange ? 1 : y < aquiferLowerRange ? 1 : (aquiferCaveNoiseValue * aquiferCaveNoiseValue) - aquiferSizeThreshold;
        }

        //double[] bridgeCavern = sampleBridgeCaveNoise(x, y, z, seed);
        double bCave = 1.0F;//bridgeCavern[0];
        double bCaveShell = -1.0F;//bridgeCavern[1];

        return (float) MathUtils.smoothMinExpo(MathUtils.smoothMinExpo(sCave, stalag, -0.05) + Math.max(bCaveShell * 0.8, 0), bCave, 0.06);//MathUtils.smoothMinExpo(stalag, sampleBridgeCaveNoise(x, y, z, seed), -0.05);//MathUtils.smoothMinExpo(aCave, MathUtils.smoothMinExpo(sCave, stalag, -0.1), 0.06);
    }
    
    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        SurfaceBuilder surfaceBuilder = new SurfaceBuilder(8, 150, this.settings.defaultBlock());
        surfaceBuilder.applySurfaceDecorators(level, chunk, new BasicNoiseField(1,1,1));
        super.applyBiomeDecoration(level, chunk, structureManager);
    }
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor chunk, RandomState random) {
        BlockState[] states = new BlockState[chunk.getHeight()];
        int iY = 0;
        for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            states[iY] = this.settings.defaultBlock();
            iY++;
        }

        return new NoiseColumn(chunk.getMinBuildHeight(), states);
    }

    public int getBaseHeight(int x, int y, Heightmap.Types heightmap, LevelHeightAccessor level, RandomState random) { return 224;}
    public int getMinY() {
        return -63;
    }
    public int getGenDepth() {
        return 384;
    }
    public int getSeaLevel() {
        return 150;
    }
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    public int getSpawnHeight(LevelHeightAccessor pLevel) {
        return pLevel.getMaxBuildHeight();
    }
    public void applyCarvers(WorldGenRegion level, long seed, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess chunk, GenerationStep.Carving p_224172_) {}
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {}
    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {}
    public void spawnOriginalMobs(WorldGenRegion pLevel) {}

    public static void register() {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(Clinker.MOD_ID, "terrain_builder_test_chunk_generator"), CODEC);
    }


}
