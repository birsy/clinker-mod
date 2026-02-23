package birsy.clinker.common.world.level.gen.system.biome.resolver;

import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunk;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.worldgen.ClinkerProtoBiomes;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class BiomeLayer {
    @Nullable
    private final BiomeLayer previousLayer;
    private final PositionalRandomFactory randomFactory;
    private final UncachedNoiseContext context;
    private final BiomeLayerOperation[] operations;
    private final int cellScale, cellSizeBlocks;
    private final Cache<Long, Integer> cache;

    private final ThreadLocal<ProtoBiomeNeighborhood> threadNeighborhood = ThreadLocal.withInitial(ProtoBiomeNeighborhood::new);

    public BiomeLayer(
            @Nullable BiomeLayer previousLayer,
            PositionalRandomFactory randomFactory,
            UncachedNoiseContext context,
            int cellScale,
            BiomeLayerOperation... operations) {
        this.previousLayer = previousLayer;
        this.randomFactory = randomFactory;
        this.context = context;
        this.operations = operations;
        this.cellScale = cellScale;
        this.cellSizeBlocks = 1 << cellScale;

        this.cache = Caffeine.newBuilder()
                .maximumSize(512)
                .executor(Runnable::run)
                .build();
    }

    public ProtoBiome getOrCreateCellAt(int blockX, int blockZ) {
        int cellX = toCellPos(blockX, cellScale),
            cellZ = toCellPos(blockZ, cellScale);
        long key = toCellKey(cellX, cellZ);

        ProtoBiomeNeighborhood neighborhood = threadNeighborhood.get();
        int id = cache.get(key, (cellKey) -> createCellAt(neighborhood, cellX, cellZ).id);
        return ClinkerRegistries.PROTO_BIOME_REGISTRY.byIdOrThrow(id);
    }

    private ProtoBiome createCellAt(ProtoBiomeNeighborhood neighborhood, int cellX, int cellZ) {
        // populate neighborhood
        if (previousLayer == null) {
            Arrays.fill(neighborhood.array, ClinkerProtoBiomes.UNINITIALIZED.get());
        } else {
            int i = 0;
            for (int z = -1; z <= 1; z++) {
                int offsetZ = fromCellPos(cellZ + z, cellScale);
                for (int x = -1; x <= 1; x++) {
                    int offsetX = fromCellPos(cellX + x, cellScale);
                    neighborhood.array[i++] = previousLayer.getOrCreateCellAt(offsetX, offsetZ);
                }
            }
        }
        // y = cellScale ensures that differently sized layers have different randoms
        RandomSource cellRandom = randomFactory.at(cellX, cellScale, cellZ);
        ProtoBiome current = neighborhood.array[4];
        for (BiomeLayerOperation operation : operations)
            current = operation.apply(
                    fromCellPos(cellX, cellScale),
                    fromCellPos(cellZ, cellScale),
                    current, neighborhood, cellRandom, context
            );
        return current;
    }

    public static long toCellKey(int cellX, int cellZ) {
        return ((long)cellX & 0xFFFFFFFFL) | (((long)cellZ & 0xFFFFFFFFL) << 32);
    }
    public static int toCellPos(int blockPos, int cellScale) {
        return blockPos >> cellScale;
    }
    // returns the block at the cell's center
    public static int fromCellPos(int cellPos, int cellScale) {
        return (cellPos << cellScale) + (1 << (cellScale - 1));
    }
}
