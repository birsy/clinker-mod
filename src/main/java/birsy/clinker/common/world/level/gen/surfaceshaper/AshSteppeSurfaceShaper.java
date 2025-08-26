package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import birsy.clinker.core.util.noise.CachedFastNoise;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.Util;

public class AshSteppeSurfaceShaper implements SurfaceShaper {
    private static final CachedFastNoise NOISE = Util.make(() -> {
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

    @Override
    public double surfaceDensity(int x, int y, int z, NoiseComputerContext context) {
        NoiseComputerExecutor cache = context.noiseComputerExecutor();
        NoiseHolder noise = context.noiseHolder();

        double heightFactor = 0.3F;

        double yScale = 0.2F;
        double scaledSurfaceHeight = cache.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER) * yScale;
        double scaledY = y * yScale;

        if (scaledY - scaledSurfaceHeight > 2.0F / heightFactor) return 1.0F;

        double freq = 0.2F;
        double baseNoise = NOISE.get(x * freq, scaledY * freq, z * freq);
        freq = 0.8F;
        double detailNoise = NOISE.get(x * freq, scaledY * freq + 800.0F, z * freq);

        double totalNoise = ((1.0 - Math.abs(detailNoise)) * 2 - 1) * 0.1;

        freq = 0.2F;
        double cliffExistenceNoise = NOISE.get(x * freq, z * freq);
        double threshold = 0.5;
        cliffExistenceNoise -= threshold;
        if (cliffExistenceNoise < 0.0) {
            cliffExistenceNoise *= 3.0F;
        }

        // terraces
        for (int i = 2; i < 5; i++) {
            double tFreq = 0.6F;
            double terracedNoise = baseNoise + NOISE.get(x * tFreq, scaledY * tFreq + (i * 500), z * tFreq);
            tFreq = 1.0F;
            terracedNoise += NOISE.get(x * tFreq, scaledY * tFreq + (i * 500), z * tFreq) * 0.4F;
            terracedNoise = terrace(terracedNoise + cliffExistenceNoise, i, 0.2F, 0.95F);
            if (terracedNoise > totalNoise) {
                totalNoise = terracedNoise;
            }
        }

        return ((scaledY - scaledSurfaceHeight) * heightFactor) - (totalNoise + baseNoise * 0.1F + detailNoise * 0.1F);
    }

    private static double terrace(double noiseVal, double stepCount, double lowerSteepness, double upperSteepness) {
        double steps = stepCount * 0.5F;
        double x = noiseVal * steps + 0.5F;
        double expBase = (2*(x - Math.round(x)));
        double steepness = expBase > 0.0 ? lowerSteepness : upperSteepness;
        double expPower = 1.0f / (1.0f - steepness);
        return (double) ((Math.round(x) + 0.5 * (Math.pow(Math.abs(expBase), expPower) * Math.signum(expBase))) - 0.5F) / steps;
    }
}
