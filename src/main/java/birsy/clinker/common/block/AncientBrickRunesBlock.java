package birsy.clinker.common.block;

import javax.annotation.Nullable;

import birsy.clinker.common.block.util.Rune;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class AncientBrickRunesBlock extends Block {
	public static final EnumProperty<Rune> RUNE = EnumProperty.create("rune", Rune.class);
	
	public AncientBrickRunesBlock() {
		super(AbstractBlock.Properties.from(ClinkerBlocks.ANCIENT_BRICKS.get()));
		this.setDefaultState(this.stateContainer.getBaseState().with(RUNE, Rune.RANDOM));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemInHand = player.getHeldItem(handIn);
		
		if (itemInHand.getItem() instanceof PickaxeItem) {
			if(!player.isCreative()) {
				itemInHand.damageItem(1, player, (consumer) -> {
					consumer.sendBreakAnimation(handIn);
				});
			}
			Property<?> property = state.getBlock().getStateContainer().getProperty("rune");
			BlockState blockstate = cycleProperty(state, property, player.isSecondaryUseActive());
			String name = property.getName();
			worldIn.setBlockState(pos, blockstate, 18);
			worldIn.playSound(player, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			player.sendStatusMessage(new TranslationTextComponent("block.clinker.ancient_rune.carve", name.substring(0, 1).toUpperCase() + name.substring(1)), true);
		}
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	
	public static <T extends Comparable<T>> BlockState cycleProperty(BlockState state, Property<T> propertyIn, boolean backwards) {
		return state.with(propertyIn, getAdjacentValue(propertyIn.getAllowedValues(), state.get(propertyIn), backwards));
	}

	private static <T> T getAdjacentValue(Iterable<T> allowedValues, @Nullable T currentValue, boolean backwards) {
		return (T)(backwards ? Util.getElementBefore(allowedValues, currentValue) : Util.getElementAfter(allowedValues, currentValue));
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		Rune rune = blockState.get(RUNE);
		switch(rune) {
			case NONE:
				return 0;
			case RANDOM:
				return 1;
			case A:
				return 1;
			case B:
				return 2;
			case C:
				return 2;
			case D:
				return 3;
			case E:
				return 3;
			case F:
				return 4;
			case G:
				return 4;
			case H:
				return 5;
			case I:
				return 5;
			case J:
				return 6;
			case K:
				return 6;
			case L:
				return 7;
			case M:
				return 7;
			case N:
				return 8;
			case O:
				return 8;
			case P:
				return 9;
			case Q:
				return 9;
			case R:
				return 10;
			case S:
				return 10;
			case T:
				return 11;
			case U:
				return 11;
			case V:
				return 12;
			case W:
				return 12;
			case X:
				return 13;
			case Y:
				return 13;
			case Z:
				return 14;
		}
		return 0;
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(RUNE);
	}
}
