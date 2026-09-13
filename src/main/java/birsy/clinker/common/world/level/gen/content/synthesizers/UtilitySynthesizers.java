package birsy.clinker.common.world.level.gen.content.synthesizers;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.VoronoiNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public class UtilitySynthesizers {
    public static final Synthesizer ROCKY_CLIFF_Y = Synthesizer.builder()
            .withNoises(VoronoiNoiseProvider.create("rocky_cliff", 0.5, 3))
            .build(InterpolatingFieldResolution.FINE_Y,
                    ctx -> {
                        double x = ctx.x() / 32.0, z = ctx.z() / 32.0;
                        return ctx.noise(0).sampleSet(x, ctx.y() / 12.0, z)[1] * 12.0;
                    }
            );

    public static final Synthesizer HEIGHT_OFFSET = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("offset"))
            .build(InterpolatingFieldResolution.COARSE_2D,
                    ctx -> {
                        double freq = 32.0;
                        return ctx.noise(0).sample(ctx.x() / freq, ctx.z() / freq);
                    }
            );

    public static final Synthesizer VORONOI_EDGE_DISTANCE_24 = Synthesizer.builder()
            .withNoises(VoronoiNoiseProvider.create("crackle_24", 0.5, 2))
            .build(InterpolatingFieldResolution.COARSE_2D,
                    ctx -> {
                        double freq = 24.0;
                        double[] sample = ctx.noise(0).sampleSet(ctx.x() / freq, ctx.z() / freq);
                        return (sample[4] - sample[3]) * freq;
                    }
            );
}
