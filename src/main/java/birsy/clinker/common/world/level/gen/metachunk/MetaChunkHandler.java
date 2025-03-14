package birsy.clinker.common.world.level.gen.metachunk;

import net.minecraft.world.level.ChunkPos;

import java.util.Map;
import java.util.Set;

public class MetaChunkHandler {
    Set<MetaChunk> metaChunkGenerationQueue;
    Map<Long, MetaChunk> metaChunkRoots;

    private long getMetaChunkRootIndex(ChunkPos pos) {
        return ChunkPos.asLong((pos.x / MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE, (pos.z / MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE);
    }

    public void queueMetaChunkGeneration(ChunkPos pos) {
        long rootIndex = getMetaChunkRootIndex(pos);
        MetaChunk root = metaChunkRoots.computeIfAbsent(rootIndex,
                (chunkLong) -> new MetaChunk(ChunkPos.getX(chunkLong), ChunkPos.getZ(chunkLong)));
        root.subdivideToContain(pos);
        MetaChunk containingMetachunk = root;
        while (containingMetachunk.hasChildren()) {
            // add only metachunks that are yet ungenerated
            if (!containingMetachunk.generated) metaChunkGenerationQueue.add(containingMetachunk);
            containingMetachunk = containingMetachunk.getChildContainingPos(pos);
        }
    }

    public boolean shouldAwaitMetaChunkGen(ChunkPos pos) {
        long rootIndex = getMetaChunkRootIndex(pos);
        MetaChunk root = metaChunkRoots.get(rootIndex);
        if (root == null) throw new RuntimeException();

        MetaChunk containingMetachunk = root;
        while (containingMetachunk.hasChildren()) {
            if (!containingMetachunk.generated) return true;
            containingMetachunk = containingMetachunk.getChildContainingPos(pos);
        }
        return false;
    }
}
