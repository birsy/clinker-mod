package birsy.clinker.client.book;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL30C;


public class PageAtlas {
    public static final ResourceLocation PAGE_ATLAS_FRAMEBUFFER = Clinker.resource("page_atlas");

    public PageAtlas() {}

    public void draw() {
        AdvancedFbo frameBuffer = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(PAGE_ATLAS_FRAMEBUFFER);
        frameBuffer.bind(true);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.getModelViewMatrix().identity();
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.ORTHOGRAPHIC_Z);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        PoseStack stack = new PoseStack();

        stack.scale(2.0F / frameBuffer.getWidth(), 2.0F / frameBuffer.getHeight(), 1.0F);
        stack.translate(-frameBuffer.getWidth() / 2.0F, -frameBuffer.getHeight() / 2.0F, 0);

        PoseStack.Pose pose = stack.last();

        float scaleX = frameBuffer.getWidth() * 2;
        float scaleY = scaleX;
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.addVertex(pose, 0, 0, 0).setUv(0F, 1F).setColor(1F,1F,1F,1F);
        builder.addVertex(pose,  scaleX, 0, 0).setUv(1F, 1F).setColor(1F,1F,1F,1F);
        builder.addVertex(pose,  scaleX,  scaleY, 0).setUv(1F, 0F).setColor(1F,1F,1F,1F);
        builder.addVertex(pose, 0,  scaleY, 0).setUv(0F, 0F).setColor(1F,1F,1F,1F);
        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ZERO);
        stack.translate(5, -5, 0);
        pose = stack.last();
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.addVertex(pose, 0, 0, 0).setUv(0F, 1F).setColor(1F,1F,1F,1F);
        builder.addVertex(pose,  scaleX, 0, 0).setUv(1F, 1F).setColor(1F,1F,1F,1F);
        builder.addVertex(pose,  scaleX,  scaleY, 0).setUv(1F, 0F).setColor(1F,1F,1F,1F);
        builder.addVertex(pose, 0,  scaleY, 0).setUv(0F, 0F).setColor(1F,1F,1F,1F);
        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.defaultBlendFunc();
//        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//        Minecraft.getInstance().font.drawInBatch(
//                Component.literal("FUCK ASS").withStyle(Style.EMPTY.applyFormats(ChatFormatting.BOLD, ChatFormatting.WHITE)),
//                -5, 5,
//                FastColor.ARGB32.colorFromFloat(1,1,1,1),
//                false,
//                pose.pose(), bufferSource,
//                Font.DisplayMode.NORMAL,
//                0, LightTexture.FULL_BRIGHT
//        );
//        bufferSource.endLastBatch();

        RenderSystem.restoreProjectionMatrix();
        AdvancedFbo.unbind();
    }
}
