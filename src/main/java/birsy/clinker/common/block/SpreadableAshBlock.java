package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.world.ClinkerDimensions;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public abstract class SpreadableAshBlock extends Block {
	Block grassBase;
	
	protected SpreadableAshBlock(AbstractBlock.Properties builder, Block blockStateIn)
	{
		super(builder);
		grassBase = blockStateIn;
	}
	
	private static boolean isBlockCovered(BlockState blockStateIn, IWorldReader worldIn, BlockPos blockPosIn) {
		BlockPos blockpos = blockPosIn.up();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (blockstate.isIn(Blocks.SNOW) && blockstate.get(SnowBlock.LAYERS) == 1) {
			return true;
		} else if (blockstate.getFluidState().getLevel() == 8) {
			return false;
		} else {
			int i = LightEngine.func_215613_a(worldIn, blockStateIn, blockPosIn, blockstate, blockpos, Direction.UP, blockstate.getOpacity(worldIn, blockpos));
			return i < worldIn.getMaxLightLevel();
		}
	}

	private static boolean isBlockDrowned(BlockState blockStateIn, IWorldReader worldIn, BlockPos blockPosIn) {
		BlockPos blockpos = blockPosIn.up();
		return isBlockCovered(blockStateIn, worldIn, blockPosIn) && !worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER);
	}

	/**
	 * Performs a random tick on a block.
	 */
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (worldIn.getDimensionKey() == ClinkerDimensions.OTHERSHORE_WORLD && worldIn.canSeeSky(pos)) {
			if (random.nextInt(5) == 1) {
				worldIn.setBlockState(pos, grassBase.getDefaultState());
			}
		}
		
		if (!isBlockCovered(state, worldIn, pos)) {
			if (!worldIn.isAreaLoaded(pos, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
				worldIn.setBlockState(pos, grassBase.getDefaultState());
		} else if (!isDarkEnough(state, worldIn, pos)) {
			worldIn.setBlockState(pos, grassBase.getDefaultState());
		} else {
			if (worldIn.getLight(pos.up()) >= 9) {
				BlockState blockstate = this.getDefaultState();
				for(int i = 0; i < 4; ++i)
				{
					BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if (worldIn.getBlockState(blockpos).isIn(grassBase) && isBlockDrowned(blockstate, worldIn, blockpos))
					{
						worldIn.setBlockState(blockpos, blockstate);
					}
				}
			}
		}
	}
	
	private static boolean isDarkEnough(BlockState state, IWorldReader reader, BlockPos pos) {
		BlockPos blockpos = pos.up();
		BlockState blockstate = reader.getBlockState(blockpos);
		int i = LightEngine.func_215613_a(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getOpacity(reader, blockpos));
		return i < reader.getMaxLightLevel();
	}
}
