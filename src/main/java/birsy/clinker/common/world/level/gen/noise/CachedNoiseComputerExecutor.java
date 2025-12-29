package birsy.clinker.common.world.level.gen.noise;

import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public class CachedNoiseComputerExecutor implements NoiseComputerExecutor {
    private final int minX, minY, minZ;
    final int height;
    private final Map<NoiseComputer, NoiseMap> noiseProcessorCaches;
    private final NoiseComputerContext context, uncachedContext;

    public CachedNoiseComputerExecutor(int minX, int minY, int minZ, int height, NoiseHolder noiseHolder) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.height = height;
        this.noiseProcessorCaches = new HashMap<>(16);
        this.context = new NoiseComputerContext(this, noiseHolder);
        this.uncachedContext = new NoiseComputerContext(new UncachedNoiseComputerExecutor(noiseHolder), noiseHolder);
    }

    @Override
    public double compute(int x, int y, int z, NoiseComputer noiseProcessor) {
        int localX = x - minX, localY = y - minY, localZ = z - minZ;

        if (noiseProcessor.cacheType() == CacheType.NONE)
            return noiseProcessor.compute(x, y, z, this.context);

        NoiseMap cache = noiseProcessorCaches.getOrDefault(noiseProcessor, null);
        if (cache == null) {
            cache = noiseProcessor.cacheType().create(this);
            cache.fill(minX, minY, minZ, noiseProcessor, this.context);
            noiseProcessorCaches.put(noiseProcessor, cache);
        }
        return cache.retrieve(localX, localY, localZ);
    }

    @Override
    public double computeDirect(int x, int y, int z, NoiseComputer noiseProcessor) {
        return noiseProcessor.compute(x, y, z, this.uncachedContext);
    }

    abstract class NoiseMap {
        abstract void fill(int minX, int minY, int minZ, NoiseComputer noiseProcessorFunction, NoiseComputerContext context);
        abstract double retrieve(int x, int y, int z);
    }

    static class DirectNoiseMap extends NoiseMap {
        final int height;
        final double[] map;

        DirectNoiseMap(int height) {
            this.height = height;
            this.map = new double[16 * 16 * height];
        }

        @Override
        void fill(int minX, int minY, int minZ, NoiseComputer noiseProcessor, NoiseComputerContext context) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < this.height; y++) {
                        int i = index(x, y, z);
                        map[i] = noiseProcessor.compute(x + minX, y + minY, z + minZ, context);
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

    static class TwoDimensionalNoiseMap extends NoiseMap {
        final double[] map;

        TwoDimensionalNoiseMap() {
            this.map = new double[16 * 16];
        }

        @Override
        void fill(int minX, int minY, int minZ, NoiseComputer noiseProcessor, NoiseComputerContext context) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int i = index(x, z);
                    map[i] = noiseProcessor.compute(x + minX, 0, z + minZ, context);
                }
            }
        }

        @Override
        double retrieve(int x, int y, int z) {
            return map[index(x, z)];
        }

        private static int index(int x, int z) {
            return Mth.clamp(x, 0, 15) + Mth.clamp(z, 0, 15) * 16;
        }
    }

    static class InterpolatedNoiseMap extends NoiseMap {
        final int cellWidth, cellHeight;
        final int horizontalResolution, verticalResolution;
        final int cellCountHorizontal, cellCountVertical;
        final double[] map;

        InterpolatedNoiseMap(int height, int cellWidth, int cellHeight, boolean finalDensity) {
            this.cellWidth = cellWidth;
            this.cellHeight = cellHeight;
            this.horizontalResolution = 16 / cellWidth;
            this.verticalResolution = height / cellHeight;

            this.cellCountHorizontal = finalDensity ? horizontalResolution + 1 : horizontalResolution + 2;
            this.cellCountVertical = finalDensity ? verticalResolution + 1 : verticalResolution + 2;
            this.map = new double[this.cellCountHorizontal * this.cellCountHorizontal * this.cellCountVertical];
        }

        @Override
        void fill(int minX, int minY, int minZ, NoiseComputer noiseProcessor, NoiseComputerContext context) {
            for (int x = 0; x < this.cellCountHorizontal; x++) {
                int blockX = x * cellWidth + minX;
                for (int z = 0; z < this.cellCountHorizontal; z++) {
                    int blockZ = z * cellWidth + minZ;
                    for (int y = 0; y < this.cellCountVertical; y++) {
                        int blockY = y * cellHeight + minY;

                        int i = index(x, y, z);
                        map[i] = noiseProcessor.compute(blockX, blockY, blockZ, context);
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

        private int index(int x, int y, int z) {
            // clamp to valid range. this will cause some additional artifacting around chunk borders, but it shouldn't
            // matter too much in practice? hopefully?
            x = Math.clamp(x, 0, this.cellCountHorizontal - 1);
            y = Math.clamp(y, 0, this.cellCountVertical - 1);
            z = Math.clamp(z, 0, this.cellCountHorizontal - 1);
            int i = x + z * this.cellCountHorizontal + y * this.cellCountHorizontal * this.cellCountHorizontal;
            if (i >= this.map.length) Clinker.LOGGER.error("{}, {}, {}", x, y, z);
            return i;
        }
    }
    
    static class Interpolated2DNoiseMap extends NoiseMap {
        final int cellWidth;
        final int horizontalResolution;
        final int cellCountHorizontal;
        final double[] map;

        Interpolated2DNoiseMap(int cellWidth, boolean finalDensity) {
            this.cellWidth = cellWidth;
            this.horizontalResolution = 16 / cellWidth;
            this.cellCountHorizontal = finalDensity ? this.horizontalResolution + 1 : this.horizontalResolution + 2;
            this.map = new double[this.cellCountHorizontal * this.cellCountHorizontal];
        }

        @Override
        void fill(int minX, int minY, int minZ, NoiseComputer noiseProcessor, NoiseComputerContext context) {
            for (int x = 0; x < this.cellCountHorizontal; x++) {
                int blockX = x * cellWidth + minX;
                for (int z = 0; z < this.cellCountHorizontal; z++) {
                    int blockZ = z * cellWidth + minZ;
                    int i = index(x, z);
                    map[i] = noiseProcessor.compute(blockX, 0, blockZ, context);
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

        private int index(int x, int z) {
            // clamp to valid range. this will cause some additional artifacting around chunk borders, but it shouldn't
            // matter too much in practice? hopefully?
            x = Math.clamp(x, 0, this.cellCountHorizontal - 1);
            z = Math.clamp(z, 0, this.cellCountHorizontal - 1);
            int i = x + z * this.cellCountHorizontal;
            if (i >= this.map.length) Clinker.LOGGER.error("{}, {}", x, z);
            return i;
        }
    }
}
