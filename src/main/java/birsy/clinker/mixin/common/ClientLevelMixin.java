package birsy.clinker.mixin.common;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "unload(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("TAIL"))
    public void unload(LevelChunk pChunk, CallbackInfo ci) {
        InteractableManager manager = InteractableManager.clientInteractableManager;
        manager.unloadChunk(pChunk.getPos());
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
    public void tick(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        InteractableManager iManager = InteractableManager.clientInteractableManager;
        iManager.tick();
        WorkstationManager wManager = WorkstationManager.clientWorkstationManager;
        wManager.tick();
    }
}
