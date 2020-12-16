package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

@SuppressWarnings("deprecation")
public class ThornLogConnectedBlock extends SixWayBlock implements IWaterLoggable
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public ThornLogConnectedBlock() {
		super(0.3125F, (Block.Properties.create(Material.WOOD)
				  .hardnessAndResistance(5.0f, 6.0f)
				  .sound(SoundType.WOOD)
				  .harvestLevel(0)
				  .harvestTool(ToolType.AXE)
				  .notSolid()));
		this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(UP, Boolean.valueOf(false)).with(DOWN, Boolean.valueOf(false)));
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.makeConnections(context.getWorld(), context.getPos());
	}

	public BlockState makeConnections(IBlockReader blockReader, BlockPos pos) {
		Block block = blockReader.getBlockState(pos.down()).getBlock();
		Block block1 = blockReader.getBlockState(pos.up()).getBlock();
		Block block2 = blockReader.getBlockState(pos.north()).getBlock();
		Block block3 = blockReader.getBlockState(pos.east()).getBlock();
		Block block4 = blockReader.getBlockState(pos.south()).getBlock();
		Block block5 = blockReader.getBlockState(pos.west()).getBlock();
		return this.getDefaultState()
				.with(DOWN, Boolean.valueOf(block == this || block == ClinkerBlocks.LOCUST_LEAVES.get() || block == ClinkerBlocks.ASH.get() || block == ClinkerBlocks.LOCUST_LOG.get() ))
				.with(UP, Boolean.valueOf(block1 == this || block1 == ClinkerBlocks.LOCUST_LOG.get() || block1 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.with(NORTH, Boolean.valueOf(block2 == this || block2 == ClinkerBlocks.LOCUST_LOG.get() || block2 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.with(EAST, Boolean.valueOf(block3 == this || block3 == ClinkerBlocks.LOCUST_LOG.get() || block3 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.with(SOUTH, Boolean.valueOf(block4 == this || block4 == ClinkerBlocks.LOCUST_LOG.get() || block4 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.with(WEST, Boolean.valueOf(block5 == this || block5 == ClinkerBlocks.LOCUST_LOG.get() || block5 == ClinkerBlocks.LOCUST_LEAVES.get()));
	}
	
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (!stateIn.isValidPosition(worldIn, currentPos)) {
			worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
			return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		} else {
			boolean flag = facingState.getBlock() == this || facingState.isIn(Blocks.CHORUS_FLOWER) || facing == Direction.DOWN && facingState.isIn(Blocks.END_STONE);
			return stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(flag));
		}
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
	      return !state.get(WATERLOGGED);
	}
	
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	 //Damages an entity on collide
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
	{
		if (this == ClinkerBlocks.THORN_LOG.get())
		{
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
		}
	}
	
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}

	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type)
	{
		return false;
	}
}
