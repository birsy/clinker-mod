package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Arrays;

public class PoolFeature extends Feature<PoolFeature.PoolFeatureConfiguration> {
    // x, z, weight
    private static final int[][] NEIGHBOR_WEIGHTS = Util.make(() -> {
        int[][] array = new int[8][3];
        int index = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                int weight = Math.round((float) Mth.length(x, z) * 10.0F);
                array[index++] = new int[]{x, z, weight};
            }
        }
        return array;
    });

    public PoolFeature(Codec<PoolFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<PoolFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed()));
        NormalNoise noise = NormalNoise.create(worldgenrandom, -4, 1.0);

        int radius = random.nextIntBetweenInclusive(3, 8);
        int arrayRadius = 8;
        int arrayWidth = 16;
        int[] distances = new int[arrayWidth * arrayWidth];
        Arrays.fill(distances, Integer.MAX_VALUE);

        // dial's algorithm
        BlockPos.MutableBlockPos cursor = origin.mutable();
        int center = arrayRadius;
        int maxDistance = radius * 10;
        IntArrayList[] buckets = new IntArrayList[maxDistance];
        Arrays.fill(buckets, new IntArrayList());
        int centerIndex = center + center * arrayWidth;
        distances[centerIndex] = 0;
        buckets[0].add(centerIndex);
        for (int curDist = 0; curDist < maxDistance; curDist++) {
            IntArrayList bucket = buckets[curDist];
            for (int bucketIndex = 0; bucketIndex < bucket.size(); bucketIndex++) {
                int currentIndex = bucket.getInt(bucketIndex);
                int currentDistance = distances[currentIndex];
                if (currentDistance != curDist) continue;

                // decode position from index
                int x = currentIndex % arrayWidth,
                    z = currentIndex / arrayWidth;

                for (int[] neighborWeight : NEIGHBOR_WEIGHTS) {
                    int nX = x + neighborWeight[0],
                        nZ = z + neighborWeight[1];
                    if (nX < 0 || nX >= arrayWidth || nZ < 0 || nZ >= arrayWidth) continue;

                    int gX = origin.getX() + nX - center, gZ = origin.getZ() + nZ - center;
                    int noiseOffset = Math.round(
                            Mth.map((float) noise.getValue(gX, origin.getY(), gZ), -1, 1, 0, 30)
                    );
                    int distance = neighborWeight[2] + noiseOffset;
                    int neighborIndex = nX + nZ * arrayWidth;
                    int newDistance = currentDistance + distance;
                    if (newDistance >= distances[neighborIndex]) continue;

                    cursor.set(gX, origin.getY(), gZ);
                    if (level.getBlockState(cursor).isSolid()) continue;

                    distances[neighborIndex] = newDistance;
                    if (newDistance < maxDistance) buckets[newDistance].add(neighborIndex);
                }
            }
        }

        BlockStateProvider borderStateProvider = context.config().border();
        BlockStateProvider fluidStateProvider = context.config().fluid();
        for (int i = 0; i < distances.length; i++) {
            int distance = distances[i];
            if (distance > 1000) continue;
            double unweightedDistance = distance / 10.0;
            int x = i % arrayWidth,
                z = i / arrayWidth;
            int gX = origin.getX() + x - center, gZ = origin.getZ() + z - center;
            cursor.set(gX, origin.getY(), gZ);
            for (int y = 0; y <= radius; y++) {
                //double sphericalDist = Math.sqrt(unweightedDistance * unweightedDistance + y * y);
                //if (sphericalDist > radius + 0.5) break;
                if (!level.getBlockState(cursor).isSolid()) {
                    level.setBlock(cursor, borderStateProvider.getState(random, cursor), 2);

                    if (distance > radius - 0.5) {
                        //level.setBlock(cursor, borderStateProvider.getState(random, cursor), 2);
                    } else {
                        //level.setBlock(cursor, fluidStateProvider.getState(random, cursor), 2);
                    }
                }
                cursor.move(Direction.DOWN);
            }
        }

        return true;
    }

    public record PoolFeatureConfiguration(
            IntProvider radius, IntProvider borderThickness,
            BlockStateProvider fluid, BlockStateProvider border
    ) implements FeatureConfiguration {
        public static final Codec<PoolFeatureConfiguration> CODEC = RecordCodecBuilder.create(
                codec -> codec.group(
                        IntProvider.CODEC.fieldOf("radius").forGetter(config -> config.radius),
                        IntProvider.CODEC.fieldOf("border_thickness").forGetter(config -> config.borderThickness),
                        BlockStateProvider.CODEC.fieldOf("fluid_state").forGetter(config -> config.fluid),
                        BlockStateProvider.CODEC.fieldOf("border_state").forGetter(config -> config.border)
                ).apply(codec, PoolFeature.PoolFeatureConfiguration::new)
        );
    }
}
