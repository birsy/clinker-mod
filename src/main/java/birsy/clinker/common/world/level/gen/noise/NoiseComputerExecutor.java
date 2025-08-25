package birsy.clinker.common.world.level.gen.noise;

public interface NoiseComputerExecutor {
    double compute(int x, int y, int z, NoiseComputer noiseProcessor);
}
