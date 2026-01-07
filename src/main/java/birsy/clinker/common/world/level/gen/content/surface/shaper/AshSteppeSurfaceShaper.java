package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;
import net.minecraft.util.Mth;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.*;

public class AshSteppeSurfaceShaper extends SurfaceShaper {
    @Override
    public void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_SURFACE_HEIGHT);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[5]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D_ALT[8]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D_ALT[7]);

        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE[5]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE[4]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_ALT[5]);
    }

    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseContext context) {
        double baseSurfaceHeight = context.retrieve(BASE_SURFACE_HEIGHT, x, y, z);

        double ashDunes = Math.abs(context.retrieve(BASE_NOISE_2D[6], x, y, z)) * -2 - 1;
        ashDunes += (Math.abs(context.retrieve(BASE_NOISE_2D[5], x, y, z)) * -2 - 1) * 0.5;
        double weirdCliffs = context.retrieve(BASE_NOISE_2D[7], x, y, z);
        weirdCliffs = Mth.lerp(biomeContribution, 0, weirdCliffs);
        weirdCliffs = (1 - Math.abs(weirdCliffs)) * Math.signum(weirdCliffs) * 5;
        weirdCliffs *= context.retrieve(BASE_NOISE_2D_ALT[7], x, y, z) * 0.5 + 0.5;

        double surfaceHeight = baseSurfaceHeight + ashDunes * 2 + weirdCliffs + 5;
        double ashDunesHeight = Math.min(surfaceHeight, OthershoreBiomeSource.MIDDLE_SHELF_HEIGHT + 30);
        double surfaceDensity = y - surfaceHeight;

        double cliffExistenceNoise = context.retrieve(BASE_NOISE_2D_ALT[8], x, y, z);
        double threshold = 0.9;
        cliffExistenceNoise = Mth.map(cliffExistenceNoise, threshold, 1, 0, -1);

        double cliffDensity = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            double stepHeight = 5 + i * 6;
            int terracedY = (int) (Math.floor(y / stepHeight) * stepHeight);
            terracedY = (int)(terracedY + ashDunes * 2);
            double terraceShape = (y - terracedY) / stepHeight;
            terraceShape *= terraceShape;
            double terracedCliffHeight = Mth.map(terracedY + terraceShape * stepHeight * 0.5, ashDunesHeight, ashDunesHeight + 40, 0, 1);
            terracedCliffHeight *= biomeContribution;
            terracedCliffHeight = Mth.map(terracedCliffHeight, 0, 1, -1, 0);

            double cliffMask = (terracedCliffHeight + cliffExistenceNoise * 0.5) * 40;
            if (cliffMask > 20) continue;
            double cliffHighFreq = context.retrieve(BASE_NOISE[4], x, y, z) * 0.5;
            double cliffNoise = i % 2 == 0 ?
                    context.retrieve(BASE_NOISE[5], x, terracedY, z) + cliffHighFreq :
                    context.retrieve(BASE_NOISE_ALT[5], x, terracedY, z) + cliffHighFreq;
            cliffDensity = Math.min(cliffDensity, cliffMask - cliffNoise * 25);
        }

        return Math.min(surfaceDensity, cliffDensity);
    }

    private static double terrace(double noiseVal, double stepCount, double lowerSteepness, double upperSteepness) {
        double steps = stepCount * 0.5;
        double x = noiseVal * steps + 0.5;
        double expBase = (2 * (x - Math.round(x)));
        double steepness = expBase > 0.0 ? lowerSteepness : upperSteepness;
        double expPower = 1.0 / (1.0 - steepness);
        return ((Math.round(x) + 0.5 * (Math.pow(Math.abs(expBase), expPower) * Math.signum(expBase))) - 0.5) / steps;
    }
}
