package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.common.world.feature.ClinkerBiomeFeatures;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.server.level.ServerLevel;

public class RootedAshBlock extends SpreadableAshBlock
{
	public RootedAshBlock(Block blockIn)
	{
		super(Block.Properties.of(Material.GRASS, MaterialColor.TERRACOTTA_ORANGE)
				.randomTicks()
				.strength(0.6F)
				.sound(SoundType.WART_BLOCK),
				blockIn);
	}
	
	public boolean canUseBonemeal(Level worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}
	
	public void grow(ServerLevel worldIn, Random rand, BlockPos pos, BlockState state) {
	      BlockPos blockpos = pos.above();
	      createGrass(worldIn, rand, blockpos, ClinkerBiomeFeatures.ROOT_GRASS_FEATURE_CONFIG, 3, 1);
	}

	public static boolean createGrass(LevelAccessor worldIn, Random random, BlockPos blockPosIn, BlockPileConfiguration featureConfigIn, int min, int max) {
		for(Block block = worldIn.getBlockState(blockPosIn.below()).getBlock(); !(block == ClinkerBlocks.ROOTED_ASH.get()) && blockPosIn.getY() > 0; block = worldIn.getBlockState(blockPosIn).getBlock()) {
			blockPosIn = blockPosIn.below();
		}

		int i = blockPosIn.getY();
		if (i >= 1 && i + 1 < 256) {
			int j = 0;

			for(int k = 0; k < min * min; ++k) {
				BlockPos blockpos = blockPosIn.offset(random.nextInt(min) - random.nextInt(min), random.nextInt(max) - random.nextInt(max), random.nextInt(min) - random.nextInt(min));
				BlockState blockstate = featureConfigIn.stateProvider.getState(random, blockpos);
				if (worldIn.isEmptyBlock(blockpos) && blockpos.getY() > 0 && blockstate.canSurvive(worldIn, blockpos)) {
					worldIn.setBlock(blockpos, blockstate, 2);
					++j;
				}
			}

			return j > 0;
		} else {
			return false;
		}
	}
}
