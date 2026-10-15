package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public class OffsetScaleSurfaceShape extends CliffySurfaceShape {
    final float scale, scaleExponent;

    public OffsetScaleSurfaceShape(int baseHeight, int expectedBorderHeight,
                                   float scale, float scaleExponent) {
        super(baseHeight, (int) Math.floor(baseHeight - Math.ceil(scale)), (int) Math.ceil(baseHeight + Math.ceil(scale)), expectedBorderHeight);
        this.scale = scale;
        this.scaleExponent = scaleExponent;
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withRange(minSurfaceY, maxSurfaceY, 100)
                .withDependencies(UtilitySynthesizers.SURFACE_OFFSET, UtilitySynthesizers.SURFACE_OFFSET_SCALE)
                .build(InterpolatingFieldResolution.VERY_COARSE_X_FINE_Y,
                        ctx -> {
                            int offsetY = ctx.y() - baseHeight;
                            float offset = ctx.dependentValue(0) * scale;
                            float offsetScale = ctx.dependentValue(1);
                            offset *= (float) Math.pow(Math.abs(offsetScale), scaleExponent);
                            return offsetY + offset;
                        }
                );
    }
}
