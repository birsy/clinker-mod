package birsy.clinker.common.world.level.gen.system.noise;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class VoronoiEvaluator {
    static final int[][] NEIGHBOR_OFFSETS = {
            // faces
            { 1,  0,  0}, {-1,  0,  0}, { 0,  1,  0}, { 0, -1,  0}, { 0,  0,  1}, { 0,  0, -1},
            // edges
            { 1,  1,  0}, { 1, -1,  0}, {-1,  1,  0}, {-1, -1,  0},
            { 1,  0,  1}, { 1,  0, -1}, {-1,  0,  1}, {-1,  0, -1},
            { 0,  1,  1}, { 0,  1, -1}, { 0, -1,  1}, { 0, -1, -1},
            // corners
            { 1,  1,  1}, { 1,  1, -1}, { 1, -1,  1}, { 1, -1, -1},
            {-1,  1,  1}, {-1,  1, -1}, {-1, -1,  1}, {-1, -1, -1},
    };

    final PositionalRandomFactory voronoiRandom;
    public final int xzPadding, yPadding = 1;
    public final int xzCellScale, xzCellSize, xzCellCount;
    public final int yCellScale, yCellSize, yCellCount;
    public final int cellCount;

    public final int chunkHeight, minX, minY, minZ;
    public final int minCellX, minCellY, minCellZ;

    final float[] cellCenterX, cellCenterY, cellCenterZ;
    final long[] cellHash;

    public VoronoiEvaluator(PositionalRandomFactory voronoiRandom, int minX, int minY, int minZ,
                            int chunkHeight, int xzCellScale, int yCellScale, int additionalXZPadding) {
        this.xzPadding = 1 + additionalXZPadding;
        this.voronoiRandom = voronoiRandom;
        this.xzCellScale = xzCellScale;
        this.xzCellSize = 1 << xzCellScale;
        this.xzCellCount = 16 >> xzCellScale;

        this.yCellScale = yCellScale;
        this.yCellSize = 1 << yCellScale;
        this.yCellCount = chunkHeight >> yCellScale;

        this.chunkHeight = chunkHeight;
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.minCellX = minX >> xzCellScale;
        this.minCellY = minY >> yCellScale;
        this.minCellZ = minZ >> xzCellScale;

        this.cellCount = (xzCellCount + xzPadding * 2) * (xzCellCount + xzPadding * 2) * (yCellCount + yPadding * 2);
        this.cellCenterX = new float[cellCount];
        this.cellCenterY = new float[cellCount];
        this.cellCenterZ = new float[cellCount];
        this.cellHash = new long[cellCount];
    }

    void computeCellAttributes(int cX, int cY, int cZ, int i) {
        RandomSource cellRandom = voronoiRandom.at(cX + minCellX, cY + minCellY, cZ + minCellZ);
        cellCenterX[i] = (float) cellRandom.triangle(cX * (xzCellCount + 0.5F), xzCellSize);
        cellCenterY[i] = (float) cellRandom.triangle(cY * (yCellCount  + 0.5F), yCellSize);
        cellCenterZ[i] = (float) cellRandom.triangle(cZ * (xzCellCount + 0.5F), xzCellSize);
        cellHash[i] = cellRandom.nextLong();
    }

    public int getNearestCellIndex(int bX, int bY, int bZ) {
        return this.getNearestCellIndexLocal(bX - minX, bY - minY, bZ - minZ);
    }

    public int getNearestCellIndexLocal(int bX, int bY, int bZ) {
        int cX = bX >> xzCellScale, cY = bY >> yCellScale, cZ = bZ >> xzCellScale;
        int pcX = cX + xzPadding, pcY = cY + yPadding, pcZ = cZ + xzPadding;

        int closestCellIndex = pcX + pcZ * xzCellCount + pcY * xzCellCount * xzCellCount;
        if (cellHash[closestCellIndex] == 0) computeCellAttributes(cX, cY, cZ, closestCellIndex);
        float closestDistanceSq = Mth.lengthSquared(
                bX - cellCenterX[closestCellIndex],
                bY - cellCenterY[closestCellIndex],
                bZ - cellCenterZ[closestCellIndex]
        );
        for (int[] neighborOffset : NEIGHBOR_OFFSETS) {
            int ncX = cX + neighborOffset[0], ncY = cY + neighborOffset[1], ncZ = cZ + neighborOffset[2];
            int pncX = ncX + xzPadding, pncY = ncY + yPadding, pncZ = ncZ + xzPadding;
            int neighborCellIndex = pncX + pncZ * xzCellCount + pncY * xzCellCount * xzCellCount;
            if (cellHash[neighborCellIndex] == 0) computeCellAttributes(cX, cY, cZ, neighborCellIndex);
            float neighborDistanceSq = Mth.lengthSquared(
                    bX - cellCenterX[neighborCellIndex],
                    bY - cellCenterY[neighborCellIndex],
                    bZ - cellCenterZ[neighborCellIndex]
            );
            if (neighborDistanceSq < closestDistanceSq) {
                closestCellIndex = neighborCellIndex;
                closestDistanceSq = neighborDistanceSq;
            }
        }
        return closestCellIndex;
    }

    public double cellCenterX(int cellIndex) { return minX + (double) cellCenterX[cellIndex]; }
    public double cellCenterY(int cellIndex) { return minY + (double) cellCenterY[cellIndex]; }
    public double cellCenterZ(int cellIndex) { return minZ + (double) cellCenterZ[cellIndex]; }
    public long cellHash(int cellIndex) { return cellHash[cellIndex]; }
}
