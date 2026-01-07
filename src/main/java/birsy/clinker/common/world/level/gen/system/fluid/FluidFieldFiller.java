package birsy.clinker.common.world.level.gen.system.fluid;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;

public interface FluidFieldFiller {
    FluidLevel compute(int x, int y, int z, NoiseContext context);
}
