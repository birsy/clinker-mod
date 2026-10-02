package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.core.util.HashUtils;
import net.minecraft.util.Mth;

public class VoronoiNoiseProvider {
    public static NoiseProvider create(String name, float jitter, int dimensions) {
        return switch (dimensions) {
            case 2 -> new NoiseProvider.SimpleNoiseProvider(name, random -> new TwoDimensional(random.nextLong(), jitter));
            case 3 -> new NoiseProvider.SimpleNoiseProvider(name, random -> new ThreeDimensional(random.nextLong(), jitter));
            default -> throw new IllegalArgumentException(dimensions + " dimensional voronoi unsupported!");
        };
    }

    private record ThreeDimensional(long seed, float jitter, float[] result) implements NoiseSampler {
        private static final float MAX_JITTER = 0.25f;

        public ThreeDimensional(long seed, float jitter) {
            this(seed, jitter, new float[6]);
        }

        @Override
        public float sample(double x, double y, double z) {
            return sampleSet(x, y, z)[3];
        }

        @Override
        public float[] sampleSet(double x, double y, double z) {
            int ax = Math.round((float) x), ay = Math.round((float) y), az = Math.round((float) z);
            int bx = Math.round((float) x - 0.5f), by = Math.round((float) y - 0.5f), bz = Math.round((float) z - 0.5f);

            float distA = (float) sq(x - ax) + (float) sq(y - ay) + (float) sq(z - az);
            float distB = (float) sq(x - bx - 0.5f) + (float) sq(y - by - 0.5f) + (float) sq(z - bz - 0.5f);

            int cellX, cellY, cellZ, sub;
            if (distA <= distB) { cellX = ax; cellY = ay; cellZ = az; sub = 0; }
            else { cellX = bx; cellY = by; cellZ = bz; sub = 1; }

            float f1 = Float.MAX_VALUE, f2 = Float.MAX_VALUE;
            int nearestX = cellX, nearestY = cellY, nearestZ = cellZ, nearestSub = sub;
            float nearestOffsetX = 0, nearestOffsetY = 0, nearestOffsetZ = 0;

            for (int n = -1; n < 14; n++) {
                int cx, cy, cz, csub;
                if (n == -1) {
                    cx = cellX; cy = cellY; cz = cellZ; csub = sub;
                } else if (n < 8) {
                    int dx = (n & 1) == 0 ? 0 : (sub == 0 ? -1 : 1);
                    int dy = (n & 2) == 0 ? 0 : (sub == 0 ? -1 : 1);
                    int dz = (n & 4) == 0 ? 0 : (sub == 0 ? -1 : 1);
                    cx = cellX + dx; cy = cellY + dy; cz = cellZ + dz;
                    csub = 1 - sub;
                } else {
                    int axis = (n - 8) / 2;
                    int dir = (n - 8) % 2 == 0 ? 1 : -1;
                    cx = cellX + (axis == 0 ? dir : 0);
                    cy = cellY + (axis == 1 ? dir : 0);
                    cz = cellZ + (axis == 2 ? dir : 0);
                    csub = sub;
                }

                int h = HashUtils.hash(seed, cx, cy, cz ^ (csub << 30));
                float offsetX = HashUtils.bandToUnitFloat(h, 0, 11) * jitter * MAX_JITTER;
                float offsetY = HashUtils.bandToUnitFloat(h, 11, 11) * jitter * MAX_JITTER;
                float offsetZ = HashUtils.bandToUnitFloat(h, 22, 10) * jitter * MAX_JITTER;

                float baseX = cx + (csub == 1 ? 0.5f : 0f);
                float baseY = cy + (csub == 1 ? 0.5f : 0f);
                float baseZ = cz + (csub == 1 ? 0.5f : 0f);

                float xDist = (float) x - (baseX + offsetX);
                float yDist = (float) y - (baseY + offsetY);
                float zDist = (float) z - (baseZ + offsetZ);
                float distSq = xDist * xDist + yDist * yDist + zDist * zDist;

                if (distSq < f1) {
                    f2 = f1; f1 = distSq;
                    nearestX = cx; nearestY = cy; nearestZ = cz; nearestSub = csub;
                    nearestOffsetX = offsetX; nearestOffsetY = offsetY; nearestOffsetZ = offsetZ;
                } else if (distSq < f2) {
                    f2 = distSq;
                }
            }

            float nb = nearestSub == 1 ? 0.5f : 0f;
            result[0] = nearestX + nb + nearestOffsetX;
            result[1] = nearestY + nb + nearestOffsetY;
            result[2] = nearestZ + nb + nearestOffsetZ;
            result[3] = HashUtils.bitsToUnitFloat(HashUtils.hash(seed + 1, nearestX, nearestY, nearestZ ^ (nearestSub << 30)));
            result[4] = Mth.sqrt(f1);
            result[5] = Mth.sqrt(f2);
            return result;
        }

