package birsy.clinker.client.gui;

import birsy.clinker.common.world.alchemy.workstation.AlchemicalWorkstation;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class AlchemicalWorkstationScreen extends Screen {
    public BlockPos targetedBlock;         // block the camera is aiming at
    public BlockPos[] nextTargetedBlock;   // block the camera will be aiming at when a direction is pressed.
    public Vec3 pCurrentCamPos;  // position the camera was in last tick
    public Vec3 currentCamPos;   // position the camera is in
    public Vec3 targetCamPos;    // camera lerps to this position every tick
    public final AlchemicalWorkstation workstation;

    private float prevScreenTransition = 0.0F;
    public float screenTransition = 0.0F;
    public boolean isClosing = false;

    public AlchemicalWorkstationScreen(AlchemicalWorkstation workstation) {
        super(GameNarrator.NO_TITLE);
        this.workstation = workstation;
        this.nextTargetedBlock = new BlockPos[6];
        BlockPos initialPos = (BlockPos) workstation.containedBlocks.toArray()[0];
        this.targetedBlock = initialPos.offset(-1, -1, -1);
        Vec3 pos = new Vec3(initialPos.getX() - 1.5, initialPos.getY(), initialPos.getZ() - 1.5);
        this.currentCamPos = pos;
        this.pCurrentCamPos = pos;
        this.targetCamPos = pos;
    }

    @Override
    protected void init() {
        super.init();
        screenTransition = 0.0F;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            this.beginClosing();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void beginClosing() {
        this.isClosing = true;
    }

    public void tick() {
        this.pCurrentCamPos = this.currentCamPos;
        this.prevScreenTransition = this.screenTransition;

        this.currentCamPos = this.currentCamPos.lerp(this.targetCamPos, 0.05);
        MathUtils.EasingType easing = isClosing ? MathUtils.EasingType.easeInCubic : MathUtils.EasingType.easeInOutCubic;
        float diff = MathUtils.ease(this.screenTransition + ((isClosing ? -1 : 1) * 0.02F), easing);
        diff -= MathUtils.ease(this.screenTransition, easing);
        this.screenTransition = Mth.clamp(screenTransition + diff, 0, 1);

        if (this.isClosing && this.prevScreenTransition == 0) {
            this.onClose();
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        setCameraView(pPartialTick);

        Camera camera = minecraft.gameRenderer.getMainCamera();
    }

    public void setCameraView(float partialTick) {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 pos = getCamPos(partialTick);
        camera.setPosition(camera.getPosition().lerp(pos, getScreenTransition(partialTick)));
        //camera.setAnglesInternal();

    }

    public float getScreenTransition(float partialTicks) {
        return Mth.lerp(partialTicks, prevScreenTransition, screenTransition);
    }

    public Vec3 getCamPos(float partialTick) {
        return pCurrentCamPos.lerp(currentCamPos, partialTick);
    }
}
