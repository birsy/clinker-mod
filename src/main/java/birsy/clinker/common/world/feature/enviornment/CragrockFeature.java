package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import javax.annotation.Nullable;
import java.util.Random;

public class CragrockFeature extends Feature<ColumnFeatureConfiguration> {
   private static final ImmutableList<Block> invalidBlocks = ImmutableList.of(Blocks.WATER, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.STONE_BRICKS, ClinkerBlocks.ASH.get(), Blocks.OAK_FENCE, Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE_GATE, Blocks.CHEST, Blocks.SPAWNER);

   public CragrockFeature(Codec<ColumnFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, ColumnFeatureConfiguration config) {
      int i = generator.getSeaLevel();
      if (!isValidColumn(reader, i, pos.mutable())) {
         return false;
      } else {
         int j = config.height().sample(rand);
         boolean flag = rand.nextFloat() < 0.9F;
         int k = Math.min(j, flag ? 5 : 8);
         int l = flag ? 50 : 15;
         boolean flag1 = false;

         for(BlockPos blockpos : BlockPos.randomBetweenClosed(rand, l, pos.getX() - k, pos.getY(), pos.getZ() - k, pos.getX() + k, pos.getY(), pos.getZ() + k)) {
            int i1 = j - blockpos.distManhattan(pos);
            if (i1 >= 0) {
               flag1 |= this.generateRocks(reader, i, blockpos, i1, config.reach().sample(rand), rand);
            }
         }

         return flag1;
      }
   }

   private boolean generateRocks(LevelAccessor reader, int seaLevelIn, BlockPos blockposIn, int manhattanDistance, int featureSpreadIn, Random rand) {
      boolean flag = false;

      for(BlockPos blockpos : BlockPos.betweenClosed(blockposIn.getX() - featureSpreadIn, blockposIn.getY(), blockposIn.getZ() - featureSpreadIn, blockposIn.getX() + featureSpreadIn, blockposIn.getY(), blockposIn.getZ() + featureSpreadIn)) {
         int i = blockpos.distManhattan(blockposIn);
         BlockPos blockpos1 = isValidPosition(reader, seaLevelIn, blockpos) ? iterateY(reader, seaLevelIn, blockpos.mutable(), i) : findAir(reader, blockpos.mutable(), i);
         if (blockpos1 != null) {
            int j = manhattanDistance - i / 2;

            for(BlockPos.MutableBlockPos blockpos$mutable = blockpos1.mutable(); j >= 0; --j) {
               if (isValidPosition(reader, seaLevelIn, blockpos$mutable)) {
                  if (rand.nextBoolean()) {
                     if (rand.nextBoolean()) {
                        this.setBlock(reader, blockpos$mutable, ClinkerBlocks.COBBLED_BRIMSTONE.get().defaultBlockState());
                     } else {
                        this.setBlock(reader, blockpos$mutable, ClinkerBlocks.BRIMSTONE_BRICKS.get().defaultBlockState());
                     }
                  } else {
                     this.setBlock(reader, blockpos$mutable, ClinkerBlocks.BRIMSTONE.get().defaultBlockState());
                  }
                  blockpos$mutable.move(Direction.UP);
                  flag = true;
               } else {
                  if (!reader.getBlockState(blockpos$mutable).is(Blocks.BASALT)) {
                     break;
                  }

                  blockpos$mutable.move(Direction.UP);
               }
            }
         }
      }

      return flag;
   }

   @Nullable
   private static BlockPos iterateY(LevelAccessor worldIn, int seaLevelIn, BlockPos.MutableBlockPos blockpos$mutable, int manhattanDistanceIn) {
      while(blockpos$mutable.getY() > 1 && manhattanDistanceIn > 0) {
         --manhattanDistanceIn;
         if (isValidColumn(worldIn, seaLevelIn, blockpos$mutable)) {
            return blockpos$mutable;
         }

         blockpos$mutable.move(Direction.DOWN);
      }

      return null;
   }

   private static boolean isValidColumn(LevelAccessor world, int seaLevel, BlockPos.MutableBlockPos blockpos$mutable) {
      if (!isValidPosition(world, seaLevel, blockpos$mutable)) {
         return false;
      } else {
         BlockState blockstate = world.getBlockState(blockpos$mutable.move(Direction.DOWN));
         blockpos$mutable.move(Direction.UP);
         return !blockstate.isAir() && !invalidBlocks.contains(blockstate.getBlock());
      }
   }

   @Nullable
   private static BlockPos findAir(LevelAccessor world, BlockPos.MutableBlockPos blockpos$mutable, int manhattenDistanceIn) {
      while(blockpos$mutable.getY() < world.getMaxBuildHeight() && manhattenDistanceIn > 0) {
         --manhattenDistanceIn;
         BlockState blockstate = world.getBlockState(blockpos$mutable);
         if (invalidBlocks.contains(blockstate.getBlock())) {
            return null;
         }

         if (blockstate.isAir()) {
            return blockpos$mutable;
         }

         blockpos$mutable.move(Direction.UP);
      }

      return null;
   }

   private static boolean isValidPosition(LevelAccessor world, int seaLevelIn_, BlockPos blockPos) {
      BlockState blockstate = world.getBlockState(blockPos);
      return blockstate.isAir() || blockstate.is(Blocks.WATER) && blockPos.getY() <= seaLevelIn_;
   }
}
