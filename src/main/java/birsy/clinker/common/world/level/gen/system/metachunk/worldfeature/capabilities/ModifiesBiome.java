package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface ModifiesBiome extends WorldFeatureCapability {
    void prefillBiomeNoiseFields(int chunkX, int chunkZ, NoiseFieldCache cache, WorldFeatureContext worldContext);
    Holder<Biome> modifyBiome(int x, int y, int z, int minX, int minY, int minZ, Holder<Biome> currentBiome, NoiseContext context);
}
