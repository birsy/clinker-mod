package birsy.clinker.common.world.level.gen.noise.field;

import birsy.clinker.common.world.level.gen.noise.CachedNoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import net.minecraft.util.Mth;

import java.util.function.Function;

public final class InterpolatedNoiseField implements NoiseField {
    final int xzCellScale, xzCellSize, xzCellMask, xzCellCount, xzCellStride;
    final int yCellScale, yCellSize, yCellMask, yCellCount;
    final int paddingCells, paddingBlocks;
    final double invXZCellSize;
    final double invYCellSize;
    final int chunkHeight;
    final double[] field;

    public InterpolatedNoiseField(int chunkHeight, int xzCellScale, int yCellScale, int paddingCells) {
        this.chunkHeight = chunkHeight;

        this.xzCellScale = xzCellScale;
        this.xzCellSize = 1 << xzCellScale;
        this.xzCellMask = this.xzCellSize - 1;
        this.invXZCellSize = 1.0 / this.xzCellSize;
        this.xzCellCount = (CHUNK_WIDTH >> xzCellScale) + 1 + paddingCells * 2; // padding
        this.xzCellStride = this.xzCellCount * this.xzCellCount;

        this.yCellScale = yCellScale;
        this.yCellSize = 1 << yCellScale;
        this.yCellMask = this.yCellSize - 1;
        this.invYCellSize = 1.0 / this.yCellSize;
        this.yCellCount = (chunkHeight >> yCellScale);

        this.paddingCells = paddingCells;
        this.paddingBlocks = paddingCells * xzCellSize;

        this.field = new double[this.xzCellStride * this.yCellCount];
    }

    @Override
    public void fill(int minX, int minY, int minZ, NoiseComputer noiseComputer, NoiseComputerContext context) {
        int index = 0;
        for (int y = 0; y < yCellCount; y++) {
            int globalY = (y << yCellScale) + minY;
            for (int z = 0; z < xzCellCount; z++) {
                int globalZ = (z << xzCellScale) + minZ;
                for (int x = 0; x < xzCellCount; x++) {
                    int globalX = (x << xzCellScale) + minX;
                    field[index++] = noiseComputer.compute(globalX, globalY, globalZ, context);
                }
            }
        }
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int unpaddedX = x + paddingBlocks,
            unpaddedZ = z + paddingBlocks;
        int cellX = unpaddedX >> xzCellScale,
            cellY = y >> yCellScale,
            cellZ = unpaddedZ >> xzCellScale;
        int localX = unpaddedX & xzCellMask,
            localY = y & yCellMask,
            localZ = unpaddedZ & xzCellMask;
        double interpX = localX * invXZCellSize,
               interpY = localY * invYCellSize,
               interpZ = localZ * invXZCellSize;
        int nextX = cellX + (localX != 0 ? 1 : 0),
            nextY = cellY + (localY != 0 ? 1 : 0),
            nextZ = cellZ + (localZ != 0 ? 1 : 0);
        return Mth.lerp3(interpX, interpZ, interpY,
                field[cellX + cellZ * xzCellCount + cellY * xzCellStride], field[nextX + cellZ * xzCellCount + cellY * xzCellStride],
                field[cellX + nextZ * xzCellCount + cellY * xzCellStride], field[nextX + nextZ * xzCellCount + cellY * xzCellStride],
                field[cellX + cellZ * xzCellCount + nextY * xzCellStride], field[nextX + cellZ * xzCellCount + nextY * xzCellStride],
                field[cellX + nextZ * xzCellCount + nextY * xzCellStride], field[nextX + nextZ * xzCellCount + nextY * xzCellStride]
        );
    }
}
