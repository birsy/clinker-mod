package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
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

public class LayeredBlobFeature extends Feature<ReplaceSphereConfiguration> {

    public LayeredBlobFeature(Codec<ReplaceSphereConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, ReplaceSphereConfiguration config) {
        BlockPos blockpos = getValidYPos(reader, pos.mutable().clamp(Direction.Axis.Y, 1, reader.getMaxBuildHeight() - 1));
        if (blockpos == null) {
            return false;
        } else {
            int i = config.radius().sample(rand);
            boolean flag = false;
            for (BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, i, i, i)) {
                BlockState blockstate = reader.getBlockState(blockpos1);
                if (!(blockpos1.distSqr(pos) < i)) {
                    if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.is(Blocks.STONE)) {
                        this.setBlock(reader, blockpos1, ClinkerBlocks.SHALE.get().defaultBlockState());
                        flag = true;
                    }
                }
            }

            for (BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, i, i, i)) {
                BlockState blockstate = reader.getBlockState(blockpos1);
                //If a block isn't touching a base stone block, and is touching shale, then it turns to packed ash to created a cool layered effect.
                if (!isTouchingBlock(reader, blockpos1, ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), true) &&
                    !isTouchingBlock(reader, blockpos1, Blocks.STONE.defaultBlockState(), true) &&
                    !isTouchingBlock(reader, blockpos1, ClinkerBlocks.COBBLED_BRIMSTONE.get().defaultBlockState(), true) &&
                    isTouchingBlock(reader, blockpos1, ClinkerBlocks.SHALE.get().defaultBlockState(), false)) {
                    this.setBlock(reader, blockpos1, ClinkerBlocks.PACKED_ASH.get().defaultBlockState());
                }
            }

            return flag;
        }
    }
    @Nullable
    private static BlockPos getValidYPos(LevelAccessor worldIn, BlockPos.MutableBlockPos pos) {
        while (pos.getY() > 1) {
            BlockState blockstate = worldIn.getBlockState(pos);
            if (blockstate.is(ClinkerBlocks.BRIMSTONE.get()) || blockstate.is(ClinkerBlocks.COBBLED_BRIMSTONE.get()) || blockstate.is(Blocks.STONE)) {
                return pos;
            }

            pos.move(Direction.DOWN);
        }
        return null;
    }

    private boolean isTouchingBlock(LevelAccessor worldIn, BlockPos pos, BlockState block, boolean selfReflect) {
        for (Direction direction : Direction.values()) {
            if (worldIn.getBlockState(pos.relative(direction)) == block) {
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
