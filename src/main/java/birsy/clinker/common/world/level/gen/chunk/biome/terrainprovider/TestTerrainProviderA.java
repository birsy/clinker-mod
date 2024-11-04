package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import birsy.clinker.common.world.level.gen.NoiseCache;
import birsy.clinker.common.world.level.gen.NoiseProviders;
import net.minecraft.world.level.chunk.ChunkAccess;

public class TestTerrainProviderA extends TerrainProvider {
    @Override
    public float sample(ChunkAccess chunk, long seed, double x, double y, double z, NoiseCache sampler) {
        sampler.setSeed(seed);
        return sampler.sampleUncached(x, y, z, NoiseProviders.NOISE_FREQ_16);
    }
}
