package birsy.clinker.common.block.riekplant;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.ToolType;

public class RiekTubeBlock extends DirectionalBlock {
	protected static final VoxelShape RIEK_TUBE_VERTICAL_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape RIEK_TUBE_NS_AABB = Block.box(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 16.0D);
	protected static final VoxelShape RIEK_TUBE_EW_AABB = Block.box(0.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D);
	
	public RiekTubeBlock() {
		super((Block.Properties.of(Material.GRASS)
				  .strength(2.0F)
				  .sound(SoundType.SLIME_BLOCK)
				  .harvestLevel(0)
				  .harvestTool(ToolType.AXE)
				  .noOcclusion()));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch(state.getValue(FACING).getAxis()) {
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
