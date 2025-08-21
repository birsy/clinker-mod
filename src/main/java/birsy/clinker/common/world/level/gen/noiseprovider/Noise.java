package birsy.clinker.common.world.level.gen.noiseprovider;

import net.minecraft.resources.ResourceLocation;

public class Noise {
    protected final CacheType cacheType;
    protected final ResourceLocation identifier;
    protected final NoiseFunction noiseFunction;

    public Noise(ResourceLocation identifier, CacheType cacheType, NoiseFunction noiseFunction) {
        this.cacheType = cacheType;
        this.identifier = identifier;
        this.noiseFunction = noiseFunction;
    }

    public Noise(ResourceLocation identifier, CacheType cacheType, Noise existingNoise) {
        this(identifier, cacheType, existingNoise.noiseFunction);
    }

    public interface NoiseFunction {
        double compute(int x, int y, int z, NoiseProvider noiseProvider);
    }
}
