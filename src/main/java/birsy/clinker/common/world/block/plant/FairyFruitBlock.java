package birsy.clinker.common.world.block.plant;

import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class FairyFruitBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, BonemealableBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 10.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    public static final MapCodec<FairyFruitBlock> CODEC = simpleCodec(FairyFruitBlock::new);

    public FairyFruitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153358_) {
        p_153358_.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public float getMaxHorizontalOffset() {
        return super.getMaxHorizontalOffset();
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        if (blockstate != null) {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        } else {
            return null;
        }
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = level.getBlockState(blockpos);
        return blockstate.isFaceSturdy(level, blockpos, Direction.DOWN);
    }

    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return pLevel.getBlockEntity(pPos) instanceof FairyFruitBlockEntity;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader p_256559_, BlockPos p_50898_, BlockState p_50899_) {
        return false;
    }

    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        //if (pLevel.getBlockEntity(pPos) instanceof FairyFruitBlockEntity entity) return entity.canGrow;
        return false;
    }

    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        if (pLevel.getBlockEntity(pPos) instanceof FairyFruitBlockEntity entity) entity.useBoneMeal();
    }

    public VoxelShape getShape(BlockState p_153342_, BlockGetter p_153343_, BlockPos p_153344_, CollisionContext p_153345_) {
        return SHAPE;
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    public BlockState updateShape(BlockState p_153351_, Direction p_153352_, BlockState p_153353_, LevelAccessor p_153354_, BlockPos p_153355_, BlockPos p_153356_) {
        if (p_153352_ == Direction.UP && !this.canSurvive(p_153351_, p_153354_, p_153355_)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if (p_153351_.getValue(WATERLOGGED)) {
                p_153354_.scheduleTick(p_153355_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153354_));
            }

            return super.updateShape(p_153351_, p_153352_, p_153353_, p_153354_, p_153355_, p_153356_);
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FairyFruitBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ClinkerBlockEntities.FAIRY_FRUIT.get(), FairyFruitBlockEntity::tick);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

}
