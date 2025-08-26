package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;

public class AshSteppeSurfaceShaper implements SurfaceShaper {
    @Override
    public double surfaceDensity(int x, int y, int z, NoiseComputerContext context) {
        double unmodifiedY = y;
        double heightFactor = 0.3F;

        double yScale = 0.2F;
        double scaledSeaLevel = 150*yScale;
        y *= yScale;

        if (y - scaledSeaLevel > 2.0F / heightFactor) return -1.0F;

        double freq = 0.2F;
        double baseNoise = NOISE.get(x * freq, y * freq, z * freq);
        freq = 0.8F;
        double detailNoise = NOISE.get(x * freq, y * freq + 800.0F, z * freq);

        double totalNoise = (1.0F - Math.abs(detailNoise))*0.2F - 1.0F;

        freq = 0.2F;
        double cliffExistenceNoise = NOISE.get(x * freq, z * freq);
        double threshold = 0.7F;
        cliffExistenceNoise -= threshold;
        if (cliffExistenceNoise < 0.0) {
            cliffExistenceNoise *= 3.0F;
        }

        // terraces
        for (int i = 2; i < 5; i++) {
            double tFreq = 0.6F;
            double terracedNoise = baseNoise + NOISE.get(x * tFreq, y * tFreq + (i * 500), z * tFreq);
            tFreq = 1.0F;
            terracedNoise += NOISE.get(x * tFreq, y * tFreq + (i * 500), z * tFreq) * 0.4F;
            terracedNoise = terrace(terracedNoise + cliffExistenceNoise, i, 0.2F, 0.95F);
            if (terracedNoise > totalNoise) {
                totalNoise = terracedNoise;
            }
        }

        return (totalNoise + baseNoise * 0.1F + detailNoise * 0.1F) - ((y - scaledSeaLevel) * heightFactor);
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
