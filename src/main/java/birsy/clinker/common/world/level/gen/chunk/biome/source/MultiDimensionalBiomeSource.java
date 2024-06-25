package birsy.clinker.common.world.level.gen.chunk.biome.source;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.*;

import java.util.*;
import java.util.stream.Stream;

public class MultiDimensionalBiomeSource extends BiomeSource implements BiomeProvider {
    // placeholder codec :P
    public static final Codec<MultiDimensionalBiomeSource> CODEC = Biome.CODEC.fieldOf("biome")
            .xmap((biomeHolder) -> new MultiDimensionalBiomeSource(new SingleBiomeProvider(biomeHolder), OverlapResolutionMethod.FIRST_COME_FIRST_SERVE),
                    multiDimensionalBiomeSource -> ((SingleBiomeProvider) multiDimensionalBiomeSource.defaultBiomeProvider).biome).stable().codec();

    protected final BiomeProvider defaultBiomeProvider;
    protected final Map<String, Dimension> dimensions = new HashMap<>();
    protected final List<BiomeSelector> biomeSelectors = new ArrayList<>();
    protected final OverlapResolutionMethod overlapResolutionMethod;

    public MultiDimensionalBiomeSource(BiomeProvider defaultBiomeProvider, OverlapResolutionMethod overlapResolutionMethod) {
        this.defaultBiomeProvider = defaultBiomeProvider;
        this.overlapResolutionMethod = overlapResolutionMethod;
    }

    public MultiDimensionalBiomeSource defineDimension(String dimensionIdentifier, NoiseProvider provider, float minimumValue, float maximumValue) {
        this.dimensions.put(dimensionIdentifier, new Dimension(dimensionIdentifier, provider, minimumValue, maximumValue));
        return this;
    }


    public MultiDimensionalBiomeSource addBiome(BiomeSelector selector) {
        this.biomeSelectors.add(selector);
        return this;
    }

    @Override
    protected Codec<? extends MultiDimensionalBiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.getPossibleBiomes().stream();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
        return this.getBiome(pX * 4, pY * 4, pZ * 4);
    }


    @Override
    public Holder<Biome> getBiome(double x, double y, double z) {
        Map<String, Float> valueCache = new HashMap<>();

        switch (this.overlapResolutionMethod) {
            case FIRST_COME_FIRST_SERVE: {
                biomes:
                for (BiomeSelector biomeSelector : biomeSelectors) {
                    for (BiomeRange range : biomeSelector.ranges.values()) {
                        float noiseValue = valueCache.getOrDefault(range.dimensionIdentifier,
                                (float) dimensions.get(range.dimensionIdentifier).provider().apply(x, y, z));
                        if (!range.evaluate(noiseValue)) {
                            continue biomes;
                        }
                    }
                    return biomeSelector.provider.getBiome(x, y, z);
                }
                break;
            }

            case VORONOI: {
                List<BiomeSelector> potentialBiomes = new ArrayList<>();

                biomes:
                for (BiomeSelector biomeSelector : biomeSelectors) {
                    for (BiomeRange range : biomeSelector.ranges.values()) {
                        float noiseValue = valueCache.getOrDefault(range.dimensionIdentifier,
                                (float) dimensions.get(range.dimensionIdentifier).provider().apply(x, y, z));
                        if (!range.evaluate(noiseValue)) {
                            continue biomes;
                        }
                    }

                    potentialBiomes.add(biomeSelector);
                }

                if (potentialBiomes.size() == 1) return potentialBiomes.get(0).provider.getBiome(x, y, z);

                float minDistance = Float.MAX_VALUE;
                BiomeSelector biomeSelector = potentialBiomes.get(0);
                for (BiomeSelector potentialBiome : potentialBiomes) {
                    float distance = 0;

                    for (Map.Entry<String, Float> entry : valueCache.entrySet()) {
                        Dimension dimension = dimensions.get(entry.getKey());
                        float val = potentialBiome.biomeRange(dimension).center - entry.getValue();
                        distance += val * val;
                    }

                    if (distance < minDistance) {
                        minDistance = distance;
                        biomeSelector = potentialBiome;
                    }
                }

                return biomeSelector.provider.getBiome(x, y, z);
            }
        }

        return this.defaultBiomeProvider.getBiome(x, y, z);
    }

    @Override
    public Set<Holder<Biome>> getPossibleBiomes() {
        Set<Holder<Biome>> biomes = new HashSet<>();
        biomes.addAll(this.defaultBiomeProvider.getPossibleBiomes());
        for (BiomeSelector biomeSelector : biomeSelectors) {
            biomes.addAll(biomeSelector.provider.getPossibleBiomes());
        }
        return biomes;
    }

    public static class BiomeSelector {
        protected final BiomeProvider provider;
        protected final Map<String, BiomeRange> ranges = new HashMap<>();

        public BiomeSelector(BiomeProvider provider) {
            this.provider = provider;
        }

        public BiomeSelector(Holder<Biome> biome) {
            this(new SingleBiomeProvider(biome));
        }

        public BiomeSelector defineRange(String dimensionIdentifier, float minimumValue, float maximumValue, float center) {
            ranges.put(dimensionIdentifier, new BiomeRange(dimensionIdentifier, minimumValue, maximumValue, center));
            return this;
        }

        public BiomeSelector defineRange(String dimensionIdentifier, float minimumValue, float maximumValue) {
            return this.defineRange(dimensionIdentifier, minimumValue, maximumValue, (minimumValue + maximumValue) / 2.0F);
        }

        public BiomeSelector defineRange(String dimensionIdentifier, float center) {
            return this.defineRange(dimensionIdentifier, Float.MIN_VALUE, Float.MAX_VALUE, center);
        }

        public BiomeRange biomeRange(Dimension dimension) {
            return this.ranges.getOrDefault(dimension.dimensionIdentifier(), new BiomeRange(dimension.dimensionIdentifier(),
                    dimension.minimumValue(),
                    dimension.maximumValue(),
                    (dimension.minimumValue() + dimension.maximumValue()) * 0.5F));
        }
    }

    public record BiomeRange(String dimensionIdentifier, float minimumValue, float maximumValue, float center) {
        public boolean evaluate(float dimensionValue) {
            return dimensionValue > minimumValue && dimensionValue < maximumValue;
        }
    }

    private record Dimension(String dimensionIdentifier, NoiseProvider provider, float minimumValue, float maximumValue) {}

    public interface NoiseProvider {
        double apply(double x, double y, double z);
    }

    public enum OverlapResolutionMethod {
        FIRST_COME_FIRST_SERVE, // faster
        VORONOI // handles arbitrary input better
    }
}
