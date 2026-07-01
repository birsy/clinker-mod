package birsy.clinker.client.render.world.cloud;

import birsy.clinker.client.render.ClinkerFramebuffers;
import birsy.clinker.client.render.ClinkerShaders;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.neoforge.client.GlStateBackup;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

import java.util.List;

import static com.mojang.blaze3d.platform.GlConst.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;

public class OthershoreCloudRenderer {
    final List<CloudRendererHolder> cloudRenderers;

    VertexBuffer billboardVbo;
    boolean initialized = false;
    int lastRenderRadius = 0;

    public OthershoreCloudRenderer() {
        this.cloudRenderers = List.of(
                new CloudRendererHolder(new ChunkedUpperLayerCloudRenderer()),
                new CloudRendererHolder(new StormFrontCloudRenderer())
        );
    }

    void initialize() {
        // create the billboard vbo
        if (billboardVbo != null) billboardVbo.close();
        billboardVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        billboardVbo.bind();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(0, 0, 0).setUv(-1, -1);
        bufferBuilder.addVertex(0, 0, 0).setUv( 1, -1);
        bufferBuilder.addVertex(0, 0, 0).setUv( 1,  1);
        bufferBuilder.addVertex(0, 0, 0).setUv(-1,  1);
        MeshData meshData = bufferBuilder.buildOrThrow();
        billboardVbo.upload(meshData);
        VertexBuffer.unbind();

        // initialize the renderers
        this.lastRenderRadius = getRenderRadius();
        for (CloudRendererHolder holder : cloudRenderers)
            holder.renderer.initialize(this.lastRenderRadius);
        this.initialized = true;
    }

    void rebuild(int renderRadius) {
        for (CloudRendererHolder holder : cloudRenderers)
            holder.renderer.rebuild(renderRadius);
    }

    public void free() {
        for (CloudRendererHolder holder : cloudRenderers)
            holder.renderer.free();
        if (this.billboardVbo != null) this.billboardVbo.close();
    }

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        Minecraft.getInstance().getProfiler().push("clinker.draw_clouds");
        if (!initialized) {
            initialize();
        }
        int renderRadius = getRenderRadius();
        if (renderRadius != lastRenderRadius) {
            lastRenderRadius = renderRadius;
            rebuild(renderRadius);
        }

        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);

        AdvancedFbo mainFbo = AdvancedFbo.getMainFramebuffer();
        AdvancedFbo cloudFbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.CLOUDS);
        cloudFbo.bind(true);
        cloudFbo.clear(skyColor.x(), skyColor.y(), skyColor.z(), 0.0F, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        RenderSystem.clearStencil(0x00);
        GL11.glClear(GL11C.GL_STENCIL_BUFFER_BIT);

        cloudFbo.bind(true);
        // copy depth from main framebuffer
        ShaderProgram depthBlitShader = VeilRenderSystem.setShader(ClinkerShaders.VEIL_BLIT_DEPTH);
        depthBlitShader.bind();
        depthBlitShader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_STRIP);
        depthBlitShader.setTexture("DiffuseDepthSampler", GL_TEXTURE_2D, mainFbo.getDepthTextureAttachment().getId());
        depthBlitShader.bindSamplers(0);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        VeilRenderSystem.drawScreenQuad();
        ShaderProgram.unbind();

        // render other useful cloud textures
        renderCloudDensityTexture(ticks, partialTick);
        renderCloudSpriteTexture(ticks, partialTick);

        // cloud drawing process
        cloudFbo.bind(true);

        Frustum cameraFrustum = Minecraft.getInstance().levelRenderer.getFrustum();
        for (CloudRendererHolder holder : cloudRenderers)
            holder.shouldRender = cameraFrustum.isVisible(holder.renderer.getRenderBounds(this, camX, camY, camZ, partialTick));

        for (CloudRendererHolder holder : cloudRenderers)
            if (holder.shouldRender) holder.renderer.preRender(this, level, ticks, partialTick, poseStack, camX, camY, camZ, projectionMatrix, skyColor);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE
        );

        GL11.glEnable(GL11C.GL_STENCIL_TEST);
        RenderSystem.stencilMask(1);
        RenderSystem.stencilFunc(GL11C.GL_ALWAYS, 1, 0xFF); // always pass stencil test
        RenderSystem.stencilOp(GL11C.GL_KEEP, GL11C.GL_KEEP, GL11C.GL_REPLACE);
        for (CloudRendererHolder holder : cloudRenderers)
            if (holder.shouldRender) holder.renderer.render(this, level, ticks, partialTick, poseStack, camX, camY, camZ, projectionMatrix, skyColor);

        RenderSystem.stencilOp(GL11C.GL_KEEP, GL11C.GL_KEEP, GL11C.GL_KEEP);
        for (CloudRendererHolder holder : cloudRenderers)
            if (holder.shouldRender) holder.renderer.postRender(this, level, ticks, partialTick, poseStack, camX, camY, camZ, projectionMatrix, skyColor);

        AdvancedFbo.unbind();

        composite();

        RenderSystem.restoreGlState(backup);
        Minecraft.getInstance().getProfiler().pop();
    }

    void composite() {
        AdvancedFbo cloudCompositeFbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.CLOUDS_COMPOSITE);
        cloudCompositeFbo.bind(true);

        ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_COMPOSITE);
        shader.bind();
        shader.bindSamplers(0);
        shader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_STRIP);
        VeilRenderSystem.drawScreenQuad();

        AdvancedFbo.unbind();
        ShaderProgram.unbind();
        // copy it into the main framebuffer
        AdvancedFbo mainFbo = AdvancedFbo.getMainFramebuffer();
        cloudCompositeFbo.bindRead();
        mainFbo.bindDraw(true);
        GlStateManager._glBlitFrameBuffer(
                0, 0, cloudCompositeFbo.getWidth(), cloudCompositeFbo.getHeight(),
                0, 0, mainFbo.getWidth(), mainFbo.getHeight(),
                GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT,
                GL_NEAREST
        );
    }

    void renderCloudDensityTexture(int ticks, double partialTicks) {
        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.CLOUD_DENSITY);
        fbo.bind(true);
        ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_DENSITY);
        shader.bind();
        shader.bindSamplers(0);
        shader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_STRIP);
        shader.getUniform("GameTime").setFloat((float) ((ticks / 20.0 + partialTicks / 20.0) * 0.3));
        VeilRenderSystem.drawScreenQuad();
        ShaderProgram.unbind();
        AdvancedFbo.unbind();
    }
    void renderCloudSpriteTexture(int ticks, double partialTicks) {
        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.CLOUD_SPRITE);
        fbo.bind(true);
        ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_SPRITE);
        shader.bind();
        shader.bindSamplers(0);
        shader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_STRIP);
        shader.getUniform("GameTime").setFloat((float) ((ticks / 20.0 + partialTicks / 20.0) * 0.3));
        VeilRenderSystem.drawScreenQuad();
        ShaderProgram.unbind();
        AdvancedFbo.unbind();
    }

    public VertexBuffer getBillboardVbo() { return billboardVbo; }

    private static int getRenderRadius() {
        return Minecraft.getInstance().options.renderDistance().get() * 16 + 8;
    }

    private static class CloudRendererHolder {
        final BillboardCloudRenderer renderer;
        boolean shouldRender = false;
        private CloudRendererHolder(BillboardCloudRenderer renderer) { this.renderer = renderer; }
    }
}
