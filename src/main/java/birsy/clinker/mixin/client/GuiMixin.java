package birsy.clinker.mixin.client;

import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow @Final protected Minecraft minecraft;

    @Shadow protected int screenHeight;

    @Inject(method = "renderHotbar(FLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"))
    private void renderHotbarStart(float pPartialTick, PoseStack pPoseStack, CallbackInfo ci) {
        pPoseStack.pushPose();
        if (minecraft.screen instanceof AlchemicalWorkstationScreen screen) {
            pPoseStack.translate(0, this.screenHeight * screen.getScreenTransition(pPartialTick), 0);
        }
    }

    @Inject(method = "renderHotbar(FLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("TAIL"))
    private void renderHotbarEnd(float pPartialTick, PoseStack pPoseStack, CallbackInfo ci) {
        pPoseStack.popPose();
    }

    @Inject(method = "renderSlot(IIFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private void renderSlot(int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int p_168683_, CallbackInfo ci) {
        if (minecraft.screen instanceof AlchemicalWorkstationScreen screen) {
            RenderSystem.getModelViewStack().translate(0, this.screenHeight * screen.getScreenTransition(pPartialTick), 0);
        }
    }
}

