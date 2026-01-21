package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;

public abstract class SurfaceShaper {
    public abstract void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight);
    public abstract double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseContext context);
    public int upperBound() { return 32; }
    public int lowerBound() { return -16; }
    public int height() { return -16; }
}
