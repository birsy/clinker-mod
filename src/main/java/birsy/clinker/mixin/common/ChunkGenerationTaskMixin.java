package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.gen.metachunk.MetaChunkHandler;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunkMap;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.*;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ChunkGenerationTask.class)
public class ChunkGenerationTaskMixin {
    @Shadow @Final private GeneratingChunkMap chunkMap;

    @Shadow @Final private ChunkPos pos;

    @Inject(
            method = "runUntilWait",
            at = @At("HEAD")
    )
    private void clinker$runUntilWait(CallbackInfoReturnable<CompletableFuture<?>> cir) {
        // wait until all metachunks have completed generating.
        while (true) {
            if (!((MetaChunkMap) this.chunkMap).getMetaChunkHandler().shouldAwaitMetaChunkGen(this.pos)) break;
        }
    }
}
