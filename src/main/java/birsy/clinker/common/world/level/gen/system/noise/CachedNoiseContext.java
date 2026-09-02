package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator;

public final class CachedNoiseContext implements NoiseContext {
    private int minY, maxY;
    final NoiseFieldCache cache;
    final NoiseProvider noiseProvider;

    public CachedNoiseContext(NoiseFieldCache cache) {
        this.cache = cache;
        this.noiseProvider = cache.noiseHolder;
    }

    public void setRange(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public double retrieve(Synthesizer computer, int x, int y, int z) {
        return cache.fieldCache[computer.id].retrieve(x - cache.minX, y - cache.minY, z - cache.minZ);
    }

    @Override
    public VoronoiEvaluator getVoronoi(String name) {
        return cache.voronoiEvaluators.get(name);
    }

    @Override
    public double sample(String name, double x, double y, double z) {
        return noiseProvider.sample(name, x, y, z);
    }

    @Override
    public double sample(String name, double x, double y) {
        return noiseProvider.sample(name, x, y);
    }

    @Override
    public int yRangeStart() {
        return minY;
    }
    @Override
    public int yRangeEnd() {
        return maxY;
    }
}
