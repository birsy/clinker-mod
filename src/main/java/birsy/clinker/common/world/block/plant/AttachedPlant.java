package birsy.clinker.common.world.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface AttachedPlant {
    BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;

    static BlockState getAttachedState(BlockState state, LevelAccessor pLevel, BlockPos pos, Direction growthDirection) {
        boolean attached = pLevel.getBlockState(pos.above()).isFaceSturdy(pLevel, pos, growthDirection, SupportType.FULL);
        if (pLevel.getBlockState(pos.above()).getBlock() instanceof AttachedPlant) return state.setValue(ATTACHED, false);
        return state.setValue(ATTACHED, attached);
    }
}
