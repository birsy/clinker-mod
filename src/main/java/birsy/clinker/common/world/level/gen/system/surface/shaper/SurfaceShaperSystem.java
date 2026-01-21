package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.VerticalRange;
import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.SeededNoiseHolder;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import java.util.*;

public class SurfaceShaperSystem {
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
    static final SurfaceShaper DEFAULT = new DefaultSurfaceShaper();

    final OthershoreBiomeSource biomeSource;
    final Map<Holder<Biome>, Integer> biomeToIndex;
    final Object2ObjectOpenHashMap<Holder<Biome>, SurfaceShaper> biomeToShaper;

    public SurfaceShaperSystem(HolderGetter<Biome> biomeGetter, OthershoreBiomeSource biomeSource) {
        this.biomeSource = biomeSource;

        this.biomeToShaper = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<TagKey<Biome>, SurfaceShaper> entry : SurfaceShapers.shaperByBiomeTag.entrySet()) {
            HolderSet.Named<Biome> tag = biomeGetter.getOrThrow(entry.getKey());
            for (Holder<Biome> biome : tag) {
                biomeToShaper.put(biome, entry.getValue());
            }
        }
        for (Map.Entry<ResourceKey<Biome>, SurfaceShaper> entry : SurfaceShapers.shaperByBiome.entrySet()) {
            Holder<Biome> biome = biomeGetter.getOrThrow(entry.getKey());
            biomeToShaper.put(biome, entry.getValue());
        }

