package birsy.clinker.common.world.level.gen.system.noise.voronoi;

public sealed interface VoronoiEvaluator permits VoronoiEvaluator2D, VoronoiEvaluator3D {
    void fill(int startY, int endY);
    int getNearestCellIndex(int bX, int bY, int bZ);
    double cellCenterX(int bX, int bY, int bZ, int cellIndex);
    double cellCenterY(int bX, int bY, int bZ, int cellIndex);
    double cellCenterZ(int bX, int bY, int bZ, int cellIndex);
    long cellHash(int bX, int bY, int bZ, int cellIndex);
}
