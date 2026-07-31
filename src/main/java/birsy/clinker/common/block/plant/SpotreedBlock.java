package birsy.clinker.common.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SpotreedBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> VERTICAL_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    protected static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

    public SpotreedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(WATERLOGGED, false)
                        .setValue(LIT, true)
                        .setValue(AGE, 0)
                        .setValue(VERTICAL_DIRECTION, Direction.UP)
        );
    }

    protected int progressAge(BlockState state, RandomSource random) {
        int age = state.getValue(AGE);
        return Math.min((int) (age + Math.round(Math.abs(random.nextGaussian() * 2) + 0.5)), 4);
    }
    protected boolean canConnect(BlockState state, BlockState connectingState) {
        return connectingState.is(state.getBlock()) && connectingState.getValue(VERTICAL_DIRECTION) == state.getValue(VERTICAL_DIRECTION);
    }
    protected int getCountAbove(BlockGetter level, BlockState state, BlockPos pos) {
        BlockPos.MutableBlockPos mPos = pos.mutable();
        int i = 0;
        while (i < 16 && canConnect(state, level.getBlockState(mPos.move(Direction.UP)))) i++;
        return i;
    }
    protected int getCountBelow(BlockGetter level, BlockState state, BlockPos pos) {
        BlockPos.MutableBlockPos mPos = pos.mutable();
        int i = 0;
        while (i < 16 && canConnect(state, level.getBlockState(mPos.move(Direction.DOWN)))) i++;
        return i;
    }

    protected boolean growSpotreed(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(AGE) >= 4) return false;

        Direction growthDirection = state.getValue(VERTICAL_DIRECTION);
        BlockPos growthPos = pos.relative(growthDirection);
        if (level.isEmptyBlock(growthPos) && net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, true)) {
            FluidState fluid = level.getBlockState(growthPos).getFluidState();
            level.setBlockAndUpdate(growthPos,
                    this.defaultBlockState()
                            .setValue(AGE, progressAge(state, random))
                            .setValue(VERTICAL_DIRECTION, growthDirection)
                            .setValue(WATERLOGGED, fluid.is(Fluids.WATER))
            );
            net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, growthPos, state);
            return true;
        }
        return false;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.5) growSpotreed(level, pos, state, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 4;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos))
            level.scheduleTick(pos, this, 1);
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        if (direction == state.getValue(VERTICAL_DIRECTION) && neighborState.is(this))
            state = state.setValue(LIT, false);

        return state;
    }

    private static final Direction[] UP_FIRST = {Direction.UP, Direction.DOWN}, DOWN_FIRST = {Direction.DOWN, Direction.UP};
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null || state.getBlock() != this) return state;

        // waterlog
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        state = state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        // determine direction
        BlockPos.MutableBlockPos mPos = context.getClickedPos().mutable();

        Direction[] directionsToAttempt = context.getClickedFace() == Direction.DOWN ? UP_FIRST : DOWN_FIRST;
        for (Direction direction : directionsToAttempt) {
            mPos.move(direction.getOpposite());
            BlockState rootState = context.getLevel().getBlockState(mPos);
            mPos.move(direction);

            BlockState stateAttempt = state.setValue(VERTICAL_DIRECTION, direction);

            if (rootState.is(this) && rootState.getValue(VERTICAL_DIRECTION) == direction) {
                return stateAttempt
                        .setValue(AGE, progressAge(state, context.getLevel().random));
            } else if (stateAttempt.canSurvive(context.getLevel(), context.getClickedPos())) {
                return stateAttempt;
            }
        }

        return null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction growthDirection = state.getValue(VERTICAL_DIRECTION);
        BlockPos rootPos = pos.relative(growthDirection.getOpposite());
        BlockState rootState = level.getBlockState(rootPos);
        if (rootState.is(this) && rootState.getValue(VERTICAL_DIRECTION) == growthDirection) return true;
        return rootState.isFaceSturdy(level, pos, growthDirection, SupportType.CENTER) &&
                (rootState.is(ClinkerTags.Blocks.OTHERSHORE_SOIL) || rootState.is(BlockTags.DIRT) || rootState.getBlock() instanceof FarmBlock);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos))
            level.destroyBlock(pos, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return SHAPE.move(vec3.x, 0, vec3.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED, AGE, VERTICAL_DIRECTION);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        int aboveCount = this.getCountAbove(level, state, pos);
        int belowCount = this.getCountBelow(level, state, pos);
        Direction growthDirection = state.getValue(VERTICAL_DIRECTION);
        int tipOffset = growthDirection == Direction.UP ? aboveCount : belowCount;
        BlockPos tipPos = pos.relative(growthDirection, tipOffset);
        return (aboveCount + belowCount + 1 < 5 || level.getBlockState(tipPos).getValue(LIT) == false)
                && level.isEmptyBlock(tipPos.relative(growthDirection));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int aboveCount = this.getCountAbove(level, state, pos);
        int belowCount = this.getCountBelow(level, state, pos);
        Direction growthDirection = state.getValue(VERTICAL_DIRECTION);
        int tipOffset = growthDirection == Direction.UP ? aboveCount : belowCount;

        if (aboveCount + belowCount + 1 >= 5) {
            BlockPos topPos = pos.relative(growthDirection, tipOffset);
            level.setBlock(topPos, level.getBlockState(topPos).setValue(LIT, true), 2);
        } else {
            BlockPos.MutableBlockPos tipPos = pos.mutable().move(growthDirection, tipOffset);
            int growthAmount = random.nextIntBetweenInclusive(1, 2);
            for (int i = 0; i < growthAmount; i++) {
                if (!growSpotreed(level, tipPos, state, random)) break;
                tipPos.move(growthDirection);
            }
        }
    }
}