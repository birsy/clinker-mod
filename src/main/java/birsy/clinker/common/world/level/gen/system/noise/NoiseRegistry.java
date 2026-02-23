package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;
import birsy.clinker.core.util.noise.FastNoiseLite;

import java.util.function.Supplier;

public interface NoiseRegistry {
    void registerNoise(String name, Supplier<FastNoiseLite> factory);

    default void registerNoise(String name) {
        this.registerNoise(name, 1, 1, 1, 1, 1);
    }

    default void registerNoise(String name, int octaves, double frequency, double lacunarity, double gain, double weightedStrength) {
        this.registerNoise(name, () -> {
            FastNoiseLite noise = new FastNoiseLite();
            noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);

            noise.SetFractalType(FastNoiseLite.FractalType.FBm);
            noise.SetFrequency((float) frequency);

            noise.SetFractalOctaves(octaves);
            noise.SetFractalLacunarity((float) lacunarity);
            noise.SetFractalGain((float) gain);
            noise.SetFractalWeightedStrength((float) weightedStrength);
            return noise;
        });
    }

    default void registerVoronoi2d(String name, int cellSize) {
        this.registerVoronoi(name, () -> VoronoiDefinition.twoDimensional(cellSize));
    }
    default void registerVoronoi3d(String name, int xzCellSize, int yCellSize) {
        this.registerVoronoi(name, () -> VoronoiDefinition.threeDimensional(xzCellSize, yCellSize));
    }
    void registerVoronoi(String name, Supplier<VoronoiDefinition> factory);
}
