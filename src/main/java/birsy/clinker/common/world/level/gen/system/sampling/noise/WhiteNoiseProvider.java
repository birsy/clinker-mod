package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.core.util.HashUtils;
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

    private record WhiteNoiseSampler(long seed, double scale) implements NoiseSampler {

        WhiteNoiseSampler(double scale, RandomSource noiseRandom) {
            this(noiseRandom.nextLong(), scale);
        }

        @Override
        public float sample(double x, double y, double z) {
            int cellX = (int) Math.floor(x * scale),
                cellY = (int) Math.floor(y * scale),
                cellZ = (int) Math.floor(z * scale);
            return HashUtils.hash3d(seed, cellX, cellY, cellZ);
        }
        @Override
        public float sample(double x, double z) {
            int cellX = (int) Math.floor(x * scale),
                cellZ = (int) Math.floor(z * scale);
            return HashUtils.hash2d(seed, cellX, cellZ);
        }
    }
}
