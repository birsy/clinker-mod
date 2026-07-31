package birsy.clinker.common.block.plant;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class ThinLogBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // from the perspective of the log's top
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().getAxis().isHorizontal())
            .collect(Util.toMap());

    protected static final Map<Direction.Axis, Map<Direction, Integer>> CLOCKWISE_TURNS = Util.make(() -> {
        Map<Direction.Axis, Map<Direction, Integer>> map = new EnumMap<>(Direction.Axis.class);
        for (Direction.Axis axis : Direction.Axis.VALUES) {
            Map<Direction, Integer> axisMap = new EnumMap<>(Direction.class);
            Direction dir = axis.isVertical() ? Direction.NORTH : Direction.UP;
            for (int i = 0; i < 4; i++) {
                axisMap.put(dir, i);
                dir = dir.getClockWise(axis);
            }
            map.put(axis, axisMap);
        }
        return map;
    });

    protected final VoxelShape[] cachedShapes = new VoxelShape[3 * 16];

    public ThinLogBlock(Properties properties, double radius) {
        super(properties);
        this.buildShapes(radius);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(AXIS, Direction.Axis.Y)
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST, false)
                        .setValue(WEST, false)
                        .setValue(WATERLOGGED, false)
        );
    }


    private static final Map<Direction, Integer> DIRECTION_TO_MASK_BIT = Util.make(() -> {
        Map<Direction, Integer> map = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL)
            map.put(direction, 1 << direction.get2DDataValue());
        return map;
    });
    private void buildShapes(double radius) {
        double min = 8.0 - radius;
        double max = 8.0 + radius;

        Map<Direction, VoxelShape> shapeByGlobalDirection = Map.of(
                Direction.WEST,  Block.box(0, min, min, min, max, max),
                Direction.DOWN,  Block.box(min, 0, min, max, min, max),
                Direction.NORTH, Block.box(min, min, 0, max, max, min),

                Direction.EAST,  Block.box(max, min, min, 16, max, max),
                Direction.UP,    Block.box(min, max, min, max, 16, max),
                Direction.SOUTH, Block.box(min, min, max, max, max, 16)
        );

        for (Direction.Axis axis : Direction.Axis.VALUES) {
            VoxelShape core = switch (axis) {
                case X -> Block.box(0, min, min, 16, max, max);
                case Y -> Block.box(min, 0, min, max, 16, max);
                case Z -> Block.box(min, min, 0, max, max, 16);
            };
            for (int mask = 0; mask < 16; mask++) {
                VoxelShape shape = core;
                for (Direction localDir : Direction.Plane.HORIZONTAL) {
                    if ((mask & DIRECTION_TO_MASK_BIT.get(localDir)) == 0) continue;
                    Direction globalDir = getGlobalDirection(axis, localDir);
                    shape = Shapes.joinUnoptimized(shape, shapeByGlobalDirection.get(globalDir), BooleanOp.OR);
                }
                cachedShapes[axis.ordinal() * 16 + mask] = shape.optimize();
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int mask = 0;
        for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet())
            if (state.getValue(entry.getValue())) mask |= DIRECTION_TO_MASK_BIT.get(entry.getKey());
        return cachedShapes[state.getValue(AXIS).ordinal() * 16 + mask];
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        if (state == null) return state;

        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        state = state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        Direction.Axis axis = state.getValue(AXIS);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Direction globalDirection = getGlobalDirection(axis, direction);
            BlockState neighborState = pContext.getLevel().getBlockState(pContext.getClickedPos().relative(globalDirection));
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), shouldConnect(state, neighborState, globalDirection));
        }

        return state;
    }

    @Override
    protected BlockState updateShape(BlockState initialState, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState state = super.updateShape(initialState, direction, neighborState, level, pos, neighborPos);

        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        Direction.Axis logAxis = state.getValue(AXIS);
        Direction localDirection = getLocalDirection(logAxis, direction);
        if (localDirection.getAxis().isVertical()) return state;

        BooleanProperty directionProperty = PROPERTY_BY_DIRECTION.get(localDirection);
        state = state.setValue(directionProperty, shouldConnect(state, neighborState, direction));

        return state;
    }

    protected static boolean shouldConnect(BlockState state, BlockState neighborState, Direction neighborDirection) {
        if (!neighborState.is(state.getBlock())) return false;
        if (neighborDirection.getAxis() == state.getValue(AXIS)) return false;
        if (neighborState.getValue(AXIS) != neighborDirection.getAxis()) return false;
        return true;
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

    public static Direction getGlobalDirection(Direction.Axis logAxis, Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y)
            return direction;
        if (logAxis == Direction.Axis.Y)
            return direction;
        return turnClockwise(Direction.UP, logAxis, CLOCKWISE_TURNS.get(Direction.Axis.Y).get(direction));
    }

    public static Direction getLocalDirection(Direction.Axis logAxis, Direction direction) {
        if (direction.getAxis() == logAxis)
            return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? Direction.UP : Direction.DOWN;
        if (logAxis == Direction.Axis.Y)
            return direction;

        return turnClockwise(Direction.NORTH, Direction.Axis.Y, CLOCKWISE_TURNS.get(logAxis).get(direction));
    }

    public static Direction turnClockwise(Direction direction, Direction.Axis axis, int count) {
        if (direction.getAxis() == axis) return direction;
        for (int i = 0; i < count; i++)
            direction = direction.getClockWise(axis);
        return direction;
    }
}
