package birsy.clinker.common.world.feature.enviornment;

import java.util.Random;

import com.mojang.serialization.Codec;

import birsy.clinker.common.block.AshLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

/**
 * Generates a dune of ash out of the "ash layer" block. Who knew this would be so difficult?
 * I took a few glances at Atum's stuff, but it didn't help much. Oh well, I'll include it in the comment anyway!
 *
 * TODO - Figure out a way to get these generating dynamically? - If you leave a window open or something. Could look pretty sweet.
 */
public class AshDuneFeature extends Feature<NoFeatureConfig> {

	public AshDuneFeature(Codec<NoFeatureConfig> FeatureConfig) {
		super(FeatureConfig);
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		if (pos.getY() < 1) {
			return false;
		} else {
			int baseHeight = MathHelper.clamp(rand.nextInt(8), 2, 8);
			this.setBlockState(reader, pos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, baseHeight));

			BlockPos.Mutable blockPos = pos.toMutable();

			for (int x = -(baseHeight + 1); x < baseHeight + 1; x++) {
				for (int z = -(baseHeight + 1); z < baseHeight + 1; z++) {
					blockPos.setX(x);
					blockPos.setZ(z);

					/**
					while(!reader.getBlockState(blockPos).isSolid() && blockPos.getY() < 2) {
						blockPos.down();
					}
					 */

					int height = MathHelper.clamp(baseHeight - (blockPos.manhattanDistance(pos)), 0, 8);
					int variation = rand.nextInt(2) - 1;

					if (height > 0) {
						this.setBlockState(reader, blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, height));//.with(AshLayerBlock.WATERLOGGED, isWaterlogged));
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

	public void setAshLayer(ISeedReader worldIn, BlockPos blockPos, int height) {
		int clampedHeight = MathHelper.clamp(height, 0, 8);

		if (clampedHeight > 0) {
			boolean isWaterlogged = worldIn.getBlockState(blockPos).getBlock() == Blocks.WATER;
			if (worldIn.getBlockState(blockPos).getBlock() == ClinkerBlocks.ASH_LAYER.get()) {
				if (worldIn.getBlockState(blockPos).get(AshLayerBlock.LAYERS) < clampedHeight) {
					this.setBlockState(worldIn, blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, clampedHeight).with(AshLayerBlock.WATERLOGGED, isWaterlogged));
				}
			} else if (worldIn.getBlockState(blockPos).getMaterial().isReplaceable()) {
				this.setBlockState(worldIn, blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, clampedHeight).with(AshLayerBlock.WATERLOGGED, isWaterlogged));
			}
		}
	}

	public void fillWithAsh(ISeedReader worldIn, Random rand, BlockPos pos, int baseHeightIn) {
		BlockPos.Mutable blockPos = pos.toMutable();

		for (int x = -baseHeightIn; x < baseHeightIn + 1; x++) {
			for (int z = -baseHeightIn; z < baseHeightIn + 1; z++) {
				blockPos.setX(x);
				blockPos.setZ(z);

				int height = (int) MathHelper.clamp(baseHeightIn - (blockPos.manhattanDistance(pos)), 0, 8);
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
	
	private int getPlacementY(IWorld worldIn, BlockPos pos) {
		BlockPos.Mutable placementPos = pos.toMutable();
		for(int i = 0; i < 20; i++) {
			if (placementPos.getY() <= 0) {
				break;
			} else if (!worldIn.getBlockState(placementPos.down()).isSolid()) {
				placementPos.setPos(placementPos.down());
			} else {
				break;
			}
		}

		//If the block below it isn't solid, or the block itself isn't solid, return an invalid position.
		if (!worldIn.getBlockState(placementPos.down()).isSolid() || worldIn.getBlockState(placementPos).isSolid()) {
			return 0;
		} else {
			return placementPos.getY();
		}
	}
}
