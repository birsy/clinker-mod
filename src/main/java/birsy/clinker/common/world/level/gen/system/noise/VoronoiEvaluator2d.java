package birsy.clinker.common.world.level.gen.system.noise;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public final class VoronoiEvaluator2d implements VoronoiEvaluator {
    static final int[] NEIGHBOR_OFFSETS = {
            // edges
           -1,  0,   1,  0,   0, -1,   0,  1,
            // corners
           -1, -1,   1, -1,  -1,  1,   1,  1
    };

    final PositionalRandomFactory voronoiRandom;
    public final int padding;
    public final int cellScale, cellSize, cellCount, paddedCellCount;
    public final float xzHalfCellSize;

    public final int totalCellCount;

    public final int minX, minZ;
    public final int minCellX, minCellZ;

    final float[] cellCenters;
    final long[] cellHash;

    public VoronoiEvaluator2d(PositionalRandomFactory voronoiRandom, int minX, int minZ, int cellScale, int additionalPadding) {
        this.voronoiRandom = voronoiRandom;
        this.cellScale = cellScale;
        this.cellSize = 1 << cellScale;
        this.cellCount = 16 >> cellScale;
        this.xzHalfCellSize = cellSize * 0.5F;
        this.padding = 1 + additionalPadding;
        this.paddedCellCount = cellCount * padding * 2;

        this.minX = minX; this.minZ = minZ;
        this.minCellX = minX >> cellScale;
        this.minCellZ = minZ >> cellScale;

        this.totalCellCount = cellCount * cellCount;
        this.cellCenters = new float[totalCellCount * 2];
        this.cellHash = new long[totalCellCount];
    }

    void computeCellAttributes(int cX, int cZ, int i) {
        RandomSource cellRandom = voronoiRandom.at(cX + minCellX, 0, cZ + minCellZ);
        cellCenters[i * 2 + 0] = (float) cellRandom.triangle((cX << cellScale) + xzHalfCellSize, xzHalfCellSize);
        cellCenters[i * 2 + 2] = (float) cellRandom.triangle((cZ << cellScale) + xzHalfCellSize, xzHalfCellSize);
        cellHash[i] = cellRandom.nextLong();
    }

    public int getNearestCellIndex(int bX, int bY, int bZ) {
        return this.getNearestCellIndexLocal(bX - minX, 0, bZ - minZ);
    }

    public int getNearestCellIndexLocal(int bX, int bY, int bZ) {
        int cX = bX >> cellScale, cZ = bZ >> cellScale;
        int pcX = cX + padding, pcZ = cZ + padding;

        int closestCellIndex = pcX + pcZ * paddedCellCount;

        // technically this can be a false positive, but it is literally a one in a
        // several billion chance so i do not care
        // at worst a chunk takes a few milliseconds longer to generate than usual
        if (cellHash[closestCellIndex] == 0) computeCellAttributes(cX, cZ, closestCellIndex);

        float dX = bX - cellCenters[closestCellIndex * 2 + 0],
              dZ = bZ - cellCenters[closestCellIndex * 2 + 1];
        float closestDistanceSq = dX * dX + dZ * dZ;
        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i += 2) {
            int ncX = cX + NEIGHBOR_OFFSETS[i + 0],
                ncZ = cZ + NEIGHBOR_OFFSETS[i + 1];
            int pncX = ncX + padding,
                pncZ = ncZ + padding;
            int neighborCellIndex = pncX + pncZ * paddedCellCount;
            if (cellHash[neighborCellIndex] == 0) computeCellAttributes(ncX, ncZ, neighborCellIndex);
            float dnX = bX - cellCenters[neighborCellIndex * 2 + 0],
                  dnZ = bZ - cellCenters[neighborCellIndex * 2 + 1];
            float neighborDistanceSq = dnX * dnX + dnZ * dnZ;
            if (neighborDistanceSq < closestDistanceSq) {
                closestCellIndex = neighborCellIndex;
                closestDistanceSq = neighborDistanceSq;
            }
        }
        return closestCellIndex;
    }

    public double cellCenterX(int bX, int bY, int bZ, int cellIndex) { return minX + (double)cellCenters[cellIndex * 2 + 0]; }
    public double cellCenterY(int bX, int bY, int bZ, int cellIndex) { return bY; }
    public double cellCenterZ(int bX, int bY, int bZ, int cellIndex) { return minZ + (double)cellCenters[cellIndex * 2 + 1]; }
    public long cellHash(int bX, int bY, int bZ, int cellIndex) { return cellHash[cellIndex]; }
}
