package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.surface.shape.SurfaceShape;
import net.minecraft.util.Mth;

public abstract class SmoothEdgeSurfaceShape extends SurfaceShape {
    final int expectedBorderHeight;
    final int minimumBiomeDistance, maximumBiomeDistance;

    public SmoothEdgeSurfaceShape(int baseHeight, int minSurfaceY, int maxSurfaceY, int expectedBorderHeight) {
        this(baseHeight, minSurfaceY, maxSurfaceY, expectedBorderHeight, 0, 28);
    }

    public SmoothEdgeSurfaceShape(int baseHeight, int minSurfaceY, int maxSurfaceY, int expectedBorderHeight, int minimumBiomeDistance, int maximumBiomeDistance) {
        super(baseHeight, minSurfaceY, maxSurfaceY);
        this.expectedBorderHeight = expectedBorderHeight;
        this.minimumBiomeDistance = minimumBiomeDistance;
        this.maximumBiomeDistance = maximumBiomeDistance;
    }

    protected abstract Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer, Synthesizer baseHeightMap, boolean surfaceContainedInChunk);

    protected Synthesizer createHeightmapSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder("smooth_edge_base_heightmap")
                .withDependencies(biomeSdfSynthesizer)
                .build(InterpolatingFieldResolution.COARSE_2D,
                        ctx -> {
                            float height = Mth.clampedMap(ctx.dependentValue(0), minimumBiomeDistance, maximumBiomeDistance, 0, 1);
                            height = (float) Mth.smoothstep(height);
                            return Mth.lerp(height, baseHeight, expectedBorderHeight - 3);
                        }
                );
    }

    @Override
    protected Synthesizer create(Synthesizer biomeSdfSynthesizer, boolean surfaceContainedInChunk) {
        return Synthesizer.builder("smooth_edge_combiner")
                .withDependencies(biomeSdfSynthesizer, createSurfaceSynthesizer(biomeSdfSynthesizer, createHeightmapSynthesizer(biomeSdfSynthesizer), surfaceContainedInChunk))
                .build(InterpolatingFieldResolution.COARSE_X_FINE_Y,
                    ctx -> {
                        return Math.max(
                                ctx.dependentValue(0) - 32,
                                ctx.dependentValue(1)
                        );
                    }
                );
    }
}
