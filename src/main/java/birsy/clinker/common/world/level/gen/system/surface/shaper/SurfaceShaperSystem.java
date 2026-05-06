package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.biome.BiomeBlender;
import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.biome.BiomeList;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesHeightmap;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesSurfaceDensity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.util.MathUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.*;

public class SurfaceShaperSystem {
    static final SurfaceShaper DEFAULT = new DefaultSurfaceShaper();
    final BiomeList biomeList;

    final Object2ObjectOpenHashMap<Holder<Biome>, SurfaceShaper> biomeToShaper;

    public SurfaceShaperSystem(HolderGetter<Biome> biomeGetter, BiomeList biomeList) {
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

        this.biomeList = biomeList;
    }

    SurfaceShaper getSurfaceShaper(Holder<Biome> biome) {
        return biomeToShaper.getOrDefault(biome, DEFAULT);
    }

    public double getHeight(double[] biomeBlendingWeights, int x, int z, UncachedNoiseContext context) {
        double height = 0;
        for (int i = 0; i < biomeBlendingWeights.length; i++) {
            double weight = biomeBlendingWeights[i];
            if (weight == 0) continue;
            Holder<Biome> biome = biomeList.byId(i);
            SurfaceShaper shaper = getSurfaceShaper(biome);
            height += shaper.getHeight(x, z, weight, context);
        }
        return height;
    }

    public ChunkSurfaceHeightmap generateHeightmap(PaddedNoiseFieldCache cache,
                                                   List<ModifiesHeightmap> heightmapModifyingWorldFeatures,
                                                   BiomeCache2d surfaceBiomeCache,
                                                   BiomeBlender.ChunkBiomeBlendingInfo surfaceBlendingInfo,
                                                   WorldFeatureContext context,
                                                   int minX, int minZ, int padding) {
        return this.generateHeightmapInternal(cache, heightmapModifyingWorldFeatures, surfaceBiomeCache, surfaceBlendingInfo, context, minX, minZ, padding);
    }

    public ChunkSurfaceHeightmap generateHeightmap(NoiseFieldCache cache,
                                                   List<ModifiesHeightmap> heightmapModifyingWorldFeatures,
                                                   BiomeCache2d surfaceBiomeCache,
                                                   BiomeBlender.ChunkBiomeBlendingInfo surfaceBlendingInfo,
                                                   WorldFeatureContext context,
                                                   int minX, int minZ) {
        return this.generateHeightmapInternal(cache, heightmapModifyingWorldFeatures, surfaceBiomeCache, surfaceBlendingInfo, context, minX, minZ, 0);
    }

    private ChunkSurfaceHeightmap generateHeightmapInternal(NoiseFieldCache cache,
                                                            List<ModifiesHeightmap> heightmapModifyingWorldFeatures,
                                                            BiomeCache2d surfaceBiomeCache,
                                                            BiomeBlender.ChunkBiomeBlendingInfo surfaceBlendingInfo,
                                                            WorldFeatureContext context,
                                                            int minX, int minZ, int padding) {
        NoiseField[] biomeHeightmaps = new NoiseField[this.biomeList.maxId() + 1];
        NoiseField mergedHeightmapField = NoiseFieldTypes.COARSE_2D.create(0, padding);
        double[] mergedHeightmapArray = mergedHeightmapField.array();
        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = surfaceBlendingInfo.weightForBiome(biomeList, biome);
            if (biomeWeightField == null) continue;
            SurfaceShaper shaper = getSurfaceShaper(biome);

            NoiseField biomeHeightmapField = NoiseFieldTypes.COARSE_2D.create(0, padding);
            double[] biomeHeightmapArray = biomeHeightmapField.array();

            shaper.prefillHeightmapNoiseFields(cache);
            mergedHeightmapField.byBlock(
                    (index, x, y, z) -> {
                        double weight = biomeWeightField.retrieve(x, y, z);
                        double height = shaper.getHeight(x + minX, z + minZ, weight, cache.context);
                        biomeHeightmapArray[index] = height;
                        mergedHeightmapArray[index] += height * weight;
                    }
            );
            biomeHeightmaps[this.biomeList.getId(biome)] = biomeHeightmapField;
        }

        for (ModifiesHeightmap worldFeature : heightmapModifyingWorldFeatures)
            worldFeature.modifyHeightmap(minX, minZ, cache, mergedHeightmapField, biomeHeightmaps, context);

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (double height : mergedHeightmapArray) {
            if (height > max) max = height;
            if (height < min) min = height;
        }

