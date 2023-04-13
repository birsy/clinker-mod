
package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;Z)V", at = @At("TAIL"))
    public void init(MinecraftServer pServer, Executor pDispatcher, LevelStorageSource.LevelStorageAccess pLevelStorageAccess, ServerLevelData pServerLevelData, ResourceKey pDimensionKey, LevelStem pLevelStem, ChunkProgressListener pProgressListener, boolean pIsDebug, long pSeed, List pCustomSpawners, boolean pTickTime, CallbackInfo ci) {
        ServerLevel me = (ServerLevel)(Object)this;
        Clinker.LOGGER.info(me);
        InteractableManager.serverInteractableManagers.put(me, new InteractableManager(me));
    }

    @Inject(method = "unload(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("TAIL"))
    public void unload(LevelChunk pChunk, CallbackInfo ci) {
        InteractableManager manager = InteractableManager.serverInteractableManagers.get(((ServerLevel)(Object)this));
        manager.unloadChunk(pChunk.getPos());
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
    public void tick(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        InteractableManager manager = InteractableManager.serverInteractableManagers.get(((ServerLevel)(Object)this));
        manager.tick();
    }
}

