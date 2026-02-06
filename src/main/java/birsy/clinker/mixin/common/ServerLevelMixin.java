
package birsy.clinker.mixin.common;

import birsy.clinker.common.alchemy.workstation.WorkstationManager;
import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunkMap;
import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunkMapHolder;
import birsy.clinker.core.registry.worldgen.ClinkerWorld;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    @Shadow @Final private ServerChunkCache chunkSource;

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V",
            at = @At("TAIL"))
    void clinker$initServerLevel(
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

    @Inject(method = "tick", at = @At("TAIL"))
    void clinker$tickServerLevel(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        //InteractableManager iManager = InteractableManager.serverInteractableManagers.get(((ServerLevel)(Object)this));
        //iManager.tick();
        WorkstationManager wManager = WorkstationManager.managerByLevel.get(((ServerLevel)(Object)this));
        wManager.tick();
    }

    @Inject(method = "advanceWeatherCycle", at = @At("HEAD"), cancellable = true)
    void clinker$advanceWeatherCycle(CallbackInfo ci) {
        if (this.dimension() == ClinkerWorld.OTHERSHORE) ci.cancel();
    }
}


