package birsy.clinker.common.world.level.gen.biome.terrainprovider;

import birsy.clinker.common.world.level.gen.noise.NoiseCache;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class TerrainProvider {
    public abstract float sample(ChunkAccess chunk, long seed, double x, double y, double z, NoiseCache sampler);

    public static boolean isSolid(float value) {
        return value > 0;
    }
}
