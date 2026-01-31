package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.biome.BiomeBlender;
import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.biome.BiomeList;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeatureContext;
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
                                                    Collection<WorldFeature> worldFeaturesInChunk,
                                                    BiomeCache2d surfaceBiomeCache,
                                                    BiomeBlender.ChunkBiomeBlendingWeights surfaceBlendingInfo,
                                                    WorldFeatureContext context,
                                                    int minX, int minZ, int padding) {
        return this.generateHeightmapInternal(cache, worldFeaturesInChunk, surfaceBiomeCache, surfaceBlendingInfo, context, minX, minZ, padding);
    }

    public ChunkSurfaceHeightmap generateHeightmap(NoiseFieldCache cache,
                                                   Collection<WorldFeature> worldFeaturesInChunk,
                                                   BiomeCache2d surfaceBiomeCache,
                                                   BiomeBlender.ChunkBiomeBlendingWeights surfaceBlendingInfo,
                                                   WorldFeatureContext context,
                                                   int minX, int minZ) {
        return this.generateHeightmapInternal(cache, worldFeaturesInChunk, surfaceBiomeCache, surfaceBlendingInfo, context, minX, minZ, 0);
    }

    private ChunkSurfaceHeightmap generateHeightmapInternal(NoiseFieldCache cache,
                                                   Collection<WorldFeature> worldFeaturesInChunk,
                                                   BiomeCache2d surfaceBiomeCache,
                                                   BiomeBlender.ChunkBiomeBlendingWeights surfaceBlendingInfo,
                                                    WorldFeatureContext context,
                                                   int minX, int minZ, int padding) {
        NoiseField heightmapField = NoiseFieldTypes.COARSE_2D.create(0, padding);
        double[] heightmapArray = heightmapField.array();
        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = surfaceBlendingInfo.weightForBiome(biomeList, biome);
            if (biomeWeightField == null) continue;
            SurfaceShaper shaper = getSurfaceShaper(biome);

            shaper.prefillHeightmapNoiseFields(cache);
            heightmapField.byBlockPadded(
                    (index, x, y, z) -> {
                        double weight = biomeWeightField.retrieve(x, y, z);
                        heightmapArray[index] += shaper.getHeight(x + minX, z + minZ, weight, cache.context);
                    }
            );
        }

        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifyHeightmap(minX, minZ, cache, heightmapField, context);

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (double height : heightmapArray) {
            if (height > max) max = height;
            if (height < min) min = height;
        }

        return new ChunkSurfaceHeightmap(heightmapField, Mth.floor(min), Mth.ceil(max));
    }

    public NoiseField generateHeightmapGradientSquaredField(NoiseField heightmap) {
        NoiseField gradientMap = NoiseFieldTypes.COARSE_2D.create(0, 0);

        int min = 0 - heightmap.paddingBlocks, max = 15 + heightmap.paddingBlocks;
        double[] gradientMapArray = gradientMap.array();
        gradientMap.byBlockPadded((index, x, y, z) -> {
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
        approxDistance.byBlockPadded((index, x, y, z) -> {
            double heightmapValue = heightmap.retrieve(x, y, z),
                   squaredHeightmapGradientValue = squaredHeightmapGradient.retrieve(x, y, z);
            approxDistanceArray[index] = ((y + minY) - heightmapValue) / Math.sqrt(1.0 + squaredHeightmapGradientValue);
        });
        return approxDistance;
    }

    public NoiseField generateSurfaceDensity(NoiseFieldCache cache,
                                             Collection<WorldFeature> worldFeaturesInChunk,
                                             BiomeCache2d surfaceBiomeCache,
                                             BiomeBlender.ChunkBiomeBlendingWeights surfaceBlendingInfo,
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
        NoiseField heightmap = heightmapInfo.field;
        NoiseField heightmapGradient = NoiseFieldTypes.COARSE_2D.create(0, 0);
        double[] heightmapGradientArray = heightmapGradient.array();
        double[] squaredHeightmapGradientArray = squaredHeightmapGradient.array();
        heightmapGradient.byIndex((index) -> heightmapGradientArray[index] = Math.sqrt(squaredHeightmapGradientArray[index]));

        // initialize surface density w/ estimate from base surface height
        NoiseField surfaceDensityField = NoiseFieldTypes.FINE.create(chunkHeight, 0);
        double[] surfaceDensityFieldArray = surfaceDensityField.array();
        Arrays.fill(surfaceDensityFieldArray, 0);
        surfaceDensityField.byBlockPadded(0, lowerBound - minY - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = distanceToHeightmap.retrieve(x, y, z)
        );
        surfaceDensityField.byBlockPadded(upperBound - minY + 1, chunkHeight - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = distanceToHeightmap.retrieve(x, y, z)
        );

        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = surfaceBlendingInfo.weightForBiome(biomeList, biome);
            if (biomeWeightField == null) continue;
            SurfaceShaper shaper = getSurfaceShaper(biome);
            shaper.fillSurfaceDensityField(surfaceDensityField, cache, chunkHeight, minX, minY, minZ, heightmap, heightmapGradient, distanceToHeightmap, lowerBound, upperBound, biomeWeightField);
        }

        this.createRockyCliffsOnSteepSlopes(cache, heightmapGradient, distanceToHeightmap, surfaceBlendingInfo.biomeTransitionFactorField(), surfaceDensityField, lowerBound, upperBound, minX, minY, minZ, chunkHeight);

        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifySurfaceDensityField(minX, minY, minZ, cache, surfaceDensityField, worldContext);

        return surfaceDensityField;
    }

    // whenever there are big discontinuities in the heightmap near biome transitions,
    // surface shapers can't really handle that...
    // so we add on some rocky cliff textures to help make it look less smooth and 1.12 biomes o' plenty mountains-y
    private void createRockyCliffsOnSteepSlopes(NoiseFieldCache cache,
                                                NoiseField heightmapGradientField, NoiseField distanceToHeightmapField,
                                                NoiseField biomeTransitionFactorField,
                                                NoiseField surfaceDensityField,
                                                int lowerSurfaceBound, int upperSurfaceBound,
                                                int minX, int minY, int minZ, int chunkHeight) {
        NoiseField cliffRockField = cache.fillNoiseField(lowerSurfaceBound, upperSurfaceBound, ClinkerNoiseComputers.CLIFF_ROCKS);
        double cliffSize = 6;
        double[] surfaceDensityFieldArray = surfaceDensityField.array();
        surfaceDensityField.byBlockPadded(lowerSurfaceBound - minY, upperSurfaceBound - minY,
                (index, x, y, z) -> {
                    // make sure it only occurs when the heightmap is steep
                    double gradientMask = Mth.clampedMap(heightmapGradientField.retrieve(x, y, z), 1, 6, 0, 1);
                    // make sure it only occurs near the ground
                    double distanceToHeightmapMask = Mth.clampedMap(Math.abs(distanceToHeightmapField.retrieve(x, y, z)), cliffSize * 0.5, cliffSize, 1, 0);
                    // make sure it only occurs between biomes. surface shapers can take care of intra-biome cliffs
                    double biomeTransitionMask = Math.clamp(biomeTransitionFactorField.retrieve(x, y, z), 0, 1);

                    double cliffRock = cliffRockField.retrieve(x, y, z);
                    cliffRock = Math.round(cliffRock * 3.0) / 3.0;
                    cliffRock = ((Math.abs(cliffRock) * -2 + 1) - 0.3) * cliffSize;

                    double cliffs = cliffRock * biomeTransitionMask * gradientMask * distanceToHeightmapMask * 1.3;

                    surfaceDensityFieldArray[index] += cliffs;
                }
        );
    }

    public record ChunkSurfaceHeightmap(NoiseField field, int minimum, int maximum) {}
}
