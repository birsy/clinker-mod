package birsy.clinker.mixin.client;

import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public final class ClientLevelMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientPacketListener;Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/Holder;IILjava/util/function/Supplier;Lnet/minecraft/client/renderer/LevelRenderer;ZJ)V", at = @At("TAIL"))
    private void clinker$init(ClientPacketListener p_205505_, ClientLevel.ClientLevelData p_205506_, ResourceKey p_205507_, Holder p_205508_, int p_205509_, int p_205510_, Supplier p_205511_, LevelRenderer p_205512_, boolean p_205513_, long p_205514_, CallbackInfo ci) {
        ClientLevel me = (ClientLevel)(Object)this;
        //InteractableAttachment.attachManagerToLevel(me, new ClientInteractableManager(me));
    }

    @Inject(method = "unload(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("TAIL"))
    public void clinker$unload(LevelChunk pChunk, CallbackInfo ci) {
        ClientLevel me = (ClientLevel)(Object)this;
        //InteractableLevelAttachment.getInteractableManagerForLevel(me).unloadChunk(pChunk);
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
    public void clinker$tick(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        WorkstationManager wManager = WorkstationManager.clientWorkstationManager;
        wManager.tick();
    }
}
