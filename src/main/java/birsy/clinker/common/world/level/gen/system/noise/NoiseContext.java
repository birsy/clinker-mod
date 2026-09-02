package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator;

import java.util.function.Supplier;

public interface NoiseContext extends NoiseProvider {
    default int yRangeStart() {
        return Integer.MAX_VALUE;
    }
    default int yRangeEnd() {
        return Integer.MAX_VALUE;
    }
    double retrieve(Synthesizer computer, int x, int y, int z);
    default double retrieve(Supplier<Synthesizer> computer, int x, int y, int z) {
        return retrieve(computer.get(), x, y, z);
    }
    VoronoiEvaluator getVoronoi(String name);
}
