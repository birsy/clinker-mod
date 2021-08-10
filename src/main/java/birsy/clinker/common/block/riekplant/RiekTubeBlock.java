package birsy.clinker.common.block.riekplant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class RiekTubeBlock extends DirectionalBlock {
	protected static final VoxelShape RIEK_TUBE_VERTICAL_AABB = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape RIEK_TUBE_NS_AABB = Block.makeCuboidShape(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 16.0D);
	protected static final VoxelShape RIEK_TUBE_EW_AABB = Block.makeCuboidShape(0.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D);
	
	public RiekTubeBlock() {
		super((Block.Properties.create(Material.ORGANIC)
				  .hardnessAndResistance(2.0F)
				  .sound(SoundType.SLIME)
				  .harvestLevel(0)
				  .harvestTool(ToolType.AXE)
				  .notSolid()));
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch(state.get(FACING).getAxis()) {
		case X:
		default:
			return RIEK_TUBE_EW_AABB;
		case Z:
			return RIEK_TUBE_NS_AABB;
		case Y:
			return RIEK_TUBE_VERTICAL_AABB;
		}
	}
}
