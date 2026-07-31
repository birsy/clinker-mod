package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDirectionalStemBlock extends PipeBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public AbstractDirectionalStemBlock(float radius, Properties properties) {
        super(radius, properties);
        registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(WATERLOGGED, false)
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST,  false)
                        .setValue(WEST,  false)
                        .setValue(UP,    false)
                        .setValue(DOWN,  false)
        );
    }

    public abstract boolean shouldConnect(LevelAccessor level, BlockPos pos, BlockState currentState, Direction neighborDirection, BlockPos neighborPos, BlockState neighborState);

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        boolean connected = false;
        BlockPos.MutableBlockPos neighborPos = context.getClickedPos().mutable();
        for (Direction direction : Direction.values()) {
            neighborPos = neighborPos.set(context.getClickedPos()).move(direction);
            boolean shouldConnect = shouldConnect(context.getLevel(), context.getClickedPos(), state, direction, neighborPos, context.getLevel().getBlockState(neighborPos));
            if (shouldConnect) connected = true;
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), shouldConnect);
        }

        if (!connected)
            return null;

        state = state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        boolean hasAttachment = false;
        for (Direction dir : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(dir))) {
                hasAttachment = true;
                break;
            }
        }
        if (!hasAttachment) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        level.scheduleTick(pos, this, 1);
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), shouldConnect(level, pos, state, direction, neighborPos, neighborState));
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
    }
}
