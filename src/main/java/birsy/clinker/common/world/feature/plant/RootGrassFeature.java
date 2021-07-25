package birsy.clinker.common.world.feature.plant;

import birsy.clinker.common.block.RootGrassBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RootGrassFeature extends Feature<NoFeatureConfig> {
    public RootGrassFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockState rooted_ash = ClinkerBlocks.ROOTED_ASH.get().getDefaultState();
        BlockState root_grass = ClinkerBlocks.ROOT_GRASS.get().getDefaultState();

        int radius = rand.nextInt(16) + 2;
        int tries = 3;
        AtomicBoolean bool = new AtomicBoolean(false);

        for (int i = 0; i < tries; i++) {
            BlockPos.getAllInBox(pos.add(radius, radius / 2, radius), pos.add(-radius, -radius / 2, -radius)).forEach((blockPos) -> {
                if (rand.nextInt(3) == 0) {
                    BlockState blockState = reader.getBlockState(blockPos.down());
                    BlockState groundState = reader.getBlockState(blockPos.down());
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
                        this.setBlockState(reader, blockPos, rooted_ash);
                        bool.set(true);
                    }

                    if (groundState.matchesBlock(ClinkerBlocks.ASH.get()) && blockState.matchesBlock(root_grass.getBlock())) {
                        this.setBlockState(reader, blockPos.down(), rooted_ash);
                        bool.set(true);
                    }
                }
            });
        }

        return bool.get();
    }

    private ArrayList<BlockState> getNeighboringStates(IWorld world, BlockPos pos) {
        ArrayList<BlockState> surroundingStates = new ArrayList<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            surroundingStates.add(world.getBlockState(pos.offset(direction)));
            surroundingStates.add(world.getBlockState(pos.offset(direction.rotateY())));
        }

        return surroundingStates;
    }
}
