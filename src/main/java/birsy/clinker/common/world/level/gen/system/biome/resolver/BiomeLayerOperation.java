package birsy.clinker.common.world.level.gen.system.biome.resolver;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import net.minecraft.util.RandomSource;

public interface BiomeLayerOperation {
    int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext);
}
