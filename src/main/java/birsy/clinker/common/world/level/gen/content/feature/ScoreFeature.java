package birsy.clinker.common.world.level.gen.content.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

// carves striation lines into rocks and such
public class ScoreFeature extends Feature<NoneFeatureConfiguration> {
    public ScoreFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        int radius = 16, sqrRadius = radius * radius;
        List<BlockPos> airToPlace = new ArrayList<>(sqrRadius);
        BlockPos.MutableBlockPos cursor = origin.mutable();
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                double dist = Mth.lengthSquared(x, z);
                if (dist > sqrRadius) continue;


                cursor.set(origin.getX() + x, origin.getY(), origin.getZ() + z);
                BlockState state = level.getBlockState(cursor);

                if (!state.isSolid()) continue;

                int nonSolidNeighbors = 0;
                int requiredSolidNeighbors = (int) Math.round(Mth.map(Math.sqrt(dist) / radius, 0.5, 1.0, 1, 3));

                boolean hasEnoughNonSolidNeighbors = false;

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    cursor.move(direction, 1);
                    BlockState offsetState = level.getBlockState(cursor);
                    cursor.move(direction, -1);

                    if (!offsetState.isSolid()) {
                        nonSolidNeighbors++;
                        if (nonSolidNeighbors >= requiredSolidNeighbors) {
                            hasEnoughNonSolidNeighbors = true;
                            break;
                        }
                    }
                }

                if (!hasEnoughNonSolidNeighbors) continue;

                airToPlace.add(cursor.immutable());
            }
        }

        for (BlockPos pos : airToPlace) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);

        return true;
    }
}
