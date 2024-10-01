package birsy.clinker.common.world.level.gen;

/**
 * keeps track of the global state of world generation
 */
public interface ClinkerWorldGenerator {
    ChunkFluidField getFluidField(int chunkX, int chunkZ);
}
