package birsy.clinker.common.world.level.gen.system.noise;

import java.util.function.Supplier;

public final class NoiseContext implements NoiseProvider {
    private int minY, maxY;
    final NoiseFieldCache cache;
    final NoiseProvider noiseProvider;

    public NoiseContext(NoiseFieldCache cache) {
        this.cache = cache;
        this.noiseProvider = cache.noiseHolder;
    }

    public void setRange(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    public double retrieve(NoiseComputer computer, int x, int y, int z) {
        return cache.fieldCache[computer.id].retrieve(x - cache.minX, y - cache.minY, z - cache.minZ);
    }

    public double retrieve(Supplier<NoiseComputer> computer, int x, int y, int z) {
        return retrieve(computer.get(), x, y, z);
    }

    @Override
    public double sample(String name, double x, double y, double z) {
        return noiseProvider.sample(name, x, y, z);
    }

    @Override
    public double sample(String name, double x, double y) {
        return noiseProvider.sample(name, x, y);
    }

    public double minY() {
        return minY;
    }

    public double maxY() {
        return maxY;
    }
}
