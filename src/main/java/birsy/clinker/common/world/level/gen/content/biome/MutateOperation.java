package birsy.clinker.common.world.level.gen.content.biome;

import birsy.clinker.common.world.level.gen.system.biome.resolver.BiomeLayerOperation;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;

public class MutateOperation {
    private static final int FLAT_THRESHOLD = 64;
    private record FlatMutate(
            MutationEntry[][] mutationSets,
            int scale, int salt, NormalNoise noise,
            @Nullable PositionalRandomFactory scaleFactory
    ) implements BiomeLayerOperation {
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            MutationEntry[] set;
            if (scale > 0 && scaleFactory != null) {
                int noise = (int) (noise().getValue(blockX, 0, blockZ) * (1 << scale-1));
                RandomSource coarse = scaleFactory.at((blockX + noise) >> scale, scale + salt, (blockZ + noise) >> scale);
                set = mutationSets[coarse.nextInt(mutationSets.length)];
            } else {
                set = mutationSets[random.nextInt(mutationSets.length)];
            }
            for (MutationEntry entry : set)
                if (entry.shouldMutate(currentId)) return entry.result(blockX, blockZ, random, noiseContext);
            return currentId;
        }
    }
    private record WeightedRandomListMutate(
            SimpleWeightedRandomList<MutationEntry[]> mutationSets,
            int scale, int salt, NormalNoise noise,
            @Nullable PositionalRandomFactory scaleFactory
    ) implements BiomeLayerOperation {
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random, NoiseContext noiseContext) {
            MutationEntry[] set;
            if (scale > 0 && scaleFactory != null) {
                int noise = (int) (noise().getValue(blockX, 0, blockZ) * (1 << scale-1));
                RandomSource coarse = scaleFactory.at((blockX + noise) >> scale, scale + salt, (blockZ + noise) >> scale);
                set = mutationSets.getRandomValue(coarse).orElseThrow();
            } else {
                set = mutationSets.getRandomValue(random).orElseThrow();
            }
            for (MutationEntry entry : set)
                if (entry.shouldMutate(currentId)) return entry.result(blockX, blockZ, random, noiseContext);
            return currentId;
        }
    }

    private sealed interface MutationEntry {
        boolean shouldMutate(int protoBiomeId);
        int result(int blockX, int blockZ, RandomSource random, NoiseContext noiseContext);
    }
    private record WeightedRandomListMutationEntry(IntPredicate target, SimpleWeightedRandomList<Integer> results) implements MutationEntry {
        @Override
        public boolean shouldMutate(int protoBiomeId) { return target.test(protoBiomeId); }
        @Override
        public int result(int blockX, int blockZ, RandomSource random, NoiseContext noiseContext) { return results.getRandomValue(random).orElseThrow(); }
    }
    private record FlatMutationEntry(IntPredicate target, int[] results) implements MutationEntry {
        @Override
        public boolean shouldMutate(int protoBiomeId) { return target.test(protoBiomeId); }
        @Override
        public int result(int blockX, int blockZ, RandomSource random, NoiseContext noiseContext) { return results[random.nextInt(results.length)]; }
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private static int salt = 0;
        private final List<Integer> setWeights = new ArrayList<>();
        private final List<MutationEntry[]> builtSets = new ArrayList<>();
        private int scale = 0;
        private PositionalRandomFactory scaleFactory;

        private Builder() {}

        public SetBuilder set(int weight) { return new SetBuilder(this, weight); }

        public SetBuilder set() { return set(1); }

        public Builder scale(int scale, PositionalRandomFactory factory) {
            this.scale = scale;
            this.scaleFactory = factory;
            return this;
        }

        public BiomeLayerOperation build() {
            int totalWeight = setWeights.stream().mapToInt(Integer::intValue).sum();
            MutationEntry[][] sets = builtSets.toArray(new MutationEntry[0][]);
            NormalNoise noise = NormalNoise.create(scaleFactory.fromSeed(salt), -scale, 1);
            if (totalWeight <= FLAT_THRESHOLD) {
                MutationEntry[][] flat = new MutationEntry[totalWeight][];
                int i = 0;
                for (int s = 0; s < sets.length; s++)
                    for (int w = 0; w < setWeights.get(s); w++)
                        flat[i++] = sets[s];
                return new FlatMutate(flat, scale, salt++, noise, scaleFactory);
            } else {
                SimpleWeightedRandomList.Builder<MutationEntry[]> listBuilder = SimpleWeightedRandomList.builder();
                for (int s = 0; s < sets.length; s++)
                    listBuilder.add(sets[s], setWeights.get(s));
                return new WeightedRandomListMutate(listBuilder.build(), scale, salt++, noise, scaleFactory);
            }
        }
    }

    public static final class SetBuilder {
        private final Builder parent;
        private final int weight;
        private final List<MutationEntry> entries = new ArrayList<>();

        private SetBuilder(Builder parent, int weight) {
            this.parent = parent;
            this.weight = weight;
        }

        public EntryBuilder entry(IntPredicate target) { return new EntryBuilder(this, target); }
        public EntryBuilder entry(ProtoBiome target) { return entry(id -> id == target.id); }
        public EntryBuilder entry(Set<ProtoBiome> targets) {
            boolean[] allowed = new boolean[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()];
            for (ProtoBiome t : targets) allowed[t.id] = true;
            return entry(id -> allowed[id]);
        }

        public Builder endSet() {
            parent.setWeights.add(weight);
            parent.builtSets.add(entries.toArray(new MutationEntry[0]));
            return parent;
        }
    }

    public static final class EntryBuilder {
        private final SetBuilder parent;
        private final IntPredicate target;
        private final List<Integer> resultIds = new ArrayList<>();
        private final List<Integer> resultWeights = new ArrayList<>();

        private EntryBuilder(SetBuilder parent, IntPredicate target) {
            this.parent = parent;
            this.target = target;
        }

        public EntryBuilder result(ProtoBiome biome, int weight) {
            resultIds.add(biome.id);
            resultWeights.add(weight);
            return this;
        }
        public EntryBuilder result(ProtoBiome biome) { return result(biome, 1); }

        public SetBuilder endEntry() {
            parent.entries.add(buildEntry());
            return parent;
        }

        private MutationEntry buildEntry() {
            int total = resultWeights.stream().mapToInt(Integer::intValue).sum();
            if (total <= FLAT_THRESHOLD) {
                int[] arr = new int[total];
                int i = 0;
                for (int j = 0; j < resultIds.size(); j++)
                    for (int w = 0; w < resultWeights.get(j); w++)
                        arr[i++] = resultIds.get(j);
                return new FlatMutationEntry(target, arr);
            } else {
                SimpleWeightedRandomList.Builder<Integer> b = SimpleWeightedRandomList.builder();
                for (int j = 0; j < resultIds.size(); j++)
                    b.add(resultIds.get(j), resultWeights.get(j));
                return new WeightedRandomListMutationEntry(target, b.build());
            }
        }
    }
}
