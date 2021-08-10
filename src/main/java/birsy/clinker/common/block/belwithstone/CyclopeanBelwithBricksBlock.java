package birsy.clinker.common.block.belwithstone;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class CyclopeanBelwithBricksBlock extends Block
{
	public static final BooleanProperty Y_EVEN = BooleanProperty.create("y_even");
	public static final BooleanProperty Z_EVEN = BooleanProperty.create("z_even");
	
	public CyclopeanBelwithBricksBlock()
	{
		super(((Block.Properties.create(Material.ROCK)
			  .hardnessAndResistance(10F, 6.0F)
			  .sound(SoundType.BONE)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE))));
		this.setDefaultState(this.stateContainer.getBaseState().with(Y_EVEN, false).with(Z_EVEN, false));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		int height = context.getPos().getY();
		int side = context.getPos().getZ();
		
		if (height % 2 == 0 && side % 2 == 0) {
			return this.getDefaultState().with(Y_EVEN, true).with(Z_EVEN, true);
		} else if (height % 2 == 0) {
			return this.getDefaultState().with(Y_EVEN, true);
		} else if (side % 2 == 0) {
			return this.getDefaultState().with(Z_EVEN, true);
		} else {
			return this.getDefaultState();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		int height = pos.getY();
		int side = pos.getZ();
		
		if (height % 2 == 0 && side % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Y_EVEN, true).with(Z_EVEN, true));
		} else if (height % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Y_EVEN, true));
		} else if (side % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Z_EVEN, true));
		}
		
		super.tick(state, worldIn, pos, rand);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		int height = pos.getY();
		int side = pos.getZ();
		
		if (height % 2 == 0 && side % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Y_EVEN, true).with(Z_EVEN, true));
		} else if (height % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Y_EVEN, true));
		} else if (side % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Z_EVEN, true));
		}
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
	}
	
	
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(Y_EVEN, Z_EVEN);
	}
}
