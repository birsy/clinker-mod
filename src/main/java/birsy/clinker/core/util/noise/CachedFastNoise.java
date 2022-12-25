package birsy.clinker.core.util.noise;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CachedFastNoise {
    private Cache<FastNoiseLite.Vector3, Double> cache;
    private final FastNoiseLite noise;

    public CachedFastNoise(FastNoiseLite noise) {
        this.noise = noise;
        this.cache = CacheBuilder.newBuilder().initialCapacity(4096).maximumSize(16384).build();
    }

    public FastNoiseLite getNoise() {
        return noise;
    }

    public void invalidateCache() {
        this.cache.invalidateAll();
    }

    public double get(double x, double y, double z) {
        double n = noise.GetNoise(x, y, z);
        cache.put(new FastNoiseLite.Vector3(x, y, z), n);
        return n;
    }
}
