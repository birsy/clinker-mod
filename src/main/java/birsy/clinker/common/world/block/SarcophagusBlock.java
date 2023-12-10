package birsy.clinker.common.world.block;

import birsy.clinker.common.world.block.blockentity.SarcophagusBlockEntity;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SarcophagusBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    public static final IntegerProperty GUTS = IntegerProperty.create("guts", 0, 3);

    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    private static final Map<Direction, VoxelShape> AABB_BY_DIRECTION = Util.make(new HashMap<>(), (map) -> {
        map.put(Direction.DOWN, Block.box(0.0D, 0.0D, 0.0D, 16.0D,2.0D, 16.0D));
        map.put(Direction.UP,   Block.box(0.0D, 14.0D,0.0D, 16.0D,16.0D,16.0D));
        map.put(Direction.NORTH,Block.box(0.0D, 0.0D, 0.0D, 16.0D,16.0D,2.0D));
        map.put(Direction.SOUTH,Block.box(0.0D, 0.0D, 14.0D,16.0D,16.0D,16.0D));
        map.put(Direction.WEST, Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D,16.0D));
        map.put(Direction.EAST, Block.box(14.0D,0.0D, 0.0D, 16.0D,16.0D,16.0D));});
    public static final MapCodec<SarcophagusBlock> CODEC = simpleCodec(SarcophagusBlock::new);

    public SarcophagusBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, true)
                .setValue(EAST, true)
                .setValue(SOUTH, true)
                .setValue(WEST, true)
                .setValue(UP, true)
                .setValue(DOWN, true)
                .setValue(GUTS, 3));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = Shapes.empty();
        for (Direction direction : Direction.values()) {
            if (pState.getValue(PROPERTY_BY_DIRECTION.get(direction))) {
                shape = Shapes.or(shape, AABB_BY_DIRECTION.get(direction));
            }
        }

        return shape;
    }

    //TODO: make breaking one side at a time work

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockGetter blockgetter = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        return this.defaultBlockState()
                .setValue(DOWN, !blockgetter.getBlockState(blockpos.below()).is(this))
                .setValue(UP, !blockgetter.getBlockState(blockpos.above()).is(this))
                .setValue(NORTH, !blockgetter.getBlockState(blockpos.north()).is(this))
                .setValue(EAST, !blockgetter.getBlockState(blockpos.east()).is(this))
                .setValue(SOUTH, !blockgetter.getBlockState(blockpos.south()).is(this))
                .setValue(WEST, !blockgetter.getBlockState(blockpos.west()).is(this));
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacingState.is(this) ? pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), Boolean.valueOf(false)) : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pRot.rotate(Direction.NORTH)),
                pState.getValue(NORTH)).setValue(PROPERTY_BY_DIRECTION.get(pRot.rotate(Direction.SOUTH)),
                pState.getValue(SOUTH)).setValue(PROPERTY_BY_DIRECTION.get(pRot.rotate(Direction.EAST)),
                pState.getValue(EAST)).setValue(PROPERTY_BY_DIRECTION.get(pRot.rotate(Direction.WEST)),
                pState.getValue(WEST)).setValue(PROPERTY_BY_DIRECTION.get(pRot.rotate(Direction.UP)),
                pState.getValue(UP)).setValue(PROPERTY_BY_DIRECTION.get(pRot.rotate(Direction.DOWN)),
                pState.getValue(DOWN));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pMirror.mirror(Direction.NORTH)),
                pState.getValue(NORTH)).setValue(PROPERTY_BY_DIRECTION.get(pMirror.mirror(Direction.SOUTH)),
                pState.getValue(SOUTH)).setValue(PROPERTY_BY_DIRECTION.get(pMirror.mirror(Direction.EAST)),
                pState.getValue(EAST)).setValue(PROPERTY_BY_DIRECTION.get(pMirror.mirror(Direction.WEST)),
                pState.getValue(WEST)).setValue(PROPERTY_BY_DIRECTION.get(pMirror.mirror(Direction.UP)),
                pState.getValue(UP)).setValue(PROPERTY_BY_DIRECTION.get(pMirror.mirror(Direction.DOWN)),
                pState.getValue(DOWN));
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, GUTS);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SarcophagusBlockEntity(pPos, pState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ClinkerBlockEntities.SARCOPHAGUS_INNARDS.get(), pLevel.isClientSide ? SarcophagusBlockEntity::clientTick : SarcophagusBlockEntity::serverTick);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

}
