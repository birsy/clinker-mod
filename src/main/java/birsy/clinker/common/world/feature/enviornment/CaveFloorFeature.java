package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import javax.annotation.Nullable;
import java.util.Random;

public class CaveFloorFeature extends Feature<ReplaceSphereConfiguration> {
    
    public CaveFloorFeature(Codec<ReplaceSphereConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, ReplaceSphereConfiguration config) {
        BlockPos blockpos = getValidYPos(reader, pos.mutable().clamp(Direction.Axis.Y, 1, reader.getMaxBuildHeight() - 1), ClinkerBlocks.BRIMSTONE.get());
        if (blockpos == null) {
            return false;
        } else {
            int i = config.radius().sample(rand);
            boolean flag = false;

            for (BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, i, i, i)) {
                int height = rand.nextInt(6);
                BlockPos.MutableBlockPos blockPos$mutable = blockpos1.mutable();

                if (!reader.getBlockState(blockpos1.above()).canOcclude()) {
                    for (int j = 0; j < height; j++) {
                        BlockState blockstate = reader.getBlockState(blockPos$mutable);
                        if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.is(Blocks.STONE)) {
                            this.setBlock(reader, blockPos$mutable, ClinkerBlocks.COBBLED_BRIMSTONE.get().defaultBlockState());

                            blockPos$mutable.setY(blockpos1.getY() - j);
                            flag = true;
                        } else {
                            break;
                        }
                    }
                }
            }
            return flag;
        }
    }

    @Nullable
    private static BlockPos getValidYPos(LevelAccessor worldIn, BlockPos.MutableBlockPos pos, Block block) {
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
