package birsy.clinker.common.world.level.gen.system.sampling.noise;

public interface NoiseSampler {
    float sample(double x, double y, double z);
    default float sample(double x, double z) { return sample(x, 0, z); }
    default float[] sampleSet(double x, double y, double z) { throw new UnsupportedOperationException(); }
    default float[] sampleSet(double x, double z) { throw new UnsupportedOperationException(); }
}
