package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import birsy.clinker.common.world.level.gen.NoiseProviders;
import birsy.clinker.common.world.level.gen.NoiseSampler;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.concurrent.ExecutionException;

public class TestTerrainProviderA extends TerrainProvider {
    @Override
    public float sample(ChunkAccess chunk, long seed, double x, double y, double z, NoiseSampler sampler) throws ExecutionException {
        sampler.setSeed(seed);
        return sampler.sample(x, y, z, NoiseProviders.NOISE_FREQ_16);
    }
}
