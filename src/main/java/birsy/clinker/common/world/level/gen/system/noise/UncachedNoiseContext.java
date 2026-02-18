package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator;
import birsy.clinker.core.registry.ClinkerRegistries;

public final class UncachedNoiseContext implements NoiseContext {
    final SeededNoiseHolder noiseProvider;

    public UncachedNoiseContext(SeededNoiseHolder provider) {
        this.noiseProvider = provider;
        // register all noises
        // i should just make noises static, shouldn't i...
        NoiseDependencyCollector collector = new NoiseDependencyCollector(provider);
        for (NoiseComputer noiseComputer : ClinkerRegistries.NOISE_COMPUTER_REGISTRY) {
            noiseComputer.dependencies.accept(collector, noiseProvider);
        }
    }

    @Override
    public double retrieve(NoiseComputer computer, int x, int y, int z) {
        return computer.filler.compute(x, y, z, this);
    }

    @Override
    public double sample(String name, double x, double y, double z) {
        return noiseProvider.sample(name, x, y, z);
    }

    @Override
    public double sample(String name, double x, double y) {
        return noiseProvider.sample(name, x, y);
    }

    // todo: this
    @Override
    public VoronoiEvaluator getVoronoi(String name) {
        throw new UnsupportedOperationException();
    }
}
