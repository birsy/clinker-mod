package birsy.clinker.common.world.level.gen.chunk;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface LevelNoiseProvidable<T extends ChunkGenerator & LevelNoiseProvidable> {
    T provideLevel(LevelAccessor level);
}
