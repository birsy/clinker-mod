package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.surface.shape.SurfaceShape;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public abstract class CliffySurfaceShape extends SurfaceShape {
    final int expectedBorderHeight;
    final int minimumBiomeDistance, maximumBiomeDistance;

    public CliffySurfaceShape(int baseHeight, int minSurfaceY, int maxSurfaceY, int expectedBorderHeight) {
        this(baseHeight, minSurfaceY, maxSurfaceY, expectedBorderHeight, 0, 28);
    }

    public CliffySurfaceShape(int baseHeight, int minSurfaceY, int maxSurfaceY, int expectedBorderHeight, int minimumBiomeDistance, int maximumBiomeDistance) {
        super(baseHeight, minSurfaceY, maxSurfaceY);
        this.expectedBorderHeight = expectedBorderHeight;
        this.minimumBiomeDistance = minimumBiomeDistance;
        this.maximumBiomeDistance = maximumBiomeDistance;
    }

    protected abstract Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer);

    protected Synthesizer createCliffSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withDependencies(biomeSdfSynthesizer, UtilitySynthesizers.ROCKY_CLIFF_Y)
                .build(InterpolatingFieldResolution.FINE_Y,
                        ctx -> {
                            float seaLevelFactor = this.baseHeight > expectedBorderHeight ?
                                    Mth.clampedMap(ctx.dependentValue(1), this.baseHeight, expectedBorderHeight, minimumBiomeDistance, maximumBiomeDistance) :
                                    minimumBiomeDistance;
                            return ctx.dependentValue(0) - seaLevelFactor;
                        }
                );
    }

    @Override
    protected Synthesizer create(Synthesizer biomeSdfSynthesizer, boolean surfaceContainedInChunk) {
        Synthesizer cliffSynthesizer = createCliffSynthesizer(biomeSdfSynthesizer);
        if (surfaceContainedInChunk) {
            return Synthesizer.builder()
                    .withDependencies(cliffSynthesizer, createSurfaceSynthesizer(biomeSdfSynthesizer))
                    .build(InterpolatingFieldResolution.FINE,
                            ctx -> {
                                float surfaceDist = ctx.dependentValue(1);
                                float cliffDist = ctx.dependentValue(0);
                                return Math.max(surfaceDist, cliffDist);
                            }
                    );
        } else {
            return Synthesizer.builder().withDependencies(cliffSynthesizer)
                    .build(InterpolatingFieldResolution.FINE,
                            ctx -> {
                                float surfaceDist = ctx.y() - baseHeight;
                                float cliffDist = ctx.dependentValue(0);
                                return Math.max(surfaceDist, cliffDist);
                            }
                    );
        }
    }
}
