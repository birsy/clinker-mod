package birsy.clinker.mixin.client;

import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
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

    @Inject(method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"))
    private void renderHotbarStart(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo ci) {
        pGuiGraphics.pose().pushPose();
        if (minecraft.screen instanceof AlchemicalWorkstationScreen screen) {
            pGuiGraphics.pose().translate(0, this.screenHeight * screen.getScreenTransition(pPartialTick), 0);
        }
    }

    @Inject(method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V", at = @At("TAIL"))
    private void renderHotbarEnd(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo ci) {
        pGuiGraphics.pose().popPose();
    }

    @Inject(method = "renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IIFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private void renderSlot(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci) {
        if (minecraft.screen instanceof AlchemicalWorkstationScreen screen) {
            RenderSystem.getModelViewStack().translate(0, this.screenHeight * screen.getScreenTransition(pPartialTick), 0);
        }
    }
}

