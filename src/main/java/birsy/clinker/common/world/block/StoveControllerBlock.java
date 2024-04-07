package birsy.clinker.common.world.block;

import birsy.clinker.common.world.block.blockentity.StoveBlockEntity;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

public class StoveControllerBlock extends StoveBlock implements EntityBlock {
    public StoveControllerBlock(Properties pProperties) {
        super(pProperties);
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

        BlockState defaultState = chestType == ChestType.SINGLE ? ClinkerBlocks.STOVE.get().defaultBlockState() : ClinkerBlocks.STOVE_DUMMY.get().defaultBlockState();
        return defaultState.setValue(FACING, facing).setValue(TYPE, chestType).setValue(LIT, lit);
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (newState.is(this)) {
            if (level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
                stove.updateState(newState);
            }
        }
    }

    @Override
    boolean isValidType(BlockState state) {
        return state.is(ClinkerBlocks.STOVE_DUMMY.get()) || state.is(ClinkerBlocks.STOVE.get());
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StoveBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ClinkerBlockEntities.STOVE.get(), StoveBlockEntity::tick);
    }

    //fuck you mojang
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>)pTicker : null;
    }
}
