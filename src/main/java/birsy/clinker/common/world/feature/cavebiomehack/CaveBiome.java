package birsy.clinker.common.world.feature.cavebiomehack;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import javax.annotation.Nullable;
import java.util.Random;

public class CaveBiome extends Feature<ReplaceSphereConfiguration> {

	public CaveBiome(Codec<ReplaceSphereConfiguration> FeatureConfig) {
		super(FeatureConfig);
	}

	public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, ReplaceSphereConfiguration config) {
		Block block = ClinkerBlocks.BRIMSTONE.get();
		BlockPos blockpos = findTarget(reader, pos.mutable().clamp(Direction.Axis.Y, 1, reader.getMaxBuildHeight() - 1), block);

		if (blockpos == null) {
			return false;
		} else {
			int i = config.radius().sample(rand);
			boolean flag = false;

			for (BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, i, i, i)) {
				BlockState blockstate = reader.getBlockState(blockpos1);
				if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.is(Blocks.STONE)) {
					//If the above it isn't solid, isn't water, and the block itself can hold water, then the puddle block will generate.
					if (!reader.getBlockState(blockpos1.above()).canOcclude() && !(reader.getBlockState(blockpos1.above()).getBlock() == Blocks.WATER) && canHoldLiquid(reader, blockpos1, rand)) {
						reader.setBlock(blockpos1, rand.nextBoolean() ? Blocks.WATER.defaultBlockState() : ClinkerBlocks.BRIMSTONE_SLAB.get().defaultBlockState().setValue(SlabBlock.WATERLOGGED, true), 1);
					}

					flag = true;
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
		//There is a 1 in 30 chance that it'll return false, which will allow some waterfalls to rarely form.
		return flag && !(rand.nextInt(30) == 0);
	}

	@Nullable
	private static BlockPos findTarget(LevelAccessor worldIn, BlockPos.MutableBlockPos pos, Block block) {
		while (pos.getY() > 1) {
			BlockState blockstate = worldIn.getBlockState(pos);
			if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.is(Blocks.STONE)) {
				return pos;
			}

			pos.move(Direction.DOWN);
		}
		return null;
	}
}