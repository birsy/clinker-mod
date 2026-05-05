package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.*;

public class AshSteppeSurfaceShaper extends SimpleSurfaceShaper {
    @Override
    public int upperBound() {
        return 48;
    }

    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(UPPER_SHELF_HEIGHT);
    }
    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return context.retrieve(UPPER_SHELF_HEIGHT, x, 0, z);
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D[5]);

        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, BASE_NOISE_2D_ALT[6]);
    }

    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        double baseNoise = context.retrieve(BASE_NOISE_2D[6], x, y, z);
        double stratifiedBaseNoise = Mth.floor(baseNoise * 2.0) / 2.0;

        double detailNoise = context.retrieve(BASE_NOISE_2D[5], x, y, z);

        double heightmap = stratifiedBaseNoise * 10 + detailNoise * 2;

        heightmap *= Mth.map(context.retrieve(BASE_NOISE_2D[7], x, y, z), -1, 1, 0, 1);

        double baseTerrain = (y - heightmapHeight) + heightmap;

        double rockMask = Mth.map(context.retrieve(BASE_NOISE_2D_ALT[6], x, y, z), -1, -0.5, -1, 0);
        double rock = -MathUtils.smoothMinExpo(-rockMask, -(Math.abs(y - heightmapHeight + heightmap) - 8), 2);

        return Math.min(baseTerrain, rock);
    }
}
