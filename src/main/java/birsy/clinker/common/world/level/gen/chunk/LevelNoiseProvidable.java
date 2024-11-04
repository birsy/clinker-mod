package birsy.clinker.common.world.level.gen.chunk;

import net.minecraft.world.level.LevelAccessor;

public interface LevelNoiseProvidable {
    void provideLevelAndSeed(LevelAccessor level, long seed);
}
