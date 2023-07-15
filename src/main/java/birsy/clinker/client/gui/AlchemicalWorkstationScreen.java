package birsy.clinker.client.gui;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.common.world.alchemy.workstation.AlchemicalWorkstation;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class AlchemicalWorkstationScreen extends GuiElementParent {
    public BlockPos targetedBlock;         // block the camera is aiming at
    public BlockPos[] nextTargetedBlock;   // block the camera will be aiming at when a direction is pressed.
    public Vec3 pCurrentCamPos;  // position the camera was in last tick
    public Vec3 currentCamPos;   // position the camera is in
    public Vec3 targetCamPos;    // camera lerps to this position every tick
    public final AlchemicalWorkstation workstation;

    private float prevScreenTransition = 0.0F;
    public float screenTransition = 0.0F;
    public boolean isClosing = false;

    private InventoryElement inventoryElement;
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/gui/workstation.png");
    private static int RES_X = 128;
    private static int RES_Y = 256;
    
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

        this.screenTransition = 0;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void createGuiElements() {
        InventoryElement iElement = new InventoryElement(-105, 0);
        this.roots.add(iElement);

        Inventory inventory = Minecraft.getInstance().player.getInventory();
        int index = 9;
        for (int inventoryX = 0; inventoryX < 9; inventoryX++) {
            for (int inventoryY = 0; inventoryY < 3; inventoryY++) {
                ItemSlotElement slot = new ItemSlotElement(16 + (inventoryY * 20), 11 + (inventoryX * 20), iElement, inventory, index);
                index++;
            }
        }

        index = 0;
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            ItemSlotElement slot = new ItemSlotElement(79, 11 + (hotbar * 20), iElement, inventory, index);
            index++;
        }

        InventoryKnobElement knob = new InventoryKnobElement(101, 93, iElement);
        
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
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void beginClosing() {
        this.isClosing = true;
    }

    public void tick() {
        super.tick();
        this.pCurrentCamPos = this.currentCamPos.add(0, 0, 0);
        this.prevScreenTransition = this.screenTransition;

        this.currentCamPos = this.currentCamPos.lerp(this.targetCamPos, 0.05);

        this.screenTransition = Mth.clamp(screenTransition + ((isClosing ? -1 : 1) * 0.05F), 0, 1);
        if (this.isClosing && this.prevScreenTransition == 0) {
            this.onClose();
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Vec2 middle = GuiHelper.toGuiSpace(this, this.minecraft.getWindow().getWidth() * 0.5F, this.minecraft.getWindow().getHeight() * 0.5F);
        inventoryElement.y = middle.y - (inventoryElement.height * 0.5F);
        inventoryElement.pY = inventoryElement.y;
        inventoryElement.setScreenTransition(this.getScreenTransition(pPartialTick));
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public void setCameraView(Camera camera, float partialTick) {
        Vec3 pos = getCamPos(partialTick);
        camera.setPosition(camera.getPosition().lerp(pos, getScreenTransition(partialTick)));
    }

    public float getScreenTransition(float partialTicks) {
        return MathUtils.ease(Mth.lerp(partialTicks, prevScreenTransition, screenTransition), isClosing ? MathUtils.EasingType.easeInCubic : MathUtils.EasingType.easeInOutCubic);
    }

    public Vec3 getCamPos(float partialTick) {
        return pCurrentCamPos.lerp(currentCamPos, partialTick);
    }

    public static class InventoryElement extends GuiElement {
        float acceleration = 0;
        boolean cancelVelocity = false;
        boolean flipVelocity = false;
        float velocity = 0;
        private float screenTrans;

        protected InventoryElement(float x, float y) {
            super(x, y, 105, 197, true);
            this.uOffset = 0;
            this.vOffset = 16;
            this.setTexture(TEXTURE, 128, 256);
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            pPoseStack.pushPose();
            pPoseStack.translate(screenTrans * -128, 0, 0);
            super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            pPoseStack.popPose();
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
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

            int maxX = -8;
            if (this.x > maxX) {
                this.x = maxX;
                flipVelocity = true;
            } else if (this.x < -this.width) {
                this.x = -this.width;
                cancelVelocity = true;
            }

            acceleration = 0;
        }

        public void accelerate(float x) {
            if (this.x > -8 && x > 0) return;
            acceleration += x;
        }

        @Override
        public void onDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
            super.onDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
            this.accelerate((float) pDragX);
        }

        public void setScreenTransition(float screenTransition) {
            this.screenTrans = screenTransition;
        }
    }

    public static class InventoryKnobElement extends GuiElement<InventoryElement> {
        float xOffset = 0, pXOffset = 0;

        protected InventoryKnobElement(float x, float y, InventoryElement parent) {
            super(parent, x, y, 16, 16, true);
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
            this.parent.accelerate((float) pDragX);
        }

        private float getXOffset(float partialTick) {
            return Mth.lerp(partialTick, xOffset, pXOffset);
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            pPoseStack.translate(this.getXOffset(pPartialTick), 0, 0);
            super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    public static class ItemSlotElement extends GuiElement<InventoryElement> {
        protected final Inventory inventory;
        protected final int index;
        private float wiggle = 0, pWiggle = 0;
        private float wiggleAmount = 0.2F;
        private float hoverTicks = 0;

        private float parentVelocity = 0, pParentVelocity = 0;

        protected ItemSlotElement(float x, float y, InventoryElement parent, Inventory inventory, int index) {
            super(parent, x, y, 16, 16, true);
            this.inventory = inventory;
            this.index = index;
            this.uOffset = 80;
            this.vOffset = 0;
            this.setTexture(TEXTURE, 128, 256);
        }

        @Override
        public void onHover(float mouseX, float mouseY) {
            super.onHover(mouseX, mouseY);
            hoverTicks = 0;
        }

        @Override
        public void tick(float mouseX, float mouseY) {
            super.tick(mouseX, mouseY);
            this.pParentVelocity = this.parentVelocity;
            this.parentVelocity = this.parent.velocity;
            //Clinker.LOGGER.info(parentVelocity);
            this.pWiggle = wiggle;

            if (this.hovered) {
                this.wiggle = Mth.clamp(wiggle + wiggleAmount, 0, 1);
                hoverTicks++;
            } else {
                this.wiggle = Mth.clamp(wiggle - wiggleAmount, 0, 1);
            }
        }

        private float getWiggle(float partialTick) {
            return Mth.lerp(partialTick, wiggle, pWiggle);
        }

        private float getParentVelocity(float partialTick) {
            return Mth.lerp(partialTick, parentVelocity, pParentVelocity);
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            Matrix4f pMatrix = pPoseStack.last().pose();

            float alpha;
            float pMinU;
            float pMinV;
            float pMaxU;
            float pMaxV;

            RenderSystem.setShader(ClinkerShaders::getPositionColorTextureUnclampedShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

            float velocityOffset = Mth.clamp(-parentVelocity * MathUtils.awfulRandom(this.id * Mth.PI * 256) * 1, -4, 4);

            //draw shadow
            if (!this.getItem().isEmpty()) {
                alpha = 0.3F;
                float shadowOffset = 3F;
                pMinU = (this.uOffset + 16) / (float)this.textureWidth;
                pMinV = this.vOffset / (float)this.textureHeight;
                pMaxU = pMinU + (this.width / (float)this.textureWidth);
                pMaxV = pMinV + (this.height / (float)this.textureHeight);

                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
                bufferbuilder.vertex(pMatrix, 0 + velocityOffset, this.height + shadowOffset, this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMaxV).endVertex();
                bufferbuilder.vertex(pMatrix, this.width + velocityOffset, this.height + shadowOffset, this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMaxV).endVertex();
                bufferbuilder.vertex(pMatrix, this.width + velocityOffset, shadowOffset, this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMinV).endVertex();
                bufferbuilder.vertex(pMatrix, 0 + velocityOffset, shadowOffset, this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMinV).endVertex();
                BufferUploader.drawWithShader(bufferbuilder.end());
            }


            //draw selection highlight
            float xDist = this.getScreenX(pPartialTick) - (pMouseX - 8);
            float yDist = this.getScreenY(pPartialTick) - (pMouseY - 4);
            float distToMouse = Mth.sqrt(xDist * xDist + yDist * yDist);
            alpha = this.hovered ? 0.2F : Mth.clamp(32.0F / distToMouse, 0, 1) * 0.1F;

            pMinU = this.uOffset / (float)this.textureWidth;
            pMinV = this.vOffset / (float)this.textureHeight;
            pMaxU = pMinU + (this.width / (float)this.textureWidth);
            pMaxV = pMinV + (this.height / (float)this.textureHeight);

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex(pMatrix, 0, this.height, this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMaxV).endVertex();
            bufferbuilder.vertex(pMatrix, this.width, this.height, this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMaxV).endVertex();
            bufferbuilder.vertex(pMatrix, this.width, 0, this.blitOffset).color(1, 1, 1, alpha).uv(pMaxU, pMinV).endVertex();
            bufferbuilder.vertex(pMatrix, 0, 0, this.blitOffset).color(1, 1, 1, alpha).uv(pMinU, pMinV).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());

            //draw item
            ItemStack stack = this.getItem();
            float rotation = Mth.cos((this.clientTicks + pPartialTick) * 0.01F + this.id * 20) * 15;
            float rotSign = this.id % 2 == 0 ? -1 : 1;
            rotation += rotSign * Mth.sin((this.hoverTicks + pPartialTick) * 0.05F) * 45 * this.getWiggle(pPartialTick);
            GuiHelper.tryRenderGuiItem(Minecraft.getInstance().getItemRenderer(), stack, rotation, this.getScreenX(pPartialTick) + velocityOffset, this.getScreenY(pPartialTick) - 3 * this.getWiggle(pPartialTick), 1.0F);
        }

        public ItemStack getItem() {
            return inventory.getItem(index);
        }
    }
}
