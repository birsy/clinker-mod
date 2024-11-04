package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noiseproviders.NoiseProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NoiseCache {
    final NoiseField field;
    final Map<NoiseProvider, float[]> cacheByProvider;
    long seed;

    public NoiseCache(NoiseField field, long seed) {
        this.field = field;
        this.seed = seed;
        cacheByProvider = new HashMap<>(4);
    }

    public void invalidateCaches() {
        this.cacheByProvider.clear();
    }

    public void setSeed(long seed) {
        if (this.seed != seed) this.invalidateCaches();
        this.seed = seed;
    }

    protected float[] createNewCache() {
        float[] cache = new float[field.xResolution() * field.yResolution() * field.zResolution()];
        Arrays.fill(cache, Float.NaN);
        return cache;
    }

    protected int index(double x, double y, double z, float[] cachedValues) {
        if (field instanceof NoiseFieldWithOffset offsetField)
            return field.index((int) offsetField.worldToLocalX(x), (int)  offsetField.worldToLocalY(y), (int)  offsetField.worldToLocalZ(z));
        return field.index((int) x, (int) y, (int) z);
    }

    public float sample(double x, double y, double z, NoiseProvider provider) {
        if (!cacheByProvider.containsKey(provider)) cacheByProvider.put(provider, createNewCache());
        float[] cache = cacheByProvider.get(provider);
        int index = index(x, y, z, cache);

        if (Float.isNaN(cache[index])) cache[index] = sampleUncached(x, y, z, provider);
        return cache[index];
    }

    public float sampleUncached(double x, double y, double z, NoiseProvider provider) {
        provider.setSeed(seed);
        return provider.sample(x, y, z);
    }
}
