package birsy.clinker.common.world.level.gen.noise;

public record NoiseComputer(String identifier, CacheType cacheType, NoiseFunction noiseFunction) {
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
