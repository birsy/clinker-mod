package birsy.clinker.common.world.level.gen.system.sampling.noise;

public interface NoiseHolder {
    String name();
    NoiseSampler fromSeed(long seed);
}