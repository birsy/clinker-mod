package birsy.clinker.common.world.block;

import birsy.clinker.core.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStoveBlock extends AbstractDoubleBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;

    protected final VoxelShape[][] shapes = new VoxelShape[3][4];

    public AbstractStoveBlock(Properties pProperties) {
        super(pProperties, DoubleBlockHalf.LOWER);

        int j = 0;
        for (ChestType chestType : ChestType.BY_ID) {
            int i = 0;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                shapes[j][i] = this.createShape(direction, chestType);
                i++;
            }
            j++;
        }

    }

    protected abstract VoxelShape getSingleShape();
    protected abstract VoxelShape getDoubleShape();

    protected VoxelShape createShape(Direction direction, ChestType type) {
        VoxelShape shape = type == ChestType.SINGLE ? getSingleShape() : getDoubleShape();
        if (type == ChestType.LEFT) shape = ShapeUtil.flip(shape, Direction.Axis.X);

        switch (direction) {
            case EAST:
                shape = ShapeUtil.rotate(shape, -1, Direction.Axis.Y);
                break;
            case WEST:
                shape = ShapeUtil.rotate(shape, 1, Direction.Axis.Y);
                break;
            case SOUTH:
                shape = ShapeUtil.rotate(shape, 2, Direction.Axis.Y);
                break;
        }

        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int directionIndex = 0;
        switch (pState.getValue(FACING)) {
            case EAST:
                directionIndex = 1;
                break;
            case WEST:
                directionIndex = 3;
                break;
            case SOUTH:
                directionIndex = 2;
                break;
        }

        return shapes[pState.getValue(TYPE).ordinal()][directionIndex];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext, DoubleBlockHalf half) {
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        ChestType chestType = getConnections(pContext.getLevel(), pContext.getClickedPos(), facing);
        if (pContext.getPlayer().isShiftKeyDown()) chestType = ChestType.SINGLE;
        boolean lit = false;

        if (chestType != ChestType.SINGLE) {
            BlockPos connectedPos = pContext.getClickedPos().relative(getConnectedDirection(facing, chestType));
            BlockState connectedBlockState = pContext.getLevel().getBlockState(connectedPos);
            lit = connectedBlockState.getValue(LIT);

            pContext.getLevel().setBlock(connectedPos, connectedBlockState.setValue(TYPE, chestType.getOpposite()), 3);
            DoubleBlockHalf connectedHalf = getHalf(connectedBlockState);

            BlockPos connectedPos2 = connectedPos.relative(connectedHalf == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
            BlockState connectedBlockState2 = pContext.getLevel().getBlockState(connectedPos2);
            pContext.getLevel().setBlock(connectedPos2, connectedBlockState2.setValue(TYPE, chestType.getOpposite()), 3);
        }

        return this.defaultBlockState().setValue(FACING, facing).setValue(TYPE, chestType).setValue(LIT, lit);
    }

    private ChestType getConnections(LevelAccessor level, BlockPos initialPos, Direction facing) {
        Direction[] checkDirections = new Direction[]{facing.getClockWise(), facing.getCounterClockWise()};

        for (int i = 0; i < checkDirections.length; i++) {
            Direction direction = checkDirections[i];
            BlockPos pos = initialPos.relative(direction);

            BlockState state = level.getBlockState(pos);
            if (state.is(this.getType())) {
                if (state.getValue(FACING) == facing && state.getValue(TYPE) == ChestType.SINGLE) {
                    return ChestType.BY_ID[i + 1];
                }
            }
        }

        return ChestType.SINGLE;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pState.getValue(TYPE) != ChestType.SINGLE) {
            if (pDirection == getConnectedDirection(pState)) {
                if (!pNeighborState.is(getType())) return Blocks.AIR.defaultBlockState();
                if (pNeighborState.getValue(FACING) != pState.getValue(FACING)) return Blocks.AIR.defaultBlockState();
                if (pNeighborState.getValue(TYPE) != pState.getValue(TYPE).getOpposite()) return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos).setValue(TYPE, ChestType.SINGLE);

                return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos).setValue(LIT, pNeighborState.getValue(LIT));
            }
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    abstract AbstractStoveBlock getType();

    public static Direction getConnectedDirection(BlockState pState) {
        return getConnectedDirection(pState.getValue(FACING), pState.getValue(TYPE));
    }

    public static Direction getConnectedDirection(Direction direction, ChestType type) {
        return type == ChestType.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT, TYPE);
    }
}
