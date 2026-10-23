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
    public final String debugName;
    public final ImmutableList<Synthesizer.Pass> passes;
    public final ImmutableList<Dependency> resolvedDependencies; // all required dependencies, recursively.
    public final InterpolatingFieldResolution resolution;

    // initial field fill stuffs
    public final float upperSliceDefaultValue, lowerSliceDefaultValue;
    public final int defaultValueSliceY;
    public final int minY, maxY;

    private Synthesizer(ImmutableList<Synthesizer.Pass> passes,
                        ImmutableList<Dependency> resolvedDependencies,
                        InterpolatingFieldResolution resolution,
                        float lowerSliceDefaultValue, float upperSliceDefaultValue, int defaultValueSliceY,
                        @Nullable String debugName) {
        this.passes = passes;
        this.resolvedDependencies = resolvedDependencies;
        this.resolution = resolution;
        this.lowerSliceDefaultValue = lowerSliceDefaultValue;
        this.upperSliceDefaultValue = upperSliceDefaultValue;
        this.defaultValueSliceY = defaultValueSliceY;
        this.debugName = debugName;

        int tempMinY = Integer.MAX_VALUE, tempMaxY = Integer.MIN_VALUE;
        for (Pass pass : passes) {
            tempMinY = Math.min(tempMinY, pass.minY());
            tempMaxY = Math.max(tempMaxY, pass.maxY());
        }
        this.minY = tempMinY; this.maxY = tempMaxY;
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

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(@Nullable String profilingId) {
        return new Builder(profilingId);
    }

    public static class Builder {
        final String debugName;
        final List<PassBuilder> passes = new ArrayList<>();
        final List<Synthesizer> dependencies = new ArrayList<>();
        final List<NoiseProvider> requiredNoises = new ArrayList<>();
        // reasonable default values for a surface biome...
        float lowerSliceDefaultValue = -100F, upperSliceDefaultValue = 100F;
        int defaultValueSliceY = Integer.MIN_VALUE, minY = Integer.MIN_VALUE, maxY = Integer.MAX_VALUE;

        private Builder(String debugName) {
            this.debugName = debugName;
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
            Collections.addAll(dependencies, synthesizers);
            return this;
        }

        public Builder withNoises(NoiseProvider... noises) {
            Collections.addAll(requiredNoises, noises);
            return this;
        }

        public Builder withPasses(PassBuilder... passBuilders) {
            Collections.addAll(passes, passBuilders);
            return this;
        }

        // handles dependency resolution and such
        public Synthesizer build(InterpolatingFieldResolution resolution, Synthesizer.Function function) {
            // resolve passes
            ImmutableList.Builder<Synthesizer.Pass> passesBuilder = ImmutableList.builder();
            // first "implicit" pass
            passesBuilder.add(
                    passBuilder(function)
                            .withRange(this.minY, this.maxY)
                            .withNoises(this.requiredNoises.toArray(new NoiseProvider[0]))
                            .withDependencies(this.dependencies.toArray(new Synthesizer[0]))
                            .build(debugName, resolution)
            );
            // the rest of the passes
            for (PassBuilder pass : this.passes) passesBuilder.add(pass.build(debugName, resolution));
            ImmutableList<Synthesizer.Pass> resolvedPasses = passesBuilder.build();

            // resolve dependencies
            SequencedMap<Synthesizer, Dependency> resolvedDependencies = new LinkedHashMap<>();
            for (Pass pass : resolvedPasses) {
                for (Dependency direct : pass.dependencies) {
                    for (Dependency resolved : direct.synthesizer.resolvedDependencies) {
                        merge(resolvedDependencies,
                                resolved.synthesizer,
                                Math.max(pass.minY, resolved.minY),
                                Math.min(pass.maxY, resolved.maxY)
                        );
                    }
                    merge(resolvedDependencies,
                            direct.synthesizer,
                            direct.minY,
                            direct.maxY
                    );
                }
            }

            return new Synthesizer(
                    resolvedPasses,
                    ImmutableList.copyOf(resolvedDependencies.sequencedValues()),
                    resolution,
                    lowerSliceDefaultValue, upperSliceDefaultValue, defaultValueSliceY,
                    debugName
            );
        }

        static void merge(Map<Synthesizer, Dependency> map, Synthesizer synthesizer, int minY, int maxY) {
            map.merge(synthesizer,
                    new Dependency(synthesizer, minY, maxY),
                    (a, b) -> new Dependency(
                            synthesizer,
                            Math.min(a.minY, b.minY),
                            Math.max(a.maxY, b.maxY)
                    )
            );
        }
    }

    public static PassBuilder passBuilder(Function function) {
        return new PassBuilder(function);
    }

    public static class PassBuilder {
        final Synthesizer.Function function;
        final List<Synthesizer> dependencies = new ArrayList<>();
        final List<NoiseProvider> requiredNoises = new ArrayList<>();
        int minY = Integer.MIN_VALUE, maxY = Integer.MAX_VALUE;

        private PassBuilder(Function function) {
            this.function = function;
        }

        public PassBuilder withRange(int minY, int maxY) {
            this.minY = minY;
            this.maxY = maxY;
            return this;
        }

        public PassBuilder withDependencies(Synthesizer... synthesizers) {
            for (Synthesizer synthesizer : synthesizers) {
                if (synthesizer == null) continue;
                dependencies.add(synthesizer);
            }
            return this;
        }

        public PassBuilder withNoises(NoiseProvider... noises) {
            for (NoiseProvider noise : noises) {
                if (noise == null) continue;
                requiredNoises.add(noise);
            }
            return this;
        }

        private Synthesizer.Pass build(@Nullable String debugName, InterpolatingFieldResolution synthResolution) {
            ImmutableList.Builder<Dependency> directDependencies = ImmutableList.builder();
            for (Synthesizer dependency : dependencies) {

                // todo: make this not a requirement?
                //       but, this does simplify padding stuff a ton...
                //       doing otherwise REALLY REALLY complicates things.
                // because resolutions always go from coarse -> fine down the dependency tree,
                // padding is ALWAYS guaranteed to be maintained, no matter what. each cell
                // will always encapsulate its dependents.
                assert dependency.resolution.xzScale() >= synthResolution.xzScale() :
                        "dependencies must have xz resolution greater than or equal to their dependent! \n" +
                        "currently: DEPENDENT " + (debugName != null ? debugName + " " : "") + "= " + synthResolution.xzScale() + "\n" +
                        "           DEPENDENCY " + (dependency.debugName != null ? dependency.debugName + " " : "") + "= " + dependency.resolution.xzScale();

                int dependencyMinY = Math.max(minY, dependency.minY), dependencyMaxY = Math.min(maxY, dependency.maxY);
                directDependencies.add(new Dependency(dependency, dependencyMinY, dependencyMaxY));
            }
            return new Pass(function, directDependencies.build(), ImmutableList.copyOf(requiredNoises), minY, maxY);
        }
    }

    public record Pass(Function function, ImmutableList<Dependency> dependencies, ImmutableList<NoiseProvider> noises, int minY, int maxY) {}

    public record Dependency(Synthesizer synthesizer, int minY, int maxY) {}

    public interface Function {
        float compute(SynthesizerContext ctx);
    }
}


