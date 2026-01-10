package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class FluidCrackFeature extends Feature<FluidCrackFeature.FluidCrackConfiguration> {
    private static final Direction[] NEIGHBOR_OFFSETS = {
            Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };
    public FluidCrackFeature(Codec<FluidCrackConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FluidCrackConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockState fluid = context.config().state();
        int minSearchDist = Math.max(context.config().minSearchDistance().sample(random), 1),
            maxSearchDist = context.config().maxSearchDistance().sample(random);

        // must start from the fluid
        if (level.getBlockState(origin) != fluid) return false;

        // search for an opening below
        boolean success = false;
        int xOffset = 0, zOffset = 0;
        int distanceTraversedThroughTerrain = 0;
        int firstSolidIndex = -1;
        BlockPos.MutableBlockPos cursor = origin.mutable();
        List<BlockPos> positions = new ArrayList<>(24);
        SEARCH:
        while (distanceTraversedThroughTerrain < maxSearchDist && cursor.getY() > level.getMinBuildHeight()+1) {
            if (random.nextInt(4) == 0) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                int newXOffset = xOffset + direction.getStepX(),
                    newZOffset = zOffset + direction.getStepZ();
                if (Math.abs(newXOffset) >= 16 || Math.abs(newZOffset) >= 16) {
                    direction = direction.getOpposite();
                }
                cursor.move(direction);
            } else {
                cursor.move(Direction.DOWN);
            }
            positions.add(cursor.immutable());

            BlockState state = level.getBlockState(cursor);
            if (state.isSolid()) {
                distanceTraversedThroughTerrain++;
                if (firstSolidIndex == -1) firstSolidIndex = positions.size() - 1;
            }
            if (state.isAir()) {
                success = true;
                break SEARCH;
            }
            for (Direction dir : NEIGHBOR_OFFSETS) {
                cursor.move(dir);
                boolean isAir = level.getBlockState(cursor).isAir();
                cursor.move(dir.getOpposite());
                if (isAir) {
                    success = true;
                    break SEARCH;
                }
            }
        }
        // if we didn't find air, don't place anything
        if (!success) return false;
        // if we found air too quickly, don't place anything
        if (distanceTraversedThroughTerrain < minSearchDist) return false;

        // place all the blocks
        for (int i = firstSolidIndex; i < positions.size(); i++) {
            BlockPos position = positions.get(i);
            level.setBlock(position, Blocks.AIR.defaultBlockState(), 2);
        }
        level.setBlock(positions.get(firstSolidIndex), fluid, 2);
        level.scheduleTick(positions.get(firstSolidIndex), fluid.getFluidState().getType(), 0);

        // if the block above us is air, don't place
        if (!level.getBlockState(origin.above()).isSolid())
            return false;

        return true;
    }

    public record FluidCrackConfiguration(BlockState state, IntProvider minSearchDistance, IntProvider maxSearchDistance)
            implements FeatureConfiguration {
        public static final Codec<FluidCrackConfiguration> CODEC = RecordCodecBuilder.create(
                codec -> codec.group(
                                BlockState.CODEC.fieldOf("state").forGetter(config -> config.state),
                                IntProvider.CODEC.fieldOf("minimum_search_distance").forGetter(config -> config.minSearchDistance),
                                IntProvider.CODEC.fieldOf("maximum_search_distance").forGetter(config -> config.maxSearchDistance)
                        ).apply(codec, FluidCrackConfiguration::new)
        );
    }
}
