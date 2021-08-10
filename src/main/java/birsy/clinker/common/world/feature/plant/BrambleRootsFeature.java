package birsy.clinker.common.world.feature.plant;

import java.util.Random;

import com.mojang.serialization.Codec;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;


public class BrambleRootsFeature extends Feature<NoneFeatureConfiguration> {
	private static final Direction[] directionArray = Direction.values();

	public BrambleRootsFeature(Codec<NoneFeatureConfiguration> featureConfigIn) {
		super(featureConfigIn);
	}

	@Override
	public boolean place(WorldGenLevel seedReaderIn, ChunkGenerator chunkGeneratorIn, Random random, BlockPos blockPosIn, NoneFeatureConfiguration featureConfigIn) {
		if (!seedReaderIn.isEmptyBlock(blockPosIn)) {
			return false;
		} else {
			BlockState blockstate = seedReaderIn.getBlockState(blockPosIn.above());
			if (!blockstate.is(ClinkerBlocks.BRIMSTONE.get()) && !blockstate.is(ClinkerBlocks.PACKED_ASH.get())) {
				return false;
			} else {
				this.placeRoofNetherWart(seedReaderIn, random, blockPosIn);
				this.placeRoofWeepingVines(seedReaderIn, random, blockPosIn);
				return true;
			}
		}
	}

	private void placeRoofNetherWart(LevelAccessor worldIn, Random random, BlockPos blockPosIn) {
		worldIn.setBlock(blockPosIn, ClinkerBlocks.BRAMBLE.get().defaultBlockState(), 2);
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos blockpos$mutable1 = new BlockPos.MutableBlockPos();

		for(int i = 0; i < 200; ++i) {
			blockpos$mutable.setWithOffset(blockPosIn, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
			if (worldIn.isEmptyBlock(blockpos$mutable)) {
				int j = 0;

				for(Direction direction : directionArray) {
					BlockState blockstate = worldIn.getBlockState(blockpos$mutable1.setWithOffset(blockpos$mutable, direction));
					if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.PACKED_ASH.get())) {
						++j;
					}

					if (j > 1) {
						break;
					}
				}

				if (j == 1) {
					worldIn.setBlock(blockpos$mutable, ClinkerBlocks.BRAMBLE.get().defaultBlockState(), 2);
				}
			}
		}
	}

	private void placeRoofWeepingVines(LevelAccessor worldIn, Random random, BlockPos blockPosIn) {
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

		for(int i = 0; i < 100; ++i) {
			blockpos$mutable.setWithOffset(blockPosIn, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
			if (worldIn.isEmptyBlock(blockpos$mutable)) {
				BlockState blockstate = worldIn.getBlockState(blockpos$mutable.above());
				if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.PACKED_ASH.get())) {
					int j = Mth.nextInt(random, 1, 8);
					if (random.nextInt(6) == 0) {
						j *= 2;
					}

					if (random.nextInt(5) == 0) {
						j = 1;
					}

					placeWeepingVinesColumn(worldIn, random, blockpos$mutable, j, 17, 25);
				}
			}
		}
	}

	public static void placeWeepingVinesColumn(LevelAccessor worldIn, Random random, BlockPos.MutableBlockPos blockpos$mutable, int chance, int minimumTries, int maximumTries) {
		for(int i = 0; i <= chance; ++i) {
			if (worldIn.isEmptyBlock(blockpos$mutable)) {
				if (i == chance || !worldIn.isEmptyBlock(blockpos$mutable.below())) {
					worldIn.setBlock(blockpos$mutable, ClinkerBlocks.BRAMBLE_ROOTS.get().defaultBlockState(), 2);
					break;
				}

				worldIn.setBlock(blockpos$mutable, ClinkerBlocks.BRAMBLE_ROOTS_BOTTOM.get().defaultBlockState(), 2);
			}
			blockpos$mutable.move(Direction.DOWN);
		}
	}
}
