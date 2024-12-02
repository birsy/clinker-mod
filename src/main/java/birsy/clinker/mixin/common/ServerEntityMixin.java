package birsy.clinker.mixin.common;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntityInitPacket;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntitySyncPacket;
import birsy.clinker.common.world.entity.rope.RopeEntity;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "addPairing(Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At("TAIL"))
    private void clinker$addPairing(ServerPlayer serverPlayer, CallbackInfo ci) {
        if (this.entity instanceof RopeEntity ropeEntity) {
            ClinkerPacketHandler.sendToClient(serverPlayer, new ClientboundRopeEntityInitPacket(ropeEntity));
        }
    }

    @Inject(method = "sendChanges()V",
            at = @At("TAIL"))
    private void clinker$sendChanges(CallbackInfo ci) {
        if (this.entity instanceof RopeEntity ropeEntity) {
            ClinkerPacketHandler.sendToClientsTrackingEntity(ropeEntity, new ClientboundRopeEntitySyncPacket(ropeEntity));
        }
    }
}
