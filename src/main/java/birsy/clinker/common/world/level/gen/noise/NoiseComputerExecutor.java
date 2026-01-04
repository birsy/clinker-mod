package birsy.clinker.common.world.level.gen.noise;

public abstract class NoiseComputerExecutor {
    public final int minX, minY, minZ;
    public final int chunkHeight;

    protected NoiseComputerExecutor(int minX, int minY, int minZ, int chunkHeight) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.chunkHeight = chunkHeight;
    }

    public double compute(int x, int y, int z, NoiseComputer noiseProcessor) {
        return computeDirect(x, y, z, noiseProcessor);
    }
    public abstract double computeDirect(int x, int y, int z, NoiseComputer noiseProcessor);
}
