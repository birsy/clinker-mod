package birsy.clinker.common.world.level.gen.noise.field;

import birsy.clinker.common.world.level.gen.noise.CachedNoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;

import java.util.function.Function;

public class DirectNoiseField implements NoiseField {
    final int padding, paddedWidth;
    final int xzStride;
    final int chunkHeight;
    final double[] field;

    public DirectNoiseField(int chunkHeight, int padding) {
        this.chunkHeight = chunkHeight;
        this.padding = padding;
        this.paddedWidth = CHUNK_WIDTH + padding * 2;
        this.xzStride = this.paddedWidth * this.paddedWidth;
        this.field = new double[this.xzStride * this.chunkHeight];
    }

    @Override
    public void fill(int minX, int minY, int minZ, NoiseComputer noiseComputer, NoiseComputerContext context) {
        int index = 0;
        for (int y = 0; y < this.chunkHeight; y++) {
            int globalY = y + minY;
            for (int z = 0; z < this.paddedWidth; z++) {
                int globalZ = z + minZ;
                for (int x = 0; x < this.paddedWidth; x++) {
                    int globalX = x + minX;
                    this.field[index++] = noiseComputer.compute(globalX, globalY, globalZ, context);
                }
            }
        }
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int unpaddedX = x - padding,
            unpaddedZ = z - padding;
        return this.field[unpaddedX + unpaddedZ * this.paddedWidth + y * this.xzStride];
    }
}
