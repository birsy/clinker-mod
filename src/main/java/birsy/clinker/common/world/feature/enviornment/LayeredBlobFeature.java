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

public class LayeredBlobFeature extends Feature<BlobReplacementConfig> {

    public LayeredBlobFeature(Codec<BlobReplacementConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlobReplacementConfig config) {
        BlockPos blockpos = getValidYPos(reader, pos.toMutable().clampAxisCoordinate(Direction.Axis.Y, 1, reader.getHeight() - 1));
        if (blockpos == null) {
            return false;
        } else {
            int i = config.func_242823_b().func_242259_a(rand);
            boolean flag = false;
            for (BlockPos blockpos1 : BlockPos.getProximitySortedBoxPositionsIterator(blockpos, i, i, i)) {
                BlockState blockstate = reader.getBlockState(blockpos1);
                if (!(blockpos1.distanceSq(pos) < i)) {
                    if (blockstate.isIn(ClinkerBlocks.BRIMSTONE.get()) || blockstate.isIn(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.isIn(Blocks.STONE)) {
                        this.setBlockState(reader, blockpos1, ClinkerBlocks.SHALE.get().getDefaultState());
                        flag = true;
                    }
                }
            }

            for (BlockPos blockpos1 : BlockPos.getProximitySortedBoxPositionsIterator(blockpos, i, i, i)) {
                BlockState blockstate = reader.getBlockState(blockpos1);
                //If a block isn't touching a base stone block, and is touching shale, then it turns to packed ash to created a cool layered effect.
                if (!isTouchingBlock(reader, blockpos1, ClinkerBlocks.BRIMSTONE.get().getDefaultState(), true) &&
                    !isTouchingBlock(reader, blockpos1, Blocks.STONE.getDefaultState(), true) &&
                    !isTouchingBlock(reader, blockpos1, ClinkerBlocks.COBBLED_BRIMSTONE.get().getDefaultState(), true) &&
                    isTouchingBlock(reader, blockpos1, ClinkerBlocks.SHALE.get().getDefaultState(), false)) {
                    this.setBlockState(reader, blockpos1, ClinkerBlocks.PACKED_ASH.get().getDefaultState());
                }
            }

            return flag;
        }
    }
    @Nullable
    private static BlockPos getValidYPos(IWorld worldIn, BlockPos.Mutable pos) {
        while (pos.getY() > 1) {
            BlockState blockstate = worldIn.getBlockState(pos);
            if (blockstate.isIn(ClinkerBlocks.BRIMSTONE.get()) || blockstate.isIn(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.isIn(Blocks.STONE)) {
                return pos;
            }

            pos.move(Direction.DOWN);
        }
        return null;
    }

    private boolean isTouchingBlock(IWorld worldIn, BlockPos pos, BlockState block, boolean selfReflect) {
        for (Direction direction : Direction.values()) {
            if (worldIn.getBlockState(pos.offset(direction)) == block) {
                if (selfReflect) {
                    if (worldIn.getBlockState(pos) != block) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }

        return false;
    }
}
