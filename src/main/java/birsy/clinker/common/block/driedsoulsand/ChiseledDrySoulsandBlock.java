package birsy.clinker.common.block.driedsoulsand;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class ChiseledDrySoulsandBlock extends HorizontalBlock
{	
	public ChiseledDrySoulsandBlock()
	{
		super(((Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY)
			.hardnessAndResistance(1.5F, 6.0F)
			.sound(SoundType.NETHER_BRICK)
			.harvestLevel(1)
			.harvestTool(ToolType.PICKAXE)
			)));
	}
		
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
	{
		Vector3d vec3d = entityIn.getMotion();
		super.onEntityWalk(worldIn, pos, entityIn);
		entityIn.setMotion(vec3d.x * 1.3, -vec3d.y, vec3d.z * 1.3);
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		if (rand.nextInt(100) == 0)
		{
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.05F + 0.8F, false);
		}
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}
}
