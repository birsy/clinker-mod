package birsy.clinker.mixin.client;

import birsy.clinker.client.ClinkerCursor;
import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.client.render.world.VolumetricRenderer;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        List<InterpolatedSkeletonParent> list = new ArrayList<>();
        for (Entity entity : this.level.entitiesForRendering()) {
            if (entity instanceof InterpolatedSkeletonParent animator) {
                if (animator.getSkeleton() != null) {
                    list.add(animator);
                }
            }
        }

        InterpolatedEntityRenderer.tick(list);
    }

    @Shadow public abstract void initOutline();

    @Shadow @Nullable private ClientLevel level;

    @Inject(method = "close()V", at = @At("TAIL"))
    private void close(CallbackInfo info) {
        VolumetricRenderer.close();
    }

    @Inject(method = "onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V", at = @At("TAIL"))
    private void onResourceManagerLoad(ResourceManager pResourceManager, CallbackInfo ci) throws IOException {
        VolumetricRenderer.init();
        ClinkerCursor.init();
    }

    @Inject(method = "resize(II)V", at = @At("TAIL"))
    private void resize(int pWidth, int pHeight, CallbackInfo ci) {
        VolumetricRenderer.resize(pWidth, pHeight);
    }

    @Inject(method = "setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V", at = @At("HEAD"))
    private void setupRender(Camera pCamera, Frustum pFrustrum, boolean pHasCapturedFrustrum, boolean pIsSpectator, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof AlchemicalWorkstationScreen screen) {
            screen.setCameraView(pCamera, null, Minecraft.getInstance().getPartialTick());
        }
    }

    @Inject(method = "renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V", at = @At("HEAD"))
    private void render(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof AlchemicalWorkstationScreen screen) {
            //screen.setCameraView(pCamera, pPoseStack, Minecraft.getInstance().getPartialTick());
        }
    }


}
