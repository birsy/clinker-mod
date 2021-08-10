package birsy.clinker.common.block.belwithstone;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ToolType;

public class CyclopeanBelwithBricksBlock extends Block
{
	public static final BooleanProperty Y_EVEN = BooleanProperty.create("y_even");
	public static final BooleanProperty Z_EVEN = BooleanProperty.create("z_even");
	
	public CyclopeanBelwithBricksBlock()
	{
		super(((Block.Properties.of(Material.STONE)
			  .strength(10F, 6.0F)
			  .sound(SoundType.BONE_BLOCK)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE))));
		this.registerDefaultState(this.stateDefinition.any().setValue(Y_EVEN, false).setValue(Z_EVEN, false));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		int height = context.getClickedPos().getY();
		int side = context.getClickedPos().getZ();
		
		if (height % 2 == 0 && side % 2 == 0) {
			return this.defaultBlockState().setValue(Y_EVEN, true).setValue(Z_EVEN, true);
		} else if (height % 2 == 0) {
			return this.defaultBlockState().setValue(Y_EVEN, true);
		} else if (side % 2 == 0) {
			return this.defaultBlockState().setValue(Z_EVEN, true);
		} else {
			return this.defaultBlockState();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		int height = pos.getY();
		int side = pos.getZ();
		
		if (height % 2 == 0 && side % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Y_EVEN, true).setValue(Z_EVEN, true));
		} else if (height % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Y_EVEN, true));
		} else if (side % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Z_EVEN, true));
		}
		
		super.tick(state, worldIn, pos, rand);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		int height = pos.getY();
		int side = pos.getZ();
		
		if (height % 2 == 0 && side % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Y_EVEN, true).setValue(Z_EVEN, true));
		} else if (height % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Y_EVEN, true));
		} else if (side % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Z_EVEN, true));
		}
		super.onPlace(state, worldIn, pos, oldState, isMoving);
	}
	
	
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(Y_EVEN, Z_EVEN);
	}
}
