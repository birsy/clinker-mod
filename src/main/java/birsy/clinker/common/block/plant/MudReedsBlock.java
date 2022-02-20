package birsy.clinker.common.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Random;

public class MudReedsBlock extends BushBlock implements BonemealableBlock, net.minecraftforge.common.IForgeShearable, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE_SHORT = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D);
    protected static final VoxelShape SHAPE_TALL = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    public MudReedsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(ClinkerBlocks.MUD.get()) || pState.is(ClinkerBlocks.BRIMSTONE.get());
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.is(ClinkerBlocks.SHORT_MUD_REEDS.get()) ? SHAPE_SHORT : SHAPE_TALL;
    }

    public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return true;
    }

    public boolean isBonemealSuccess(Level pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    public void performBonemeal(ServerLevel pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
        if (pState.is(ClinkerBlocks.SHORT_MUD_REEDS.get())) {
            pLevel.setBlock(pPos, copyWaterloggedFrom(pLevel, pPos, ClinkerBlocks.MUD_REEDS.get().defaultBlockState()).setValue(WATERLOGGED, pState.getValue(WATERLOGGED)), 2);
        } else {
            DoubleMudReedsBlock doubleplantblock = (DoubleMudReedsBlock) ClinkerBlocks.TALL_MUD_REEDS.get();
            if (doubleplantblock.defaultBlockState().canSurvive(pLevel, pPos) && pLevel.isEmptyBlock(pPos.above())) {
                DoublePlantBlock.placeAt(pLevel, doubleplantblock.defaultBlockState(), pPos, 2);
            }
        }

    }

    public static BlockState copyWaterloggedFrom(LevelReader reader, BlockPos pos, BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(reader.isWaterAt(pos))) : state;
    }

    public BlockBehaviour.OffsetType getOffsetType() {
        return BlockBehaviour.OffsetType.XYZ;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        if (blockstate != null) {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
        } else {
            return null;
        }
    }
}
