package birsy.clinker.common.world.level.gen.system.sampling.noise;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public interface NoiseProvider {
    String name();
    NoiseSampler fromRandom(RandomSource random);

    record MemoizedNoiseProvider(String name, Function<RandomSource, NoiseSampler> provider) implements NoiseProvider {

        public MemoizedNoiseProvider(String name, Function<RandomSource, NoiseSampler> provider) {
            this.name = name;
            this.provider = Util.memoize(provider);
        }
        @Override
        public NoiseSampler fromRandom(RandomSource random) {
            return provider.apply(random);
        }
    }
}