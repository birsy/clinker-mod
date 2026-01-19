package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;

public class TestingSurfaceShaper extends SurfaceShaper {
    final double noiseMultiplier, heightOffset;

    public TestingSurfaceShaper(double noiseMultiplier, double heightOffset) {
        this.heightOffset = heightOffset;
        this.noiseMultiplier = noiseMultiplier;
    }

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_SURFACE_HEIGHT);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE[5]);
    }
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseContext context) {
        double surfaceHeight = context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, x, y, z) + heightOffset;
        double noise = context.retrieve(ClinkerNoiseComputers.BASE_NOISE[5], x, y, z);
        return y - surfaceHeight;// + noise * noiseMultiplier;
    }
}
