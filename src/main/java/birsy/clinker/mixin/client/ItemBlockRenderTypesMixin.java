package birsy.clinker.mixin.client;

import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin {
    @Redirect(method = "setRenderLayer(Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/client/renderer/RenderType;)V",
              at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/base/Preconditions;checkArgument(ZLjava/lang/Object;)V"
              )
    )
    private static void clinker$dontCheckBuffersBecauseIRegisterItWithVeilYouDummy(boolean expression, Object errorMessage) {
        // Just Don't Do Any Error Checking.
    }
}
