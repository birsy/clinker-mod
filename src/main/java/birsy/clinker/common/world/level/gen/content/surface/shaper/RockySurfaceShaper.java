package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.util.MathUtils;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.*;

public class RockySurfaceShaper extends SimpleSurfaceShaper {
    final NoiseComputer heightmapComputer;
    final double cliffMultiplier, heightmapOffset;

    public RockySurfaceShaper(NoiseComputer heightmapComputer, double cliffMultiplier, double heightmapOffset) {
        this.heightmapComputer = heightmapComputer;
        this.cliffMultiplier = cliffMultiplier;
        this.heightmapOffset = heightmapOffset;
    }

    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(heightmapComputer);
    }

    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return context.retrieve(heightmapComputer, x, 0, z) + heightmapOffset;
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[4]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D_ALT[5]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D_ALT[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[8]);

        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE[5]);
    }

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

        double surface = context.retrieve(BASE_NOISE_2D[7], x, y, z);
        surface *= 8;

        double verticalVarianceNoise = context.retrieve(BASE_NOISE[5], x, y, z) * 5;

        double cliffHeight = (-MathUtils.smoothMinExpo(-cliff0, -cliff1, 10) + verticalVarianceNoise) * biomeWeight;
        double surfaceHeight = -MathUtils.smoothMinExpo(-cliffHeight, -surface, 5);
        return (y - heightmapHeight) + baseNoise - surfaceHeight * cliffMultiplier;
    }
}
