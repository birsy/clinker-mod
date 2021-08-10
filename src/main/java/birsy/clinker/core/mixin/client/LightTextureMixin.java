package birsy.clinker.core.mixin.client;

import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.multiplayer.ClientLevel;
import com.mojang.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    @Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/vector/Vector3f;lerp(Lnet/minecraft/util/math/vector/Vector3f;F)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void doOurLightMap(float partialTicks, CallbackInfo callbackInfo, ClientLevel clientworld, float f, float f1, float f3, float f2, Vector3f skyVector) {
        Clinker.LOGGER.info("poopy lighting mixing a A a A a");

        if (skyVector != null) {
            skyVector.lerp(new Vector3f(10, 0, 0), 1.0F);
        }
    }
}
