package birsy.clinker.common.world.level.gen.metachunk;

import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.ChunkPos;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MetaChunkHandler {
    Set<MetaChunk> metaChunkGenerationQueue;
    Map<Long, MetaChunk> metaChunkRoots;

    public MetaChunkHandler() {
        this.metaChunkGenerationQueue = new HashSet<>(64);
        this.metaChunkRoots = new Long2ObjectArrayMap<>();
    }

    public void generateMetaChunks(ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> worldGenMailbox) {
        metaChunkGenerationQueue.stream()
                .sorted(Comparator.comparingInt((metaChunk) -> ((MetaChunk) metaChunk).size).reversed())
                .forEachOrdered((metaChunk) -> {
                    worldGenMailbox.tell(ChunkTaskPriorityQueueSorter.message(
                            () -> CompletableFuture.runAsync(metaChunk::waitForParentGeneration)
                                                   .thenRunAsync(metaChunk::generate),
                            ChunkPos.asLong(metaChunk.minimumX + metaChunk.size/2, metaChunk.minimumZ + metaChunk.size/2),
                            () -> ChunkLevel.MAX_LEVEL
                    ));
                });
        metaChunkGenerationQueue.clear();
    }

    public void queueMetaChunkGeneration(ChunkPos pos) {
        long rootIndex = getMetaChunkRootIndex(pos);
        MetaChunk root = metaChunkRoots.computeIfAbsent(rootIndex,
                (chunkLong) -> new MetaChunk(ChunkPos.getX(chunkLong), ChunkPos.getZ(chunkLong)));
        root.subdivideToContain(pos);
        MetaChunk containingMetachunk = root;
        do {
            // add only meta-chunks that are yet to be generated
            if (!containingMetachunk.generated) metaChunkGenerationQueue.add(containingMetachunk);
            containingMetachunk = containingMetachunk.getChildContainingPos(pos);
        } while (containingMetachunk.hasChildren());
    }

    public boolean shouldAwaitMetaChunkGen(ChunkPos pos) {
        return false;
//        long rootIndex = getMetaChunkRootIndex(pos);
//        MetaChunk root = metaChunkRoots.get(rootIndex);
//        if (root == null) throw new RuntimeException();
//
//        MetaChunk containingMetachunk = root;
//        while (containingMetachunk.hasChildren()) {
//            if (!containingMetachunk.generated) return true;
//            containingMetachunk = containingMetachunk.getChildContainingPos(pos);
//        }
//        return false;
    }

    private long getMetaChunkRootIndex(ChunkPos pos) {
        return ChunkPos.asLong((pos.x / MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE, (pos.z / MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE);
    }
}
