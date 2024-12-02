package birsy.clinker.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;

import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;


public class GuiHelper {
    //Given all leaf nodes, finds the hovered element with the most parents.
    public static GuiElement findHoveredElement(Collection<GuiElement> leafNodes, float mouseX, float mouseY, float partialTick) {
        Set<GuiElement> parents = new HashSet<>();
        if (leafNodes == null) return null;
        for (GuiElement element : leafNodes) {
            if (element == null) continue;

            if (mouseX > element.getScreenX(partialTick) && mouseX < element.getScreenX(partialTick) + element.width &&
                mouseY > element.getScreenY(partialTick) && mouseY < element.getScreenY(partialTick) + element.height &&
                element.interactable){
                return element;
            }
            parents.add(element.parent);
        }

        if (parents.isEmpty()) return null;
        return findHoveredElement(parents, mouseX, mouseY, partialTick);
    }

    public static List<GuiElement> generateLeafNodes(GuiElement root) {
        List<GuiElement> list = new ArrayList<>();
        return generateLeafNodes(root, list);
    }

    public static List<GuiElement> generateLeafNodes(GuiElement parent, List<GuiElement> list) {
        if (parent.children.isEmpty()) {
            list.add(parent);
        } else {
            for (Object child : parent.children) {
                generateLeafNodes((GuiElement) child, list);
            }
        }

        return list;
    }

    public static Vec2 toGuiSpace(Screen guiScreen, float x, float y) {
        Window mainWindow = guiScreen.getMinecraft().getWindow();
        float gX = x * mainWindow.getGuiScaledWidth() / mainWindow.getScreenWidth();
        float gY = y * mainWindow.getGuiScaledHeight() / mainWindow.getScreenHeight();
        return new Vec2(gX, gY);
    }

    public static void tryRenderGuiItem(ItemRenderer renderer, ItemStack pStack, float rotation, float pX, float pY, float alpha) {
        if (!pStack.isEmpty()) {
            BakedModel bakedmodel = renderer.getModel(pStack, null, Minecraft.getInstance().player, 0);
            try {
                renderGuiItem(renderer, pStack, rotation, pX, pY, bakedmodel, alpha);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(pStack.getItem()));
                //crashreportcategory.setDetail("Registry Name", () -> String.valueOf(net.neoforged.registries.ForgeRegistries.ITEMS.getKey(pStack.getItem())));
                crashreportcategory.setDetail("Item Damage", () -> String.valueOf(pStack.getDamageValue()));
                crashreportcategory.setDetail("Item NBT", () -> String.valueOf(pStack.getTag()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(pStack.hasFoil()));
                throw new ReportedException(crashreport);
            }
        }
    }

    protected static void renderGuiItem(ItemRenderer renderer, ItemStack pStack, float rotation, float pX, float pY, BakedModel pBakedModel, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        mc.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(pX, pY, 150.0F);
        posestack.translate(8.0D, 8.0D, 0.0D);
        posestack.mulPose(Axis.YP.rotationDegrees(rotation));
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !pBakedModel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }

