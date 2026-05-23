package birsy.clinker.common.world.level.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface HeatReader {
    default void onPacketConsumed(Level level, BlockPos pos, BlockState state) {}
    default void onPacketPassed(Level level, BlockPos pos, BlockState state) {}
}
