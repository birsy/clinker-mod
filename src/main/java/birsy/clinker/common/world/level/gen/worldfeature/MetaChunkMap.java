package birsy.clinker.common.world.level.gen.worldfeature;

import birsy.clinker.core.Clinker;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetaChunkMap {
    public static final int MAX_DEPTH = 10;
    private final RandomState randomState;
    private final PositionalRandomFactory metaChunkRandom;
    private final Map<Long, MetaChunk>[] metaChunkCache;

    public MetaChunkMap(RandomState randomState) {
        this.randomState = randomState;
        this.metaChunkRandom = randomState.random.fromHashOf(Clinker.resource("meta_chunk")).forkPositional();

        this.metaChunkCache = new ConcurrentHashMap[MAX_DEPTH];
        for (int i = 0; i < this.metaChunkCache.length; i++) {
            this.metaChunkCache[i] = new ConcurrentHashMap<>();
        }
    }

    int getMetaChunkSizeForDepth(int depth) {
        return 16 << depth;
    }

    MetaChunk getMetaChunk(int depth, int blockX, int blockZ) {
        int size = getMetaChunkSizeForDepth(depth);
        int metaChunkX = Math.floorDiv(blockX, size), metaChunkZ = Math.floorDiv(blockZ, size);

        long key = MetaChunk.asLong(metaChunkX, metaChunkZ);

        // if contained in cache, return cached meta-chunk.
        if (metaChunkCache[depth].containsKey(key))
            return metaChunkCache[depth].get(key);

        // otherwise, create a new meta-chunk.
        MetaChunk newChunk = new MetaChunk(size, metaChunkX, metaChunkZ);

        // generate "parents"
        if (depth < MAX_DEPTH) {
            int parentSize = getMetaChunkSizeForDepth(depth + 1);
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    MetaChunk parent = getMetaChunk(
                            depth + 1,
                            (Math.floorDiv(blockX, parentSize) + xOffset) * parentSize,
                            (Math.floorDiv(blockZ, parentSize) + zOffset) * parentSize
                    );
                    // propagate features "downward" to "child"
                    parent.propagateFeatures(newChunk);
                }
            }
        }

        generateWorldFeatures(depth, newChunk);
        metaChunkCache[depth].put(key, newChunk);
        return newChunk;
    }

    void generateWorldFeatures(int depth, MetaChunk metaChunk) {
        RandomSource random = metaChunkRandom.at(metaChunk.minX(), depth, metaChunk.maxZ());
        WorldFeature feature = new TestWorldFeature(depth);
        feature.plan(metaChunk, random);

        metaChunk.worldFeatures.add(feature);
    }

    public List<WorldFeature> getWorldFeatures(int blockX, int blockZ) {
        return getMetaChunk(0, blockX, blockZ).worldFeatures;
    }
}
