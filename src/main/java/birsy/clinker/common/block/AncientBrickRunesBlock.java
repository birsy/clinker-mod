package birsy.clinker.common.block;

import javax.annotation.Nullable;

import birsy.clinker.common.block.util.Rune;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class AncientBrickRunesBlock extends Block {
	public static final EnumProperty<Rune> RUNE = EnumProperty.create("rune", Rune.class);
	
	public AncientBrickRunesBlock() {
		super(BlockBehaviour.Properties.copy(ClinkerBlocks.ANCIENT_BRICKS.get()));
		this.registerDefaultState(this.stateDefinition.any().setValue(RUNE, Rune.RANDOM));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		ItemStack itemInHand = player.getItemInHand(handIn);
		
		if (itemInHand.getItem() instanceof PickaxeItem) {
			if(!player.isCreative()) {
				itemInHand.hurtAndBreak(1, player, (consumer) -> {
					consumer.broadcastBreakEvent(handIn);
				});
			}
			Property<?> property = state.getBlock().getStateDefinition().getProperty("rune");
			BlockState blockstate = cycleProperty(state, property, player.isSecondaryUseActive());
			String name = property.toString().toUpperCase();
			worldIn.setBlock(pos, blockstate, 18);
			worldIn.playSound(player, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
			player.displayClientMessage(new TranslatableComponent("block.clinker.ancient_rune.carve", name.substring(0, 1).toUpperCase() + name.substring(1)), true);
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
	
	public static <T extends Comparable<T>> BlockState cycleProperty(BlockState state, Property<T> propertyIn, boolean backwards) {
		return state.setValue(propertyIn, getAdjacentValue(propertyIn.getPossibleValues(), state.getValue(propertyIn), backwards));
	}

	private static <T> T getAdjacentValue(Iterable<T> allowedValues, @Nullable T currentValue, boolean backwards) {
		return (T)(backwards ? Util.findPreviousInIterable(allowedValues, currentValue) : Util.findNextInIterable(allowedValues, currentValue));
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		Rune rune = blockState.getValue(RUNE);
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
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(RUNE);
	}
}