        renderer.render(pStack, ItemDisplayContext.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, pBakedModel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }



    
    public static void renderGuiItemDecorations(GuiGraphics graphics, Font font, ItemStack pStack, float pXPosition, float pYPosition, float alpha, @Nullable String pText) {
        if (!pStack.isEmpty()) {
            PoseStack posestack = new PoseStack();
            if (pStack.getCount() != 1 || pText != null) {
                String s = pText == null ? String.valueOf(pStack.getCount()) : pText;
                float scale = 0.7F;
                posestack.scale(scale, scale, 1.0F);
                posestack.translate(0.0D, 0.0D, 200.0F);
                MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(s, (pXPosition + 19 - 2) / scale - font.width(s), (pYPosition + 6 + 3) / scale, 16777215 + ((int)(alpha * 255) << 24), true, posestack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                bufferSource.endBatch();
            }

            if (pStack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                int i = pStack.getBarWidth();
                int j = pStack.getBarColor();
                fillRect(bufferbuilder, pXPosition + 2, pYPosition + 13, 13, 2, 0, 0, 0, 255);
                fillRect(bufferbuilder, pXPosition + 2, pYPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            }

            LocalPlayer localplayer = Minecraft.getInstance().player;
            float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(pStack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator1 = Tesselator.getInstance();
                BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                fillRect(bufferbuilder1, pXPosition, pYPosition + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableDepthTest();
            }

            net.neoforged.neoforge.client.ItemDecoratorHandler.of(pStack).render(graphics, font, pStack, (int) pXPosition, (int) pYPosition);
        }
    }

    public static void tryRenderGuiItem(ItemRenderer renderer, ItemStack pStack, PoseStack stack, float alpha) {
        if (!pStack.isEmpty()) {
            BakedModel bakedmodel = renderer.getModel(pStack, null, Minecraft.getInstance().player, 0);
            try {
                renderGuiItem(renderer, pStack, stack, bakedmodel, alpha);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(pStack.getItem()));
                //crashreportcategory.setDetail("Registry Name", () -> String.valueOf(net.neoforged.registries.ForgeRegistries.ITEMS.getKey(pStack.getItem())));
                crashreportcategory.setDetail("Item Damage", () -> String.valueOf(pStack.getDamageValue()));
                crashreportcategory.setDetail("Item NBT", () -> String.valueOf(pStack.getTag()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(pStack.hasFoil()));
                throw new ReportedException(crashreport);
            }

        }
    }

    protected static void renderGuiItem(ItemRenderer renderer, ItemStack pStack, PoseStack stack, BakedModel pBakedModel, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        mc.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        stack.pushPose();

        stack.scale(1.0F, -1.0F, 1.0F);
        stack.scale(16.0F, 16.0F, 16.0F);


        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();


        boolean flag = !pBakedModel.usesBlockLight();
        Vector3f DIFFUSE_LIGHT_0 = Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
        Vector3f DIFFUSE_LIGHT_1 = Util.make(new Vector3f(1.0F, 1.0F, 1.0F), (vec) -> {
            stack.last(). normal().transform(vec);
            vec.normalize();
        });
        //DIFFUSE_LIGHT_1.transform(stack.last(). normal());

        //RenderSystem.setupGui3DDiffuseLighting(DIFFUSE_LIGHT_1, DIFFUSE_LIGHT_1);
        //RenderSystem.setupGuiFlatDiffuseLighting(DIFFUSE_LIGHT_1, DIFFUSE_LIGHT_1);

        if (pBakedModel.isGui3d()) {
            Vector3f DL_0 = new Vector3f(0.2F, -1.0F, -0.7F);
            DL_0.normalize();
            Vector3f DL_1 = new Vector3f(-0.2F, -1.0F, 0.7F);
            DL_1.normalize();
            RenderSystem.setupGui3DDiffuseLighting(DL_1, DL_0);
        } else {
            Lighting.setupForFlatItems();
        }


        renderer.render(pStack, ItemDisplayContext.GUI, false, stack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, pBakedModel);
        bufferSource.endBatch();

        RenderSystem.enableDepthTest();
        stack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void fillRect(BufferBuilder pRenderer, float pX, float pY, float pWidth, float pHeight, float pRed, float pGreen, float pBlue, float pAlpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        pRenderer.addVertex(pX + 0, pY + 0, 0.0D).setColor(pRed, pGreen, pBlue, pAlpha);
        pRenderer.addVertex(pX + 0, pY + pHeight, 0.0D).setColor(pRed, pGreen, pBlue, pAlpha);
        pRenderer.addVertex(pX + pWidth, pY + pHeight, 0.0D).setColor(pRed, pGreen, pBlue, pAlpha);
        pRenderer.addVertex(pX + pWidth, pY + 0, 0.0D).setColor(pRed, pGreen, pBlue, pAlpha);
        BufferUploader.drawWithShader(pRenderer.end());
    }

    public static int blitOffset = 0;

    public static void blit(PoseStack pPoseStack, float pX, float pY, float pBlitOffset, float pWidth, float pHeight, TextureAtlasSprite pSprite) {
        innerBlit(pPoseStack.last().pose(), pX, pX + pWidth, pY, pY + pHeight, pBlitOffset, pSprite.getU0(), pSprite.getU1(), pSprite.getV0(), pSprite.getV1());
    }

    public void blit(PoseStack pPoseStack, float pX, float pY, float pUOffset, float pVOffset, float pUWidth, float pVHeight) {
        blit(pPoseStack, pX, pY, 0, pUOffset, pVOffset, pUWidth, pVHeight, 256, 256);
    }

    public static void blit(PoseStack pPoseStack, float pX, float pY, float pBlitOffset, float pUOffset, float pVOffset, float pUWidth, float pVHeight, int pTextureHeight, int pTextureWidth) {
        innerBlit(pPoseStack, pX, pX + pUWidth, pY, pY + pVHeight, pBlitOffset, pUWidth, pVHeight, pUOffset, pVOffset, pTextureHeight, pTextureWidth);
    }

    public static void blit(PoseStack pPoseStack, float pX, float pY, float pWidth, float pHeight, float pUOffset, float pVOffset, float pUWidth, float pVHeight, int pTextureWidth, int pTextureHeight) {
        innerBlit(pPoseStack, pX, pX + pWidth, pY, pY + pHeight, blitOffset, pUWidth, pVHeight, pUOffset, pVOffset, pTextureWidth, pTextureHeight);
    }

    public static void blit(PoseStack pPoseStack, float pX, float pY, float pUOffset, float pVOffset, float pWidth, float pHeight, int pTextureWidth, int pTextureHeight) {
        blit(pPoseStack, pX, pY, pWidth, pHeight, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    private static void innerBlit(PoseStack pPoseStack, float pX1, float pX2, float pY1, float pY2, float pBlitOffset, float pUWidth, float pVHeight, float pUOffset, float pVOffset, int pTextureWidth, int pTextureHeight) {
        innerBlit(pPoseStack.last().pose(), pX1, pX2, pY1, pY2, pBlitOffset, (pUOffset + 0.0F) / (float)pTextureWidth, (pUOffset + pUWidth) / (float)pTextureWidth, (pVOffset + 0.0F) / (float)pTextureHeight, (pVOffset + pVHeight) / (float)pTextureHeight);
    }

    private static void innerBlit(Matrix4f pMatrix, float pX1, float pX2, float pY1, float pY2, float pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(pMatrix, pX1, pY2, pBlitOffset).setUv(pMinU, pMaxV);
        bufferbuilder.addVertex(pMatrix, pX2, pY2, pBlitOffset).setUv(pMaxU, pMaxV);
        bufferbuilder.addVertex(pMatrix, pX2, pY1, pBlitOffset).setUv(pMaxU, pMinV);
        bufferbuilder.addVertex(pMatrix, pX1, pY1, pBlitOffset).setUv(pMinU, pMinV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void setCameraRotation(Camera camera, Quaterniond rotation) {
        camera.rotation.set(rotation);
        camera.forwards.set(0.0F, 0.0F, 1.0F).rotate(camera.rotation);
        camera.up.set(0.0F, 1.0F, 0.0F).rotate(camera.rotation);
        camera.left.set(1.0F, 0.0F, 0.0F).rotate(camera.rotation);
    }
}
