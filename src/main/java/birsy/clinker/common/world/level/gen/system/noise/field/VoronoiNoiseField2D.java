package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator2D;
import birsy.clinker.core.Clinker;

public final class VoronoiNoiseField2D extends NoiseField2D {
    private final VoronoiEvaluator2D evaluator;
    private final int minX, minZ;
    private final double[] field;
    private boolean filled;

    public VoronoiNoiseField2D(VoronoiEvaluator2D evaluator, int minX, int minZ) {
        super(0, 0);
        this.evaluator = evaluator;
        this.minX = minX;
        this.minZ = minZ;
        this.field = new double[evaluator.cellCount];
        this.filled = false;
    }

    @Override
    public double[] array() {
        return field;
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int cellIndex = evaluator.getNearestCellIndex(x + minX, 0, z + minZ);
        return field[cellIndex];
    }

    @Override
    public void fill(int minLocalY, int maxLocalY, int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller filler) {
        if (filled) return;
        evaluator.fill(0, 0);
        for (int i = 0; i < field.length; i++) {
            double cellCenterX = evaluator.cellCenterX(minX, minY, minZ, i),
                   cellCenterZ = evaluator.cellCenterZ(minX, minY, minZ, i);
            field[i] = filler.compute((int) cellCenterX, 0, (int) cellCenterZ, context);
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
        for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
            for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                visitor.visit(index++, cellX, 0, cellZ);
            }
        }
    }
    @Override
    public void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
            int blockZ = cellZ * evaluator.cellSize + evaluator.minBlockZ;
            for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                int blockX = cellX * evaluator.cellSize + evaluator.minBlockX;
                double cellCenterX = evaluator.cellCenterX(blockX, 0, blockZ, index),
                       cellCenterZ = evaluator.cellCenterZ(blockX, 0, blockZ, index);
                visitor.visit(index++, (int) cellCenterX, 0, (int) cellCenterZ);
            }
        }
    }
    @Override
    public void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
            int blockZ = cellZ * evaluator.cellSize + evaluator.minBlockZ;
            for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                int blockX = cellX * evaluator.cellSize + evaluator.minBlockX;
                double cellCenterX = evaluator.cellCenterX(blockX, 0, blockZ, index),
                       cellCenterZ = evaluator.cellCenterZ(blockX, 0, blockZ, index);
                visitor.visit(index++, (int) cellCenterX, 0, (int) cellCenterZ, cellX, 0, cellZ);
            }
        }
    }
}
