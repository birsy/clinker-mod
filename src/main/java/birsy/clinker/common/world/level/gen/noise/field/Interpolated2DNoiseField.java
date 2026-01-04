package birsy.clinker.common.world.level.gen.noise.field;

import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import net.minecraft.util.Mth;

public class Interpolated2DNoiseField implements NoiseField {
    final int cellScale, cellCount, cellSize, cellMask;
    final int paddingCells, paddingBlocks;
    final double inverseCellSize;
    final double[] field;

    public Interpolated2DNoiseField(int cellScale, int paddingCells) {
        this.cellScale = cellScale;
        this.cellCount = (CHUNK_WIDTH >> this.cellScale) + 1 + paddingCells * 2;
        this.cellSize = 1 << this.cellScale;
        this.inverseCellSize = 1.0 / this.cellSize;
        this.cellMask = this.cellSize - 1;
        this.paddingCells = paddingCells;
        this.paddingBlocks = paddingCells * cellSize;
        this.field = new double[this.cellCount * this.cellCount];
    }

    @Override
    public void fill(int minX, int minY, int minZ, NoiseComputer noiseComputer, NoiseComputerContext context) {
        int index = 0;
        for (int z = 0; z < cellCount; z++) {
            int globalZ = z + minZ;
            for (int x = 0; x < cellCount; x++) {
                int globalX = x + minX;
                this.field[index++] = noiseComputer.compute(globalX, 0, globalZ, context);
            }
        }
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int unpaddedX = x + paddingBlocks,
            unpaddedZ = z + paddingBlocks;
        int cellX = unpaddedX >> this.cellScale,
            cellZ = unpaddedZ >> this.cellScale;
        int localX = unpaddedX & cellMask,
            localZ = unpaddedZ & cellMask;
        double interpX = localX * inverseCellSize,
               interpZ = localZ * inverseCellSize;
        int nextX = cellX + (localX != 0 ? 1 : 0),
            nextZ = cellZ + (localZ != 0 ? 1 : 0);
        return Mth.lerp2(interpX, interpZ,
                field[cellX + cellZ * cellCount], field[nextX + cellZ * cellCount],
                field[cellX + nextZ * cellCount], field[nextX + nextZ * cellCount]
        );
    }
}
