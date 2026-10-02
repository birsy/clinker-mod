package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public interface NoiseProvider {
    ResourceLocation NOISE_GEN_SEED = Clinker.resource("clinkernoisegen");
    String name();
    NoiseSampler fromRandom(RandomSource random);

    record SimpleNoiseProvider(String name, Function<RandomSource, NoiseSampler> provider) implements NoiseProvider {
        @Override
        public NoiseSampler fromRandom(RandomSource random) {
            return provider.apply(random);
        }
    }

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