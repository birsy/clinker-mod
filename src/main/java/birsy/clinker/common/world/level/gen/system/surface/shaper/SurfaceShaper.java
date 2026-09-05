package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatingField;

public abstract class SurfaceShaper {
    public abstract void prefillHeightmapNoiseFields(NoiseFieldCache cache);
    public abstract double getHeight(int x, int z, double weight, NoiseContext context);
    public abstract void fillSurfaceDensityField(InterpolatingField surfaceDensityField, NoiseFieldCache cache, int chunkHeight, int minX, int minY, int minZ,
                                                 InterpolatingField heightmapField, InterpolatingField squaredHeightmapGradientField, InterpolatingField distanceToHeightmap, int lowerGenBound, int upperGenBound,
                                                 InterpolatingField biomeWeight);
    public int upperBound() { return 32; }
    public int lowerBound() { return -16;}
}
