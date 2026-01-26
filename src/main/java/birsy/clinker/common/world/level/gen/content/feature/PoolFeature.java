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
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlobsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Arrays;

public class PoolFeature extends Feature<PoolFeature.PoolFeatureConfiguration> {
    // x, z, weight
    private static final int[][] NEIGHBOR_WEIGHTS = Util.make(() -> {
        int[][] array = new int[17][4];
        int index = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y <= 1; y++) {
                    if (x == 0 && z == 0 && y == 0) continue;
                    int weight = Math.round((float) Mth.length(x, y * 0.8, z) * 10.0F);
                    array[index++] = new int[]{x, y, z, weight};
                }
            }
        }
        return array;
    });
    private static final int[][] BORDER_OFFSETS = Util.make(() -> {
        Direction[] directions = Direction.values();
        int[][] array = new int[directions.length - 1][3];
        int index = 0;
        for (Direction dir : directions) {
            if (dir == Direction.UP) continue;
            array[index++] = new int[]{dir.getStepX(), dir.getStepY(), dir.getStepZ()};
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

        int offsetHeight = context.config().offsetHeight.sample(random);
        int radius = context.config().radius.sample(random);
        origin = origin.above(offsetHeight);

        int arrayRadius = 7;
        int arrayWidth = arrayRadius * 2 + 1,
            arrayHeight = 8,
            arrayLayerSize = arrayWidth * arrayWidth;
        int[] distances = new int[arrayLayerSize * arrayHeight];
        Arrays.fill(distances, Integer.MAX_VALUE);

        // dial's algorithm
        BlockPos.MutableBlockPos cursor = origin.mutable();
        int center = arrayRadius;
        int maxDistance = radius * 10;
        IntArrayList[] buckets = new IntArrayList[maxDistance];
        Arrays.fill(buckets, new IntArrayList());
        int centerIndex = center + center * arrayWidth + 0 * arrayLayerSize;
        distances[centerIndex] = 0;
        buckets[0].add(centerIndex);
        for (int curDist = 0; curDist < maxDistance; curDist++) {
            IntArrayList bucket = buckets[curDist];
            for (int bucketIndex = 0; bucketIndex < bucket.size(); bucketIndex++) {
                int currentIndex = bucket.getInt(bucketIndex);
                int currentDistance = distances[currentIndex];
                if (currentDistance != curDist) continue;

                // decode position from index
                int bY = currentIndex / arrayLayerSize;
                int rem = currentIndex % arrayLayerSize;
                int bZ = rem / arrayWidth;
                int bX = rem % arrayWidth;

                for (int[] neighborWeight : NEIGHBOR_WEIGHTS) {
                    int nX = bX + neighborWeight[0],
                        nY = bY + neighborWeight[1],
                        nZ = bZ + neighborWeight[2];
                    if (nX < 0 || nX >= arrayWidth || nZ < 0 || nZ >= arrayWidth || nY < 0 || nY >= arrayHeight) continue;

                    int gX = origin.getX() + nX - center,
                        gY = origin.getY() - nY,
                        gZ = origin.getZ() + nZ - center;
                    int noiseOffset = Math.round(
                            Mth.map((float) noise.getValue(gX, gY, gZ),
                                    -1, 1, -8, 20)
                    );
                    int distance = Math.max(neighborWeight[3] + noiseOffset, 0);
                    int neighborIndex = nX + nZ * arrayWidth + nY * arrayLayerSize;
                    int newDistance = currentDistance + distance;
                    if (newDistance >= distances[neighborIndex]) continue;

                    cursor.set(gX, gY, gZ);
                    if (level.getBlockState(cursor).isSolid()) continue;

                    distances[neighborIndex] = newDistance;
                    if (newDistance < maxDistance) buckets[newDistance].add(neighborIndex);
                }
            }
        }

        // place blocks
        BlockStateProvider borderStateProvider = context.config().border();
        BlockStateProvider fluidStateProvider = context.config().fluid();
        int maxDist = radius * 10;
        for (int x = 0; x < arrayWidth; x++) {
            cursor.setX(origin.getX() + x - center);
            for (int z = 0; z < arrayWidth; z++) {
                cursor.setZ(origin.getZ() + z - center);
                for (int y = 0; y < arrayHeight; y++) {
                    // y is reversed
                    cursor.setY(origin.getY() - y);
                    int index = x + z * arrayWidth + y * arrayLayerSize;
                    if (distances[index] >= maxDist) continue;

                    boolean isBorder = x <= 0 || x >= arrayWidth-1 ||
                                       z <= 0 || z >= arrayWidth-1 ||
                                       y >= arrayHeight-1;
                    if (!isBorder) {
                        for (int[] borderOffset : BORDER_OFFSETS) {
                            int nX = x + borderOffset[0], nY = y - borderOffset[1], nZ = z + borderOffset[2];
                            int neighborIndex = nX + nZ * arrayWidth + nY * arrayLayerSize;
                            if (distances[neighborIndex] >= maxDist) {
                                isBorder = true;
                                break;
                            }
                        }
                    }

                    if (isBorder) {
                        level.setBlock(cursor, borderStateProvider.getState(random, cursor), 2);
                    } else {
                        level.setBlock(cursor, fluidStateProvider.getState(random, cursor), 2);
                    }
                }
            }
        }
        return true;
    }

    public record PoolFeatureConfiguration(
            IntProvider radius, IntProvider offsetHeight,
            BlockStateProvider fluid, BlockStateProvider border
    ) implements FeatureConfiguration {
        public static final Codec<PoolFeatureConfiguration> CODEC = RecordCodecBuilder.create(
                codec -> codec.group(
                        IntProvider.CODEC.fieldOf("radius").forGetter(config -> config.radius),
                        IntProvider.CODEC.fieldOf("offset_height").forGetter(config -> config.radius),
                        BlockStateProvider.CODEC.fieldOf("fluid_state").forGetter(config -> config.fluid),
                        BlockStateProvider.CODEC.fieldOf("border_state").forGetter(config -> config.border)
                ).apply(codec, PoolFeature.PoolFeatureConfiguration::new)
        );
    }
}