        Set<Holder<Biome>> possibleBiomes = biomeSource.possibleBiomes();
        this.biomeToIndex = new HashMap<>(possibleBiomes.size());
        int index = 0;
        for (Holder<Biome> biomeHolder : possibleBiomes) {
            biomeToIndex.put(biomeHolder, index++);
        }
    }

    SurfaceShaper getSurfaceShaper(Holder<Biome> biome) {
        return biomeToShaper.getOrDefault(biome, DEFAULT);
    }

    public NoiseField generateSurfaceField(ChunkAccess chunk, SeededNoiseHolder noiseHolder,
                                           NoiseFieldCache cache,
                                           Collection<WorldFeature> worldFeaturesInChunk,
                                           NoiseField baseSurfaceHeight,
                                           int minSurfaceHeight, int maxSurfaceHeight,
                                           int minX, int minY, int minZ, int chunkHeight) {
        int padding = HALF_BIOME_BLUR_KERNEL_SIZE + 1;
        BiomeCache2d surfaceBiomeCache = biomeSource.createSurfaceBiomeCache(
                QuartPos.fromBlock(minX) - padding, QuartPos.fromBlock(minZ) - padding,
                QuartPos.fromBlock(minX + 16) + padding, QuartPos.fromBlock(minZ + 16) + padding
        );

        NoiseField totalWeightField = NoiseFieldTypes.COARSE_2D.create(chunkHeight, 0);
        NoiseField[] biomeWeightFields = createBiomeWeightFields(minX, minY, minZ, chunkHeight, surfaceBiomeCache, totalWeightField);

        // determine bounds
        int lowerBound = Integer.MAX_VALUE, upperBound = Integer.MIN_VALUE;
        for (Holder<Biome> biomeHolder : surfaceBiomeCache.containedBiomes()) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            lowerBound = Math.min(lowerBound, shaper.lowerBound());
            upperBound = Math.max(upperBound, shaper.upperBound());
        }
        lowerBound = Math.max(minSurfaceHeight + lowerBound, minY);
        upperBound = Math.min(maxSurfaceHeight + upperBound, chunkHeight-1);

        // initialize surface density field
        NoiseField surfaceDensityField = NoiseFieldTypes.COARSE.create(chunkHeight, 0);
        double[] surfaceDensityFieldArray = surfaceDensityField.array();
        // initialize surface density w/ estimate from base surface height
        Arrays.fill(surfaceDensityFieldArray, 0);
        surfaceDensityField.byBlock(0, lowerBound - minY - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = (y + minY) - baseSurfaceHeight.retrieve(x, y, z)
        );
        surfaceDensityField.byBlock(upperBound - minY + 1, chunkHeight - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = (y + minY) - baseSurfaceHeight.retrieve(x, y, z)
        );

        // shape per biome
        for (Holder<Biome> biomeHolder : surfaceBiomeCache.containedBiomes()) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            shaper.prefillNoiseFields(cache, lowerBound, upperBound);
        }
        for (Holder<Biome> biomeHolder : surfaceBiomeCache.containedBiomes()) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            NoiseField biomeWeightField = biomeWeightFields[biomeToIndex.get(biomeHolder)];
            surfaceDensityField.byBlock(lowerBound - minY, upperBound - minY,
                    (index, x, y, z) -> fillSurfaceDensity(
                            biomeWeightField, totalWeightField, surfaceDensityFieldArray,
                            shaper,
                            minX, minY, minZ,
                            index, x, y, z, cache.context
                    )
            );
        }

        // modify w/ world features
        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifySurfaceDensityField(minX, minY, minZ, cache, surfaceDensityField);
        
        return surfaceDensityField;
    }

    private NoiseField[] createBiomeWeightFields(int minX, int minY, int minZ, int chunkHeight, BiomeCache2d surfaceBiomeCache, NoiseField totalWeightField) {
        double[] totalWeightFieldArray = totalWeightField.array();

        NoiseField[] biomeWeightFields = new NoiseField[this.biomeToIndex.size()];
        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = NoiseFieldTypes.COARSE_2D.create(chunkHeight, 0);
            double[] biomeWeightFieldArray = biomeWeightField.array();
            biomeWeightField.byBlock((index, x, y, z) -> fillBiomeWeightField(
                    biome, surfaceBiomeCache, biomeWeightFieldArray,
                    minX, minZ,
                    index, x, z
            ));
            biomeWeightFields[biomeToIndex.get(biome)] = biomeWeightField;
            totalWeightField.byIndex((index) -> totalWeightFieldArray[index] += biomeWeightFieldArray[index]);
        }

        totalWeightField.byIndex((index) -> {
            double weight = totalWeightFieldArray[index];
            totalWeightFieldArray[index] = weight > 0 ? 1.0 / totalWeightFieldArray[index] : 0;
        });

        return biomeWeightFields;
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

    private VerticalRange fillSurfaceHeightField(int minX, int minY, int minZ, BiomeCache2d surfaceBiomeCache,
                                                 NoiseField[] biomeWeightFields, NoiseField totalWeightField,
                                                 NoiseField surfaceHeightField) {
        double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        double[] surfaceHeightArray = surfaceHeightField.array();
        for (Holder<Biome> biomeHolder : surfaceBiomeCache.containedBiomes()) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            NoiseField biomeWeightField = biomeWeightFields[biomeToIndex.get(biomeHolder)];
            surfaceHeightField.byBlock((index, x, y, z) -> {
                double weight = biomeWeightField.retrieve(x, y, z) * totalWeightField.retrieve(x, y, z);
                surfaceHeightArray[index] += shaper.height() * weight;
            });
        }
        for (double height : surfaceHeightArray) {
            if (height > max) max = height;
            if (height < min) min = height;
        }
        return new VerticalRange(Mth.floor(min), Mth.ceil(max));
    }

    private static void fillSurfaceDensity(NoiseField weightField, NoiseField totalWeightField, double[] surfaceDensities,
                                    SurfaceShaper shaper,
                                    int minX, int minY, int minZ,
                                    int index, int x, int y, int z,
                                    NoiseContext context) {
        double weight = weightField.retrieve(x, y, z) * totalWeightField.retrieve(x, y, z);
        surfaceDensities[index] += shaper.surfaceDensity(x + minX, y + minY, z + minZ, weight, context) * weight;
    }
}
