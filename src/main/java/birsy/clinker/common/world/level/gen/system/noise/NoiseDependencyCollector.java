package birsy.clinker.common.world.level.gen.system.noise;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

public final class NoiseDependencyCollector {
    private final NoiseRegistry noiseRegistry;
    final LinkedHashSet<NoiseComputer> dependencies = new LinkedHashSet<>();

    NoiseDependencyCollector(NoiseRegistry noiseRegistry) {
        this.noiseRegistry = noiseRegistry;
    }

    public void addDependency(NoiseComputer computer) {
        computer.dependencies.accept(this, noiseRegistry);
        if (!dependencies.contains(computer))
            dependencies.addLast(computer);
    }

    public void addDependency(Supplier<NoiseComputer> computer) {
        addDependency(computer.get());
    }
}