package birsy.clinker.core.util.noise;

import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class Basic1dNoise {
    final float[] buffer;

    public Basic1dNoise(RandomSource random, int bufferSize) {
        this.buffer = new float[bufferSize];
        for (int i = 0; i < bufferSize; i++)
            this.buffer[i] = random.nextFloat() * 2F - 1F;
    }

    public float sample(double x) {
        long pos = (long) Math.floor(x);
        float a = buffer[Math.floorMod(pos + 0, buffer.length)],
              b = buffer[Math.floorMod(pos + 1, buffer.length)];
        float frac = (float) (x - pos);
        return Mth.lerp(frac * frac * (3.0f - 2.0f * frac), a, b);
    }

    public float sample(double x, int octaves, float persistence, double lacunarity) {
        float noise = sample(x),
              maxAmp = 1F,
              power = 1;
        for (int i = 0; i < octaves; i++) {
            x += 30; x *= -lacunarity;
            power *= persistence;
            noise += sample(x) * power;
            maxAmp += power;
        }
        return noise / maxAmp;
    }
}
