package birsy.clinker.common.world.level.gen.content.biome;

import birsy.clinker.common.world.level.gen.system.biome.resolver.BiomeLayerOperation;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiomeNeighborhood;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;

import java.util.Arrays;

public class BiomeLayerOperations {
    public record Smooth() implements BiomeLayerOperation {
        private static final ThreadLocal<int[]> threadedCounts =
                ThreadLocal.withInitial(() -> new int[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()]);
        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
            int[] counts = threadedCounts.get();
            // reset value
            Arrays.fill(counts, 0);

            int highestCount = 0;
            int winningBiome = current.id;
            for (int dz = -1; dz <= 1; dz++) {
                for (int dx = -1; dx <= 1; dx++) {
                    ProtoBiome neighbor = previousLayerNeighborhood.fromOffset(dx, dz);
                    int biomeCount = counts[neighbor.id] + 1;
                    counts[neighbor.id] = biomeCount;
                    if (biomeCount > highestCount) {
                        winningBiome = neighbor.id;
                        highestCount = biomeCount;
                    }
                }
            }
            return ClinkerRegistries.PROTO_BIOME_REGISTRY.byIdOrThrow(winningBiome);
        }
    }

    public record RandomizeIntoNeighbor(double probability) implements BiomeLayerOperation {
        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
            int x = random.nextIntBetweenInclusive(-1, 1), z = random.nextInt(-1, 1);
            return random.nextDouble() > probability ? current : previousLayerNeighborhood.fromOffset(x, z);
        }
    }

    public record Mutate(ProtoBiome biomeToMutate, SimpleWeightedRandomList<ProtoBiome> results) implements BiomeLayerOperation {
        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
            if (current == biomeToMutate) return results.getRandomValue(random).orElse(current);
            return current;
        }
    }

    public record Surround(ProtoBiome biomeToSurround, ProtoBiome surroundingBiome) implements BiomeLayerOperation {
        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
            if (current == biomeToSurround) return current;
            for (int offsetIndex : ProtoBiomeNeighborhood.NEIGHBOR_INDICES)
                if (previousLayerNeighborhood.fromIndex(offsetIndex) == biomeToSurround) return surroundingBiome;
            return current;
        }
    }

    // biome A takes priority
    public record CreateBorders(ProtoBiome biomeA, ProtoBiome biomeB, ProtoBiome borderBiome) implements BiomeLayerOperation {
        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
            if (current == biomeB) {
                for (int offsetIndex : ProtoBiomeNeighborhood.NEIGHBOR_INDICES)
                    if (previousLayerNeighborhood.fromIndex(offsetIndex) == biomeA) return borderBiome;
            }
            return current;
        }
    }
}
