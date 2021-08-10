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
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ToolType;

public class MonolithicBelwithBricksBlock extends Block
{
	public static final BooleanProperty X_EVEN = BooleanProperty.create("x_even");
	public static final BooleanProperty Y_EVEN = BooleanProperty.create("y_even");
	public static final BooleanProperty Z_EVEN = BooleanProperty.create("z_even");
	
	public MonolithicBelwithBricksBlock()
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
		int sideZ = context.getClickedPos().getZ();
		
		if (height % 2 == 0 && sideZ % 2 == 0) {
			return this.defaultBlockState().setValue(Y_EVEN, true).setValue(Z_EVEN, true);
		} else if (height % 2 == 0) {
			return this.defaultBlockState().setValue(Y_EVEN, true);
		} else if (sideZ % 2 == 0) {
			return this.defaultBlockState().setValue(Z_EVEN, true);
		} else {
			return this.defaultBlockState();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		if (pos.getX() % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(X_EVEN, true));
		}
		
		if (pos.getY() % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Y_EVEN, true));
		}
		
		if (pos.getZ() % 2 == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(Z_EVEN, true));
		}
		
		super.tick(state, worldIn, pos, rand);
	}
	
	/**
	@SuppressWarnings("deprecation")
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (pos.getX() % 2 == 0) {
			worldIn.setBlockState(pos, state.with(X_EVEN, true));
		}
		
		if (pos.getY() % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Y_EVEN, true));
		}
		
		if (pos.getZ() % 2 == 0) {
			worldIn.setBlockState(pos, state.with(Z_EVEN, true));
		}
		
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
	}
	*/
	
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(X_EVEN, Y_EVEN, Z_EVEN);
	}
}
