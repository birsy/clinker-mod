package birsy.clinker.client.render.page;

import birsy.clinker.client.render.ClinkerFramebuffers;
import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.common.page.Page;
import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.elements.TextPageElement;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

import java.util.List;

public class PageRenderer {
    private static final ResourceLocation TEST_PAGE_LOCATION = Clinker.resource("test_page");
    private static final MultiBufferSource.BufferSource PAGE_BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    private static final Matrix4f pageViewMatrix = new Matrix4f();

    public static void beginPageRenderBatch(AdvancedFbo fbo) {
        fbo.bind(true);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(
                RenderSystem.getProjectionMatrix().identity().setOrtho(
                        0.0F, fbo.getWidth(),
                        fbo.getHeight(), 0.0F,
                        100f, -100f
                ),
                VertexSorting.ORTHOGRAPHIC_Z
        );
        RenderSystem.getModelViewMatrix().identity();
    }

    public static void renderPageToAtlas(Page.PageLayout layout, int atlasX, int atlasY) {
        for (PageElement element : layout.elements())
            element.drawToAtlas(PAGE_BUFFER_SOURCE, pageViewMatrix, atlasX, atlasY);
    }

    public static void endPageRenderBatch() {
        PAGE_BUFFER_SOURCE.endBatch();
        RenderSystem.restoreProjectionMatrix();
        AdvancedFbo.unbind();
    }
}
