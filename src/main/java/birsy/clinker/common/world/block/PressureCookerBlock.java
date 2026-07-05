package birsy.clinker.common.world.block;

import birsy.clinker.common.world.block.blockentity.PressureCookerBlockEntity;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public abstract class PressureCookerBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<LadderBlock> CODEC = simpleCodec(LadderBlock::new);
    public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");

    public PressureCookerBlock(Properties properties) {
        super(properties);
    }

    public static boolean isHead(BlockState state) {
        return state.getBlock() instanceof Head;
    }

    public static Direction getConnectedDirection(BlockState state) {
        Direction direction = state.getValue(FACING).getCounterClockWise();
        if (state.getValue(MIRRORED)) direction = direction.getOpposite();
        return isHead(state) ? direction : direction.getOpposite();
    }

    public static boolean shouldConnect(BlockState state, BlockState otherState) {
        return otherState.getBlock() instanceof PressureCookerBlock &&
                isHead(state) != isHead(otherState) &&
                otherState.getValue(FACING) == state.getValue(FACING) &&
                otherState.getValue(MIRRORED) == state.getValue(MIRRORED);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState returnState = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        if (facing == getConnectedDirection(state)) {
            return shouldConnect(state, facingState) ? returnState : Blocks.AIR.defaultBlockState();
        }
        return returnState;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(getConnectedDirection(state));
            BlockState otherState = level.getBlockState(blockpos);
            if (shouldConnect(state, otherState)) {
                level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(otherState));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MIRRORED);
    }

    public static class Head extends PressureCookerBlock implements EntityBlock {
        public static final MapCodec<Head> CODEC = simpleCodec(Head::new);

        public Head(Properties properties) {
            super(properties);
        }

        @Override
        public @org.jetbrains.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
            Level level = context.getLevel();
            BlockPos.MutableBlockPos testPos = context.getClickedPos().mutable();
            double localX = 0.5 - Mth.frac(context.getClickLocation().x), localZ = 0.5 - Mth.frac(context.getClickLocation().z());
            for (Direction nearestLookingDirection : context.getNearestLookingDirections()) {
                if (nearestLookingDirection.getAxis().isVertical()) continue;

                Direction idealFacingDirection = nearestLookingDirection.getOpposite();

                Direction bodyDirection = idealFacingDirection.getCounterClockWise();
                Direction mirroredBodyDirection = bodyDirection.getOpposite();

                double dist = Mth.length(localX - bodyDirection.getStepX(), localZ - bodyDirection.getStepZ());
                double mirroredDist = Mth.length(localX - mirroredBodyDirection.getStepX(), localZ - mirroredBodyDirection.getStepZ());

                boolean tryNormalFirst = dist <= mirroredDist;

                if (tryNormalFirst) {
                    // check normal
                    testPos.set(context.getClickedPos()).move(bodyDirection);
                    if (level.getBlockState(testPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(testPos)) {
                        return this.defaultBlockState()
                                .setValue(FACING, idealFacingDirection)
                                .setValue(MIRRORED, false);
                    }

                    // check mirrored
                    testPos.set(context.getClickedPos()).move(mirroredBodyDirection);
                    if (level.getBlockState(testPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(testPos)) {
                        return this.defaultBlockState()
                                .setValue(FACING, idealFacingDirection)
                                .setValue(MIRRORED, true);
                    }
                } else {
                    // check mirrored
                    testPos.set(context.getClickedPos()).move(mirroredBodyDirection);
                    if (level.getBlockState(testPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(testPos)) {
                        return this.defaultBlockState()
                                .setValue(FACING, idealFacingDirection)
                                .setValue(MIRRORED, true);
                    }

                    // check normal
                    testPos.set(context.getClickedPos()).move(bodyDirection);
                    if (level.getBlockState(testPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(testPos)) {
                        return this.defaultBlockState()
                                .setValue(FACING, idealFacingDirection)
                                .setValue(MIRRORED, false);
                    }
                }
            }
            return null;
        }

        @Override
        public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
            super.setPlacedBy(level, pos, state, placer, stack);
            if (!level.isClientSide) {
                Direction connectedDirection = getConnectedDirection(state);
                BlockPos blockpos = pos.relative(connectedDirection);
                BlockState stateToPlace = ClinkerBlocks.PRESSURE_COOKER_BODY.get().defaultBlockState();
                stateToPlace = stateToPlace.setValue(FACING, state.getValue(FACING));
                stateToPlace = stateToPlace.setValue(MIRRORED, state.getValue(MIRRORED));

                level.setBlock(blockpos, stateToPlace, 3);
                level.blockUpdated(pos, Blocks.AIR);
                state.updateNeighbourShapes(level, pos, 3);
            }
        }

        @Override
        protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
            return CODEC;
        }

        @Override
        public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new PressureCookerBlockEntity(pos, state);
        }
    }

    public static class Body extends PressureCookerBlock {
        public static final MapCodec<Body> CODEC = simpleCodec(Body::new);

        public Body(Properties properties) {
            super(properties);
        }

        @Override
        protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
            return CODEC;
        }
    }
}
