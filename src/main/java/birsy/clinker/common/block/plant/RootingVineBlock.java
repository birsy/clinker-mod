package birsy.clinker.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;
import org.jetbrains.annotations.Nullable;

public class RootingVineBlock extends Block implements IShearable, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ROOTED = BooleanProperty.create("rooted");
    public static final BooleanProperty TIP = BooleanProperty.create("tip");

    final Direction growthDirection;
    final double diameter, tipLength;
    final VoxelShape stalkShape, tipShape;

    public RootingVineBlock(Properties properties, Direction growthDirection, double diameter, double tipLength) {
        super(properties);
        this.growthDirection = growthDirection;
        this.diameter = diameter;
        this.tipLength = tipLength;
        this.stalkShape = createStalkShape();
        this.tipShape = createTipShape();
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(ROOTED, true)
                .setValue(TIP, true)
        );
    }

    protected VoxelShape createStalkShape() {
        double radius = diameter / 2;
        return Shapes.box(
                0.5 - radius, 0, 0.5 - radius,
                0.5 + radius, 1, 0.5 + radius
        );
    }
    protected VoxelShape createTipShape() {
        double radius = diameter / 2;
        boolean growingUp = growthDirection == Direction.UP;
        return Shapes.box(
                0.5 - radius, growingUp ? 0 : 1 - tipLength, 0.5 - radius,
                0.5 + radius, growingUp ? tipLength : 1, 0.5 + radius
        );
    }

    protected boolean canBlockSupport(BlockGetter level, BlockState aboveState, BlockPos abovePos) {
        return aboveState.isFaceSturdy(level, abovePos, Direction.DOWN, SupportType.CENTER);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TIP) ? tipShape : stalkShape;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState currentState = super.getStateForPlacement(context);
        if (currentState == null) return null;
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        currentState = currentState.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        BlockPos prevPos = context.getClickedPos().relative(growthDirection.getOpposite());
        BlockState prevState = context.getLevel().getBlockState(prevPos);
        boolean rooted = false;
        if (!prevState.is(this)) {
            if (canBlockSupport(context.getLevel(), prevState, prevPos)) {
                rooted = true;
            } else {
                // we need a valid block in the rooting direction to support us!
                return null;
            }
        }

        BlockState nextState = context.getLevel().getBlockState(context.getClickedPos().relative(growthDirection));
        boolean tip = !nextState.is(this);

        return currentState
                .setValue(ROOTED, rooted)
                .setValue(TIP, tip);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        if (facing == growthDirection) {
            if (facingState.is(this)) {
                return state.setValue(TIP, false);
            } else {
                return state.setValue(TIP, true);
            }
        }

        if (facing == growthDirection.getOpposite()) {
            if (facingState.is(this)) {
                return state.setValue(ROOTED, false);
            } else if (canBlockSupport(level, facingState, facingPos)) {
                return state.setValue(ROOTED, true);
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return state;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(WATERLOGGED, ROOTED, TIP);
    }
}
