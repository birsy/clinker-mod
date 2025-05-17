package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.gen.chunk.LevelNoiseProvidable;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunk;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunkHandler;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunkMap;
import birsy.clinker.common.world.level.gen.metachunk.feature.MetaChunkFeature;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Debug(export = true)
@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTasksMixin {
    @Inject(
            method = "generateStructureStarts(Lnet/minecraft/world/level/chunk/status/WorldGenContext;Lnet/minecraft/world/level/chunk/status/ChunkStep;Lnet/minecraft/util/StaticCache2D;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD")
    )
    private static void clinker$generateStructureStarts(WorldGenContext worldGenContext, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        ServerLevel serverlevel = worldGenContext.level();
        ((MetaChunkMap) serverlevel.getChunkSource().chunkMap).clinker$getMetaChunkHandler().generateMetaChunks(chunk.getPos());
    }

    @Inject(
            method = "generateNoise(Lnet/minecraft/world/level/chunk/status/WorldGenContext;Lnet/minecraft/world/level/chunk/status/ChunkStep;Lnet/minecraft/util/StaticCache2D;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("RETURN")
    )
    private static void clinker$uploadWorldGenRegionToNoiseStage(WorldGenContext worldGenContext, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Local ServerLevel serverlevel, @Local WorldGenRegion worldgenregion) {
        ChunkGenerator generator = worldGenContext.generator();
        if (generator instanceof LevelNoiseProvidable providable) providable.provideLevelAndSeed(serverlevel, serverlevel.getSeed());

    }

    @Inject(
            method = "generateFeatures(Lnet/minecraft/world/level/chunk/status/WorldGenContext;Lnet/minecraft/world/level/chunk/status/ChunkStep;Lnet/minecraft/util/StaticCache2D;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("RETURN")
    )
    private static void clinker$generateFeatures(WorldGenContext worldGenContext, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Local ServerLevel serverlevel, @Local WorldGenRegion worldgenregion) {
        ChunkGenerator generator = worldGenContext.generator();
        if (generator instanceof LevelNoiseProvidable providable) providable.provideLevelAndSeed(serverlevel, serverlevel.getSeed());

        ((MetaChunkMap) serverlevel.getChunkSource().chunkMap).clinker$getMetaChunkHandler().realizeMetaChunkFeatures(chunk);
    }
}
