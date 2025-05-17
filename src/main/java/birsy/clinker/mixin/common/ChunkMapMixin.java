package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.gen.metachunk.MetaChunkHandler;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunkMap;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.ChunkGenerationTask;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements MetaChunkMap {
    @Unique private MetaChunkHandler clinker$metaChunkHandler;

    @Inject(
            method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Ljava/util/concurrent/Executor;Lnet/minecraft/util/thread/BlockableEventLoop;Lnet/minecraft/world/level/chunk/LightChunkGetter;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/server/level/progress/ChunkProgressListener;Lnet/minecraft/world/level/entity/ChunkStatusUpdateListener;Ljava/util/function/Supplier;IZ)V",
            at = @At("TAIL")
    )
    private void clinker$chunkMapInit(
            ServerLevel level,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            DataFixer fixerUpper,
            StructureTemplateManager structureManager,
            Executor dispatcher,
            BlockableEventLoop mainThreadExecutor,
            LightChunkGetter lightChunk,
            ChunkGenerator generator,
            ChunkProgressListener progressListener,
            ChunkStatusUpdateListener chunkStatusListener,
            Supplier overworldDataStorage,
            int viewDistance, boolean sync,
            CallbackInfo ci) {
        clinker$metaChunkHandler = new MetaChunkHandler(level.getSeed());
    }

    @Override
    public MetaChunkHandler clinker$getMetaChunkHandler() {
        return clinker$metaChunkHandler;
    }
}
