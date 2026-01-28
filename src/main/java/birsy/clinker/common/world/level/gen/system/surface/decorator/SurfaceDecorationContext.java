package birsy.clinker.common.world.level.gen.system.surface.decorator;

public record SurfaceDecorationContext(
        boolean visibleToSky,
        int depth,
        int maxElevationIncrease,
        int maxElevationDecrease,
        double surfaceHeight,
        double surfaceHeightGradient) {
}
