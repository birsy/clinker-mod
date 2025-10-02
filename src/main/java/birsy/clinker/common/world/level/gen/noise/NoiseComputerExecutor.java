package birsy.clinker.common.world.level.gen.noise;

public interface NoiseComputerExecutor {
    default double compute(int x, int y, int z, NoiseComputer noiseProcessor) {
        return computeDirect(x, y, z, noiseProcessor);
    }
    double computeDirect(int x, int y, int z, NoiseComputer noiseProcessor);
}
