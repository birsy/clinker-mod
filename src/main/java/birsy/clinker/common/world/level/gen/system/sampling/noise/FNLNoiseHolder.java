package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.Util;

import java.util.function.Function;
import java.util.function.Supplier;

public record FNLNoiseHolder(String name, Function<Long, FastNoiseLite> configuration) implements NoiseHolder {
    public FNLNoiseHolder(String name, Supplier<FastNoiseLite> configuration) {
        this(name, seed -> {
            FastNoiseLite noise = configuration.get();
            noise.SetSeed(seed.intValue());
            return noise;
        });
    }

    public FNLNoiseHolder(String name) {
        this(name, () -> {
            FastNoiseLite noise = new FastNoiseLite();
            noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
            noise.SetFractalType(FastNoiseLite.FractalType.None);
            noise.SetFrequency(1.0F);
            return noise;
        });
    }

    @Override
    public NoiseSampler fromSeed(long seed) {
        return null;
    }
}
