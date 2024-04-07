package birsy.clinker.mixin.common;

import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getItem();

    @Inject(method = "setUnderwaterMovement()V", at = @At("HEAD"), cancellable = true)
    private void setUnderwaterMovement(CallbackInfo ci) {
        ItemEntity me = (ItemEntity)(Object)this;
        if (this.getItem().is(ClinkerTags.NOT_BUOYANT)) {
            Vec3 vec3 = me.getDeltaMovement();
            me.setDeltaMovement(vec3.x * 0.99F, vec3.y + (double)(vec3.y > -0.06F ? 5.0E-4F : 0.0F), vec3.z * 0.99F);
            ci.cancel();
        }
    }
}
