package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldType;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;
import birsy.clinker.core.Clinker;

public abstract class SimpleSurfaceShaper extends SurfaceShaper {
    public abstract void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight);
    public abstract double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context);

    @Override
    public void fillSurfaceDensityField(NoiseField surfaceDensityField, NoiseFieldCache cache, int chunkHeight, int minX, int minY, int minZ,
                                        NoiseField heightmapField, NoiseField heightmapGradientField,
                                        NoiseField distanceToHeightmap, int localLowerGenBound, int localUpperGenBound,
                                        NoiseField biomeWeight) {
        this.prefillDensityNoiseFields(cache, localLowerGenBound, localUpperGenBound);

        NoiseField biomeDensityField = this.noiseFieldType().create(chunkHeight, 0);

        double[] biomeDensityArray = biomeDensityField.array();
        NoiseContext context = cache.context;
        biomeDensityField.byBlockPadded(localLowerGenBound - minY, localUpperGenBound - minY,
                (index, x, y, z) -> {
                    double weight = biomeWeight.retrieve(x, y, z);
                    double heightmap = heightmapField.retrieve(x, y, z),
                           heightmapGradient = heightmapGradientField.retrieve(x, y, z),
                           distanceToSurface = distanceToHeightmap.retrieve(x, y, z);
                    biomeDensityArray[index] += this.surfaceDensity(x + minX, y + minY, z + minZ, heightmap, heightmapGradient, distanceToSurface, weight, context) * weight;
                }
        );

        double[] surfaceDensityArray = surfaceDensityField.array();
        surfaceDensityField.byBlockPadded(localLowerGenBound - minY, localUpperGenBound - minY,
                (index, x, y, z) -> surfaceDensityArray[index] += biomeDensityField.retrieve(x, y, z)
        );
    }

    public NoiseFieldType<?> noiseFieldType() {
        return NoiseFieldTypes.COARSE;
    }
}
