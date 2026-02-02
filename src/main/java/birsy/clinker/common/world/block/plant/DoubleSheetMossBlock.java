package birsy.clinker.common.world.block.plant;

import birsy.clinker.core.registry.ClinkerTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;

import javax.annotation.Nullable;

public class DoubleSheetMossBlock extends OthershorePlantBlock implements IShearable {
    public static final MapCodec<DoubleSheetMossBlock> CODEC = simpleCodec(DoubleSheetMossBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final VoxelShape SHAPE_TOP = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    public static final VoxelShape SHAPE_BOTTOM = Block.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);

    public DoubleSheetMossBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(HALF, DoubleBlockHalf.UPPER));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 offset = pState.getOffset(pLevel, pPos);
        return (pState.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE_BOTTOM).move(offset.x, 0, offset.z);
    }

    public static boolean canPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(ClinkerTags.OTHERSHORE_SOIL) && pState.isFaceSturdy(pLevel, pPos, Direction.DOWN);
    }

    @Override
    public boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return canPlaceOn(pState, pLevel, pPos);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (facing == Direction.UP) || facingState.is(this) && facingState.getValue(HALF) != half) {
            return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
//        DoubleBlockHalf half = state.getValue(HALF);
//        if (facing.getAxis() == Direction.Axis.Y) {
//            if (half == DoubleBlockHalf.LOWER && facing == Direction.UP) {
//                return facingState.is(this) && facingState.getValue(HALF) == DoubleBlockHalf.UPPER ?
//                        Blocks.AIR.defaultBlockState() :
//                        super.updateShape(state, facing, facingState, level, currentPos, facingPos);
//            } else {
//                if (facing == Direction.DOWN) {
//                    return facingState.is(this) && facingState.getValue(HALF) == DoubleBlockHalf.LOWER ?
//                            Blocks.AIR.defaultBlockState() :
//                            super.updateShape(state, facing, facingState, level, currentPos, facingPos);
//                } else {
//                    return !state.canSurvive(level, currentPos) ?
//                            Blocks.AIR.defaultBlockState() :
//                            super.updateShape(state, facing, facingState, level, currentPos, facingPos);
//                }
//            }
//        }
//
//        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        return blockpos.getY() > level.getMinBuildHeight() + 1 && level.getBlockState(blockpos.below()).canBeReplaced(context)
                ? super.getStateForPlacement(context)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos blockpos = pos.below();
        level.setBlock(blockpos, DoublePlantBlock.copyWaterloggedFrom(level, blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER)), 3);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState aboveBlockState = level.getBlockState(blockpos);
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            net.neoforged.neoforge.common.util.TriState soilDecision = aboveBlockState.canSustainPlant(level, blockpos, Direction.DOWN, state);
            if (!soilDecision.isDefault()) return soilDecision.isTrue();
            return this.mayPlaceOn(aboveBlockState, level, blockpos);
        } else {
            return aboveBlockState.is(this) && aboveBlockState.getValue(HALF) == DoubleBlockHalf.UPPER;
        }
    }

    public static void placeAt(LevelAccessor level, BlockState state, BlockPos pos, int flags) {
        BlockPos belowPos = pos.below();
        level.setBlock(pos, DoublePlantBlock.copyWaterloggedFrom(level, pos, state.setValue(HALF, DoubleBlockHalf.UPPER)), flags);
        level.setBlock(belowPos, DoublePlantBlock.copyWaterloggedFrom(level, belowPos, state.setValue(HALF, DoubleBlockHalf.LOWER)), flags);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                preventDropFromBottomPart(level, pos, state, player);
            } else {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
    }

    protected static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, HALF);
    }

    @Override
    protected long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    protected MapCodec<? extends DoubleSheetMossBlock> codec() {
        return this.codec();
    }
}
