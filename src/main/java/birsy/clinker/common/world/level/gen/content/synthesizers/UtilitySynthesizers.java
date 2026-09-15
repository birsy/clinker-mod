package birsy.clinker.common.world.level.gen.content.synthesizers;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.VoronoiNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.core.util.noise.FastNoiseLite;

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

    public static final Synthesizer[] BASIC_NOISE_2D = basicNoiseArray(3.0F, 5, true);
    public static final Synthesizer[] BASIC_NOISE_3D = basicNoiseArray(3.0F, 5, false);

    private static Synthesizer basicNoise(float frequency, boolean twoDimensional) {
        InterpolatingFieldResolution[] potentialResolutions = twoDimensional ?
                new InterpolatingFieldResolution[]{InterpolatingFieldResolution.DIRECT_2D, InterpolatingFieldResolution.FINE_2D, InterpolatingFieldResolution.COARSE_2D, InterpolatingFieldResolution.VERY_COARSE_2D} :
                new InterpolatingFieldResolution[]{InterpolatingFieldResolution.DIRECT, InterpolatingFieldResolution.FINE, InterpolatingFieldResolution.COARSE, InterpolatingFieldResolution.VERY_COARSE};
        InterpolatingFieldResolution resolution = potentialResolutions[potentialResolutions.length - 1];

        // choose the appropriate resolution.
        for (int i = 1; i < potentialResolutions.length; i++) {
            InterpolatingFieldResolution potentialResolution = potentialResolutions[i];
            int cellSize = 1 << potentialResolution.xzScale();
            if (cellSize > frequency) {
                resolution = potentialResolutions[i - 1];
                break;
            }
        }

        return Synthesizer.builder().withNoises(
                FNLNoiseProvider.create("basic_noise_f" + frequency + (twoDimensional ? "_2d" : ""), () -> {
                    FastNoiseLite fnl = new FastNoiseLite();
                    fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
                    fnl.SetFractalType(FastNoiseLite.FractalType.None);
                    fnl.SetFrequency(frequency);
                    return fnl;
                })
            ).build(resolution,
                    twoDimensional ?
                            ctx -> ctx.noise(0).sample(ctx.x(), ctx.z()) :
                            ctx -> ctx.noise(0).sample(ctx.x(), ctx.y(), ctx.z())
            );
    }
    private static Synthesizer[] basicNoiseArray(float startingFrequency, int maxScale, boolean twoDimensional) {
        Synthesizer[] array = new Synthesizer[maxScale + 1];
        for (int i = 0; i < array.length; i++) {
            float freq = 1 << i;
            array[i] = basicNoise(startingFrequency * freq, twoDimensional);
        }
        return array;
    }
}
