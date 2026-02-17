package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;

public abstract class SimpleSurfaceShaper extends SurfaceShaper {
    public abstract void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight);
    public abstract double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context);

    @Override
    public void fillSurfaceDensityField(NoiseField surfaceDensityField, NoiseFieldCache cache, int chunkHeight, int minX, int minY, int minZ,
                                        NoiseField heightmapField, NoiseField heightmapGradientField,
                                        NoiseField distanceToHeightmap, int lowerGenBound, int upperGenBound,
                                        NoiseField biomeWeight) {
        this.prefillDensityNoiseFields(cache, lowerGenBound, upperGenBound);

        NoiseContext context = cache.context;
        double[] surfaceDensityArray = surfaceDensityField.array();
        surfaceDensityField.byBlock(lowerGenBound - minY, upperGenBound - minY,
                (index, x, y, z) -> {
                    double weight = biomeWeight.retrieve(x, y, z);
                    double heightmap = heightmapField.retrieve(x, y, z),
                           heightmapGradient = heightmapGradientField.retrieve(x, y, z),
                           distanceToSurface = distanceToHeightmap.retrieve(x, y, z);
                    surfaceDensityArray[index] += this.surfaceDensity(x + minX, y + minY, z + minZ, heightmap, heightmapGradient, distanceToSurface, weight, context) * weight;
                }
        );
    }
}
