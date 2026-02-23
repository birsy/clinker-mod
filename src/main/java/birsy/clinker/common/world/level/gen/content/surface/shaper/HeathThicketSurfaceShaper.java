package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.util.MathUtils;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.*;

public class HeathThicketSurfaceShaper extends SimpleSurfaceShaper {

    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(BASE_ELEVATION);
        cache.fillNoiseField(UPPER_SHELF_ELEVATION);
    }
    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return Math.max(context.retrieve(UPPER_SHELF_ELEVATION, x, 0, z) - 20, context.retrieve(BASE_ELEVATION, x, 0, z));
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[3]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[4]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D_ALT[5]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D_ALT[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[8]);

        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE[5]);
    }

    // todo: make this extend HeathSurfaceShaper, somehow
    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        double baseNoise =  context.retrieve(BASE_NOISE_2D_ALT[5], x, y, z);

        double smallNoise = context.retrieve(BASE_NOISE_2D[4], x, y, z) * 0.13;

        double cliff0Fac = context.retrieve(BASE_NOISE_2D[6], x, 0, z) + smallNoise;
        double cliff0 = Math.pow(Math.abs(cliff0Fac), 1 / 6.0) * Math.signum(cliff0Fac) * 8;

        double cliff1Fac = context.retrieve(BASE_NOISE_2D_ALT[7], x, 0, z) - 0.5 + smallNoise;
        double cliff1 = Math.pow(Math.abs(cliff1Fac), 1 / 6.0) * Math.signum(cliff1Fac) * 30;
        double cliff1Mask = context.retrieve(BASE_NOISE_2D[8], x, 0, z) * 0.5 + 0.5;
        cliff1 *= cliff1Mask;

        double smallNoise2 = context.retrieve(BASE_NOISE_2D[4], x, y, z) * 0.5;
        double cliff2Fac = context.retrieve(BASE_NOISE_2D[4], x, 0, z) - 0.5 + smallNoise2;
        double cliff2 = Math.pow(Math.abs(cliff2Fac), 1 / 6.0) * Math.signum(cliff2Fac) * 20;
        double cliff2MaskFac = context.retrieve(BASE_NOISE_2D[6], x, 0, z) - 0.5;
        double cliff2Mask = Math.pow(Math.abs(cliff2MaskFac), 1 / 12.0) * Math.signum(cliff2MaskFac) * 20;
        cliff2 = Math.min(cliff2, cliff2Mask);

//        double cliff1Fac = context.retrieve(BASE_NOISE_2D_ALT[7], x, 0, z) - 0.5 + smallNoise;
//        double cliff1 = Math.pow(Math.abs(cliff1Fac), 1 / 12.0) * Math.signum(cliff1Fac) * 30;
//        double cliff1Mask = context.retrieve(BASE_NOISE_2D[8], x, 0, z) * 0.5 + 0.5;
//        cliff1 *= cliff1Mask;


        double surface = context.retrieve(BASE_NOISE_2D[7], x, y, z);
        surface *= 8;

        double verticalVarianceNoise = context.retrieve(BASE_NOISE[5], x, y, z) * 5;

        double cliffHeight = -MathUtils.smoothMinExpo(-cliff1, -cliff2, 5);
        cliffHeight = -MathUtils.smoothMinExpo(-cliff0, -cliffHeight, 5);
        cliffHeight = (cliffHeight + verticalVarianceNoise) * biomeWeight;
        double surfaceHeight = -MathUtils.smoothMinExpo(-cliffHeight, -surface, 5);
        return (y - heightmapHeight) + baseNoise - surfaceHeight;
    }
}
