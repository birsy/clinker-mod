package birsy.clinker.common.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public abstract class AbstractDoubleBlock extends Block {
    protected final DoubleBlockHalf placedHalf;

    public AbstractDoubleBlock(Properties pProperties, DoubleBlockHalf placedHalf) {
        super(pProperties);
        this.placedHalf = placedHalf;
    }


    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        DoubleBlockHalf half = this.getHalf(pState);
        Direction checkDirection = half == DoubleBlockHalf.UPPER ? Direction.DOWN : Direction.UP;

        if (pDirection == checkDirection) {
            if (!isValidAttachmentBlock(pNeighborState, half)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        boolean canPlace = placedHalf == DoubleBlockHalf.LOWER ?
                blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(pContext) :
                blockpos.getY() > level.getMinBuildHeight() + 1 && level.getBlockState(blockpos.below()).canBeReplaced(pContext);

        if (canPlace) {
            return getStateForPlacement(pContext, placedHalf);
        } else {
            return null;
        }
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(placedHalf == DoubleBlockHalf.LOWER ? pPos.above() : pPos.below(), getValidAttachmentBlock(pState, placedHalf == DoubleBlockHalf.LOWER ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER), 3);
    }

    @Nullable
    public abstract BlockState getStateForPlacement(BlockPlaceContext pContext, DoubleBlockHalf half);

    public abstract boolean isValidAttachmentBlock(BlockState state, DoubleBlockHalf half);

    public abstract BlockState getValidAttachmentBlock(BlockState state, DoubleBlockHalf half);

    public abstract DoubleBlockHalf getHalf(BlockState state);
}
