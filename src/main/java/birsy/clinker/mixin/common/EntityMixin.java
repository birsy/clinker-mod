package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.interactable.CollidableInteractable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void collide(Vec3 pVec, CallbackInfoReturnable<Vec3> cir) {
        Vec3 newVelocity = CollidableInteractable.collideWithEntities(cir.getReturnValue(), (Entity)(Object)this);
        if (newVelocity != null) cir.setReturnValue(newVelocity);
    }
}
