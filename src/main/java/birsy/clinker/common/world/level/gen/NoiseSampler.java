package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noiseproviders.NoiseProvider;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NoiseSampler {
    protected long seed;
    protected final Map<NoiseProvider, Cache<Position, Float>> noiseProviderCacheMap;
    public NoiseSampler(long seed) {
        this.seed = seed;
        this.noiseProviderCacheMap = new HashMap<>();
    }

    public void setSeed(long seed) {
        if (this.seed != seed) {
            for (Cache<Position, Float> cache : this.noiseProviderCacheMap.values()) {
                cache.invalidateAll();
            }
        }
        this.seed = seed;
    }

    // avoid changing the frequency here (multiplying x/y/z by constants); it messes up the cache.
    public float sample(double x, double y, double z, NoiseProvider provider) throws ExecutionException {
        Position pos = new Position(x, y, z);
        if (!noiseProviderCacheMap.containsKey(provider)) {
            Cache<Position, Float> cache = CacheBuilder.newBuilder().maximumSize(16 * 16 * 384).build();
            noiseProviderCacheMap.put(provider, cache);
        }

        Cache<Position, Float> cache = noiseProviderCacheMap.get(provider);
        return cache.get(pos, () -> sampleUncached(x, y, z, provider));
    }

    public float sampleUncached(double x, double y, double z, NoiseProvider provider) {
        provider.setSeed(seed);
        return provider.sample(x, y, z);
    }

    protected record Position(double x, double y, double z) {}
}
