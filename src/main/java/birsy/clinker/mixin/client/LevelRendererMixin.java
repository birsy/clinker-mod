package birsy.clinker.mixin.client;

import birsy.clinker.client.render.world.InteractableRenderer;
import birsy.clinker.client.render.world.VolumetricRenderer;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "close()V", at = @At("TAIL"))
    private void close(CallbackInfo info) {
        VolumetricRenderer.close();
    }

    @Inject(method = "onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V", at = @At("TAIL"))
    private void onResourceManagerLoad(ResourceManager pResourceManager, CallbackInfo ci) throws IOException {
        VolumetricRenderer.init();
    }

    @Inject(method = "resize(II)V", at = @At("TAIL"))
    private void resize(int pWidth, int pHeight, CallbackInfo ci) {
        VolumetricRenderer.resize(pWidth, pHeight);
    }
}
