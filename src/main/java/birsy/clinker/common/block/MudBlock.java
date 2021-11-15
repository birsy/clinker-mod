package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Random;

public class MudBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty SQUISHED = BooleanProperty.create("squished");

    public MudBlock(Properties properties) {
        super(properties.randomTicks());
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(SQUISHED, false));
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        pEntity.setDeltaMovement(pEntity.getDeltaMovement().multiply(0.5, 0.5, 0.5));

        if (!pState.getValue(SQUISHED)) {
            pLevel.playSound(null, pEntity, SoundEvents.SLIME_SQUISH, pEntity.getSoundSource(), 0.125F, 0.125F);
            pLevel.setBlock(pPos, pState.setValue(SQUISHED, true), 2);
            pLevel.getBlockTicks().scheduleTick(pPos, this, pLevel.random.nextInt(60) + 20);
        }

        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
        if (pState.getValue(SQUISHED) && !isSaturated(pLevel, pPos)) {
            float ySubtract = pState.getValue(WATERLOGGED) ? 0.8125F : 0.0F;

            if (pLevel.getEntities((Entity) null, new AABB(pPos.above().getX(), pPos.above().getY() - ySubtract, pPos.above().getZ(), pPos.above().getX() + 1, pPos.above().getY() + 0.25 - ySubtract, pPos.above().getZ() + 1), (entity) -> entity.isOnGround()).isEmpty()) {
                pLevel.setBlock(pPos, pState.setValue(SQUISHED, false), 2);
            }
        }

        super.tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.getLiquidTicks().scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        if (pLevel.getFluidState(pFacingPos).getType() == Fluids.WATER) {
            pState.setValue(SQUISHED, true);
        } else if (!isSaturated(pLevel, pCurrentPos)) {
            pState.setValue(SQUISHED, false);
        }

        return pState;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return isSaturated(pContext.getLevel(), pContext.getClickedPos()) ? super.getStateForPlacement(pContext).setValue(SQUISHED, true) : super.getStateForPlacement(pContext);
    }

    private boolean isSaturated(LevelAccessor pLevel, BlockPos pPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (pLevel.getFluidState(pPos.relative(direction)).getType() == Fluids.WATER) {
                return true;
            }
        }

        return false;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        double squishedSubtraction = pState.getValue(SQUISHED) ? 1.0 : 0.0;
        return pState.getValue(WATERLOGGED) ? Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D - squishedSubtraction, 16.0D) : Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D - squishedSubtraction, 16.0D);
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, SQUISHED);
    }
}
