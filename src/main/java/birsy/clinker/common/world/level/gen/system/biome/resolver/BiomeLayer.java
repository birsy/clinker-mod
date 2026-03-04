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

    private final ThreadLocal<int[]> threadNeighborhood = ThreadLocal.withInitial(() -> new int[9]);

    private final int cacheSize;
    private final ThreadLocal<Long2IntLinkedOpenHashMap> threadCache;

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

        this.cacheSize = 1024;
        this.threadCache = ThreadLocal.withInitial(() -> {
            Long2IntLinkedOpenHashMap cache = new Long2IntLinkedOpenHashMap(this.cacheSize + 1);
            cache.defaultReturnValue(-1);
            return cache;
        });
    }

    public ProtoBiome getOrCreateCellAt(int blockX, int blockZ) {
        return ClinkerRegistries.PROTO_BIOME_REGISTRY.byIdOrThrow(getIdAt(blockX, blockZ));
    }

    int getIdAt(int blockX, int blockZ) {
        int cellX = toCellPos(blockX, cellScale),
            cellZ = toCellPos(blockZ, cellScale);
        long key = toCellKey(cellX, cellZ);

        Long2IntLinkedOpenHashMap cache = threadCache.get();
        int cached = cache.getAndMoveToFirst(key);
        if (cached >= 0) return cached;

        int id = createCellAt(threadNeighborhood.get(), cellX, cellZ);
        if (cache.size() >= cacheSize) cache.removeLastInt();
        cache.putAndMoveToFirst(key, id);

        return id;
    }

    private int createCellAt(int[] neighborhood, int cellX, int cellZ) {
        if (previousLayer == null) {
            Arrays.fill(neighborhood, ClinkerProtoBiomes.UNINITIALIZED.get().id);
        } else {
            int i = 0;
            for (int z = -1; z <= 1; z++) {
                int offsetZ = fromCellPos(cellZ + z, cellScale);
                for (int x = -1; x <= 1; x++) {
                    int offsetX = fromCellPos(cellX + x, cellScale);
                    neighborhood[i++] = previousLayer.getIdAt(offsetX, offsetZ);
                }
            }
        }
        RandomSource cellRandom = randomFactory.at(cellX, cellScale, cellZ);
        int currentId = neighborhood[4];
        for (BiomeLayerOperation op : operations)
            currentId = op.apply(fromCellPos(cellX, cellScale), fromCellPos(cellZ, cellScale), currentId, neighborhood, cellRandom, context);
        return currentId;
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
