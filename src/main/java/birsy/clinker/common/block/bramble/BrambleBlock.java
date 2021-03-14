package birsy.clinker.common.block.bramble;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BrambleBlock extends BushBlock implements net.minecraftforge.common.IForgeShearable
{	
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	
	public BrambleBlock()
	{
		super(((Block.Properties.create(Material.ORGANIC)
			  .sound(SoundType.CROP)
			  .tickRandomly()
			  .notSolid()
			  .setRequiresTool()
			  .hardnessAndResistance(4.0F)
			  .doesNotBlockMovement())));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		BlockPos posUp = pos.up();
		BlockPos posDown = pos.down();
		if (worldIn.getBlockState(pos.north()).getBlock() == ClinkerBlocks.BRAMBLE.get() || worldIn.getBlockState(pos.south()).getBlock() == ClinkerBlocks.BRAMBLE.get() || worldIn.getBlockState(pos.east()).getBlock() == ClinkerBlocks.BRAMBLE.get() || worldIn.getBlockState(pos.west()).getBlock() == ClinkerBlocks.BRAMBLE.get()) {
			return;
		} else if (!worldIn.getBlockState(posUp).isSolid() && worldIn.getBlockState(posDown).isSolid()) {
			worldIn.setBlockState(pos, ClinkerBlocks.BRAMBLE_VINES_TOP.get().getDefaultState(), 2);
		} else if (worldIn.getBlockState(posUp).isSolid() && !worldIn.getBlockState(posDown).isSolid()) {
			worldIn.setBlockState(pos, ClinkerBlocks.BRAMBLE_ROOTS_BOTTOM.get().getDefaultState(), 2);
		}
		
		super.tick(state, worldIn, pos, rand);
	}
	
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}
	
	public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1;
	}
	
	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return true;
	}
	
	public AbstractBlock.OffsetType getOffsetType() {
		return AbstractBlock.OffsetType.XYZ;
	}
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
		if (entityIn.getMotion().getY() > 1) {
			worldIn.destroyBlock(pos, true, entityIn);
		}
		
		entityIn.setMotionMultiplier(state, new Vector3d(0.35D, 0.7D, 0.35D));
		entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
    }
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}
	
	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if(state.hasProperty(BlockStateProperties.WATERLOGGED)) {
			return 0;
		}
		return 60;
	}	
}
