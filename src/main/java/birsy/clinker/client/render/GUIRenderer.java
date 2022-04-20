package birsy.clinker.client.render;

import birsy.clinker.common.blockentity.IAlchemicalInfo;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GUIRenderer {
    @SubscribeEvent
    public static void onRenderGUI(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();

        if (mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)mc.hitResult).getBlockPos();
            BlockEntity blockEntity = mc.getCameraEntity().level.getBlockEntity(blockpos);
            if (blockEntity instanceof IAlchemicalInfo) {
                List<? extends FormattedText> text = ((IAlchemicalInfo) blockEntity).getText(blockEntity);

                Vector3f screenSpacePosition = worldToScreenSpace(new Vec3(blockpos.getX() + 0.5F, blockpos.getY() + 0.5F, blockpos.getZ() + 0.5F), event.getPartialTicks());
                screenSpacePosition = new Vector3f(Mth.clamp(screenSpacePosition.x(), 0, window.getGuiScaledWidth()),
                                                   Mth.clamp(screenSpacePosition.y(), 0, window.getGuiScaledHeight() - (mc.font.lineHeight * text.size())),
                                                   screenSpacePosition.z());

                int[] boxSize = drawHoveringText(ItemStack.EMPTY, event.getMatrixStack(), text, (int) Math.floor(screenSpacePosition.x()), (int) Math.floor(screenSpacePosition.y()), window.getGuiScaledWidth(), window.getGuiScaledHeight(), 200, 0x08100010,  (0x508589C2 & 0xFEFEFE) >> 1 | 0x08100010 & 0xFF000000, 0x50A59f5B,  mc.font);
                ((IAlchemicalInfo) blockEntity).renderCustomElements(blockEntity, event.getMatrixStack(), (int) Math.floor(screenSpacePosition.x()), (int) Math.floor(screenSpacePosition.y()), boxSize[0], boxSize[1]);
            }
        }
    }

    public static Vector3f worldToScreenSpace(Vec3 pos, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPosition = camera.getPosition();

        Vector3f position = new Vector3f((float) (cameraPosition.x - pos.x), (float) (cameraPosition.y - pos.y), (float) (cameraPosition.z - pos.z));
        Quaternion cameraRotation = camera.rotation().copy();
        cameraRotation.conj();
        position.transform(cameraRotation);

        // Account for view bobbing
        if (mc.options.bobView && mc.getCameraEntity() instanceof Player)
        {
            Player player = (Player) mc.getCameraEntity();
            float playerStep = player.walkDist - player.walkDistO;
            float stepSize = -(player.walkDist + playerStep * partialTicks);
            float viewBob = Mth.lerp(partialTicks, player.oBob, player.bob);

            Quaternion bobXRotation = Vector3f.XP.rotationDegrees(Math.abs(Mth.cos(stepSize * (float) Math.PI - 0.2f) * viewBob) * 5f);
            Quaternion bobZRotation = Vector3f.ZP.rotationDegrees(Mth.sin(stepSize * (float) Math.PI) * viewBob * 3f);
            bobXRotation.conj();
            bobZRotation.conj();
            position.transform(bobXRotation);
            position.transform(bobZRotation);
            position.add(Mth.sin(stepSize * (float) Math.PI) * viewBob * 0.5f, Math.abs(Mth.cos(stepSize * (float) Math.PI) * viewBob), 0f);
        }

        Window window = mc.getWindow();
        float screenSize = window.getGuiScaledHeight() / 2f / position.z() / (float) Math.tan(Math.toRadians(mc.gameRenderer.getFov(camera, partialTicks, true) / 2f));
        position.mul(-screenSize, -screenSize, 1f);
        position.add(window.getGuiScaledWidth() / 2f, window.getGuiScaledHeight() / 2f, 0f);

        return position;
    }

    public static int[] drawHoveringText(@Nonnull final ItemStack stack, PoseStack pStack, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
        if (!textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, pStack, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
            if (MinecraftForge.EVENT_BUS.post(event))
                return new int[0];
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();

            // RenderSystem.disableRescaleNormal();
            RenderSystem.disableDepthTest();
            int tooltipTextWidth = 0;

            for (FormattedText textLine : textLines)
            {
                int textLineWidth = font.width(textLine);
                if (textLineWidth > tooltipTextWidth)
                    tooltipTextWidth = textLineWidth;
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth)
            {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2)
                        tooltipTextWidth = mouseX - 12 - 8;
                    else
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
            {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap)
            {
                int wrappedTooltipWidth = 0;
                List<FormattedText> wrappedTextLines = new ArrayList<>();
                for (int i = 0; i < textLines.size(); i++)
                {
                    FormattedText textLine = textLines.get(i);
                    List<FormattedText> wrappedLine = font.getSplitter().splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                    if (i == 0)
                        titleLinesCount = wrappedLine.size();

                    for (FormattedText line : wrappedLine)
                    {
                        int lineWidth = font.width(line);
                        if (lineWidth > wrappedTooltipWidth)
                            wrappedTooltipWidth = lineWidth;
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                else
                    tooltipX = mouseX + 12;
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1)
            {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount)
                    tooltipHeight += 2; // gap between title lines and next lines
            }

            if (tooltipY < 4)
                tooltipY = 4;
            else if (tooltipY + tooltipHeight + 4 > screenHeight)
                tooltipY = screenHeight - tooltipHeight - 4;

            final int zLevel = 400;
            RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, pStack, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
            MinecraftForge.EVENT_BUS.post(colorEvent);
            backgroundColor = colorEvent.getBackground();
            borderColorStart = colorEvent.getBorderStart();
            borderColorEnd = colorEvent.getBorderEnd();

            pStack.pushPose();
            Matrix4f mat = pStack.last().pose();
            //TODO, lots of unnessesary GL calls here, we can buffer all these together.
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, pStack, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

            MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            pStack.translate(0.0D, 0.0D, zLevel);

            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
            {
                FormattedText line = textLines.get(lineNumber);
                if (line != null)
                    font.drawInBatch(Language.getInstance().getVisualOrder(line), (float)tooltipX, (float)tooltipY, -1, true, mat, renderType, false, 0, 15728880);

                if (lineNumber + 1 == titleLinesCount)
                    tooltipY += 2;

                tooltipY += 10;
            }

            renderType.endBatch();
            pStack.popPose();

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, pStack, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            RenderSystem.enableDepthTest();
            // RenderSystem.enableRescaleNormal();

            return new int[]{tooltipTextWidth, tooltipHeight};
        }

        return null;
    }
}
