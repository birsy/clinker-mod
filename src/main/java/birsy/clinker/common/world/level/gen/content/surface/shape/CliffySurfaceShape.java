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
    public CliffySurfaceShape(int baseHeight, int minSurfaceY, int maxSurfaceY, int expectedBorderHeight) {
        super(baseHeight, minSurfaceY, maxSurfaceY);
        this.expectedBorderHeight = expectedBorderHeight;
    }

    protected abstract Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer);

    protected Synthesizer createCliffSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withDependencies(biomeSdfSynthesizer, UtilitySynthesizers.ROCKY_CLIFF_Y)
                .build(InterpolatingFieldResolution.FINE_Y,
                        ctx -> {
                            double seaLevelFactor = this.baseHeight > expectedBorderHeight ?
                                    Mth.clampedMap(ctx.dependentValue(1), this.baseHeight, expectedBorderHeight, 0, 1) : 0;
                            return ctx.dependentValue(0) - seaLevelFactor * 28;
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
                                double surfaceDist = ctx.dependentValue(1);
                                double cliffDist = ctx.dependentValue(0);
                                return Math.max(surfaceDist, cliffDist);
                            }
                    );
        } else {
            return Synthesizer.builder().withDependencies(cliffSynthesizer)
                    .build(InterpolatingFieldResolution.FINE,
                            ctx -> {
                                double surfaceDist = ctx.y() - baseHeight;
                                double cliffDist = ctx.dependentValue(0);
                                return Math.max(surfaceDist, cliffDist);
                            }
                    );
        }
    }
}
