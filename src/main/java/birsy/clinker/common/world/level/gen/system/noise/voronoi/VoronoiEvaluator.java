package birsy.clinker.common.world.level.gen.system.noise.voronoi;

import net.minecraft.util.Mth;

public sealed interface VoronoiEvaluator permits VoronoiEvaluator2D, VoronoiEvaluator3D {
    void fill(int startY, int endY);
    long getPackedF1F2Indices(int bX, int bY, int bZ);

    double cellCenterX(int bX, int bY, int bZ, int cellIndex);
    double cellCenterY(int bX, int bY, int bZ, int cellIndex);
    double cellCenterZ(int bX, int bY, int bZ, int cellIndex);
    long cellHash(int bX, int bY, int bZ, int cellIndex);

    default int getNearestCellIndex(int bX, int bY, int bZ) {
        return unpackF1(getPackedF1F2Indices(bX, bY, bZ));
    }

    default double distanceToBorder(int bX, int bY, int bZ) {
        long packed = getPackedF1F2Indices(bX, bY, bZ);
        int f1 = unpackF1(packed), f2 = unpackF2(packed);

        double f1x = cellCenterX(bX, bY, bZ, f1), f1y = cellCenterY(bX, bY, bZ, f1), f1z = cellCenterZ(bX, bY, bZ, f1);
        double f1DistanceSquared = Mth.lengthSquared(bX - f1x, bY - f1y, bZ - f1z);

        double f2x = cellCenterX(bX, bY, bZ, f2), f2y = cellCenterY(bX, bY, bZ, f2), f2z = cellCenterZ(bX, bY, bZ, f2);
        double f2DistanceSquared = Mth.lengthSquared(bX - f2x, bY - f2y, bZ - f2z);

        double f1f2DistanceSquared = Mth.lengthSquared(f2x - f1x, f2y - f1y, f2z - f1z);

        return (f2DistanceSquared - f1DistanceSquared) / (2.0 * f1f2DistanceSquared);
    }

    static long packF1F2(int f1, int f2) { return (long)f1 | ((long)f2 << 32); }
    static int unpackF1(long combinedIndex) { return (int) combinedIndex; }
    static int unpackF2(long combinedIndex) { return (int) (combinedIndex >> 32); }
}
