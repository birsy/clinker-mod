package birsy.clinker.common.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LayerLightEngine;

import java.util.Random;


public abstract class SpreadableAshBlock extends Block {
	Block grassBase;
	
	protected SpreadableAshBlock(Block.Properties builder, Block blockStateIn)
	{
		super(builder);
		grassBase = blockStateIn;
	}

	private static boolean canBeGrass(BlockState pState, LevelReader pLevelReader, BlockPos pPos) {
		BlockPos blockpos = pPos.above();
		BlockState blockstate = pLevelReader.getBlockState(blockpos);
		if (blockstate.getFluidState().getAmount() == 8) {
			return false;
		} else {
			int i = LayerLightEngine.getLightBlockInto(pLevelReader, pState, pPos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(pLevelReader, blockpos));
			return i < pLevelReader.getMaxLightLevel();
		}
	}

	private static boolean canPropagate(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		BlockPos blockpos = pPos.above();
		return canBeGrass(pState, pLevel, pPos) && !pLevel.getFluidState(blockpos).is(FluidTags.WATER);
	}

	/**
	 * Performs a random tick on a block.
	 */
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (!canBeGrass(pState, pLevel, pPos)) {
			if (!pLevel.isAreaLoaded(pPos, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
			pLevel.setBlockAndUpdate(pPos, grassBase.defaultBlockState());
		} else {
			if (pLevel.getMaxLocalRawBrightness(pPos.above()) >= 9) {
				BlockState blockstate = this.defaultBlockState();

				for(int i = 0; i < 4; ++i) {
					BlockPos blockpos = pPos.offset(pRandom.nextInt(3) - 1, pRandom.nextInt(5) - 3, pRandom.nextInt(3) - 1);
					if (pLevel.getBlockState(blockpos).is(grassBase) && canPropagate(blockstate, pLevel, blockpos)) {
						pLevel.setBlockAndUpdate(blockpos, blockstate);
					}
				}
			}

		}
	}
}
