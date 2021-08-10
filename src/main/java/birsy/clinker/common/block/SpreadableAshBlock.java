package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.server.level.ServerLevel;

public abstract class SpreadableAshBlock extends Block {
	Block grassBase;
	
	protected SpreadableAshBlock(BlockBehaviour.Properties builder, Block blockStateIn)
	{
		super(builder);
		grassBase = blockStateIn;
	}
	
	private static boolean isBlockCovered(BlockState blockStateIn, LevelReader worldIn, BlockPos blockPosIn) {
		BlockPos blockpos = blockPosIn.above();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockstate.getFluidState().getAmount() == 8) {
			return false;
		} else {
			int i = LayerLightEngine.getLightBlockInto(worldIn, blockStateIn, blockPosIn, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(worldIn, blockpos));
			return i < worldIn.getMaxLightLevel();
		}
	}

	private static boolean isBlockDrowned(BlockState blockStateIn, LevelReader worldIn, BlockPos blockPosIn) {
		BlockPos blockpos = blockPosIn.above();
		return isBlockCovered(blockStateIn, worldIn, blockPosIn) && !worldIn.getFluidState(blockpos).is(FluidTags.WATER);
	}

	/**
	 * Performs a random tick on a block.
	 */
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		if (!(worldIn.getBlockState(pos.above()).getBlock() == ClinkerBlocks.ROOTSTALK.get())) {
			if (worldIn.dimension() == ClinkerDimensions.OTHERSHORE && worldIn.canSeeSky(pos)) {
				if (random.nextInt(5) == 1) {
					worldIn.setBlockAndUpdate(pos, grassBase.defaultBlockState());
				}
			}
			if (!isBlockCovered(state, worldIn, pos)) {
				if (!worldIn.isAreaLoaded(pos, 3))
					return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
				worldIn.setBlockAndUpdate(pos, grassBase.defaultBlockState());
			} else if (!isDarkEnough(state, worldIn, pos)) {
				worldIn.setBlockAndUpdate(pos, grassBase.defaultBlockState());
			} else {
				if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9) {
					BlockState blockstate = this.defaultBlockState();
					for (int i = 0; i < 4; ++i) {
						BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						if (worldIn.getBlockState(blockpos).is(grassBase) && isBlockDrowned(blockstate, worldIn, blockpos)) {
							worldIn.setBlockAndUpdate(blockpos, blockstate);
						}
					}
				}
			}
		}
	}
	
	private static boolean isDarkEnough(BlockState state, LevelReader reader, BlockPos pos) {
		BlockPos blockpos = pos.above();
		BlockState blockstate = reader.getBlockState(blockpos);
		int i = LayerLightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
		return i < reader.getMaxLightLevel();
	}
}