        private static double sq(double v) { return v * v; }
    }

    private record TwoDimensional(long seed, float jitter, float[] result) implements NoiseSampler {
        private static final float SQRT3_2 = 0.8660254f;
        private static final float MAX_JITTER_COMPONENT = 0.3536f;
        private static final int[][] NEIGHBORS = {{1,0},{1,-1},{0,-1},{-1,0},{-1,1},{0,1}};

        public TwoDimensional(long seed, float jitter) {
            this(seed, jitter, new float[5]);
        }

        @Override
        public float sample(double x, double y, double z) {
            return sample(x, z);
        }

        @Override
        public float sample(double x, double z) {
            return sampleSet(x, z)[2];
        }

        @Override
        public float[] sampleSet(double x, double z) {
            float r = (float) (z / SQRT3_2);
            float q = (float) x - 0.5f * r;

            float cx = q, cz = r, cy = -cx - cz;
            int rx = Math.round(cx), ry = Math.round(cy), rz = Math.round(cz);
            float dx = Math.abs(rx - cx), dy = Math.abs(ry - cy), dz = Math.abs(rz - cz);
            if (dx > dy && dx > dz) rx = -ry - rz;

            else rz = -rx - ry;
            int cellQ = rx, cellR = rz;

            float f1 = Float.MAX_VALUE, f2 = Float.MAX_VALUE;
            int nearestQ = cellQ, nearestR = cellR;
            float nearestOffsetX = 0, nearestOffsetZ = 0;

            for (int n = -1; n < 6; n++) {
                int neighborQ = cellQ + (n < 0 ? 0 : NEIGHBORS[n][0]);
                int neighborR = cellR + (n < 0 ? 0 : NEIGHBORS[n][1]);

                int h = HashUtils.hash(seed, neighborQ, neighborR, 0);
                float offsetX = HashUtils.bandToUnitFloat(h, 0, 16) * jitter * MAX_JITTER_COMPONENT;
                float offsetZ = HashUtils.bandToUnitFloat(h, 16, 16) * jitter * MAX_JITTER_COMPONENT;

                float px = neighborQ + 0.5f * neighborR + offsetX;
                float pz = neighborR * SQRT3_2 + offsetZ;

                float xDist = (float) x - px, zDist = (float) z - pz;
                float distSq = xDist * xDist + zDist * zDist;
                if (distSq < f1) {
                    f2 = f1; f1 = distSq;
                    nearestQ = neighborQ; nearestR = neighborR;
                    nearestOffsetX = offsetX; nearestOffsetZ = offsetZ;
                } else if (distSq < f2) {
                    f2 = distSq;
                }
            }

            result[0] = nearestQ + 0.5f * nearestR + nearestOffsetX;
            result[1] = nearestR * SQRT3_2 + nearestOffsetZ;
            result[2] = HashUtils.bitsToUnitFloat(HashUtils.hash(seed + 1, nearestQ, nearestR, 0));
            result[3] = Mth.sqrt(f1);
            result[4] = Mth.sqrt(f2);
            return result;
        }
    }
}
