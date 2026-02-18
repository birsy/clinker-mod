package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;
import birsy.clinker.core.util.noise.FastNoiseLite;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

public final class NoiseDependencyCollector {
    final NoiseRegistry noiseRegistry;
    final LinkedHashSet<NoiseComputer> dependencies = new LinkedHashSet<>();
    final ObjectArraySet<String> dependentVoronoiDefinitions;

    NoiseDependencyCollector(NoiseRegistry noiseRegistry) {
        this.noiseRegistry = new NoiseRegistryWrapper(this, noiseRegistry);
        this.dependentVoronoiDefinitions = new ObjectArraySet<>(4);
    }

    public void addDependency(NoiseComputer computer) {
        computer.dependencies.accept(this, noiseRegistry);
        if (!dependencies.contains(computer))
            dependencies.addLast(computer);
    }

    public void addDependency(Supplier<NoiseComputer> computer) {
        addDependency(computer.get());
    }

    private record NoiseRegistryWrapper(NoiseDependencyCollector collector, NoiseRegistry noiseRegistry) implements NoiseRegistry {
        @Override
        public void registerNoise(String name, Supplier<FastNoiseLite> factory) {
            this.noiseRegistry.registerNoise(name, factory);
        }
        @Override
        public void registerVoronoi(String name, Supplier<VoronoiDefinition> factory) {
            this.collector.dependentVoronoiDefinitions.add(name);
            this.noiseRegistry.registerVoronoi(name, factory);
        }
    }
}