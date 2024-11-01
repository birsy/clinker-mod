package birsy.clinker.common.world.level.gen.noiseproviders;

public interface NoiseProvider {
    void setSeed(long seed);
    float sample(double x, double y, double z);
}
