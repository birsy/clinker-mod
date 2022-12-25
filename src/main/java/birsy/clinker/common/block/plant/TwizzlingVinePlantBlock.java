package birsy.clinker.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TwizzlingVinePlantBlock extends GrowingPlantBodyBlock implements TwizzlingVine {
    public TwizzlingVinePlantBlock(Properties pProperties) {
        super(pProperties, Direction.DOWN, SHAPE, false);
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
    protected GrowingPlantHeadBlock getHeadBlock() {
        return null;
    }
}
