package birsy.clinker.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public class OreClinkerBlock extends OreBlock
{
	int expMin;
	int expMax;
	
	public OreClinkerBlock(float hardness, float resistance, int level, int expMin, int expMax, SoundType sound)
	{
		super((Block.Properties.create(Material.ROCK)
			  .hardnessAndResistance(hardness, resistance)
			  .sound(sound)
			  .harvestLevel(level)
			  .harvestTool(ToolType.PICKAXE)));
		this.expMin = expMin;
		this.expMax = expMax;
	}
	
	@Override
	protected int getExperience(Random rand)
	{
		return MathHelper.nextInt(rand, this.expMin, this.expMax);
	}
	
	@Override
	public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch)
	{
		return silktouch == 0 ? this.getExperience(RANDOM) : 0;
	}
}