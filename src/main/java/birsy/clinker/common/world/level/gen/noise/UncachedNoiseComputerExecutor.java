package birsy.clinker.common.world.level.gen.noise;

import net.minecraft.world.level.chunk.ChunkAccess;

public class UncachedNoiseComputerExecutor implements NoiseComputerExecutor {
    private final NoiseComputerContext context;

    public UncachedNoiseComputerExecutor(NoiseHolder noiseHolder) {
        this.context = new NoiseComputerContext(this, noiseHolder);
    }

    public double compute(int x, int y, int z, NoiseComputer noiseProcessor) {
        return noiseProcessor.compute(x, y, z, this.context);
    }
}
