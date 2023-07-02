package birsy.clinker.common.world.block;

import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import birsy.clinker.common.world.block.blockentity.CounterBlockEntity;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CounterBlock extends BaseEntityBlock {
	public CounterBlock(Properties properties) {
		super(properties);
	}

	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new CounterBlockEntity(pPos, pState);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? BaseEntityBlock.createTickerHelper(pBlockEntityType, ClinkerBlockEntities.COUNTER.get(), CounterBlockEntity::tick) : null;
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pLevel.isClientSide) {
			AlchemicalWorkstationScreen screen = new AlchemicalWorkstationScreen(((CounterBlockEntity) pLevel.getBlockEntity(pPos)).workstation);
			Minecraft.getInstance().setScreen(screen);
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
}
