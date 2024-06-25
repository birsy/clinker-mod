package birsy.clinker.common.world.level.gen;

import birsy.clinker.core.Clinker;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetaChunkTracker {
    private static final ConcurrentMap<MetaChunk.MetaChunkPos, MetaChunk> META_CHUNK_CACHE = new ConcurrentHashMap<>();

    public static void clear() {
        META_CHUNK_CACHE.clear();
    }

    public static MetaChunk getOrCreateMetaChunkAtChunk(WorldGenLevel level, ChunkPos pos) {
        MetaChunk.MetaChunkPos mcp = MetaChunk.MetaChunkPos.fromChunkPos(pos);
        if (META_CHUNK_CACHE.containsKey(mcp)) {
            return META_CHUNK_CACHE.get(mcp);
        } else {
            Clinker.LOGGER.info("Generating New Metachunk...");
            MetaChunk mc = new MetaChunk(level, mcp);
            mc.populate();
            META_CHUNK_CACHE.put(mcp, mc);
            return mc;
        }
    }
}
