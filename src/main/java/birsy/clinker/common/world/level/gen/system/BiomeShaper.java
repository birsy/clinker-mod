package birsy.clinker.common.world.level.gen.system;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;

public abstract class BiomeShaper {
    public abstract double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseContext context);
    public void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {}
    public int upperBound() { return 32; }
    public int lowerBound() { return -16; }
}
