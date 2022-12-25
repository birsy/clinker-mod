package birsy.clinker.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class TwizzlingVineBlock extends GrowingPlantHeadBlock implements TwizzlingVine {
    public TwizzlingVineBlock(Properties pProperties) {
        super(pProperties, Direction.DOWN, SHAPE, false, 0.1D);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return TwizzlingVine.getAttachedState(state, pContext.getLevel(), pContext.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        BlockState state = super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        if (pFacing == Direction.UP) { return TwizzlingVine.getAttachedState(state, pLevel, pCurrentPos); }

        return state;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource pRandom) {
        return pRandom.nextInt(1,3);
    }

    @Override
    protected boolean canGrowInto(BlockState pState) {
        return pState.isAir();
    }

    @Override
    protected Block getBodyBlock() {
        return null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ATTACHED);
    }
}
