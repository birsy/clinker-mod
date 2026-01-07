package birsy.clinker.common.world.level.gen.system.fluid;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record FluidLevel(int height, BlockState fluid) {
    public static final FluidLevel EMPTY = new FluidLevel(Integer.MIN_VALUE, Blocks.AIR.defaultBlockState());
}
