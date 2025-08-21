package birsy.clinker.common.world.level.gen.noiseprovider;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.NoiseChunk;

import java.util.HashMap;
import java.util.Map;

public class NoiseProvider {
    private final int minX, minY, minZ;
    private final int height;
    final private Map<Noise, NoiseMap> maps;

    public NoiseProvider(int minX, int minY, int minZ, int height) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.height = height;
        this.maps = new HashMap<>(16);
    }

    public double compute(int x, int y, int z, Noise noise) {
        if (noise.cacheType == CacheType.NONE) {
            return noise.noiseFunction.compute(x, y, z, this);
        }
        
        if (!maps.containsKey(noise)) {
            NoiseMap noiseMap = switch (noise.cacheType) {
                case NONE -> null;
                case DIRECT -> new DirectNoiseMap(this.height);
                case TWO_DIMENSIONAL -> new TwoDimensionalNoiseMap();
                case INTERPOLATED -> new InterpolatedNoiseMap(this.height);
                case INTERPOLATED_TWO_DIMENSIONAL -> new InterpolatedTwoDimensionalNoiseMap();
            };

            noiseMap.fill(minX, minY, minZ, noise, this);
        }
        return maps.get(noise).retrieve(x, y, z);
    }

    private abstract class NoiseMap {
        abstract void fill(int minX, int minY, int minZ, Noise noiseFunction, NoiseProvider noiseProvider);
        abstract double retrieve(int x, int y, int z);
    }

    private class DirectNoiseMap extends NoiseMap {
        final int height;
        final double[] map;

        private DirectNoiseMap(int height) {
            this.height = height;
            this.map = new double[16 * 16 * height];
        }

        @Override
        void fill(int minX, int minY, int minZ, Noise noise, NoiseProvider noiseProvider) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < this.height; y++) {
                        int i = index(x, y, z);
                        map[i] = noise.noiseFunction.compute(x + minX, y + minY, z + minZ, noiseProvider);
                    }
                }
            }
        }

        @Override
        double retrieve(int x, int y, int z) {
            return map[index(x, y, z)];
        }

        private static int index(int x, int y, int z) {
            return x + z * 16 + y * 16 * 16;
        }
    }

    private class TwoDimensionalNoiseMap extends NoiseMap {
        final double[] map;

        private TwoDimensionalNoiseMap() {
            this.map = new double[16 * 16];
        }

        @Override
        void fill(int minX, int minY, int minZ, Noise noise, NoiseProvider noiseProvider) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int i = index(x, z);
                    map[i] = noise.noiseFunction.compute(x + minX, 0, z + minZ, noiseProvider);
                }
            }
        }

        @Override
        double retrieve(int x, int y, int z) {
            return map[index(x, z)];
        }

        private static int index(int x, int z) {
            return x + z * 16;
        }
    }

    private class InterpolatedNoiseMap extends NoiseMap {
        static int cellWidth = 4, cellHeight = 8;
        static int horizontalResolution = 16 / cellWidth;
        final int verticalResolution;
        final double[] map;

        private InterpolatedNoiseMap(int height) {
            this.verticalResolution = height / cellHeight;
            this.map = new double[
                    (horizontalResolution + 1) *
                    (horizontalResolution + 1) *
                    (this.verticalResolution + 1)
            ];
        }

        @Override
        void fill(int minX, int minY, int minZ, Noise noise, NoiseProvider noiseProvider) {
            for (int x = 0; x < horizontalResolution + 1; x++) {
                int blockX = x * horizontalResolution + minX;
                for (int z = 0; z < horizontalResolution + 1; z++) {
                    int blockZ = z * horizontalResolution + minZ;
                    for (int y = 0; y < verticalResolution + 1; y++) {
                        int blockY = y * verticalResolution + minY;
                        int i = index(x, y, z);
                        map[i] = noise.noiseFunction.compute(blockX, blockY, blockZ, noiseProvider);
                    }
                }
            }
        }

        @Override
        double retrieve(int x, int y, int z) {
            int cellX = Math.floorDiv(x, cellWidth),
                cellY = Math.floorDiv(y, cellHeight),
                cellZ = Math.floorDiv(z, cellWidth);
            double fracX = Math.floorMod(x, cellWidth)  / (double)cellWidth,
                   fracY = Math.floorMod(y, cellHeight) / (double)cellHeight,
                   fracZ = Math.floorMod(z, cellWidth)  / (double)cellWidth;
            
            double n000 = map[index(cellX + 0, cellY + 0, cellZ + 0)];
            double n001 = map[index(cellX + 0, cellY + 0, cellZ + 1)];
            double n010 = map[index(cellX + 0, cellY + 1, cellZ + 0)];
            double n011 = map[index(cellX + 0, cellY + 1, cellZ + 1)];
            double n100 = map[index(cellX + 1, cellY + 0, cellZ + 0)];
            double n101 = map[index(cellX + 1, cellY + 0, cellZ + 1)];
            double n110 = map[index(cellX + 1, cellY + 1, cellZ + 0)];
            double n111 = map[index(cellX + 1, cellY + 1, cellZ + 1)];

            return Mth.lerp3(
                    fracX, fracY, fracZ,
                    n000, n100, n010, n110,
                    n001, n101, n011, n111
            );
        }

        private static int index(int x, int y, int z) {
            return x + z * (horizontalResolution + 1) + y * (horizontalResolution + 1) * (horizontalResolution + 1);
        }
    }
    
    private class InterpolatedTwoDimensionalNoiseMap extends NoiseMap {
        static int cellWidth = 4;
        static int horizontalResolution = 16 / cellWidth;
        final double[] map;

        private InterpolatedTwoDimensionalNoiseMap() {
            this.map = new double[
                    (horizontalResolution + 1) *
                    (horizontalResolution + 1)
            ];
        }

        @Override
        void fill(int minX, int minY, int minZ, Noise noise, NoiseProvider noiseProvider) {
            for (int x = 0; x < horizontalResolution + 1; x++) {
                int blockX = x * horizontalResolution + minX;
                for (int z = 0; z < horizontalResolution + 1; z++) {
                    int blockZ = z * horizontalResolution + minZ;
                    int i = index(x, z);
                    map[i] = noise.noiseFunction.compute(blockX, 0, blockZ, noiseProvider);
                }
            }
        }

        @Override
        double retrieve(int x, int y, int z) {
            int cellX = Math.floorDiv(x, cellWidth),
                cellZ = Math.floorDiv(z, cellWidth);
            double fracX = Math.floorMod(x, cellWidth)  / (double)cellWidth,
                   fracZ = Math.floorMod(z, cellWidth)  / (double)cellWidth;

            double n00 = map[index(cellX + 0, cellZ + 0)];
            double n01 = map[index(cellX + 0, cellZ + 1)];
            double n10 = map[index(cellX + 1, cellZ + 0)];
            double n11 = map[index(cellX + 1, cellZ + 1)];

            return Mth.lerp2(fracX, fracZ, n00, n10, n01, n11);
        }

        private static int index(int x, int z) {
            return x + z * (horizontalResolution + 1);
        }
    }
}
