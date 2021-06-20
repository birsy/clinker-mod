package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.common.world.feature.ClinkerBiomeFeatures;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.BlockStateProvidingFeatureConfig;
import net.minecraft.world.server.ServerWorld;

public class RootedAshBlock extends SpreadableAshBlock
{
	public RootedAshBlock(Block blockIn)
	{
		super(Block.Properties.create(Material.ORGANIC, MaterialColor.ORANGE_TERRACOTTA)
				.tickRandomly()
				.hardnessAndResistance(0.6F)
				.sound(SoundType.WART),
				blockIn);
	}
	
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}
	
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
	      BlockPos blockpos = pos.up();
	      createGrass(worldIn, rand, blockpos, ClinkerBiomeFeatures.ROOT_GRASS_FEATURE_CONFIG, 3, 1);
	}

	public static boolean createGrass(IWorld worldIn, Random random, BlockPos blockPosIn, BlockStateProvidingFeatureConfig featureConfigIn, int min, int max) {
		for(Block block = worldIn.getBlockState(blockPosIn.down()).getBlock(); !(block == ClinkerBlocks.ROOTED_ASH.get()) && blockPosIn.getY() > 0; block = worldIn.getBlockState(blockPosIn).getBlock()) {
			blockPosIn = blockPosIn.down();
		}

		int i = blockPosIn.getY();
		if (i >= 1 && i + 1 < 256) {
			int j = 0;

			for(int k = 0; k < min * min; ++k) {
				BlockPos blockpos = blockPosIn.add(random.nextInt(min) - random.nextInt(min), random.nextInt(max) - random.nextInt(max), random.nextInt(min) - random.nextInt(min));
				BlockState blockstate = featureConfigIn.stateProvider.getBlockState(random, blockpos);
				if (worldIn.isAirBlock(blockpos) && blockpos.getY() > 0 && blockstate.isValidPosition(worldIn, blockpos)) {
					worldIn.setBlockState(blockpos, blockstate, 2);
					++j;
				}
			}

			return j > 0;
		} else {
			return false;
		}
	}
}
