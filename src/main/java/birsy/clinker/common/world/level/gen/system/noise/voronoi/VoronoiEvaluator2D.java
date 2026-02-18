package birsy.clinker.common.world.level.gen.system.noise.voronoi;

import birsy.clinker.core.Clinker;
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
    public final int xCellCount, zCellCount, cellCount;

    public final int cellSize;
    public final double halfCellSize;
    public final int minBlockX, minBlockZ, maxBlockX, maxBlockZ;

    final double[] cellCenters;
    final long[] cellHashes;

    boolean filled = false;

    public VoronoiEvaluator2D(PositionalRandomFactory randomFactory, int cellSize, int minCellX, int minCellZ, int maxCellX, int maxCellZ) {
        this.randomFactory = randomFactory;
        this.minCellX = minCellX; this.maxCellX = maxCellX;
        this.minCellZ = minCellZ; this.maxCellZ = maxCellZ;
        this.xCellCount = maxCellX - minCellX;
        this.zCellCount = maxCellZ - minCellZ;
        this.cellCount = xCellCount * zCellCount;
        this.cellHashes = new long[cellCount];
        this.cellCenters = new double[cellCount * 2];

        this.cellSize = cellSize; this.halfCellSize = cellSize / 2.0F;
        this.minBlockX = minCellX * cellSize; this.maxBlockX = maxCellX * cellSize;
        this.minBlockZ = minCellZ * cellSize; this.maxBlockZ = maxCellZ * cellSize;
    }

    @Override
    public double cellCenterX(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 2 + 0]; }
    @Override
    public double cellCenterY(int bX, int bY, int bZ, int cellIndex) { return bY; }
    @Override
    public double cellCenterZ(int bX, int bY, int bZ, int cellIndex) { return cellCenters[cellIndex * 2 + 1]; }
    @Override
    public long cellHash(int bX, int bY, int bZ, int cellIndex) { return cellHashes[cellIndex]; }

    @Override
    public int getNearestCellIndex(int bX, int bY, int bZ) {
        int cellX = Math.floorDiv(bX, cellSize),
            cellZ = Math.floorDiv(bZ, cellSize);
        int localCellX = cellX - minCellX,
            localCellZ = cellZ - minCellZ;
        int closestCellIndex = localCellX + localCellZ * xCellCount;
        double dX = bX - cellCenters[closestCellIndex * 2 + 0],
               dZ = bZ - cellCenters[closestCellIndex * 2 + 1];
        double closestDistanceSq = dX * dX + dZ * dZ;
        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i += 2) {
            int neighborCellX = cellX + NEIGHBOR_OFFSETS[i + 0],
                neighborCellZ = cellZ + NEIGHBOR_OFFSETS[i + 1];
            int localNeighborCellX = neighborCellX - minCellX,
                localNeighborCellZ = neighborCellZ - minCellZ;
            int neighborCellIndex = localNeighborCellX + localNeighborCellZ * xCellCount;
            double dnX = bX - cellCenters[neighborCellIndex * 2 + 0],
                   dnZ = bZ - cellCenters[neighborCellIndex * 2 + 1];
            double neighborDistanceSq = dnX * dnX + dnZ * dnZ;
            if (neighborDistanceSq < closestDistanceSq) {
                closestCellIndex = neighborCellIndex;
                closestDistanceSq = neighborDistanceSq;
            }
        }
        return closestCellIndex;
    }

    @Override
    public void fill(int startY, int endY) {
        if (filled) return;
        int index = 0;
        for (int localCellZ = 0; localCellZ < zCellCount; localCellZ++) {
            int cellZ = minCellZ + localCellZ;
            for (int localCellX = 0; localCellX < xCellCount; localCellX++) {
                int cellX = minCellX + localCellX;
                computeCellAttributes(cellX, cellZ, index++);
            }
        }
        filled = true;
    }

    void computeCellAttributes(int cX, int cZ, int i) {
        RandomSource cellRandom = randomFactory.at(cX, 0, cZ);
        cellCenters[i * 2 + 0] = (cX + cellRandom.nextDouble()) * cellSize; //cellRandom.triangle((cX * 2 + 1) * halfCellSize, halfCellSize);
        cellCenters[i * 2 + 1] = (cZ + cellRandom.nextDouble()) * cellSize;//cellRandom.triangle((cZ * 2 + 1) * halfCellSize, halfCellSize);
        cellHashes[i] = cellRandom.nextLong();
    }
}
