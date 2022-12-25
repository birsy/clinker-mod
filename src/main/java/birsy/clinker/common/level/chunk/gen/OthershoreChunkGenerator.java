package birsy.clinker.common.level.chunk.gen;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.google.common.annotations.VisibleForTesting;
import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//TODO: convert density function to NoiseChunk so can do beardifier shit to it. or don't, maybe? more importantly, i'd like to use surface rules because otherwise that shit will be a pain.
public class OthershoreChunkGenerator extends ChunkGenerator {
    public static final Codec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.create((codec) -> commonCodec(codec).and(codec.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> generator.settingsHolder)))
            .apply(codec, codec.stable(OthershoreChunkGenerator::new)));

    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;
    private OthershoreNoiseSampler sampler;
    private Beardifier beardifier;
    private final Aquifer.FluidPicker globalFluidPicker;
    static int horizontalSamplePoints = (int) (16 * 0.45F);
    static int verticalSamplePoints = (int) (384 * 0.3F);

    double[][][] terrainShapeSamplePoints;
    Vec3[][][] terrainDerivativeSamplePoints;
    double[][][] duneSamplePoints;

    double[][][] caveShapeSamplePoints;
    Vec3[][][] caveDerivativeSamplePoints;

    public OthershoreChunkGenerator(Registry<StructureSet> pStructureSets, BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pStructureSets, Optional.empty(), pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.get();
        this.sampler = new OthershoreNoiseSampler(0);
        this.globalFluidPicker = (a, b, c) -> new Aquifer.FluidStatus(this.settings.seaLevel(), this.settings.defaultFluid());

        this.terrainShapeSamplePoints = null;
        this.duneSamplePoints = null;
    }

    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public void buildSurface(WorldGenRegion genRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        if (!SharedConstants.debugVoidTerrain(chunk.getPos())) {
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, genRegion);
            this.buildSurface(chunk, worldgenerationcontext, randomState, structureManager, genRegion.getBiomeManager(), genRegion.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), Blender.of(genRegion));
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
        int hSamplePoints = (int) (16 * 0.3F);
        int vSamplePoints = (int) (384 * 0.3F);

        float hOffset = (16.0F / (float) hSamplePoints);
        float vOffset = ((float) chunk.getHeight() / (float) vSamplePoints);

        this.terrainShapeSamplePoints = new double[hSamplePoints + 1][vSamplePoints][hSamplePoints + 1];
        this.duneSamplePoints = new double[hSamplePoints + 1][vSamplePoints][hSamplePoints + 1];
        long startTime = System.currentTimeMillis();
        for (int sX = 0; sX < hSamplePoints + 1; sX++) {
            for (int sZ = 0; sZ < hSamplePoints + 1; sZ++) {
                for (int sY = 0; sY < vSamplePoints; sY++) {

                    float cX = sX * hOffset;
                    float cY = sY * vOffset;
                    float cZ = sZ * hOffset;

                    float x = cX + chunk.getPos().getMinBlockX();
                    float z = cZ + chunk.getPos().getMinBlockZ();
                    float y = cY + chunk.getMinBuildHeight();

                    double[] noiseSamples = sampleTerrainAndDuneShapeNoise(x, y, z, seed);
                    terrainShapeSamplePoints[sX][sY][sZ] = noiseSamples[0];
                    duneSamplePoints[sX][sY][sZ] = noiseSamples[1];
                }
            }
        }
        //Clinker.LOGGER.info("terrain gen took " + (System.currentTimeMillis() - startTime) + " milliseconds.");

        startTime = System.currentTimeMillis();
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
        //Clinker.LOGGER.info("cave gen took " + (System.currentTimeMillis() - startTime) + " milliseconds.");

        this.terrainDerivativeSamplePoints = fillDerivativeArray(this.terrainShapeSamplePoints, new Vec3[hSamplePoints + 1][vSamplePoints][hSamplePoints + 1], hOffset, vOffset);
        this.caveDerivativeSamplePoints = fillDerivativeArray(this.caveShapeSamplePoints, new Vec3[hCaveSamplePoints + 1][vCaveSamplePoints][hCaveSamplePoints + 1], hOffset, vOffset);
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
        int maxHeight = 240;
        int minHeight = 64;

        //Clinker.LOGGER.info("" + mod(x, 16) +" "+ y +" "+ mod(z, 16));
        double terrainShape = lerpSample(terrainShapeSamplePoints, 0, -64, 0, 16, 320, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.easeInOutQuad);
        boolean surfaceFluid = (terrainShape < 0);

        double caveShape = lerpSample(caveShapeSamplePoints, 0, -64, 0, 16, 320, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.easeInOutQuad);
        double duneShape = lerpSample(duneSamplePoints, 0, -64, 0,16, 320, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.linear);
        Vec3 derivative;
        double edgeDeriv;
        derivative = lerpSampleVec3(terrainDerivativeSamplePoints, 0, -64, 0,16, 320, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.easeInOutQuad);
        edgeDeriv = 0.17;
        derivative = derivative.normalize();
        double facingUp = (derivative.dot(new Vec3(0, 1, 0)) + 1) * 0.5F;

        terrainShape = MathUtils.smoothMinExpo(caveShape, terrainShape, 0.05F);
        duneShape = MathUtils.smoothMinExpo(caveShape, duneShape, 0.07F);
        terrainShape += getBeardifierContribution(x, y, z);

        //return terrainShape > 0 ? (terrainShape > 6 ? this.settings.defaultBlock() : this.getStone(x, y, z, facingUp)) : (duneShape > 0 ? ClinkerBlocks.ASH.get().defaultBlockState() : this.getAir(x, y, z));
        return terrainShape > 0 ?
                (terrainShape > 6 ? this.settings.defaultBlock() : terrainShape > edgeDeriv ? this.getStone(x, y, z, facingUp) : this.settings.defaultBlock()) :
                (duneShape > 0 ? ClinkerBlocks.ASH.get().defaultBlockState() :
                        surfaceFluid ? (y > 75 ? Blocks.AIR.defaultBlockState() : settings.defaultFluid()) : this.getAir(x, y, z, seed));
        /*OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);
        s.voronoiGenerator.setOffsetAmount(1.0);
        double cellSize = 16.0;
        VoronoiGenerator.VoronoiInfo cell = s.voronoiGenerator.get2(x / cellSize, z / cellSize);

        double freq = cellSize * 0.1;
        double offset = 0.1;
        double noise = s.largeNoise.GetNoise(cell.cellPos().x() * freq, cell.cellPos().z() * freq);
        double noiseX = s.largeNoise.GetNoise((cell.cellPos().x() * freq) + offset, cell.cellPos().z() * freq);
        double noiseZ = s.largeNoise.GetNoise(cell.cellPos().x() * freq, (cell.cellPos().z() * freq) + offset);
        Vec3 derivative = new Vec3((noise - noiseX) / offset, 0, (noise - noiseZ) / offset);


        Vec3 riverVector = derivative.normalize();

        Vec3 riverPos = cell.localPos().yRot((float) (Math.atan2(riverVector.z, riverVector.x) + Math.toRadians(90.0)));
        double riverGradient = Math.abs(riverPos.x);
        if (riverVector == Vec3.ZERO) {
            riverGradient = cell.distance();
        }
        noise = MathUtils.mapRange(-1, 1, minHeight, maxHeight, noise);
        noise -= y;
        double cellSize = 128.0;
        VoronoiGenerator.VoronoiInfo cell = s.voronoiGenerator.get2(x / cellSize, z / cellSize);

        double freq = 0.1;
        double offset = 0.1;
        double noise = s.largeNoise.GetNoise(x * freq, z * freq);
        double noiseX = s.largeNoise.GetNoise((x * freq) + offset, z * freq);
        double noiseZ = s.largeNoise.GetNoise(x * freq, (z * freq) + offset);
        Vec3 derivative = new Vec3((noise - noiseX) / offset, 0, (noise - noiseZ) / offset);

        noise /= derivative.length();
        double riverSize = 1.0;
        noise = Math.abs(noise) - riverSize;
        double riverHeight = MathUtils.map(0, 32, cell.hash());
        riverHeight -= y;
        noise *= Math.abs(riverHeight) - 8;
        return noise > 0 ? settings.defaultBlock() : Blocks.AIR.defaultBlockState();*/
    }

    public double[] sampleTerrainAndDuneShapeNoise(double x, double y, double z, long seed) {
        int maxHeight = 240;
        int minHeight = 64;
        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);

        double terrainShape;
        double duneShape;

        if (y > maxHeight + 10) {
            terrainShape = -100000.0;
            duneShape = -100000.0;
        } else if (y < minHeight - 30) {
            terrainShape = 100000.0;
            duneShape = 100000.0;
        } else {
            double frequency = 0.05;

            double detailSize = 1.5;
            double detailNoise = s.detailNoise.GetNoise(x * detailSize, y * 0.3, z * detailSize);

            double plateauSize = 2.0;
            double cliffsideSize = 1.5;
            double erosion = MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise((x + 4233) * frequency * 3, (z + 3234) * frequency) * 3, 0.1F, 1);
            erosion = (erosion + 1) * 0.5;
            erosion = Mth.clamp(erosion, 0.0, 1.0);
            erosion = MathUtils.bias(erosion, 0.3F);
            erosion = MathUtils.mapRange(0.0, 1.0, 0.0, 0.5, erosion);
            double tBaseNoise = s.largeNoise.GetNoise(x * frequency, z * frequency);
            tBaseNoise = MathUtils.smoothMinExpo((tBaseNoise * plateauSize + plateauSize) - 1, 1, 0.0);
            tBaseNoise = tBaseNoise < 0.0 ? MathUtils.smoothMinExpo(tBaseNoise * cliffsideSize, -1, -0.1) : tBaseNoise;
            double baseNoise = MathUtils.biasTowardsExtreme(tBaseNoise, erosion, 2);

            double basicNoise = baseNoise;
            basicNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, basicNoise);
            basicNoise -= y;

            double overhangStepSize = 0.07;
            double overhangStepNoise = (s.largeNoise.GetNoise(x * overhangStepSize, 22.0, z * overhangStepSize) + 1.0F) / 2.0F;
            overhangStepNoise = MathUtils.map(2.0, 8.0, overhangStepNoise);
            double detailIntensityO = 0.3;//0.23;
            double overhangNoise = (MathUtils.biasTowardsExtreme(tBaseNoise, 0.9F, 2) * (1 - detailIntensityO)) + (detailNoise * detailIntensityO);
            overhangNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, overhangNoise);
            overhangNoise -= overhang(y, overhangStepNoise, 64, 240);

            double stepSize = 0.15;
            double terrainStepNoise = (s.largeNoise.GetNoise(x * stepSize, 2342.0, z * stepSize) + 1.0F) / 2.0F;
            double detailIntensityT = 0.04;
            double terracedNoise = MathUtils.terrace((float) ((baseNoise * (1 - detailIntensityT)) + (detailNoise * detailIntensityT)), 1.0F / (MathUtils.map(3.3, 1.2, MathUtils.bias(terrainStepNoise, -0.2F))), 0.0F).first;
            terracedNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, terracedNoise);
            terracedNoise -= y;

            double smoothingFactor = baseNoise * baseNoise * baseNoise * baseNoise * baseNoise * baseNoise;
            smoothingFactor = MathUtils.bias(smoothingFactor, -0.1);
            smoothingFactor = MathUtils.map(0.0, 0.9, smoothingFactor);

            double shapeScale = 0.4;
            double shapeNoise = MathUtils.bias((MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise(x * shapeScale, -1234.0, z * shapeScale), 0.8F, 3) + 1.0F) / 2.0F, 0.35F);
            terrainShape = Mth.lerp(shapeNoise, terracedNoise, overhangNoise);
            //double shapeScale2 = 0.4;
            //double shapeNoise2 = MathUtils.bias((MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise(x * shapeScale2, -4234.0, z * shapeScale2), 0.8F, 3) + 1.0F) / 2.0F, 0.35F);
            //terrainShape = Mth.lerp(shapeNoise2, terrainShape, Math.max(terracedNoise, overhangNoise));

            terrainShape = Mth.lerp(smoothingFactor, terrainShape, basicNoise);

            double duneSize = 0.7;
            duneShape = (1 - Math.abs(s.detailNoise.GetNoise(x * duneSize, z * duneSize)));
            duneShape *= duneShape;
            duneShape *= 1 + (1 - smoothingFactor);
            duneShape *= MathUtils.mapRange(-1.0, 1.0, 3.0, 5.0, tBaseNoise);
            duneShape += basicNoise;
            duneShape += ((terrainStepNoise * 2) - 1) * 6;
            duneShape -= MathUtils.map(2, 15, /*(float) Math.min(shapeNoise, 1 - smoothingFactor)*/ shapeNoise);
            duneShape += MathUtils.mapRange(-1.0, 1.0, 6.0, 0.0, tBaseNoise);
            duneShape += (smoothingFactor) * 1.5;
            duneShape -= (1 - smoothingFactor) * 3.5;
        }

        return new double[]{terrainShape, duneShape};
    }

    public double overhang(double y, double overhangAmount, int minHeight, int maxHeight) {
        /*double dist = MathUtils.mapRange(minHeight, maxHeight, 0, 1, y);
        double a = Mth.frac(dist * overhangAmount);

        double topSteepness = 0.001;
        double overhangDepth = 4.1;

        double i = MathUtils.smoothClampExpo((a - 1)/topSteepness + 1, 0, 1, 0.2);
        double o = ((a - 1)*(a - 1)*(a - 1) + (a - 1)*(a - 1)) * overhangDepth;
        double s = MathUtils.bias(a, 1.0);
        double combined = MathUtils.smoothMinExpo(i, o, -0.01);
        a = Mth.lerp(i, combined, s);

        //a = OVERHANG_CURVE.bezierPoint(a).y();
        //a = (7.466 * a * a * a - 11.2 * a * a + 4.733 * a);
        double b = Math.floor(dist * overhangAmount);

        double overhang = (a + b) / overhangAmount;
        return MathUtils.map(64, 240, overhang);*/
        double dist = MathUtils.mapRange(minHeight, maxHeight, 0, 1, y);
        double a = Mth.frac(dist * overhangAmount);
        double b = Math.floor(dist * overhangAmount);

        a = MathUtils.bias(a, 1.0) * 0.3;

        double topSteepness = 0.001;
        double i = MathUtils.smoothClampExpo((a - 1)/topSteepness + 1, 0, 1, 0.2);
        double s = MathUtils.bias(a, 1.0);
        double combined = MathUtils.smoothMinExpo(i, a, -0.02);
        a = Mth.lerp(i, combined, s);

        double overhang = (a + b) / overhangAmount;
        return MathUtils.map(64, 240, overhang);
    }

    public double sampleCaveNoise(double x, double y, double z, long seed) {
        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);
        int maxHeight = 240;
        int minHeight = 64;

        double baseNoise = lerpSample(terrainShapeSamplePoints, 0, -64, 0, 16, 320, 16, mod(x, 16), y, mod(z, 16), MathUtils.EasingType.easeInOutQuad);
        double caveBlockage = Mth.clamp(MathUtils.mapRange(16, 0, 1, 0, baseNoise), 0, 1);

        double aHFreq = 1.0;
        double aquiferMidHeight  = -40;
        double aquiferUpperRange = aquiferMidHeight + MathUtils.mapRange(-1.0F, 1.0F, 10.0F, 45.0F, s.largeNoise.GetNoise(x * aHFreq, z * aHFreq));
        double aquiferLowerRange = aquiferMidHeight - 20;

        if (baseNoise > -5.0) {
            double sizeTransition = Mth.clamp(MathUtils.mapRange(80, 100, 1, 0, baseNoise), 1, 0);
            double caveClamping = Mth.clamp(MathUtils.mapRange(aquiferUpperRange - 10, aquiferUpperRange + 15, 0, 1, y), 0, 1);

            //Stalagmite Noise;
            double stalagHFreq = (1.0 / 16.0);
            double stalagVFreq = (1.0 / 34.0);
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

            double sHFreq2 = 0.6;
            double sCave1 = s.largeNoise.GetNoise(x * sHFreq * sHFreq2, y * sVFreq, z * sHFreq * sHFreq2);
            double sCave2 = s.largeNoise.GetNoise(x * sHFreq, y * sVFreq + 3000.0, z * sHFreq);
            double sCave = sCave1 * sCave1 + sCave2 * sCave2;
            double threshold = Mth.lerp(sizeTransition, 0.07, 0.1) * caveBlockage * caveClamping;

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

            return MathUtils.smoothMinExpo(aCave, MathUtils.smoothMinExpo(sCave, stalag, -0.06), 0.06);
        } else {
            return 1000;
        }
    }

    private double sampleAquiferNoise(double x, double y, double z, long seed) {
        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);
        if (y < 20 && y > -30) {
            double aHFreq = 1.0;
            double aquiferMidHeight  = -40;
            double aquiferUpperRange = aquiferMidHeight + MathUtils.mapRange(-1.0F, 1.0F, 10.0F, 45.0F, s.largeNoise.GetNoise(x * aHFreq, z * aHFreq));

            double aFreq = 0.4;
            double aquiferNoise = s.largeNoise.GetNoise(x * aFreq, y * aFreq * 0.05, z * aFreq);
            aquiferNoise += 0.3;
            if (aquiferNoise < 0.0) {
                return aquiferNoise;
            } else {
                double bottomCutoff = Mth.clamp(MathUtils.mapRange(aquiferUpperRange, 10, 0, 1, y), 0, 1);
                double tcFreq = 1.0;
                double tcWarp = s.largeNoise.GetNoise(x * tcFreq, y * tcFreq, z * tcFreq);
                tcWarp *= 0.2;
                double vFreq = 1.0 / 40.0;
                VoronoiGenerator.VoronoiInfo voronoiInfo = s.voronoiGenerator.get2(x * vFreq + tcWarp, z * vFreq + tcWarp);
                double topCutoff = MathUtils.map(-10, 10, voronoiInfo.hash() - Math.floor(voronoiInfo.hash()));
                topCutoff = topCutoff > y ? 1.0 : 0.0;
                double cutoff = bottomCutoff * topCutoff;

                double aquifer = cutoff * aquiferNoise;
                double smoothening = 0.04;
                aquifer -= smoothening;
                aquifer /= 1 - smoothening;

                return aquifer;
            }
        } else {
            return 0.0;
        }
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

    public void addDebugScreenInfo(List<String> strings, RandomState randomState, BlockPos pos) {
        OthershoreNoiseSampler s = getOrCreateNoiseSampler(randomState.legacyLevelSeed());
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int maxHeight = 240;
        int minHeight = 64;

        double frequency = 0.05;

        double plateauSize = 2.0;
        double cliffsideSize = 1.5;
        double erosion = MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise((x + 4233) * frequency * 3, (z + 3234) * frequency) * 3, 0.1F, 1);
        erosion = (erosion + 1) * 0.5;
        erosion = Mth.clamp(erosion, 0.0, 1.0);
        erosion = MathUtils.bias(erosion, 0.3F);
        erosion = MathUtils.mapRange(0.0, 1.0, 0.2, 0.8, erosion);
        double tBaseNoise = s.largeNoise.GetNoise(x * frequency, z * frequency);
        tBaseNoise = MathUtils.smoothMinExpo((tBaseNoise * plateauSize + plateauSize) - 1, 1, 0.0);
        tBaseNoise = tBaseNoise < 0.0 ? MathUtils.smoothMinExpo(tBaseNoise * cliffsideSize, -1, -0.1) : tBaseNoise;
        double baseNoise = MathUtils.biasTowardsExtreme(tBaseNoise, erosion, 2);

        double basicNoise = baseNoise;
        basicNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, basicNoise);

        String regionInfo = "";
        if (y < basicNoise - 8) { regionInfo = "Cave"; }
        else if (baseNoise > 0.65) { regionInfo = "Plateau"; }
        else if (baseNoise < -0.8) { regionInfo = "Valley"; }
        else { regionInfo = "Cliffside"; }


        strings.add("RegionInfo: " + regionInfo + ", BaseNoise: " + (Math.round(baseNoise * 1000.0F) / 1000.0F));
    }

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
        return 224;
    }

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Clinker.MOD_ID, "othershore_generator"), CODEC);
    }
}
