package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.Arrays;

public class BiomeBlender {
    static final int BIOME_BLUR_KERNEL_SIZE = 7, HALF_BIOME_BLUR_KERNEL_SIZE = 3;
    static final double[] BIOME_BLUR_KERNEL = Util.make(() -> {
        double halfSize = BIOME_BLUR_KERNEL_SIZE / 2.0;
        double gamma = halfSize / 3.0;
        double total = 0;
        double[] array = new double[BIOME_BLUR_KERNEL_SIZE * BIOME_BLUR_KERNEL_SIZE];
        for (int kX = 0; kX < BIOME_BLUR_KERNEL_SIZE; kX++) {
            double kernelX = kX + 0.5;
            for (int kZ = 0; kZ < BIOME_BLUR_KERNEL_SIZE; kZ++) {
                double kernelZ = kZ + 0.5;
                double distanceToCenter = Math.sqrt(
                        (kernelX - halfSize) * (kernelX - halfSize) +
                                (kernelZ - halfSize) * (kernelZ - halfSize)
                );
                double value =  (1.0 / Math.sqrt(2 * Math.PI * gamma)) * Math.exp(-((distanceToCenter * distanceToCenter) / (2 * gamma * gamma)));
                array[kX + kZ * BIOME_BLUR_KERNEL_SIZE] = value;
                total += value;
            }
        }
        // normalize
        for (int i = 0; i < array.length; i++) array[i] /= total;
        return array;
    });

    final BiomeList biomeList;
    final OthershoreBiomeSource biomeSource;

    public BiomeBlender(BiomeList biomeList, OthershoreBiomeSource biomeSource) {
        this.biomeList = biomeList;
        this.biomeSource = biomeSource;
    }

    public int requiredBiomeCachePadding() {
        return HALF_BIOME_BLUR_KERNEL_SIZE + 1;
    }

    public ChunkBiomeBlendingWeights generateChunkBiomeBlendingWeights(BiomeCache2d surfaceBiomeCache, int minX, int minZ, int padding) {
        NoiseField[] biomeWeightFields = new NoiseField[biomeList.maxId() + 1];

        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = NoiseFieldTypes.COARSE_2D.create(1, padding);
            double[] biomeWeightFieldArray = biomeWeightField.array();
            biomeWeightField.byBlock((index, x, y, z) -> fillBiomeWeightField(
                    biome, surfaceBiomeCache, biomeWeightFieldArray,
                    minX, minZ,
                    index, x, z
            ));
            biomeWeightFields[biomeList.getId(biome)] = biomeWeightField;
        }

        return new ChunkBiomeBlendingWeights(biomeWeightFields, generateBiomeTransitionFactorField(surfaceBiomeCache, biomeWeightFields, padding));
    }

    private NoiseField generateBiomeTransitionFactorField(BiomeCache2d surfaceBiomeCache, NoiseField[] biomeWeightFields, int padding) {
        NoiseField biomeTransitionFactorField = NoiseFieldTypes.COARSE_2D.create(1, padding);
        double[] biomeTransitionFactorArray = biomeTransitionFactorField.array();

        biomeTransitionFactorField.byIndex(index -> {
            double max1 = 0, max2 = 0;
            for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
                NoiseField biomeWeightField = biomeWeightFields[biomeList.getId(biome)];
                double weight = biomeWeightField.array()[index];
                if (weight > max1) {
                    max2 = max1;
                    max1 = weight;
                } else if (weight > max2) {
                    max2 = weight;
                }
            }
            biomeTransitionFactorArray[index] = Math.clamp(1 - (max1 - max2), 0, 1);
        });

        return biomeTransitionFactorField;
    }

    public double[] getBiomeBlendingWeights(double[] weightByBiomeId, int x, int z) {
        Arrays.fill(weightByBiomeId, 0);
        int blurIndex = 0;
        for (int oZ = 0; oZ < BIOME_BLUR_KERNEL_SIZE; oZ++) {
            int offsetQZ = QuartPos.fromBlock(z + (oZ - HALF_BIOME_BLUR_KERNEL_SIZE) * QuartPos.SIZE);

            for (int oX = 0; oX < BIOME_BLUR_KERNEL_SIZE; oX++) {
                int offsetQX = QuartPos.fromBlock(x + (oX - HALF_BIOME_BLUR_KERNEL_SIZE) * QuartPos.SIZE);

                Holder<Biome> neighborBiome = this.biomeSource.getSurfaceBiome(offsetQX, offsetQZ);
                weightByBiomeId[biomeList.getId(neighborBiome)] += BIOME_BLUR_KERNEL[blurIndex];
                blurIndex++;
            }
        }
        return weightByBiomeId;
    }

    private static void fillBiomeWeightField(Holder<Biome> biome, BiomeCache2d surfaceBiomeCache, double[] field,
                                             int minX, int minZ, int index, int localX, int localZ) {
        int x = minX + localX,
            z = minZ + localZ;
        double total = 0;
        int blurIndex = 0;
        for (int oZ = 0; oZ < BIOME_BLUR_KERNEL_SIZE; oZ++) {
            int offsetQZ = QuartPos.fromBlock(z + (oZ - HALF_BIOME_BLUR_KERNEL_SIZE) * QuartPos.SIZE);

            for (int oX = 0; oX < BIOME_BLUR_KERNEL_SIZE; oX++) {
                int offsetQX = QuartPos.fromBlock(x + (oX - HALF_BIOME_BLUR_KERNEL_SIZE) * QuartPos.SIZE);

                Holder<Biome> neighborBiome = surfaceBiomeCache.retrieve(offsetQX, offsetQZ);
                if (neighborBiome.is(biome)) total += BIOME_BLUR_KERNEL[blurIndex];
                blurIndex++;
            }
        }
        field[index] = total;
    }

    public record ChunkBiomeBlendingWeights(NoiseField[] weightByBiomeId, NoiseField biomeTransitionFactorField) {
        @Nullable
        public NoiseField weightForBiome(BiomeList biomes, Holder<Biome> biome) {
            return weightByBiomeId[biomes.getId(biome)];
        }
    }
}
