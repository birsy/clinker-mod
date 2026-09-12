package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.util.RandomSource;

import java.util.function.Supplier;

public class WhiteNoiseProvider {
    public static NoiseProvider.MemoizedNoiseProvider create(String name, double scale) {
        return new NoiseProvider.MemoizedNoiseProvider(name, noiseRandom -> new WhiteNoiseSampler(scale, noiseRandom));
    }
    public static NoiseProvider.MemoizedNoiseProvider create(String name) {
        return create(name, 1.0);
    }

    private record WhiteNoiseSampler(double scale, long seed) implements NoiseSampler {
        private static final long PRIME = 0xd6e8feb86659fd93L;

        WhiteNoiseSampler(double scale, RandomSource noiseRandom) {
            this(scale, noiseRandom.nextLong());
        }

        @Override
        public double sample(double x, double y, double z) {
            long ix = (long) Math.floor(x * scale),
                 iy = (long) Math.floor(y * scale),
                 iz = (long) Math.floor(z * scale);

            long hash = seed ^ PRIME;
            hash = (hash ^ ix) * PRIME;
            hash = (hash ^ iy) * PRIME;
            hash = (hash ^ iz) * PRIME;

            hash ^= hash >>> 33;
            hash *= 0xff51afd7ed558ccdL;
            hash ^= hash >>> 33;

            double zeroToOne = (double) (hash & 0x001fffffffffffffL) / 9007199254740991.0;
            return zeroToOne * 2.0 - 1.0;
        }
        @Override
        public double sample(double x, double z) {
            long ix = (long) Math.floor(x * scale),
                 iz = (long) Math.floor(z * scale);

            long hash = seed ^ PRIME;
            hash = (hash ^ ix) * PRIME;
            hash = (hash ^ iz) * PRIME;

            hash ^= hash >>> 33;
            hash *= 0xff51afd7ed558ccdL;
            hash ^= hash >>> 33;

            double zeroToOne = (double) (hash & 0x001fffffffffffffL) / 9007199254740991.0;
            return zeroToOne * 2.0 - 1.0;
        }
    }
}