        return new ChunkSurfaceHeightmap(mergedHeightmapField, biomeHeightmaps, Mth.floor(min), Mth.ceil(max));
    }

    public NoiseField generateHeightmapGradientSquaredField(NoiseField heightmap) {
        NoiseField gradientMap = NoiseFieldTypes.COARSE_2D.create(0, 0);

        int min = 0 - heightmap.paddingBlocks, max = 15 + heightmap.paddingBlocks;
        double[] gradientMapArray = gradientMap.array();
        gradientMap.byBlock((index, x, y, z) -> {
            double xHeight0 = heightmap.retrieve(Math.clamp(x - 1, min, max), 0, z),
                   xHeight1 = heightmap.retrieve(Math.clamp(x + 1, min, max), 0, z);
            double dX = (xHeight1 - xHeight0) * 0.5;
            double zHeight0 = heightmap.retrieve(x, 0, Math.clamp(z - 1, min, max)),
                   zHeight1 = heightmap.retrieve(x, 0, Math.clamp(z + 1, min, max));
            double dZ = (zHeight1 - zHeight0) * 0.5;

            // gradient squared more generally useful
            // distance scale = sqrt(1 + gradient squared)
            // gradient = sqrt(gradient squared), obviously
            gradientMapArray[index] = dX * dX + dZ * dZ;
        });
        return gradientMap;
    }

    public NoiseField generateApproximateDistanceToHeightmap(int chunkHeight, int minY, NoiseField heightmap, NoiseField squaredHeightmapGradient) {
        NoiseField approxDistance = NoiseFieldTypes.COARSE.create(chunkHeight, 0);
        double[] approxDistanceArray = approxDistance.array();
        approxDistance.byBlock((index, x, y, z) -> {
            double heightmapValue = heightmap.retrieve(x, y, z),
                   squaredHeightmapGradientValue = squaredHeightmapGradient.retrieve(x, y, z);
            approxDistanceArray[index] = ((y + minY) - heightmapValue) / Math.sqrt(1.0 + squaredHeightmapGradientValue);
        });
        return approxDistance;
    }

    public NoiseField generateSurfaceDensity(NoiseFieldCache cache,
                                             List<ModifiesSurfaceDensity> surfaceDensityModifyingWorldFeatures,
                                             BiomeCache2d surfaceBiomeCache,
                                             BiomeBlender.ChunkBiomeBlendingInfo blendingInfo,
                                             ChunkSurfaceHeightmap heightmapInfo, NoiseField squaredHeightmapGradient, NoiseField distanceToHeightmap,
                                             WorldFeatureContext worldContext,
                                             int minX, int minY, int minZ, int chunkHeight) {
        // determine bounds
        int lowerBound = -16, upperBound = 16;
        for (Holder<Biome> biomeHolder : surfaceBiomeCache.containedBiomes()) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            lowerBound = Math.min(lowerBound, shaper.lowerBound());
            upperBound = Math.max(upperBound, shaper.upperBound());
        }
        lowerBound = Math.max(heightmapInfo.minimum + lowerBound, minY);
        upperBound = Math.min(heightmapInfo.maximum + upperBound, chunkHeight-1);

        // some utility stuff for the heightmaps
        NoiseField heightmap = heightmapInfo.combinedHeightmapField;
        NoiseField heightmapGradient = NoiseFieldTypes.COARSE_2D.create(0, 0);
        double[] heightmapGradientArray = heightmapGradient.array();
        double[] squaredHeightmapGradientArray = squaredHeightmapGradient.array();
        heightmapGradient.byIndex((index) -> heightmapGradientArray[index] = Math.sqrt(squaredHeightmapGradientArray[index]));

        // initialize surface density w/ estimate from base surface height
        NoiseField surfaceDensityField = NoiseFieldTypes.FINE.create(chunkHeight, 0);
        double[] surfaceDensityFieldArray = surfaceDensityField.array();
        Arrays.fill(surfaceDensityFieldArray, 0);
        surfaceDensityField.byBlock(0, lowerBound - minY - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = distanceToHeightmap.retrieve(x, y, z)
        );
        surfaceDensityField.byBlock(upperBound - minY + 1, chunkHeight - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = distanceToHeightmap.retrieve(x, y, z)
        );

        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = blendingInfo.weightForBiome(biomeList, biome);
            if (biomeWeightField == null) continue;
            SurfaceShaper shaper = getSurfaceShaper(biome);
            shaper.fillSurfaceDensityField(surfaceDensityField, cache, chunkHeight, minX, minY, minZ, heightmap, heightmapGradient, distanceToHeightmap, lowerBound, upperBound, biomeWeightField);
        }

        if (surfaceBiomeCache.containedBiomes().size() > 1)
            this.createCliffs(cache, surfaceBiomeCache, heightmapInfo, blendingInfo, surfaceDensityField, lowerBound, upperBound, minX, minY, minZ, chunkHeight);

        for (ModifiesSurfaceDensity worldFeature : surfaceDensityModifyingWorldFeatures)
            worldFeature.modifySurfaceDensity(minX, minY, minZ, cache, surfaceDensityField, worldContext);

        return surfaceDensityField;
    }

    // whenever there are big discontinuities in the heightmap near biome transitions,
    // surface shapers can't really handle that...
    // so we add on some rocky cliff textures to help make it look less smooth and 1.12 biomes o' plenty mountains-y
    private static final int MIN_CLIFF_HEIGHT_DIFFERENCE = 10;
    private void createCliffs(NoiseFieldCache cache,
                              BiomeCache2d surfaceBiomeCache,
                              ChunkSurfaceHeightmap heightmapInfo,
                              BiomeBlender.ChunkBiomeBlendingInfo blendingInfo,
                              NoiseField surfaceDensityField,
                              int lowerSurfaceBound, int upperSurfaceBound,
                              int minX, int minY, int minZ, int chunkHeight) {
        NoiseField stratifiedYField = cache.fillNoiseField(lowerSurfaceBound, upperSurfaceBound, ClinkerNoiseComputers.CLIFF_STRATIFIED_Y);
        NoiseField cliffCracksField = cache.fillNoiseField(lowerSurfaceBound, upperSurfaceBound, ClinkerNoiseComputers.BASE_NOISE[5]);

        NoiseField borderDistanceField = NoiseFieldTypes.COARSE_2D.create(chunkHeight, 0);
        double[] borderDistanceArray = borderDistanceField.array();
        double[] surfaceDensityFieldArray = surfaceDensityField.array();
        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField distanceToBiomeEdgeField = blendingInfo.borderDistanceForBiome(this.biomeList, biome);
            NoiseField biomeHeightmapField = heightmapInfo.heightmapForBiome(this.biomeList, biome);
            if (distanceToBiomeEdgeField == null || biomeHeightmapField == null) continue;

            // create horizontal distance-to-biome map
            Arrays.fill(borderDistanceArray, 50.0);
            borderDistanceField.byBlock(
                    (index, x, y, z) -> {
                        double biomeHeight = biomeHeightmapField.retrieve(x, y, z);
                        double combinedHeight = heightmapInfo.combinedHeightmapField.retrieve(x, y, z);
                        double baseCliffRadius = Mth.clampedMap(biomeHeight - combinedHeight, 0, MIN_CLIFF_HEIGHT_DIFFERENCE, 0, 8);
                        borderDistanceArray[index] = Math.abs(distanceToBiomeEdgeField.retrieve(x, y, z)) - baseCliffRadius;
                    }
            );

            // each biome gets its own surrounding cliff!
            surfaceDensityField.byBlock(lowerSurfaceBound - minY, upperSurfaceBound - minY,
                    (index, x, y, z) -> {
                        double stratifiedY = stratifiedYField.retrieve(x, y, z);
                        stratifiedY = Mth.lerp(0.2, stratifiedY, y + minY);
                        double biomeHeight = biomeHeightmapField.retrieve(x, y, z) - 3;
                        double combinedHeight = heightmapInfo.combinedHeightmapField.retrieve(x, y, z);

                        double verticalCliffDistance = stratifiedY - biomeHeight;
                        double lateralCliffDistance = borderDistanceField.retrieve(x, y, z);
                        double baseFlare = Mth.clampedMap(stratifiedY, combinedHeight, biomeHeight, 1, 0);
                        baseFlare *= baseFlare;
                        double baseFlareRadius = Mth.clampedMap(biomeHeight - combinedHeight, 0, 30, 5, 16);
                        lateralCliffDistance -= baseFlare * baseFlareRadius;
                        double cliffDistance = -MathUtils.smoothMinExpo(-lateralCliffDistance, -verticalCliffDistance, 5);
                        double cliffCracks = Mth.clampedMap(Math.abs(cliffCracksField.retrieve(x, y, z)), 0, 0.5, 4, 0);
                        cliffDistance += cliffCracks;

                        double surfaceFieldDensity = surfaceDensityFieldArray[index];
                        double density = MathUtils.smoothMinExpo(surfaceFieldDensity, cliffDistance, 2);
                        if (Double.isNaN(density) || Double.isInfinite(density) || density > 100000 || density < -100000) {
                            Clinker.LOGGER.info("WARNING! WARNING! SHITTY DENSITY! {}", density);
                        }

                        surfaceDensityFieldArray[index] = density;
                    }
            );
        }
    }

    public record ChunkSurfaceHeightmap(NoiseField combinedHeightmapField, NoiseField[] heightmapByBiome, int minimum, int maximum) {
        @Nullable
        public NoiseField heightmapForBiome(BiomeList biomes, Holder<Biome> biome) {
            return heightmapByBiome[biomes.getId(biome)];
        }
    }
}
