package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import net.minecraft.util.Mth;

public final class Interpolated2DNoiseField extends NoiseField2D {
    public final int cellScale, cellCount, cellSize, cellMask;
    final double inverseCellSize;
    boolean filled = false;
    public final double[] field;

    public Interpolated2DNoiseField(int cellScale, int paddingCells) {
        super(paddingCells << cellScale, paddingCells);
        this.cellScale = cellScale;
        this.cellCount = (CHUNK_WIDTH >> this.cellScale) + 1 + paddingCells * 2;
        this.cellSize = 1 << cellScale;
        this.inverseCellSize = 1.0 / cellSize;
        this.cellMask = cellSize - 1;
        this.field = new double[cellCount * cellCount];
    }

    @Override
    public double[] array() {
        return field;
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int paddedX = x + paddingBlocks, paddedZ = z + paddingBlocks;
        int cellX = paddedX >> cellScale, cellZ = paddedZ >> cellScale;
        int localX = paddedX & cellMask, localZ = paddedZ & cellMask;
        double interpX = localX * inverseCellSize, interpZ = localZ * inverseCellSize;
        int nextX = cellX + ((localX | -localX) >>> 31), nextZ = cellZ + ((localZ | -localZ) >>> 31);
        return Mth.lerp2(interpX, interpZ,
                field[cellX + cellZ * cellCount], field[nextX + cellZ * cellCount],
                field[cellX + nextZ * cellCount], field[nextX + nextZ * cellCount]
        );
    }

    @Override
    public void fill(int startY, int endY, int minX, int minY, int minZ, NoiseFieldFiller filler, NoiseContext context) {
        if (filled) return;
        int index = 0;
        for (int cellZ = 0; cellZ < cellCount; cellZ++) {
            int globalZ = (cellZ << cellScale) + minZ - paddingBlocks;
            for (int cellX = 0; cellX < cellCount; cellX++) {
                int globalX = (cellX << cellScale) + minX - paddingBlocks;
                field[index++] = filler.compute(globalX, 0, globalZ, context);
            }
        }
        filled = true;
    }

    @Override
    public void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < cellCount; cellZ++) {
            for (int cellX = 0; cellX < cellCount; cellX++) {
                visitor.visit(index++);
            }
        }
    }

    @Override
    public void byCell(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < cellCount; cellZ++) {
            for (int cellX = 0; cellX < cellCount; cellX++) {
                visitor.visit(index++, cellX, 0, cellZ);
            }
        }
    }

    @Override
    public void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < cellCount; cellZ++) {
            int blockZ = cellZ << cellScale;
            for (int cellX = 0; cellX < cellCount; cellX++) {
                int blockX = cellX << cellScale;
                visitor.visit(index++, blockX, 0, blockZ);
            }
        }
    }

    @Override
    public void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < cellCount; cellZ++) {
            int blockZ = cellZ << cellScale;
            for (int cellX = 0; cellX < cellCount; cellX++) {
                int blockX = cellX << cellScale;
                visitor.visit(index++, blockX, 0, blockZ, cellX, 0, cellZ);
            }
        }
    }
}
