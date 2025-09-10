package birsy.clinker.common.world.level.gen.fluid;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;

public interface FluidFiller {
    FluidLevel compute(int x, int y, int z, NoiseComputerContext context);
}
