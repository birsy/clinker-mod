package birsy.clinker.common.block.bramble;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlockHelper;
import net.minecraft.entity.Entity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AbstractBrambleTopBlock extends AbstractTopPlantBlock implements net.minecraftforge.common.IForgeShearable
{	
	public static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	
	public AbstractBrambleTopBlock(AbstractBlock.Properties properties, Direction growthDirection) {
		super(properties, growthDirection, SHAPE, true, 0.05D);
	}
	
	@Override
	protected int getGrowthAmount(Random rand) {
		return PlantBlockHelper.getGrowthAmount(rand);
	}
	
	@Override
	protected boolean canGrowIn(BlockState state) {
		return PlantBlockHelper.isAir(state);
	}
	
	@Override
	public boolean isTransparent(BlockState state) {
		return true;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
		entityIn.setMotionMultiplier(state, new Vector3d(0.35D, 0.95D, 0.35D));
		entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
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
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, net.minecraft.entity.LivingEntity entity) { return true; }
	
	public AbstractBlock.OffsetType getOffsetType() {
		return AbstractBlock.OffsetType.XZ;
	}
}
