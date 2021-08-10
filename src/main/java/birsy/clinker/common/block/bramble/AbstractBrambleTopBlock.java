package birsy.clinker.common.block.bramble;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

public abstract class AbstractBrambleTopBlock extends GrowingPlantHeadBlock implements net.minecraftforge.common.IForgeShearable
{	
	public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	
	public AbstractBrambleTopBlock(BlockBehaviour.Properties properties, Direction growthDirection) {
		super(properties, growthDirection, SHAPE, true, 0.05D);
	}
	
	@Override
	protected int getBlocksToGrowWhenBonemealed(Random rand) {
		return NetherVines.getBlocksToGrowWhenBonemealed(rand);
	}
	
	@Override
	protected boolean canGrowInto(BlockState state) {
		return NetherVines.isValidGrowthState(state);
	}

	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type)
	{
		return false;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}
	
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
		entityIn.makeStuckInBlock(state, new Vec3(0.35D, 0.95D, 0.35D));
		entityIn.hurt(DamageSource.CACTUS, 1.0F);
    }
	
	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if(state.hasProperty(BlockStateProperties.WATERLOGGED)) {
			return 0;
		}
		return 60;
	}
	
	@Override
	public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) { return true; }
	
	public BlockBehaviour.OffsetType getOffsetType() {
		return BlockBehaviour.OffsetType.XZ;
	}
}
