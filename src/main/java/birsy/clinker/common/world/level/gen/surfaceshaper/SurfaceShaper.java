package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;

public interface SurfaceShaper {
    double surfaceDensity(int x, int y, int z, NoiseComputerContext context);
}
