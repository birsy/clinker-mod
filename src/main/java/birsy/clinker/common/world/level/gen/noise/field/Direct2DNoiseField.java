package birsy.clinker.common.world.level.gen.noise.field;

import birsy.clinker.common.world.level.gen.noise.CachedNoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;

import java.util.function.Function;

public class Direct2DNoiseField implements NoiseField {
    final int padding, paddedWidth;
    final double[] field;

    public Direct2DNoiseField(int padding) {
        this.padding = padding;
        this.paddedWidth = CHUNK_WIDTH + padding * 2;
        this.field = new double[this.paddedWidth * this.paddedWidth];
    }

    @Override
    public void fill(int minX, int minY, int minZ, NoiseComputer noiseComputer, NoiseComputerContext context) {
        int index = 0;
        for (int z = 0; z < CHUNK_WIDTH; z++) {
            int globalZ = z + minZ;
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                int globalX = x + minX;
                this.field[index++] = noiseComputer.compute(globalX, 0, globalZ, context);
            }
        }
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int unpaddedX = x - padding,
            unpaddedZ = z - padding;
        return this.field[unpaddedX + unpaddedZ * CHUNK_WIDTH];
    }
}
