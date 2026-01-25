package birsy.clinker.common.world.level.gen.content.biome;

import birsy.clinker.common.world.level.gen.system.biome.resolver.BiomeLayerOperation;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiomeNeighborhood;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.util.RandomSource;

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

//    public record Smooth() implements BiomeLayerOperation {
//        private static final ThreadLocal<int[]> threadedIds =
//                ThreadLocal.withInitial(() -> new int[9]);
//        private static final ThreadLocal<int[]> threadedCounts =
//                ThreadLocal.withInitial(() -> new int[9]);
//        @Override
//        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
//            // at most 9 different biome ids among the 3x3
//            int[] ids = new int[9];
//            int[] counts = new int[9];
//            int used = 0;
//
//            int highestCount = 0;
//            int winningBiome = current.id;
//
//            for (int dz = -1; dz <= 1; dz++) {
//                for (int dx = -1; dx <= 1; dx++) {
//                    ProtoBiome neighbor = previousLayerNeighborhood.fromOffset(dx, dz);
//                    int id = neighbor.id;
//
//                    // find id in our small array
//                    int idx = -1;
//                    for (int i = 0; i < used; i++) {
//                        if (ids[i] == id) { idx = i; break; }
//                    }
//                    if (idx == -1) {
//                        ids[used] = id;
//                        counts[used] = 1;
//                        idx = used++;
//                    } else {
//                        counts[idx]++;
//                    }
//
//                    if (counts[idx] > highestCount) {
//                        highestCount = counts[idx];
//                        winningBiome = id;
//                    }
//                }
//            }
//
//            return Registries.PROTO_BIOME_REGISTRY.byIdOrThrow(winningBiome);
//        }
//    }

    public record RandomizeIntoNeighbor(double probability) implements BiomeLayerOperation {
        @Override
        public ProtoBiome apply(int blockX, int blockZ, ProtoBiome current, ProtoBiomeNeighborhood previousLayerNeighborhood, RandomSource random, NoiseContext noiseContext) {
            int x = random.nextIntBetweenInclusive(-1, 1), z = random.nextInt(-1, 1);
            return random.nextDouble() > probability ? current : previousLayerNeighborhood.fromOffset(x, z);
        }
    }
}
