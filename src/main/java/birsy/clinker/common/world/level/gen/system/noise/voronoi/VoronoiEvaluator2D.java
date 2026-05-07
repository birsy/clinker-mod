package birsy.clinker.common.world.level.gen.system.noise.voronoi;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public final class VoronoiEvaluator2D implements VoronoiEvaluator {
    static final int[] NEIGHBOR_OFFSETS = {
            // edges
            -1,  0,   1,  0,   0, -1,   0,  1,
            // corners
            -1, -1,   1, -1,  -1,  1,   1,  1
    };

    final PositionalRandomFactory randomFactory;
    public final int minCellX, minCellZ, maxCellX, maxCellZ;
    public final int cellCountX, cellCountZ, cellCount;

    public final int cellSize;
    public final int minBlockX, minBlockZ, maxBlockX, maxBlockZ;

    final double[] cellCenters;
    final long[] cellHashes;

    boolean filled = false;

    public VoronoiEvaluator2D(PositionalRandomFactory randomFactory, int cellSize, int minCellX, int minCellZ, int maxCellX, int maxCellZ) {
        this.randomFactory = randomFactory;
        this.minCellX = minCellX; this.maxCellX = maxCellX;
        this.minCellZ = minCellZ; this.maxCellZ = maxCellZ;
        this.cellCountX = maxCellX - minCellX;
        this.cellCountZ = maxCellZ - minCellZ;
        this.cellCount = cellCountX * cellCountZ;
        this.cellHashes = new long[cellCount];
        this.cellCenters = new double[cellCount * 2];

        this.cellSize = cellSize;
        this.minBlockX = minCellX * cellSize; this.maxBlockX = maxCellX * cellSize;
        this.minBlockZ = minCellZ * cellSize; this.maxBlockZ = maxCellZ * cellSize;
    }

    @Override
    public double cellCenterX(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 2 + 0]; }
    @Override
    public double cellCenterY(int bX, int bY, int bZ, int cellIndex, boolean rescale) { return bY; }
    @Override
    public double cellCenterZ(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 2 + 1]; }
    @Override
    public long cellHash(int bX, int bY, int bZ, int cellIndex) { return cellHashes[cellIndex]; }

    @Override
    public long getPackedF1F2Indices(int bX, int bY, int bZ) {
        int cellX = Math.floorDiv(bX, cellSize),
            cellZ = Math.floorDiv(bZ, cellSize);
        int localCellX = cellX - minCellX,
            localCellZ = cellZ - minCellZ;

        int closestCellIndex = localCellX + localCellZ * cellCountX;
        double dX = bX - cellCenters[closestCellIndex * 2 + 0],
               dZ = bZ - cellCenters[closestCellIndex * 2 + 1];
        double closestDistanceSq = dX * dX + dZ * dZ;

        int secondClosestCellIndex = -1;
        double secondClosestDistanceSq = Double.MAX_VALUE;

        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i += 2) {
            int neighborCellX = cellX + NEIGHBOR_OFFSETS[i + 0],
                neighborCellZ = cellZ + NEIGHBOR_OFFSETS[i + 1];
            int localNeighborCellX = neighborCellX - minCellX,
                localNeighborCellZ = neighborCellZ - minCellZ;
            int neighborCellIndex = localNeighborCellX + localNeighborCellZ * cellCountX;
            double dnX = bX - cellCenters[neighborCellIndex * 2 + 0],
                   dnZ = bZ - cellCenters[neighborCellIndex * 2 + 1];
            double neighborDistanceSq = dnX * dnX + dnZ * dnZ;
            if (neighborDistanceSq < closestDistanceSq) {
                if (closestDistanceSq < secondClosestDistanceSq) {
                    secondClosestCellIndex = closestCellIndex;
                    secondClosestDistanceSq = closestDistanceSq;
                }
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
        if (filled) return;
        int index = 0;
        for (int localCellZ = 0; localCellZ < cellCountZ; localCellZ++) {
            int cellZ = minCellZ + localCellZ;
            for (int localCellX = 0; localCellX < cellCountX; localCellX++) {
                int cellX = minCellX + localCellX;
                computeCellAttributes(cellX, cellZ, index++);
            }
        }
        filled = true;
    }

    void computeCellAttributes(int cX, int cZ, int i) {
        RandomSource cellRandom = randomFactory.at(cX, 0, cZ);
        cellCenters[i * 2 + 0] = (cX + cellRandom.nextDouble()) * cellSize;
        cellCenters[i * 2 + 1] = (cZ + cellRandom.nextDouble()) * cellSize;
        cellHashes[i] = cellRandom.nextLong();
    }
}
