package birsy.clinker.client.render;

import birsy.clinker.common.page.Page;
import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.elements.TextPageElement;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.joml.Matrix4f;

import java.util.List;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class PageRenderer {
    private static final ResourceLocation TEST_PAGE_LOCATION = Clinker.resource("test_page");
    private static final MultiBufferSource.BufferSource TEXT_BUFFER_SOURCE = MultiBufferSource.immediate(new ByteBufferBuilder(1536));

    private static final Matrix4f pageViewMatrix = new Matrix4f();

    @SubscribeEvent
    public static void onRenderGUI(RenderGuiEvent.Post event) {
        if (Minecraft.getInstance().level == null) return;
        if (Minecraft.getInstance().getConnection() == null) return;

        RegistryAccess registryAccess = Minecraft.getInstance().getConnection().registryAccess();
        Page testPage = registryAccess.registryOrThrow(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY).get(TEST_PAGE_LOCATION);

        Page.PageLayout layout = testPage.getLayout(Minecraft.getInstance().getLanguageManager().getSelected());

        AdvancedFbo pageAtlasFBO = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.PAGE_ATLAS);
        pageAtlasFBO.bind(true);

        RenderUtils.backupGLState();
        RenderSystem.setProjectionMatrix(
                RenderSystem.getProjectionMatrix().identity().setOrtho(0.0F, pageAtlasFBO.getWidth(), pageAtlasFBO.getHeight(), 0.0F, 0.1f, -0.1f),
                VertexSorting.ORTHOGRAPHIC_Z
        );
        RenderSystem.getModelViewMatrix().identity();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();

        for (PageElement element : layout.elements()) {
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            float halfWidth  = element.transform.width() * 0.5F,
                  halfHeight = element.transform.height() * 0.5F;

            pageViewMatrix.identity();
            pageViewMatrix.translate(element.transform.x() + halfWidth, element.transform.y() + halfHeight, 0);
            pageViewMatrix.rotateYXZ(0, 0, element.transform.rotation());

            bufferbuilder.addVertex(pageViewMatrix, -halfWidth, -halfHeight, 0).setColor(0.0F, 0.0F, 0.0F, 0.5F);
            bufferbuilder.addVertex(pageViewMatrix, -halfWidth,  halfHeight, 0).setColor(0.0F, 1.0F, 0.0F, 0.5F);
            bufferbuilder.addVertex(pageViewMatrix,  halfWidth,  halfHeight, 0).setColor(1.0F, 1.0F, 0.0F, 0.5F);
            bufferbuilder.addVertex(pageViewMatrix,  halfWidth, -halfHeight, 0).setColor(1.0F, 0.0F, 0.0F, 0.5F);

            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            pageViewMatrix.translate(-halfWidth, -halfHeight, 0);

            if (element instanceof TextPageElement textElement) {
                if (textElement.formattedText == null)
                    textElement.resolveFormattedText();
                List<FormattedCharSequence> text = textElement.formattedText;

                int lineHeight = Minecraft.getInstance().font.lineHeight;
                for (int i = 0; i < text.size(); i++) {
                    FormattedCharSequence string = text.get(i);
                    Minecraft.getInstance().font.drawInBatch(
                            string,
                            0, lineHeight * i,
                            0xFFFFFFFF,
                            false,
                            pageViewMatrix,
                            TEXT_BUFFER_SOURCE,
                            Font.DisplayMode.NORMAL,
                            0,
                            LightTexture.FULL_BRIGHT
                    );
                }
            }
        }

        TEXT_BUFFER_SOURCE.endBatch();


        ShaderProgram.unbind();
        AdvancedFbo.unbind();
        RenderUtils.restoreGLState();
    }
}
