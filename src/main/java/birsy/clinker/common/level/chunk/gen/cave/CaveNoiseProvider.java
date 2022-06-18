package birsy.clinker.common.level.chunk.gen.cave;

import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class CaveNoiseProvider {
    protected final long seed;
    protected final ChunkAccess chunk;

    public CaveNoiseProvider(ChunkAccess chunk, long seed) {
        this.chunk = chunk;
        this.seed = seed;
    }

    public abstract double[][][] getNoiseInChunk(int chunkX, int chunkZ);
}
