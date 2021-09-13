package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class CaveSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
    private long seed;
    private FastNoiseLite spaghettiCaveNoise1;
    private FastNoiseLite spaghettiCaveNoise2;
    private FastNoiseLite caveSizeNoise;

    private FastNoiseLite aquiferCaveNoise;


    public CaveSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        startHeight += 90;
        float yMultiplier = 1.4F;
        float averageSpaghettiCaveSize = 0.002F;
        float minSpaghettiCaveSizeDeviation = -0.0019F;
        float maxSpaghettiCaveSizeDeviation = 0.002F;

        float averageAquiferCaveSize = 0.005F;
        float minAquiferCaveSizeDeviation = -0.01F;
        float maxAquiferCaveSizeDeviation = 0.01F;

        int aquiferUpperRange = (int) (50 - (this.caveSizeNoise.GetNoise((x + 682) * 0.5F, (z + 682) * 0.5F) * 12));
        int aquiferLowerRange = 3;
        int aquiferMidHeight = ((aquiferUpperRange + aquiferLowerRange) / 2) - 5;

        int maxHeight = startHeight + 4;
        final int waterLevel = 20;
        BlockPos.Mutable blockPos = new BlockPos.Mutable(x, 255, z);
        for (int y = startHeight; y > 0; y--) {
            blockPos.setPos(x, y, z);
            float heightClamping = MathHelper.clamp(MathUtils.mapRange(4.0F, 20.0F, 0.0F, 1.0F, y), 0, 1);
            heightClamping *= MathHelper.clamp(MathUtils.mapRange(startHeight - 16, startHeight, 1.0F, 0.0F, y), 0, 1);
            float distanceBelowWaterLevel = MathHelper.clamp(MathUtils.ease(MathUtils.mapRange(4.0F, waterLevel + 3.0F, 1.0F, 0.0F, y), MathUtils.EasingType.easeInOutQuint), 0, 1.0F) * 1.5F;

            float distanceToAquifer = MathHelper.clamp(MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 0.0F, 1.0F, y), 0, 1);


            float spaghettiSizeThreshold = this.caveSizeNoise.GetNoise(x, y, z);
            spaghettiSizeThreshold *= spaghettiSizeThreshold < 0 ? Math.abs(minSpaghettiCaveSizeDeviation) : Math.abs(maxSpaghettiCaveSizeDeviation);
            spaghettiSizeThreshold += averageSpaghettiCaveSize;
            spaghettiSizeThreshold *= heightClamping * distanceToAquifer;

            float spaghettiCaveNoiseValue1 = this.spaghettiCaveNoise1.GetNoise(x, y * yMultiplier, z);
            float spaghettiCaveNoiseValue2 = this.spaghettiCaveNoise2.GetNoise(x, y * yMultiplier, z);


            //Some uhh math magic don't worry about it. Makes caves funny and noodly!
            float thresholdedSpaghettiCaveNoise = (MathUtils.invert(spaghettiCaveNoiseValue1 * spaghettiCaveNoiseValue1 + spaghettiCaveNoiseValue2 * spaghettiCaveNoiseValue2) - MathUtils.invert(spaghettiSizeThreshold)) / spaghettiSizeThreshold;

            float thresholdedAquiferCaveNoise = 1.0F;
            if (y < aquiferUpperRange + 10) {
                float aquiferSizeThrottling = MathUtils.ease(y < aquiferMidHeight ? MathHelper.clamp(MathUtils.mapRange(aquiferLowerRange, aquiferMidHeight, 0.0F, 1.0F, y), 0, 1) : MathHelper.clamp(MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), 0, 1), MathUtils.EasingType.easeOutQuad);
                float aquiferSizeThreshold = this.caveSizeNoise.GetNoise(x, y + 123, z + 123);
                aquiferSizeThreshold *= aquiferSizeThreshold < 0 ? Math.abs(minAquiferCaveSizeDeviation) : Math.abs(maxAquiferCaveSizeDeviation);
                aquiferSizeThreshold += averageAquiferCaveSize;
                aquiferSizeThreshold *= aquiferSizeThrottling;

                float aquiferCaveNoiseValue = this.aquiferCaveNoise.GetNoise(x, y * 0.125F, z);
                thresholdedAquiferCaveNoise = y > aquiferUpperRange ? 1 : y < aquiferLowerRange ? 1 : (MathUtils.invert(aquiferCaveNoiseValue * aquiferCaveNoiseValue) - MathUtils.invert(aquiferSizeThreshold)) / aquiferSizeThreshold;
            }

            if (MathUtils.smoothMin(thresholdedAquiferCaveNoise, thresholdedSpaghettiCaveNoise, 2.0F) + distanceBelowWaterLevel > 0.0F || y < 4) {
                if (y < startHeight) {
                    chunkIn.setBlockState(blockPos, defaultBlock, false);
                }
            } else {
                chunkIn.setBlockState(blockPos, y > waterLevel ? Blocks.CAVE_AIR.getDefaultState() : defaultFluid, false);
            }
        }
    }

    public float[][][] getAquiferCaveNoiseValue(float x, float y, float z, float sampleSize) {
        float[][][] valueMatrix = new float[3][3][3];
        for (int locX = -1; locX < 1; locX++) {
            for (int locY = -1; locY < 1; locY++) {
                for (int locZ = -1; locZ < 1; locZ++) {
                    valueMatrix[locX + 1][locY + 1][locZ + 1] = this.aquiferCaveNoise.GetNoise(x + (locX * sampleSize), y + (locY * sampleSize), z + (locZ * sampleSize));
                }
            }
        }

        return valueMatrix;
    }

    @Override
    public void setSeed(long seed) {
        if (this.seed != seed || this.spaghettiCaveNoise1 == null || this.spaghettiCaveNoise2 == null || this.caveSizeNoise == null || this.aquiferCaveNoise == null) {
            this.seed = seed;
            Random rand = new Random(seed);

            this.initNoise(rand);
        }
    }

    private void initNoise(Random rand) {
        this.spaghettiCaveNoise1 = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.spaghettiCaveNoise1.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.spaghettiCaveNoise1.SetFrequency(0.04F);
        this.spaghettiCaveNoise1.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.spaghettiCaveNoise1.SetFractalOctaves(2);
        this.spaghettiCaveNoise1.SetFractalGain(0.9F);
        this.spaghettiCaveNoise1.SetFractalWeightedStrength(0.8F);
        this.spaghettiCaveNoise1.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.spaghettiCaveNoise1.SetDomainWarpAmp(100.0F);
        this.spaghettiCaveNoise1.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.spaghettiCaveNoise2 = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.spaghettiCaveNoise2.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.spaghettiCaveNoise2.SetFrequency(0.04F);
        this.spaghettiCaveNoise2.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.spaghettiCaveNoise2.SetFractalOctaves(2);
        this.spaghettiCaveNoise2.SetFractalGain(0.9F);
        this.spaghettiCaveNoise2.SetFractalWeightedStrength(0.8F);
        this.spaghettiCaveNoise2.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.spaghettiCaveNoise2.SetDomainWarpAmp(100.0F);
        this.spaghettiCaveNoise2.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.aquiferCaveNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.aquiferCaveNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.aquiferCaveNoise.SetFrequency(0.04F);
        this.aquiferCaveNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.aquiferCaveNoise.SetFractalOctaves(4);
        this.aquiferCaveNoise.SetFractalGain(0.9F);
        this.aquiferCaveNoise.SetFractalWeightedStrength(0.9F);
        this.aquiferCaveNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.aquiferCaveNoise.SetDomainWarpAmp(100.0F);
        this.aquiferCaveNoise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.caveSizeNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.caveSizeNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.caveSizeNoise.SetFrequency(0.02F);
        this.caveSizeNoise.SetFractalType(FastNoiseLite.FractalType.None);
    }


}
