package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerCache;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

public interface ModifiesSurfaceDecoration extends WorldFeatureCapability {
    void modifySurfaceDecoration(WorldGenLevel level, WorldFeatureContext worldContext);
}
