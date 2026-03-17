package birsy.clinker.common.world.level.gen.content.biome;

import birsy.clinker.common.world.level.gen.system.biome.resolver.BiomeLayerOperation;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiomeNeighborhood;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public class BiomeLayerOperations {
    public record Replace(IntPredicate shouldReplace, int replacement) implements BiomeLayerOperation {
        public Replace(Set<ProtoBiome> targets, ProtoBiome replacement) {
            this(Util.make(() -> {
                Set<Integer> ids = targets
                        .stream()
                        .map(protoBiome -> protoBiome.id)
                        .collect(Collectors.toUnmodifiableSet());
                return ids::contains;
            }), replacement.id);
        }
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            if (shouldReplace.test(currentId)) return replacement;
            return currentId;
        }
    }

    public record Smooth() implements BiomeLayerOperation {
        private static final ThreadLocal<int[]> threadedCounts =
                ThreadLocal.withInitial(() -> new int[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()]);
        private static final ThreadLocal<int[]> threadedDirtyIds =
                ThreadLocal.withInitial(() -> new int[9]);
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            int[] counts = threadedCounts.get();
            int[] dirty = threadedDirtyIds.get();
            int dirtyCount = 0, highestCount = 0, winner = currentId;

            for (int i = 0; i < 9; i++) {
                int id = neighborhood[i];
                if (counts[id] == 0) dirty[dirtyCount++] = id;
                int countForId = ++counts[id];
                if (countForId > highestCount) {
                    highestCount = countForId;
                    winner = id;
                }
            }
            for (int i = 0; i < dirtyCount; i++) counts[dirty[i]] = 0;
            return winner;
        }
    }

    public record SmoothSpecific(IntPredicate shouldSmoothInto) implements BiomeLayerOperation {
        public SmoothSpecific(ProtoBiome... targets) {
            this(Util.make(
                    () -> {
                        boolean[] allowed = new boolean[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()];
                        for (ProtoBiome t : targets) allowed[t.id] = true;
                        return (IntPredicate) id -> allowed[id];
                    }
            ));
        }

        private static final ThreadLocal<int[]> threadedCounts =
                ThreadLocal.withInitial(() -> new int[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()]);
        private static final ThreadLocal<int[]> threadedDirtyIds =
                ThreadLocal.withInitial(() -> new int[9]);
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            int[] counts = threadedCounts.get();
            int[] dirty = threadedDirtyIds.get();
            int dirtyCount = 0, highestCount = 0, winner = currentId;

            for (int i = 0; i < 9; i++) {
                int id = neighborhood[i];
                if (counts[id] == 0) dirty[dirtyCount++] = id;
                int countForId = ++counts[id];
                if (countForId > highestCount) {
                    highestCount = countForId;
                    winner = id;
                }
            }
            for (int i = 0; i < dirtyCount; i++) counts[dirty[i]] = 0;
            return shouldSmoothInto.test(winner) ? winner : currentId;
        }
    }

    public record RandomizeIntoNeighbor(double probability) implements BiomeLayerOperation {
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            if (random.nextDouble() > probability) return currentId;
            int x = random.nextIntBetweenInclusive(-1, 1), z = random.nextIntBetweenInclusive(-1, 1);
            return neighborhood[(z + 1) * 3 + (x + 1)];
        }
    }

    private record WeightedRandomListMutate(IntPredicate target, SimpleWeightedRandomList<Integer> results) implements BiomeLayerOperation {
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            return target.test(currentId) ? results.getRandomValue(random).orElse(currentId) : currentId;
        }
    }
    private record FlatMutate(IntPredicate target, int[] results) implements BiomeLayerOperation {
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            if (!target.test(currentId)) return currentId;
            return results[random.nextInt(results.length)];
        }
    }
    public static class MutateBuilder {
        final IntPredicate target;
        final List<ProtoBiome> results = new ArrayList<>();
        final List<Integer> weights = new ArrayList<>();
        private static final int FLAT_ARRAY_THRESHOLD = 64;

        public MutateBuilder(IntPredicate target) {
            this.target = target;
        }
        public MutateBuilder(ProtoBiome target) {
            this((id) -> id == target.id);
        }
        public MutateBuilder(Set<ProtoBiome> targets) {
            this(Util.make(() -> {
                Set<Integer> ids = targets
                        .stream()
                        .map(protoBiome -> protoBiome.id)
                        .collect(Collectors.toUnmodifiableSet());
                return (IntPredicate) ids::contains;
            }));
        }
        public MutateBuilder entry(ProtoBiome biome, int weight) {
            results.add(biome);
            weights.add(weight);
            return this;
        }
        public BiomeLayerOperation build() {
            int total = weights.stream().mapToInt(Integer::intValue).sum();
            if (total <= FLAT_ARRAY_THRESHOLD) {
                int[] arr = new int[total];
                int i = 0;
                for (int j = 0; j < results.size(); j++)
                    for (int w = 0; w < weights.get(j); w++)
                        arr[i++] = results.get(j).id;
                return new FlatMutate(target, arr);
            } else {
                SimpleWeightedRandomList.Builder<Integer> builder = SimpleWeightedRandomList.builder();
                for (int i = 0; i < results.size(); i++)
                    builder.add(results.get(i).id, weights.get(i));
                return new WeightedRandomListMutate(target, builder.build());
            }
        }
    }

    public record Surround(IntPredicate target, int surroundingId) implements BiomeLayerOperation {
        public Surround(ProtoBiome target, ProtoBiome surrounding) {
            this(id -> id == target.id, surrounding.id);
        }
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            if (target.test(currentId)) return currentId;
            for (int i : ProtoBiomeNeighborhood.NEIGHBOR_INDICES)
                if (target.test(neighborhood[i])) return surroundingId;
            return currentId;
        }
    }

    public record CreateBorders(IntPredicate biomeA, IntPredicate biomeB, int borderBiomeId) implements BiomeLayerOperation {
        public CreateBorders(ProtoBiome a, ProtoBiome b, ProtoBiome border) {
            this(id -> id == a.id, id -> id == b.id, border.id);
        }
        public CreateBorders(Set<ProtoBiome> a, Set<ProtoBiome> b, ProtoBiome border) {
            this(Util.make(() -> {
                        Set<Integer> ids = a
                                .stream()
                                .map(protoBiome -> protoBiome.id)
                                .collect(Collectors.toUnmodifiableSet());
                        return ids::contains;
                    }),
                 Util.make(() -> {
                        Set<Integer> ids = b
                                .stream()
                                .map(protoBiome -> protoBiome.id)
                                .collect(Collectors.toUnmodifiableSet());
                        return ids::contains;
                    }),
                 border.id);
        }
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            if (!biomeA.test(currentId)) return currentId;
            for (int i : ProtoBiomeNeighborhood.NEIGHBOR_INDICES)
                if (biomeB.test(neighborhood[i])) return borderBiomeId;
            return currentId;
        }
    }

    public record Expand(boolean[] isExpanding) implements BiomeLayerOperation {
        public Expand(ProtoBiome... biomes) {
            this(Util.make(() -> {
                boolean[] shouldExpand = new boolean[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()];
                for (ProtoBiome b : biomes) shouldExpand[b.id] = true;
                return shouldExpand;
            }));
        }
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            if (isExpanding[currentId]) return currentId;
            for (int i : ProtoBiomeNeighborhood.NEIGHBOR_INDICES)
                if (isExpanding[neighborhood[i]]) return neighborhood[i];
            return currentId;
        }
    }

    public record Biome(int biomeId) implements BiomeLayerOperation {
        public Biome(ProtoBiome biome) { this(biome.id); }
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            return biomeId;
        }
    }
}
