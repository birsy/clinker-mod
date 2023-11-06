package birsy.clinker.mixin.client;

import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "addEntity(ILnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void addEntity(int pEntityId, Entity pEntityToSpawn, CallbackInfo ci) {
        if (this.minecraft.getEntityRenderDispatcher().getRenderer(pEntityToSpawn) instanceof InterpolatedEntityRenderer renderer) {
            renderer.createSkeleton((LivingEntity) pEntityToSpawn);
        }
    }

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
