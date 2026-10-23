package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.biome.placement.BiomeList;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.RandomState;

public record WorldFeatureContext(
        OthershoreBiomeSource biomeSource,
        BiomeList biomeList,
        SynthesizerCache cache,
        LevelHeightAccessor heightAccessor,
        ChunkPos chunk,
        RandomState randomState) {
}
