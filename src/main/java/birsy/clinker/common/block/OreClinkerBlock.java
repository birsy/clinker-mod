package birsy.clinker.common.block;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.ToolType;

public class OreClinkerBlock extends OreBlock
{
	int expMin;
	int expMax;
	
	public OreClinkerBlock(float hardness, float resistance, int level, int expMin, int expMax, SoundType sound)
	{
		super((Block.Properties.of(Material.STONE)
			  .strength(hardness, resistance)
			  .sound(sound)
			  .harvestLevel(level)
			  .harvestTool(ToolType.PICKAXE)));
		this.expMin = expMin;
		this.expMax = expMax;
	}
	
	@Override
	protected int xpOnDrop(Random rand)
	{
		return Mth.nextInt(rand, this.expMin, this.expMax);
	}
	
	@Override
	public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch)
	{
		return silktouch == 0 ? this.xpOnDrop(RANDOM) : 0;
	}
}