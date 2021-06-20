package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlobReplacementConfig;
import net.minecraft.world.gen.feature.Feature;

import javax.annotation.Nullable;
import java.util.Random;

public class CaveFloorFeature extends Feature<BlobReplacementConfig> {
    
    public CaveFloorFeature(Codec<BlobReplacementConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlobReplacementConfig config) {
        BlockPos blockpos = getValidYPos(reader, pos.toMutable().clampAxisCoordinate(Direction.Axis.Y, 1, reader.getHeight() - 1), ClinkerBlocks.BRIMSTONE.get());
        if (blockpos == null) {
            return false;
        } else {
            int i = config.getRadius().getSpread(rand);
            boolean flag = false;

            for (BlockPos blockpos1 : BlockPos.getProximitySortedBoxPositionsIterator(blockpos, i, i, i)) {
                int height = rand.nextInt(6);
                BlockPos.Mutable blockPos$mutable = blockpos1.toMutable();

                if (!reader.getBlockState(blockpos1.up()).isSolid()) {
                    for (int j = 0; j < height; j++) {
                        BlockState blockstate = reader.getBlockState(blockPos$mutable);
                        if (blockstate.matchesBlock(ClinkerBlocks.BRIMSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.matchesBlock(Blocks.STONE)) {
                            this.setBlockState(reader, blockPos$mutable, ClinkerBlocks.COBBLED_BRIMSTONE.get().getDefaultState());

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
    private static BlockPos getValidYPos(IWorld worldIn, BlockPos.Mutable pos, Block block) {
        while (pos.getY() > 1) {
            BlockState blockstate = worldIn.getBlockState(pos);
            if (blockstate.matchesBlock(ClinkerBlocks.BRIMSTONE.get()) || blockstate.matchesBlock(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.matchesBlock(Blocks.STONE)) {
                return pos;
            }

            pos.move(Direction.DOWN);
        }
        return null;
    }
}
