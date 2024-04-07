package birsy.clinker.common.world.level.gen.chunk.noiseproviders;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Random;

public class CaveSurfaceBuilder {
    private long seed;
    private FastNoiseLite spaghettiCaveNoise1;
    private FastNoiseLite spaghettiCaveNoise2;
    private FastNoiseLite caveSizeNoise;
    private FastNoiseLite aquiferCaveNoise;
    private FastNoiseLite stalagmiteNoise;
    private FastNoiseLite liquidNoise;

    private VoronoiGenerator bridgeCaveNoise = new VoronoiGenerator(1337);

    public CaveSurfaceBuilder() {
        super();
    }

    public void apply(Random pRandom, ChunkAccess chunkIn, Biome pBiome, int x, int z, int pHeight, double pNoise, BlockState defaultBlock, BlockState defaultFluid, int pSeaLevel, int pMinSurfaceLevel, long pSeed) {
        //super.apply(pRandom, chunkIn, pBiome, x, z, pHeight, pNoise, defaultBlock, defaultFluid, pSeaLevel, pMinSurfaceLevel, pSeed, pConfig);
/*
        int aquiferUpperRange = (int) MathUtils.mapRange(-1.0F, 1.0F, -30.0F, -5.0F, (float) this.caveSizeNoise.GetNoise((x + 682) * 2, (z + 682) * 2));
        int aquiferLowerRange = -63;
        int aquiferMidHeight  = ((aquiferUpperRange + aquiferLowerRange) / 2) - 5;
        double altNoise = caveSizeNoise.GetNoise(x * 6, z * 6);
        //double stalagmiteSize = 2.0;
        double liquidNoiseSample = liquidNoise.GetNoise(x * 0.5, z * 0.5);
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, chunkIn.getMaxBuildHeight(), z);

        int bridgeOffset = 5;
        int startHeight = Math.min(pHeight + 16 + bridgeOffset, chunkIn.getMaxBuildHeight());
        double[] spaghettiNoise = new double[chunkIn.getHeight() + 1];
        for (int y = 0; y < spaghettiNoise.length; y++) {
            spaghettiNoise[y] = 0;
            if (y + chunkIn.getMinBuildHeight() < startHeight) {
                //spaghettiNoise[y] = sampleSpaghettiNoise(x, y  + chunkIn.getMinBuildHeight(), z);
            }
        }

        for (int y = startHeight; y > chunkIn.getMinBuildHeight(); y--) {
            blockPos.setY(y);
            Pair<Double, BlockState> liquidShellNoise = getLiquidNoiseShell(liquidNoiseSample, y, aquiferUpperRange, -3, 11);
            double bigCavernNoise = sampleBigCavernNoise(x, y, z);
            double sample = sampleCaveNoise(x, y, z, pHeight, chunkIn.getMinBuildHeight(), altNoise, aquiferUpperRange, aquiferMidHeight, aquiferLowerRange, liquidShellNoise.getFirst(), spaghettiNoise, bridgeOffset, bigCavernNoise);

            if (sample < 0.00) {
                BlockState state = y < -45 ? defaultFluid : liquidShellNoise.getSecond();
                chunkIn.setBlockState(blockPos, state, state != Blocks.CAVE_AIR.defaultBlockState());
            }
        }
*/
/*
        int sampleSize = 3;

         for (int y = (int) (Math.ceil((pHeight + bridgeOffset) + 16 / sampleSize) * sampleSize); y > chunkIn.getMinBuildHeight() - 2; y -= sampleSize) {
            Pair<Double, BlockState> liquidShellNoise1 = getLiquidNoiseShell(liquidNoiseSample, y, 1.3, -3, 11);
            Pair<Double, BlockState> liquidShellNoise2 = getLiquidNoiseShell(liquidNoiseSample, y - sampleSize, 1.3, -3, 11);
            double sample1 = sampleCaveNoise(x, y, z, pHeight, chunkIn.getMinBuildHeight(), altNoise, aquiferUpperRange, aquiferMidHeight, aquiferLowerRange, liquidShellNoise1.getFirst());
            double sample2 = sampleCaveNoise(x, y - sampleSize, z, pHeight, chunkIn.getMinBuildHeight(), altNoise, aquiferUpperRange, aquiferMidHeight, aquiferLowerRange, liquidShellNoise2.getFirst());

            for (int y2 = sampleSize; y2 > 0; y2--) {
                int yDiff = y2 - sampleSize;
                blockPos.setY(y + yDiff);
                double delta = (double) -yDiff / (double) sampleSize;
                spaghettiNoise[]
                double sample = Mth.lerp(Mth.lerp(0.3, MathUtils.ease((float) delta, MathUtils.EasingType.easeInOutCirc), delta), sample1, sample2);
                //Pair<Double, BlockState> liquidShellNoiseSample = getLiquidNoiseShell(liquidNoiseSample, y + y2, 1.3, -3, 11);


                if (sample < 0.00) {
                    BlockState state = y < -45 ? defaultFluid : liquidShellNoise1.getSecond() == liquidShellNoise2.getSecond() ? liquidShellNoise1.getSecond() : Blocks.CAVE_AIR.defaultBlockState();
                    chunkIn.setBlockState(blockPos, state, state != Blocks.CAVE_AIR.defaultBlockState());
                }
            }
        }
 *//*
        double size = 16;
        int y = 250;
        Pair<double[], Vec2> voronoiSample = bridgeCaveNoise.get2(x * (1.0D / size),z * (1.0D / size));
        double sample = (voronoiSample.getSecond().x - (x * (1.0D / size)));
        double minValue = -1.0;
        double maxValue = 1.0;

        BlockState state = Blocks.BLACK_WOOL.defaultBlockState();
        if (sample > maxValue) {
            state = Blocks.WHITE_WOOL.defaultBlockState();
        } else if (sample > Mth.lerp(0.75, minValue, maxValue)) {
            state = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
        } else if (sample > Mth.lerp(0.5, minValue, maxValue)) {
            state = Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState();
        } else if (sample > Mth.lerp(0.25, minValue, maxValue)) {
            state = Blocks.GRAY_STAINED_GLASS.defaultBlockState();
        } else if (sample > Mth.lerp(0.0, minValue, maxValue)) {
            state = Blocks.BLACK_STAINED_GLASS.defaultBlockState();
        }

        chunkIn.setBlockState(new BlockPos(x, y, z), state, false);
*/

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, chunkIn.getMaxBuildHeight(), z);

