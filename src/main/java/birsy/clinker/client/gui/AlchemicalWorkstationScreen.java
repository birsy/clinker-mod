package birsy.clinker.client.gui;

import birsy.clinker.client.ClinkerCursor;
import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.alchemy.workstation.WorkstationPhysicsObject;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.JomlConversions;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import java.lang.Math;
import java.util.Optional;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class AlchemicalWorkstationScreen extends GuiElementParent {
    public BlockPos targetedBlock;         // block the camera is aiming at
    public BlockPos[] nextTargetedBlock;   // block the camera will be aiming at when a direction is pressed.

    public Vec3 pCurrentCamPos;  // position the camera was in last tick
    public Vec3 currentCamPos;   // position the camera is in
    public Vec3 targetCamPos;    // camera lerps to this position every tick
    public Quaterniond camRotation = new Quaterniond(), pCamRotation = new Quaterniond(), targetCamRotation = new Quaterniond();

    public final Workstation workstation;

    private float prevScreenTransition = 0.0F;
    public float screenTransition = 0.0F;
    public boolean isClosing = false;

    private InventoryElement inventoryElement;
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/gui/workstation.png");
    private static final int RES_X = 128;
    private static final int RES_Y = 256;

    private float cameraSpeed = 0.1F;
    private boolean movingRight;
    private boolean movingLeft;

    private ClinkerCursor.CursorState cursorState;

    protected float itemOffsetX;
    protected float itemOffsetY;

    WorkstationPhysicsObject hoveredObject;

    public AlchemicalWorkstationScreen(Workstation workstation) {
        super(GameNarrator.NO_TITLE);
        this.workstation = workstation;
        this.nextTargetedBlock = new BlockPos[6];
        BlockPos initialPos = (BlockPos) workstation.containedBlocks.blocks.toArray()[0];
        this.targetedBlock = initialPos.offset(-1, -1, -1);

        this.workstation.initializeCameraPosition(Minecraft.getInstance().cameraEntity.position());

        //Vec3 pos = new Vec3(initialPos.getX() - 1.5, initialPos.getY(), initialPos.getZ() - 1.5);
        this.currentCamPos = this.workstation.camera.position;
        this.pCurrentCamPos = this.workstation.camera.position;
        this.targetCamPos = this.workstation.camera.position;

        this.screenTransition = 0;
        GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void createGuiElements() {
        InventoryElement iElement = new InventoryElement(this, -105, 0);
        this.roots.add(iElement);

        Inventory inventory = Minecraft.getInstance().player.getInventory();
        int index = 9;
        for (int inventoryY = 0; inventoryY < 3; inventoryY++) {
            for (int inventoryX = 0; inventoryX < 9; inventoryX++) {
                ItemSlotElement slot = new ItemSlotElement(this, 16 + (inventoryY * 20), 11 + (inventoryX * 20), iElement, inventory, index);
                index++;
            }
        }


        index = 0;
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            ItemSlotElement slot = new ItemSlotElement(this, 79, 11 + (hotbar * 20), iElement, inventory, index);
            index++;
        }

        InventoryKnobElement knob = new InventoryKnobElement(this, 101, 93, iElement);

        InventoryDragElement dragBar1 = new InventoryDragElement(this, iElement, 0, 0);
        InventoryDragElement dragBar2 = new InventoryDragElement(this, iElement, 0, 187);

        this.inventoryElement = iElement;
    }

    @Override
    protected void initializeGuiElements() {
        Vec2 middle = GuiHelper.toGuiSpace(this, this.minecraft.getWindow().getWidth() * 0.5F, this.minecraft.getWindow().getHeight() * 0.5F);
        inventoryElement.y = middle.y - (inventoryElement.height * 0.5F);
        inventoryElement.pY = inventoryElement.y;
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
            if (this.isClosing) {
                this.onClose();
                return true;
            }
            this.beginClosing();
            return true;
        }

        boolean hasMoved = false;
        if (pKeyCode == this.minecraft.options.keyLeft.getKey().getValue()) {
            this.movingLeft = true;
            hasMoved = true;
        }
        if (pKeyCode == this.minecraft.options.keyRight.getKey().getValue()) {
            this.movingRight = true;
            hasMoved = true;
        }

        if (hasMoved) {
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        boolean hasStoppedMoving = false;
        if (pKeyCode == this.minecraft.options.keyLeft.getKey().getValue()) {
            this.movingLeft = false;
            hasStoppedMoving = true;
        }
        if (pKeyCode == this.minecraft.options.keyRight.getKey().getValue()) {
            this.movingRight = false;
            hasStoppedMoving = true;
        }

        if (hasStoppedMoving) {
            return true;
        }

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    public void beginClosing() {
        this.isClosing = true;
    }

    @Override
    public void onClose() {
        GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        super.onClose();
    }

    public void tick() {
        super.tick();
        this.prevScreenTransition = this.screenTransition;
        this.screenTransition = Mth.clamp(screenTransition + ((isClosing ? -1 : 1) * 0.05F), 0, 1);

        this.pCurrentCamPos = this.currentCamPos.scale(1);
        this.pCamRotation = new Quaterniond(this.camRotation);

        if (!this.isClosing) {
            this.targetCamPos = this.workstation.camera.position;
            this.targetCamRotation = new Quaterniond().lookAlong(JomlConversions.toJOML(workstation.camera.direction.scale(-1)), JomlConversions.toJOML(new Vec3(0, 1, 0)));
        }

        this.currentCamPos = this.currentCamPos.lerp(this.targetCamPos, 0.15);
        this.camRotation = this.camRotation.slerp(this.targetCamRotation, 0.15);

        if (this.isClosing && this.prevScreenTransition == 0) {
            this.onClose();
        }
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        this.updateHoveredObject(pMouseX, pMouseY);
        super.mouseMoved(pMouseX, pMouseY);
    }

    public void updateHoveredObject(double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 rayOrigin = workstation.camera.position;
        Vec3 rayDirection = minecraft.gameRenderer.getMainCamera().getNearPlane().getPointOnPlane((float) (mouseX / minecraft.screen.width), (float) (mouseY / minecraft.screen.height));
        rayDirection = rayOrigin.add(rayDirection);

        // todo: make this not shit : )
        for (WorkstationPhysicsObject object : workstation.environment.objects) {
            Optional<Vec3> raycast = object.collider.boundingBox.clip(rayOrigin, rayDirection.scale(4));
            if (raycast.isPresent()) {
                this.setHoveredObject(object);
                Clinker.LOGGER.info("got");
                return;
            }
        }
        this.setHoveredObject(null);
    }

    public void setHoveredObject(WorkstationPhysicsObject object) {
        if (this.hoveredObject != null) {
            this.hoveredObject.isHovered = false;
        }
        if (object != null) object.isHovered = true;
        this.hoveredObject = object;
    }

    @Override
    protected void updateHoverState(int pMouseX, int pMouseY, float pPartialTick) {
        GuiElement newHoveredElement = GuiHelper.findHoveredElement(leafNodes, pMouseX, pMouseY, pPartialTick);
        if (newHoveredElement != hoveredElement) {
            if (this.hoveredElement != null) hoveredElement.hovered = false;

            hoveredElement = newHoveredElement;

            if (hoveredElement != null) {
                hoveredElement.hovered = true;
                hoveredElement.onHover(pMouseX, pMouseY);
            }
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.hoveredObject != null) {
            Camera camera = minecraft.gameRenderer.getMainCamera();
            Vec3 push = new Vec3(camera.getLeftVector()).scale(pDragX);
            push = push.add(new Vec3(camera.getUpVector()).scale(pDragY));
            this.hoveredObject.position = this.hoveredObject.position.add(push);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.cursorState = ClinkerCursor.CursorState.GRAB;
        if (hoveredElement != null) {
            if (hoveredElement == this.inventoryElement) {

            }
            hoveredElement.onClick((float) pMouseX, (float) pMouseY, pButton);
            clickedElement = hoveredElement;
            clickedElement.clicked = true;
            clickedElement.button = pButton;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.cursorState = ClinkerCursor.CursorState.IDLE;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.movingLeft) this.workstation.camera.lineProgress -= cameraSpeed * Minecraft.getInstance().getDeltaFrameTime();
        if (this.movingRight) this.workstation.camera.lineProgress += cameraSpeed * Minecraft.getInstance().getDeltaFrameTime();
        this.workstation.camera.update();

        Vec2 middle = GuiHelper.toGuiSpace(this, this.width, this.height);
        inventoryElement.y = middle.y - (inventoryElement.height * 0.5F);
        inventoryElement.pY = inventoryElement.y;
        inventoryElement.setScreenTransition(this.getScreenTransition(pPartialTick));

        super.render(graphics, pMouseX, pMouseY, pPartialTick);

        renderMouse(graphics.pose(), pPartialTick);
    }
    
    private void renderMouse(PoseStack pPoseStack, float pPartialTick) {
        Vec2 mousePos = GuiHelper.toGuiSpace(this, (float) Minecraft.getInstance().mouseHandler.xpos(), (float) Minecraft.getInstance().mouseHandler.ypos());

        if (this.clickedElement instanceof ItemSlotElement element) {
            if (!element.getItem().isEmpty()) {
                RenderSystem.setShader(ClinkerShaders::getPositionColorTextureUnclampedShader);
                RenderSystem.setShaderTexture(0, TEXTURE);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

                pPoseStack.pushPose();
                float offsetX = mousePos.x - this.itemOffsetX, offsetY = mousePos.y - this.itemOffsetY;
                pPoseStack.translate(offsetX, offsetY, 0);

                float ix = this.inventoryElement.getScreenX(pPartialTick) - offsetX, iy = this.inventoryElement.getScreenY(pPartialTick) - offsetY;
                element.renderShadow(bufferbuilder, pPoseStack.last().pose(), pPartialTick, ix, iy + 7, (ix + inventoryElement.width - 6), (iy + inventoryElement.height - 7));
                pPoseStack.translate(0, 0, 200);
                element.renderItem(pPoseStack, pPartialTick);

                pPoseStack.popPose();
            }
        }
        RenderSystem.disableDepthTest();

        //todo: do this the proper GLFW way
        if (cursorState != ClinkerCursor.CursorState.GRAB) {
            if (hoveredElement != null) {
                cursorState = ClinkerCursor.CursorState.HOVER;
            } else {
                cursorState = ClinkerCursor.CursorState.IDLE;
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        GuiHelper.blitOffset = -100;
        GuiHelper.blit(pPoseStack, mousePos.x, mousePos.y - 3, cursorState.ordinal() * 16, 0, 16, 16, RES_X, RES_Y);
    }

    public void setCameraView(Camera camera, PoseStack matrixStack, float partialTick) {
        Vec3 pos = getCamPos(partialTick);
        camera.setPosition(camera.getPosition().lerp(pos, getScreenTransition(partialTick)));

        //Quaterniond workshopCameraRotation = this.pCamRotation.slerp(this.camRotation, partialTick, new Quaterniond());
        //Quaterniond quaterniond = new Quaterniond(camera.rotation()).slerp(workshopCameraRotation, getScreenTransition(partialTick));
        //GuiHelper.setCameraRotation(camera, this.camRotation);

        if (matrixStack != null) {
           // matrixStack.mulPose(new Quaternionf(new AxisAngle4f(Mth.lerp(getScreenTransition(partialTick), 0, -25), new Vector3f(1, 0, 0))));
           // matrixStack.mulPose(new Quaternionf(quaterniond));
        }
    }

    @SubscribeEvent
    public static void resetCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (Minecraft.getInstance().screen instanceof AlchemicalWorkstationScreen screen) {
            Quaterniond workshopCameraRotation = screen.pCamRotation.slerp(screen.camRotation, event.getPartialTick(), new Quaterniond());
            Vector3d angles = workshopCameraRotation.getEulerAnglesYXZ(new Vector3d());

            float transition = screen.getScreenTransition((float) event.getPartialTick());
            event.setPitch(Mth.rotLerp(transition, event.getPitch(), 20.0F));
            event.setRoll(Mth.rotLerp(transition, event.getRoll(), (float) Math.toDegrees(angles.z)));
            event.setYaw(Mth.rotLerp(transition, event.getYaw(), (float) Math.toDegrees(angles.y)));
        }
    }

    public float getScreenTransition(float partialTicks) {
        return MathUtils.ease(Mth.lerp(partialTicks, prevScreenTransition, screenTransition), isClosing ? MathUtils.EasingType.easeInCubic : MathUtils.EasingType.easeInOutCubic);
    }

    public Vec3 getCamPos(float partialTick) {
        return pCurrentCamPos.lerp(currentCamPos, partialTick);
    }

    public static class InventoryElement extends GuiElement<GuiElement, AlchemicalWorkstationScreen> {
        float acceleration = 0;
        boolean cancelVelocity = false;
        boolean flipVelocity = false;
        float velocity = 0;
        private float screenTrans;

        public final float minX;
        public final float maxX;

        public Vec3 lightColor;
        private Vec3 desiredLightColor;

        protected InventoryElement(AlchemicalWorkstationScreen screen, float x, float y) {
            super(screen, x, y, 105, 197, false);
            this.uOffset = 0;
            this.vOffset = 17;
            this.setTexture(TEXTURE, 128, 256);
            minX = -this.width;
            maxX = -8;

            this.lightColor = new Vec3(1, 1, 1);
            this.desiredLightColor = new Vec3(1, 1, 1);
            updateLightColor(true);
        }

        private void updateLightColor(boolean updateCurrent) {
            Entity entity = Minecraft.getInstance().cameraEntity;
            int blockLight = Math.max(entity.level().getBrightness(LightLayer.BLOCK, entity.blockPosition()), 11);
            int skyLight = entity.level().getBrightness(LightLayer.SKY, entity.blockPosition());

            int decimalColor = Minecraft.getInstance().gameRenderer.lightTexture().lightPixels.getPixelRGBA(blockLight, skyLight);
            Vec3 color = MathUtils.convertColorToVec3(decimalColor);

            if (updateCurrent) lightColor = color;
            desiredLightColor = color;
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            pPoseStack.pushPose();
            if (Minecraft.getInstance().screen instanceof AlchemicalWorkstationScreen screen) screenTrans = 1 - screen.getScreenTransition(pPartialTick);
            pPoseStack.translate(screenTrans * -128, 0, 0);
            this.lightColor = this.lightColor.lerp(desiredLightColor, 0.1F);
            super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            pPoseStack.popPose();
        }

        public float[] getLightColor() {
            Vec3 color = this.lightColor.lerp(new Vec3(1, 1, 1), 0.3F);
            return new float[]{(float) color.x(), (float) color.y(), (float) color.z()};
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            float[] lightColor = getLightColor();
            RenderSystem.setShaderColor(lightColor[0], lightColor[1], lightColor[2], 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            GuiHelper.blit(pPoseStack, 0, 0, this.uOffset, this.vOffset, this.width, this.height, this.textureWidth, this.textureHeight);
        }

        @Override
        public void tick(float mouseX, float mouseY) {
            float velocity = x - pX;
            this.velocity = velocity;

            if (this.cancelVelocity) velocity = 0;
            this.cancelVelocity = false;
            if (this.flipVelocity) velocity *= -0.5;
            this.flipVelocity = false;

            super.tick(mouseX, mouseY);
            velocity *= 0.8;
            velocity += acceleration;
            this.x += velocity;

            if (this.x > maxX) {
                this.x = maxX;
                flipVelocity = true;
            } else if (this.x < minX) {
                this.x = minX;
                cancelVelocity = true;
            }

            acceleration = 0;

            updateLightColor(false);
        }

        public void accelerate(float x) {
            if (this.x >= maxX && x > 0) return;
            acceleration += x;
        }

        public void setX(float x) {
            this.x = Mth.clamp(x, minX, maxX);
        }

        public void setScreenTransition(float screenTransition) {
            this.screenTrans = 1 - screenTransition;
        }
    }

    public static class InventoryDragElement extends GuiElement<InventoryElement, AlchemicalWorkstationScreen> {
        protected InventoryDragElement(AlchemicalWorkstationScreen screen, InventoryElement parent, float x, float y) {
            super(screen, parent, x, y, 105, 10, true);
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {}

        @Override
        public void onDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
            super.onDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
            this.parent.accelerate((float) pDragX);
        }
    }


    public static class InventoryKnobElement extends GuiElement<InventoryElement, AlchemicalWorkstationScreen> {
        float xOffset = 0, pXOffset = 0;

        protected InventoryKnobElement(AlchemicalWorkstationScreen screen, float x, float y, InventoryElement parent) {
            super(screen, parent, x, y, 16, 16, true);
            this.uOffset = 112;
            this.vOffset = 0;
            this.setTexture(TEXTURE, 128, 256);
        }

        @Override
        public void tick(float mouseX, float mouseY) {
            super.tick(mouseX, mouseY);
            float velocity = xOffset - pXOffset;
            this.pXOffset = xOffset;
            this.xOffset += velocity * 0.9F;

            float target;
            if (this.hovered) {
                target = 2;
            } else {
                target = 0;
            }

            if (this.clicked) {
                this.parent.setX(mouseX + this.parent.minX);
            }

            xOffset += (target - xOffset) * 0.2F;

            if (this.parent.x > -50) {
                this.uOffset = 112;
            } else {
                this.uOffset = 64;
            }
        }

        @Override
        public void onDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
            super.onDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
            //this.parent.setX((float) pMouseX + this.parent.minX);
        }

        private float getXOffset(float partialTick) {
            return Mth.lerp(partialTick, pXOffset, xOffset);
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            pPoseStack.translate(this.getXOffset(pPartialTick), 0, 0);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            float[] lightColor = this.parent.getLightColor();
            RenderSystem.setShaderColor(lightColor[0], lightColor[1], lightColor[2], 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            GuiHelper.blit(pPoseStack, 0, 0, this.uOffset, this.vOffset, this.width, this.height, this.textureWidth, this.textureHeight);
        }
    }

    public static class ItemSlotElement extends GuiElement<InventoryElement, AlchemicalWorkstationScreen> {
        protected final Inventory inventory;
        protected final int index;
        private float wiggle = 0, pWiggle = 0;
        private float wiggleAmount = 0.2F;
        private float hoverTicks = 0;

        private float parentVelocity = 0, pParentVelocity = 0;

        float itemX = 0, itemY = 0;

        protected ItemSlotElement(AlchemicalWorkstationScreen screen, float x, float y, InventoryElement parent, Inventory inventory, int index) {
            super(screen, parent, x, y, 16, 16, true);
            this.inventory = inventory;
            this.index = index;
            this.uOffset = 80;
            this.vOffset = 0;
            this.setTexture(TEXTURE, 128, 256);
        }

        @Override
        public void onHover(float mouseX, float mouseY) {
            super.onHover(mouseX, mouseY);
            if (!this.clicked) hoverTicks = 0;
        }

        @Override
        public void tick(float mouseX, float mouseY) {
            super.tick(mouseX, mouseY);
            this.pParentVelocity = this.parentVelocity;
            this.parentVelocity = this.parent.velocity;
            //Clinker.LOGGER.info(parentVelocity);
            this.pWiggle = wiggle;

            this.wiggleAmount = 0.08F;
            if (this.hovered || this.clicked) {
                this.wiggle = Mth.clamp(wiggle + wiggleAmount, 0, 1);
                hoverTicks++;
            } else {
                this.wiggle = Mth.clamp(wiggle - wiggleAmount, 0, 1);
            }
        }

        private float getWiggle(float partialTick) {
            return MathUtils.ease(Mth.lerp(partialTick, wiggle, pWiggle), MathUtils.EasingType.easeOutBack);
        }

        private float getParentVelocity(float partialTick) {
            return Mth.lerp(partialTick, pParentVelocity, parentVelocity);
        }

        @Override
        public void onClick(float mouseX, float mouseY, int pButton) {
            super.onClick(mouseX, mouseY, pButton);
            this.screen.itemOffsetX = mouseX - this.getScreenX(Minecraft.getInstance().getPartialTick());
            this.screen.itemOffsetY = mouseY - this.getScreenY(Minecraft.getInstance().getPartialTick());
        }

        @Override
        public void onRelease(float mouseX, float mouseY, int pButton) {
            super.onRelease(mouseX, mouseY, pButton);
            float partialTick = Minecraft.getInstance().getPartialTick();

            //parent isnt marked as interactable, so an extra check is needed to make sure you don't dump stuff
            boolean isHoveringParent = (mouseX > parent.getScreenX(partialTick) && mouseX < parent.getScreenX(partialTick) + parent.width &&
                                        mouseY > parent.getScreenY(partialTick) && mouseY < parent.getScreenY(partialTick) + parent.height);

            // TODO : code for handling bundles, stacks of objects. increase range of selecting other slots slightly to make it Feel Better(tm)
            if (this.screen.hoveredElement instanceof ItemSlotElement element) {
                ItemStack swapStack = element.inventory.getItem(element.index).copy();
                element.inventory.setItem(element.index, this.inventory.getItem(index).copy());
                this.inventory.setItem(this.index, swapStack);

                element.hoverTicks = this.hoverTicks;
                element.wiggle = this.wiggle;
                element.pWiggle = this.wiggle;

                element.itemX = (mouseX - element.screen.itemOffsetX) - element.getScreenX(partialTick);
                element.itemY = (mouseY - element.screen.itemOffsetY) - element.getScreenY(partialTick);
                this.itemX = element.getScreenX(partialTick) - this.getScreenX(partialTick);
                this.itemY = element.getScreenY(partialTick) - this.getScreenY(partialTick);

            } else if (this.screen.hoveredElement == null && !isHoveringParent) {
                this.inventory.setItem(this.index, ItemStack.EMPTY);
                Vec3 position = this.screen.workstation.camera.position;
                position = new Vec3(Math.round(position.x()), Math.round(position.y()), Math.round(position.z()));
                position = position.add(-0.5, -0.5, -2.00 - (1.0F / 16.0F));
                WorkstationPhysicsObject object = new WorkstationPhysicsObject(position, 1.0F,
                        3.0F / 16.0F, 3.0F / 16.0F, 3.0F / 16.0F);
                this.screen.workstation.environment.addObject(object);
            } else {
                this.itemX = (mouseX - this.screen.itemOffsetX) - this.getScreenX(partialTick);
                this.itemY = (mouseY - this.screen.itemOffsetY) - this.getScreenY(partialTick);
            }
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

            Matrix4f pMatrix = pPoseStack.last().pose();

            RenderSystem.setShader(ClinkerShaders::getPositionColorTextureUnclampedShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

            //draw selection highlight
            renderSelection(bufferbuilder, pMatrix, pPartialTick, pMouseX, pMouseY);
            
            //draw item
            if (!this.clicked && !this.getItem().isEmpty()) {
                pPoseStack.pushPose();
                pPoseStack.translate(this.itemX, this.itemY, 0);

                float ix = - this.x - this.itemX,
                      iy = - this.y - this.itemY;

                renderShadow(bufferbuilder, pPoseStack.last().pose(), pPartialTick, ix, iy + 7, (ix + parent.width - 6), (iy + parent.height - 7));
                renderItem(pPoseStack, pPartialTick);
                pPoseStack.popPose();
            }

            this.itemX = Mth.lerp(0.08F, this.itemX, 0);
            this.itemY = Mth.lerp(0.08F, this.itemY, 0);
        }

        protected void renderShadow(BufferBuilder bufferbuilder, Matrix4f pMatrix, float pPartialTick) {
            renderShadow(bufferbuilder, pMatrix, pPartialTick, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        }

        protected void renderShadow(BufferBuilder bufferbuilder, Matrix4f pMatrix, float pPartialTick, float x1, float y1, float x2, float y2) {
            float alpha = 0.3F;
            float shadowOffset = 3F;
            float awfulRandom = 0;
            float velocityOffset = Mth.clamp(-getParentVelocity(pPartialTick) * awfulRandom, -4, 4);

            float pMinU = (this.uOffset + 16) / (float)this.textureWidth;
            float pMinV = this.vOffset / (float)this.textureHeight;
            float pMaxU = pMinU + (this.width / (float)this.textureWidth);
            float pMaxV = pMinV + (this.height / (float)this.textureHeight);

            float xStart = 0 + velocityOffset, xEnd = this.width + velocityOffset;
            float xStartClamped = Mth.clamp(xStart, x1, x2), xEndClamped = Mth.clamp(xEnd, x1, x2);
            float yStart = this.height + shadowOffset, yEnd = shadowOffset;
            float yStartClamped = Mth.clamp(yStart, y1, y2), yEndClamped = Mth.clamp(yEnd, y1, y2);

            float uMinOffset = xStart - xStartClamped, uMaxOffset = xEnd - xEndClamped;
            float vMinOffset = yStart - yStartClamped, vMaxOffset = yEnd - yEndClamped;
            pMinU -= uMinOffset / (float)this.textureWidth;
            pMinV -= vMaxOffset / (float)this.textureHeight;
            pMaxU -= uMaxOffset / (float)this.textureWidth;
            pMaxV -= vMinOffset / (float)this.textureHeight;

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex(pMatrix, xStartClamped, yStartClamped, this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMaxV).endVertex();
            bufferbuilder.vertex(pMatrix, xEndClamped,   yStartClamped, this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMaxV).endVertex();
            bufferbuilder.vertex(pMatrix, xEndClamped,   yEndClamped,   this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMinV).endVertex();
            bufferbuilder.vertex(pMatrix, xStartClamped, yEndClamped,   this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMinV).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
        }

        protected void renderSelection(BufferBuilder bufferbuilder, Matrix4f pMatrix, float pPartialTick, float pMouseX, float pMouseY) {
            float xDist = this.getScreenX(pPartialTick) - (pMouseX - 8);
            float yDist = this.getScreenY(pPartialTick) - (pMouseY - 4);
            float distToMouse = Mth.sqrt(xDist * xDist + yDist * yDist);
            float alpha = this.hovered ? 0.2F : Mth.clamp(32.0F / distToMouse, 0, 1) * 0.1F;

            float pMinU = this.uOffset / (float)this.textureWidth;
            float pMinV = this.vOffset / (float)this.textureHeight;
            float pMaxU = pMinU + (this.width / (float)this.textureWidth);
            float pMaxV = pMinV + (this.height / (float)this.textureHeight);

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex(pMatrix, 0, this.height, this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMaxV).endVertex();
            bufferbuilder.vertex(pMatrix, this.width, this.height, this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMaxV).endVertex();
            bufferbuilder.vertex(pMatrix, this.width, 0, this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMinV).endVertex();
            bufferbuilder.vertex(pMatrix, 0, 0, this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMinV).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
        }

        protected void renderItem(PoseStack pPoseStack, float pPartialTick) {
            float awfulRandom = 0;
            float velocityOffset = Mth.clamp(-getParentVelocity(pPartialTick) * awfulRandom, -4, 4);

            ItemStack stack = this.getItem();
            float rotation = Mth.cos((this.clientTicks + pPartialTick) * 0.01F + this.id * 20) * 15;
            rotation += Mth.sin((this.hoverTicks + pPartialTick) * 0.05F) * 45 * this.getWiggle(pPartialTick);
            pPoseStack.pushPose();
            pPoseStack.translate(velocityOffset, -3 * this.getWiggle(pPartialTick), 0);
            pPoseStack.translate(0, 0, 100.0F + 100);
            pPoseStack.translate(8.0D, 8.0D, 8.0D);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            pPoseStack.translate(0.0D, 4.0D, 0.0D);
            pPoseStack.mulPose(Axis.ZP.rotation(Mth.clamp(-getParentVelocity(pPartialTick) * Mth.lerp(0.5F, awfulRandom, 1), -4, 4) * 0.05F));
            pPoseStack.translate(0.0D, -4.0D, 0.0D);

            GuiHelper.tryRenderGuiItem(Minecraft.getInstance().getItemRenderer(), stack, pPoseStack, 1.0F);
            pPoseStack.popPose();
        }

        public ItemStack getItem() {
            return inventory.getItem(index);
        }
    }
}
