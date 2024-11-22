package birsy.clinker.client.gui;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerItems;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AlchemyBundleGUIRenderer {
    public static final ResourceLocation BUNDLE_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/gui/alchemy_bundle.png");
    private final Minecraft minecraft;
    private final ItemRenderer itemRenderer;
    public int ticks;
    private float previousOpenProgress;
    float openProgress;
    boolean open;
    private int soundTimer;
    Random rand = new Random();

    private List<BaggedItem> items;
    public AlchemyBundleGUIRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.itemRenderer = minecraft.getItemRenderer();
        this.items = new ArrayList<>();
        items.add(new BaggedItem(new ItemStack(Items.GLOWSTONE, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.REDSTONE, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.BLAZE_POWDER, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.GUNPOWDER, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.FERMENTED_SPIDER_EYE, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.GLOWSTONE_DUST, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.LAPIS_LAZULI, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.FIRE_CHARGE, rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(ClinkerItems.IMPURE_RUBY.get(), rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(ClinkerItems.PHOSPHOR.get(), rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(ClinkerItems.SULFUR.get(), rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(ClinkerItems.RAW_LEAD.get(), rand.nextInt(512))));
        items.add(new BaggedItem(new ItemStack(Items.BONE_MEAL, rand.nextInt(512))));
        this.ticks = 0;
        this.openProgress = 0;
    }

    public void tick() {
        ticks++;
        soundTimer--;
        previousOpenProgress = openProgress;
        float speed = (1.0F / 20.0F) / 5.0F;
        if (open) {
            openProgress = Mth.clamp(openProgress + speed, 0, 1);
        } else {
            openProgress = Mth.clamp(openProgress - speed, 0, 1);
        }



        for (BaggedItem item : items) {
            item.tick(items, openProgress, open);
        }

        if (soundTimer == 0) {
            if (Minecraft.getInstance().player != null) {
                Vec3 playerPos = Minecraft.getInstance().player.position();
                Minecraft.getInstance().level.playLocalSound(playerPos.x(), playerPos.y(), playerPos.z(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.08F, 0.7F, false);
            }
        }

        if (openProgress <= 0 && previousOpenProgress > 0) {
            if (Minecraft.getInstance().player != null) {
                Vec3 playerPos = Minecraft.getInstance().player.position();
                Minecraft.getInstance().level.playLocalSound(playerPos.x(), playerPos.y(), playerPos.z(), SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 0.2F, 1.0F, false);
            }
        }

        if (ticks % (15 * 20) == 0) {
            for (BaggedItem item : items) {
                if (open == false) {
                    if (Minecraft.getInstance().player != null) {
                        Vec3 playerPos = Minecraft.getInstance().player.position();
                        Minecraft.getInstance().level.playLocalSound(playerPos.x(), playerPos.y(), playerPos.z(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.02F, 1.0F, false);
                    }
                    item.setOpenPosition((float) (28 + (rand.nextGaussian() * 1)), (float) (120 + (rand.nextGaussian() * 15)));
                } else {
                    soundTimer = 5;
                    item.setClosePosition(23.0F, (float) (209 + (rand.nextGaussian() * 0.5)));
                }
            }
            open = !open;
        }
    }

    public void render(PoseStack pPoseStack, float partialTick) {
        float x = 15;
        float y = 200;
        float size = 32.0F;
        RenderSystem.disableDepthTest();
        if (openProgress > 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, BUNDLE_LOCATION);
            drawQuad(pPoseStack, x, y, x + size, y + size, 0, 16, 0, 32, 16, 32, 32);
        }

        int z = 0;
        for (BaggedItem item : items) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
            //item.render(itemRenderer, minecraft.player, pPoseStack, openProgress, ticks + partialTick, partialTick, z++);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BUNDLE_LOCATION);
        if (openProgress > 0) {
            drawQuad(pPoseStack, x, y, x + size, y + size, 300, 0, 0, 16, 16, 32, 32);
        } else {
            drawQuad(pPoseStack, x, y, x + size, y + size, 300, 0, 16, 16, 32, 32, 32);
        }
        RenderSystem.enableDepthTest();
    }

    private static void drawQuad(PoseStack stack, float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2, int textureSizeX, int textureSizeY) {
        u1 /= textureSizeX;
        u2 /= textureSizeX;
        v1 /= textureSizeY;
        v2 /= textureSizeY;

        Matrix4f matrix = stack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, x1, y2, z).uv(u1, v2).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, z).uv(u2, v2).endVertex();
        bufferbuilder.vertex(matrix, x2, y1, z).uv(u2, v1).endVertex();
        bufferbuilder.vertex(matrix, x1, y1, z).uv(u1, v1).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    private class BaggedItem {
        private Vec2 previousPosition;
        private Vec2 currentPosition;
        private Vec2 openPosition;
        private Vec2 closePosition;
        private Vec2 force;
        public ItemStack stack;

        protected BaggedItem(ItemStack stack) {
            this.stack = stack;
            this.currentPosition = Vec2.ZERO;
            this.openPosition = Vec2.ZERO;
            this.closePosition = Vec2.ZERO;
            this.force = Vec2.ZERO;
        }

        protected void setOpenPosition(float x, float y) {
            this.openPosition = new Vec2(x, y);
        }
        protected void setClosePosition(float x, float y) {
            this.closePosition = new Vec2(x, y);
        }
        private Vec2 getTargetPosition(boolean open) {
            return open ? openPosition : closePosition;
        }

        protected void tick(List<BaggedItem> items, float floatProgress, boolean open) {
            this.previousPosition = this.currentPosition;
            this.force = this.force.scale(0.8F);

            Vec2 difference = this.getTargetPosition(open).add(this.currentPosition.negated());
            this.force = this.force.add(difference.scale(0.01F));

            for (BaggedItem item : items) {
                Vec2 difference2 = item.currentPosition.add(this.currentPosition.negated());
                if (difference2.length() != 0) {
                    this.force = this.force.add(difference2.normalized().scale((1 / difference2.length()) * 0.8F).negated());
                }
            }

            if (Float.isNaN(this.force.x) || Float.isNaN(this.force.y)) {
                this.force = Vec2.ZERO;
            }

            float factor = floatProgress * floatProgress;
            Vec2 target = closePosition;
            if (open) {
                target = new Vec2(Mth.lerp(floatProgress, closePosition.x, openPosition.x), Mth.lerp(floatProgress, closePosition.y, openPosition.y));
            }
            this.currentPosition = this.currentPosition.add(this.force.scale(factor));

            this.currentPosition = new Vec2(Mth.lerp(factor, target.x, currentPosition.x), Mth.lerp(factor, target.y, currentPosition.y));
            //this.currentPosition = this.targetPosition;
        }

        protected void render(ItemRenderer renderer, GuiGraphics graphics, Player player, PoseStack pPoseStack, float floatProgress, float ticks, float partialTick, int z) {
            float x = Mth.lerp(partialTick, previousPosition.x, currentPosition.x) + (Mth.sin(z + ticks * 0.05F) * floatProgress * 0.5F);
            float y = Mth.lerp(partialTick, previousPosition.y, currentPosition.y) + (Mth.cos(z + ticks * 0.07F) * floatProgress * 0.5F);
            float rot = ticks + partialTick + z;
            GuiHelper.tryRenderGuiItem(renderer, stack, rot, x, y, 0.5F);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            GuiHelper.renderGuiItemDecorations(graphics, Minecraft.getInstance().fontFilterFishy, stack, x, y, 0.5F, null);
        }
    }
}
