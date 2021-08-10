package birsy.clinker.common.block;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DustStalagmiteBlock extends FallingBlock
{
	private final int dustColor;
	public static final BooleanProperty BIG = BooleanProperty.create("big");
	
	public DustStalagmiteBlock()
	{
		super(((Block.Properties.of(Material.DIRT)
			  .instabreak()
			  .sound(SoundType.SOUL_SAND)
			  .noDrops()
			  .randomTicks()
			  )));
		this.dustColor = 8616308;
	}
	
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(8) == 0) {
			worldIn.destroyBlock(pos, false);
		}
	}
	
	protected void falling(FallingBlockEntity fallingEntity) {
		fallingEntity.setHurtsEntities(true);
	}

	public void onLand(Level worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
		if (!fallingBlock.isSilent()) {
			worldIn.levelEvent(1031, pos, 0);
			worldIn.destroyBlock(pos, false);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getDustColor(BlockState state, BlockGetter p_189876_2_, BlockPos p_189876_3_) {
	   return this.dustColor;
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BIG);
	}
}
