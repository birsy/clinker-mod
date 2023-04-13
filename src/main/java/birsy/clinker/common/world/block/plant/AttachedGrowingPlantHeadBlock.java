package birsy.clinker.common.world.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AttachedGrowingPlantHeadBlock extends GrowingPlantHeadBlock implements AttachedPlant {
    public AttachedGrowingPlantHeadBlock(Properties pProperties, Direction pGrowthDirection, VoxelShape pShape, boolean pScheduleFluidTicks, double pGrowPerTickProbability) {
        super(pProperties, pGrowthDirection, pShape, pScheduleFluidTicks, pGrowPerTickProbability);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return AttachedPlant.getAttachedState(state, pContext.getLevel(), pContext.getClickedPos(), this.growthDirection);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pFacing == this.growthDirection.getOpposite() && !pState.canSurvive(pLevel, pCurrentPos)) {
            pLevel.scheduleTick(pCurrentPos, this, 1);
        }

        if (pFacing == this.growthDirection && !pFacingState.is(this) && !pFacingState.is(this.getHeadBlock())) {
            return AttachedPlant.getAttachedState(this.getHeadBlock().getStateForPlacement(pLevel), pLevel, pCurrentPos, this.growthDirection);
        } else {
            if (this.scheduleFluidTicks) {
                pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
            }

            return AttachedPlant.getAttachedState(super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos), pLevel, pCurrentPos, this.growthDirection);
        }
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ATTACHED);
    }
}
