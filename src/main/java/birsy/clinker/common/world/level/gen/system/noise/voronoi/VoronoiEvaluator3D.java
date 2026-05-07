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

    public final int cellSize, cellStride;
    public final double yScale, invYScale;

    public final int minBlockX, minBlockY, minBlockZ,
                     maxBlockX, maxBlockY, maxBlockZ;

    final double[] cellCenters;
    final long[] cellHashes;

    final BitSet filledLayers, fillMask;


    public VoronoiEvaluator3D(PositionalRandomFactory randomFactory,
                              int cellSize, double yScale,
                              int minCellX, int minCellY, int minCellZ,
                              int maxCellX, int maxCellY, int maxCellZ) {
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

        this.cellSize = cellSize;
        this.minBlockX = minCellX * cellSize; this.maxBlockX = maxCellX * cellSize;
        this.minBlockY = minCellY * cellSize; this.maxBlockY = maxCellY * cellSize;
        this.minBlockZ = minCellZ * cellSize; this.maxBlockZ = maxCellZ * cellSize;

        this.cellStride = cellCountX * cellCountZ;

        this.yScale = yScale;
        this.invYScale = 1.0 / yScale;

        this.filledLayers = new BitSet(cellCountY);
        this.fillMask = new BitSet(cellCountY);
    }

    @Override
    public double cellCenterX(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 3 + 0]; }
    @Override
    public double cellCenterY(int bX, int bY, int bZ, int cellIndex, boolean rescale) {
        double scaledY = cellCenters[cellIndex * 3 + 1];
        return rescale ? scaledY * invYScale : scaledY;
    }
    @Override
    public double cellCenterZ(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 3 + 2]; }
    @Override
    public long cellHash(int bX, int bY, int bZ, int cellIndex) { return cellHashes[cellIndex]; }

    @Override
    public double scaledInputY(int bY) { return bY * yScale; }

    @Override
    public long getPackedF1F2Indices(int bX, int bY, int bZ) {
        double scaledY = bY * yScale;

        int cellX = Math.floorDiv(bX, cellSize),
            cellY = Math.floorDiv((int) scaledY, cellSize),
            cellZ = Math.floorDiv(bZ, cellSize);
        int localCellX = cellX - minCellX,
            localCellY = cellY - minCellY,
            localCellZ = cellZ - minCellZ;

        int closestIndex = localCellX + localCellZ * cellCountX + localCellY * cellStride;
        double dX = bX  - cellCenters[closestIndex * 3 + 0],
                dY = scaledY - cellCenters[closestIndex * 3 + 1], // compare in scaled space
                dZ = bZ  - cellCenters[closestIndex * 3 + 2];
        double closestDistSq = dX*dX + dY*dY + dZ*dZ;

        int secondIndex = -1;
        double secondDistSq = Double.MAX_VALUE;

        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i += 3) {
            int nCellX = cellX + NEIGHBOR_OFFSETS[i],
                nCellY = cellY + NEIGHBOR_OFFSETS[i+1],
                nCellZ = cellZ + NEIGHBOR_OFFSETS[i+2];
            int lnX = nCellX - minCellX,
                lnY = nCellY - minCellY,
                lnZ = nCellZ - minCellZ;
            int neighborIndex = lnX + lnZ * cellCountX + lnY * cellStride;
            double dnX = bX  - cellCenters[neighborIndex * 3 + 0],
                   dnY = scaledY - cellCenters[neighborIndex * 3 + 1],
                   dnZ = bZ  - cellCenters[neighborIndex * 3 + 2];
            double neighborDistSq = dnX*dnX + dnY*dnY + dnZ*dnZ;
            if (neighborDistSq < closestDistSq) {
                if (closestDistSq < secondDistSq) {
                    secondIndex = closestIndex;
                    secondDistSq = closestDistSq;
                }
                closestIndex = neighborIndex;
                closestDistSq = neighborDistSq;
            } else if (neighborDistSq < secondDistSq) {
                secondIndex = neighborIndex;
                secondDistSq = neighborDistSq;
            }
        }

        return VoronoiEvaluator.packF1F2(closestIndex, secondIndex);
    }

    @Override
    public void fill(int startY, int endY) {
        int localStartCellY = Math.max(0, Math.floorDiv((int)(startY * yScale), cellSize) - minCellY);
        int localEndCellY = Math.min(cellCountY - 1, Math.ceilDiv((int)(endY * yScale), cellSize) - minCellY);
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
        cellCenters[i * 3 + 0] = (cX + cellRandom.nextDouble()) * cellSize;
        cellCenters[i * 3 + 1] = (cY + cellRandom.nextDouble()) * cellSize;
        cellCenters[i * 3 + 2] = (cZ + cellRandom.nextDouble()) * cellSize;
        cellHashes[i] = cellRandom.nextLong();
    }
}
