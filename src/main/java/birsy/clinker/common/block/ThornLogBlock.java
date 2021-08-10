package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

public class ThornLogBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock
{
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	
	public ThornLogBlock()
	{
		super((Block.Properties.of(Material.WOOD)
			  .strength(5.0f, 6.0f)
			  .sound(SoundType.WOOD)
			  .harvestLevel(0)
			  .harvestTool(ToolType.AXE)
			  .noOcclusion()));
	}
	
    //Damages an entity on collide
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
        if (this == ClinkerBlocks.THORN_LOG.get())
        {
            entityIn.hurt(DamageSource.CACTUS, 1.0F);
        }
    }
	
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}

	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}

	public boolean isNormalCube(BlockState state, BlockGetter worldIn, BlockPos pos)
	{
		return false;
	}

	public boolean canEntitySpawn(BlockState state, BlockGetter worldIn, BlockPos pos, EntityType<?> type)
	{
		return false;
	}
	
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type)
	{
		return false;
	}
	
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
	{
	    return true;
	}
}
