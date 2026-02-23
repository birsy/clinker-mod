package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.Arrays;

public class BiomeBlender {
    static final int BIOME_KERNEL_SIZE = 11, HALF_BIOME_KERNEL_SIZE = 5;
    static final int BIOME_BLUR_KERNEL_SIZE = 7, HALF_BIOME_BLUR_KERNEL_SIZE = 3;
    static final double[] BIOME_DISTANCE_KERNEL = new double[BIOME_KERNEL_SIZE * BIOME_KERNEL_SIZE];
    static final double[] BIOME_BLUR_KERNEL = new double[BIOME_KERNEL_SIZE * BIOME_KERNEL_SIZE];
    // optimized smaller version for when i only need the blur kernel
    static final double[] SMALL_BIOME_BLUR_KERNEL = new double[BIOME_BLUR_KERNEL_SIZE * BIOME_BLUR_KERNEL_SIZE];
    static {
        double totalWeight = 0;
        for (int x = 0; x < BIOME_KERNEL_SIZE; x++) {
            double centeredX = (x + 0.5) - (BIOME_KERNEL_SIZE * 0.5);
            for (int y = 0; y < BIOME_KERNEL_SIZE; y++) {
                double centeredY = (y + 0.5) - (BIOME_KERNEL_SIZE * 0.5);

                int index = x + y * BIOME_KERNEL_SIZE;

                double distance = Mth.length(centeredX, centeredY);
                BIOME_DISTANCE_KERNEL[index] = distance;

                double weight = blurKernel(distance);
                totalWeight += weight;
                BIOME_BLUR_KERNEL[index] = weight;
            }
        }
        for (int x = 0; x < BIOME_BLUR_KERNEL_SIZE; x++) {
            double centeredX = (x + 0.5) - (BIOME_BLUR_KERNEL_SIZE * 0.5);
            for (int y = 0; y < BIOME_BLUR_KERNEL_SIZE; y++) {
                double centeredY = (y + 0.5) - (BIOME_BLUR_KERNEL_SIZE * 0.5);
                int index = x + y * BIOME_BLUR_KERNEL_SIZE;
                double distance = Mth.length(centeredX, centeredY);
                SMALL_BIOME_BLUR_KERNEL[index] = blurKernel(distance);
            }
        }

        // normalize
        for (int i = 0; i < BIOME_BLUR_KERNEL.length; i++) BIOME_BLUR_KERNEL[i] /= totalWeight;
        for (int i = 0; i < SMALL_BIOME_BLUR_KERNEL.length; i++) SMALL_BIOME_BLUR_KERNEL[i] /= totalWeight;
    }

    private static double blurKernel(double distance) {
        double biomeBlurRadius = BIOME_BLUR_KERNEL_SIZE * 0.5;
        double t = Math.clamp(distance / biomeBlurRadius, 0, 1);
        return (1 - t * t) * (1 - t * t);
    }

    final BiomeList biomeList;
    final OthershoreBiomeSource biomeSource;

    public BiomeBlender(BiomeList biomeList, OthershoreBiomeSource biomeSource) {
        this.biomeList = biomeList;
        this.biomeSource = biomeSource;
    }

    public int requiredBiomeCachePadding() {
        return HALF_BIOME_KERNEL_SIZE + 1;
    }

    public ChunkBiomeBlendingInfo generateChunkBiomeBlendingInfo(BiomeCache2d surfaceBiomeCache, int minX, int minZ, int padding) {
        NoiseField[] biomeWeightFields = new NoiseField[biomeList.maxId() + 1];
        NoiseField[] biomeDistanceFields = new NoiseField[biomeList.maxId() + 1];

        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = NoiseFieldTypes.COARSE_2D.create(1, padding);
            double[] biomeWeightFieldArray = biomeWeightField.array();
            NoiseField biomeDistanceField = NoiseFieldTypes.COARSE_2D.create(1, padding);
            double[] biomeDistanceFieldArray = biomeDistanceField.array();

            biomeWeightField.byBlock((index, x, y, z) -> fillBiomeBlendingFields(
                    biome, surfaceBiomeCache,
                    biomeWeightFieldArray, biomeDistanceFieldArray,
                    minX, minZ,
                    index, x, z
            ));
            int biomeId = biomeList.getId(biome);
            biomeWeightFields[biomeId] = biomeWeightField;
            biomeDistanceFields[biomeId] = biomeDistanceField;
        }

