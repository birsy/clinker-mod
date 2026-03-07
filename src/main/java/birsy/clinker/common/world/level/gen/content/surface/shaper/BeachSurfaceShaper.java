package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.BEACH_HEIGHT;
import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.SEA_FLOOR_HEIGHT;

public class BeachSurfaceShaper extends SimpleSurfaceShaper {
    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(BEACH_HEIGHT);
    }
    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return context.retrieve(BEACH_HEIGHT, x, 0, z);
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {}
    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        return distanceToSurface;
    }
}
