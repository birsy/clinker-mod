package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;

public class TestingSurfaceShaper extends SurfaceShaper {
    @Override
    public void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.TEST_SURFACE.get());
    }
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseContext context) {
        return context.retrieve(ClinkerNoiseComputers.TEST_SURFACE, x, y, z);
    }

    @Override
    public int upperBound() { return 48; }
    @Override
    public int lowerBound() { return super.lowerBound(); }
}
