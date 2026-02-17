package birsy.clinker.common.world.level.gen.system.noise;

public sealed interface VoronoiEvaluator permits VoronoiEvaluator2d, VoronoiEvaluator3d {
    int getNearestCellIndex(int bX, int bY, int bZ);
    int getNearestCellIndexLocal(int bX, int bY, int bZ);

    double cellCenterX(int bX, int bY, int bZ, int cellIndex);
    double cellCenterY(int bX, int bY, int bZ, int cellIndex);
    double cellCenterZ(int bX, int bY, int bZ, int cellIndex);
    long cellHash(int bX, int bY, int bZ, int cellIndex);
}
