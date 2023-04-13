package birsy.clinker.common.world.level.chunk.gen;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CaveChunkGenerator extends ChunkGenerator {
    public static final Codec<CaveChunkGenerator> CODEC = RecordCodecBuilder.create((codec) -> commonCodec(codec).and(codec.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder)))
            .apply(codec, codec.stable(CaveChunkGenerator::new)));

    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;
    private OthershoreNoiseSampler sampler;

    private Beardifier beardifier;
    private final Aquifer.FluidPicker globalFluidPicker;
    static int horizontalSamplePoints = (int) (16 * 0.45F);
    static int verticalSamplePoints = (int) (384 * 0.3F);

    double[][][] caveShapeSamplePoints;
    Vec3[][][] caveDerivativeSamplePoints;

    public CaveChunkGenerator(Registry<StructureSet> pStructureSets, BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pStructureSets, Optional.empty(), pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.get();
        this.sampler = new OthershoreNoiseSampler(0);
        this.globalFluidPicker = (a, b, c) -> new Aquifer.FluidStatus(this.settings.seaLevel(), this.settings.defaultFluid());

    }

    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> pBiomeRegistry, Executor pExecutor, RandomState pRandom, Blender pBlender, StructureManager pStructureManager, ChunkAccess pChunk) {
        return super.createBiomes(pBiomeRegistry, pExecutor, pRandom, pBlender, pStructureManager, pChunk); // creates biomes
        // THEN afterwards, generates world zones and all their relevant info
    }

    public void buildSurface(WorldGenRegion genRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        if (!SharedConstants.debugVoidTerrain(chunk.getPos())) {
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, genRegion);
            this.buildSurface(chunk, worldgenerationcontext, randomState, structureManager, genRegion.getBiomeManager(), genRegion.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), Blender.of(genRegion));
        }
    }

    @VisibleForTesting
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

    public OthershoreNoiseSampler getOrCreateNoiseSampler(long seed) {
        return this.sampler.setSeed(seed);
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender densityBlender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Heightmap[] heightmaps = {chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG), chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG)};

        this.beardifier = Beardifier.forStructuresInChunk(structureManager, chunk.getPos());
        fillNoiseSampleArrays(chunk, random.legacyLevelSeed());

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = chunk.getMaxBuildHeight(); y > chunk.getMinBuildHeight(); y--) {
                    double awfulRandom = Mth.frac(Mth.frac(x * 32.2312343) * Mth.frac(z * 433.4445323) * 314159.32342);
                    boolean waterfall = awfulRandom < (1.0 / 8.0);

                    pos.set(x, y, z);
                    BlockState state = getBlockStateAtPos(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ(), random.legacyLevelSeed());

                    chunk.setBlockState(pos, state, false);
                    if (waterfall && state == settings.defaultFluid()) {
                        chunk.markPosForPostprocessing(pos);
                    }
                    for (int i = 0; i < heightmaps.length; i++) {
                        Heightmap heightmap = heightmaps[i];
                        heightmap.update(x, y, z, state);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    public void fillNoiseSampleArrays(ChunkAccess chunk, long seed) {
        int hCaveSamplePoints = (int) (16 * 0.2F);
        int vCaveSamplePoints = (int) (384 * 0.2F);
        float hCaveOffset = (16.0F / (float) hCaveSamplePoints);
        float vCaveOffset = ((float) chunk.getHeight() / (float) vCaveSamplePoints);

        this.caveShapeSamplePoints = new double[hCaveSamplePoints + 1][vCaveSamplePoints][hCaveSamplePoints + 1];
        for (int sX = 0; sX < hCaveSamplePoints + 1; sX++) {
            for (int sZ = 0; sZ < hCaveSamplePoints + 1; sZ++) {
                for (int sY = 0; sY < vCaveSamplePoints; sY++) {

                    float cX = sX * hCaveOffset;
                    float cY = sY * vCaveOffset;
                    float cZ = sZ * hCaveOffset;

                    float x = cX + chunk.getPos().getMinBlockX();
                    float z = cZ + chunk.getPos().getMinBlockZ();
                    float y = cY + chunk.getMinBuildHeight();

                    caveShapeSamplePoints[sX][sY][sZ] = sampleCaveNoise(x, y, z, seed);
                }
            }
        }

        this.caveDerivativeSamplePoints = fillDerivativeArray(this.caveShapeSamplePoints, new Vec3[hCaveSamplePoints + 1][vCaveSamplePoints][hCaveSamplePoints + 1], hCaveOffset, vCaveOffset);
    }

    public Vec3[][][] fillDerivativeArray(double[][][] inputArray, Vec3[][][] outputArray, double hOffset, double vOffset) {
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
        /*return MathUtils.lerp3(sFracX, sFracY, sFracZ,
                sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMinX][sMaxY][sMinZ],
                sampleGrid[sMaxX][sMinY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ],
                sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMinX][sMaxY][sMaxZ],
                sampleGrid[sMaxX][sMinY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);*/
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

        //Clinker.LOGGER.info("" + x +" "+ y +" "+ z + " -> " + nNX +" "+ nNY +" "+ nNZ + " -> " + sX +" "+ sY +" "+ sZ);

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
        /*return MathUtils.lerp3(sFracX, sFracY, sFracZ,
                sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMinX][sMaxY][sMinZ],
                sampleGrid[sMaxX][sMinY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ],
                sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMinX][sMaxY][sMaxZ],
                sampleGrid[sMaxX][sMinY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);*/
    }

    public double mod(double x, double m) {
        return x - (m * Math.floor(x / m));
    }

    public BlockState getBlockStateAtPos(int x, int y, int z, long seed) {
        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);
        s.voronoiGenerator.setOffsetAmount(1.3F);
        double voronoiScale = 12;
        double voronoiYScale = 4.5;
        VoronoiGenerator.VoronoiInfo voronoiInfo = s.voronoiGenerator.get3((x / voronoiScale), (y / voronoiYScale), (z / voronoiScale));
        double newY = voronoiInfo.cellPos().y * voronoiYScale;
        double erosion = 0.8;
        newY = Mth.clamp(newY, -62, 318);

        double caveShape = lerpSample(caveShapeSamplePoints, 0, -64, 0, 16, 320, 16, mod(x, 16), newY, mod(z, 16), MathUtils.EasingType.linear);
        double caveShape2 = lerpSample(caveShapeSamplePoints, 0, -64, 0, 16, 320, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.linear);
        caveShape = Mth.lerp(erosion, caveShape, caveShape2);
        Vec3 derivative = lerpSampleVec3(caveDerivativeSamplePoints, 0, -64, 0,16, 320, 16, mod(x, 16), newY, mod(z, 16), MathUtils.EasingType.linear);
        Vec3 derivative2 = lerpSampleVec3(caveDerivativeSamplePoints, 0, -64, 0,16, 320, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.linear);
        derivative = derivative.lerp(derivative2, erosion);
        derivative = derivative.normalize();
        double facingUp = (derivative.dot(new Vec3(0, 1, 0)) + 1) * 0.5F;

        caveShape += getBeardifierContribution(x, y, z);

        boolean isSurface = caveShape < -0.1;
        BlockState surfaceBlock = facingUp > 0.96 ? ClinkerBlocks.MUD.get().defaultBlockState() : this.settings.defaultBlock();
        return caveShape > 0 ? this.settings.defaultBlock() : Blocks.AIR.defaultBlockState();
    }

    public double sampleCaveNoise(double x, double y, double z, long seed) {
        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);
        int maxHeight = 240;
        int minHeight = 64;

        double aHFreq = 1.0;
        double aquiferMidHeight  = -40;
        double aquiferUpperRange = aquiferMidHeight + MathUtils.mapRange(-1.0F, 1.0F, 10.0F, 45.0F, s.largeNoise.GetNoise(x * aHFreq, z * aHFreq));
        double aquiferLowerRange = aquiferMidHeight - 20;

        double caveClamping = Mth.clamp(MathUtils.mapRange(aquiferUpperRange - 10, aquiferUpperRange + 15, 0, 1, y), 0, 1);

        //Stalagmite Noise;
        double stalagHFreq = (1.0 / 16.0);
        double stalagVFreq = (1.0 / 34.0);
        s.voronoiGenerator.setOffsetAmount(1.0F);
        VoronoiGenerator.VoronoiInfo stalagInfo = s.voronoiGenerator.get3(x * stalagHFreq, y * stalagVFreq, z * stalagHFreq);
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
        double sCave1 = s.largeNoise.GetNoise(x * sHFreq * sHFreq2, y * sVFreq, z * sHFreq * sHFreq2);
        double sCave2 = s.largeNoise.GetNoise(x * sHFreq, y * sVFreq + 3000.0, z * sHFreq);
        double sCave = sCave1 * sCave1 + sCave2 * sCave2;
        double threshold = 0.06 * caveClamping;

        sCave -= threshold;

        //Aquifer Cave Noise
        double aCave = 1.0;
        if (y < aquiferUpperRange + 10) {
            double aFreq = 6.0;
            double altNoise = s.largeNoise.GetNoise(x * aFreq, z * aFreq);

            double aTopThres = MathUtils.mapRange(aquiferLowerRange, aquiferMidHeight, 0.0F, 1.0F, y);
            double aquiferSizeThrottling = y < aquiferMidHeight ?
                    Mth.lerp(0.0, MathUtils.ease((float) aTopThres, MathUtils.EasingType.easeOutBounce), MathUtils.ease((float) aTopThres, MathUtils.EasingType.easeOutQuad)) :
                    Mth.lerp(MathUtils.bias((altNoise + 1) / 2, 0.2), MathUtils.ease((float) MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutQuad),
                            MathUtils.ease((float) MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutBounce));
            double aquiferSizeThreshold = 0.5 * aquiferSizeThrottling;

            double aquiferCaveNoiseValue = s.largeNoise.GetNoise(x, z);
            aCave = y > aquiferUpperRange ? 1 : y < aquiferLowerRange ? 1 : (aquiferCaveNoiseValue * aquiferCaveNoiseValue) - aquiferSizeThreshold;
        }

        double[] bridgeCavern = sampleBridgeCaveNoise(x, y, z, seed);
        double bCave = bridgeCavern[0];
        double bCaveShell = bridgeCavern[1];

        return MathUtils.smoothMinExpo(MathUtils.smoothMinExpo(sCave, stalag, -0.05) + Math.max(bCaveShell * 0.8, 0), bCave, 0.06);//MathUtils.smoothMinExpo(stalag, sampleBridgeCaveNoise(x, y, z, seed), -0.05);//MathUtils.smoothMinExpo(aCave, MathUtils.smoothMinExpo(sCave, stalag, -0.1), 0.06);
    }

    private double[] sampleBridgeCaveNoise (double x, double y, double z, long seed) {
        Vec3 position = new Vec3(x, y, z);

        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);
        double frequency = 1.0 / 82.0;
        s.voronoiGenerator.setOffsetAmount(0.0F);
        VoronoiGenerator.VoronoiInfo info = s.voronoiGenerator.get2(x * frequency, z * frequency);
        double caveY = Mth.lerp(info.hash(), 10, 120);
        Vec3 caveCenter = new Vec3(info.cellPos().x() / frequency, caveY, info.cellPos().z() / frequency);
        Vec3 localPos = position.subtract(caveCenter);
        double noise = s.largeNoise.GetNoise(x * 0.8, y * 0.8, z * 0.8);
        noise *= 8.0;
        Vec3 distortPos = localPos.add(noise, noise, noise);
        double cavernRadius = 32.0;
        double cavern = distortPos.multiply(1.0, 0.5, 1.0).length() - cavernRadius;
        double shell = MathUtils.mapRange(cavernRadius, cavernRadius + 10.0, 1.0, -1.0, distortPos.multiply(1.0, 1.0, 1.0).length());
        double bridge = 1.0 / frequency;
        double tunnel = 1.0 / frequency;

        float bridgeNumber = 4;//Mth.lerp(MathUtils.awfulRandom(info.hash() * 256.12342), 2, 5);
        double bridgeHeight = caveCenter.y() - cavernRadius;
        // TODO: please cache this
        for (int i = 0; i < bridgeNumber; i++) {
            bridgeHeight += ((cavernRadius * 2.0) / bridgeNumber); //MathUtils.awfulRandom(info.hash() * 512.12342 + i * 32) *
            float rotation = (float) Mth.lerp(MathUtils.awfulRandom(info.hash() * 864.2343 + i * 12), 0, 2 * Math.PI);
            Vec3 point1 = new Vec3(Mth.sin(rotation), 0, Mth.cos(rotation)).scale(cavernRadius * 2).add(caveCenter.x(), bridgeHeight, caveCenter.z());
            Vec3 point2 = new Vec3(Mth.sin(rotation + Mth.PI), 0, Mth.cos(rotation + Mth.PI)).scale(cavernRadius * 2).add(caveCenter.x(), bridgeHeight, caveCenter.z());
            Vec3 random = new Vec3((MathUtils.awfulRandom(info.hash() * 464.2383 + i * 2) * 2.0) - 1.0, 0, (MathUtils.awfulRandom(info.hash() * 33.2343 + i * 18) * 2.0) - 1.0).scale(16);
            double bDistance = getBridgeDistance(position, point1.add(random), point2.add(random), 4);
            double tDistance = getBridgeDistance(position, point1.add(random).add(0, 10, 0), point2.add(random).add(0, 10, 0), 3.5);

            bridge = Math.min(bridge, bDistance);
            tunnel = Math.min(tunnel, tDistance);
        }

        shell += Math.min((tunnel - 5) * 2, 0);
        shell -= Math.min(bridge * 2, 0);
        //shell = Mth.clamp(shell, 0, 1);
        return new double[]{MathUtils.smoothMinExpo(MathUtils.smoothMinExpo((float) -bridge, (float) cavern, -5.0F), tunnel, 2.0F) * 0.05, shell};
    }

    // actually just a cylinder SDF
    private double getBridgeDistance(Vec3 position, Vec3 bridgeStart, Vec3 bridgeEnd, double radius) {
        Vec3  ba = bridgeEnd.subtract(bridgeStart);
        Vec3  pa = position.subtract(bridgeStart);
        double baba = ba.dot(ba);
        double paba = pa.dot(ba);
        double x = pa.scale(baba).subtract(ba.scale(paba)).length() - radius * baba;
        double y = Math.abs(paba-baba*0.5)-baba*0.5;
        double x2 = x*x;
        double y2 = y*y*baba;

        double d = (Math.max(x,y)<0.0) ? -Math.min(x2,y2) : ( ( (x > 0.0) ? x2 : 0.0) + ( (y > 0.0) ? y2 : 0.0) );

        return Mth.sign(d) * Math.sqrt(Math.abs(d)) / baba;
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
        /*double awfulRandom = Mth.frac(Mth.frac(x * 32.2312343) * Mth.frac(z * 433.4445323) * 314159.32342);
        //Clinker.LOGGER.info(awfulRandom);
        boolean waterfall = awfulRandom < (1.0 / 256.0);
        double aquifer = sampleAquiferNoise(x, y, z, seed);
        double offset = 2.0;
        double aquiferX = sampleAquiferNoise(x + offset, y, z, seed);
        double aquiferZ = sampleAquiferNoise(x, y, z + offset, seed);
        double aquiferY = sampleAquiferNoise(x, y + offset, z, seed);

        double a = aquifer > 0 ? 1.0 : 0.0;
        double aX = aquiferX > 0 ? 1.0 : 0.0;
        double aZ = aquiferZ > 0 ? 1.0 : 0.0;
        double aY = aquiferY > 0 ? 1.0 : 0.0;

        double aNoise = 0.0;
        if (aX != a || aZ != a || aY > a) {
            aNoise = 1.0;
        }

        BlockState state = aNoise > 0 && !waterfall ? settings.defaultBlock() : aquifer > 0 ? settings.defaultFluid() : (y < -45 ? settings.defaultFluid() : Blocks.AIR.defaultBlockState());*/

        return Blocks.AIR.defaultBlockState();
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

    public void applyCarvers(WorldGenRegion p_224166_, long seed, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess p_224171_, GenerationStep.Carving p_224172_) {}

    public void spawnOriginalMobs(WorldGenRegion pLevel) {}

    public int getMinY() {
        return -63;
    }

    public int getGenDepth() {
        return 384;
    }

    public int getSeaLevel() {
        return 224;
    }

    private class BridgeCavernInfo {
        public final Vec3 caveCenter;
        public final List<CaveBridge> bridges;
        private final double frequency;
        private final double cavernRadius;

        protected BridgeCavernInfo(VoronoiGenerator.VoronoiInfo info, double frequency, double cavernRadius, RandomSource random) {
            this.frequency = frequency;
            this.cavernRadius = cavernRadius;

            double caveY = Mth.lerp(info.hash(), 10, 120);
            this.caveCenter = new Vec3(info.cellPos().x() / frequency, caveY, info.cellPos().z() / frequency);

            this.bridges = new ArrayList<>();
            float bridgeNumber = 4;
            double bridgeHeight = caveCenter.y() - cavernRadius;
            random.setSeed((long) (info.hash() * 2147483648.0D));
            for (int i = 0; i < bridgeNumber; i++) {
                bridgeHeight += ((cavernRadius * 2.0) / bridgeNumber);
                double rotation = Mth.lerp(random.nextDouble(), 0, 2 * Math.PI);

                Vec3 point1 = new Vec3(Math.sin(rotation), 0, Math.cos(rotation)).scale(cavernRadius * 2).add(caveCenter.x(), bridgeHeight, caveCenter.z());
                Vec3 point2 = new Vec3(Math.sin(rotation + Mth.PI), 0, Math.cos(rotation + Mth.PI)).scale(cavernRadius * 2).add(caveCenter.x(), bridgeHeight, caveCenter.z());
                Vec3 randomOffset = new Vec3((random.nextDouble() * 2.0) - 1.0, 0, (random.nextDouble() * 2.0) - 1.0).scale(16);

                bridges.add(new CaveBridge(point1.add(randomOffset), point2.add(randomOffset)));
            }
        }

        protected CaveDensitySample sampleDensity(double x, double y, double z, OthershoreNoiseSampler s) {
            Vec3 position = new Vec3(x, y, z);

            Vec3 localPos = position.subtract(this.caveCenter);
            double noise = s.largeNoise.GetNoise(x * 0.8, y * 0.8, z * 0.8);
            noise *= 8.0;
            Vec3 distortPos = localPos.add(noise, noise, noise);
            double cavern = distortPos.multiply(1.0, 0.5, 1.0).length() - cavernRadius;
            double shell = MathUtils.mapRange(cavernRadius, cavernRadius + 10.0, 1.0, -1.0, distortPos.multiply(1.0, 1.0, 1.0).length());

            double bridge = 1.0 / frequency;
            double tunnel = 1.0 / frequency;
            for (CaveBridge caveBridge : this.bridges) {
                double bDistance = getBridgeDistance(position, caveBridge.start, caveBridge.end, 4);
                double tDistance = getBridgeDistance(position, caveBridge.start.add(0, 10, 0), caveBridge.end.add(0, 10, 0), 3.5);

                bridge = Math.min(bridge, bDistance);
                tunnel = Math.min(tunnel, tDistance);
            }

            shell += Math.min((tunnel - 5) * 2, 0);
            shell -= Math.min(bridge * 2, 0);

            return new CaveDensitySample(MathUtils.smoothMinExpo( MathUtils.smoothMinExpo(-bridge, cavern, -5.0) , tunnel, 2.0) * 0.05, shell );
        }

        /* actually just a tube sdf
           thanks iq */
        private double getBridgeDistance(Vec3 position, Vec3 bridgeStart, Vec3 bridgeEnd, double radius) {
            Vec3  ba = bridgeEnd.subtract(bridgeStart);
            Vec3  pa = position.subtract(bridgeStart);
            double baba = ba.dot(ba);
            double paba = pa.dot(ba);
            double x = pa.scale(baba).subtract(ba.scale(paba)).length() - radius * baba;
            double y = Math.abs(paba-baba*0.5)-baba*0.5;
            double x2 = x*x;
            double y2 = y*y*baba;

            double d = (Math.max(x,y)<0.0) ? -Math.min(x2,y2) : ( ( (x > 0.0) ? x2 : 0.0) + ( (y > 0.0) ? y2 : 0.0) );

            return Mth.sign(d) * Math.sqrt(Math.abs(d)) / baba;
        }
    }

    protected record CaveDensitySample(double hollow, double fill) {}
    private record CaveBridge(Vec3 start, Vec3 end) {}
}
