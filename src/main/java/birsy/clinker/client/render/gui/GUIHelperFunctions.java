package birsy.clinker.client.render.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GUIHelperFunctions {
    public static void tryRenderGuiItem(ItemRenderer renderer, ItemStack pStack, float rotation, float pX, float pY, float alpha) {
        if (!pStack.isEmpty()) {
            BakedModel bakedmodel = renderer.getModel(pStack, null, Minecraft.getInstance().player, 0);
            renderer.blitOffset = bakedmodel.isGui3d() ? renderer.blitOffset + 50.0F : renderer.blitOffset + 50.0F;

            try {
                renderGuiItem(renderer, pStack, rotation, pX, pY, bakedmodel, alpha);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(pStack.getItem()));
                crashreportcategory.setDetail("Registry Name", () -> String.valueOf(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(pStack.getItem())));
                crashreportcategory.setDetail("Item Damage", () -> String.valueOf(pStack.getDamageValue()));
                crashreportcategory.setDetail("Item NBT", () -> String.valueOf(pStack.getTag()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(pStack.hasFoil()));
                throw new ReportedException(crashreport);
            }

            renderer.blitOffset = bakedmodel.isGui3d() ? renderer.blitOffset - 50.0F : renderer.blitOffset - 50.0F;
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
        posestack.translate(pX, pY, 100.0F + mc.getItemRenderer().blitOffset);
        posestack.translate(8.0D, 8.0D, 0.0D);
        posestack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !pBakedModel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }

        renderer.render(pStack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, pBakedModel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderGuiItemDecorations(ItemRenderer renderer, Font font, ItemStack pStack, float pXPosition, float pYPosition, float alpha, @Nullable String pText) {
        if (!pStack.isEmpty()) {
            PoseStack posestack = new PoseStack();
            if (pStack.getCount() != 1 || pText != null) {
                String s = pText == null ? String.valueOf(pStack.getCount()) : pText;
                float scale = 0.7F;
                posestack.scale(scale, scale, 1.0F);
                posestack.translate(0.0D, 0.0D, renderer.blitOffset + 200.0F);
                MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(s, (pXPosition + 19 - 2) / scale - font.width(s), (pYPosition + 6 + 3) / scale, 16777215 + ((int)(alpha * 255) << 24), true, posestack.last().pose(), bufferSource, true, 0, 15728880);
                bufferSource.endBatch();
            }

            if (pStack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                int i = pStack.getBarWidth();
                int j = pStack.getBarColor();
                fillRect(bufferbuilder, pXPosition + 2, pYPosition + 13, 13, 2, 0, 0, 0, 255);
                fillRect(bufferbuilder, pXPosition + 2, pYPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            LocalPlayer localplayer = Minecraft.getInstance().player;
            float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(pStack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator1 = Tesselator.getInstance();
                BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                fillRect(bufferbuilder1, pXPosition, pYPosition + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            net.minecraftforge.client.ItemDecoratorHandler.of(pStack).render(font, pStack, (int) pXPosition, (int) pYPosition, renderer.blitOffset);
        }
    }

    public static void fillRect(BufferBuilder pRenderer, float pX, float pY, float pWidth, float pHeight, float pRed, float pGreen, float pBlue, float pAlpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        pRenderer.vertex(pX + 0, pY + 0, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex(pX + 0, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex(pX + pWidth, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex(pX + pWidth, pY + 0, 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        BufferUploader.drawWithShader(pRenderer.end());
    }
}
