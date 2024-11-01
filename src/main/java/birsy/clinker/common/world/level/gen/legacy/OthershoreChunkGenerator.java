package birsy.clinker.common.world.level.gen.legacy;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//TODO: convert density function to NoiseChunk so can do beardifier shit to it. or don't, maybe? more importantly, i'd like to use surface rules because otherwise that shit will be a pain.
public class OthershoreChunkGenerator extends ChunkGenerator {
    public static final Codec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.create((codec) ->
            codec.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder))
                    .apply(codec, codec.stable(OthershoreChunkGenerator::new)));

    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;
    private OthershoreNoiseSampler sampler;
    private Beardifier beardifier;
    private final Aquifer.FluidPicker globalFluidPicker;
    static int horizontalSamplePoints = (int) (16 * 0.45F);
    static int verticalSamplePoints = (int) (384 * 0.3F);
    private static long seed;

    double[][][] terrainShapeSamplePoints;
    Vec3[][][] terrainDerivativeSamplePoints;
    double[][][] duneSamplePoints;

    double[][][] caveShapeSamplePoints;
    Vec3[][][] caveDerivativeSamplePoints;

    public OthershoreChunkGenerator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.value();
        this.sampler = new OthershoreNoiseSampler(0);
        this.globalFluidPicker = (a, b, c) -> new Aquifer.FluidStatus(this.settings.seaLevel(), this.settings.defaultFluid());

        this.terrainShapeSamplePoints = null;
        this.duneSamplePoints = null;
    }

    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> pStructureSetLookup, RandomState pRandomState, long pSeed) {
        seed = pSeed;
        return super.createState(pStructureSetLookup, pRandomState, pSeed);
    }
    
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public void buildSurface(WorldGenRegion genRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        if (!SharedConstants.debugVoidTerrain(chunk.getPos())) {
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, genRegion);
            this.buildSurface(chunk, worldgenerationcontext, randomState, structureManager, genRegion.getBiomeManager(), genRegion.registryAccess().registryOrThrow(Registries.BIOME), Blender.of(genRegion));
        }
    }

    @VisibleForTesting
    public void buildSurface(ChunkAccess chunk, WorldGenerationContext context, RandomState randomState, StructureManager structure, BiomeManager biomeManager, Registry<Biome> biomes, Blender noiseBlender) {
        NoiseChunk noisechunk = chunk.getOrCreateNoiseChunk((chunkGenerator) -> {
            return this.createNoiseChunk(chunkGenerator, structure, noiseBlender, randomState);
        });
        NoiseGeneratorSettings noisegeneratorsettings = this.settings;
        randomState.surfaceSystem().buildSurface(randomState, biomeManager, biomes, noisegeneratorsettings.useLegacyRandomSource(), context, chunk, noisechunk, noisegeneratorsettings.surfaceRule());
    }

    private NoiseChunk createNoiseChunk(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState random) {
        return NoiseChunk.forChunk(chunk, random, Beardifier.forStructuresInChunk(structureManager, chunk.getPos()), this.settings, this.globalFluidPicker, blender);
    }

    public int getSpawnHeight(LevelHeightAccessor pLevel) {
        return pLevel.getMaxBuildHeight();
    }

    public OthershoreNoiseSampler getOrCreateNoiseSampler(long seed) {
        return this.sampler.setSeed(seed);
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender densityBlender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Heightmap[] heightmaps = {chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG), chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG)};

        this.beardifier = Beardifier.forStructuresInChunk(structureManager, chunk.getPos());
        this.terrainShapeSamplePoints = fillSampleArray(chunk, (x, y, z, seed) -> sampleTerrainShapeNoise(x, y, z, seed), seed);
        this.terrainDerivativeSamplePoints = fillDerivativeArray(this.terrainShapeSamplePoints, 16.0 / horizontalSamplePoints, 384 / verticalSamplePoints);

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = chunk.getMaxBuildHeight(); y > chunk.getMinBuildHeight(); y--) {
                    pos.set(x, y, z);
                    BlockState state = getBlockStateAtPos(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ(), seed);

                    chunk.setBlockState(pos, state, false);
                    for (int i = 0; i < heightmaps.length; i++) {
                        Heightmap heightmap = heightmaps[i];
                        heightmap.update(x, y, z, state);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    public double[][][] fillSampleArray(ChunkAccess chunk, Function4<Double, Double, Double, Long, Double> sampler, long seed) {
        int hSamplePoints = (int) (16 * 0.5F);
        int vSamplePoints = (int) (384 * 0.5F);

        float hOffset = (16.0F / (float) hSamplePoints);
        float vOffset = ((float) chunk.getHeight() / (float) vSamplePoints);

        double[][][] samplePoints = new double[hSamplePoints + 1][vSamplePoints][hSamplePoints + 1];
        for (int sX = 0; sX < hSamplePoints + 1; sX++) {
            for (int sZ = 0; sZ < hSamplePoints + 1; sZ++) {
                for (int sY = 0; sY < vSamplePoints; sY++) {
                    double cX = sX * hOffset;
                    double cY = sY * vOffset;
                    double cZ = sZ * hOffset;

                    double x = cX + chunk.getPos().getMinBlockX();
                    double z = cZ + chunk.getPos().getMinBlockZ();
                    double y = cY + chunk.getMinBuildHeight();

                    samplePoints[sX][sY][sZ] = sampler.apply(x, y, z, seed);
                }
            }
        }
        //        this.caveDerivativeSamplePoints = fillDerivativeArray(this.caveShapeSamplePoints, new Vec3[hCaveSamplePoints + 1][vCaveSamplePoints][hCaveSamplePoints + 1], hOffset, vOffset);
        return samplePoints;
    }
    public Vec3[][][] fillDerivativeArray(double[][][] inputArray, double hOffset, double vOffset) {
        int hSamplePoints = (int) (16 * 0.5F);
        int vSamplePoints = (int) (384 * 0.5F);
        Vec3[][][] outputArray = new Vec3[hSamplePoints + 1][vSamplePoints][hSamplePoints + 1];
        for (int sX = 0; sX < inputArray.length; sX++) {
            for (int sZ = 0; sZ < inputArray[0][0].length; sZ++) {
                for (int sY = 0; sY < inputArray[0].length; sY++) {
                    double dX;
                    double dY;
                    double dZ;

                    if (sX == 0) {
                        dX = (inputArray[sX][sY][sZ] - inputArray[sX + 1][sY][sZ]) / hOffset;
                    } else {
                        dX = (inputArray[sX - 1][sY][sZ] - inputArray[sX][sY][sZ]) / hOffset;
                    }

                    if (sY == 0) {
                        dY = (inputArray[sX][sY][sZ] - inputArray[sX][sY + 1][sZ]) / vOffset;
                    } else {
                        dY = (inputArray[sX][sY - 1][sZ] - inputArray[sX][sY][sZ]) / vOffset;
                    }

                    if (sZ == 0) {
                        dZ = (inputArray[sX][sY][sZ] - inputArray[sX][sY][sZ + 1]) / hOffset;
                    } else {
                        dZ = (inputArray[sX][sY][sZ - 1] - inputArray[sX][sY][sZ]) / hOffset;
                    }

                    outputArray[sX][sY][sZ] = new Vec3(dX, dY, dZ);
                }
            }
        }

        return outputArray;
    }

    public double lerpSample(double[][][] sampleGrid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double x, double y, double z, MathUtils.EasingType yEasingType) {
        //non-negative
        double nNX = x - minX;
        double nNY = y - minY;
        double nNZ = z - minZ;
        int nNMaxX = maxX - minX;
        int nNMaxY = maxY - minY;
        int nNMaxZ = maxZ - minZ;

        double sX = (nNX / nNMaxX) * (sampleGrid.length - 1);
        double sY = (nNY / nNMaxY) * (sampleGrid[0].length - 1);
        double sZ = (nNZ / nNMaxZ) * (sampleGrid[0][0].length - 1);

        double sFracX = Mth.frac(sX);
        double sFracY = Mth.frac(sY);
        double sFracZ = Mth.frac(sZ);

        int sMinX = Mth.floor(sX);
        int sMaxX = Mth.ceil(sX);
        int sMinY = Mth.floor(sY);
        int sMaxY = Mth.ceil(sY);
        int sMinZ = Mth.floor(sZ);
        int sMaxZ = Mth.ceil(sZ);

        double lerpX = Mth.lerp(sFracX, sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMaxX][sMinY][sMinZ]);
        double lerpXZ = Mth.lerp(sFracX, sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMaxX][sMinY][sMaxZ]);

        double lerpXY = Mth.lerp(sFracX, sampleGrid[sMinX][sMaxY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ]);
        double lerpXYZ = Mth.lerp(sFracX, sampleGrid[sMinX][sMaxY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);

        return Mth.lerp(MathUtils.ease((float) sFracY, yEasingType), Mth.lerp(sFracZ, lerpX, lerpXZ), Mth.lerp(sFracZ, lerpXY, lerpXYZ));
    }
    public Vec3 lerpSampleVec3(Vec3[][][] sampleGrid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double x, double y, double z, MathUtils.EasingType yEasingType) {
        //non-negative
        double nNX = x - minX;
        double nNY = y - minY;
        double nNZ = z - minZ;
        int nNMaxX = maxX - minX;
        int nNMaxY = maxY - minY;
        int nNMaxZ = maxZ - minZ;

        double sX = (nNX / nNMaxX) * (sampleGrid.length - 1);
        double sY = (nNY / nNMaxY) * (sampleGrid[0].length - 1);
        double sZ = (nNZ / nNMaxZ) * (sampleGrid[0][0].length - 1);

        double sFracX = Mth.frac(sX);
        double sFracY = Mth.frac(sY);
        double sFracZ = Mth.frac(sZ);

        int sMinX = Mth.floor(sX);
        int sMaxX = Mth.ceil(sX);
        int sMinY = Mth.floor(sY);
        int sMaxY = Mth.ceil(sY);
        int sMinZ = Mth.floor(sZ);
        int sMaxZ = Mth.ceil(sZ);

        Vec3 lerpX = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMaxX][sMinY][sMinZ]);
        Vec3 lerpXZ = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMaxX][sMinY][sMaxZ]);
        Vec3 lerpXY = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMaxY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ]);
        Vec3 lerpXYZ = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMaxY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);
        return MathUtils.vec3Lerp(MathUtils.ease((float) sFracY, yEasingType), MathUtils.vec3Lerp(sFracZ, lerpX, lerpXZ), MathUtils.vec3Lerp(sFracZ, lerpXY, lerpXYZ));
    }

    public double mod(double x, double m) {
        return x - (m * Math.floor(x / m));
    }

    public BlockState getBlockStateAtPos(int x, int y, int z, long seed) {
        int maxHeight = 320;
        int minHeight = -64;

        double terrainShape = lerpSample(terrainShapeSamplePoints, 0, minHeight, 0, 16, maxHeight, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.linear);
        Vec3 derivative = lerpSampleVec3(terrainDerivativeSamplePoints, 0, minHeight, 0,16, maxHeight, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.linear);
        double facingUp = (derivative.dot(new Vec3(0, 1, 0)) + 1) * 0.5F;
        terrainShape += getBeardifierContribution(x, y, z);

        return terrainShape < 0 ? this.getStone(x, y, z, facingUp) : this.getAir(x, y, z, seed);
    }

    public double sampleTerrainShapeNoise(double x, double y, double z, long seed) {
        int seaLevel = this.getSeaLevel();
        OthershoreNoiseSampler sampler = this.getOrCreateNoiseSampler(seed);

        double baseHeight = y - seaLevel;
        baseHeight -= 28;
        double frequency = 0.2F;
        double sample = sampler.largeNoise.GetNoise(x * frequency, y * 0.3 * frequency, z * frequency);
        double frequency2 = 0.8F;
        sample += sampler.largeNoise.GetNoise(x * frequency2, y * 0.1 * frequency2, z * frequency2) * 0.5;

        double steepnessNoise = sampler.largeNoise.GetNoise(x * frequency, z * frequency);
        steepnessNoise = MathUtils.mapRange(-1, 1, 0.07, 1, steepnessNoise);

        sample = shape(sample, 0.2, steepnessNoise);

        double streamSample = Math.abs(sampler.largeNoise.GetNoise(x * frequency, z * frequency));
        streamSample = 1 - shape(streamSample - 0.05, 0.1, 0.1);
        streamSample = Mth.lerp(Mth.clamp(sample, 0, 1), 0, streamSample);
        sample += streamSample * 0.2;
        sample += baseHeight / 20.0F;
        return sample;
    }

    private double shape(double number, double upperShape, double lowerShape) {
        return Math.pow(Math.abs(number), number > 0 ? upperShape : lowerShape) * Math.signum(number);
    }
    private double getBeardifierContribution(int x, int y, int z) {
        double totalContribution;
        double individualContribution;
        for(totalContribution = 0.0D; this.beardifier.pieceIterator.hasNext(); totalContribution += individualContribution) {
            Beardifier.Rigid beardifier$rigid = this.beardifier.pieceIterator.next();
            BoundingBox boundingbox = beardifier$rigid.box();
            int groundLevelD = beardifier$rigid.groundLevelDelta();
            int bbX = Math.max(0, Math.max(boundingbox.minX() - x, x - boundingbox.maxX()));
            int bbZ = Math.max(0, Math.max(boundingbox.minZ() - z, z - boundingbox.maxZ()));
            int bbY = boundingbox.minY() + groundLevelD;
            int yFactor = y - bbY;
            int i3;
            switch (beardifier$rigid.terrainAdjustment()) {
                case NONE:
                    i3 = 0;
                    break;
                case BURY:
                case BEARD_THIN:
                    i3 = yFactor;
                    break;
                case BEARD_BOX:
                    i3 = Math.max(0, Math.max(bbY - y, y - boundingbox.maxY()));
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            int i2 = i3;
            switch (beardifier$rigid.terrainAdjustment()) {
                case NONE:
                    individualContribution = 0.0D;
                    break;
                case BURY:
                    individualContribution = this.beardifier.getBuryContribution(bbX, i2, bbZ);
                    break;
                case BEARD_THIN:
                case BEARD_BOX:
                    individualContribution = this.beardifier.getBeardContribution(bbX, i2, bbZ, yFactor) * 0.8D;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }
        }

        this.beardifier.pieceIterator.back(Integer.MAX_VALUE);

        while(this.beardifier.junctionIterator.hasNext()) {
            JigsawJunction jigsawjunction = this.beardifier.junctionIterator.next();
            int jjX = x - jigsawjunction.getSourceX();
            int jjY = y - jigsawjunction.getSourceGroundY();
            int jjZ = z - jigsawjunction.getSourceZ();
            totalContribution += this.beardifier.getBeardContribution(jjX, jjY, jjZ, jjY) * 0.4D;
        }

        this.beardifier.junctionIterator.back(Integer.MAX_VALUE);

        return totalContribution;
    }

    public BlockState getStone(int x, int y, int z, double facingUp) {
        return facingUp > 0.88 ? ClinkerBlocks.ASHEN_REGOLITH.get().defaultBlockState() : settings.defaultBlock();//ClinkerBlocks.CAPSTONE.get().defaultBlockState();
    }

    public BlockState getAir(int x, int y, int z, long seed) {
        return y > 70 ? Blocks.AIR.defaultBlockState() : settings.defaultFluid();
    }

    public int getBaseHeight(int x, int y, Heightmap.Types heightmap, LevelHeightAccessor level, RandomState random) {
        return 224;
    }

    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor chunk, RandomState random) {
        BlockState[] states = new BlockState[chunk.getHeight()];
        int iY = 0;
        for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            states[iY] = Blocks.AIR.defaultBlockState();
            iY++;
        }

        return new NoiseColumn(chunk.getMinBuildHeight(), states);
    }

    public void addDebugScreenInfo(List<String> strings, RandomState randomState, BlockPos pos) {}

    public void applyCarvers(WorldGenRegion p_224166_, long seed, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess p_224171_, GenerationStep.Carving p_224172_) {
    }

    public void spawnOriginalMobs(WorldGenRegion pLevel) {
    }

    public int getMinY() {
        return -63;
    }

    public int getGenDepth() {
        return 384;
    }

    public int getSeaLevel() {
        return this.settings.seaLevel();
    }

    public static void register() {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(Clinker.MOD_ID, "othershore_generator"), CODEC);
    }
}
