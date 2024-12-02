package birsy.clinker.mixin.client;

import birsy.clinker.client.ClinkerCursor;
import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import birsy.necromancer.SkeletonParent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "tick()V", at = @At("HEAD"))
    private void clinker$tick(CallbackInfo ci) {
        for (Entity entity : this.level.entitiesForRendering()) {
            if (entity instanceof SkeletonParent parent) {
                if (parent.getAnimator() != null) parent.getAnimator().tick((SkeletonParent<?, ?, ?>) parent);
            }
        }

    }

    @Shadow public abstract void initOutline();

    @Shadow @Nullable private ClientLevel level;


    @Inject(method = "onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V", at = @At("TAIL"))
    private void clinker$onResourceManagerLoad(ResourceManager pResourceManager, CallbackInfo ci) throws IOException {
        ClinkerCursor.init();
    }


    @Inject(method = "setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V", at = @At("HEAD"))
    private void clinker$setupRender(Camera pCamera, Frustum pFrustrum, boolean pHasCapturedFrustrum, boolean pIsSpectator, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof AlchemicalWorkstationScreen screen) {
            screen.setCameraView(pCamera, null, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
        }
    }


}
