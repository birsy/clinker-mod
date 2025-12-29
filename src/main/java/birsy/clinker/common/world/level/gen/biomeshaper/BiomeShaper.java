package birsy.clinker.common.world.level.gen.biomeshaper;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;

public interface BiomeShaper {
    double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context);
}
