package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.gen.chunk.LevelNoiseProvidable;
import com.mojang.datafixers.util.Either;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;

@Debug(export = true)
@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    @Inject(
            method = "register(Ljava/lang/String;Lnet/minecraft/world/level/chunk/ChunkStatus;ILjava/util/EnumSet;Lnet/minecraft/world/level/chunk/ChunkStatus$ChunkType;Lnet/minecraft/world/level/chunk/ChunkStatus$GenerationTask;)Lnet/minecraft/world/level/chunk/ChunkStatus;",
            at = @At("RETURN"), cancellable = true
    )
    private static void clinker$uploadWorldGenRegionToNoiseStage(String pKey, ChunkStatus pParent, int pTaskRange, EnumSet<Heightmap.Types> pHeightmaps, ChunkStatus.ChunkType pType, ChunkStatus.GenerationTask pGenerationTask, CallbackInfoReturnable<ChunkStatus> cir) {
        if (pKey.equals("noise")) {
            cir.setReturnValue(ChunkStatus.register(pKey, pParent, pTaskRange, false, pHeightmaps, pType,
                    (pStatus, pExecutor, pLevel, pChunkGenerator, pStructureTemplateManager, pLightEngine, pTask, pCache, pLoadingChunk) -> {
                        WorldGenRegion worldgenregion = new WorldGenRegion(pLevel, pCache, pStatus, 0);
                        if (pChunkGenerator instanceof LevelNoiseProvidable providable) providable.provideLevelAndSeed(worldgenregion, pLevel.getSeed());
                        return pChunkGenerator.fillFromNoise(
                                pExecutor,
                                Blender.of(worldgenregion),
                                pLevel.getChunkSource().randomState(),
                                pLevel.structureManager().forWorldGenRegion(worldgenregion),
                                pLoadingChunk
                            ).thenApply(chunk -> {
                                if (chunk instanceof ProtoChunk protochunk) {
                                    BelowZeroRetrogen belowzeroretrogen = protochunk.getBelowZeroRetrogen();
                                    if (belowzeroretrogen != null) {
                                        BelowZeroRetrogen.replaceOldBedrock(protochunk);
                                        if (belowzeroretrogen.hasBedrockHoles()) {
                                            belowzeroretrogen.applyBedrockMask(protochunk);
                                        }
                                    }
                                }
                                return Either.left(chunk);
                            });
                    },
                    ChunkStatus.PASSTHROUGH_LOAD_TASK));
            cir.cancel();
        }
    }
}
