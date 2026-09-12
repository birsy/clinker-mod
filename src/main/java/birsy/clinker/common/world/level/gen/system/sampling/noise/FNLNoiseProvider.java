package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.util.RandomSource;

import java.util.function.Supplier;

public class FNLNoiseProvider {
    public static NoiseProvider.MemoizedNoiseProvider create(String name, Supplier<FastNoiseLite> provider) {
        return new NoiseProvider.MemoizedNoiseProvider(
                name, noiseRandom -> new FNLNoiseSampler(noiseRandom, provider)
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
        FNLNoiseSampler(RandomSource noiseRandom, Supplier<FastNoiseLite> provider) {
            this(provider.get());
            noise.SetSeed(noiseRandom.nextInt());
        }

        @Override public double sample(double x, double y, double z) { return noise.GetNoise(x, y, z); }
        @Override public double sample(double x, double z) { return noise.GetNoise(x, z); }
    }
}
