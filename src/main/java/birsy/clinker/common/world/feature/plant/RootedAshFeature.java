package birsy.clinker.common.world.feature.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RootedAshFeature extends Feature<NoFeatureConfig> {
    public RootedAshFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        Block block = ClinkerBlocks.ROOTED_ASH.get();
        //BlockPos blockpos = getValidPosition (reader, pos.toMutable().clampAxisCoordinate(Direction.Axis.Y, 1, reader.getHeight() - 1));

        int radius = rand.nextInt(16) + 2;
        AtomicBoolean flag = new AtomicBoolean(false);

        BlockPos.getAllInBox(pos.add(radius, radius, radius), pos.add(-radius, -radius, -radius)).forEach((blockPos) -> {
            if (blockPos.distanceSq(pos) < radius) {
                BlockState blockstate = reader.getBlockState(blockPos);
                if (blockstate.isSolid()) {
                    if (blockstate.matchesBlock(ClinkerBlocks.ASH.get())) {
                        this.setBlockState(reader, blockPos, ClinkerBlocks.ROOTED_ASH.get().getDefaultState());
                    } else if (blockstate.matchesBlock(ClinkerBlocks.PACKED_ASH.get()) || blockstate.matchesBlock(ClinkerBlocks.ROCKY_PACKED_ASH.get())) {
                        this.setBlockState(reader, blockPos, ClinkerBlocks.ROOTED_PACKED_ASH.get().getDefaultState());
                    }

                    if (rand.nextInt(4) == 0) {
                        this.setBlockState(reader, blockPos, ClinkerBlocks.ROOTSTALK.get().getDefaultState());
                    }

                    if (rand.nextInt(8) == 0) {
                        this.setBlockState(reader, blockPos.up(), ClinkerBlocks.ROOT_GRASS.get().getDefaultState());
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
    private static BlockPos getValidPosition (ISeedReader reader, BlockPos.Mutable pos) {
        while(pos.getY() > 1) {
            BlockState blockstate = reader.getBlockState(pos);
            if (blockstate.isSolid()) {
                return pos;
            }

            pos.move(Direction.DOWN);
        }

        return null;
    }

    private void createRootstalkBlob (ISeedReader reader, Random rand, BlockPos pos) {
        for (int i = 0; i < rand.nextInt(2); i++) {
            Vector3d blobVecPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f), MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f), MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f));
            BlockPos.Mutable blobPos = new BlockPos(blobVecPos.getX(), blobVecPos.getY(), blobVecPos.getZ()).toMutable();
            float radius = MathUtils.getRandomFloatBetween(rand, -1.5f, 1.5f);

            for(int j = 0; j < 20; j++) {
                if (blobPos.getY() <= 0) {
                    break;
                } else if (!reader.getBlockState(blobPos.down()).isSolid()) {
                    blobPos.setPos(blobPos.down());
                    blobVecPos.add(0, -1, 0);
                } else {
                    break;
                }
            }

            blobPos.setPos(blobPos.down(2));
            blobVecPos.add(0, -2, 0);

            BlockPos.getAllInBox(blobPos.add(radius, radius, radius), blobPos.add(-radius, -radius, -radius)).forEach((blockPos) -> {
                if (blockPos.withinDistance(blobVecPos, radius)) {
                    this.setBlockState(reader, blockPos, ClinkerBlocks.ROOTSTALK.get().getDefaultState());
                    if (rand.nextInt(2) == 0 && !reader.getBlockState(pos.up()).isSolid()) {
                        this.setBlockState(reader, blockPos.up(), ClinkerBlocks.ROOT_GRASS.get().getDefaultState());
                    }
                }
            });
        }
    }
}
