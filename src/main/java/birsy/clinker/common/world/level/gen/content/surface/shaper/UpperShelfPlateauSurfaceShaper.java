package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.UPPER_SHELF_HEIGHT;

public class UpperShelfPlateauSurfaceShaper extends SimpleSurfaceShaper {
    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(UPPER_SHELF_HEIGHT);
    }
    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return context.retrieve(UPPER_SHELF_HEIGHT, x, 0, z) + 40;
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {}
    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        return distanceToSurface;
    }
}
