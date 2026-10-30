package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public class FlatWithCliffsSurfaceShape extends CliffAndOverhangEdgeSurfaceShape {
    public FlatWithCliffsSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight - 1, baseHeight + 1, expectedBorderHeight);
    }

    public FlatWithCliffsSurfaceShape(int surfaceHeight,
                                      int cliffBaseHeight, int cliffOutset,
                                      int overhangBaseHeight, int overhangOutset) {
        super(surfaceHeight, surfaceHeight - 1, surfaceHeight + 1,
                cliffBaseHeight, cliffOutset, overhangBaseHeight, overhangOutset);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return initSurfaceSynthesizer("flat_surface_shape")
                .withDependencies(UtilitySynthesizers.HEIGHT_OFFSET)
                .build(InterpolatingFieldResolution.COARSE_X_FINE_Y,
                        ctx -> { return ctx.y() - baseHeight + ctx.dependentValue(0); }
                );
    }
}
