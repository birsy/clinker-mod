package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noiseproviders.NoiseProvider;
import birsy.clinker.core.util.noise.FastNoiseLite;

public class NoiseProviders {
    private static FastNoiseLite fastNoiseLite = new FastNoiseLite();
    static { fastNoiseLite.SetFrequency(1.0F); }

    public static final NoiseProvider NOISE_FREQ_4 = new NoiseProvider() {
        final double frequency = 4;
        @Override
        public void setSeed(long seed) {
            fastNoiseLite.SetSeed((int) seed);
        }
        @Override
        public float sample(double x, double y, double z) {
            return fastNoiseLite.GetNoise(x/ frequency*0.7, y / frequency * 0.1, z/ frequency*0.7) - ((float)y - 140)/30.0F;
        }
    };

    public static final NoiseProvider NOISE_FREQ_8 = new NoiseProvider() {
        final double frequency = 8;
        @Override
        public void setSeed(long seed) {
            fastNoiseLite.SetSeed((int) seed);
        }
        @Override
        public float sample(double x, double y, double z) {
            return fastNoiseLite.GetNoise(x/ frequency*0.7, y / frequency * 0.1, z/ frequency*0.7) - ((float)y - 140)/30.0F;
        }
    };

    public static final NoiseProvider NOISE_FREQ_16 = new NoiseProvider() {
        final double frequency = 16;
        @Override
        public void setSeed(long seed) {
            fastNoiseLite.SetSeed((int) seed);
        }
        @Override
        public float sample(double x, double y, double z) {
            return fastNoiseLite.GetNoise(x/ frequency*0.7, y / frequency * 0.1, z/ frequency*0.7) - ((float)y - 140)/30.0F;
        }
    };

    public static final NoiseProvider NOISE_FREQ_32 = new NoiseProvider() {
        final double frequency = 32;
        @Override
        public void setSeed(long seed) {
            fastNoiseLite.SetSeed((int) seed);
        }
        @Override
        public float sample(double x, double y, double z) {
            return fastNoiseLite.GetNoise(x/ frequency*0.3, y / frequency * 0.1, z/ frequency*0.3) - ((float)y - 140)/30.0F;
        }
    };
}
