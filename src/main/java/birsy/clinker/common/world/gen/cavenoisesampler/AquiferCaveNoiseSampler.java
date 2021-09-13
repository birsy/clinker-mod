package birsy.clinker.common.world.gen.cavenoisesampler;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import java.util.Random;

public class AquiferCaveNoiseSampler extends CaveNoiseSampler {
    private FastNoiseLite caveSizeNoise;
    private FastNoiseLite aquiferCaveNoise;

    public AquiferCaveNoiseSampler() {
        super();
    }

    public float sampleCaveNoise(Random random, IChunk chunkIn, Biome biomeIn, int x, int y, int z, int surfaceHeight, int seaLevel, long seed) {
        float averageAquiferCaveSize = 0.01F;
        float minAquiferCaveSizeDeviation = -0.01F;
        float maxAquiferCaveSizeDeviation = 0.01F;

        int aquiferUpperRange = (int) (27 + ((this.caveSizeNoise.GetNoise((x + 682) * 0.5F, (z + 682) * 0.5F)) + 1) * 12);
        int aquiferMidHeight = Math.min(20, aquiferUpperRange - 5);
        int aquiferLowerRange = 3;

        int maxHeight = surfaceHeight + 4;
        final int waterLevel = 15;

        float heightClamping = MathHelper.clamp(MathUtils.mapRange(4.0F, 20.0F, 0.0F, 1.0F, y), 0, 1);
        heightClamping *= MathHelper.clamp(MathUtils.mapRange(surfaceHeight - 16, surfaceHeight, 1.0F, 0.0F, y), 0, 1);
        float distanceBelowWaterLevel = MathHelper.clamp(MathUtils.ease(MathUtils.mapRange(4.0F, waterLevel + 3.0F, 1.0F, 0.0F, y), MathUtils.EasingType.easeInOutQuint), 0, 1.0F) * 1.5F;
        float aquiferSizeThrottling = y < aquiferMidHeight ? MathHelper.clamp(MathUtils.mapRange(aquiferLowerRange, aquiferMidHeight, 0.0F, 1.0F, y), 0, 1) : MathHelper.clamp(MathUtils.mapRange(aquiferMidHeight, aquiferUpperRange, 1.0F, 0.0F, y), 0, 1);
        float aquiferSizeThreshold = this.caveSizeNoise.GetNoise(x, y + 123, z + 123);
        aquiferSizeThreshold *= aquiferSizeThreshold < 0 ? Math.abs(minAquiferCaveSizeDeviation) : Math.abs(maxAquiferCaveSizeDeviation);
        aquiferSizeThreshold += averageAquiferCaveSize;
        aquiferSizeThreshold *= aquiferSizeThrottling;

        float aquiferCaveNoiseValue = this.aquiferCaveNoise.GetNoise(x, y * 0.125F, z);
        float thresholdedAquiferCaveNoise = y > aquiferUpperRange ? 1 : y < aquiferLowerRange ? 1 : (MathUtils.invert(aquiferCaveNoiseValue * aquiferCaveNoiseValue) - MathUtils.invert(aquiferSizeThreshold)) / aquiferSizeThreshold;

        return thresholdedAquiferCaveNoise + distanceBelowWaterLevel;
    }

    @Override
    public void setSeed(long seed) {
        super.setSeed(seed);
        if (this.caveSizeNoise == null || this.aquiferCaveNoise == null) {
            Random rand = new Random(seed);
            initNoise(rand);
        }
    }

    private void initNoise(Random rand) {
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
