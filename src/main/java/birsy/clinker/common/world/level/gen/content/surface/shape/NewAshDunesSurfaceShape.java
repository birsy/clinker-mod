package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerContext;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class NewAshDunesSurfaceShape extends CliffEdgeSurfaceShape {
    public NewAshDunesSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight - 10, baseHeight + 24, expectedBorderHeight);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return initSurfaceSynthesizer("ash_dunes_surface_shape_2")
                .withNoises(FNLNoiseProvider.create("cliffs"))
                .withDependencies(UtilitySynthesizers.BASIC_NOISE_2D[5], biomeSdfSynthesizer)
                .build(InterpolatingFieldResolution.COARSE_X_FINE_Y, this::surface);
    }

    float surface(SynthesizerContext ctx) {
        float x = ctx.x(), z = ctx.z();
        float y = ctx.y();

        float rockHeight = Mth.clampedMap(ctx.noise(0).sample(x / 100F, z / 100F), 0.3F, 0.4F, 12F, 18F);
        float largeScaleHorizontalRockMask = ctx.noise(0).sample(x / 180F, z / 180F);

        float verticalRockMask = y - (baseHeight + rockHeight);

        float horizontalRockMask = largeScaleHorizontalRockMask * 160F;
        horizontalRockMask += 130F;

        float smallHorizontalRockMask = ctx.noise(0).sample(x / 8F, z / 8F) * 30F;
        smallHorizontalRockMask += Math.max(0, horizontalRockMask);
        smallHorizontalRockMask = Math.max(smallHorizontalRockMask, verticalRockMask);

        float overhangAmount = ctx.noise(0).sample(x / 64F, z / 64F) * 0.5F + 0.5F;
        float rockMask = horizontalRockMask + Mth.clampedMap(y - baseHeight, 0, rockHeight - 2, 1F, -overhangAmount) * 24F;
        rockMask = MathUtils.smoothMax(rockMask, verticalRockMask, 0.5F);

        rockMask = Math.min(rockMask, smallHorizontalRockMask);


        float heightmap = baseHeight + (largeScaleHorizontalRockMask * 0.5F + 0.5F) * -5;
        heightmap += (1 - Math.abs(ctx.dependentValue(0))) * 5;

        return Math.min(y - heightmap, rockMask);
    }
}
