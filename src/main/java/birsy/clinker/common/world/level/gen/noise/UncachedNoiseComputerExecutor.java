package birsy.clinker.common.world.level.gen.noise;

public class UncachedNoiseComputerExecutor implements NoiseComputerExecutor {
    private final NoiseComputerContext context;

    public UncachedNoiseComputerExecutor(NoiseHolder noiseHolder) {
        this.context = new NoiseComputerContext(this, noiseHolder);
    }

    @Override
    public double computeDirect(int x, int y, int z, NoiseComputer noiseProcessor) {
        return noiseProcessor.compute(x, y, z, this.context);
    }
}
