package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature;

import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunk;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;
import java.util.Optional;

public record WorldFeatureType<T extends WorldFeature>(int priority, int separationRadius, WorldFeatureMaker<T> planner) {
    public Optional<T> realize(@Nullable BlockPos center, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth, RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext) {
        return planner.realize(center, level, minX, minZ, maxX, maxZ, metaChunkDepth, randomSource, context, worldContext);
    }
    public Optional<T> realize(LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth, RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext) {
        return planner.realize(null, level, minX, minZ, maxX, maxZ, metaChunkDepth, randomSource, context, worldContext);
    }

    public interface WorldFeatureMaker<T extends WorldFeature> {
        Optional<T> realize(@Nullable BlockPos center, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth, RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext);
    }
}
