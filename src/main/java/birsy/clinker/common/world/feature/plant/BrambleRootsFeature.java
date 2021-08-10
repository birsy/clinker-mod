package birsy.clinker.common.world.feature.plant;

import java.util.Random;

import com.mojang.serialization.Codec;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;


public class BrambleRootsFeature extends Feature<NoFeatureConfig> {
	private static final Direction[] directionArray = Direction.values();

	public BrambleRootsFeature(Codec<NoFeatureConfig> featureConfigIn) {
		super(featureConfigIn);
	}

	@Override
	public boolean generate(ISeedReader seedReaderIn, ChunkGenerator chunkGeneratorIn, Random random, BlockPos blockPosIn, NoFeatureConfig featureConfigIn) {
		if (!seedReaderIn.isAirBlock(blockPosIn)) {
			return false;
		} else {
			BlockState blockstate = seedReaderIn.getBlockState(blockPosIn.up());
			if (!blockstate.matchesBlock(ClinkerBlocks.BRIMSTONE.get()) && !blockstate.matchesBlock(ClinkerBlocks.PACKED_ASH.get())) {
				return false;
			} else {
				this.func_236428_a_(seedReaderIn, random, blockPosIn);
				this.func_236429_b_(seedReaderIn, random, blockPosIn);
				return true;
			}
		}
	}

	private void func_236428_a_(IWorld worldIn, Random random, BlockPos blockPosIn) {
		worldIn.setBlockState(blockPosIn, ClinkerBlocks.BRAMBLE.get().getDefaultState(), 2);
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
		BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();

		for(int i = 0; i < 200; ++i) {
			blockpos$mutable.setAndOffset(blockPosIn, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
			if (worldIn.isAirBlock(blockpos$mutable)) {
				int j = 0;

				for(Direction direction : directionArray) {
					BlockState blockstate = worldIn.getBlockState(blockpos$mutable1.setAndMove(blockpos$mutable, direction));
					if (blockstate.matchesBlock(ClinkerBlocks.BRIMSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.PACKED_ASH.get())) {
						++j;
					}

					if (j > 1) {
						break;
					}
				}

				if (j == 1) {
					worldIn.setBlockState(blockpos$mutable, ClinkerBlocks.BRAMBLE.get().getDefaultState(), 2);
				}
			}
		}
	}

	private void func_236429_b_(IWorld worldIn, Random random, BlockPos blockPosIn) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

		for(int i = 0; i < 100; ++i) {
			blockpos$mutable.setAndOffset(blockPosIn, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
			if (worldIn.isAirBlock(blockpos$mutable)) {
				BlockState blockstate = worldIn.getBlockState(blockpos$mutable.up());
				if (blockstate.matchesBlock(ClinkerBlocks.BRIMSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.PACKED_ASH.get())) {
					int j = MathHelper.nextInt(random, 1, 8);
					if (random.nextInt(6) == 0) {
						j *= 2;
					}

					if (random.nextInt(5) == 0) {
						j = 1;
					}

					func_236427_a_(worldIn, random, blockpos$mutable, j, 17, 25);
				}
			}
		}
	}

	public static void func_236427_a_(IWorld worldIn, Random random, BlockPos.Mutable blockpos$mutable, int chance, int minimumTries, int maximumTries) {
		for(int i = 0; i <= chance; ++i) {
			if (worldIn.isAirBlock(blockpos$mutable)) {
				if (i == chance || !worldIn.isAirBlock(blockpos$mutable.down())) {
					worldIn.setBlockState(blockpos$mutable, ClinkerBlocks.BRAMBLE_ROOTS.get().getDefaultState(), 2);
					break;
				}

				worldIn.setBlockState(blockpos$mutable, ClinkerBlocks.BRAMBLE_ROOTS_BOTTOM.get().getDefaultState(), 2);
			}
			blockpos$mutable.move(Direction.DOWN);
		}
	}
}
