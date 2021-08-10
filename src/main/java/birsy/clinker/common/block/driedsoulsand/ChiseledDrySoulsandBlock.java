package birsy.clinker.common.block.driedsoulsand;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class ChiseledDrySoulsandBlock extends HorizontalDirectionalBlock
{	
	public ChiseledDrySoulsandBlock()
	{
		super(((Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
			.strength(1.5F, 6.0F)
			.sound(SoundType.NETHER_BRICKS)
			.harvestLevel(1)
			.harvestTool(ToolType.PICKAXE)
			)));
	}
		
	public void stepOn(Level worldIn, BlockPos pos, Entity entityIn)
	{
		Vec3 vec3d = entityIn.getDeltaMovement();
		super.stepOn(worldIn, pos, entityIn);
		entityIn.setDeltaMovement(vec3d.x * 1.3, -vec3d.y, vec3d.z * 1.3);
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand)
	{
		if (rand.nextInt(100) == 0)
		{
			worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.5F, rand.nextFloat() * 0.05F + 0.8F, false);
		}
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
