package birsy.clinker.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface TwizzlingVine {
    BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    static BlockState getAttachedState(BlockState state, LevelAccessor pLevel, BlockPos pos) {
        return state.setValue(ATTACHED, pLevel.getBlockState(pos.above()).isFaceSturdy(pLevel, pos, Direction.DOWN, SupportType.FULL));
    }

}
