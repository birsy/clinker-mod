package birsy.clinker.common.world.level.gen.noise;

public class UncachedNoiseComputerExecutor extends NoiseComputerExecutor {
    private final NoiseComputerContext context;

    public UncachedNoiseComputerExecutor(int minX, int minY, int minZ, int chunkHeight, NoiseHolder noiseHolder) {
        super(minX, minY, minZ, chunkHeight);
        this.context = new NoiseComputerContext(this, noiseHolder);
    }

    @Override
    public double computeDirect(int x, int y, int z, NoiseComputer noiseProcessor) {
        return noiseProcessor.compute(x, y, z, this.context);
    }
}
