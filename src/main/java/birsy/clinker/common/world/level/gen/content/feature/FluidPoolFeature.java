package birsy.clinker.common.world.level.gen.content.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.*;

public class FluidPoolFeature extends Feature<FluidPoolFeature.PoolConfiguration> {
    public FluidPoolFeature(Codec<PoolConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<PoolConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        PoolConfiguration config = context.config();

        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed()));
        NormalNoise noise = NormalNoise.create(worldgenrandom, -4, 1.0);

        int noiseIntensity = 7;
        int radius = config.radius.sample(random);
        int generationRadius = radius + noiseIntensity;

        BlockPos.MutableBlockPos mPos = origin.mutable(), borderCheckPos = origin.mutable();

        Collection<BlockPos> positionsToPlace = new ArrayList<>();
        Collection<BlockPos> positionsToUpdate = new ArrayList<>();

        BlockState stateToPlace = config.stateProvider().getState(random, origin);

        // for every column
        for (int x = -generationRadius; x <= generationRadius; x++) {
            NEXT_BLOCK:
            for (int z = -generationRadius; z < generationRadius; z++) {
                mPos.set(origin.getX() + x, origin.getY(), origin.getZ() + z);

                boolean foundSurface = false;
                BlockState stateAtPos = level.getBlockState(mPos);
                if (stateAtPos == stateToPlace) continue NEXT_BLOCK;

                if (stateAtPos.isSolid()) {
                    // search upwards if we're a solid block to look for a surface
                    for (int i = 0; i < 5; i++) {
                        mPos.move(Direction.UP);
                        stateAtPos = level.getBlockState(mPos);
                        if (stateAtPos == stateToPlace) continue NEXT_BLOCK;
                        if (!stateAtPos.isSolid()) {
                            mPos.move(Direction.DOWN);
                            foundSurface = true;
                            break;
                        }
                    }
                } else {
                    // search downwards if we're an air block to look for a surface
                    for (int i = 0; i < 5; i++) {
                        mPos.move(Direction.DOWN);
                        stateAtPos = level.getBlockState(mPos);
                        if (stateAtPos == stateToPlace) continue NEXT_BLOCK;
                        if (stateAtPos.isSolid()) {
                            foundSurface = true;
                            break;
                        }
                    }
                }
                // if we haven't found a surface, then skip
                if (!foundSurface)
                    continue NEXT_BLOCK;

                boolean waterfall = false;//random.nextInt(20) == 0;
                boolean onUpEdge = false;
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    borderCheckPos.set(mPos);
                    borderCheckPos.move(direction);

                    BlockState neighborState = level.getBlockState(borderCheckPos);
                    if (!neighborState.isFaceSturdy(level, borderCheckPos, direction.getOpposite(), SupportType.FULL) && neighborState != stateToPlace && !waterfall) {
                        continue NEXT_BLOCK;
                    }

                    borderCheckPos.move(Direction.UP);
                    if (level.getBlockState(borderCheckPos).isFaceSturdy(level, borderCheckPos, direction.getOpposite(), SupportType.FULL)) {
                        onUpEdge = true;
                    }
                }

                // only discard it when its going up occasionally
                if (onUpEdge && noise.getValue(mPos.getX(), 1000, mPos.getZ()) > 0)
                    continue NEXT_BLOCK;

                double dist = Mth.length(x, z);

                double distanceFromOrigin = Math.sqrt(mPos.distToCenterSqr(origin.getCenter()));
                dist = Mth.lerp(0.25, distanceFromOrigin, dist);

                double noiseValue = noise.getValue(mPos.getX(), mPos.getY(), mPos.getZ()) * noiseIntensity;
                dist += noiseValue;

                if (dist > radius)
                    continue NEXT_BLOCK;

                mPos.move(Direction.DOWN);
                if (!level.getBlockState(mPos).isFaceSturdy(level, mPos, Direction.UP, SupportType.FULL))
                    continue NEXT_BLOCK;

                mPos.move(Direction.UP, 2);
                if (!level.getBlockState(mPos).canBeReplaced())
                    continue NEXT_BLOCK;
                mPos.move(Direction.DOWN);

                BlockPos pos = mPos.immutable();
                positionsToPlace.add(pos);
                if (waterfall) positionsToUpdate.add(pos);
            }
        }


        for (BlockPos pos : positionsToPlace) {
            mPos.set(pos);
            level.setBlock(mPos, stateToPlace, 3);
            mPos.move(Direction.UP);
            BlockState stateAtPos = level.getBlockState(mPos);
            while (mPos.getY() < level.getMaxBuildHeight() - 1 && (!stateAtPos.isAir() && stateAtPos.canBeReplaced())) {
                level.destroyBlock(mPos, false);
                //level.setBlock(mPos, Blocks.AIR.defaultBlockState(), 3);
                mPos.move(Direction.UP);
                stateAtPos = level.getBlockState(mPos);
            }
        }

        for (BlockPos pos : positionsToUpdate) {
            BlockState stateAtPos = level.getBlockState(pos);
            level.scheduleTick(pos, stateAtPos.getFluidState().getType(), 0);
        }

        return !positionsToPlace.isEmpty();
    }

    public record PoolConfiguration(BlockStateProvider stateProvider,
                                    IntProvider radius) implements FeatureConfiguration {
        public static final Codec<PoolConfiguration> CODEC = RecordCodecBuilder.create(
                obj -> obj.group(
                        BlockStateProvider.CODEC.fieldOf("state_provider")
                                .forGetter(PoolConfiguration::stateProvider),
                        // default of 4
                        IntProvider.POSITIVE_CODEC.fieldOf("radius")
                                .orElse(ConstantInt.of(4)).forGetter(PoolConfiguration::radius)
                ).apply(obj, PoolConfiguration::new)
        );
    }
}