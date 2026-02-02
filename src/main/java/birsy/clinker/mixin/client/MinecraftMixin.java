package birsy.clinker.mixin.client;

import birsy.clinker.core.registry.worldgen.ClinkerWorld;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;
    @Inject(method = "getSituationalMusic",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/MusicManager;isPlayingMusic(Lnet/minecraft/sounds/Music;)Z", shift = At.Shift.BEFORE),
            cancellable = true
    )
    void clinker$overrideOthershoreMusic(CallbackInfoReturnable<Music> cir, @Local Holder<Biome> biomeHolder) {
        if (this.player.level().dimension() == ClinkerWorld.OTHERSHORE) 
            cir.setReturnValue(biomeHolder.value().getBackgroundMusic().orElse(Musics.GAME));
    }
}
