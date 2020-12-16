package birsy.clinker.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DustStalagmiteBlock extends FallingBlock
{
	private final int dustColor;
	public static final BooleanProperty BIG = BooleanProperty.create("big");
	
	public DustStalagmiteBlock()
	{
		super(((Block.Properties.create(Material.EARTH)
			  .zeroHardnessAndResistance()
			  .sound(SoundType.SOUL_SAND)
			  .noDrops()
			  .tickRandomly()
			  )));
		this.dustColor = 8616308;
	}
	
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(8) == 0) {
			worldIn.destroyBlock(pos, false);
		}
	}
	
	protected void onStartFalling(FallingBlockEntity fallingEntity) {
		fallingEntity.setHurtEntities(true);
	}

	public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
		if (!fallingBlock.isSilent()) {
			worldIn.playEvent(1031, pos, 0);
			worldIn.destroyBlock(pos, false);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getDustColor(BlockState state, IBlockReader p_189876_2_, BlockPos p_189876_3_) {
	   return this.dustColor;
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BIG);
	}
}
