package birsy.clinker.mixin.client;

import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import birsy.clinker.client.render.world.VolumetricRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private Camera mainCamera;

    @Inject(method = "renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;prepareCullFrustum(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;Lcom/mojang/math/Matrix4f;)V"))
    private void renderLevel(float pPartialTicks, long pFinishTimeNano, PoseStack pMatrixStack, CallbackInfo ci) {
        if (minecraft.screen instanceof AlchemicalWorkstationScreen screen) {
            screen.setCameraView(mainCamera, pMatrixStack, minecraft.getPartialTick());
        }
    }


}
