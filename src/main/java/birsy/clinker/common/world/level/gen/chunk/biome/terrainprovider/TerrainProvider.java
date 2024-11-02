package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import birsy.clinker.common.world.level.gen.NoiseSampler;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.concurrent.ExecutionException;

public abstract class TerrainProvider {
    public abstract float sample(ChunkAccess chunk, long seed, double x, double y, double z, NoiseSampler sampler) throws ExecutionException;

    public static boolean isSolid(float value) {
        return value > 0;
    }
}
