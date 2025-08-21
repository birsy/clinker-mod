package birsy.clinker.common.world.level.gen.noiseprovider;

import net.minecraft.resources.ResourceLocation;

public record NoiseComputer(ResourceLocation identifier, CacheType cacheType, NoiseFunction noiseFunction) {
    public NoiseComputer(ResourceLocation identifier, CacheType cacheType, NoiseComputer existingNoiseProcessor) {
        this(identifier, cacheType, existingNoiseProcessor.noiseFunction);
    }

    public double compute(int x, int y, int z, NoiseCache noiseCache) {
        return this.noiseFunction.compute(x, y, z, noiseCache);
    }

    public interface NoiseFunction {
        double compute(int x, int y, int z, NoiseCache noiseCache);
    }
}
