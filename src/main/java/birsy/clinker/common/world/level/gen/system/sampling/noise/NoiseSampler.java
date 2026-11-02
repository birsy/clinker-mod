package birsy.clinker.common.world.level.gen.system.sampling.noise;

import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerContext;

public interface NoiseSampler {
    float sample(double x, double y, double z);
    default float sample(double x, double z) { return sample(x, 0, z); }
    default float sample(SynthesizerContext ctx, double period) { return sample(ctx.x() / period, ctx.z() / period); }

    default float[] sampleSet(double x, double y, double z) { throw new UnsupportedOperationException(); }
    default float[] sampleSet(double x, double z) { throw new UnsupportedOperationException(); }
}
