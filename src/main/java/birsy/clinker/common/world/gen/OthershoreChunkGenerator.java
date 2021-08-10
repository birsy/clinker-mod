package birsy.clinker.common.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class OthershoreChunkGenerator extends ChunkGenerator {
    private final long seed;
    private final SharedSeedRandom randomSeed;
    private final DimensionSettings dimensionSettings;
    private final NoiseSettings noiseSettings;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final INoiseGenerator surfaceDepthNoise;
    private final int worldHeight;
    
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();


    public OthershoreChunkGenerator(BiomeProvider biomeProvider, Supplier<DimensionSettings> dimensionSettings, long seed) {
        super(biomeProvider, dimensionSettings.get().getStructures());

        this.seed = seed;
        this.randomSeed = new SharedSeedRandom(this.seed);
        this.dimensionSettings = dimensionSettings.get();
        this.noiseSettings = this.dimensionSettings.getNoise();
        this.worldHeight = this.noiseSettings.func_236169_a_();
        this.defaultBlock = this.dimensionSettings.getDefaultBlock();
        this.defaultFluid = this.dimensionSettings.getDefaultFluid();
        this.surfaceDepthNoise = (INoiseGenerator) (this.noiseSettings.func_236178_i_() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
    }

    @Override
    public void generateSurface(WorldGenRegion genRegion, IChunk chunkIn) {
        ChunkPos chunkpos = chunkIn.getPos();
        int chunkPosX = chunkpos.x;
        int chunkPosZ = chunkpos.z;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setBaseChunkSeed(chunkPosX, chunkPosZ);

        int worldPosX = chunkpos.getXStart();
        int worldPosZ = chunkpos.getZStart();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(int chunkX = 0; chunkX < 16; ++chunkX) {
            for(int chunkZ = 0; chunkZ < 16; ++chunkZ) {
                int posX = worldPosX + chunkX;
                int posZ = worldPosZ + chunkZ;
                int startHeight = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, chunkX, chunkZ) + 1;
                double surfaceDepth = this.surfaceDepthNoise.noiseAt(posX * 0.0625D, posZ * 0.0625D, 0.0625D, chunkX * 0.0625D) * 15.0D;

                genRegion.getBiome(blockpos$mutable.setPos(worldPosX + chunkX, startHeight, worldPosZ + chunkZ)).buildSurface(sharedseedrandom, chunkIn, posX, posZ, startHeight, surfaceDepth, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), genRegion.getSeed());
            }
        }

        this.makeBedrock(chunkIn, sharedseedrandom);
    }

    private void makeBedrock(IChunk chunkIn, Random rand) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        int chunkX = chunkIn.getPos().getXStart();
        int chunkZ = chunkIn.getPos().getZStart();
        int bedrockFloorHeight = dimensionSettings.getBedrockFloorPosition();
        int bedrockRoofHeight = this.worldHeight - 1 - dimensionSettings.getBedrockRoofPosition();

        boolean shouldGenerateRoof = bedrockRoofHeight + 4 >= 0 && bedrockRoofHeight < this.worldHeight;
        boolean shouldGenerateFloor = bedrockFloorHeight + 4 >= 0 && bedrockFloorHeight < this.worldHeight;
        if (shouldGenerateRoof || shouldGenerateFloor) {
            for(BlockPos blockpos : BlockPos.getAllInBoxMutable(chunkX, 0, chunkZ, chunkX + 15, 0, chunkZ + 15)) {
                if (shouldGenerateRoof) {
                    for(int roofHeight = 0; roofHeight < 5; ++roofHeight) {
                        if (roofHeight <= rand.nextInt(5)) {
                            chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), bedrockRoofHeight - roofHeight, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                        }
                    }
                }

                if (shouldGenerateFloor) {
                    for(int floorDepth = 4; floorDepth >= 0; --floorDepth) {
                        if (floorDepth <= rand.nextInt(5)) {
                            chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), bedrockFloorHeight + floorDepth, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                        }
                    }
                }
            }
        }
    }

    public void fillFromNoise(IWorld worldIn, StructureManager structureManagerIn, IChunk chunkIn) {

    }

    public void createBiomes(Registry<Biome> biomeIn, IChunk chunkIn) {
        ChunkPos chunkpos = chunkIn.getPos();
        ((ChunkPrimer)chunkIn).setBiomes(new BiomeContainer(biomeIn, chunkpos, this.field_235949_c_));
    }

    public enum HeightType {
        PLATEAU,
        SURFACE,
        CANYON,
        WATER;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return 0;
    }

    public IBlockReader getBaseColumn(int x, int z) {
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
    public void func_230352_b_(IWorld worldIn, StructureManager structureManagerIn, IChunk chunkIn) { this.fillFromNoise(worldIn, structureManagerIn, chunkIn); }

    //getCodec
    @Override
    public Codec<? extends ChunkGenerator> func_230347_a_() {
        return this.getCodec();
    }

    //getChunkGeneratorWithSeed
    @Override
    public ChunkGenerator func_230349_a_(long seed) { return new OthershoreChunkGenerator(this.biomeProvider.getBiomeProvider(seed), () -> this.dimensionSettings, seed); }

    //getBaseColumn
    @Override
    public IBlockReader func_230348_a_(int x, int z) {
        return this.getBaseColumn(x, z);
    }

    //createBiomes
    @Override
    public void func_242706_a(Registry<Biome> biomeIn, IChunk chunkIn) { this.createBiomes(biomeIn, chunkIn); }
}
