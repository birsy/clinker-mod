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
 * TO DO - Figure out a way to get these generating dynamically? - If you leave a window open or something. Could look pretty sweet.
 */
public class AshDuneFeature extends Feature<NoFeatureConfig> {

	public AshDuneFeature(Codec<NoFeatureConfig> FeatureConfig) {
		super(FeatureConfig);
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		if (pos.getY() < 5) {
			return false;
		} else {
			int baseHeight = MathHelper.clamp(rand.nextInt(8), 2, 8);
			reader.setBlockState(pos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, baseHeight), 2);
			STUPIDWONTWORKUGHHH(reader, generator, rand, pos, baseHeight);
			return true;
		}
	}

	public void STUPIDWONTWORKUGHHH(ISeedReader worldIn, ChunkGenerator chunkIn, Random rand, BlockPos pos, int baseHeightIn) {
		BlockPos.Mutable blockPos = pos.toMutable();
		boolean bool = false;
		int timesThrough = 1;

		for (int i = 1; i < (baseHeightIn * baseHeightIn) - 1; i++) {
			if (!(i % 2 == 0)) {
				blockPos.add(bool ? timesThrough : -timesThrough, 0, 0);
				blockPos.setY(getPlacementY(worldIn, blockPos));

				int height = MathHelper.clamp(getAshLayerHeight(worldIn, blockPos) + (rand.nextInt(2) - 1), 0, 8);
				setAshLayer(worldIn, blockPos, height);

			} else {
				blockPos.add(0, bool ? timesThrough : -timesThrough, 0);
				blockPos.setY(getPlacementY(worldIn, blockPos));

				int height = MathHelper.clamp(getAshLayerHeight(worldIn, blockPos) + (rand.nextInt(2) - 1), 0, 8);
				setAshLayer(worldIn, blockPos, height);


				bool = !bool;
				timesThrough++;
			}
		}
	}

	public void setAshLayer(ISeedReader worldIn, BlockPos blockPos, int height) {
		if (height > 0) {
			boolean isWaterlogged = worldIn.getFluidState(blockPos).getFluid() == Fluids.WATER;
			if (worldIn.getBlockState(blockPos).getBlock() == ClinkerBlocks.ASH_LAYER.get()) {
				worldIn.setBlockState(blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, Integer.valueOf(Math.max(height, worldIn.getBlockState(blockPos).get(AshLayerBlock.LAYERS)))).with(AshLayerBlock.WATERLOGGED, Boolean.valueOf(isWaterlogged)), 2);
			} else if (!worldIn.getBlockState(blockPos).isSolid()) {
				worldIn.setBlockState(blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, Integer.valueOf(height)).with(AshLayerBlock.WATERLOGGED, Boolean.valueOf(isWaterlogged)), 2);
			}
		}
	}

	public void fillWithAsh(ISeedReader worldIn, ChunkGenerator chunkIn, Random rand, BlockPos pos, int baseHeightIn) {
		BlockPos.Mutable blockPos = pos.toMutable();

		for (int x = 0; x < (baseHeightIn * 2) - 1; x++) {
			for (int z = 0; z < (baseHeightIn * 2) - 1; z++) {
				blockPos.setX((x + pos.getX()) - (baseHeightIn - 1));
				blockPos.setZ((z + pos.getZ()) - (baseHeightIn - 1));

				for (int i = 0; i < baseHeightIn; i++) {
					int height = MathHelper.clamp(getAshLayerHeight(worldIn, blockPos) + (rand.nextInt(2) - 1), 0, 8);
					blockPos.setY(getPlacementY(worldIn, blockPos));

					if (!(blockPos.getY() == -1) && height > 0) {
						boolean isWaterlogged = worldIn.getFluidState(blockPos).getFluid() == Fluids.WATER;
						if (worldIn.getBlockState(blockPos).getBlock() == ClinkerBlocks.ASH_LAYER.get()) {
							worldIn.setBlockState(blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, Integer.valueOf(Math.max(height, worldIn.getBlockState(blockPos).get(AshLayerBlock.LAYERS)))).with(AshLayerBlock.WATERLOGGED, Boolean.valueOf(isWaterlogged)), 2);
						} else if (!worldIn.getBlockState(blockPos).isSolid()) {
							worldIn.setBlockState(blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, Integer.valueOf(height)).with(AshLayerBlock.WATERLOGGED, Boolean.valueOf(isWaterlogged)), 2);
						}
					}
				}

				//int distanceFromMiddle = blockPos.manhattanDistance(pos);
				//int height = MathHelper.clamp((distanceFromMiddle - baseHeightIn) * -2, 0, 8);

				/**
				if (!(blockPos.getY() == -1) && !(height == 0)) {
					boolean isWaterlogged = worldIn.getFluidState(blockPos).getFluid() == Fluids.WATER;
					if (worldIn.getBlockState(blockPos).getBlock() == ClinkerBlocks.ASH_LAYER.get()) {
						worldIn.setBlockState(blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, Integer.valueOf(Math.max(height, worldIn.getBlockState(blockPos).get(AshLayerBlock.LAYERS)))).with(AshLayerBlock.WATERLOGGED, Boolean.valueOf(isWaterlogged)), 2);
					} else if (!worldIn.getBlockState(blockPos).isSolid()) {
						worldIn.setBlockState(blockPos, ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, Integer.valueOf(height)).with(AshLayerBlock.WATERLOGGED, Boolean.valueOf(isWaterlogged)), 2);
					}
				}
				 */

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
			if(!worldIn.getBlockState(placementPos.down()).isSolid()) {
				placementPos.setPos(placementPos.down());
			} else {
				break;
			}
		}

		//If the block below it isn't solid, or the block itself isn't solid, return an invalid position.
		if (!worldIn.getBlockState(placementPos.down()).isSolid() || worldIn.getBlockState(placementPos).isSolid()) {
			return -1;
		} else {
			return placementPos.getY();
		}
	}

	private int getAshLayerHeight(IWorld worldIn, BlockPos pos) {
		Direction[] directionArray = {Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};

		for (Direction direction : directionArray) {
			if (worldIn.getBlockState(pos.offset(direction)).getBlock() == ClinkerBlocks.ASH_LAYER.get()) {
				return worldIn.getBlockState(pos.offset(direction)).get(AshLayerBlock.LAYERS) - 1;
			}
		}

		return 0;
	}
}
