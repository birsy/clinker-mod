package birsy.clinker.mixin.client;

import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "addEntity(ILnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void addEntity(int pEntityId, Entity pEntityToSpawn, CallbackInfo ci) {
        if (this.minecraft.getEntityRenderDispatcher().getRenderer(pEntityToSpawn) instanceof InterpolatedEntityRenderer renderer) {
            renderer.createModel((LivingEntity) pEntityToSpawn);
        }
    }
}
