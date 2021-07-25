package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlobReplacementConfig;
import net.minecraft.world.gen.feature.Feature;

import javax.annotation.Nullable;
import java.util.Random;

public class ShorelinePuddleFeature extends Feature<BlobReplacementConfig> {

	public ShorelinePuddleFeature(Codec<BlobReplacementConfig> FeatureConfig) {
		super(FeatureConfig);
	}

	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlobReplacementConfig config) {
		Block block = ClinkerBlocks.BRIMSTONE.get();
		BlockPos blockpos = getValidPosition(reader, pos.toMutable().clampAxisCoordinate(Direction.Axis.Y, 1, reader.getHeight() - 1), block);

		if (blockpos == null) {
			return false;
		} else {
			int i = config.getRadius().getSpread(rand);
			boolean flag = false;

			for (BlockPos blockpos1 : BlockPos.getProximitySortedBoxPositionsIterator(blockpos, i, i, i)) {
				if (blockpos1.withinDistance(blockpos, i)) {
					BlockState blockstate = reader.getBlockState(blockpos1);
					if (blockstate.matchesBlock(ClinkerBlocks.SCORSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.SALT_BLOCK.get())) {
						if (!reader.getBlockState(blockpos1.up()).isSolid() && !(reader.getBlockState(blockpos1.up()).getBlock() == Blocks.WATER) && canHoldLiquid(reader, blockpos1, rand)) {
							this.setBlockState(reader, blockpos1, Blocks.WATER.getDefaultState());
						}

						flag = true;
					}
				}
			}

			return flag;
		}
	}

	private boolean canHoldLiquid(ISeedReader reader, BlockPos pos, Random rand) {
		boolean flag = true;
		Direction[] directionArray = {Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.DOWN};

		for (Direction direction : directionArray) {
			BlockState state = reader.getBlockState(pos.offset(direction));

			//If the block isn't solid, and isn't water, then it won't be able to hold water.
			if (!state.isSolid() && !(state.getBlock() == Blocks.WATER)) {
				flag = false;
			}
		}
		return flag;
	}

	@Nullable
	private static BlockPos getValidPosition(IWorld worldIn, BlockPos.Mutable pos, Block block) {
		while (pos.getY() > 1) {
			BlockState blockstate = worldIn.getBlockState(pos);
			if (blockstate.matchesBlock(ClinkerBlocks.BRIMSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.SCORSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.SALT_BLOCK.get())) {
				return pos;
			}

			pos.move(Direction.DOWN);
		}
		return null;
	}
}