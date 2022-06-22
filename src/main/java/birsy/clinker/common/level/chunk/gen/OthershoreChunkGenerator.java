package birsy.clinker.common.level.chunk.gen;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.BezierCurve;
import birsy.clinker.core.util.MathUtils;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.CubicSpline;
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
    private final Aquifer.FluidPicker globalFluidPicker;

    double[][][] terrainShapeSamplePoints;
    double[][][] duneSamplePoints;

    public OthershoreChunkGenerator(Registry<StructureSet> pStructureSets, BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pStructureSets, Optional.empty(), pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.get();
        this.sampler = new OthershoreNoiseSampler(0);
        this.globalFluidPicker = (a, b, c) -> new Aquifer.FluidStatus(this.settings.seaLevel(), this.settings.defaultFluid());
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

        //createNoiseSampleArray(chunk, random.legacyLevelSeed());

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = chunk.getMaxBuildHeight(); y > chunk.getMinBuildHeight(); y--) {
                    pos.set(x, y, z);
                    BlockState state = getBlockStateAtPos(x + (chunk.getPos().x * 16), y, z + (chunk.getPos().z * 16), random.legacyLevelSeed());
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

    public void createNoiseSampleArray(ChunkAccess chunk, long seed) {
        if (this.duneSamplePoints != null && this.terrainShapeSamplePoints != null) return;
        int horizontalSamplePoints = 8;
        int verticalSamplePoints = 48;
        this.terrainShapeSamplePoints = new double[horizontalSamplePoints][verticalSamplePoints][horizontalSamplePoints];
        this.duneSamplePoints = new double[horizontalSamplePoints][verticalSamplePoints][horizontalSamplePoints];
        for (int sX = 0; sX < horizontalSamplePoints; sX++) {
            for (int sZ = 0; sZ < horizontalSamplePoints; sZ++) {
                for (int sY = 0; sY < verticalSamplePoints; sY++) {
                    int hOffset = (int) (16.0F / (float)horizontalSamplePoints);
                    int vOffset = (int) ((float)chunk.getHeight() / (float)horizontalSamplePoints);

                    int cX = sX * hOffset;
                    int x = cX + (chunk.getPos().x * 16);
                    int y = sY * vOffset;
                    int cZ = sZ * hOffset;
                    int z = cZ + (chunk.getPos().z * 16);

                    double[] noiseSamples = sampleTerrainAndDuneShapeNoise(x, y, z, seed);
                    terrainShapeSamplePoints[sX][sY][sZ] = noiseSamples[0];
                    duneSamplePoints[sX][sY][sZ] = noiseSamples[1];
                }
            }
        }
    }

    public void createNoiseSampleArray(int x, int z, LevelHeightAccessor chunk, long seed) {
        if (this.duneSamplePoints != null && this.terrainShapeSamplePoints != null) return;
        int horizontalSamplePoints = 8;
        int verticalSamplePoints = 48;
        this.terrainShapeSamplePoints = new double[horizontalSamplePoints][verticalSamplePoints][horizontalSamplePoints];
        this.duneSamplePoints = new double[horizontalSamplePoints][verticalSamplePoints][horizontalSamplePoints];
        for (int sX = 0; sX < horizontalSamplePoints; sX++) {
            for (int sZ = 0; sZ < horizontalSamplePoints; sZ++) {
                for (int sY = 0; sY < verticalSamplePoints; sY++) {
                    int hOffset = (int) (16.0F / (float)horizontalSamplePoints);
                    int vOffset = (int) ((float)chunk.getHeight() / (float)horizontalSamplePoints);

                    int cX = sX * hOffset;
                    int tX = cX + ((int)(Math.floor((float)x / 16.0F)) * 16);
                    int y = sY * vOffset;
                    int cZ = sZ * hOffset;
                    int tZ = cZ + ((int)(Math.floor((float)z / 16.0F)) * 16);

                    double[] noiseSamples = sampleTerrainAndDuneShapeNoise(tX, y, tZ, seed);
                    terrainShapeSamplePoints[sX][sY][sZ] = noiseSamples[0];
                    duneSamplePoints[sX][sY][sZ] = noiseSamples[1];
                }
            }
        }
    }

    public BlockState getBlockStateAtPos(int x, int y, int z, long seed) {
        int maxHeight = 240;
        int minHeight = 64;
        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);

        double terrainShape; /*= MathUtils.lerp3(sFrac.x(), sFrac.y(), sFrac.z(),
                terrainShapeSamplePoints[sMin.getX()][sMin.getY()][sMin.getZ()], terrainShapeSamplePoints[sMin.getX()][sMax.getY()][sMin.getZ()],
                terrainShapeSamplePoints[sMax.getX()][sMin.getY()][sMin.getZ()], terrainShapeSamplePoints[sMax.getX()][sMax.getY()][sMin.getZ()],
                terrainShapeSamplePoints[sMin.getX()][sMin.getY()][sMax.getZ()], terrainShapeSamplePoints[sMin.getX()][sMax.getY()][sMax.getZ()],
                terrainShapeSamplePoints[sMax.getX()][sMin.getY()][sMax.getZ()], terrainShapeSamplePoints[sMax.getX()][sMax.getY()][sMax.getZ()]);*/
        double duneNoise; /*= MathUtils.lerp3(sFrac.x(), sFrac.y(), sFrac.z(),
                duneSamplePoints[sMin.getX()][sMin.getY()][sMin.getZ()], duneSamplePoints[sMin.getX()][sMax.getY()][sMin.getZ()],
                duneSamplePoints[sMax.getX()][sMin.getY()][sMin.getZ()], duneSamplePoints[sMax.getX()][sMax.getY()][sMin.getZ()],
                duneSamplePoints[sMin.getX()][sMin.getY()][sMax.getZ()], duneSamplePoints[sMin.getX()][sMax.getY()][sMax.getZ()],
                duneSamplePoints[sMax.getX()][sMin.getY()][sMax.getZ()], duneSamplePoints[sMax.getX()][sMax.getY()][sMax.getZ()]);*/

        if (y > maxHeight + 10) {
            terrainShape = -100000.0;
            duneNoise = -100000.0;
        } else if (y < minHeight - 30) {
            terrainShape = 100000.0;
            duneNoise = 100000.0;
        } else {
            double frequency = 0.05;

            double detailSize = 2.0;
            double detailNoise = s.detailNoise.GetNoise(x * detailSize, y * 0.3, z * detailSize);

            double plateauSize = 2.0;
            double cliffsideSize = 1.5;
            double erosion =  MathUtils.mapRange(-1, 1, 0.4, 0.8, MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise((x + 4233) * frequency * 2, (z + 3234) * frequency) * 2, 0.75F, 2));
            double tBaseNoise = s.largeNoise.GetNoise(x * frequency, z * frequency);
            tBaseNoise = MathUtils.smoothMinExpo((tBaseNoise * plateauSize + plateauSize) - 1, 1, 0.0);
            tBaseNoise = tBaseNoise < 0.0 ? MathUtils.smoothMinExpo(tBaseNoise * cliffsideSize, -1, -0.1) : tBaseNoise;
            double baseNoise = MathUtils.biasTowardsExtreme(tBaseNoise, erosion, 2);

            double basicNoise = baseNoise;
            basicNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, basicNoise);
            basicNoise -= y;

            double overhangStepSize = 0.12;
            double overhangStepNoise = (s.largeNoise.GetNoise(x * overhangStepSize, 22.0, z * overhangStepSize) + 1.0F) / 2.0F;
            double overhangNoise = MathUtils.biasTowardsExtreme(tBaseNoise, 0.9F, 2) * 0.8 + detailNoise * 0.2;
            overhangNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, overhangNoise);
            overhangNoise -= overhang(y, MathUtils.map(4.0F, 8.0F, (float) MathUtils.bias(overhangStepNoise, 0.0)), 64, 240);

            double stepSize = 0.2;
            double terrainStepNoise = (s.largeNoise.GetNoise(x * stepSize, 2342.0, z * stepSize) + 1.0F) / 2.0F;
            double terracedNoise = MathUtils.terrace((float) (baseNoise * 0.83 + detailNoise * 0.17), 1.0F / (MathUtils.map(4.5F, 0.5F, (float) MathUtils.bias(terrainStepNoise, 0.0F)) + (float)((overhangStepNoise * 2) - 1)*0.1F), 0.0F).first;
            terracedNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, terracedNoise);
            terracedNoise -= y;

            double smoothingFactor = baseNoise * baseNoise * baseNoise * baseNoise * baseNoise * baseNoise;
            smoothingFactor = MathUtils.bias(smoothingFactor, -0.5F);
            smoothingFactor = MathUtils.map(0.0F, 0.9F, (float) smoothingFactor);

            double shapeScale = 0.32;
            double shapeNoise = MathUtils.bias((MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise(x * shapeScale, -1234.0, z * shapeScale), 0.5F, 3) + 1.0F) / 2.0F, 0.6F);
            terrainShape = Mth.lerp(shapeNoise, terracedNoise, overhangNoise);
            terrainShape = Mth.lerp(smoothingFactor, terrainShape, basicNoise);

            double duneSize = 0.7;
            duneNoise = (1 - Math.abs(s.detailNoise.GetNoise(x * duneSize, z * duneSize)));
            duneNoise *= duneNoise;
            duneNoise *= 1 + (1 - smoothingFactor);
            duneNoise *= MathUtils.mapRange(-1.0, 1.0, 3.0, 5.0, tBaseNoise);
            duneNoise += basicNoise;
            duneNoise += ((terrainStepNoise * 2) - 1) * 4;
            duneNoise -= MathUtils.map(2, 15, (float) Math.min(shapeNoise, 1 - smoothingFactor));
            duneNoise += MathUtils.mapRange(-1.0, 1.0, 6.0, 0.0, tBaseNoise);
            duneNoise -= (1 - smoothingFactor) * 11;
        }

        //SPAGHETTI CAVE NOISE
        double spaghettiFrequency1 = 0.3;
        double spaghettiNoise1 = s.largeNoise.GetNoise(x * spaghettiFrequency1, y * spaghettiFrequency1, z * spaghettiFrequency1);
        double spaghettiFrequency2 = spaghettiFrequency1 * 1.1;
        double spaghettiNoise2 = s.largeNoise.GetNoise(x * spaghettiFrequency2, (y + 1000) * spaghettiFrequency2, z * spaghettiFrequency2);
        double spaghettiNoise = (spaghettiNoise1 * spaghettiNoise1) + (spaghettiNoise2 * spaghettiNoise2);
        double spagCaveThreshold = 0.01;

        double thresholdedSpaghettiNoise = spaghettiNoise - spagCaveThreshold;

        double smoothness = 0.0;
        return MathUtils.smoothMinExpo(terrainShape, thresholdedSpaghettiNoise, smoothness) > 0 ? this.getStone(x, y, z) : MathUtils.smoothMinExpo(duneNoise, thresholdedSpaghettiNoise, smoothness) > 0 ? ClinkerBlocks.ASH.get().defaultBlockState() : this.getAir(x, y, z);
    }

    public double[] sampleTerrainAndDuneShapeNoise(double x, double y, double z, long seed) {
        int maxHeight = 240;
        int minHeight = 64;
        OthershoreNoiseSampler s = this.getOrCreateNoiseSampler(seed);

        double terrainShape;
        double duneNoise;
        double frequency = 0.05;
        double midHeight = (float) (maxHeight + minHeight) / 2.0F;

        double stepSize = 0.3;
        double terrainStepNoise = (s.largeNoise.GetNoise(x * stepSize, 2342.0, z * stepSize) + 1.0F) / 2.0F;
        double detailSize = 2.0;
        double detailNoise = s.detailNoise.GetNoise(x * detailSize, y * 0.3, z * detailSize);

        double plateauSize = 2.3;
        double tBaseNoise = s.largeNoise.GetNoise(x * frequency, z * frequency);
        tBaseNoise = MathUtils.smoothMinExpo((tBaseNoise * plateauSize + plateauSize) - 1, 1, 0.0);
        double baseNoise = MathUtils.biasTowardsExtreme(tBaseNoise, 0.5F, 2);

        double basicNoise = baseNoise;
        basicNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, basicNoise);
        basicNoise -= y;

        double terracedNoise = MathUtils.terrace((float) (baseNoise * 0.83 + detailNoise * 0.17), 1.0F / MathUtils.map(0.5F, 8.0F, (float) MathUtils.bias(terrainStepNoise, 0.2)), 0.0F).first;
        terracedNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, terracedNoise);
        terracedNoise -= y;

        double overhangNoise = MathUtils.biasTowardsExtreme(tBaseNoise, 0.9F, 2) * 0.8 + detailNoise * 0.2;
        overhangNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, overhangNoise);
        overhangNoise -= overhang(y, 3.0F, 64, 240);

        double smoothingFactor = baseNoise * baseNoise * baseNoise * baseNoise * baseNoise * baseNoise;
        smoothingFactor = MathUtils.bias(smoothingFactor, -1.0F);

        double shapeScale = 0.4;
        double shapeNoise = 1.0;//MathUtils.bias((MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise(x * shapeScale, -1234.0, z * shapeScale), 0.7F, 3) + 1.0F) / 2.0F, 0.3F);
        terrainShape = Mth.lerp(shapeNoise, terracedNoise, overhangNoise);
        terrainShape = Mth.lerp(smoothingFactor, terrainShape, basicNoise);

        double duneSize = 0.7;
        duneNoise = (1 - Math.abs(s.detailNoise.GetNoise(x * duneSize, z * duneSize)));
        duneNoise *= duneNoise;
        duneNoise *= MathUtils.mapRange(-1.0, 1.0, 3.0, 5.0, tBaseNoise);
        duneNoise += basicNoise;
        duneNoise += detailNoise * 4;
        duneNoise -= MathUtils.map(2, 15, (float) Math.min(shapeNoise, 1 - smoothingFactor));

        return new double[]{terrainShape, duneNoise};
    }

    public double overhang(double y, double overhangAmount, int minHeight, int maxHeight) {
        double dist = MathUtils.mapRange(minHeight, maxHeight, 0, 1, y);
        double a = Mth.frac(dist * overhangAmount);

        double topSteepness = 0.08;
        double overhangDepth = 3.5;

        double i = MathUtils.smoothClampExpo((a - 1)/topSteepness + 1, 0, 1, 0.2);
        double o = ((a - 1)*(a - 1)*(a - 1) + (a - 1)*(a - 1)) * overhangDepth;
        double s = MathUtils.bias(a, 0.7);
        a = Mth.lerp(i, o, s);

        //a = OVERHANG_CURVE.bezierPoint(a).y();
        //a = (7.466 * a * a * a - 11.2 * a * a + 4.733 * a);
        double b = Math.floor(dist * overhangAmount);

        double overhang = (a + b) / overhangAmount;
        return MathUtils.map(64, 240, (float) overhang);
    }

    public BlockState getStone(int x, int y, int z) {
        return settings.defaultBlock();
    }

    public BlockState getAir(int x, int y, int z) {
        return y > 70 ? Blocks.AIR.defaultBlockState() : settings.defaultFluid();
    }

    public int getBaseHeight(int x, int y, Heightmap.Types heightmap, LevelHeightAccessor level, RandomState random) {
        return 224;
    }

    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor chunk, RandomState random) {
        BlockState[] states = new BlockState[chunk.getHeight()];
        createNoiseSampleArray(x, z, chunk, random.legacyLevelSeed());
        int iY = 0;
        for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            states[iY] = getBlockStateAtPos(x, y, z, random.legacyLevelSeed());
            iY++;
        }

        return new NoiseColumn(chunk.getMinBuildHeight(), states);
    }

    public void addDebugScreenInfo(List<String> strings, RandomState randomState, BlockPos pos) {
        OthershoreNoiseSampler s = getOrCreateNoiseSampler(randomState.legacyLevelSeed());
        int x = pos.getX();
        int z = pos.getZ();

        double frequency = 0.05;

        double plateauSize = 2.0;
        double cliffsideSize = 1.5;
        double erosion =  MathUtils.mapRange(-1, 1, 0.4, 0.8, MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise((x + 4233) * frequency * 2, (z + 3234) * frequency) * 2, 0.8F, 1));
        double tBaseNoise = s.largeNoise.GetNoise(x * frequency, z * frequency);
        tBaseNoise = MathUtils.smoothMinExpo((tBaseNoise * plateauSize + plateauSize) - 1, 1, 0.0);
        tBaseNoise = tBaseNoise < 0.0 ? MathUtils.smoothMinExpo(tBaseNoise * cliffsideSize, -1, -0.1) : tBaseNoise;
        double baseNoise = MathUtils.biasTowardsExtreme(tBaseNoise, erosion, 2);

        String regionInfo = "";
        if (baseNoise > 0.65) { regionInfo = "Plateau"; }
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
