package birsy.clinker.common.world.level.gen.system.sampling.noise;

public interface NoiseSampler {
    double sample(double x, double y, double z);
    default double sample(double x, double z) { return sample(x, 0, z); }
}
