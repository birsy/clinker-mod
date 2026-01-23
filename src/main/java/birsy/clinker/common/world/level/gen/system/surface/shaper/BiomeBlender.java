package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.Map;

public class BiomeBlender {
    static final int BIOME_BLUR_KERNEL_SIZE = 7, HALF_BIOME_BLUR_KERNEL_SIZE = 3;
    static final double[] BIOME_BLUR_KERNEL = Util.make(() -> {
        double halfSize = BIOME_BLUR_KERNEL_SIZE / 2.0;
        double gamma = halfSize / 3.0;
        double[] array = new double[BIOME_BLUR_KERNEL_SIZE * BIOME_BLUR_KERNEL_SIZE];
        for (int kX = 0; kX < BIOME_BLUR_KERNEL_SIZE; kX++) {
            double kernelX = kX + 0.5;
            for (int kZ = 0; kZ < BIOME_BLUR_KERNEL_SIZE; kZ++) {
                double kernelZ = kZ + 0.5;
                double distanceToCenter = Math.sqrt(
                        (kernelX - halfSize) * (kernelX - halfSize) +
                                (kernelZ - halfSize) * (kernelZ - halfSize)
                );
                array[kX + kZ * BIOME_BLUR_KERNEL_SIZE] = (1.0 / Math.sqrt(2 * Math.PI * gamma)) * Math.exp(-((distanceToCenter * distanceToCenter) / (2 * gamma * gamma)));
            }
        }
        return array;
    });
    private static final NoiseField EMPTY = NoiseFieldTypes.COARSE_2D.create(1, 0);

    final int maxBiomeId;
    final Object2IntMap<Holder<Biome>> biomeToBiomeId;
    final OthershoreBiomeSource biomeSource;
    final Long2ObjectMap<BiomeBlendingInfo> cachedInfoByChunk = new Long2ObjectOpenHashMap<>();

    public BiomeBlender(Object2IntMap<Holder<Biome>> biomeToBiomeId, int maxBiomeId, OthershoreBiomeSource biomeSource) {
        this.biomeToBiomeId = biomeToBiomeId;
        this.maxBiomeId = maxBiomeId;
        this.biomeSource = biomeSource;
    }

    public int requiredPadding() {
        return HALF_BIOME_BLUR_KERNEL_SIZE + 1;
    }

    public BiomeBlendingInfo generateBiomeBlendingInfoAndAddToCache(BiomeCache2d surfaceBiomeCache, ChunkPos chunkPos) {
        int minX = chunkPos.getMinBlockX(), minZ = chunkPos.getMinBlockZ();
        NoiseField[] biomeWeightFields = new NoiseField[maxBiomeId + 1];
        Arrays.fill(biomeWeightFields, EMPTY);
        NoiseField totalWeightField = NoiseFieldTypes.COARSE_2D.create(1, 0);

        double[] totalWeightFieldArray = totalWeightField.array();
        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = NoiseFieldTypes.COARSE_2D.create(1, 0);
            double[] biomeWeightFieldArray = biomeWeightField.array();
            biomeWeightField.byBlock((index, x, y, z) -> fillBiomeWeightField(
                    biome, surfaceBiomeCache, biomeWeightFieldArray,
                    minX, minZ,
                    index, x, z
            ));
            biomeWeightFields[biomeToBiomeId.get(biome)] = biomeWeightField;

            totalWeightField.byIndex((index) -> totalWeightFieldArray[index] += biomeWeightFieldArray[index]);
        }
        totalWeightField.byIndex((index) -> totalWeightFieldArray[index] = 1.0 / totalWeightFieldArray[index]);

        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = biomeWeightFields[biomeToBiomeId.get(biome)];
            double[] biomeWeightFieldArray = biomeWeightField.array();
            totalWeightField.byIndex((index) -> biomeWeightFieldArray[index] /= totalWeightFieldArray[index]);
        }
        BiomeBlendingInfo info = new BiomeBlendingInfo(biomeWeightFields);
        synchronized (cachedInfoByChunk) {
            cachedInfoByChunk.put(chunkPos.toLong(), info);
        }
        return new BiomeBlendingInfo(biomeWeightFields);
    }

    public BiomeBlendingInfo retrieveBiomeBlendingInfoAndRemoveFromCache(ChunkPos chunkPos) {
        long key = chunkPos.toLong();
        BiomeBlendingInfo info;
        synchronized (cachedInfoByChunk) {
            info = cachedInfoByChunk.get(key);
            cachedInfoByChunk.remove(key);
        }
        return info;
    }

    private static void fillBiomeWeightField(Holder<Biome> biome, BiomeCache2d surfaceBiomeCache, double[] field,
                                             int minX, int minZ,
                                             int index, int x, int z) {
        int qX = QuartPos.fromBlock(minX + x),
                qZ = QuartPos.fromBlock(minZ + z);
        double total = 0;
        int blurIndex = 0;
        for (int oZ = 0; oZ < BIOME_BLUR_KERNEL_SIZE; oZ++) {
            int offsetQZ = qZ + (oZ - HALF_BIOME_BLUR_KERNEL_SIZE);
            for (int oX = 0; oX < BIOME_BLUR_KERNEL_SIZE; oX++) {
                int offsetQX = qX + (oX - HALF_BIOME_BLUR_KERNEL_SIZE);
                Holder<Biome> neighborBiome = surfaceBiomeCache.retrieve(offsetQX, offsetQZ);
                if (neighborBiome.is(biome)) total += BIOME_BLUR_KERNEL[blurIndex];
                blurIndex++;
            }
        }
        field[index] = total;
    }


    public record BiomeBlendingInfo(NoiseField[] weightByBiomeId) {
        public NoiseField weightForBiome(Map<Holder<Biome>, Integer> biomeToBiomeId, Holder<Biome> biome) {
            return weightByBiomeId[biomeToBiomeId.get(biome)];
        }
    }
}
