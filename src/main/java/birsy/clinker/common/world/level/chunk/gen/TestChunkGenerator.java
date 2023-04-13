package birsy.clinker.common.world.level.chunk.gen;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.CachedFastNoise;
import birsy.clinker.core.util.noise.FastNoiseLite;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TestChunkGenerator extends ChunkGenerator {
    public static final Codec<TestChunkGenerator> CODEC = RecordCodecBuilder.create((codec) -> commonCodec(codec).and(codec.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder)))
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

    public TestChunkGenerator(Registry<StructureSet> pStructureSets, BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pStructureSets, Optional.empty(), pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.get();
        this.globalFluidPicker = (a, b, c) -> new Aquifer.FluidStatus(this.settings.seaLevel(), this.settings.defaultFluid());

        this.voronoi = new VoronoiGenerator(0);
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
        long startTime = System.currentTimeMillis();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Heightmap[] heightmaps = {chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG), chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG)};
        this.setNoiseSeed((int) random.legacyLevelSeed());
        this.voronoi.setOffsetAmount(1.3F);

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = chunk.getMaxBuildHeight(); y > chunk.getMinBuildHeight(); y--) {
                    pos.set(x, y, z);
                    BlockState state = getBlockStateAtPos(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ(), random.legacyLevelSeed());
                    chunk.setBlockState(pos, state, false);

                    for (Heightmap heightmap : heightmaps) {
                        heightmap.update(x, y, z, state);
                    }
                }
            }
        }
        //Clinker.LOGGER.info("generationTime = " + (((System.currentTimeMillis() - startTime) / 1000.0) * 20));
        return CompletableFuture.completedFuture(chunk);
    }

    public BlockState getBlockStateAtPos(int x, int y, int z, long seed) {
        double offsetScale = 1.0F;
        double offset = this.noise.get(x * offsetScale, y * offsetScale, z * offsetScale);
        offset *= 0.3;

        this.voronoi.setOffsetAmount(1.3F);
        y -= this.getSeaLevel();
        double voronoiScale = 38;
        double voronoiYScale = 12;
        VoronoiGenerator.VoronoiInfo voroninfo = this.voronoi.get3(((double)x / voronoiScale) + offset, ((double)y / voronoiYScale) + offset, ((double)z / voronoiScale) + offset);
        double newY = voroninfo.cellPos().y * voronoiYScale;

        double erosion = 0.15;
        newY = Mth.lerp(erosion, newY, y);
        double scale = 0.15;
        double verticalScale = 0.8;
        double noiseTerraced = this.noise.get(x * scale, newY * scale * verticalScale, z * scale);
        noiseTerraced = MathUtils.biasTowardsExtreme(noiseTerraced, 1.0, 1);

        double noise = this.noise.get(x * scale, y * scale * verticalScale, z * scale);
        noise = MathUtils.biasTowardsExtreme(noise, 1.0, 1);
        double duneNoise = this.noise.getNoise().GetNoise(x, z);
        duneNoise = (((1 - Math.abs(duneNoise)) * 2) - 1);
        noise *= 60;
        noise += duneNoise * 3;
        noise -= 3;
        noise -= y;

        double terrain = noiseTerraced * 60;
        terrain -= newY;

        return terrain > 0 ? settings.defaultBlock() : (noise > 0 ? ClinkerBlocks.ASH.get().defaultBlockState() : Blocks.AIR.defaultBlockState());
    }



    public int getBaseHeight(int x, int y, Heightmap.Types heightmap, LevelHeightAccessor level, RandomState random) {
        return 224;
    }
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor chunk, RandomState random) {
        BlockState[] states = new BlockState[chunk.getHeight()];
        int iY = 0;
        for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            states[iY] = getBlockStateAtPos(x, y, z, random.legacyLevelSeed());
            iY++;
        }

        return new NoiseColumn(chunk.getMinBuildHeight(), states);
    }
    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {}
    public void applyCarvers(WorldGenRegion p_224166_, long seed, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess p_224171_, GenerationStep.Carving p_224172_) {}
    public void spawnOriginalMobs(WorldGenRegion pLevel) {}
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    public void buildSurface(WorldGenRegion genRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        if (!SharedConstants.debugVoidTerrain(chunk.getPos())) {
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, genRegion);
            this.buildSurface(chunk, worldgenerationcontext, randomState, structureManager, genRegion.getBiomeManager(), genRegion.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), Blender.of(genRegion));
        }
    }
    public void buildSurface(ChunkAccess chunk, WorldGenerationContext context, RandomState randomState, StructureManager structure, BiomeManager biomeManager, Registry<Biome> biomes, Blender noiseBlender) {
        NoiseChunk noisechunk = chunk.getOrCreateNoiseChunk((chunkGenerator) -> this.createNoiseChunk(chunkGenerator, structure, noiseBlender, randomState));
        NoiseGeneratorSettings noisegeneratorsettings = this.settings;
        randomState.surfaceSystem().buildSurface(randomState, biomeManager, biomes, noisegeneratorsettings.useLegacyRandomSource(), context, chunk, noisechunk, noisegeneratorsettings.surfaceRule());
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
        return 224;
    }

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Clinker.MOD_ID, "test_chunk_generator"), CODEC);
    }
}
