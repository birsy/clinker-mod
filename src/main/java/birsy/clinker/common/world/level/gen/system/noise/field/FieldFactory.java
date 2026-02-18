package birsy.clinker.common.world.level.gen.system.noise.field;


import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;

public sealed interface FieldFactory permits FieldFactory.Standard, FieldFactory.Voronoi {
    public static FieldFactory standard(NoiseFieldType<?> fieldType) { return new Standard(fieldType); }
    public static FieldFactory voronoi(VoronoiDefinition definition) { return new Voronoi(definition); }
    public static FieldFactory voronoi2d(int cellSize) { return voronoi(VoronoiDefinition.twoDimensional(cellSize)); }
    public static FieldFactory voronoi3d(int cellSizeXZ, int cellSizeY) { return new Voronoi(VoronoiDefinition.threeDimensional(cellSizeXZ, cellSizeY)); }

    record Standard(NoiseFieldType<?> fieldType) implements FieldFactory {}
    record Voronoi(VoronoiDefinition definition) implements FieldFactory {}
}
