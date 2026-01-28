package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;

public abstract class SurfaceShaper {
    public abstract void prefillHeightmapNoiseFields(NoiseFieldCache cache);
    public abstract double getHeight(int x, int z, double weight, NoiseContext context);
    public abstract void fillSurfaceDensityField(NoiseField surfaceDensityField, NoiseFieldCache cache, int chunkHeight, int minX, int minY, int minZ,
                                                 NoiseField heightmapField, NoiseField squaredHeightmapGradientField, NoiseField distanceToHeightmap, int lowerGenBound, int upperGenBound,
                                                 NoiseField biomeWeight);
    public int upperBound() { return 32; }
    public int lowerBound() { return -16;}
}
