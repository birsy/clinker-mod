package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.common.block.AshLayerBlock;
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
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class CavePuddleFeature extends Feature<BlobReplacementConfig> {

	public CavePuddleFeature(Codec<BlobReplacementConfig> FeatureConfig) {
		super(FeatureConfig);
	}

	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlobReplacementConfig config) {
		Block block = ClinkerBlocks.BRIMSTONE.get();
		BlockPos blockpos = func_236329_a_(reader, pos.toMutable().clampAxisCoordinate(Direction.Axis.Y, 1, reader.getHeight() - 1), block);

		if (blockpos == null) {
			return false;
		} else {
			int i = config.func_242823_b().func_242259_a(rand);
			boolean flag = false;

			for (BlockPos blockpos1 : BlockPos.getProximitySortedBoxPositionsIterator(blockpos, i, i, i)) {
				BlockState blockstate = reader.getBlockState(blockpos1);
				if (blockstate.isIn(ClinkerBlocks.BRIMSTONE.get()) || blockstate.isIn(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.isIn(Blocks.STONE)) {
					//If the above it isn't solid, isn't water, and the block itself can hold water, then the puddle block will generate.
					if (!reader.getBlockState(blockpos1.up()).isSolid() && !(reader.getBlockState(blockpos1.up()).getBlock() == Blocks.WATER) && canHoldLiquid(reader, blockpos1, rand)) {
						reader.setBlockState(blockpos1, rand.nextBoolean() ? Blocks.WATER.getDefaultState() : ClinkerBlocks.BRIMSTONE_SLAB.get().getDefaultState().with(SlabBlock.WATERLOGGED, true), 1);
					}

					flag = true;
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
		//There is a 1 in 30 chance that it'll return false, which will allow some waterfalls to rarely form.
		return flag && !(rand.nextInt(30) == 0);
	}

	@Nullable
	private static BlockPos func_236329_a_(IWorld worldIn, BlockPos.Mutable pos, Block block) {
		while (pos.getY() > 1) {
			BlockState blockstate = worldIn.getBlockState(pos);
			if (blockstate.isIn(ClinkerBlocks.BRIMSTONE.get()) || blockstate.isIn(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.isIn(Blocks.STONE)) {
				return pos;
			}

			pos.move(Direction.DOWN);
		}
		return null;
	}
}