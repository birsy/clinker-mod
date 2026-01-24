package birsy.clinker.common.world.level.gen.system.biome.resolver;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import net.minecraft.util.RandomSource;

public interface BiomeLayerOperation {
    ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext);
}
