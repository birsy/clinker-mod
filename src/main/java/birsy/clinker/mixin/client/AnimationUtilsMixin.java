package birsy.clinker.mixin.client;

import birsy.clinker.common.world.item.AlchemistsCrossbowItem;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnimationUtils.class)
public final class AnimationUtilsMixin {
    @Redirect(
            method = "animateCrossbowCharge",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksUsingItem()I"))
    private static int clinker$animateCrossbowChargeUseItem(LivingEntity livingEntity) {
        ItemStack currentItem = livingEntity.getUseItem();
        if (currentItem.getItem() instanceof AlchemistsCrossbowItem && AlchemistsCrossbowItem.hasRepeater(currentItem)) {
            return livingEntity.getTicksUsingItem() % AlchemistsCrossbowItem.ITEM_LOAD_TIME;
        }
        return livingEntity.getTicksUsingItem();
    }

}
