package birsy.clinker.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Redirect(method = "setupColor",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"))
    private static boolean clinker$hasNightVision(LivingEntity instance, Holder<MobEffect> effect) {
        if (effect == MobEffects.NIGHT_VISION) return false;
        return instance.hasEffect(effect);
    }
}
