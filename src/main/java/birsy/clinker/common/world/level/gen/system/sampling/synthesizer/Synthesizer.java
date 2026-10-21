package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Synthesizer {
    public static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public final int id = NEXT_ID.getAndIncrement();
    @Nullable
    public final String profilerId;
    public final ImmutableList<Dependency> directDependencies;
    public final ImmutableList<Dependency> resolvedDependencies; // all required dependencies, recursively.
    public final ImmutableList<NoiseProvider> noises;

    public final InterpolatingFieldResolution resolution;
    public final Synthesizer.Function function;

    // required y, everything else is filled with defaultValue
    public final float upperSliceDefaultValue, lowerSliceDefaultValue;
    public final int defaultValueSliceY;
    public final int minY, maxY;

    private Synthesizer(InterpolatingFieldResolution resolution,
                        Synthesizer.Function function,
                        ImmutableList<Dependency> directDependencies,
                        ImmutableList<Dependency> resolvedDependencies,
                        ImmutableList<NoiseProvider> noises,
                        float lowerSliceDefaultValue, float upperSliceDefaultValue, int defaultValueSliceY,
                        int minY, int maxY,
                        @Nullable String profilerId) {
        this.directDependencies = directDependencies;
        this.resolvedDependencies = resolvedDependencies;
        this.noises = noises;
        this.resolution = resolution;
        this.function = function;

        this.lowerSliceDefaultValue = lowerSliceDefaultValue;
        this.upperSliceDefaultValue = upperSliceDefaultValue;
        this.defaultValueSliceY = defaultValueSliceY;
        this.minY = minY;
        this.maxY = maxY;

        this.profilerId = profilerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Synthesizer) obj;
        return this.id == that.id;
    }
    @Override
    public int hashCode() {
        return id;
    }

//    public static Builder builder() {
//        return builder(null);
//    }

    public static Builder builder(@Nullable String profilingId) {
        return new Builder(profilingId);
    }

    public static class Builder {
        final String profilerId;
        List<Synthesizer> dependencies = new ArrayList<>();
        List<NoiseProvider> requiredNoises = new ArrayList<>();
        // reasonable default values for a surface biome...
        float lowerSliceDefaultValue = -100F, upperSliceDefaultValue = 100F;
        int defaultValueSliceY = Integer.MIN_VALUE, minY = Integer.MIN_VALUE, maxY = Integer.MAX_VALUE;

        private Builder(String profilerId) {
            this.profilerId = profilerId;
        }

        public Builder withDefaultValue(float defaultValue) {
            return withDefaultValue(defaultValue, defaultValue, Integer.MIN_VALUE);
        }

        public Builder withDefaultValue(float lowerSliceDefaultValue, float upperSliceDefaultValue, int defaultValueSliceY) {
            this.lowerSliceDefaultValue = lowerSliceDefaultValue;
            this.upperSliceDefaultValue = upperSliceDefaultValue;
            this.defaultValueSliceY = defaultValueSliceY;
            return this;
        }

        public Builder withRange(int minY, int maxY) {
            this.minY = minY;
            this.maxY = maxY;
            return this;
        }

        public Builder withDependencies(Synthesizer... synthesizers) {
            for (Synthesizer synthesizer : synthesizers) {
                if (synthesizer == null) continue;
                dependencies.add(synthesizer);
            }
            return this;
        }
        public Builder withNoises(NoiseProvider... noises) {
            for (NoiseProvider noise : noises) {
                if (noise == null) continue;
                requiredNoises.add(noise);
            }
            return this;
        }

        // handles dependency resolution and such
        public Synthesizer build(InterpolatingFieldResolution resolution, Synthesizer.Function function) {
            ImmutableList.Builder<Dependency> directDependencies = ImmutableList.builder();
            ImmutableList.Builder<Dependency> resolvedDependencies = ImmutableList.builder();
            Map<Synthesizer, Set<Dependency>> synthToDependency = new HashMap<>();
            for (Synthesizer dependency : dependencies) {
                directDependencies.add(collectDependencies(dependency, synthToDependency, resolvedDependencies, minY, maxY, resolution.xzScale()));
            }

            return new Synthesizer(resolution, function,
                    directDependencies.build(),
                    resolvedDependencies.build(),
                    ImmutableList.copyOf(requiredNoises),
                    lowerSliceDefaultValue, upperSliceDefaultValue, defaultValueSliceY,
                    minY, maxY,
                    profilerId);
        }

        private static Dependency collectDependencies(Synthesizer synthesizer,
                                                      Map<Synthesizer, Set<Dependency>> synthToDependency,
                                                      ImmutableList.Builder<Dependency> resolvedDependencies,
                                                      int minY, int maxY, int xzScale) {
            // compute the next "window"
            int nextMinY = Math.max(minY, synthesizer.minY), nextMaxY = Math.min(maxY, synthesizer.maxY);
            int nextXZScale = Math.max(xzScale, synthesizer.resolution.xzScale());
            // add dependencies
            for (Dependency directDependency : synthesizer.directDependencies) {
                collectDependencies(directDependency.synthesizer(), synthToDependency, resolvedDependencies, nextMinY, nextMaxY, nextXZScale);
            }

            // make sure a similar dependency hasn't already been added
            if (synthToDependency.containsKey(synthesizer)) {
                Set<Dependency> existingDependencies = synthToDependency.get(synthesizer);
                for (Dependency existingDependency : existingDependencies) {
                    // if it's the same scale and completely encapsulated, we don't need to add a new dependency.
                    if (existingDependency.xzScale == nextXZScale &&
                        existingDependency.minY <= nextMinY && existingDependency.maxY >= nextMaxY) {
                        return existingDependency;
                    }
                }
            }
            Dependency newDependency = new Dependency(synthesizer, nextMinY, nextMaxY, nextXZScale);
            synthToDependency.computeIfAbsent(synthesizer, key -> new HashSet<>()).add(newDependency);
            resolvedDependencies.add(newDependency);
            return newDependency;
        }
    }

    public record Dependency(Synthesizer synthesizer, int minY, int maxY, int xzScale) {}

    public interface Function {
        float compute(SynthesizerContext ctx);
    }
}
