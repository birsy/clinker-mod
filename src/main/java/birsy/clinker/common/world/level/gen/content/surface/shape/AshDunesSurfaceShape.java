package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerContext;
import net.minecraft.util.Mth;

public class AshDunesSurfaceShape extends CliffySurfaceShape {
    public AshDunesSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight - 10, baseHeight + 15, expectedBorderHeight);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withRange(minSurfaceY, maxSurfaceY, 100)
                .withNoises(FNLNoiseProvider.create("cliffs"))
                .withDependencies(UtilitySynthesizers.BASIC_NOISE_2D[4], UtilitySynthesizers.ROCKY_CLIFF_Y)
                .build(InterpolatingFieldResolution.FINE_Y, this::surface);
    }

    float surface(SynthesizerContext ctx) {
        float y = ctx.y();
        float duneNoise = ctx.dependentValue(0);
        float duneHeight = (1F - Mth.abs(duneNoise)) * 2F - 1F;

        float heightmap = Mth.clampedMap(
                ctx.noise(0).sample(ctx.x() / 200.0, ctx.z() / 200.0) * 200F,
                0.0F, 5F, baseHeight, baseHeight + 10
        );

        float rockyY = Mth.lerp((duneNoise * 0.5F + 0.5F) * 0.7F, y, ctx.dependentValue(1));

        return rockyY - heightmap - duneHeight * 5;
    }
}
