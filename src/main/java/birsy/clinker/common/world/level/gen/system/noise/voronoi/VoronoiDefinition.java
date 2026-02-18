package birsy.clinker.common.world.level.gen.system.noise.voronoi;

import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public sealed interface VoronoiDefinition permits VoronoiDefinition.TwoDimensional, VoronoiDefinition.ThreeDimensional {
    static VoronoiDefinition.TwoDimensional twoDimensional(int cellSize) {
        return new TwoDimensional(cellSize);
    }
    static VoronoiDefinition.ThreeDimensional threeDimensional(int xzCellSize, int yCellSize) {
        return new ThreeDimensional(xzCellSize, yCellSize);
    }

    VoronoiEvaluator createEvaluatorForChunk(PositionalRandomFactory baseRandom, int minX, int minY, int minZ, int chunkHeight, int padding);

    record TwoDimensional(int cellSize) implements VoronoiDefinition {
        @Override
        public VoronoiEvaluator createEvaluatorForChunk(PositionalRandomFactory baseRandom, int minX, int minY, int minZ, int chunkHeight, int padding) {
            int minCellX = Math.floorDiv(minX - padding, cellSize) - 1,
                maxCellX = Math.ceilDiv(minX + 16 + padding, cellSize) + 2;
            int minCellZ = Math.floorDiv(minZ - padding, cellSize) - 1,
                maxCellZ = Math.ceilDiv(minZ + 16 + padding, cellSize) + 2;
            return new VoronoiEvaluator2D(baseRandom, cellSize, minCellX, minCellZ, maxCellX, maxCellZ);
        }
    }
    record ThreeDimensional(int xzCellSize, int yCellSize) implements VoronoiDefinition {
        @Override
        public VoronoiEvaluator createEvaluatorForChunk(PositionalRandomFactory baseRandom, int minX, int minY, int minZ, int chunkHeight, int padding) {
            int minCellX = Math.floorDiv(minX - padding, xzCellSize) - 1,
                maxCellX = Math.ceilDiv(minX + 16 + padding, xzCellSize) + 2;
            int minCellZ = Math.floorDiv(minZ - padding, xzCellSize) - 1,
                maxCellZ = Math.ceilDiv(minZ + 16 + padding, xzCellSize) + 2;
            int minCellY = Math.floorDiv(minY, yCellSize) - 1,
                maxCellY = Math.ceilDiv(minY + chunkHeight, yCellSize) + 2;
            return new VoronoiEvaluator3D(baseRandom, xzCellSize, yCellSize, minCellX, minCellY, minCellZ, maxCellX, maxCellY, maxCellZ);
        }
    }
}