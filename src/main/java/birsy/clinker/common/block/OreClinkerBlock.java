package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class OreClinkerBlock extends DropExperienceBlock
{
	int expMin;
	int expMax;
	
	public OreClinkerBlock(float hardness, float resistance, int expMin, int expMax, SoundType sound) {
		super((Block.Properties.of(Material.STONE)
			  .strength(hardness, resistance)
			  .sound(sound)));
		this.expMin = expMin;
		this.expMax = expMax;
	}
	
	public int getExperience(RandomSource rand) {
		return Mth.nextInt(rand, this.expMin, this.expMax);
	}


	@Override
	public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
		return silkTouchLevel == 0 ? this.getExperience(randomSource) : 0;
	}
}