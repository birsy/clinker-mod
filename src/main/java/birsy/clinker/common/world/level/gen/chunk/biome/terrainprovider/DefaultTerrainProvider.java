package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import birsy.clinker.common.world.level.gen.NoiseCache;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultTerrainProvider extends TerrainProvider {
    @Override
    public float sample(ChunkAccess chunk, long seed, double x, double y, double z, NoiseCache sampler) {
        return -1;
    }
}