        int aquiferUpperRange = (int) MathUtils.mapRange(-1.0F, 1.0F, -30.0F, -5.0F, (float) this.caveSizeNoise.GetNoise((x + 682) * 2, (z + 682) * 2));
        int aquiferLowerRange = -63;
        int aquiferMidHeight  = ((aquiferUpperRange + aquiferLowerRange) / 2) - 5;
        double altNoise = caveSizeNoise.GetNoise(x * 6, z * 6);
        //double stalagmiteSize = 2.0;
        double liquidNoiseSample = liquidNoise.GetNoise(x * 0.5, z * 0.5);

        double scale = 150;
        double waterLevel = -38;
        double cavernRadius = 30;
        double cavernHeightMult = 2.5;
        double tunnelMinDistance = 36;
        double tunnelMaxDistance = 50;
        double bridgeRadius = 1.3;
        double tunnelRadius = 6;

        //Offset set exactly to prevent intersection!
        bridgeCaveNoise.setOffsetAmount((scale - ((tunnelMinDistance + tunnelMaxDistance) * 0.5)) / scale);

        ArrayList<VoronoiGenerator.VoronoiInfo> cavernSamples = new ArrayList<>(chunkIn.getHeight() + 1);
        double[] bridgeSamples = new double[chunkIn.getHeight() + 1];
        Vec3[] localPoses = new Vec3[chunkIn.getHeight() + 1];
        for (int y = 0; y < bridgeSamples.length; y++) {
            Vec3 coords = new Vec3(x / scale, (y - (scale)) / (scale * cavernHeightMult), z / scale);

            double frequency = 0.5;
            double noise = spaghettiCaveNoise1.GetNoise(x * frequency, y * frequency, z * frequency);

            double ampedNoise = noise * ((cavernRadius / scale) * 0.5);
            cavernSamples.add(y, bridgeCaveNoise.get3(coords.x() + ampedNoise, coords.y(), coords.z() + ampedNoise));

            double ampedNoise2 = noise * Math.max(cavernSamples.get(y).distance() - (cavernRadius / scale), 0);

            VoronoiGenerator.VoronoiInfo voronoiSample = bridgeCaveNoise.get3(coords.x(), coords.y(), coords.z());
            Vec3 localPos = voronoiSample.localPos();
            localPoses[y] = localPos;
            bridgeSamples[y] = sampleCavernBridgeNoise(voronoiSample.localPos().multiply(1, cavernHeightMult, 1).add(ampedNoise2, ampedNoise2, ampedNoise2), voronoiSample.localPos(), voronoiSample.hash(), cavernRadius / scale, (cavernRadius * cavernHeightMult) / scale);
        }

