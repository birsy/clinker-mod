package birsy.clinker.common.world.gen.cave;

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

public class NetherTestSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
    private long seed;
    private FastNoiseLite spaghettiCaveNoise1;
    private FastNoiseLite spaghettiCaveNoise2;
    private FastNoiseLite caveSizeNoise;

    private FastNoiseLite caveNoise;


    public NetherTestSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        startHeight += 90;

        float averageCaveSize = 0.006F;
        float minCaveSizeDeviation = -0.01F;
        float maxCaveSizeDeviation = 0.01F;

        int caveUpperRange = (int) (100 - (this.caveSizeNoise.GetNoise((x + 682) * 0.5F, (z + 682) * 0.5F) * 64));
        int caveLowerRange = 3;
        int caveMidHeight = ((caveUpperRange + caveLowerRange) / 2) - 5;
        
        final int waterLevel = 20;
        BlockPos.Mutable blockPos = new BlockPos.Mutable(x, 255, z);

        for (int y = 255; y > 0; y--) {
            blockPos.setPos(x, y, z);
            float distanceBelowWaterLevel = MathHelper.clamp(MathUtils.ease(MathUtils.mapRange(4.0F, waterLevel + 3.0F, 1.0F, 0.0F, y), MathUtils.EasingType.easeInOutQuint), 0, 1.0F) * 1.5F;
            float thresholdedCaveNoise = 1.0F;

            if (y < caveUpperRange + 10 && y > caveLowerRange) {
                float caveSizeThrottling = MathUtils.ease(y < caveMidHeight ? MathHelper.clamp(MathUtils.mapRange(caveLowerRange, caveMidHeight, 0.0F, 1.0F, y), 0, 1) : MathHelper.clamp(MathUtils.mapRange(caveMidHeight, caveUpperRange, 1.0F, 0.0F, y), 0, 1), MathUtils.EasingType.easeOutQuad);
                float caveSizeThreshold = this.caveSizeNoise.GetNoise(x, y + 123, z + 123);
                caveSizeThreshold *= caveSizeThreshold < 0 ? Math.abs(minCaveSizeDeviation) : Math.abs(maxCaveSizeDeviation);
                caveSizeThreshold += averageCaveSize;
                caveSizeThreshold *= caveSizeThrottling;

                float heightFactor = MathUtils.mapRange(caveLowerRange, caveUpperRange, 0, 1, y);
                float offsetNoise = this.caveNoise.GetNoise((x * 0.125F) + 256, (z * 0.125F) + 256);
                float offsetOrientation = MathHelper.lerp(MathUtils.biasTowardsIntegers((offsetNoise + 1) * 2) * 0.25F, offsetNoise, 0.6F);
                float offsetDistance =  40.0F * heightFactor * (float) Math.pow(this.caveNoise.GetNoise(x * 0.125F, z * 0.125F), 1 / 5);

                float xOffset = MathHelper.sin((float) (offsetOrientation * Math.PI)) * offsetDistance;
                float zOffset = MathHelper.cos((float) (offsetOrientation * Math.PI)) * offsetDistance;

                float caveNoiseValue = this.caveNoise.GetNoise(x + xOffset, y * 0.125F, z + zOffset);
                thresholdedCaveNoise = y > caveUpperRange ? 1 : y < caveLowerRange ? 1 : (MathUtils.invert(caveNoiseValue * caveNoiseValue) - MathUtils.invert(caveSizeThreshold)) / caveSizeThreshold;
            }

            if (thresholdedCaveNoise + distanceBelowWaterLevel > 0.0F || y < 4) {
                if (y < startHeight) {
                    //boolean isBlockLava = random.nextInt(256) == 0;
                    chunkIn.setBlockState(blockPos, defaultBlock, false);
                }
            } else {
                chunkIn.setBlockState(blockPos, y > waterLevel ? Blocks.CAVE_AIR.getDefaultState() : Blocks.LAVA.getDefaultState(), false);
            }
        }
    }

    public float[][][] getCaveNoiseValue(float x, float y, float z, float sampleSize) {
        float[][][] valueMatrix = new float[3][3][3];
        for (int locX = -1; locX < 1; locX++) {
            for (int locY = -1; locY < 1; locY++) {
                for (int locZ = -1; locZ < 1; locZ++) {
                    valueMatrix[locX + 1][locY + 1][locZ + 1] = this.caveNoise.GetNoise(x + (locX * sampleSize), y + (locY * sampleSize), z + (locZ * sampleSize));
                }
            }
        }

        return valueMatrix;
    }

    @Override
    public void setSeed(long seed) {
        if (this.seed != seed || this.spaghettiCaveNoise1 == null || this.spaghettiCaveNoise2 == null || this.caveSizeNoise == null || this.caveNoise == null) {
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

        this.caveNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.caveNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.caveNoise.SetFrequency(0.04F);
        this.caveNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.caveNoise.SetFractalOctaves(4);
        this.caveNoise.SetFractalGain(0.9F);
        this.caveNoise.SetFractalWeightedStrength(0.9F);
        this.caveNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.caveNoise.SetDomainWarpAmp(100.0F);
        this.caveNoise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.caveSizeNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.caveSizeNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.caveSizeNoise.SetFrequency(0.02F);
        this.caveSizeNoise.SetFractalType(FastNoiseLite.FractalType.None);
    }


}
