
package birsy.clinker.mixin.common;

import birsy.clinker.common.alchemy.workstation.WorkstationManager;
import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.worldfeature.MetaChunkMap;
import birsy.clinker.common.world.level.gen.system.worldfeature.MetaChunkMapHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow @Final private ServerChunkCache chunkSource;

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V",
            at = @At("TAIL"))
    public void clinker$initServerLevel(
            MinecraftServer pServer,
            Executor pDispatcher,
            LevelStorageSource.LevelStorageAccess pLevelStorageAccess,
            ServerLevelData pServerLevelData,
            ResourceKey pDimension,
            LevelStem pLevelStem,
            ChunkProgressListener pProgressListener,
            boolean pIsDebug,
            long pBiomeZoomSeed,
            List pCustomSpawners,
            boolean pTickTime,
            RandomSequences pRandomSequences,
            CallbackInfo ci) {
        ServerLevel me = (ServerLevel)(Object)this;
        //InteractableAttachment.attachManagerToLevel(me, new ServerInteractableManager(me));
        WorkstationManager manager = new WorkstationManager(me);
        WorkstationManager.managerByLevel.put(me, manager);
        WorkstationManager.managerByDimension.put(me.dimension(), manager);

        RandomState randomState = this.chunkSource.chunkMap.randomState();
        MetaChunkMap metaChunkMap = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap();
        // init biome source!
        if (this.chunkSource.getGenerator().getBiomeSource() instanceof OthershoreBiomeSource othershoreBiomeSource) {
            othershoreBiomeSource.initRandomState(this.chunkSource.chunkMap.randomState());
        }

    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
    public void clinker$tickServerLevel(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        //InteractableManager iManager = InteractableManager.serverInteractableManagers.get(((ServerLevel)(Object)this));
        //iManager.tick();
        WorkstationManager wManager = WorkstationManager.managerByLevel.get(((ServerLevel)(Object)this));
        wManager.tick();
    }
}


