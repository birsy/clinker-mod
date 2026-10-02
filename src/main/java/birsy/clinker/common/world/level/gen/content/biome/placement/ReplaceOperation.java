package birsy.clinker.common.world.level.gen.content.biome.placement;

import birsy.clinker.common.world.level.gen.system.biome.placement.resolver.BiomeLayerOperation;
import birsy.clinker.common.world.level.gen.system.biome.placement.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class ReplaceOperation {
    private ReplaceOperation() {}

    private record Replace(Int2ObjectOpenHashMap<Rule[]> byId, NoiseSampler noise) implements BiomeLayerOperation {
        @Override
        public int apply(int blockX, int blockZ, int currentId, int[] neighborhood, RandomSource random) {
            Rule[] rules = byId.get(currentId);
            float n = Float.NaN;
            for (Rule r : rules) {
                if (r.usesNoise()) {
                    if (Float.isNaN(n)) n = noise.sample(blockX, blockZ);
                    if (!r.accepts(n)) continue;
                }
                return r.replacement();
            }
            return currentId;
        }
    }

    private record Rule(int replacement, float min, float max, boolean usesNoise) {
        boolean accepts(float n) { return n >= min && n < max; }
    }


    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private static final AtomicInteger NOISE_ID = new AtomicInteger();

        private final List<BuilderRule> builderRules = new ArrayList<>();
        private PositionalRandomFactory randomFactory;
        private NoiseProvider provider;

        public Builder noise(PositionalRandomFactory randomFactory) {
            return noise(randomFactory, FNLNoiseProvider.create("noise_" + NOISE_ID.getAndIncrement(), 128.0F));
        }
        public Builder noise(PositionalRandomFactory randomFactory, NoiseProvider provider) {
            this.randomFactory = randomFactory;
            this.provider = provider;
            return this;
        }
        public RuleBuilder replace(Supplier<ProtoBiome>... from) {
            return new RuleBuilder(List.of(from));
        }
        public RuleBuilder replaceAll() {
            return new RuleBuilder(null);
        }

        public final class RuleBuilder {
            private final List<Supplier<ProtoBiome>> from;
            private float min = Float.NEGATIVE_INFINITY, max = Float.POSITIVE_INFINITY;
            private boolean usesNoise;

            private RuleBuilder(List<Supplier<ProtoBiome>> from) { this.from = from; }

            public RuleBuilder above(float min) {
                this.min = min;
                this.usesNoise = true;
                return this;
            }
            public RuleBuilder below(float max) {
                this.max = max;
                this.usesNoise = true;
                return this;
            }
            public RuleBuilder between(float min, float max) {
                this.min = min; this.max = max;
                usesNoise = true;
                return this;
            }
            public Builder with(Supplier<ProtoBiome> to) {
                builderRules.add(new BuilderRule(from, to, min, max, usesNoise));
                return Builder.this;
            }
        }

        public BiomeLayerOperation build() {
            // streams hell ;u;
            boolean needsNoise = builderRules.stream().anyMatch(BuilderRule::usesNoise);
            if (needsNoise && provider == null) throw new IllegalStateException("configure noise first dummy");

            List<RuleForIds> resolved = new ArrayList<>();
            IntSet mentioned = new IntOpenHashSet();
            for (BuilderRule s : builderRules) {
                IntSet ids = null;
                if (s.from() != null) {
                    ids = new IntOpenHashSet();
                    for (var sup : s.from()) ids.add(sup.get().id);
                    mentioned.addAll(ids);
                }
                resolved.add(new RuleForIds(ids, new Rule(s.to().get().id, s.min(), s.max(), s.usesNoise())));
            }

            Int2ObjectOpenHashMap<Rule[]> byId = new Int2ObjectOpenHashMap<>();
            for (int id : mentioned) {
                byId.put(id, resolved.stream()
                        .filter(r -> r.from() == null || r.from().contains(id))
                        .map(RuleForIds::rule).toArray(Rule[]::new));
            }
            byId.defaultReturnValue(
                    resolved.stream().filter(r -> r.from() == null).map(RuleForIds::rule).toArray(Rule[]::new)
            );

            NoiseSampler sampler = needsNoise
                    ? provider.fromRandom(randomFactory.fromHashOf(provider.name()))
                    : null;
            return new Replace(byId, sampler);
        }

        private record BuilderRule(List<Supplier<ProtoBiome>> from, Supplier<ProtoBiome> to, float min, float max, boolean usesNoise) {}
        private record RuleForIds(IntSet from, Rule rule) {}
    }
}
