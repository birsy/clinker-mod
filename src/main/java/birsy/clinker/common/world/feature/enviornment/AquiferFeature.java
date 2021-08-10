package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BlobReplacementConfig;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AquiferFeature extends Feature<BlockStateConfiguration> {
	private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

	public AquiferFeature(Codec<BlockStateConfiguration> FeatureConfig) {
		super(FeatureConfig);
	}

	public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateConfiguration config) {
		int radius = rand.nextInt(13) + 3;

		//Generates a sphere of contained water...
		if (pos.getY() < radius) {
			return false;
		} else {
			BlockPos.MutableBlockPos blockPos = pos.mutable();

			//Loops through it once to place the water.
			for (int x = 0; x < radius * 2; x++) {
				for (int y = 0; y < radius * 2; y++) {
					for (int z = 0; z < radius * 2; z++) {
						blockPos.set(pos.getX() + (x - radius), pos.getY() + (y - radius), pos.getZ() + (z - radius));

						if (!reader.getBlockState(blockPos).canOcclude()) {
							if (blockPos.immutable().distSqr(pos.getX(), pos.getY(), pos.getZ(), true) <= radius) {
								reader.setBlock(blockPos, config.state, 1);
							}
						}
					}
				}
			}

			/**
			//And once again to generate the shell.
			for (int x = 0; x < radius * 2; x++) {
				for (int y = 0; y < radius * 2; y++) {
					for (int z = 0; z < radius * 2; z++) {
						blockPos.setPos(pos.getX() + (x - radius), pos.getY() + (y - radius), pos.getZ() + (z - radius));

						//If there is a fluid there, and the block can't hold liquid, then it becomes brimstone.
						if (!(reader.getBlockState(blockPos) == Blocks.WATER.getDefaultState())) {
							if (!canHoldLiquid(reader, blockPos)) {
								reader.setBlockState(blockPos, ClinkerBlocks.BRIMSTONE.get().getDefaultState(), 1);
							}
						}
					}
				}
			}
			 */

			return true;
		}
	}

	private boolean canHoldLiquid(WorldGenLevel reader, BlockPos pos) {
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
}