package birsy.clinker.common.world.level.gen.system.sampling.noise;

public class VoronoiNoiseProvider {
    public static NoiseProvider create(String name, double jitter, int dimensions) {
        return switch (dimensions) {
            case 2 -> new NoiseProvider.SimpleNoiseProvider(name, random -> new TwoDimensional(random.nextLong(), jitter));
            case 3 -> new NoiseProvider.SimpleNoiseProvider(name, random -> new ThreeDimensional(random.nextLong(), jitter));
            default -> throw new IllegalArgumentException(dimensions + " dimensional voronoi unsupported!");
        };
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
            this(seed, jitter, new double[6]);
        }

        @Override
        public double sample(double x, double y, double z) {
            return sampleSet(x, y, z)[3];
        }

        @Override
        public double[] sampleSet(double x, double y, double z) {
            long ix = (long) Math.floor(x);
            long iy = (long) Math.floor(y);
            long iz = (long) Math.floor(z);
            double lx = x - ix, ly = y - iy, lz = z - iz;

            double f1 = Double.MAX_VALUE, f2 = Double.MAX_VALUE;
            double bestCX = 0, bestCY = 0, bestCZ = 0;
            long bestIX = 0, bestIY = 0, bestIZ = 0;

            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        long h = hash(seed, ix + dx, iy + dy, iz + dz);
                        double ox = offset(h,  0, jitter);
                        double oy = offset(h, 20, jitter);
                        double oz = offset(h, 40, jitter);

                        double rx = (dx + 0.5 + ox) - lx;
                        double ry = (dy + 0.5 + oy) - ly;
                        double rz = (dz + 0.5 + oz) - lz;
                        double distSq = rx * rx + ry * ry + rz * rz;

                        if (distSq < f1) {
                            f2 = f1;
                            f1 = distSq;
                            bestIX = ix + dx; bestIY = iy + dy; bestIZ = iz + dz;
                            bestCX = ix + dx + 0.5 + ox;
                            bestCY = iy + dy + 0.5 + oy;
                            bestCZ = iz + dz + 0.5 + oz;
                        } else if (distSq < f2) {
                            f2 = distSq;
                        }
                    }
                }
            }

            result[0] = bestCX;
            result[1] = bestCY;
            result[2] = bestCZ;
            result[3] = (hash(seed + 1, bestIX, bestIY, bestIZ) >>> 32) / (double)(1L << 32);
            result[4] = Math.sqrt(f1);
            result[5] = Math.sqrt(f2);
            return result;
        }
    }

    private record TwoDimensional(long seed, double jitter, double[] result) implements NoiseSampler {
        public TwoDimensional(long seed, double jitter) {
            this(seed, jitter, new double[5]);
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
            long ix = (long) Math.floor(x);
            long iz = (long) Math.floor(z);
            double lx = x - ix, lz = z - iz;

            double f1 = Double.MAX_VALUE, f2 = Double.MAX_VALUE;
            double bestCX = 0, bestCZ = 0;
            long bestIX = 0, bestIZ = 0;

            for (int dz = -1; dz <= 1; dz++) {
                for (int dx = -1; dx <= 1; dx++) {
                    long h = hash(seed, ix + dx, 0L, iz + dz);
                    double ox = offset(h,  0, jitter);
                    double oz = offset(h, 20, jitter);

                    double rx = (dx + 0.5 + ox) - lx;
                    double rz = (dz + 0.5 + oz) - lz;
                    double distSq = rx * rx + rz * rz;

                    if (distSq < f1) {
                        f2 = f1;
                        f1 = distSq;
                        bestIX = ix + dx; bestIZ = iz + dz;
                        bestCX = ix + dx + 0.5 + ox;
                        bestCZ = iz + dz + 0.5 + oz;
                    } else if (distSq < f2) {
                        f2 = distSq;
                    }
                }
            }

            result[0] = bestCX;
            result[1] = bestCZ;
            result[2] = (hash(seed + 1, bestIX, 0L, bestIZ) >>> 32) / (double)(1L << 32);
            result[3] = Math.sqrt(f1);
            result[4] = Math.sqrt(f2);
            return result;
        }
    }
}
