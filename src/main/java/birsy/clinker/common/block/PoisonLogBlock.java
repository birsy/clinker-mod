package birsy.clinker.common.block;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

public class PoisonLogBlock extends RotatedPillarBlock
{	
	public PoisonLogBlock()
	{
		super((Block.Properties.of(Material.WOOD)
			  .strength(2.0F)
			  .sound(SoundType.WOOD)
			  .harvestLevel(0)
			  .harvestTool(ToolType.AXE)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		Item itemInHand = player.getItemInHand(handIn).getItem();
		
		if (itemInHand == Items.SHEARS && state.getBlock() == ClinkerBlocks.LOCUST_LOG.get()) {
			worldIn.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			worldIn.setBlock(pos, ClinkerBlocks.TRIMMED_LOCUST_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 11);
			itemInHand.damageItem(player.getMainHandItem(), 1, player, (consumer) -> {
				consumer.broadcastBreakEvent(handIn);
			});
		} else if (itemInHand instanceof AxeItem) {
			worldIn.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			worldIn.setBlock(pos, ClinkerBlocks.STRIPPED_LOCUST_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 11);
			itemInHand.damageItem(player.getMainHandItem(), 1, player, (consumer) -> {
				consumer.broadcastBreakEvent(handIn);
			});
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) {
		Item itemInHand = player.getMainHandItem().getItem();
		if (itemInHand == Items.SHEARS && state.getBlock() == ClinkerBlocks.LOCUST_LOG.get()) {
			worldIn.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			worldIn.setBlock(pos, ClinkerBlocks.TRIMMED_LOCUST_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 11);
			itemInHand.damageItem(player.getMainHandItem(), 1, player, (consumer) -> {
				consumer.broadcastBreakEvent(InteractionHand.MAIN_HAND);
			});
		}
		super.attack(state, worldIn, pos, player);
	}

	public boolean canEntitySpawn(BlockState state, BlockGetter worldIn, BlockPos pos, EntityType<?> type)
	{
		return false;
	}
	
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type)
	{
		return false;
	}
}
