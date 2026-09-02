package birsy.clinker.common.world.level.gen.system.noise.field;


import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;

public sealed interface FieldFactory permits FieldFactory.Standard, FieldFactory.Voronoi {
    static FieldFactory standard(NoiseFieldType<?> fieldType) { return new Standard(fieldType); }
    static FieldFactory voronoi(VoronoiDefinition definition) { return new Voronoi(definition); }
    static FieldFactory voronoi2d(int cellSize) { return voronoi(VoronoiDefinition.twoDimensional(cellSize)); }
    static FieldFactory voronoi3d(int cellSize, double yCellScale) { return new Voronoi(VoronoiDefinition.threeDimensional(cellSize, yCellScale)); }

    record Standard(NoiseFieldType<?> fieldType) implements FieldFactory {}
    record Voronoi(VoronoiDefinition definition) implements FieldFactory {}
}
