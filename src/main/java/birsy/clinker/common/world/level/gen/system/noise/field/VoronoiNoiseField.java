package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.VoronoiEvaluator;

import java.util.BitSet;

public final class VoronoiNoiseField extends NoiseField3D {
    final VoronoiEvaluator evaluator;
    public final int xzCellScale, xzCellSize, xzCellCount, xzCellStride;
    public final int yCellScale, yCellSize, yCellCount;
    final BitSet filledLayers, fillMask;
    public final double[] field;

    public VoronoiNoiseField(VoronoiEvaluator evaluator, int chunkHeight, int xzCellScale, int yCellScale, int paddingCells) {
        super(chunkHeight, paddingCells, paddingCells << xzCellScale);

        this.evaluator = evaluator;
        this.xzCellScale = xzCellScale;
        this.xzCellSize = 1 << xzCellScale;
        this.xzCellCount = (CHUNK_WIDTH >> xzCellScale) + 1 + paddingCells * 2; // padding
        this.xzCellStride = this.xzCellCount * this.xzCellCount;

        this.yCellScale = yCellScale;
        this.yCellSize = 1 << yCellScale;
        this.yCellCount = (chunkHeight >> yCellScale) + 1;

        this.field = new double[this.xzCellStride * this.yCellCount];
        this.filledLayers = new BitSet(this.yCellCount);
        this.fillMask = new BitSet(this.yCellCount);
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
        // find unfilled layers
        fillMask.clear();
        fillMask.set(Math.max(0, startY >> yCellScale), Math.min(yCellCount - 1, endY >> yCellScale) + 1);
        fillMask.andNot(filledLayers);
        // fill them
        for (int startCellY = fillMask.nextSetBit(0); startCellY >= 0; startCellY = fillMask.nextSetBit(startCellY + 1)) {
            int endCellY = fillMask.nextClearBit(startCellY);
            if (endCellY == -1) endCellY = yCellCount;
            fillInternal(startCellY, endCellY - 1, minX, minY, minZ, filler, context);
        }
        // finally, set filled layers
        filledLayers.or(fillMask);
    }

    public void fillInternal(int startCellY, int endCellY, int minX, int minY, int minZ, NoiseFieldFiller filler, NoiseContext context) {
        int index = startCellY * xzCellStride;
        for (int cellY = startCellY; cellY <= endCellY; cellY++) {
            int globalY = (cellY << yCellScale) + minY;
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                int globalZ = (cellZ << xzCellScale) + minZ;
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    int globalX = (cellX << xzCellScale) + minX;
                    double cellCenterX = evaluator.cellCenterX(globalX, globalY, globalZ, index),
                           cellCenterY = evaluator.cellCenterY(globalX, globalY, globalZ, index),
                           cellCenterZ = evaluator.cellCenterZ(globalX, globalY, globalZ, index);
                    field[index++] = filler.compute((int) cellCenterX, (int) cellCenterY, (int) cellCenterZ, context);
                }
            }
        }
    }

    @Override
    public void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor) {
        int cellYStart = Math.max(0, minLocalY >> yCellScale),
            cellYEnd = Math.min(yCellCount - 1, maxLocalY >> yCellScale);
        int index = cellYStart * xzCellStride;
        for (int cellY = cellYStart; cellY <= cellYEnd; cellY++) {
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    visitor.visit(index++);
                }
            }
        }
    }
    @Override
    public void byCell(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int cellYStart = Math.max(0, minLocalY >> yCellScale),
            cellYEnd = Math.min(yCellCount - 1, maxLocalY >> yCellScale);
        int index = cellYStart * xzCellStride;
        for (int cellY = cellYStart; cellY <= cellYEnd; cellY++) {
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    visitor.visit(index++, cellX, cellY, cellZ);
                }
            }
        }
    }
    @Override
    public void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int cellYStart = Math.max(0, minLocalY >> yCellScale),
            cellYEnd = Math.min(yCellCount - 1, maxLocalY >> yCellScale);
        int index = cellYStart * xzCellStride;
        for (int cellY = cellYStart; cellY <= cellYEnd; cellY++) {
            int bY = cellY << yCellScale;
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                int bZ = (cellZ << xzCellScale) - paddingBlocks;
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    int bX = (cellX << xzCellScale) - paddingBlocks;
                    double cellCenterX = evaluator.cellCenterX(bX, bY, bZ, index),
                           cellCenterY = evaluator.cellCenterY(bX, bY, bZ, index),
                           cellCenterZ = evaluator.cellCenterZ(bX, bY, bZ, index);
                    visitor.visit(index++, (int) cellCenterX, (int) cellCenterY, (int) cellCenterZ);
                }
            }
        }
    }
    @Override
    public void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int cellYStart = Math.max(0, minLocalY >> yCellScale),
                cellYEnd = Math.min(yCellCount - 1, maxLocalY >> yCellScale);
        int index = cellYStart * xzCellStride;
        for (int cellY = cellYStart; cellY <= cellYEnd; cellY++) {
            int bY = cellY << yCellScale;
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                int bZ = (cellZ << xzCellScale) - paddingBlocks;
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    int bX = (cellX << xzCellScale) - paddingBlocks;
                    double cellCenterX = evaluator.cellCenterX(bX, bY, bZ, index),
                           cellCenterY = evaluator.cellCenterY(bX, bY, bZ, index),
                           cellCenterZ = evaluator.cellCenterZ(bX, bY, bZ, index);
                    visitor.visit(index++, (int) cellCenterX, (int) cellCenterY, (int) cellCenterZ, cellX, cellY, cellZ);
                }
            }
        }
    }
}
