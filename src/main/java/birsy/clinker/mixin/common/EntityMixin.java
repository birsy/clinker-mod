package birsy.clinker.mixin.common;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Vec3 getLookAngle();
    @Shadow public abstract float getBbWidth();
    @Shadow public abstract Level level();

    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();

    @Shadow private Level level;
    @Unique boolean clinker$leftFootStepping = false;

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

    @Inject(method = "walkingStepSound", at = @At("TAIL"))
    private void clinker$stepEffects(BlockPos pos, BlockState state, CallbackInfo ci) {
        Vec3 facing = this.getLookAngle();
        double mult = this.getBbWidth() * 0.3 * (clinker$leftFootStepping ? -1 : 1);
        double particleX = this.getX() + -facing.z * mult,
               particleY = this.getY(),
               particleZ = this.getZ() + facing.x * mult;
        level.addParticle(ClinkerParticles.FOOTPRINT.get(), false, particleX, particleY, particleZ, facing.x, facing.z, 4.0F / 16.0F);
        clinker$leftFootStepping = !clinker$leftFootStepping;
    }
}
