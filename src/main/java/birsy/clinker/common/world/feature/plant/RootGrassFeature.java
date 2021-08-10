package birsy.clinker.common.world.feature.plant;

import birsy.clinker.common.block.RootGrassBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RootGrassFeature extends Feature<NoneFeatureConfiguration> {
    public RootGrassFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
        BlockState rooted_ash = ClinkerBlocks.ROOTED_ASH.get().defaultBlockState();
        BlockState root_grass = ClinkerBlocks.ROOT_GRASS.get().defaultBlockState();

        int radius = rand.nextInt(16) + 2;
        int tries = 3;
        AtomicBoolean bool = new AtomicBoolean(false);

        for (int i = 0; i < tries; i++) {
            BlockPos.betweenClosedStream(pos.offset(radius, radius / 2, radius), pos.offset(-radius, -radius / 2, -radius)).forEach((blockPos) -> {
                if (rand.nextInt(3) == 0) {
                    BlockState blockState = reader.getBlockState(blockPos.below());
                    BlockState groundState = reader.getBlockState(blockPos.below());
                    ArrayList<BlockState> neighboringStates = getNeighboringStates(reader, blockPos);

                    boolean isValidBlock = RootGrassBlock.isValidGround(groundState) && blockState.isAir();
                    /**
                    for (BlockState neighboringState : neighboringStates) {
                        if (!neighboringState.isSolid() && !neighboringState.matchesBlock(root_grass.getBlock())) {
                            isValidBlock = false;
                        }
                    }
                     */

                    if (isValidBlock) {
                        this.setBlock(reader, blockPos, rooted_ash);
                        bool.set(true);
                    }

                    if (groundState.is(ClinkerBlocks.ASH.get()) && blockState.is(root_grass.getBlock())) {
                        this.setBlock(reader, blockPos.below(), rooted_ash);
                        bool.set(true);
                    }
                }
            });
        }

        return bool.get();
    }

    private ArrayList<BlockState> getNeighboringStates(LevelAccessor world, BlockPos pos) {
        ArrayList<BlockState> surroundingStates = new ArrayList<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            surroundingStates.add(world.getBlockState(pos.relative(direction)));
            surroundingStates.add(world.getBlockState(pos.relative(direction.getClockWise())));
        }

        return surroundingStates;
    }
}
