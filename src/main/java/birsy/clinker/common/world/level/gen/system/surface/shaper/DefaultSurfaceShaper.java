package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;

public class DefaultSurfaceShaper extends SurfaceShaper {
    @Override
    public void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_SURFACE_HEIGHT.get());
    }
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseContext context) {
        return y - context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, x, y, z);
    }

    @Override
    public int upperBound() { return 16; }
    @Override
    public int lowerBound() { return -16; }
}
