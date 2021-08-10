package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import javax.annotation.Nullable;
import java.util.Random;

public class ShorelinePuddleFeature extends Feature<ReplaceSphereConfiguration> {

	public ShorelinePuddleFeature(Codec<ReplaceSphereConfiguration> FeatureConfig) {
		super(FeatureConfig);
	}

	public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, ReplaceSphereConfiguration config) {
		Block block = ClinkerBlocks.BRIMSTONE.get();
		BlockPos blockpos = getValidPosition(reader, pos.mutable().clamp(Direction.Axis.Y, 1, reader.getMaxBuildHeight() - 1), block);

		if (blockpos == null) {
			return false;
		} else {
			int i = config.radius().sample(rand);
			boolean flag = false;

			for (BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, i, i, i)) {
				if (blockpos1.closerThan(blockpos, i)) {
					BlockState blockstate = reader.getBlockState(blockpos1);
					if (blockstate.is(ClinkerBlocks.SCORSTONE.get()) || blockstate.is(ClinkerBlocks.SALT_BLOCK.get())) {
						if (!reader.getBlockState(blockpos1.above()).canOcclude() && !(reader.getBlockState(blockpos1.above()).getBlock() == Blocks.WATER) && canHoldLiquid(reader, blockpos1, rand)) {
							this.setBlock(reader, blockpos1, Blocks.WATER.defaultBlockState());
						}

						flag = true;
					}
				}
			}

			return flag;
		}
	}

	private boolean canHoldLiquid(WorldGenLevel reader, BlockPos pos, Random rand) {
		boolean flag = true;
		Direction[] directionArray = {Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.DOWN};

		for (Direction direction : directionArray) {
			BlockState state = reader.getBlockState(pos.relative(direction));

			//If the block isn't solid, and isn't water, then it won't be able to hold water.
			if (!state.canOcclude() && !(state.getBlock() == Blocks.WATER)) {
				flag = false;
			}
		}
		return flag;
	}

	@Nullable
	private static BlockPos getValidPosition(LevelAccessor worldIn, BlockPos.MutableBlockPos pos, Block block) {
		while (pos.getY() > 1) {
			BlockState blockstate = worldIn.getBlockState(pos);
			if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.SCORSTONE.get()) || blockstate.is(ClinkerBlocks.SALT_BLOCK.get())) {
				return pos;
			}

			pos.move(Direction.DOWN);
		}
		return null;
	}
}