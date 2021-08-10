package birsy.clinker.common.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.gen.*;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

import BlockState;
import NoiseGeneratorSettings;
import NoiseSettings;
import SurfaceNoise;
import WorldgenRandom;

public class OthershoreChunkGenerator extends ChunkGenerator {
    private final long seed;
    private final WorldgenRandom randomSeed;
    private final NoiseGeneratorSettings dimensionSettings;
    private final NoiseSettings noiseSettings;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final SurfaceNoise surfaceDepthNoise;
    private final int worldHeight;
    
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();


    public OthershoreChunkGenerator(BiomeSource biomeProvider, Supplier<NoiseGeneratorSettings> dimensionSettings, long seed) {
        super(biomeProvider, dimensionSettings.get().structureSettings());

        this.seed = seed;
        this.randomSeed = new WorldgenRandom(this.seed);
        this.dimensionSettings = dimensionSettings.get();
        this.noiseSettings = this.dimensionSettings.noiseSettings();
        this.worldHeight = this.noiseSettings.height();
        this.defaultBlock = this.dimensionSettings.getDefaultBlock();
        this.defaultFluid = this.dimensionSettings.getDefaultFluid();
        this.surfaceDepthNoise = (SurfaceNoise) (this.noiseSettings.useSimplexSurfaceNoise() ? new PerlinSimplexNoise(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new PerlinNoise(this.randomSeed, IntStream.rangeClosed(-3, 0)));
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion genRegion, ChunkAccess chunkIn) {
        ChunkPos chunkpos = chunkIn.getPos();
        int chunkPosX = chunkpos.x;
        int chunkPosZ = chunkpos.z;
        WorldgenRandom sharedseedrandom = new WorldgenRandom();
        sharedseedrandom.setBaseChunkSeed(chunkPosX, chunkPosZ);

        int worldPosX = chunkpos.getMinBlockX();
        int worldPosZ = chunkpos.getMinBlockZ();
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for(int chunkX = 0; chunkX < 16; ++chunkX) {
            for(int chunkZ = 0; chunkZ < 16; ++chunkZ) {
                int posX = worldPosX + chunkX;
                int posZ = worldPosZ + chunkZ;
                int startHeight = chunkIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkX, chunkZ) + 1;
                double surfaceDepth = this.surfaceDepthNoise.getSurfaceNoiseValue(posX * 0.0625D, posZ * 0.0625D, 0.0625D, chunkX * 0.0625D) * 15.0D;

                genRegion.getBiome(blockpos$mutable.set(worldPosX + chunkX, startHeight, worldPosZ + chunkZ)).buildSurfaceAt(sharedseedrandom, chunkIn, posX, posZ, startHeight, surfaceDepth, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), genRegion.getSeed());
            }
        }

        this.makeBedrock(chunkIn, sharedseedrandom);
    }

    private void makeBedrock(ChunkAccess chunkIn, Random rand) {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        int chunkX = chunkIn.getPos().getMinBlockX();
        int chunkZ = chunkIn.getPos().getMinBlockZ();
        int bedrockFloorHeight = dimensionSettings.getBedrockFloorPosition();
        int bedrockRoofHeight = this.worldHeight - 1 - dimensionSettings.getBedrockRoofPosition();

        boolean shouldGenerateRoof = bedrockRoofHeight + 4 >= 0 && bedrockRoofHeight < this.worldHeight;
        boolean shouldGenerateFloor = bedrockFloorHeight + 4 >= 0 && bedrockFloorHeight < this.worldHeight;
        if (shouldGenerateRoof || shouldGenerateFloor) {
            for(BlockPos blockpos : BlockPos.betweenClosed(chunkX, 0, chunkZ, chunkX + 15, 0, chunkZ + 15)) {
                if (shouldGenerateRoof) {
                    for(int roofHeight = 0; roofHeight < 5; ++roofHeight) {
                        if (roofHeight <= rand.nextInt(5)) {
                            chunkIn.setBlockState(blockpos$mutable.set(blockpos.getX(), bedrockRoofHeight - roofHeight, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                        }
                    }
                }

                if (shouldGenerateFloor) {
                    for(int floorDepth = 4; floorDepth >= 0; --floorDepth) {
                        if (floorDepth <= rand.nextInt(5)) {
                            chunkIn.setBlockState(blockpos$mutable.set(blockpos.getX(), bedrockFloorHeight + floorDepth, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                        }
                    }
                }
            }
        }
    }

    public void fillFromNoise(LevelAccessor worldIn, StructureFeatureManager structureManagerIn, ChunkAccess chunkIn) {

    }

    public void createBiomes(Registry<Biome> biomeIn, ChunkAccess chunkIn) {
        ChunkPos chunkpos = chunkIn.getPos();
        ((ProtoChunk)chunkIn).setBiomes(new ChunkBiomeContainer(biomeIn, chunkpos, this.runtimeBiomeSource));
    }

    public enum HeightType {
        PLATEAU,
        SURFACE,
        CANYON,
        WATER;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmapType) {
        return 0;
    }

    public BlockGetter getBaseColumn(int x, int z) {
        return null;
    }

    public Codec<? extends ChunkGenerator> getCodec() {
        return null;
    }

    /**
     * Unmapped stuff. DO NOT TOUCH!
     */

    @Override
    //fillFromNoise
    public void fillFromNoise(IWorld worldIn, StructureManager structureManagerIn, IChunk chunkIn) { this.fillFromNoise(worldIn, structureManagerIn, chunkIn); }

    //getCodec
    @Override
    public Codec<? extends ChunkGenerator> codec() {
        return this.getCodec();
    }

    //getChunkGeneratorWithSeed
    @Override
    public ChunkGenerator withSeed(long seed) { return new OthershoreChunkGenerator(this.biomeSource.withSeed(seed), () -> this.dimensionSettings, seed); }

    //getBaseColumn
    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        return this.getBaseColumn(x, z);
    }

    //createBiomes
    @Override
    public void createBiomes(Registry<Biome> biomeIn, IChunk chunkIn) { this.createBiomes(biomeIn, chunkIn); }
}
