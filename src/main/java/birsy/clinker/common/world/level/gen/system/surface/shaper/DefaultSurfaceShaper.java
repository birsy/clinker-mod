package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;

public class DefaultSurfaceShaper extends SurfaceShaper {
    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {}
    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) { return OthershoreGenerationConstants.BASE_SEA_LEVEL * weight; }

    @Override
    public void fillSurfaceDensityField(NoiseField surfaceDensityField, NoiseFieldCache cache, int chunkHeight, int minX, int minY, int minZ, NoiseField heightmapField, NoiseField squaredHeightmapGradientField, NoiseField distanceToHeightmap, int lowerGenBound, int upperGenBound, NoiseField biomeWeight) {
        double[] surfaceDensityArray = surfaceDensityField.array();
        surfaceDensityField.byBlockPadded(lowerGenBound - minY, upperGenBound - minY,
                (index, x, y, z) -> surfaceDensityArray[index] += (distanceToHeightmap.retrieve(x, y, z)) * biomeWeight.retrieve(x, y, z)
        );
    }

    @Override
    public int upperBound() { return 8; }
    @Override
    public int lowerBound() { return -8; }
}
