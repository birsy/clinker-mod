package birsy.clinker.common.world.feature.enviornment;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockBlobFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class BoulderFeature extends BlockBlobFeature {

	public BoulderFeature(Codec<BlockStateConfiguration> FeatureConfig) {
		super(FeatureConfig);
	}

	@Override
	public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateConfiguration config) {
		while(true) {
			label46: {
				if (pos.getY() > 3) {
					if (reader.isEmptyBlock(pos.below())) {
						break label46;
					}
				}

				if (pos.getY() <= 3) {
					return false;
				}

				for(int l = 0; l < 3; ++l) {
					int i = rand.nextInt(2);
					int j = rand.nextInt(2);
					int k = rand.nextInt(2);
					float f = (float)(i + j + k) * 0.333F + 0.5F;

					for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-i, -j, -k), pos.offset(i, j, k))) {
						if (blockpos.distSqr(pos) <= (double)(f * f)) {
							reader.setBlock(blockpos, config.state, 4);
						}
					}

					pos = pos.offset(-1 + rand.nextInt(2), -rand.nextInt(2), -1 + rand.nextInt(2));
				}

				return true;
			}

			pos = pos.below();
		}
	}
}