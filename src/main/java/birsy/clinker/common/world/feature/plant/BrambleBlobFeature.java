package birsy.clinker.common.world.feature.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class BrambleBlobFeature extends Feature<NoFeatureConfig> {
   public BrambleBlobFeature(Codec<NoFeatureConfig> codec) {
      super(codec);
   }

   public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      if (!reader.isAirBlock(pos)) {
         return false;
      } else {
         BlockState blockstate = reader.getBlockState(pos.up());
         if (!blockstate.matchesBlock(ClinkerBlocks.BRIMSTONE.get()) && !blockstate.matchesBlock(ClinkerBlocks.COBBLED_BRIMSTONE.get()) && !blockstate.matchesBlock(ClinkerBlocks.SILT_BLOCK.get())) {
            return false;
         } else {
            this.setBlockState(reader, pos, ClinkerBlocks.BRAMBLE.get().getDefaultState());

            for(int i = 0; i < 500; ++i) {
               BlockPos blockpos = pos.add(rand.nextInt(5) - rand.nextInt(5),rand.nextInt(2) - rand.nextInt(4), rand.nextInt(5) - rand.nextInt(5));
               if (reader.getBlockState(blockpos).isAir(reader, blockpos)) {
                  int j = 0;
                  boolean isNearSolid = false;

                  for(Direction direction : Direction.values()) {
                     if (reader.getBlockState(blockpos.offset(direction)).matchesBlock(ClinkerBlocks.BRAMBLE.get())) {
                        ++j;
                     }

                     if (j > 1) {
                        break;
                     }
                  }

                  for (int x = -2; x < 2; x++) {
                     for (int y = -2; y < 2; y++) {
                        for (int z = -2; z < 2; z++) {
                           if (reader.getBlockState(pos.add(x, y, z)).isSolid()) {
                              isNearSolid = true;
                           }
                        }
                     }
                  }

                  if (j == 1 && isNearSolid) {
                     this.setBlockState(reader, blockpos, ClinkerBlocks.BRAMBLE.get().getDefaultState());
                  }
               }
            }

            return true;
         }
      }
   }
}
