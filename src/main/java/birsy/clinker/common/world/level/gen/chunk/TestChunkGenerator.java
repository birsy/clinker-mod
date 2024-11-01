package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.MetaChunk;
import birsy.clinker.common.world.level.gen.MetaChunkTracker;
import birsy.clinker.common.world.level.gen.NoiseSampler;
import birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator.SurfaceDecorator;
import birsy.clinker.common.world.level.gen.chunk.biome.SurfaceDecorators;
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
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
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
    private final NoiseSampler sampler = new NoiseSampler(0);
    private static final CachedFastNoise noise = Util.make(() -> {
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


    private static final VoronoiGenerator voronoi = new VoronoiGenerator(0);
    private long seed;
    private float[][][] terrainShapeSamplePoints;

    private MetaChunk metaChunk;
    private List<MetaChunk.TerrainFeature> terrainFeatures;

    public TestChunkGenerator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pBiomeSource);//Util.make(() -> {
//            // this is just to get biomes from the dynamic registry in a way that vanilla BiomeSources understand.
//            // this fucking sucks, but i do what i must...
//            HolderLookup.Provider holderGetterProvider = VanillaRegistries.createLookup();
//            HolderGetter<Biome> holderGetter = holderGetterProvider.lookupOrThrow(Registries.BIOME);
//
//            return new FixedBiomeSource(holderGetter.getOrThrow(Biomes.PLAINS));

//            return new MultiDimensionalBiomeSource(
//                    new SingleBiomeProvider(holderGetter.getOrThrow(Biomes.WINDSWEPT_FOREST)), MultiDimensionalBiomeSource.OverlapResolutionMethod.FIRST_COME_FIRST_SERVE)
//
//                        .defineDimension("temperature", (x, y, z) -> noise.get(x * 0.5, y * 0.5, z * 0.5), -1.0F, 1.0F)
//                        .defineDimension("humidity", (x, y, z) -> noise.get(x * 0.2, y * 0.2 + 1000.0F, z * 0.2), -1.0F, 1.0F)
//
//                        .addBiome(new MultiDimensionalBiomeSource.BiomeSelector(holderGetter.getOrThrow(Biomes.PLAINS))
//                                .defineRange("temperature", 0.0F, 0.5F)
//                                .defineRange("humidity", -0.5F, 0.5F))
//                        .addBiome(new MultiDimensionalBiomeSource.BiomeSelector(holderGetter.getOrThrow(Biomes.DESERT))
//                                .defineRange("temperature", 0.5F, 1.0F)
//                                .defineRange("humidity", -1.0F, -0.5F))
//                        .addBiome(new MultiDimensionalBiomeSource.BiomeSelector(holderGetter.getOrThrow(Biomes.SWAMP))
//                                .defineRange("temperature", 0.5F, 1.0F)
//                                .defineRange("humidity", 0.0F, 1.0F));
        //}));
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.value();
        this.globalFluidPicker = (a, b, c) -> new Aquifer.FluidStatus(this.settings.seaLevel(), this.settings.defaultFluid());
    }



    private void setNoiseSeed(long seed) {
        if (seed != this.noise.getNoise().GetSeed()) {
            this.noise.getNoise().SetSeed((int) seed);
            this.noise.invalidateCache();
        }
        if (seed != this.voronoi.getSeed()) {
            this.voronoi.setSeed(seed);
        }
        this.sampler.setSeed(seed);
    }

    // earliest possible reference to a seed
    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> pStructureSetLookup, RandomState pRandomState, long pSeed) {
        seed = pSeed;
        this.setNoiseSeed(this.seed);
        return super.createState(pStructureSetLookup, pRandomState, pSeed);
    }

    // earliest possible reference to the WorldGenLevel
    @Override
    public void createReferences(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkAccess pChunk) {
        this.metaChunk = MetaChunkTracker.getOrCreateMetaChunkAtChunk(pLevel, pChunk.getPos());
        //Clinker.LOGGER.info("metachunk??");
        super.createReferences(pLevel, pStructureManager, pChunk);
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender densityBlender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        //fillNoiseSampleArrays(chunk);
        if (this.metaChunk != null) {
            this.terrainFeatures = metaChunk.getFeaturesInChunk(chunk.getPos());
            for (MetaChunk.TerrainFeature terrainFeature : this.terrainFeatures) {
                Clinker.LOGGER.info(terrainFeature.getBoundingBoxes().get(0).getCenter());
            }
        } else {
            this.terrainFeatures = new ArrayList<>();
        }

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

    private static final Vector3f ZERO = new Vector3f(0);
    private static final int MAX_ELEVATION_DIFFERENCE = 4;
    private void applySurfaceDecorators(WorldGenLevel level, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos().set(pos);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int startHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                pos.set(x + chunk.getPos().getMinBlockX(), startHeight, z + chunk.getPos().getMinBlockZ());

                boolean visibleToSun = true;
                while (pos.getY() > chunk.getMinBuildHeight() + 1) {
                    //when it encounters a new surface, check the biome and generate the corresponding surface.
                    if (level.getBlockState(pos) == this.settings.defaultBlock()) {
                        ResourceLocation biome = level.getBiome(pos).unwrapKey().get().location();
                        SurfaceDecorator decorator = SurfaceDecorators.getSurfaceDecorator(biome);
                        decorator.setSeed(this.seed);

                        neighborPos.set(pos);
                        int depth = 1;
                        while (neighborPos.getY() > chunk.getMinBuildHeight() + 1) {
                            neighborPos.move(Direction.DOWN);
                            if (level.getBlockState(neighborPos) == this.settings.defaultBlock()) depth++;
                            else break;
                        }

                        int maxElevationIncrease = 0;
                        int maxElevationDecrease = 0;
                        if (decorator.shouldCalculateElevationChange(visibleToSun, pos.getY())) {
                            for (Direction direction : Direction.Plane.HORIZONTAL) {
                                neighborPos.set(pos).move(direction);
                                boolean movingUp = level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                Direction moveDirection = movingUp ? Direction.UP : Direction.DOWN;

                                for (int i = 0; i < MAX_ELEVATION_DIFFERENCE + 1; i++) {
                                    neighborPos.move(moveDirection);

                                    if (movingUp) maxElevationIncrease = Math.max(maxElevationIncrease, i);
                                    else maxElevationDecrease = Math.max(maxElevationDecrease, i + 1);

                                    if (neighborPos.getY() < chunk.getMinBuildHeight() + 1) break;

                                    boolean isTop = movingUp != level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                    if (isTop) break;
                                }
                            }
                        }

                        decorator.buildSurface(chunk, new BlockPos.MutableBlockPos().set(pos), this.getSeaLevel(), visibleToSun, depth, maxElevationIncrease, maxElevationDecrease, (dx, dy, dz) -> ZERO, this.sampler);
                        visibleToSun = false;

                        // move down to the next air block
                        pos.move(Direction.DOWN, depth - 1);
                    }

                    pos.move(Direction.DOWN);
                }
            }
        }
    }

    public void applyCarvers(WorldGenRegion level, long seed, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess chunk, GenerationStep.Carving p_224172_) {
//        BlockPos.MutableBlockPos testPos = new BlockPos(chunk.getPos().getMinBlockX() + 8, 0, chunk.getPos().getMinBlockZ() + 8).mutable();
//        for (int y = chunk.getMinBuildHeight() + 1; y < chunk.getMaxBuildHeight() - 1; y++) {
//            testPos.setY(y);
//            level.setBlock(testPos, Blocks.GREEN_STAINED_GLASS.defaultBlockState(), 2);
//        }
        //this.applySurfaceDecorators(level, chunk);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_) {
        this.applySurfaceDecorators(p_223087_, p_223088_);
        super.applyBiomeDecoration(p_223087_, p_223088_, p_223089_);
    }

    // we don't actually use this for surface decorators since they don't have access to the neighboring chunks.
    // instead, use applyBiomeDecoration.
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {}
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

    // multi-layer terracing!
    // should look hytale-ish
    private float sampleDensity(float x, float y, float z) {
        float unmodifiedY = y;
        float heightFactor = 0.3F;

        float yScale = 0.2F;
        float scaledSeaLevel = this.getSeaLevel()*yScale;
        y *= yScale;

        if (y - scaledSeaLevel > 2.0F / heightFactor) {
            return -1.0F;
        }

        float caveNoise = (float) sampleCaveNoise(x, y / yScale, z);

        if (y - scaledSeaLevel < -2.0F / heightFactor) {
            return caveNoise;
        }

        float freq = 0.2F;
        float baseNoise = (float) this.noise.get(x * freq, y * freq, z * freq);
        freq = 0.8F;
        float detailNoise = (float) this.noise.get(x * freq, y * freq + 800.0F, z * freq);

        float totalNoise = (1.0F - Math.abs(detailNoise))*0.2F - 1.0F;

        freq = 0.2F;
        float cliffExistenceNoise = (float) this.noise.get(x * freq, z * freq);
        float threshold = 0.7F;
        cliffExistenceNoise -= threshold;
        if (cliffExistenceNoise < 0.0) {
            cliffExistenceNoise *= 3.0F;
        }

        // terraces
        for (int i = 2; i < 5; i++) {
            float tFreq = 0.6F;
            float terracedNoise = (float) (baseNoise + this.noise.get(x * tFreq, y * tFreq + (i * 500), z * tFreq) * 1.0);
            tFreq = 1.0F;
            terracedNoise += (float) (this.noise.get(x * tFreq, y * tFreq + (i * 500), z * tFreq) * 0.4F);
            terracedNoise = terrace(terracedNoise + cliffExistenceNoise, i, 0.2F, 0.95F);
            if (terracedNoise > totalNoise) {
                totalNoise = terracedNoise;
                // set surface flag for not ash dunes
            }
        }

        float surfaceNoise = (totalNoise + baseNoise * 0.1F + detailNoise * 0.1F) - ((y - scaledSeaLevel) * heightFactor);

        float value = (float) MathUtils.smoothMinExpo(caveNoise, surfaceNoise, 0.05F);
        for (MetaChunk.TerrainFeature terrainFeature : this.terrainFeatures) {
            value = terrainFeature.modifyNoiseLayers(value, x, unmodifiedY, z);
        }
        return value;
    }

    private static float terrace(float noiseVal, float stepCount, float lowerSteepness, float upperSteepness) {
        float steps = stepCount * 0.5F;
        float x = noiseVal * steps + 0.5F;
        float expBase = (2*(x - Math.round(x)));
        float steepness = expBase > 0.0 ? lowerSteepness : upperSteepness;
        float expPower = 1.0f / (1.0f - steepness);
        return (float) ((Math.round(x) + 0.5 * (Math.pow(Math.abs(expBase), expPower) * Math.signum(expBase))) - 0.5F) / steps;
    }

    public double sampleCaveNoise(double x, double y, double z) {
        int maxHeight = 240;
        int minHeight = 64;

        double aHFreq = 1.0;
        double aquiferMidHeight  = -40;
        double aquiferUpperRange = aquiferMidHeight + MathUtils.mapRange(-1.0F, 1.0F, 10.0F, 45.0F, noise.get(x * aHFreq, z * aHFreq));
        double aquiferLowerRange = aquiferMidHeight - 20;

        double seaLevel = this.getSeaLevel();
        double caveClamping = Mth.clamp(MathUtils.mapRange(aquiferUpperRange - 10, aquiferUpperRange + 15, 0, 1, y), 0, 1);
        caveClamping *= Mth.clamp(MathUtils.mapRange(seaLevel - 20, seaLevel + 5, 1, 0.2, y), 0, 1);


        //Stalagmite Noise;
        double stalagHFreq = (1.0 / 16.0);
        double stalagVFreq = (1.0 / 34.0);
        voronoi.setOffsetAmount(1.0F);
        VoronoiGenerator.VoronoiInfo stalagInfo = voronoi.get3(x * stalagHFreq, y * stalagVFreq, z * stalagHFreq);
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
        double sCave1 = this.noise.get(x * sHFreq * sHFreq2, y * sVFreq, z * sHFreq * sHFreq2);
        double sCave2 = this.noise.get(x * sHFreq, y * sVFreq + 3000.0, z * sHFreq);
        double sCave = sCave1 * sCave1 + sCave2 * sCave2;
        double threshold = 0.06 * caveClamping;

        sCave -= threshold;

        //Aquifer Cave Noise
        double aCave = 1.0;
        if (y < aquiferUpperRange + 10) {
            double aFreq = 6.0;
            double altNoise = noise.get(x * aFreq, z * aFreq);

            double aTopThres = MathUtils.mapRange(aquiferLowerRange, aquiferMidHeight, 0.0F, 1.0F, y);
            double aquiferSizeThrottling = y < aquiferMidHeight ?
                    Mth.lerp(0.0, MathUtils.ease((float) aTopThres, MathUtils.EasingType.easeOutBounce), MathUtils.ease((float) aTopThres, MathUtils.EasingType.easeOutQuad)) :
                    Mth.lerp(MathUtils.bias((altNoise + 1) / 2, 0.2), MathUtils.ease((float) MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutQuad),
                            MathUtils.ease((float) MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutBounce));
            double aquiferSizeThreshold = 0.5 * aquiferSizeThrottling;

            double aquiferCaveNoiseValue = this.noise.get(x, z);
            aCave = y > aquiferUpperRange ? 1 : y < aquiferLowerRange ? 1 : (aquiferCaveNoiseValue * aquiferCaveNoiseValue) - aquiferSizeThreshold;
        }

        //double[] bridgeCavern = sampleBridgeCaveNoise(x, y, z, seed);
        double bCave = 1.0F;//bridgeCavern[0];
        double bCaveShell = -1.0F;//bridgeCavern[1];

        return MathUtils.smoothMinExpo(MathUtils.smoothMinExpo(sCave, stalag, -0.05) + Math.max(bCaveShell * 0.8, 0), bCave, 0.06);//MathUtils.smoothMinExpo(stalag, sampleBridgeCaveNoise(x, y, z, seed), -0.05);//MathUtils.smoothMinExpo(aCave, MathUtils.smoothMinExpo(sCave, stalag, -0.1), 0.06);
    }

    private double[] sampleBridgeCaveNoise (double x, double y, double z, long seed) {
        Vec3 position = new Vec3(x, y, z);

        double frequency = 1.0 / 200.0;
        this.voronoi.setOffsetAmount(0.5F);
        VoronoiGenerator.VoronoiInfo info = this.voronoi.get2(x * frequency, z * frequency);
        double caveY = Mth.lerp(info.hash(), 10, 100);
        Vec3 caveCenter = new Vec3(info.cellPos().x() / frequency, caveY, info.cellPos().z() / frequency);
        Vec3 localPos = position.subtract(caveCenter);
        double wiggleNoise = this.noise.get(x * 0.8, y * 0.8, z * 0.8);
        wiggleNoise *= 8.0;
        Vec3 distortPos = localPos.add(wiggleNoise, wiggleNoise, wiggleNoise);
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

    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {}

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
        return 150;
    }

    public static void register() {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(Clinker.MOD_ID, "test_chunk_generator"), CODEC);
    }
}
