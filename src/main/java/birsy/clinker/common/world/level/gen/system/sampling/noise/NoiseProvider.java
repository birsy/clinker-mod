package birsy.clinker.common.world.level.gen.system.sampling.noise;

import net.minecraft.Util;

import java.util.function.Function;

public interface NoiseProvider {
    String name();
    NoiseSampler fromSeed(long seed);

    record MemoizedNoiseProvider(String name, Function<Long, NoiseSampler> provider) implements NoiseProvider {
        public MemoizedNoiseProvider(String name, Function<Long, NoiseSampler> provider) {
            this.name = name;
            this.provider = Util.memoize(provider);
        }
        @Override
        public NoiseSampler fromSeed(long seed) {
            return provider.apply(seed);
        }
    }
}