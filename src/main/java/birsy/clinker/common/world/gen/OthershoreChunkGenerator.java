package birsy.clinker.common.world.gen;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class OthershoreChunkGenerator extends ChunkGenerator {
    private final long seed;
    private final SharedSeedRandom randomSeed;
    private final DimensionSettings dimensionSettings;
    private final NoiseSettings noiseSettings;
    private final OthershoreNoiseSampler othershoreNoiseSampler;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final INoiseGenerator surfaceDepthNoise;
    private final int worldHeight;

    private FastNoiseLite heightmapNoiseGenerator;
    private FastNoiseLite terraceNoiseGenerator;
    private FastNoiseLite erosionNoiseGenerator;

    private FastNoiseLite overhangNoiseGenerator;

    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    public OthershoreChunkGenerator(BiomeProvider biomeProvider, Supplier<DimensionSettings> dimensionSettings, long seed) {
        super(biomeProvider, dimensionSettings.get().getStructures());

        this.seed = seed;
        this.randomSeed = new SharedSeedRandom(this.seed);
        this.dimensionSettings = dimensionSettings.get();
        this.noiseSettings = this.dimensionSettings.getNoise();
        this.othershoreNoiseSampler = new OthershoreNoiseSampler((int) this.seed);
        this.worldHeight = this.noiseSettings.func_236169_a_();
        this.defaultBlock = this.dimensionSettings.getDefaultBlock();
        this.defaultFluid = this.dimensionSettings.getDefaultFluid();
        this.surfaceDepthNoise = this.noiseSettings.func_236178_i_() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0));

        this.initNoise((int) seed);
    }

    private void initNoise(int seed) {
        this.heightmapNoiseGenerator = new FastNoiseLite((int) (seed + randomSeed.nextLong()));
        this.heightmapNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.heightmapNoiseGenerator.SetFrequency(0.04F);
        this.heightmapNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.heightmapNoiseGenerator.SetFractalOctaves(2);
        this.heightmapNoiseGenerator.SetFractalGain(0.9F);
        this.heightmapNoiseGenerator.SetFractalWeightedStrength(0.8F);
        this.heightmapNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.heightmapNoiseGenerator.SetDomainWarpAmp(100.0F);
        this.heightmapNoiseGenerator.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.terraceNoiseGenerator = new FastNoiseLite((int) (seed + randomSeed.nextLong()));
        this.terraceNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.terraceNoiseGenerator.SetFrequency(0.01F);
        this.terraceNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);
        this.terraceNoiseGenerator.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.erosionNoiseGenerator = new FastNoiseLite((int) (seed + randomSeed.nextLong()));
        this.erosionNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.erosionNoiseGenerator.SetFrequency(0.02F);
        this.erosionNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);
        this.erosionNoiseGenerator.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.overhangNoiseGenerator = new FastNoiseLite((int) (seed + randomSeed.nextLong()));
        this.overhangNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.overhangNoiseGenerator.SetFrequency(0.04F);
        this.overhangNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.overhangNoiseGenerator.SetFractalOctaves(4);
        this.overhangNoiseGenerator.SetFractalGain(0.9F);
        this.overhangNoiseGenerator.SetFractalWeightedStrength(0.9F);
        this.overhangNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.overhangNoiseGenerator.SetDomainWarpAmp(100.0F);
        this.overhangNoiseGenerator.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);
    }

    private float sampleNoise(int x, int y, int z) {
        Pair<Float, Float> surfaceNoise = sampleTerracedSurfaceNoise(x, y, z);
        float overhangNoise = overhangNoiseGenerator.GetNoise(x, y, z);
        float overhangNoiseIntensity = surfaceNoise.second * MathUtils.invert(MathHelper.clamp(Math.abs(y - surfaceNoise.first) * 0.125F, 0, 1));

        return surfaceNoise.first * (overhangNoise * overhangNoiseIntensity);
    }

    //This base layer will return a distance from Y. This is later modulated with some funky shit to allow overhangs, if only slightly. Should work!
    private Pair<Float, Float> sampleTerracedSurfaceNoise(int x, int y, int z) {
        float baseNoise = (heightmapNoiseGenerator.GetNoise(x, z) + 1) * 0.5F;
        float terraceNoise = MathUtils.mapRange(-1, 1, 0.07F, 0.6F, (float) terraceNoiseGenerator.GetNoise(x, z));
        float erosionNoise = MathUtils.mapRange(-1, 1, 0.05F, 0.5F, (float) erosionNoiseGenerator.GetNoise(x, z));

        Pair<Float, Float> terrace = MathUtils.terrace(baseNoise, terraceNoise, erosionNoise);
        float final2DNoise = (terrace.first * 2) - 1;
        float terrainHeight = (final2DNoise * 70) + 55;

        return Pair.of((terrainHeight - y) * 0.05F, terrace.second);
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

        float[][] heightmap = new float[16][16];
        for(int chunkX = 0; chunkX < 16; ++chunkX) {
            for (int chunkZ = 0; chunkZ < 16; ++chunkZ) {
                heightmap[chunkX][chunkZ] = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, chunkX, chunkZ) - this.getSeaLevel();
            }
        }

        for(int chunkX = 0; chunkX < 16; ++chunkX) {
            for(int chunkZ = 0; chunkZ < 16; ++chunkZ) {
                int posX = worldPosX + chunkX;
                int posZ = worldPosZ + chunkZ;
                int startHeight = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, chunkX, chunkZ) + 1;
                double surfaceDepth = this.surfaceDepthNoise.noiseAt(posX * 0.0625D, posZ * 0.0625D, 0.0625D, chunkX * 0.0625D) * 15.0D;

                //Biome biome = genRegion.getBiome(blockpos$mutable.setPos(worldPosX + chunkX, startHeight, worldPosZ + chunkZ));
                //ConfiguredSurfaceBuilder<> surfaceBuilder = biome.biomeGenerationSettings.getSurfaceBuilder().get();
                genRegion.getBiome(blockpos$mutable.setPos(worldPosX + chunkX, startHeight, worldPosZ + chunkZ)).buildSurface(sharedseedrandom, chunkIn, posX, posZ, startHeight, surfaceDepth, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), genRegion.getSeed());
            }
        }

        this.makeBedrock(chunkIn, sharedseedrandom);
    }



    public void carveBlocks(long seed, BiomeManager biomeManager, IChunk chunk, GenerationStage.Carving carvingStage) {

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
        ChunkPos chunkpos = chunkIn.getPos();
        int chunkX = chunkpos.x;
        int chunkZ = chunkpos.z;
        int posX = chunkX << 4;
        int posZ = chunkZ << 4;

        ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
        for(Structure<?> structure : Structure.field_236384_t_) {
            structureManagerIn.func_235011_a_(SectionPos.from(chunkpos, 0), structure).forEach((structureStarts) -> {
                for(StructurePiece structurepiece1 : structureStarts.getComponents()) {
                    if (structurepiece1.func_214810_a(chunkpos, 12)) {
                        if (structurepiece1 instanceof AbstractVillagePiece) {
                            AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece)structurepiece1;
                            JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getJigsawPiece().getPlacementBehaviour();
                            if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                                objectlist.add(abstractvillagepiece);
                            }

                            for(JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
                                int l5 = jigsawjunction1.getSourceX();
                                int i6 = jigsawjunction1.getSourceZ();
                                if (l5 > posX - 12 && i6 > posZ - 12 && l5 < posX + 15 + 12 && i6 < posZ + 15 + 12) {
                                    objectlist1.add(jigsawjunction1);
                                }
                            }
                        } else {
                            objectlist.add(structurepiece1);
                        }
                    }
                }
            });
        }
    }

    public void createBiomes(Registry<Biome> biomeIn, IChunk chunkIn) { }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return 0;
    }

    public IBlockReader getBaseColumn(int x, int z) {
        BlockState[] column = new BlockState[256];
        return new Blockreader(column);
    }

    private int sampleHeight(int x, int z, @Nullable BlockState[] column) {
        return 1;
    }

    public Codec<? extends ChunkGenerator> getCodec() {
        return null;
    }

    public static class OthershoreNoiseSampler {
        private final int seed;
        private Random random;

        //Noise determining where ash plains and oceans lie. Very, very large, sampled in 2D.
        private FastNoiseLite continentalNoiseGenerator;
        //Noise determining the basic height of the terrain. Ridged, sampled in 2D.
        private FastNoiseLite heightmapNoiseGenerator;
        //Noise determining the 3D terrain shape. Ridged, sampled in 3D.
        private FastNoiseLite terrainNoiseGenerator;
        //Noise determining where mountain peaks and river valleys lie. Ridged, sampled in 2D.
        private FastNoiseLite peakValleyNoiseGenerator;
        //Noise determining erosion levels for terrain generation. Sampled in 2D.
        private FastNoiseLite erosionNoiseGenerator;
        //Noise determining how much influence 3D noise has over the terrain. Large, sampled in 3D.
        private FastNoiseLite factorNoiseGenerator;

        public OthershoreNoiseSampler(int seed) {
            this.seed = seed;
            this.random = new Random(seed);

            this.initNoise(seed);
        }

        private void initNoise(int seed) {
            this.continentalNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
            this.continentalNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
            this.continentalNoiseGenerator.SetFrequency(0.002F);
            this.continentalNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);

            this.heightmapNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
            this.heightmapNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
            this.heightmapNoiseGenerator.SetFrequency(0.04F);
            this.heightmapNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
            this.heightmapNoiseGenerator.SetFractalOctaves(2);
            this.heightmapNoiseGenerator.SetFractalGain(0.9F);
            this.heightmapNoiseGenerator.SetFractalWeightedStrength(0.8F);
            this.heightmapNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
            this.heightmapNoiseGenerator.SetDomainWarpAmp(100.0F);

            this.terrainNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
            this.terrainNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
            this.terrainNoiseGenerator.SetFrequency(0.03F);
            this.terrainNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
            this.terrainNoiseGenerator.SetFractalOctaves(3);
            this.terrainNoiseGenerator.SetFractalGain(0.5F);
            this.terrainNoiseGenerator.SetFractalWeightedStrength(0.5F);
            this.terrainNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
            this.terrainNoiseGenerator.SetDomainWarpAmp(100.0F);
            this.terrainNoiseGenerator.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

            this.peakValleyNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
            this.peakValleyNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
            this.peakValleyNoiseGenerator.SetFrequency(0.0002F);
            this.peakValleyNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.Ridged);
            this.peakValleyNoiseGenerator.SetFractalOctaves(4);
            this.peakValleyNoiseGenerator.SetFractalLacunarity(1.5F);
            this.peakValleyNoiseGenerator.SetFractalGain(0.5F);
            this.peakValleyNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
            this.peakValleyNoiseGenerator.SetDomainWarpAmp(35.0F);

            this.erosionNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
            this.erosionNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
            this.erosionNoiseGenerator.SetFrequency(0.02F);
            this.erosionNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);

            this.factorNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
            this.factorNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
            this.factorNoiseGenerator.SetFrequency(0.02F);
            this.factorNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);
        }

        public float sampleContinentalNoise(float x, float z) {
            return continentalNoiseGenerator.GetNoise(x, z);
        }

        public float sampleHeightmapNoise(float x, float z) {
            return heightmapNoiseGenerator.GetNoise(x, z);
        }

        public float sampleTerrainNoise(float x, float y, float z) {
            return terrainNoiseGenerator.GetNoise(x, y, z);
        }

        public float samplePeakValleyNoise(float x, float z) {
            return peakValleyNoiseGenerator.GetNoise(x, z);
        }

        public float sampleErosionNoise(float x, float z) {
            return erosionNoiseGenerator.GetNoise(x, z);
        }

        public float sampleFactorNoise(float x, float z) {
            return factorNoiseGenerator.GetNoise(x, z);
        }

        public enum HeightType {
            MOUNTAIN,
            PLATEAU,
            SURFACE,
            CANYON,
            COASTAL,
            WATER;
        }
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

    @Override
    public void func_230350_a_(long seed, BiomeManager biomeManager, IChunk chunk, GenerationStage.Carving carvingStage) {
        this.carveBlocks(seed, biomeManager, chunk, carvingStage);
    }

    //createBiomes
    @Override
    public void func_242706_a(Registry<Biome> biomeIn, IChunk chunkIn) { this.createBiomes(biomeIn, chunkIn); }
}
