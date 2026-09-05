package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatingFieldResolution;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Synthesizer {
    public static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public final int id = NEXT_ID.getAndIncrement();
    public final ImmutableList<Dependency> directDependencies;
    public final ImmutableList<Dependency> resolvedDependencies; // all required dependencies, recursively.
    public final ImmutableList<NoiseBuilder> noises;

    public final InterpolatingFieldResolution resolution;
    public final Synthesizer.Function function;

    // required y, everything else is filled with defaultValue
    public final double defaultValue;
    public final int minY, maxY;

    private Synthesizer(InterpolatingFieldResolution resolution,
                        Synthesizer.Function function,
                        ImmutableList<Dependency> directDependencies,
                        ImmutableList<Dependency> resolvedDependencies,
                        ImmutableList<NoiseBuilder> noises,
                        double defaultValue, int minY, int maxY) {
        this.directDependencies = directDependencies;
        this.resolvedDependencies = resolvedDependencies;
        this.noises = noises;
        this.resolution = resolution;
        this.function = function;
        this.defaultValue = defaultValue;

        this.minY = minY;
        this.maxY = maxY;
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

    public static class Builder {
        List<Synthesizer> dependencies = new ArrayList<>();
        List<NoiseBuilder> requiredNoises = new ArrayList<>();
        double defaultValue = 0;
        int minY = Integer.MIN_VALUE, maxY = Integer.MAX_VALUE;

        public Builder() {}

        public Builder setRange(int minY, int maxY, double defaultValue) {
            this.minY = minY; this.maxY = maxY;
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder addDependencies(Synthesizer... synthesizers) {
            Collections.addAll(dependencies, synthesizers);
            return this;
        }
        public Builder addNoises(NoiseBuilder... noises) {
            Collections.addAll(requiredNoises, noises);
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
                    defaultValue, minY, maxY);
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
                collectDependencies(directDependency.synthesizer(), synthToDependency, resolvedDependencies, nextMinY, nextMaxY, xzScale);
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

    public interface Context {
        double[] synthesizerValues();
        FastNoiseLite[] noises();
        void advanceX(); void advanceY(); void advanceZ(); void setSlice(int cellY);
    }

    public interface Function {
        double compute(int x, int y, int z, double[] depValues, FastNoiseLite[] noises);
    }

    public interface NoiseBuilder {
        FastNoiseLite create(long seed);
    }
}
