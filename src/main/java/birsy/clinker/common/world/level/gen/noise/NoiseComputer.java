package birsy.clinker.common.world.level.gen.noise;

import java.util.concurrent.atomic.AtomicLong;

public record NoiseComputer(long id, String identifier, CacheType cacheType, NoiseFunction noiseFunction) {
    public static final AtomicLong atomicID = new AtomicLong(0);

    public NoiseComputer(String identifier, CacheType cacheType, NoiseFunction noiseFunction) {
        this(atomicID.getAndIncrement(), identifier, cacheType, noiseFunction);
    }

    public NoiseComputer(String identifier, CacheType cacheType, NoiseComputer existingNoiseProcessor) {
        this(identifier, cacheType, existingNoiseProcessor.noiseFunction);
    }

    public double compute(int x, int y, int z, NoiseComputerContext context) {
        return this.noiseFunction.compute(x, y, z, context);
    }

    public interface NoiseFunction {
        double compute(int x, int y, int z, NoiseComputerContext context);
    }
}
