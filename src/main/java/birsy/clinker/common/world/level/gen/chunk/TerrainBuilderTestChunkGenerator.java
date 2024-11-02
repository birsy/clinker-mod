package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.CubicSampler;
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

public class TerrainBuilderTestChunkGenerator extends ChunkGenerator implements LevelNoiseProvidable<TerrainBuilderTestChunkGenerator> {
    public static final Codec<TerrainBuilderTestChunkGenerator> CODEC = RecordCodecBuilder.create((codec) ->
            codec.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder))
                    .apply(codec, codec.stable(TerrainBuilderTestChunkGenerator::new)));

    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;

    private final NoiseSampler sampler;
    private NoiseFieldWithOffset noiseField;
    private final TerrainBuilder terrainBuilder;
    private final SurfaceBuilder surfaceBuilder;
    private long seed;

    public TerrainBuilderTestChunkGenerator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pBiomeSource);
        this.settingsHolder = settings;

        this.settings = this.settingsHolder.value();

        this.sampler = new NoiseSampler(0);
        this.noiseField = new InterpolatedNoiseField(16, this.getGenDepth(), 16, 4, 4, 4);
        this.terrainBuilder = new TerrainBuilder(4, (distance) -> (1.0F), sampler);
        this.surfaceBuilder = new SurfaceBuilder(8, 150, this.settings.defaultBlock(), sampler);
    }

    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> pStructureSetLookup, RandomState pRandomState, long pSeed) {
        seed = pSeed;
        this.sampler.setSeed(seed);
        return super.createState(pStructureSetLookup, pRandomState, pSeed);
    }

    private LevelAccessor noiseLevel;
    @Override
    public TerrainBuilderTestChunkGenerator provideLevel(LevelAccessor level) {
        noiseLevel = level;
        return this;
    }
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender densityBlender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        this.noiseField = new InterpolatedNoiseField(16, this.getGenDepth(), 16, 6, this.getGenDepth() / 4, 6);

        this.noiseField.setPosOffset(chunkPos.getMinBlockX(), chunk.getMinBuildHeight(), chunkPos.getMinBlockZ());

        this.terrainBuilder.populateNoiseField(noiseLevel, chunk, this.seed, this.noiseField);
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

        //this.updateHeightmaps(chunk);
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

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        //this.surfaceBuilder.applySurfaceDecorators(level, chunk, this.noiseField);
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
