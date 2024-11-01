package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class TerrainProvider {
    public abstract float sample(WorldGenLevel level, ChunkAccess chunk, long seed, double x, double y, double z);

    public static boolean isSolid(float value) {
        return value > 0;
    }
}