        for (int y = chunkIn.getMaxBuildHeight(); y > chunkIn.getMinBuildHeight(); y--) {
            blockPos.setY(y);
            int maxHeight = 200;
            int minHeight = chunkIn.getMinBuildHeight();

            double[] bigBridgeSample = sampleBridgedCavernNoise(chunkIn, x, y, z, maxHeight, minHeight, scale, cavernRadius, tunnelMinDistance, tunnelMaxDistance, bridgeRadius, tunnelRadius, cavernSamples, bridgeSamples);
            Pair<Double, BlockState> liquidShellNoise = Pair.of(-1.0, Blocks.CAVE_AIR.defaultBlockState()); //getLiquidNoiseShell(liquidNoiseSample, y, aquiferUpperRange, -3, 11);

            double sample = sampleCaveNoise(x, y, z, pHeight, chunkIn.getMinBuildHeight(), altNoise, aquiferUpperRange, aquiferMidHeight, aquiferLowerRange, liquidShellNoise.getFirst(), cavernSamples.get(y - chunkIn.getMinBuildHeight()), bigBridgeSample[0], bigBridgeSample[1], tunnelMinDistance, tunnelMaxDistance, scale);

            if (sample < 0) {
                BlockState state = y < -45 ? defaultFluid : liquidShellNoise.getSecond();
                chunkIn.setBlockState(blockPos, state, state != Blocks.CAVE_AIR.defaultBlockState());
            } /*else if (y < pHeight){
                double minValue = 0;
                double maxValue = 1.0;

                BlockState state = Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState();
                double colorSample = cavernSamples.get(y - chunkIn.getMinBuildHeight()).getFirst()[0] > (cavernRadius / scale) ? 0 : 1; //cavernSamples.get(y - chunkIn.getMinBuildHeight()).getFirst()[0];
                if (colorSample > maxValue) {
                    state = Blocks.WHITE_CONCRETE.defaultBlockState();
                } else if (colorSample > Mth.lerp(0.75, minValue, maxValue)) {
                    state = Blocks.CALCITE.defaultBlockState();
                } else if (colorSample > Mth.lerp(0.5, minValue, maxValue)) {
                    state = Blocks.DIORITE.defaultBlockState();
                } else if (colorSample > Mth.lerp(0.25, minValue, maxValue)) {
                    state = Blocks.STONE.defaultBlockState();
                } else if (colorSample > Mth.lerp(0.0, minValue, maxValue)) {
                    state = Blocks.TUFF.defaultBlockState();
                }

                chunkIn.setBlockState(new BlockPos(x, y, z), state, false);
            }*/
        }
    }

    private double[] sampleBridgedCavernNoise(ChunkAccess chunk, int x, int y, int z, int maxHeight, int minHeight, double scale, double cavernRadius, double tunnelMinDistance, double tunnelMaxDistance, double bridgeRadius, double tunnelRadius, ArrayList<VoronoiGenerator.VoronoiInfo> cavernSamples, double[] bridgeSamples) {
        double upperHeightThrottling = Mth.clamp(MathUtils.mapRange(maxHeight, maxHeight - 20.0F, 0, 1, y), 0, 1);
        double lowerHeightThrottling = Mth.clamp(MathUtils.mapRange(minHeight + 3, minHeight + 20.0F, 0, 1, y), 0, 1);
        double heightThrottling = y > (maxHeight + minHeight / 2) ? upperHeightThrottling : lowerHeightThrottling;

        VoronoiGenerator.VoronoiInfo voronoiSample = cavernSamples.get(y - chunk.getMinBuildHeight());

        double distanceMul1 = Mth.clamp(MathUtils.mapRange(100 / scale, tunnelMinDistance / scale, 2, 1, voronoiSample.distance()), 0, 1);
        double distanceMul2 = Mth.clamp(MathUtils.mapRange(tunnelMaxDistance / scale, tunnelMinDistance / scale, 5, 1, voronoiSample.distance()), 1, 5);

        double bridgeSample = bridgeSamples[y - chunk.getMinBuildHeight()] - (bridgeRadius / scale);

        int tunnelSampleY = (int) ((y - chunk.getMinBuildHeight()) - (bridgeRadius + (tunnelRadius * heightThrottling)));
        double tunnelSample1 = (tunnelSampleY > chunk.getHeight() || tunnelSampleY < 0) ? 1.0 : bridgeSamples[tunnelSampleY] - (((tunnelRadius * heightThrottling) * distanceMul1) / scale);
        tunnelSample1 *= -1;
        double tunnelSample2 = (tunnelSampleY > chunk.getHeight() || tunnelSampleY < 0) ? 1.0 : bridgeSamples[tunnelSampleY] - (((tunnelRadius * heightThrottling) * distanceMul2) / scale);
        tunnelSample2 *= -1;

        double cavernSample = (voronoiSample.distance() * -1) + ((cavernRadius * heightThrottling) / scale);

        return new double[]{MathUtils.smoothMinExpo(MathUtils.smoothMinExpo(tunnelSample1, cavernSample, -0.01), bridgeSample, 0.03), MathUtils.smoothMinExpo(MathUtils.smoothMinExpo(tunnelSample2, cavernSample, -0.01), bridgeSample, 0.03)};
    }

    private double sampleCavernBridgeNoise(Vec3 localPos, Vec3 globalPos, double cellValue, double cavernRadius, double cavernHeight) {
        long seed = Math.abs((long)(cellValue * Long.MAX_VALUE));
        int bridgeAmount = (int) ((Math.abs(cellValue) * 6) + 8);
        double[] distances = new double[bridgeAmount];

        for (int bridgeNum = 0; bridgeNum < bridgeAmount; bridgeNum++) {
            double rotation = (Math.PI * 2) * randomValue(seed);
            seed = hash(seed);

            double sampleX = localPos.x * Mth.cos((float) rotation) - localPos.z * Mth.sin((float) rotation);
            double sampleY = localPos.y;
            //double sampleZ = localPos.z * Mth.cos((float) rotation) + localPos.x * Mth.sin((float) rotation);

            double xzOffsetFactor = 0.5;
            double xOffset = ((randomValue(seed) * 2) - 1) * xzOffsetFactor * cavernRadius;
            seed = hash(seed);
            double yOffset = ((randomValue(seed) * 2) - 1) * cavernHeight;
            seed = hash(seed);
            //double zOffset = ((rand.nextDouble() * 2) - 1) * xzOffsetFactor;

            distances[bridgeNum] = new Vec3(sampleX + xOffset, sampleY - yOffset, 0).length() * MathUtils.mapRange(0, 1, 0.95, 1.4, randomValue(seed));
            seed = hash(seed);
        }

        double minimum = distances[0];
        for (int i = 1; i < distances.length; i++) {
            minimum = MathUtils.smoothMinExpo(minimum, distances[i], 0.01);
        }
        return minimum;
    }
    private long hash(long state) {
        state = (state ^ 2747636419l) * 2654435769l;

        state = (state ^ (state >> 16l)) * 2654435769l;

        state = (state ^ (state >> 16l)) * 2654435769l;

        return state;
    }
    private float randomValue(long randomState) {
        randomState = hash(randomState);
        //Clinker.LOGGER.info(Math.abs((float) randomState / (float) Long.MAX_VALUE));
        return Math.abs((float) randomState / (float) Long.MAX_VALUE);
    }


    private double sampleCaveNoise(int x, int y, int z, int startHeight, int minheight, double altNoise, float aquiferUpperRange, float aquiferMidHeight, float aquiferLowerRange, double liquidShellNoise, VoronoiGenerator.VoronoiInfo cavernVoronoiSample, double cavernNoiseSmall, double cavernNoiseTunnel, double cavernMinInfluence, double cavernMaxInfluence, double cavernScale) {
        //STALAGMITES
        double stalagmiteFrequency = 2.5;
        //Determines the area that the stalagmite effects surrounding it. Higher values = more blend-y stalagmites.
        double minStalagmiteFactor = 1.0;
        double stalagmiteSize = MathUtils.mapRange(0, 1, 0.4F, 0.9F, (float) ((altNoise + 1) * 0.5F));
        double stalagmiteNoise1 = Math.max(Math.min(Math.max(MathUtils.ease((float) (stalagmiteNoise.GetNoise(x * stalagmiteFrequency, y * 0.1 * stalagmiteFrequency, z * stalagmiteFrequency) + stalagmiteSize), MathUtils.EasingType.easeOutSine), 0.0), minStalagmiteFactor) / minStalagmiteFactor, 0.07);

        //BRIDGE CAVERN NOISE
        double cavernStrength = 15000;
        double tunnelEncouragement = 10;
        double lowerBridgeCavernHeightClamping = Mth.clamp(MathUtils.mapRange(aquiferLowerRange + 2.0F, aquiferLowerRange + 12.0F, 0, 1, y), 0, 1);
        double intensityDistance = Mth.clamp(MathUtils.mapRange(cavernMinInfluence / cavernScale, cavernMaxInfluence / cavernScale, 0, 1, cavernVoronoiSample.distance()), 0, 1);
        double intensityMultiplier1 = Mth.lerp(intensityDistance, cavernStrength, tunnelEncouragement) * lowerBridgeCavernHeightClamping;
        double intensityMultiplier2 = Mth.lerp(intensityDistance, cavernStrength, cavernStrength * 10) * lowerBridgeCavernHeightClamping;
        double bridgeCavernNoise = Math.max(Math.max(cavernNoiseTunnel * intensityMultiplier1, cavernNoiseSmall * intensityMultiplier2), 0) + Mth.clamp(MathUtils.mapRange((cavernMinInfluence - (cavernMinInfluence / 2)) / cavernScale, cavernMaxInfluence / cavernScale, 0, 1, cavernVoronoiSample.distance()), 0, 1);

        //CAVE SIZE
        double lowerCaveHeightClamping = Mth.clamp(MathUtils.mapRange(minheight + 5.0F, minheight + 20.0F, 0, 1, y), 0, 1);
        double surfaceCaveEntranceBlending = Math.max(MathUtils.mapRange(startHeight - 6, startHeight, 1, 10, y), 1);
        //Determines the area that the liquid shell effects surrounding it. Higher values = more blend-y liquid shells.
        double minLiquidNoiseFactor = 1;
        double liquidShell = Math.min(liquidShellNoise * -1, minLiquidNoiseFactor) / minLiquidNoiseFactor;
        double caveSize = liquidShell * lowerCaveHeightClamping * surfaceCaveEntranceBlending;

        //SPAGHETTI CAVE NOISE
        double spaghettiFrequency1 = 0.7;
        double spaghettiNoise1 = spaghettiCaveNoise1.GetNoise(x * spaghettiFrequency1, y * spaghettiFrequency1, z * spaghettiFrequency1);
        double spaghettiFrequency2 = spaghettiFrequency1 * 1.1;
        double spaghettiNoise2 = spaghettiCaveNoise2.GetNoise(x * spaghettiFrequency2, y * spaghettiFrequency2, z * spaghettiFrequency2);
        double spaghettiNoise = (spaghettiNoise1 * spaghettiNoise1) + (spaghettiNoise2 * spaghettiNoise2);
        double spagCaveSize = 0.006;
        double spagCaveThreshold = caveSize * spagCaveSize * stalagmiteNoise1 * bridgeCavernNoise;

        double thresholdedSpaghettiNoise = spaghettiNoise - spagCaveThreshold;

        //AQUIFER CAVE NOISE
        double thresholdedAquiferCaveNoise = 1.0;
        if (y < aquiferUpperRange + 10) {
            double aquiferSizeThrottling = y < aquiferMidHeight ?
                    MathUtils.ease(MathUtils.mapRange(aquiferLowerRange, aquiferMidHeight, 0.0F, 1.0F, y), MathUtils.EasingType.easeOutBounce):
                    Mth.lerp(MathUtils.bias((altNoise + 1) / 2, 0.2), MathUtils.ease(MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutQuad), MathUtils.ease(MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), MathUtils.EasingType.easeOutBounce));
            double aquiferSizeThreshold = 0.03 * caveSize * aquiferSizeThrottling * Mth.lerp(0.0, stalagmiteNoise1, 1);

            double aquiferCaveNoiseValue = this.aquiferCaveNoise.GetNoise(x, y * 0.125F, z);
            thresholdedAquiferCaveNoise = y > aquiferUpperRange ? 1 : y < aquiferLowerRange ? 1 : (aquiferCaveNoiseValue * aquiferCaveNoiseValue) - aquiferSizeThreshold;
        }

        //return (float) thresholdedAquiferCaveNoise;
        return Math.min(thresholdedAquiferCaveNoise, thresholdedSpaghettiNoise);
    }

    public Pair<Double, BlockState> getLiquidNoiseShell(double liquidNoise, double y, double lowerRange, int liquidHeight, int liquidSurfaceHeight) {
        double liquidNoiseThreshold = 0.08;
        double borderSize = 0.02;

        if (y > liquidHeight + liquidSurfaceHeight + 10) {
            return Pair.of(-1.0D, Blocks.CAVE_AIR.defaultBlockState());
        } else {
            double multiplier = 0.04;
            double adjustedY = (y - liquidHeight) * multiplier;
            adjustedY = adjustedY * adjustedY * Mth.sign(adjustedY);
            adjustedY = Math.min(adjustedY / Math.abs((lowerRange - liquidHeight) * multiplier), adjustedY * -multiplier);

            double shellNoise = Math.abs(liquidNoise) + adjustedY;
            shellNoise -= liquidNoiseThreshold;

            double hBorderSize = borderSize / 2;
            double shell = Math.min(MathUtils.mapRange(0, hBorderSize, 0, 1, shellNoise), MathUtils.mapRange(hBorderSize, 0, 0, 1, shellNoise));

            double heightClamping = (y - (liquidHeight + liquidSurfaceHeight));
            heightClamping *= heightClamping > 0 ? 0.7 : 0.001;
            return Pair.of(shell - heightClamping, ((shellNoise <= 0) || (y > liquidHeight + liquidSurfaceHeight)) ? Blocks.CAVE_AIR.defaultBlockState() : liquidNoise + adjustedY > 0 ? Blocks.WATER.defaultBlockState() : Blocks.WATER.defaultBlockState());
        }
    }


    public void visualizeNoise(ChunkAccess chunkIn, double value, int x, int y, int z, int density) {
        double minRange = 0;
        double maxRange = 1;
        double stepSize = (maxRange - minRange) / 3;
        BlockState state = value > minRange ? Blocks.BLACK_STAINED_GLASS.defaultBlockState() :
                           value > minRange + stepSize ? Blocks.GRAY_STAINED_GLASS.defaultBlockState() :
                           value > minRange + 2 * stepSize ? Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState() :
                                   Blocks.WHITE_STAINED_GLASS.defaultBlockState();
        chunkIn.setBlockState(new BlockPos(x, y, z), state, false);
    }

    public void initNoise(long seed) {
        if (this.seed != seed || this.spaghettiCaveNoise1 == null || this.spaghettiCaveNoise2 == null || this.caveSizeNoise == null || this.aquiferCaveNoise == null || this.stalagmiteNoise == null || this.bridgeCaveNoise == null) {
            this.seed = seed;
            Random rand = new Random(seed);

            this.setupNoise(rand);
        }
    }

    private void setupNoise(Random rand) {
        Clinker.LOGGER.info("setting up noise!");
        this.spaghettiCaveNoise1 = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.spaghettiCaveNoise1.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        this.spaghettiCaveNoise1.SetFrequency(0.04F);

        this.spaghettiCaveNoise1.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.spaghettiCaveNoise1.SetFractalOctaves(3);
        this.spaghettiCaveNoise1.SetFractalLacunarity(0.5);
        this.spaghettiCaveNoise1.SetFractalGain(1.7F);
        this.spaghettiCaveNoise1.SetFractalWeightedStrength(0.0F);

        //this.spaghettiCaveNoise1.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        //this.spaghettiCaveNoise1.SetDomainWarpAmp(100.0F);
        this.spaghettiCaveNoise1.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);


        this.spaghettiCaveNoise2 = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.spaghettiCaveNoise2.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        this.spaghettiCaveNoise2.SetFrequency(0.04F);

        this.spaghettiCaveNoise2.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.spaghettiCaveNoise2.SetFractalOctaves(3);
        this.spaghettiCaveNoise2.SetFractalLacunarity(0.5);
        this.spaghettiCaveNoise2.SetFractalGain(1.7F);
        this.spaghettiCaveNoise2.SetFractalWeightedStrength(0.0F);

        //this.spaghettiCaveNoise2.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        //this.spaghettiCaveNoise2.SetDomainWarpAmp(100.0F);
        this.spaghettiCaveNoise2.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);


        this.aquiferCaveNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.aquiferCaveNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.aquiferCaveNoise.SetFrequency(0.04F);
        this.aquiferCaveNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.aquiferCaveNoise.SetFractalOctaves(4);
        this.aquiferCaveNoise.SetFractalGain(0.5F);
        this.aquiferCaveNoise.SetFractalWeightedStrength(0.5F);
        this.aquiferCaveNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.aquiferCaveNoise.SetDomainWarpAmp(100.0F);
        this.aquiferCaveNoise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.stalagmiteNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.stalagmiteNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        this.stalagmiteNoise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
        this.stalagmiteNoise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Add);
        this.stalagmiteNoise.SetFrequency(0.04F);
        this.stalagmiteNoise.SetFractalType(FastNoiseLite.FractalType.None);
        this.stalagmiteNoise.SetFractalOctaves(1);
        this.stalagmiteNoise.SetFractalGain(0.9F);
        this.stalagmiteNoise.SetFractalWeightedStrength(0.9F);
        this.stalagmiteNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.stalagmiteNoise.SetDomainWarpAmp(100.0F);
        this.stalagmiteNoise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.caveSizeNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.caveSizeNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.caveSizeNoise.SetFrequency(0.02F);
        this.caveSizeNoise.SetFractalType(FastNoiseLite.FractalType.None);

        this.liquidNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.liquidNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.liquidNoise.SetFrequency(0.02F);
        this.liquidNoise.SetFractalType(FastNoiseLite.FractalType.None);
        this.liquidNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.liquidNoise.SetDomainWarpAmp(100.0F);

        this.bridgeCaveNoise = new VoronoiGenerator(seed);
    }
}
