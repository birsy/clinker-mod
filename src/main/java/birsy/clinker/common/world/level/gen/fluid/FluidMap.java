package birsy.clinker.common.world.level.gen.fluid;

import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import net.minecraft.world.level.block.state.BlockState;

public interface FluidMap {
    default void precomputeValues(NoiseComputer finalDensityComputer, NoiseComputer waterfallPresenceComputer) {}
    double getBorderDensity(int localX, int localY, int localZ);
    BlockState getFluidState(int x, int y, int z);
}
