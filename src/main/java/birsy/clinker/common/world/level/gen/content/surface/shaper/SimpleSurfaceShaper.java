package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;

public abstract class SimpleSurfaceShaper extends SurfaceShaper {
    public abstract void prefillHeightNoiseFields(NoiseFieldCache cache);
    public abstract double surfaceHeight(int x, int z, double biomeContribution, NoiseContext context);

    public abstract void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight);
    public abstract double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseContext context);

    @Override
    public void fillSurfaceHeightField(NoiseField surfaceHeightField, NoiseFieldCache cache, int minX, int minZ, NoiseField biomeWeight) {
        this.prefillHeightNoiseFields(cache);
        NoiseContext context = cache.context;
        double[] surfaceHeightArray = surfaceHeightField.array();
        surfaceHeightField.byBlock(
                (index, x, y, z) -> {
                    double weight = biomeWeight.retrieve(x, y, z);
                    surfaceHeightArray[index] += this.surfaceHeight(x + minX, z + minZ, weight, context) * weight;
                }
        );
    }

    @Override
    public void fillSurfaceDensityField(NoiseField surfaceDensityField, NoiseFieldCache cache, int minX, int minY, int minZ,
                                        NoiseField surfaceHeightField, int lowerGenBound, int upperGenBound,
                                        NoiseField biomeWeight) {
        this.prefillNoiseFields(cache, lowerGenBound, upperGenBound);
        NoiseContext context = cache.context;
        double[] surfaceDensityArray = surfaceDensityField.array();
        surfaceDensityField.byBlock(lowerGenBound - minY, upperGenBound - minY,
                (index, x, y, z) -> {
                    double weight = biomeWeight.retrieve(x, y, z);
                    surfaceDensityArray[index] += this.surfaceDensity(x + minX, y + minY, z + minZ, weight, context) * weight;
                }
        );
    }
}
