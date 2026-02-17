package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.VoronoiEvaluator;

public final class VoronoiNoiseField2D extends NoiseField2D {
    private final VoronoiEvaluator evaluator;
    private final int cellScale, cellCount;

    private final double[] field;
    private boolean filled;

    public VoronoiNoiseField2D(VoronoiEvaluator evaluator, int cellScale, int paddingCells) {
        super(paddingCells << cellScale, paddingCells);
        this.evaluator = evaluator;
        this.cellScale = cellScale;
        this.cellCount = (CHUNK_WIDTH >> cellScale) + paddingCells * 2;
        this.field = new double[cellCount * cellCount];
        this.filled = false;
    }

    @Override
    public double[] array() {
        return field;
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int cellIndex = evaluator.getNearestCellIndex(x, y, z);
        return field[cellIndex];
    }

    @Override
    public void fill(int startY, int endY, int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller filler) {
        if (filled) return;
        int index = 0;
        for (int cellZ = 0; cellZ < cellCount; cellZ++) {
            int globalZ = (cellZ << cellScale) + minZ - paddingBlocks;
            for (int cellX = 0; cellX < cellCount; cellX++) {
                int globalX = (cellX << cellScale) + minX - paddingBlocks;
                double cellCenterX = evaluator.cellCenterX(globalX, 0, globalZ, index),
                       cellCenterZ = evaluator.cellCenterZ(globalX, 0, globalZ, index);
                field[index++] = filler.compute((int) cellCenterX, 0, (int) cellCenterZ, context);
            }
        }
        filled = true;
    }

    @Override
    public void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor) {
        for (int i = 0; i < field.length; i++) visitor.visit(i);
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
            int bZ = (cellZ << cellScale) - paddingBlocks;
            for (int cellX = 0; cellX < cellCount; cellX++) {
                int bX = (cellX << cellScale) - paddingBlocks;
                double cellCenterX = evaluator.cellCenterX(bX, 0, bZ, index),
                       cellCenterZ = evaluator.cellCenterZ(bX, 0, bZ, index);
                visitor.visit(index++, (int) cellCenterX, 0, (int) cellCenterZ);
            }
        }
    }
    @Override
    public void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < cellCount; cellZ++) {
            int bZ = (cellZ << cellScale) - paddingBlocks;
            for (int cellX = 0; cellX < cellCount; cellX++) {
                int bX = (cellX << cellScale) - paddingBlocks;
                double cellCenterX = evaluator.cellCenterX(bX, 0, bZ, index),
                       cellCenterZ = evaluator.cellCenterZ(bX, 0, bZ, index);
                visitor.visit(index++, (int) cellCenterX, 0, (int) cellCenterZ, cellX, 0, cellZ);
            }
        }
    }
}