        return new ChunkBiomeBlendingInfo(biomeWeightFields, biomeDistanceFields);
    }

    public double[] getBiomeBlendingWeights(double[] weightByBiomeId, int x, int z) {
        Arrays.fill(weightByBiomeId, 0);
        int kernelIndex = 0;
        for (int oZ = 0; oZ < BIOME_BLUR_KERNEL_SIZE; oZ++) {
            int sampleQZ = QuartPos.fromBlock(z + (oZ - HALF_BIOME_BLUR_KERNEL_SIZE) * QuartPos.SIZE);

            for (int oX = 0; oX < BIOME_BLUR_KERNEL_SIZE; oX++) {
                int sampleQX = QuartPos.fromBlock(x + (oX - HALF_BIOME_BLUR_KERNEL_SIZE) * QuartPos.SIZE);

                Holder<Biome> neighborBiome = this.biomeSource.getSurfaceBiome(sampleQX, sampleQZ);
                weightByBiomeId[biomeList.getId(neighborBiome)] += SMALL_BIOME_BLUR_KERNEL[kernelIndex];
                kernelIndex++;
            }
        }
        return weightByBiomeId;
    }

    private static void fillBiomeBlendingFields(Holder<Biome> biome, BiomeCache2d surfaceBiomeCache,
                                                double[] weightField, double[] distanceToBorderField,
                                                int minX, int minZ, int index, int localX, int localZ) {
        int x = minX + localX,
            z = minZ + localZ;
        Holder<Biome> currentBiome = surfaceBiomeCache.retrieve(QuartPos.fromBlock(x), QuartPos.fromBlock(z));
        boolean isInsideBiome = currentBiome.is(biome);
        double minimumDistance = 100;

        double totalWeight = 0;
        int kernelIndex = 0;
        for (int oZ = 0; oZ < BIOME_KERNEL_SIZE; oZ++) {
            int sampleQZ = QuartPos.fromBlock(z + (oZ - HALF_BIOME_KERNEL_SIZE) * QuartPos.SIZE);

            for (int oX = 0; oX < BIOME_KERNEL_SIZE; oX++) {
                int sampleQX = QuartPos.fromBlock(x + (oX - HALF_BIOME_KERNEL_SIZE) * QuartPos.SIZE);

                Holder<Biome> neighborBiome = surfaceBiomeCache.retrieve(sampleQX, sampleQZ);
                boolean neighborIsBiome = neighborBiome.is(biome);

                if (isInsideBiome != neighborIsBiome)
                    minimumDistance = Math.min(minimumDistance, BIOME_DISTANCE_KERNEL[kernelIndex]);
                if (neighborIsBiome)
                    totalWeight += BIOME_BLUR_KERNEL[kernelIndex];
                kernelIndex++;
            }
        }
        weightField[index] = totalWeight;
        distanceToBorderField[index] = minimumDistance * QuartPos.SIZE * (isInsideBiome ? -1 : 1);
    }

    public record ChunkBiomeBlendingInfo(NoiseField[] weightByBiomeId, NoiseField[] borderDistanceByBiomeId) {
        @Nullable
        public NoiseField weightForBiome(BiomeList biomes, Holder<Biome> biome) {
            return weightByBiomeId[biomes.getId(biome)];
        }
        @Nullable
        public NoiseField borderDistanceForBiome(BiomeList biomes, Holder<Biome> biome) {
            return borderDistanceByBiomeId[biomes.getId(biome)];
        }
    }
}
