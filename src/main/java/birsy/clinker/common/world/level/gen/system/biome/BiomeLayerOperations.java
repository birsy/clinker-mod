package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;

public class BiomeLayerOperations {
    public record WeightedSmooth() implements BiomeLayerOperation {
        private static final int[][] WEIGHTS = {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };

        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current,
                                ProtoBiomeNeighborhood neighborhood, RandomSource random,
                                NoiseContext context) {
            Map<ProtoBiome, Integer> weightedCounts = new HashMap<>();
            for (int dz = -1; dz <= 1; dz++) {
                for (int dx = -1; dx <= 1; dx++) {
                    ProtoBiome neighbor = neighborhood.fromOffset(dx, dz);
                    int weight = WEIGHTS[dz + 1][dx + 1];
                    weightedCounts.put(neighbor, weightedCounts.getOrDefault(neighbor, 0) + weight);
                }
            }
            ProtoBiome winner = current;
            int maxWeight = 0;
            for (Map.Entry<ProtoBiome, Integer> entry : weightedCounts.entrySet()) {
                if (entry.getValue() > maxWeight) {
                    maxWeight = entry.getValue();
                    winner = entry.getKey();
                }
            }
            return winner;
        }
    }

    public record RandomizeIntoNeighbor(double probability) implements BiomeLayerOperation {
        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
            int x = random.nextIntBetweenInclusive(-1, 1), z = random.nextInt(-1, 1);
            return random.nextDouble() > probability ? current : previousLayerNeighborhood.fromOffset(x, z);
        }
    }
}
