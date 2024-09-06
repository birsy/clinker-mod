package birsy.clinker.mixin.common;

import birsy.clinker.common.world.entity.ColliderEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @ModifyVariable(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("STORE"), ordinal = 2)
    private boolean clinker$modifyCritRequirements(boolean crit, Entity pTarget, @Local(ordinal = 0) boolean flag) {
        Player me = (Player)(Object) this;
        boolean modifiedReq = flag
                && me.fallDistance > 0.0F
                && !me.onGround()
                && !me.onClimbable()
                && !me.isInWater()
                && !me.hasEffect(MobEffects.BLINDNESS)
                && !me.isPassenger()
                && (pTarget instanceof LivingEntity || pTarget instanceof ColliderEntity<?>);
        return crit || modifiedReq;
    }
}
