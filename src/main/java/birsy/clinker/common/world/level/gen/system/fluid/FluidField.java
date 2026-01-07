package birsy.clinker.common.world.level.gen.system.fluid;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface FluidField {
    BlockState AIR = Blocks.AIR.defaultBlockState();

    default void precomputeValues(NoiseField finalDensityField, NoiseField waterfallPresenceField) {}
    double getBorderDensity(int localX, int localY, int localZ);
    BlockState getFluidState(int x, int y, int z);

}
