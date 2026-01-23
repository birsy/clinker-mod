package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;

public abstract class SurfaceShaper {
    public abstract void fillSurfaceHeightField (NoiseField surfaceHeightField, NoiseFieldCache cache, int minX, int minZ, NoiseField biomeWeight);
    public abstract void fillSurfaceDensityField(NoiseField surfaceDensityField, NoiseFieldCache cache, int minX, int minY, int minZ,
                                                 NoiseField surfaceHeightField, int lowerGenBound, int upperGenBound,
                                                 NoiseField biomeWeight);
    public int upperBound() { return 32; }
    public int lowerBound() { return -16; }
}
