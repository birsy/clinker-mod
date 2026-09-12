package birsy.clinker.common.world.level.gen.system.sampling.noise;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class VoronoiNoiseProvider {
    public static NoiseProvider create(String name, double jitter, boolean twoDimensional) {
        return new NoiseProvider.SimpleNoiseProvider(name,
                twoDimensional ?
                        random -> new TwoDimensional(random.nextLong(), jitter) :
                        random -> new ThreeDimensional(random.nextLong(), jitter)
        );
    }

    private static long hash(long seed, long x, long y, long z) {
        long h = seed ^ (x * 0x9E3779B97F4A7C15L)
                ^ (y * 0x6C62272E07BB0142L)
                ^ (z * 0xC2B2AE3D27D4EB4FL);
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        return h ^ (h >>> 31);
    }

    private static double offset(long h, int shift, double jitter) {
        return (((h >>> shift) & 0xFFFFFL) * (2.0 / 0xFFFFFL) - 1.0) * jitter;
    }

    private record ThreeDimensional(long seed, double jitter, double[] result) implements NoiseSampler {
        public ThreeDimensional(long seed, double jitter) {
            this(seed, jitter, new double[4]);
        }

        @Override
        public double sample(double x, double y, double z) {
            return sampleSet(x, y, z)[3];
        }

        @Override
        public double[] sampleSet(double x, double y, double z) {
            long iX = (long) Math.floor(x);
            long iY = (long) Math.floor(y);
            long iZ = (long) Math.floor(z);
            double lx = x - iX, ly = y - iY, lz = z - iZ;

            double bestDist = Double.MAX_VALUE;
            double bestCX = 0, bestCY = 0, bestCZ = 0;
            long bestIX = 0, bestIY = 0, bestIZ = 0;

            for (int dY = -1; dY <= 1; dY++) {
                for (int dZ = -1; dZ <= 1; dZ++) {
                    for (int dX = -1; dX <= 1; dX++) {
                        long h = hash(seed, iX + dX, iY + dY, iZ + dZ);
                        double oX = offset(h, 0, jitter);
                        double oY = offset(h, 20, jitter);
                        double oZ = offset(h, 40, jitter);

                        double rX = (dX + 0.5 + oX) - lx;
                        double rY = (dY + 0.5 + oY) - ly;
                        double rZ = (dZ + 0.5 + oZ) - lz;
                        double distSq = Mth.lengthSquared(rX, rY, rZ);

                        if (distSq < bestDist) {
                            bestDist = distSq;
                            bestIX = iX + dX; bestIY = iY + dY; bestIZ = iZ + dZ;
                            bestCX = iX + dX + 0.5 + oX;
                            bestCY = iY + dY + 0.5 + oY;
                            bestCZ = iZ + dZ + 0.5 + oZ;
                        }
                    }
                }
            }

            result[0] = bestCX;
            result[1] = bestCY;
            result[2] = bestCZ;
            result[3] = (hash(seed + 1, bestIX, bestIY, bestIZ) >>> 32) / (double)(1L << 32);
            return result;
        }
    }

    public record TwoDimensional(long seed, double jitter, double[] result) implements NoiseSampler {
        public TwoDimensional(long seed, double jitter) {
            this(seed, jitter, new double[4]);
        }

        @Override
        public double sample(double x, double y, double z) {
            return sample(x, z);
        }

        @Override
        public double sample(double x, double z) {
            return sampleSet(x, z)[2];
        }

        @Override
        public double[] sampleSet(double x, double z) {
            long iX = (long) Math.floor(x);
            long iZ = (long) Math.floor(z);
            double lx = x - iX, lz = z - iZ;

            double bestDist = Double.MAX_VALUE;
            double bestCX = 0, bestCZ = 0;
            long bestIX = 0, bestIZ = 0;

            for (int dZ = -1; dZ <= 1; dZ++) {
                for (int dX = -1; dX <= 1; dX++) {
                    long h = hash(seed, iX + dX, 0L, iZ + dZ);
                    double oX = offset(h,  0, jitter);
                    double oZ = offset(h, 20, jitter);

                    double rX = (dX + 0.5 + oX) - lx;
                    double rZ = (dZ + 0.5 + oZ) - lz;
                    double distSq = rX * rX + rZ * rZ;

                    if (distSq < bestDist) {
                        bestDist = distSq;
                        bestIX = iX + dX; bestIZ = iZ + dZ;
                        bestCX = iX + dX + 0.5 + oX;
                        bestCZ = iZ + dZ + 0.5 + oZ;
                    }
                }
            }

            result[0] = bestCX;
            result[1] = bestCZ;
            result[2] = (hash(seed + 1, bestIX, 0L, bestIZ) >>> 32) / (double)(1L << 32);
            return result;
        }
    }
}
