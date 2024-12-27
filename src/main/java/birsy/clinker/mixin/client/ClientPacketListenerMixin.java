package birsy.clinker.mixin.client;

import birsy.clinker.client.render.world.gas.GasRenderer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public final class ClientPacketListenerMixin {
    @Inject(method = "applyLightData(IILnet/minecraft/network/protocol/game/ClientboundLightUpdatePacketData;)V",
            at = @At("TAIL"))
    private void clinker$applyLightData(int x, int z, ClientboundLightUpdatePacketData data, CallbackInfo ci) {
        GasRenderer.updateLight(x, z);
    }
}
