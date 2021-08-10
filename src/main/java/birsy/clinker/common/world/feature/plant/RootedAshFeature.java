package birsy.clinker.common.world.feature.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RootedAshFeature extends Feature<NoneFeatureConfiguration> {
    public RootedAshFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
        Block block = ClinkerBlocks.ROOTED_ASH.get();
        //BlockPos blockpos = getValidPosition (reader, pos.toMutable().clampAxisCoordinate(Direction.Axis.Y, 1, reader.getHeight() - 1));

        int radius = rand.nextInt(16) + 2;
        AtomicBoolean flag = new AtomicBoolean(false);

        BlockPos.betweenClosedStream(pos.offset(radius, radius, radius), pos.offset(-radius, -radius, -radius)).forEach((blockPos) -> {
            if (blockPos.distSqr(pos) < radius) {
                BlockState blockstate = reader.getBlockState(blockPos);
                if (blockstate.canOcclude()) {
                    if (blockstate.is(ClinkerBlocks.ASH.get())) {
                        this.setBlock(reader, blockPos, ClinkerBlocks.ROOTED_ASH.get().defaultBlockState());
                    } else if (blockstate.is(ClinkerBlocks.PACKED_ASH.get()) || blockstate.is(ClinkerBlocks.ROCKY_PACKED_ASH.get())) {
                        this.setBlock(reader, blockPos, ClinkerBlocks.ROOTED_PACKED_ASH.get().defaultBlockState());
                    }

                    if (rand.nextInt(4) == 0) {
                        this.setBlock(reader, blockPos, ClinkerBlocks.ROOTSTALK.get().defaultBlockState());
                    }

                    if (rand.nextInt(8) == 0) {
                        this.setBlock(reader, blockPos.above(), ClinkerBlocks.ROOT_GRASS.get().defaultBlockState());
                    }

                    if (rand.nextInt(12) == 0) {
                        this.createRootstalkBlob(reader, rand, pos);
                    }

                    flag.set(true);
                }
            }
        });
        return flag.get();
    }

    @Nullable
    private static BlockPos getValidPosition (WorldGenLevel reader, BlockPos.MutableBlockPos pos) {
        while(pos.getY() > 1) {
            BlockState blockstate = reader.getBlockState(pos);
            if (blockstate.canOcclude()) {
                return pos;
            }

            pos.move(Direction.DOWN);
        }

        return null;
    }

    private void createRootstalkBlob (WorldGenLevel reader, Random rand, BlockPos pos) {
        for (int i = 0; i < rand.nextInt(2); i++) {
            Vec3 blobVecPos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f), MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f), MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f));
            BlockPos.MutableBlockPos blobPos = new BlockPos(blobVecPos.x(), blobVecPos.y(), blobVecPos.z()).mutable();
            float radius = MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f);

            for(int j = 0; j < 20; j++) {
                if (blobPos.getY() <= 0) {
                    break;
                } else if (!reader.getBlockState(blobPos.below()).canOcclude()) {
                    blobPos.set(blobPos.below());
                    blobVecPos.add(0, -1, 0);
                } else {
                    break;
                }
            }

            blobPos.set(blobPos.below(2));
            blobVecPos.add(0, -2, 0);

            BlockPos.betweenClosedStream(blobPos.offset(radius, radius, radius), blobPos.offset(-radius, -radius, -radius)).forEach((blockPos) -> {
                if (blockPos.closerThan(blobVecPos, radius)) {
                    this.setBlock(reader, blockPos, ClinkerBlocks.ROOTSTALK.get().defaultBlockState());
                    if (rand.nextInt(2) == 0 && !reader.getBlockState(pos.above()).canOcclude()) {
                        this.setBlock(reader, blockPos.above(), ClinkerBlocks.ROOT_GRASS.get().defaultBlockState());
                    }
                }
            });
        }
    }
}
