package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator3D;

import java.util.BitSet;

public final class VoronoiNoiseField extends NoiseField3D {
    final VoronoiEvaluator3D evaluator;
    final int minX, minY, minZ;
    final BitSet filledLayers, fillMask;
    public final double[] field;

    public VoronoiNoiseField(VoronoiEvaluator3D evaluator, int minX, int minY, int minZ, int chunkHeight) {
        super(chunkHeight, 0, 0);
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.evaluator = evaluator;
        this.field = new double[evaluator.cellCount];
        this.filledLayers = new BitSet(evaluator.yCellCount);
        this.fillMask = new BitSet(evaluator.yCellCount);
    }

    @Override
    public double[] array() {
        return field;
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int cellIndex = evaluator.getNearestCellIndex(x + minX, y + minY, z + minZ);
        return field[cellIndex];
    }

    @Override
    public void fill(int minLocalY, int maxLocalY, int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller filler) {
        this.evaluator.fill(minLocalY + minY, maxLocalY + minY);
        int startCellY = Math.floorDiv(minLocalY + minY, evaluator.yCellSize),
            endCellY = Math.ceilDiv(maxLocalY + minY, evaluator.yCellSize);
        int localStartCellY = Math.max(0, startCellY - evaluator.minCellY),
            localEndCellY = Math.min(evaluator.yCellCount - 1, endCellY - evaluator.minCellY);
        // find unfilled layers
        fillMask.clear();
        fillMask.set(localStartCellY, localEndCellY + 1);
        fillMask.andNot(filledLayers);
        // fill them
        for (int layerStartY = fillMask.nextSetBit(0); layerStartY >= 0; layerStartY = fillMask.nextSetBit(layerStartY + 1)) {
            int layerEndY = fillMask.nextClearBit(layerStartY);
            if (layerEndY == -1) layerEndY = evaluator.yCellCount;
            fillInternal(layerStartY, layerEndY - 1, minX, minY, minZ, filler, context);
        }
        // finally, set filled layers
        filledLayers.or(fillMask);
    }

    public void fillInternal(int startCellY, int endCellY, int minX, int minY, int minZ, NoiseFieldFiller filler, NoiseContext context) {
        int index = startCellY * evaluator.cellStride;
        for (int cellY = startCellY; cellY <= endCellY; cellY++) {
            for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
                for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                    double cellCenterX = evaluator.cellCenterX(minX, minY, minZ, index),
                           cellCenterY = evaluator.cellCenterY(minX, minY, minZ, index),
                           cellCenterZ = evaluator.cellCenterZ(minX, minY, minZ, index);
                    field[index++] = filler.compute((int) cellCenterX, (int) cellCenterY, (int) cellCenterZ, context);
                }
            }
        }
    }

    @Override
    public void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor) {
        int startCellY = Math.floorDiv(minLocalY + minY, evaluator.yCellSize),
            endCellY = Math.ceilDiv(maxLocalY + minY, evaluator.yCellSize);
        int localStartCellY = Math.max(0, startCellY - evaluator.minCellY),
            localEndCellY = Math.min(evaluator.yCellCount - 1, endCellY - evaluator.minCellY);
        int index = localStartCellY * evaluator.cellStride;
        for (int cellY = localStartCellY; cellY <= localEndCellY; cellY++) {
            for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
                for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                    visitor.visit(index++);
                }
            }
        }
    }
    @Override
    public void byCell(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int startCellY = Math.floorDiv(minLocalY + minY, evaluator.yCellSize),
            endCellY = Math.ceilDiv(maxLocalY + minY, evaluator.yCellSize);
        int localStartCellY = Math.max(0, startCellY - evaluator.minCellY),
            localEndCellY = Math.min(evaluator.yCellCount - 1, endCellY - evaluator.minCellY);
        int index = localStartCellY * evaluator.cellStride;
        for (int cellY = localStartCellY; cellY <= localEndCellY; cellY++) {
            for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
                for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                    visitor.visit(index++, cellX, cellY, cellZ);
                }
            }
        }
    }
    @Override
    public void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int startCellY = Math.floorDiv(minLocalY + minY, evaluator.yCellSize),
            endCellY = Math.ceilDiv(maxLocalY + minY, evaluator.yCellSize);
        int localStartCellY = Math.max(0, startCellY - evaluator.minCellY),
            localEndCellY = Math.min(evaluator.yCellCount - 1, endCellY - evaluator.minCellY);
        int index = localStartCellY * evaluator.cellStride;
        for (int cellY = localStartCellY; cellY <= localEndCellY; cellY++) {
            for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
                for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                    double cellCenterX = evaluator.cellCenterX(minX, minY, minZ, index),
                           cellCenterY = evaluator.cellCenterY(minX, minY, minZ, index),
                           cellCenterZ = evaluator.cellCenterZ(minX, minY, minZ, index);
                    visitor.visit(index++, (int) cellCenterX, (int) cellCenterY, (int) cellCenterZ);
                }
            }
        }
    }
    @Override
    public void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int startCellY = Math.floorDiv(minLocalY + minY, evaluator.yCellSize),
            endCellY = Math.ceilDiv(maxLocalY + minY, evaluator.yCellSize);
        int localStartCellY = Math.max(0, startCellY - evaluator.minCellY),
            localEndCellY = Math.min(evaluator.yCellCount - 1, endCellY - evaluator.minCellY);
        int index = localStartCellY * evaluator.cellStride;
        for (int cellY = localStartCellY; cellY <= localEndCellY; cellY++) {
            for (int cellZ = 0; cellZ < evaluator.zCellCount; cellZ++) {
                for (int cellX = 0; cellX < evaluator.xCellCount; cellX++) {
                    double cellCenterX = evaluator.cellCenterX(minX, minY, minZ, index),
                           cellCenterY = evaluator.cellCenterY(minX, minY, minZ, index),
                           cellCenterZ = evaluator.cellCenterZ(minX, minY, minZ, index);
                    visitor.visit(index++, (int) cellCenterX, (int) cellCenterY, (int) cellCenterZ, cellX, cellY, cellZ);
                }
            }
        }
    }
}
