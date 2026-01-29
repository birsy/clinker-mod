package birsy.clinker.common.world.level.gen.content.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Optional;

public class SurfaceBlobFeature extends Feature<SurfaceBlobFeature.SurfaceBlobConfiguration> {
    public SurfaceBlobFeature(Codec<SurfaceBlobConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<SurfaceBlobConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        SurfaceBlobConfiguration config = context.config();

        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed()));
        NormalNoise noise = NormalNoise.create(worldgenrandom, -3, 1.0F);

        int noiseIntensity = 7;
        int radius = config.radius.sample(random);
        int generationRadius = radius + noiseIntensity;

        boolean placedBlock = false;
        BlockPos.MutableBlockPos mutableBlockPos = origin.mutable();

        double yMult = config.soilDepthMultiplier.sample(random);

        // for every column
        for (int x = -generationRadius; x <= generationRadius; x++) {
            for (int z = -generationRadius; z < generationRadius; z++) {
                mutableBlockPos.set(origin.getX() + x, origin.getY(), origin.getZ() + z);

                boolean foundSurface = false;
                if (level.getBlockState(mutableBlockPos).isSolid()) {
                    // search upwards if we're a solid block to look for a surface
                    for (int i = 0; i < 5; i++) {
                        mutableBlockPos.move(Direction.UP);
                        if (!level.getBlockState(mutableBlockPos).isSolid()) {
                            mutableBlockPos.move(Direction.DOWN);
                            foundSurface = true;
                            break;
                        }
                    }
                } else {
                    // search downwards if we're an air block to look for a surface
                    for (int i = 0; i < 5; i++) {
                        mutableBlockPos.move(Direction.DOWN);
                        if (level.getBlockState(mutableBlockPos).isSolid()) {
                            foundSurface = true;
                            break;
                        }
                    }
                }
                // if we haven't found a surface, then skip
                if (!foundSurface)
                    continue;

                double xzDist = Mth.length(x, z);
                for (int y = 0; y < generationRadius / yMult; y++) {
                    double dist = Mth.length(xzDist, y * yMult); // multiply y by yMult so it's more squished

                    double distanceFromOrigin = Math.sqrt(mutableBlockPos.distToCenterSqr(origin.getCenter()));
                    dist = Mth.lerp(0.25, distanceFromOrigin, dist);

                    double noiseValue = noise.getValue(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ()) * noiseIntensity;
                    dist += noiseValue;

                    double randomDitherValue = random.triangle(0, 1);
                    dist += randomDitherValue;

                    BlockState currentBlockState = level.getBlockState(mutableBlockPos);
                    if (currentBlockState.isAir()) break;
                    if (dist <= radius) {
                        if (y == 0) {
                            // surface block
                            level.setBlock(mutableBlockPos, config.surfaceStateProvider.getState(random, mutableBlockPos), 3);

                            // place foliage feature
                            mutableBlockPos.move(Direction.UP);
                            config.foliageFeature.ifPresent(placedFeatureHolder -> placedFeatureHolder.value().place(level, context.chunkGenerator(), random, mutableBlockPos));
                            mutableBlockPos.move(Direction.DOWN);
                        } else {
                            // underground block
                            level.setBlock(mutableBlockPos, config.underStateProvider.getState(random, mutableBlockPos), 3);
                        }

                        placedBlock = true;
                    }

                    mutableBlockPos.move(Direction.DOWN);
                }
            }
        }

        return placedBlock;
    }

    public record SurfaceBlobConfiguration(Optional<Holder<PlacedFeature>> foliageFeature,
                                           BlockStateProvider surfaceStateProvider, BlockStateProvider underStateProvider,
                                           IntProvider radius,
                                           FloatProvider soilDepthMultiplier) implements FeatureConfiguration {
        public static final Codec<SurfaceBlobConfiguration> CODEC = RecordCodecBuilder.create(
                obj -> obj.group(
                                PlacedFeature.CODEC.lenientOptionalFieldOf("feature")
                                        .forGetter(SurfaceBlobConfiguration::foliageFeature),
                                BlockStateProvider.CODEC.fieldOf("surface_state_provider")
                                        .forGetter(SurfaceBlobConfiguration::surfaceStateProvider),
                                BlockStateProvider.CODEC.fieldOf("under_state_provider")
                                        .forGetter(SurfaceBlobConfiguration::underStateProvider),
                                // default of 4
                                IntProvider.POSITIVE_CODEC.fieldOf("radius")
                                        .orElse(ConstantInt.of(4)).forGetter(SurfaceBlobConfiguration::radius),
                                // default of 3.0
                                FloatProvider.codec(0, 1000000.0F).fieldOf("soil_depth_multiplier")
                                        .orElse(ConstantFloat.of(3.0F)).forGetter(SurfaceBlobConfiguration::soilDepthMultiplier)
                        ).apply(obj, SurfaceBlobConfiguration::new)
        );
    }
}
