package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature;

import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunk;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;
import java.util.Optional;

public record WorldFeatureType<T extends WorldFeature>(int priority, int separationRadius, WorldFeatureMaker<T> planner) {
    public Optional<WorldFeatureInstance<T>> realize(@Nullable BlockPos center, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth, RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext) {
        Optional<T> feature = planner.realize(center, level, minX, minZ, maxX, maxZ, metaChunkDepth, randomSource, context, worldContext);
        return feature.map(f -> new WorldFeatureInstance<>(this, f));
    }
    public Optional<WorldFeatureInstance<T>> realize(LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth, RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext) {
        Optional<T> feature = planner.realize(null, level, minX, minZ, maxX, maxZ, metaChunkDepth, randomSource, context, worldContext);
        return feature.map(f -> new WorldFeatureInstance<>(this, f));
    }

    public record WorldFeatureInstance<T extends WorldFeature>(WorldFeatureType<T> type, T feature) {
        public int centerX() { return feature.getCenterX(); }
        public int centerZ() { return feature.getCenterZ(); }
        public boolean within(int minX, int minZ, int maxX, int maxZ) { return feature.within(minX, minZ, maxX, maxZ); }
    }
    public interface WorldFeatureMaker<T extends WorldFeature> {
        Optional<T> realize(@Nullable BlockPos center, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth, RandomSource randomSource, UncachedNoiseContext context, WorldFeatureContext worldContext);
    }
}
