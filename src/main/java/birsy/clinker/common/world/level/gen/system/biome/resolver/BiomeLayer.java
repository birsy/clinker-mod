package birsy.clinker.common.world.level.gen.system.biome.resolver;

import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.worldgen.ClinkerProtoBiomes;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
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
    final int cellScale;
    private final int cellSizeBlocks;

    private final ThreadLocal<int[]> threadNeighborhood = ThreadLocal.withInitial(() -> new int[9]);

    private final int cacheSize;
    private final ThreadLocal<Long2IntOpenHashMap> threadCache;

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
            Long2IntOpenHashMap cache = new Long2IntOpenHashMap(this.cacheSize + 1);
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

        Long2IntOpenHashMap cache = threadCache.get();
        int cached = cache.get(key);
        if (cached >= 0) return cached;

        int id = createCellAt(threadNeighborhood.get(), cellX, cellZ);
        if (cache.size() >= cacheSize) cache.clear();
        cache.put(key, id);

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

        return applyOperations(cellX, cellZ, neighborhood);
    }

    public int[] getIdsInArea(int minCellX, int minCellZ, int cellsX, int cellsZ) {
        int[] out = new int[cellsX * cellsZ];
        int[] neighborhood = new int[9];

        if (previousLayer == null) {
            Arrays.fill(neighborhood, ClinkerProtoBiomes.UNINITIALIZED.get().id);
            for (int z = 0; z < cellsZ; z++) {
                int cellZ = minCellZ + z;
                for (int x = 0; x < cellsX; x++) {
                    int cellX = minCellX + x;
                    out[z * cellsX + x] = applyOperations(cellX, cellZ, neighborhood);
                }
            }
            return out;
        }

        int paddedX = cellsX + 2, paddedZ = cellsZ + 2;
        int prevScale = previousLayer.cellScale;
        int[] prevCellXForCol = new int[paddedX], prevCellZForRow = new int[paddedZ];
        int minPrevCellX = Integer.MAX_VALUE, maxPrevCellX = Integer.MIN_VALUE;
        int minPrevCellZ = Integer.MAX_VALUE, maxPrevCellZ = Integer.MIN_VALUE;

        for (int i = 0; i < paddedX; i++) {
            int blockX = fromCellPos(minCellX - 1 + i, cellScale);
            int prevCellX = toCellPos(blockX, prevScale);
            prevCellXForCol[i] = prevCellX;
            minPrevCellX = Math.min(minPrevCellX, prevCellX);
            maxPrevCellX = Math.max(maxPrevCellX, prevCellX);
        }
        for (int i = 0; i < paddedZ; i++) {
            int blockZ = fromCellPos(minCellZ - 1 + i, cellScale);
            int prevCellZ = toCellPos(blockZ, prevScale);
            prevCellZForRow[i] = prevCellZ;
            minPrevCellZ = Math.min(minPrevCellZ, prevCellZ);
            maxPrevCellZ = Math.max(maxPrevCellZ, prevCellZ);
        }

        int prevSizeX = maxPrevCellX - minPrevCellX + 1, prevSizeZ = maxPrevCellZ - minPrevCellZ + 1;
        int[] parentIds = previousLayer.getIdsInArea(minPrevCellX, minPrevCellZ, prevSizeX, prevSizeZ);

        for (int z = 0; z < cellsZ; z++) {
            int cellZ = minCellZ + z;
            for (int x = 0; x < cellsX; x++) {
                int cellX = minCellX + x;

                int ni = 0;
                for (int dz = 0; dz <= 2; dz++) {
                    int prevRow = prevCellZForRow[z + dz] - minPrevCellZ;
                    for (int dx = 0; dx <= 2; dx++) {
                        int prevCol = prevCellXForCol[x + dx] - minPrevCellX;
                        neighborhood[ni++] = parentIds[prevRow * prevSizeX + prevCol];
                    }
                }
                out[z * cellsX + x] = applyOperations(cellX, cellZ, neighborhood);
            }
        }
        return out;
    }

    private int applyOperations(int cellX, int cellZ, int[] neighborhood) {
        RandomSource cellRandom = randomFactory.at(cellX, cellScale, cellZ);
        int currentId = neighborhood[4];
        for (BiomeLayerOperation op : operations)
            currentId = op.apply(fromCellPos(cellX, cellScale), fromCellPos(cellZ, cellScale),
                    currentId, neighborhood, cellRandom, context);
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
