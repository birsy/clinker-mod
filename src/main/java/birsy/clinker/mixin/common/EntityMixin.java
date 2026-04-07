package birsy.clinker.mixin.common;

import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void clinker$collide(Vec3 pVec, CallbackInfoReturnable<Vec3> cir) {
        //Vec3 newVelocity = CollidableInteractable.collideWithEntities(cir.getReturnValue(), (Entity)(Object)this);
        //if (newVelocity != null) cir.setReturnValue(newVelocity);
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void clinker$invulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        EntityType<?> self = ((Entity) (Object) this).getType();
        if (source.is(ClinkerTags.DamageTypes.THORNY) && self.is(ClinkerTags.Entities.THORN_IMMUNE))
            cir.setReturnValue(true);
    }
}
