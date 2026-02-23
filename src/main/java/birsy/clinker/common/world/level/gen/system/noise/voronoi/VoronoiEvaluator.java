package birsy.clinker.common.world.level.gen.system.noise.voronoi;

public sealed interface VoronoiEvaluator permits VoronoiEvaluator2D, VoronoiEvaluator3D {
    void fill(int startY, int endY);
    long getPackedF1F2Indices(int bX, int bY, int bZ);
    default int getNearestCellIndex(int bX, int bY, int bZ) {
        return unpackF1(getPackedF1F2Indices(bX, bY, bZ));
    }

    double cellCenterX(int bX, int bY, int bZ, int cellIndex);
    double cellCenterY(int bX, int bY, int bZ, int cellIndex);
    double cellCenterZ(int bX, int bY, int bZ, int cellIndex);
    long cellHash(int bX, int bY, int bZ, int cellIndex);

    static long packF1F2(int f1, int f2) { return (long)f1 | ((long)f2 << 32); }
    static int unpackF1(long combinedIndex) { return (int) combinedIndex; }
    static int unpackF2(long combinedIndex) { return (int) (combinedIndex >> 32); }
}
