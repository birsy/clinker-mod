package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultTerrainProvider extends TerrainProvider {
    @Override
    public float sample(WorldGenLevel level, ChunkAccess chunk, long seed, double x, double y, double z) {
        return 0;
    }
}
