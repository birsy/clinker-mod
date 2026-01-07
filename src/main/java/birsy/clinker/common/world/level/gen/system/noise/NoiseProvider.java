package birsy.clinker.common.world.level.gen.system.noise;

public interface NoiseProvider {
    double sample(String name, double x, double y, double z);
    double sample(String name, double x, double y);
}
