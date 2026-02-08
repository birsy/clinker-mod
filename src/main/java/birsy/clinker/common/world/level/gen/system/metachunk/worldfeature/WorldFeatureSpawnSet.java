package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature;

import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public record WorldFeatureSpawnSet(int metaChunkDepth, List<WorldFeatureSpawn> features) {
    public WorldFeatureSpawnSet(int metaChunkDepth, List<WorldFeatureSpawn> features) {
        this.metaChunkDepth = metaChunkDepth;
        features.sort(Comparator.comparingInt((featureSpawn) -> featureSpawn.featureType().priority()));
        this.features = Collections.unmodifiableList(features);
    }

    public record WorldFeatureSpawn(WorldFeatureType<?> featureType, IntProvider count) {}

    public static Builder builder(int depth) {
        return new Builder(depth);
    }

    public static final class Builder {
        final int depth;
        final List<WorldFeatureSpawn> spawns = new ArrayList<>();

        private Builder(int depth) {
            this.depth = depth;
        }

        public Builder add(WorldFeatureType<?> type, IntProvider count) {
            spawns.add(new WorldFeatureSpawn(type, count));
            return this;
        }

        public Builder add(WorldFeatureType<?> type, int count) {
            return this.add(type, ConstantInt.of(count));
        }

        public Builder add(WorldFeatureType<?> type, int min, int max) {
            return this.add(type, UniformInt.of(min, max));
        }

        public WorldFeatureSpawnSet build() {
            return new WorldFeatureSpawnSet(depth, spawns);
        }
    }
}
