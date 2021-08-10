package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

@SuppressWarnings("deprecation")
public class ThornLogConnectedBlock extends PipeBlock implements SimpleWaterloggedBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public ThornLogConnectedBlock() {
		super(0.3125F, (Block.Properties.of(Material.WOOD)
				  .strength(5.0f, 6.0f)
				  .sound(SoundType.WOOD)
				  .harvestLevel(0)
				  .harvestTool(ToolType.AXE)
				  .noOcclusion()));
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(UP, Boolean.valueOf(false)).setValue(DOWN, Boolean.valueOf(false)));
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.makeConnections(context.getLevel(), context.getClickedPos());
	}

	public BlockState makeConnections(BlockGetter blockReader, BlockPos pos) {
		Block block = blockReader.getBlockState(pos.below()).getBlock();
		Block block1 = blockReader.getBlockState(pos.above()).getBlock();
		Block block2 = blockReader.getBlockState(pos.north()).getBlock();
		Block block3 = blockReader.getBlockState(pos.east()).getBlock();
		Block block4 = blockReader.getBlockState(pos.south()).getBlock();
		Block block5 = blockReader.getBlockState(pos.west()).getBlock();
		return this.defaultBlockState()
				.setValue(DOWN, Boolean.valueOf(block == this || block == ClinkerBlocks.LOCUST_LEAVES.get() || block == ClinkerBlocks.ASH.get() || block == ClinkerBlocks.LOCUST_LOG.get() ))
				.setValue(UP, Boolean.valueOf(block1 == this || block1 == ClinkerBlocks.LOCUST_LOG.get() || block1 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.setValue(NORTH, Boolean.valueOf(block2 == this || block2 == ClinkerBlocks.LOCUST_LOG.get() || block2 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.setValue(EAST, Boolean.valueOf(block3 == this || block3 == ClinkerBlocks.LOCUST_LOG.get() || block3 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.setValue(SOUTH, Boolean.valueOf(block4 == this || block4 == ClinkerBlocks.LOCUST_LOG.get() || block4 == ClinkerBlocks.LOCUST_LEAVES.get()))
				.setValue(WEST, Boolean.valueOf(block5 == this || block5 == ClinkerBlocks.LOCUST_LOG.get() || block5 == ClinkerBlocks.LOCUST_LEAVES.get()));
	}
	
	
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (!stateIn.canSurvive(worldIn, currentPos)) {
			worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
			return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		} else {
			boolean flag = facingState.getBlock() == this || facingState.is(Blocks.CHORUS_FLOWER) || facing == Direction.DOWN && facingState.is(Blocks.END_STONE);
			return stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), Boolean.valueOf(flag));
		}
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
	      return !state.getValue(WATERLOGGED);
	}
	
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	 //Damages an entity on collide
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
	{
		if (this == ClinkerBlocks.THORN_LOG.get())
		{
			entityIn.hurt(DamageSource.CACTUS, 1.0F);
		}
	}
	
	public boolean isNormalCube(BlockState state, BlockGetter worldIn, BlockPos pos)
	{
		return false;
	}

	public boolean canEntitySpawn(BlockState state, BlockGetter worldIn, BlockPos pos, EntityType<?> type)
	{
		return false;
	}
}
