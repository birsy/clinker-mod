package birsy.clinker.common.world.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class DriedCloversBlock extends CarpetBlock {
    public DriedCloversBlock(Properties p_152915_) {
        super(p_152915_);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (!this.canSurvive(state, context.getLevel(), context.getClickedPos())) return null;
        return state;
    }

    @Override
    public BlockState updateShape(BlockState p_56113_, Direction p_56114_, BlockState p_56115_, LevelAccessor p_56116_, BlockPos p_56117_, BlockPos p_56118_) {
        if (!p_56113_.canSurvive(p_56116_, p_56117_)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape( p_56113_, p_56114_, p_56115_, p_56116_, p_56117_, p_56118_);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        return !belowState.getCollisionShape(level, pos.below()).getFaceShape(Direction.UP).isEmpty() || belowState.isFaceSturdy(level, pos.below(), Direction.UP);
    }
}
