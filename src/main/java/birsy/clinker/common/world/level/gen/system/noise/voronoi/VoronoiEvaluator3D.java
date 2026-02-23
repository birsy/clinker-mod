package birsy.clinker.common.world.level.gen.system.noise.voronoi;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.BitSet;

public final class VoronoiEvaluator3D implements VoronoiEvaluator {
    static final int[] NEIGHBOR_OFFSETS = {
            // faces
            1,  0,  0,  -1,  0,  0,   0,  1,  0,
            0, -1,  0,   0,  0,  1,   0,  0, -1,
            // edges
            1,  1,  0,   1, -1,  0,  -1,  1,  0,  -1, -1,  0,
            1,  0,  1,   1,  0, -1,  -1,  0,  1,  -1,  0, -1,
            0,  1,  1,   0,  1, -1,   0, -1,  1,   0, -1, -1,
            // corners
            1,  1,  1,   1,  1, -1,   1, -1,  1,   1, -1, -1,
            -1,  1,  1,  -1,  1, -1,  -1, -1,  1,  -1, -1, -1
    };

    final PositionalRandomFactory randomFactory;
    public final int minCellX, minCellY, minCellZ,
                     maxCellX, maxCellY, maxCellZ;
    public final int cellCountX, cellCountY, cellCountZ, cellCount;

    public final int cellSizeXZ, cellSizeY, cellStride;
    public final int minBlockX, minBlockY, minBlockZ,
                     maxBlockX, maxBlockY, maxBlockZ;

    final double[] cellCenters;
    final long[] cellHashes;

    final BitSet filledLayers, fillMask;

    public VoronoiEvaluator3D(PositionalRandomFactory randomFactory, int cellSizeXZ, int cellSizeY, int minCellX, int minCellY, int minCellZ, int maxCellX, int maxCellY, int maxCellZ) {
        this.randomFactory = randomFactory;
        this.minCellX = minCellX; this.maxCellX = maxCellX;
        this.minCellY = minCellY; this.maxCellY = maxCellY;
        this.minCellZ = minCellZ; this.maxCellZ = maxCellZ;
        this.cellCountX = maxCellX - minCellX;
        this.cellCountY = maxCellY - minCellY;
        this.cellCountZ = maxCellZ - minCellZ;
        this.cellCount = cellCountX * cellCountY * cellCountZ;
        this.cellHashes = new long[cellCount];
        this.cellCenters = new double[cellCount * 3];

        this.cellSizeXZ = cellSizeXZ;
        this.cellSizeY = cellSizeY;
        this.minBlockX = minCellX * cellSizeXZ; this.maxBlockX = maxCellX * cellSizeXZ;
        this.minBlockY = minCellY * cellSizeY; this.maxBlockY = maxCellY * cellSizeY;
        this.minBlockZ = minCellZ * cellSizeXZ; this.maxBlockZ = maxCellZ * cellSizeXZ;

        this.cellStride = cellCountX * cellCountZ;
        this.filledLayers = new BitSet(cellCountY);
        this.fillMask = new BitSet(cellCountY);
    }

