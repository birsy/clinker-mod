package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.interactableOLD.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    ///@Inject(method = "playerLoadedChunk(Lnet/minecraft/server/level/ServerPlayer;Lorg/apache/commons/lang3/mutable/MutableObject;Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("TAIL"))
    public void playerLoadedChunk(ServerPlayer pPlayer, MutableObject<ClientboundLevelChunkWithLightPacket> pPacketCache, LevelChunk pChunk, CallbackInfo ci) {
        if (InteractableManager.serverInteractableManagers.get(pPlayer.level()) == null) {
            Clinker.LOGGER.warn("Uh oh! ServerLevel " + pPlayer.level().dimension().location() + " unregistered!");
        } else {
            InteractableManager.serverInteractableManagers.get(pPlayer.level()).loadChunkToPlayer(pChunk.getPos(), pPlayer);
        }
    }
}
