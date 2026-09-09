package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.core.util.noise.FastNoiseLite;

import java.util.function.Supplier;

public class FNLNoiseProvider {
    public static NoiseProvider.MemoizedNoiseProvider create(String name, Supplier<FastNoiseLite> provider) {
        return new NoiseProvider.MemoizedNoiseProvider(
                name, (seed) -> FNLNoiseSampler.create(seed, provider)
        );
    }

    public static NoiseProvider.MemoizedNoiseProvider create(String name) {
        return create(name, () -> {
            FastNoiseLite fnl = new FastNoiseLite();
            fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
            fnl.SetFractalType(FastNoiseLite.FractalType.None);
            fnl.SetFrequency(1.0F);
            return fnl;
        });
    }

    private record FNLNoiseSampler(FastNoiseLite noise) implements NoiseSampler {
        static FNLNoiseSampler create(long seed, Supplier<FastNoiseLite> provider) {
            FastNoiseLite noise = provider.get();
            noise.SetSeed((int) seed);
            return new FNLNoiseSampler(noise);
        }

        @Override public double sample(double x, double y, double z) { return noise.GetNoise(x, y, z); }
        @Override public double sample(double x, double z) { return noise.GetNoise(x, z); }
    }
}