    @Override
    public double cellCenterX(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 3 + 0]; }
    @Override
    public double cellCenterY(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 3 + 1]; }
    @Override
    public double cellCenterZ(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 3 + 2]; }
    @Override
    public long cellHash(int bX, int bY, int bZ, int cellIndex) { return cellHashes[cellIndex]; }

    @Override
    public long getPackedF1F2Indices(int bX, int bY, int bZ) {
        int cellX = Math.floorDiv(bX, cellSizeXZ),
            cellY = Math.floorDiv(bY, cellSizeY),
            cellZ = Math.floorDiv(bZ, cellSizeXZ);
        int localCellX = cellX - minCellX,
            localCellY = cellY - minCellY,
            localCellZ = cellZ - minCellZ;

        int closestCellIndex = localCellX + localCellZ * cellCountX + localCellY * cellStride;
        double dX = bX - cellCenters[closestCellIndex * 3 + 0],
               dY = bY - cellCenters[closestCellIndex * 3 + 1],
               dZ = bZ - cellCenters[closestCellIndex * 3 + 2];
        double closestDistanceSq = dX * dX + dY * dY + dZ * dZ;

        int secondClosestCellIndex = -1;
        double secondClosestDistanceSq = Double.MAX_VALUE;

        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i += 3) {
            int neighborCellX = cellX + NEIGHBOR_OFFSETS[i + 0],
                    neighborCellY = cellY + NEIGHBOR_OFFSETS[i + 1],
                    neighborCellZ = cellZ + NEIGHBOR_OFFSETS[i + 2];
            int localNeighborCellX = neighborCellX - minCellX,
                    localNeighborCellY = neighborCellY - minCellY,
                    localNeighborCellZ = neighborCellZ - minCellZ;
            int neighborCellIndex = localNeighborCellX + localNeighborCellZ * cellCountX + localNeighborCellY * cellStride;
            double dnX = bX - cellCenters[neighborCellIndex * 3 + 0],
                    dnY = bY - cellCenters[neighborCellIndex * 3 + 1],
                    dnZ = bZ - cellCenters[neighborCellIndex * 3 + 2];
            double neighborDistanceSq = dnX * dnX + dnY * dnY + dnZ * dnZ;
            if (neighborDistanceSq < closestDistanceSq) {
                closestCellIndex = neighborCellIndex;
                closestDistanceSq = neighborDistanceSq;
            } else if (neighborDistanceSq < secondClosestDistanceSq) {
                secondClosestCellIndex = neighborCellIndex;
                secondClosestDistanceSq = neighborDistanceSq;
            }
        }

        return VoronoiEvaluator.packF1F2(closestCellIndex, secondClosestCellIndex);
    }

    @Override
    public void fill(int startY, int endY) {
        int localStartCellY = Math.max(0, Math.floorDiv(startY, cellSizeY) - minCellY),
            localEndCellY = Math.min(cellCountY - 1, Math.ceilDiv(endY, cellSizeY) - minCellY);
        // find unfilled layers
        fillMask.clear();
        fillMask.set(localStartCellY, localEndCellY + 1);
        fillMask.andNot(filledLayers);
        // fill them
        for (int layerStartY = fillMask.nextSetBit(0); layerStartY >= 0; layerStartY = fillMask.nextSetBit(layerStartY + 1)) {
            int layerEndY = fillMask.nextClearBit(layerStartY);
            if (layerEndY == -1) layerEndY = cellCountY;
            fillInternal(layerStartY, layerEndY - 1);
        }
        // finally, set filled layers
        filledLayers.or(fillMask);
    }

    public void fillInternal(int startCellY, int endCellY) {
        int index = startCellY * cellStride;
        for (int localCellY = startCellY; localCellY <= endCellY; localCellY++) {
            int cellY = minCellY + localCellY;
            for (int localCellZ = 0; localCellZ < cellCountZ; localCellZ++) {
                int cellZ = minCellZ + localCellZ;
                for (int localCellX = 0; localCellX < cellCountX; localCellX++) {
                    int cellX = minCellX + localCellX;
                    computeCellAttributes(cellX, cellY, cellZ, index++);
                }
            }
        }
    }

    void computeCellAttributes(int cX, int cY, int cZ, int i) {
        RandomSource cellRandom = randomFactory.at(cX, cY, cZ);
        cellCenters[i * 3 + 0] = (cX + cellRandom.nextDouble()) * cellSizeXZ;
        cellCenters[i * 3 + 1] = (cY + cellRandom.nextDouble()) * cellSizeY;
        cellCenters[i * 3 + 2] = (cZ + cellRandom.nextDouble()) * cellSizeXZ;
        cellHashes[i] = cellRandom.nextLong();
    }
}
