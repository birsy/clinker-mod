package birsy.clinker.common.world.feature.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

public class BrambleBlobFeature extends Feature<NoneFeatureConfiguration> {
   public BrambleBlobFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
      if (!reader.isEmptyBlock(pos)) {
         return false;
      } else {
         BlockState blockstate = reader.getBlockState(pos.above());
         if (!blockstate.is(ClinkerBlocks.BRIMSTONE.get()) && !blockstate.is(ClinkerBlocks.COBBLED_BRIMSTONE.get()) && !blockstate.is(ClinkerBlocks.SILT_BLOCK.get())) {
            return false;
         } else {
            this.setBlock(reader, pos, ClinkerBlocks.BRAMBLE.get().defaultBlockState());

            for(int i = 0; i < 500; ++i) {
               BlockPos blockpos = pos.offset(rand.nextInt(5) - rand.nextInt(5),rand.nextInt(2) - rand.nextInt(4), rand.nextInt(5) - rand.nextInt(5));
               if (reader.getBlockState(blockpos).isAir(reader, blockpos)) {
                  int j = 0;
                  boolean isNearSolid = false;

                  for(Direction direction : Direction.values()) {
                     if (reader.getBlockState(blockpos.relative(direction)).is(ClinkerBlocks.BRAMBLE.get())) {
                        ++j;
                     }

                     if (j > 1) {
                        break;
                     }
                  }

                  for (int x = -2; x < 2; x++) {
                     for (int y = -2; y < 2; y++) {
                        for (int z = -2; z < 2; z++) {
                           if (reader.getBlockState(pos.offset(x, y, z)).canOcclude()) {
                              isNearSolid = true;
                           }
                        }
                     }
                  }

                  if (j == 1 && isNearSolid) {
                     this.setBlock(reader, blockpos, ClinkerBlocks.BRAMBLE.get().defaultBlockState());
                  }
               }
            }

            return true;
         }
      }
   }
}
