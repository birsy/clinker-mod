package birsy.clinker.common.world.level.gen.system.noise;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public final class VoronoiEvaluator3d implements VoronoiEvaluator {
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

    final PositionalRandomFactory voronoiRandom;
    public final int xzPadding, yPadding = 1;
    public final int xzCellScale, xzCellSize, xzCellCount, paddedXZCellCount;
    public final int yCellScale, yCellSize, yCellCount, paddedYCellCount;
    public final float xzHalfCellSize, yHalfCellSize;

    public final int totalCellCount;

    public final int chunkHeight, minX, minY, minZ;
    public final int minCellX, minCellY, minCellZ;

    final float[] cellCenters;
    final long[] cellHash;

    public VoronoiEvaluator3d(PositionalRandomFactory voronoiRandom, int minX, int minY, int minZ,
                              int chunkHeight, int xzCellScale, int yCellScale, int additionalXZPadding) {
        this.xzPadding = 1 + additionalXZPadding;
        this.voronoiRandom = voronoiRandom;
        this.xzCellScale = xzCellScale;
        this.xzCellSize = 1 << xzCellScale;
        this.xzCellCount = 16 >> xzCellScale;
        this.xzHalfCellSize = xzCellSize * 0.5F;
        this.paddedXZCellCount = xzCellCount + xzPadding * 2;

        this.yCellScale = yCellScale;
        this.yCellSize = 1 << yCellScale;
        this.yCellCount = chunkHeight >> yCellScale;
        this.yHalfCellSize = yCellSize * 0.5F;
        this.paddedYCellCount = yCellCount + yPadding * 2;

        this.chunkHeight = chunkHeight;
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.minCellX = minX >> xzCellScale;
        this.minCellY = minY >> yCellScale;
        this.minCellZ = minZ >> xzCellScale;

        this.totalCellCount = paddedXZCellCount * paddedXZCellCount * paddedYCellCount;
        this.cellCenters = new float[totalCellCount * 3];
        this.cellHash = new long[totalCellCount];
    }

    void computeCellAttributes(int cX, int cY, int cZ, int i) {
        RandomSource cellRandom = voronoiRandom.at(cX + minCellX, cY + minCellY, cZ + minCellZ);
        cellCenters[i * 3 + 0] = (float) cellRandom.triangle((cX << xzCellScale) + xzHalfCellSize, xzHalfCellSize);
        cellCenters[i * 3 + 1] = (float) cellRandom.triangle((cY << yCellScale) + yHalfCellSize, yHalfCellSize);
        cellCenters[i * 3 + 2] = (float) cellRandom.triangle((cZ << xzCellScale) + xzHalfCellSize, xzHalfCellSize);
        cellHash[i] = cellRandom.nextLong();
    }

    public int getNearestCellIndex(int bX, int bY, int bZ) {
        return this.getNearestCellIndexLocal(bX - minX, bY - minY, bZ - minZ);
    }

    public int getNearestCellIndexLocal(int bX, int bY, int bZ) {
        int cX = bX >> xzCellScale,
            cY = bY >> yCellScale,
            cZ = bZ >> xzCellScale;
        int pcX = cX + xzPadding,
            pcY = cY + yPadding,
            pcZ = cZ + xzPadding;
        int closestCellIndex = pcX + pcZ * paddedXZCellCount + pcY * paddedXZCellCount * paddedXZCellCount;

        // technically this can be a false positive, but it is literally a one in a
        // several billion chance so i do not care
        // at worst a chunk takes a few milliseconds longer to generate than usual
        if (cellHash[closestCellIndex] == 0) computeCellAttributes(cX, cY, cZ, closestCellIndex);
        float dX = bX - cellCenters[closestCellIndex * 3 + 0],
              dY = bY - cellCenters[closestCellIndex * 3 + 1],
              dZ = bZ - cellCenters[closestCellIndex * 3 + 2];
        float closestDistanceSq = dX * dX + dY * dY + dZ * dZ;

        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i += 3) {
            int ncX = cX + NEIGHBOR_OFFSETS[i],
                ncY = cY + NEIGHBOR_OFFSETS[i + 1],
                ncZ = cZ + NEIGHBOR_OFFSETS[i + 2];
            int pncX = ncX + xzPadding,
                pncY = ncY + yPadding,
                pncZ = ncZ + xzPadding;
            int neighborCellIndex = pncX + pncZ * paddedXZCellCount + pncY * paddedXZCellCount * paddedXZCellCount;
            if (cellHash[neighborCellIndex] == 0) computeCellAttributes(ncX, ncY, ncZ, neighborCellIndex);
            float dnX = bX - cellCenters[neighborCellIndex * 3 + 0],
                  dnY = bY - cellCenters[neighborCellIndex * 3 + 1],
                  dnZ = bZ - cellCenters[neighborCellIndex * 3 + 2];
            float neighborDistanceSq = dnX * dnX + dnY * dnY + dnZ * dnZ;
            if (neighborDistanceSq < closestDistanceSq) {
                closestCellIndex = neighborCellIndex;
                closestDistanceSq = neighborDistanceSq;
            }
        }
        return closestCellIndex;
    }

    public double cellCenterX(int bX, int bY, int bZ, int cellIndex) { return minX + (double)cellCenters[cellIndex * 3 + 0]; }
    public double cellCenterY(int bX, int bY, int bZ, int cellIndex) { return minY + (double)cellCenters[cellIndex * 3 + 1]; }
    public double cellCenterZ(int bX, int bY, int bZ, int cellIndex) { return minZ + (double)cellCenters[cellIndex * 3 + 2]; }
    public long cellHash(int bX, int bY, int bZ, int cellIndex) { return cellHash[cellIndex]; }
}
