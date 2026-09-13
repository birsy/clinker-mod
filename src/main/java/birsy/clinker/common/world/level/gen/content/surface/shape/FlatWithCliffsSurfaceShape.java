package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public class FlatWithCliffsSurfaceShape extends CliffySurfaceShape {
    public FlatWithCliffsSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight - 1, baseHeight + 1, expectedBorderHeight);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withRange(minSurfaceY, maxSurfaceY, 100)
                .withDependencies(UtilitySynthesizers.HEIGHT_OFFSET)
                .build(InterpolatingFieldResolution.FINE_Y,
                        ctx -> { return ctx.y() - baseHeight + ctx.dependentValue(0); }
                );
    }
}
