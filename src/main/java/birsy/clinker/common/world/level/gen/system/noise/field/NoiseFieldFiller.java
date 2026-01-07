package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;

public interface NoiseFieldFiller {
    double compute(int x, int y, int z, NoiseContext context);
}
