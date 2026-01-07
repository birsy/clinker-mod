package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.*;

public class SurfaceShaperSystem {
    static final int BIOME_BLUR_KERNEL_SIZE = 5;
    static final double HALF_BIOME_BLUR_KERNEL_SIZE = BIOME_BLUR_KERNEL_SIZE / 2.0;
    static final double[] BIOME_BLUR_KERNEL = Util.make(() -> {
        double gamma = HALF_BIOME_BLUR_KERNEL_SIZE / 3.0;
        double[] array = new double[BIOME_BLUR_KERNEL_SIZE * BIOME_BLUR_KERNEL_SIZE];
        for (int kX = 0; kX < BIOME_BLUR_KERNEL_SIZE; kX++) {
            double kernelX = kX + 0.5;
            for (int kZ = 0; kZ < BIOME_BLUR_KERNEL_SIZE; kZ++) {
                double kernelZ = kZ + 0.5;
                double distanceToCenter = Math.sqrt(
                        (kernelX - HALF_BIOME_BLUR_KERNEL_SIZE) * (kernelX - HALF_BIOME_BLUR_KERNEL_SIZE) +
                                (kernelZ - HALF_BIOME_BLUR_KERNEL_SIZE) * (kernelZ - HALF_BIOME_BLUR_KERNEL_SIZE)
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
                                           Set<Holder<Biome>> surfaceBiomesInChunk,
                                           Collection<WorldFeature> worldFeaturesInChunk,
                                           NoiseField baseSurfaceHeight,
                                           int minSurfaceHeight, int maxSurfaceHeight,
                                           int minX, int minY, int minZ, int chunkHeight) {
        NoiseField[] biomeWeightFields = new NoiseField[this.biomeToIndex.size()];
        NoiseField totalWeightField = NoiseFieldTypes.COARSE_2D.create(chunkHeight, 0);
        double[] totalWeightFieldArray = totalWeightField.array();
        for (Holder<Biome> biomeHolder : surfaceBiomesInChunk) {
            NoiseField biomeWeightField = NoiseFieldTypes.COARSE_2D.create(chunkHeight, 0);
            double[] biomeWeightFieldArray = biomeWeightField.array();
            biomeWeightField.byBlock((index, x, y, z) -> {
                double total = 0;
                int blurIndex = 0;
                for (int oZ = 0; oZ < BIOME_BLUR_KERNEL_SIZE; oZ++) {
                    double offsetZ = z + (oZ - HALF_BIOME_BLUR_KERNEL_SIZE) * 4 + minZ;
                    for (int oX = 0; oX < BIOME_BLUR_KERNEL_SIZE; oX++) {
                        double offsetX = x + (oX - HALF_BIOME_BLUR_KERNEL_SIZE) * 4 + minX;
                        Holder<Biome> neighborBiome = biomeSource.getSurfaceBiome(
                                QuartPos.fromBlock((int) offsetX),
                                QuartPos.fromBlock((int) offsetZ)
                        );
                        if (neighborBiome.is(biomeHolder)) total += BIOME_BLUR_KERNEL[blurIndex];
                        blurIndex++;
                    }
                }
                biomeWeightFieldArray[index] = total;
            });
            biomeWeightFields[biomeToIndex.get(biomeHolder)] = biomeWeightField;
            totalWeightField.byIndex((index) -> totalWeightFieldArray[index] += biomeWeightFieldArray[index]);
        }
        totalWeightField.byIndex((index) -> {
            double weight = totalWeightFieldArray[index];
            totalWeightFieldArray[index] = weight > 0 ? 1.0 / totalWeightFieldArray[index] : 0;
        });

        // determine bounds
        int lowerBound = Integer.MAX_VALUE, upperBound = Integer.MIN_VALUE;
        for (Holder<Biome> biomeHolder : surfaceBiomesInChunk) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            lowerBound = Math.min(lowerBound, shaper.lowerBound());
            upperBound = Math.max(upperBound, shaper.upperBound());
        }
        // clamp
        lowerBound = Math.max(minSurfaceHeight + lowerBound, minY);
        upperBound = Math.min(maxSurfaceHeight + lowerBound, chunkHeight-1);

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
        for (Holder<Biome> biomeHolder : surfaceBiomesInChunk) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            shaper.prefillNoiseFields(cache, lowerBound, upperBound);
        }
        for (Holder<Biome> biomeHolder : surfaceBiomesInChunk) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            NoiseField biomeWeightField = biomeWeightFields[biomeToIndex.get(biomeHolder)];
            surfaceDensityField.byBlock(lowerBound - minY, upperBound - minY,
                    (index, x, y, z) -> {
                        double weight = biomeWeightField.retrieve(x, y, z) * totalWeightField.retrieve(x, y, z);
                        surfaceDensityFieldArray[index] =
                                shaper.surfaceDensity(x + minX, y + minY, z + minZ, weight, cache.context);
                    }
            );
        }

        // modify w/ world features
        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifySurfaceDensityField(minX, minY, minZ, cache, surfaceDensityField);
        
        return surfaceDensityField;
    }
}
