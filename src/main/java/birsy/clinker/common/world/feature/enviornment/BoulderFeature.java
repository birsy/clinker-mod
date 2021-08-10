package birsy.clinker.common.world.feature.enviornment;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockBlobFeature;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class BoulderFeature extends BlockBlobFeature {

	public BoulderFeature(Codec<BlockStateFeatureConfig> FeatureConfig) {
		super(FeatureConfig);
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config) {
		while(true) {
			label46: {
				if (pos.getY() > 3) {
					if (reader.isAirBlock(pos.down())) {
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

					for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-i, -j, -k), pos.add(i, j, k))) {
						if (blockpos.distanceSq(pos) <= (double)(f * f)) {
							reader.setBlockState(blockpos, config.state, 4);
						}
					}

					pos = pos.add(-1 + rand.nextInt(2), -rand.nextInt(2), -1 + rand.nextInt(2));
				}

				return true;
			}

			pos = pos.down();
		}
	}
}