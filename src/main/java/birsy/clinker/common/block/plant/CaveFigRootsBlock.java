package birsy.clinker.common.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.ScheduledTick;

import javax.annotation.Nullable;

public class CaveFigRootsBlock extends PipeBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public CaveFigRootsBlock(Properties properties) {
        super(0.1875F, properties);
        registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, true));
    }

    public BlockState getBlockStateAtPosition(LevelAccessor level, BlockPos pos, BlockState state) {
        /*boolean isVerticalLog = false;
        if (canConnectWith(level.getBlockState(pos.above()))) {
            state.setValue(PipeBlock.UP, true);
            isVerticalLog = true;
        } else {
            state.setValue(PipeBlock.UP, false);
        }

        if (level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.DOWN)) {
            state.setValue(PipeBlock.DOWN, true);
        } else {
            state.setValue(PipeBlock.DOWN, false);
            isVerticalLog = false;
        }

        if (!isVerticalLog) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState nState = level.getBlockState(pos.relative(direction));
                state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), false);
                if (canConnectWith(nState)) {
                    if (canConnect(nState)) {
                        state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), true);
                    }
                }
            }
        } else {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), false);
            }
        }

        return state;*/
        for (Direction direction : Direction.values()) {
            BlockState nState = level.getBlockState(pos.relative(direction));
            state.setValue(PROPERTY_BY_DIRECTION.get(direction), false);
            if (canConnectWith(nState)) {
                if (canConnect(nState)) {
                    state.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
                }
            }
        }
        return state;
    }

    public boolean canConnectWith(BlockState state) {
        return state.is(this) || state.getBlock() == ClinkerBlocks.CAVE_FIG_STEM.get();
    }

    public boolean canConnect(BlockState state) {
        return !(state.getValue(PipeBlock.UP) && state.getValue(PipeBlock.DOWN));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (pState.getValue(PROPERTY_BY_DIRECTION.get(direction))) {
                return true;
            }
        }

        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getBlockStateAtPosition(pContext.getLevel(), pContext.getClickedPos(), super.getStateForPlacement(pContext));
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandomSource) {
        if (!pState.canSurvive(pLevel, pPos)) {
            pLevel.destroyBlock(pPos, true);
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (!pState.canSurvive(pLevel, pCurrentPos)) {
            pLevel.getBlockTicks().schedule(new ScheduledTick<>(this, pCurrentPos, 1, 0));
            return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
        } else {
            if (pDirection == Direction.DOWN) {
                return pState.setValue(DOWN, pNeighborState.isFaceSturdy(pLevel, pNeighborPos, Direction.UP) || canConnectWith(pNeighborState));
            } else if (canConnectWith(pNeighborState)) {
                return pState.setValue(PROPERTY_BY_DIRECTION.get(pDirection), true);
            } else {
                return pState.setValue(PROPERTY_BY_DIRECTION.get(pDirection), false);
            }
            //return getBlockStateAtPosition(pLevel, pCurrentPos, pState);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}
