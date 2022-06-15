package birsy.clinker.common.block.plant.aspen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;

import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class SwampAspenLogBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    //From the perspective of the log's TOP.
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    //Currently doesn't support the log connections. The code was super convoluted and barely worked - just not doing it is cleaner and probably clearer to the player.
    //TODO: Make the collisions support log connections. Or not? It's pretty fine as-is.
    private static final double AABB_MIN = 4.0D;
    private static final double AABB_MAX = 12.0D;
    private static final VoxelShape X_AXIS_AABB = Block.box(0.0D, AABB_MIN, AABB_MIN, 16.0D, AABB_MAX, AABB_MAX);
    private static final VoxelShape Y_AXIS_AABB = Block.box(AABB_MIN, 0.0D, AABB_MIN, AABB_MAX, 16.0D, AABB_MAX);
    private static final VoxelShape Z_AXIS_AABB = Block.box(AABB_MIN, AABB_MIN, 0.0D, AABB_MAX, AABB_MAX, 16.0D);
    public static final Map<Direction.Axis, VoxelShape> SHAPE_BY_AXIS = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.Axis.class), (enumMap) -> {
        enumMap.put(Direction.Axis.X, X_AXIS_AABB);
        enumMap.put(Direction.Axis.Y, Y_AXIS_AABB);
        enumMap.put(Direction.Axis.Z, Z_AXIS_AABB);
    }));

    public SwampAspenLogBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(AXIS, Direction.Axis.Y).setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState baseState = super.getStateForPlacement(pContext);
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());

        for (Direction direction : Direction.values()) {
            BlockPos facingPos = pContext.getClickedPos().relative(direction);
            BlockState facingState = pContext.getLevel().getBlockState(facingPos);

            baseState = updateShape(baseState, direction, facingState, pContext.getLevel(), pContext.getClickedPos(), facingPos);
        }

        return baseState.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_AXIS.get(pState.getValue(AXIS));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            //pLevel.getLi().scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        BooleanProperty property = directionToProperty(getLocalDirection(pState.getValue(AXIS), pFacing));
        if (property != null) {
            if (pFacingState.getBlock() instanceof SwampAspenLogBlock) {
                return pState.setValue(property, pFacingState.getValue(AXIS) == pFacing.getAxis() && pFacingState.getValue(AXIS) != pState.getValue(AXIS));
            } else {
                return pState.setValue(property, false);
            }
        }

        return pState;
    }


    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AXIS, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }



    //Gets the global direction for a given LOG CONNECTION DIRECTION, given the log's AXIS.
    public static Direction getGlobalDirection(Direction.Axis axis, Direction direction) {
        switch (axis) {
            case X:
                return direction.getClockWise(Direction.Axis.Y).getClockWise(Direction.Axis.Z);
            case Y:
                return direction;
            default:
               return direction.getClockWise(Direction.Axis.X);
        }
    }

    //Gets the LOG CONNECTION DIRECTION for a given global direction, given the log's AXIS.
    //Returns null if no LOG CONNECTION DIRECTION can be calculated (the global direction is facing the same way as the log's AXIS)
    public static Direction getLocalDirection(Direction.Axis axis, Direction direction) {
        if (direction.getAxis() != axis) {
            switch (axis) {
                case X:
                    return direction.getCounterClockWise(Direction.Axis.Z).getCounterClockWise(Direction.Axis.Y);
                case Y:
                    return direction;
                case Z:
                    return direction.getCounterClockWise(Direction.Axis.X);
            }
        }

        return null;
    }

    private BooleanProperty directionToProperty(Direction direction) {
        if (direction == null) {
            return null;
        } else {
            switch (direction) {
                case NORTH:
                    return this.NORTH;
                case SOUTH:
                    return this.SOUTH;
                case EAST:
                    return this.EAST;
                case WEST:
                    return this.WEST;
                default:
                    return null;
            }
        }
    }
}