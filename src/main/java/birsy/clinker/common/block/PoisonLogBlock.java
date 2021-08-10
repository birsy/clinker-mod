package birsy.clinker.common.block;

import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class PoisonLogBlock extends RotatedPillarBlock
{	
	public PoisonLogBlock()
	{
		super((Block.Properties.create(Material.WOOD)
			  .hardnessAndResistance(2.0F)
			  .sound(SoundType.WOOD)
			  .harvestLevel(0)
			  .harvestTool(ToolType.AXE)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		Item itemInHand = player.getHeldItem(handIn).getItem();
		
		if (itemInHand == Items.SHEARS && state.getBlock() == ClinkerBlocks.LOCUST_LOG.get()) {
			worldIn.playSound(player, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
			worldIn.setBlockState(pos, ClinkerBlocks.TRIMMED_LOCUST_LOG.get().getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS)), 11);
			itemInHand.damageItem(player.getHeldItemMainhand(), 1, player, (consumer) -> {
				consumer.sendBreakAnimation(handIn);
			});
		} else if (itemInHand instanceof AxeItem) {
			worldIn.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
			worldIn.setBlockState(pos, ClinkerBlocks.STRIPPED_LOCUST_LOG.get().getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS)), 11);
			itemInHand.damageItem(player.getHeldItemMainhand(), 1, player, (consumer) -> {
				consumer.sendBreakAnimation(handIn);
			});
		}
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		Item itemInHand = player.getHeldItemMainhand().getItem();
		if (itemInHand == Items.SHEARS && state.getBlock() == ClinkerBlocks.LOCUST_LOG.get()) {
			worldIn.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
			worldIn.setBlockState(pos, ClinkerBlocks.TRIMMED_LOCUST_LOG.get().getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS)), 11);
			itemInHand.damageItem(player.getHeldItemMainhand(), 1, player, (consumer) -> {
				consumer.sendBreakAnimation(Hand.MAIN_HAND);
			});
		}
		super.onBlockClicked(state, worldIn, pos, player);
	}

	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type)
	{
		return false;
	}
	
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
	{
		return false;
	}
}
