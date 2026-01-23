package birsy.clinker.common.world.level.gen.system.surface.shaper;

import birsy.clinker.common.world.level.gen.system.VerticalRange;
import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.*;

public class SurfaceShaperSystem {
    static final SurfaceShaper DEFAULT = new DefaultSurfaceShaper();
    final Object2IntMap<Holder<Biome>> biomeToBiomeId;
    final Object2ObjectOpenHashMap<Holder<Biome>, SurfaceShaper> biomeToShaper;
    final Long2ObjectMap<SurfaceHeightInfo> cachedInfoByChunk = new Long2ObjectOpenHashMap<>();

    public SurfaceShaperSystem(HolderGetter<Biome> biomeGetter, Object2IntMap<Holder<Biome>> biomeToBiomeId) {
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

        this.biomeToBiomeId = biomeToBiomeId;
    }

    SurfaceShaper getSurfaceShaper(Holder<Biome> biome) {
        return biomeToShaper.getOrDefault(biome, DEFAULT);
    }

    public SurfaceHeightInfo generateSurfaceHeightAndAddToCache(NoiseFieldCache cache,
                                            Collection<WorldFeature> worldFeaturesInChunk,
                                            BiomeCache2d surfaceBiomeCache,
                                            BiomeBlender.BiomeBlendingInfo surfaceBlendingInfo,
                                            ChunkPos chunkPos) {
        int minX = chunkPos.getMinBlockX(), minZ = chunkPos.getMinBlockZ();
        NoiseField surfaceHeightField = NoiseFieldTypes.COARSE_2D.create(0, 0);
        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = surfaceBlendingInfo.weightForBiome(this.biomeToBiomeId, biome);
            SurfaceShaper shaper = getSurfaceShaper(biome);
            shaper.fillSurfaceHeightField(surfaceHeightField, cache, minX, minZ, biomeWeightField);
        }
        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifySurfaceHeight(minX, minZ, cache, surfaceHeightField);

        double[] surfaceHeightFieldArray = surfaceHeightField.array();
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (double height : surfaceHeightFieldArray) {
            if (height > max) max = height;
            if (height < min) min = height;
        }

        SurfaceHeightInfo info = new SurfaceHeightInfo(surfaceHeightField, Mth.floor(min), Mth.ceil(max));
        synchronized (cachedInfoByChunk) {
            cachedInfoByChunk.put(chunkPos.toLong(), info);
        }
        return new SurfaceHeightInfo(surfaceHeightField, Mth.floor(min), Mth.ceil(max));
    }

    public SurfaceHeightInfo retrieveSurfaceHeightInfo(ChunkPos chunkPos) {
        long key = chunkPos.toLong();
        SurfaceHeightInfo info;
        synchronized (cachedInfoByChunk) {
            info = cachedInfoByChunk.get(key);
        }
        return info;
    }

    public SurfaceHeightInfo retrieveSurfaceHeightInfoAndRemoveFromCache(ChunkPos chunkPos) {
        long key = chunkPos.toLong();
        SurfaceHeightInfo info;
        synchronized (cachedInfoByChunk) {
            info = cachedInfoByChunk.get(key);
            cachedInfoByChunk.remove(key);
        }
        return info;
    }

    public NoiseField generateSurfaceDensity(NoiseFieldCache cache,
                                             Collection<WorldFeature> worldFeaturesInChunk,
                                             BiomeCache2d surfaceBiomeCache,
                                             BiomeBlender.BiomeBlendingInfo surfaceBlendingInfo,
                                             SurfaceHeightInfo surfaceHeightInfo,
                                             int minX, int minY, int minZ, int chunkHeight) {
        // determine bounds
        int lowerBound = Integer.MAX_VALUE, upperBound = Integer.MIN_VALUE;
        for (Holder<Biome> biomeHolder : surfaceBiomeCache.containedBiomes()) {
            SurfaceShaper shaper = getSurfaceShaper(biomeHolder);
            lowerBound = Math.min(lowerBound, shaper.lowerBound());
            upperBound = Math.max(upperBound, shaper.upperBound());
        }
        lowerBound = Math.max(surfaceHeightInfo.minimum + lowerBound, minY);
        upperBound = Math.min(surfaceHeightInfo.maximum + upperBound, chunkHeight-1);

        NoiseField surfaceHeight = surfaceHeightInfo.surfaceHeight;
        // initialize surface density w/ estimate from base surface height
        NoiseField surfaceDensityField = NoiseFieldTypes.COARSE.create(chunkHeight, 0);
        double[] surfaceDensityFieldArray = surfaceDensityField.array();
        Arrays.fill(surfaceDensityFieldArray, 0);
        surfaceDensityField.byBlock(0, lowerBound - minY - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = (y + minY) - surfaceHeight.retrieve(x, y, z)
        );
        surfaceDensityField.byBlock(upperBound - minY + 1, chunkHeight - 1,
                (index, x, y, z) -> surfaceDensityFieldArray[index] = (y + minY) - surfaceHeight.retrieve(x, y, z)
        );

        for (Holder<Biome> biome : surfaceBiomeCache.containedBiomes()) {
            NoiseField biomeWeightField = surfaceBlendingInfo.weightForBiome(this.biomeToBiomeId, biome);
            SurfaceShaper shaper = getSurfaceShaper(biome);
            shaper.fillSurfaceDensityField(surfaceDensityField, cache, minX, minY, minZ, surfaceHeight, lowerBound, upperBound, biomeWeightField);
        }

        for (WorldFeature worldFeature : worldFeaturesInChunk)
            worldFeature.modifySurfaceDensityField(minX, minY, minZ, cache, surfaceDensityField);

        return surfaceDensityField;
    }

    public record SurfaceHeightInfo(NoiseField surfaceHeight, int minimum, int maximum) {}
}
