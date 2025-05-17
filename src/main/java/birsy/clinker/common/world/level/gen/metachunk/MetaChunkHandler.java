package birsy.clinker.common.world.level.gen.metachunk;

import birsy.clinker.common.networking.packet.debug.ClientboundMetaChunkBeginGenDebugPacket;
import birsy.clinker.common.networking.packet.debug.ClientboundMetaChunkFinishGenDebugPacket;
import birsy.clinker.common.world.level.gen.metachunk.feature.MetaChunkFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class MetaChunkHandler {
    private final long worldSeed;
    ConcurrentMap<Long, MetaChunk> metaChunkRoots;
    ConcurrentMap<MetaChunk, CountDownLatch> generatingMetaChunks;

    public MetaChunkHandler(long worldSeed) {
        this.worldSeed = worldSeed;
        this.metaChunkRoots = new ConcurrentHashMap<>();
        this.generatingMetaChunks = new ConcurrentHashMap<>();
    }

    // generates all the ancestor meta-chunks to a given chunkPos, in sequence.
    public void generateMetaChunks(ChunkPos pos) {
        MetaChunk metaChunk = metaChunkRoots.computeIfAbsent(getMetaChunkRootIndex(pos),
                (rootIndex) -> new MetaChunk(ChunkPos.getX(rootIndex), ChunkPos.getZ(rootIndex))
        );
        do {
            if (!metaChunk.isGenerated()) {
                CountDownLatch latch = new CountDownLatch(1);
                CountDownLatch existing = generatingMetaChunks.putIfAbsent(metaChunk, latch);

                if (existing != null) {
                    // some other thread is generating, so wait.
                    try {
                        existing.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // we "won" the right to generate
                    try {
                        PacketDistributor.sendToAllPlayers(new ClientboundMetaChunkBeginGenDebugPacket(metaChunk, pos));
                        metaChunk.generate(new SingleThreadedRandomSource(
                                Mth.getSeed(metaChunk.minimumX,
                                        metaChunk.minimumZ + (int)(worldSeed % Integer.MAX_VALUE),
                                        metaChunk.depth + (int)((worldSeed >> Integer.SIZE) % Integer.MAX_VALUE)
                                )
                        ));
                        PacketDistributor.sendToAllPlayers(new ClientboundMetaChunkFinishGenDebugPacket(metaChunk));
                        if (!metaChunk.markGenerated()) {
                            throw new IllegalStateException("MetaChunk generation state inconsistency");
                        }
                    } finally {
                        // signal any waiters, then remove
                        latch.countDown();
                        generatingMetaChunks.remove(metaChunk);
                    }
                }
            }

            // continue to the next child.
            metaChunk.createChildrenIfNeeded();
            metaChunk = metaChunk.getChildContainingPos(pos);
        } while (!metaChunk.isLeaf());
    }

    public MetaChunk getLeafContaining(ChunkPos chunkPos) {
        return this.getLeafContaining(chunkPos.x, chunkPos.z);
    }

    public MetaChunk getLeafContaining(int chunkX, int chunkZ) {
        MetaChunk metaChunk = metaChunkRoots.get(getMetaChunkRootIndex(chunkX, chunkZ));
        if (metaChunk == null) {
            Clinker.LOGGER.warn("No root MetaChunk at {}. {}", chunkX, chunkZ);
            return null;
        }
        do {
            metaChunk = metaChunk.getChildContainingPos(chunkX, chunkZ);
        } while (!metaChunk.isLeaf());
        return metaChunk;
    }

    public MetaChunk getOrGenerateLeafContaining(int chunkX, int chunkZ) {
        this.generateMetaChunks(new ChunkPos(chunkX, chunkZ));
        MetaChunk metaChunk = metaChunkRoots.get(getMetaChunkRootIndex(chunkX, chunkZ));
        do {
            metaChunk = metaChunk.getChildContainingPos(chunkX, chunkZ);
        } while (!metaChunk.isLeaf());
        return metaChunk;
    }

    private long getMetaChunkRootIndex(ChunkPos pos) {
        return getMetaChunkRootIndex(pos.x, pos.z);
    }

    private long getMetaChunkRootIndex(int chunkX, int chunkZ) {
        return ChunkPos.asLong(
                Math.floorDiv(chunkX, MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE,
                Math.floorDiv(chunkZ, MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE
        );
    }
    
    private void collectMetaChunkFeatures(List<MetaChunkFeature> listToPopulate, ChunkPos chunkPos) {
        //listToPopulate.addAll(this.getLeafContaining(chunkPos.x, chunkPos.z).features);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                listToPopulate.addAll(this.getOrGenerateLeafContaining(chunkPos.x + x, chunkPos.z + z).features);
            }
        }
    }

    private static ThreadLocal<List<MetaChunkFeature>> list = ThreadLocal.withInitial(ArrayList::new);
    public void realizeMetaChunkFeatures(ChunkAccess chunkAccess) {
//        collectMetaChunkFeatures(list.get(), chunkAccess.getPos());
//
//        // todo: sort the list according to priority.
//
//        for (int i = 0; i < list.get().size(); i++) {
//            MetaChunkFeature feature = list.get().get(i);
//            feature.realizeBlocks(chunkAccess);
//        }
    }

//    public boolean shouldAwaitMetaChunkGen(ChunkPos pos) {
//        MetaChunk root = metaChunkRoots.get(getMetaChunkRootIndex(pos));
//        if (root == null) throw new RuntimeException();
//
//        MetaChunk containingMetachunk = root;
//        while (!containingMetachunk.isLeaf()) {
//            if (!containingMetachunk.generated) return true;
//            containingMetachunk = containingMetachunk.getChildContainingPos(pos);
//        }
//        return false;
//    }
//    public void generateMetaChunks(ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> worldGenMailbox) {
//        metaChunkGenerationQueue.stream()
//                .sorted(Comparator.comparingInt((metaChunk) -> ((MetaChunk) metaChunk).size).reversed())
//                .forEachOrdered((metaChunk) -> {
//                    worldGenMailbox.tell(ChunkTaskPriorityQueueSorter.message(
//                            () -> CompletableFuture.runAsync(metaChunk::waitForParentGeneration)
//                                                   .thenRunAsync(metaChunk::generate),
//                            ChunkPos.asLong(metaChunk.minimumX + metaChunk.size/2, metaChunk.minimumZ + metaChunk.size/2),
//                            () -> ChunkLevel.MAX_LEVEL
//                    ));
//                });
//        metaChunkGenerationQueue.clear();
//    }
//
//    public void queueMetaChunkGeneration(ChunkPos pos) {
//        long rootIndex = getMetaChunkRootIndex(pos);
//        MetaChunk root = metaChunkRoots.computeIfAbsent(rootIndex,
//                (chunkLong) -> new MetaChunk(ChunkPos.getX(chunkLong), ChunkPos.getZ(chunkLong)));
//        root.subdivideToContain(pos);
//        MetaChunk containingMetachunk = root;
//        do {
//            // add only meta-chunks that are yet to be generated
//            if (!containingMetachunk.generated && !containingMetachunk.queuedForGeneration) {
//                containingMetachunk.queuedForGeneration = true;
//                metaChunkGenerationQueue.add(containingMetachunk);
//            }
//            containingMetachunk = containingMetachunk.getChildContainingPos(pos);
//        } while (!containingMetachunk.isLeaf());
//    }
//
//    public boolean shouldAwaitMetaChunkGen(ChunkPos pos) {
//        return false;
////        long rootIndex = getMetaChunkRootIndex(pos);
////        MetaChunk root = metaChunkRoots.get(rootIndex);
////        if (root == null) throw new RuntimeException();
////
////        MetaChunk containingMetachunk = root;
////        while (containingMetachunk.hasChildren()) {
////            if (!containingMetachunk.generated) return true;
////            containingMetachunk = containingMetachunk.getChildContainingPos(pos);
////        }
////        return false;
//    }
//
//    private long getMetaChunkRootIndex(ChunkPos pos) {
//        return ChunkPos.asLong(
//                Math.floorDiv(pos.x, MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE,
//                Math.floorDiv(pos.z, MetaChunk.MAX_SIZE) * MetaChunk.MAX_SIZE
//        );
//    }
}
