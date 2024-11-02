package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import birsy.clinker.common.world.level.gen.NoiseSampler;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.concurrent.ExecutionException;

public class DefaultTerrainProvider extends TerrainProvider {
    @Override
    public float sample(ChunkAccess chunk, long seed, double x, double y, double z, NoiseSampler sampler) throws ExecutionException {
        return -1;
    }
}
