package birsy.clinker.common.world.feature.enviornment;

import java.util.Random;

import com.mojang.serialization.Codec;

import birsy.clinker.common.block.AshLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Generates a dune of ash out of the "ash layer" block. Who knew this would be so difficult?
 * I took a few glances at Atum's stuff, but it didn't help much. Oh well, I'll include it in the comment anyway!
 *
 * TODO - Figure out a way to get these generating dynamically? - If you leave a window open or something. Could look pretty sweet.
 */
public class AshDuneFeature extends Feature<NoneFeatureConfiguration> {

	public AshDuneFeature(Codec<NoneFeatureConfiguration> FeatureConfig) {
		super(FeatureConfig);
	}

	@Override
	public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
		if (pos.getY() < 1) {
			return false;
		} else {
			int baseHeight = Mth.clamp(rand.nextInt(8), 2, 8);
			this.setBlock(reader, pos, ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, baseHeight));

			BlockPos.MutableBlockPos blockPos = pos.mutable();

			for (int x = -(baseHeight + 1); x < baseHeight + 1; x++) {
				for (int z = -(baseHeight + 1); z < baseHeight + 1; z++) {
					blockPos.setX(x);
					blockPos.setZ(z);

					/**
					while(!reader.getBlockState(blockPos).isSolid() && blockPos.getY() < 2) {
						blockPos.down();
					}
					 */

					int height = Mth.clamp(baseHeight - (blockPos.distManhattan(pos)), 0, 8);
					int variation = rand.nextInt(2) - 1;

					if (height > 0) {
						this.setBlock(reader, blockPos, ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, height));//.with(AshLayerBlock.WATERLOGGED, isWaterlogged));
					}
					/**
					if (reader.getBlockState(blockPos).getMaterial().isReplaceable() || reader.isAirBlock(blockPos)) {
						boolean isWaterlogged = reader.getBlockState(blockPos).getBlock() == Blocks.WATER;
					}
					 */
				}
			}

			return true;
		}
	}

	public void setAshLayer(WorldGenLevel worldIn, BlockPos blockPos, int height) {
		int clampedHeight = Mth.clamp(height, 0, 8);

		if (clampedHeight > 0) {
			boolean isWaterlogged = worldIn.getBlockState(blockPos).getBlock() == Blocks.WATER;
			if (worldIn.getBlockState(blockPos).getBlock() == ClinkerBlocks.ASH_LAYER.get()) {
				if (worldIn.getBlockState(blockPos).getValue(AshLayerBlock.LAYERS) < clampedHeight) {
					this.setBlock(worldIn, blockPos, ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, clampedHeight).setValue(AshLayerBlock.WATERLOGGED, isWaterlogged));
				}
			} else if (worldIn.getBlockState(blockPos).getMaterial().isReplaceable()) {
				this.setBlock(worldIn, blockPos, ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, clampedHeight).setValue(AshLayerBlock.WATERLOGGED, isWaterlogged));
			}
		}
	}

	public void fillWithAsh(WorldGenLevel worldIn, Random rand, BlockPos pos, int baseHeightIn) {
		BlockPos.MutableBlockPos blockPos = pos.mutable();

		for (int x = -baseHeightIn; x < baseHeightIn + 1; x++) {
			for (int z = -baseHeightIn; z < baseHeightIn + 1; z++) {
				blockPos.setX(x);
				blockPos.setZ(z);

				int height = (int) Mth.clamp(baseHeightIn - (blockPos.distManhattan(pos)), 0, 8);
				int variation = rand.nextInt(2) - 1;

				this.setAshLayer(worldIn, blockPos, height + variation);
			}
		}
	}

	/**
	 * //If this works, then this is totally scuffed.
	 * 				int distanceFromMiddle1 = blockPos.manhattanDistance(pos.add(baseHeightIn, 0, 0));
	 * 				int distanceFromMiddle2 = blockPos.manhattanDistance(pos.add(-baseHeightIn, 0, 0));
	 * 				int distanceFromMiddle3 = blockPos.manhattanDistance(pos.add(0, 0, baseHeightIn));
	 * 				int distanceFromMiddle4 = blockPos.manhattanDistance(pos.add(0, 0, -baseHeightIn));
	 *
	 * 				int height = MathHelper.clamp(Math.max(Math.max(Math.max(distanceFromMiddle1, distanceFromMiddle2), distanceFromMiddle3), distanceFromMiddle4) + (rand.nextInt(2) - 1), 0, 8);
*/
	
	private int getPlacementY(LevelAccessor worldIn, BlockPos pos) {
		BlockPos.MutableBlockPos placementPos = pos.mutable();
		for(int i = 0; i < 20; i++) {
			if (placementPos.getY() <= 0) {
				break;
			} else if (!worldIn.getBlockState(placementPos.below()).canOcclude()) {
				placementPos.set(placementPos.below());
			} else {
				break;
			}
		}

		//If the block below it isn't solid, or the block itself isn't solid, return an invalid position.
		if (!worldIn.getBlockState(placementPos.below()).canOcclude() || worldIn.getBlockState(placementPos).canOcclude()) {
			return 0;
		} else {
			return placementPos.getY();
		}
	}
}
