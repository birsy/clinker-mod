package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3fc;


public class OthershoreCloudRenderer {
    public static final float CLOUDS_START = 250.0F;
    public static final float CLOUDS_END = 450.0F;
    public static final float CLOUD_LAYER_THICKNESS = 48.0F;

    private static final ResourceLocation NOISE_TEXTURE = Clinker.resource("textures/environment/noise.png");

    private VertexBuffer cloudLayerDownBuffer;
    private VertexBuffer cloudLayerUpBuffer;

    public OthershoreCloudRenderer() {}

    private float previousRadius = -1.0F;
    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.depthMask(true);

        float radius = (Minecraft.getInstance().options.getEffectiveRenderDistance()+1) * 4.0F * 16.0F;

        // rebuild VBOs if the render distance changes
        if (radius != previousRadius) {
            previousRadius = radius;
            this.cloudLayerDownBuffer = buildCloudBuffer(cloudLayerDownBuffer, 64, 2, true, radius);
            this.cloudLayerUpBuffer = buildCloudBuffer(cloudLayerUpBuffer, 64, 2, false, radius);
        }

        RenderSystem.setShader(ClinkerShaders::getCloudShader);
        RenderSystem.setShaderTexture(0, NOISE_TEXTURE);
        poseStack.pushPose();
        poseStack.translate(0, -camY, 0);

        ShaderInstance shader = RenderSystem.getShader();

        float transitionLerp = Mth.clamp(
                MathUtil.mapRange(CLOUDS_START, CLOUDS_START + CLOUD_LAYER_THICKNESS, 0.0F, 1.0F, (float)camY),
                0.0F, 1.0F);

        // render ordering magic
        if (Math.abs(camY - CLOUDS_START) < Math.abs(camY - CLOUDS_END)) {
            if (camY > CLOUDS_START) drawCloudLayer(CLOUDS_END, radius, CLOUD_LAYER_THICKNESS * 0.5F, false, skyColor, transitionLerp, camX, camY, camZ, poseStack, projectionMatrix, shader);
            if (camY < CLOUDS_END + CLOUD_LAYER_THICKNESS) drawCloudLayer(CLOUDS_START, radius, CLOUD_LAYER_THICKNESS, true, skyColor, transitionLerp, camX, camY, camZ, poseStack, projectionMatrix, shader);
        } else {
            if (camY < CLOUDS_END + CLOUD_LAYER_THICKNESS) drawCloudLayer(CLOUDS_START, radius, CLOUD_LAYER_THICKNESS, true, skyColor, transitionLerp, camX, camY, camZ, poseStack, projectionMatrix, shader);
            if (camY > CLOUDS_START) drawCloudLayer(CLOUDS_END, radius, CLOUD_LAYER_THICKNESS * 0.5F, false, skyColor, transitionLerp, camX, camY, camZ, poseStack, projectionMatrix, shader);
        }

        // todo:
        // increase fog surrounding player when inside cloud layer
        // create a little sphere around the player (at fog distance) to blot surroundings

        poseStack.popPose();
    }

    private void drawCloudLayer(float height, float radius, float thickness, boolean lower, Vector3fc skyColor, float fadeLerp,
                                double camX, double camY, double camZ,
                                PoseStack poseStack, Matrix4f projectionMatrix, ShaderInstance shader) {
        int layers = 16;
        float layerThickness = (thickness / layers);
        float[] fogColors = RenderSystem.getShaderFogColor();

        if (camY < height + layers * layerThickness) {
            for (int i = layers - 1; i >= 0; i--) {
                poseStack.pushPose();
                float y = height + i* layerThickness;
                poseStack.translate(0, y, 0);
                poseStack.scale(1, layerThickness, 1);

                setShaderUniform(shader, "SkyColor",
                        Mth.lerp(fadeLerp, skyColor.x() * 0.8F, fogColors[0]),
                        Mth.lerp(fadeLerp, skyColor.y() * 0.8F, fogColors[1]),
                        Mth.lerp(fadeLerp, skyColor.z() * 0.8F, fogColors[2]),
                        1.0F);
                setShaderUniform(shader, "FogColor", fogColors);
                setShaderUniform(shader, "UVOffset", (float)((camX * 0.5F)), (float)((camZ * 0.5F)));
                float depth = (float) i / (layers - 2);
                if (!lower) depth = 1.0F - depth;
                setShaderUniform(shader, "Depth", depth);
                setShaderUniform(shader, "Radius", radius);
                setShaderUniform(shader, "Facing", lower ? 0.0F : 0.4F);

                if (y + layerThickness > camY) {
                    cloudLayerDownBuffer.bind();
                    cloudLayerDownBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shader);
                    VertexBuffer.unbind();
                }

                poseStack.popPose();
            }
        }

        if (camY > height) {
            for (int i = 0; i < layers; i++) {
                poseStack.pushPose();
                float y = height + i* layerThickness;
                poseStack.translate(0, y, 0);
                poseStack.scale(1, layerThickness, 1);

                setShaderUniform(shader, "SkyColor",
                        Mth.lerp(fadeLerp, skyColor.x() * 0.8F, fogColors[0]),
                        Mth.lerp(fadeLerp, skyColor.y() * 0.8F, fogColors[1]),
                        Mth.lerp(fadeLerp, skyColor.z() * 0.8F, fogColors[2]),
                        1.0F);
                setShaderUniform(shader, "FogColor", fogColors);
                setShaderUniform(shader, "UVOffset", (float)((camX * 0.5F)), (float)((camZ * 0.5F)));
                float depth = (float) i / (layers - 2);
                if (!lower) depth = 1.0F - depth;
                setShaderUniform(shader, "Depth", depth);
                setShaderUniform(shader, "Radius", radius);
                setShaderUniform(shader, "Facing", lower ? 0.0F : 0.4F);

                if (y - layerThickness < camY){
                    cloudLayerUpBuffer.bind();
                    cloudLayerUpBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shader);
                    VertexBuffer.unbind();
                }

                poseStack.popPose();
            }
        }
    }

    private VertexBuffer buildCloudBuffer(VertexBuffer vbo, int resolution, int layers, boolean down, float radius) {
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        for (int i = 0; i < layers; i++) {
            float offset = (float) i / layers;
            if (down) {
                buildDisc(bufferBuilder, resolution, radius, (1.0F - offset),
                        1.0F, 1.0F, 1.0F, (1.0F - offset), true);
            } else {
                buildDisc(bufferBuilder, resolution, radius, offset,
                        1.0F, 1.0F, 1.0F, offset, false);
            }
        }

        MeshData renderedBuffer = bufferBuilder.buildOrThrow();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }

    private void buildDisc(BufferBuilder bufferBuilder, int resolution, float radius, float height, float r, float g, float b, float a, boolean winding) {
        for (int i = 0; i < resolution; i++) {
            float f1 = i / (float) resolution;
            float x1 = Mth.sin(f1 * Mth.TWO_PI);
            float z1 = Mth.cos(f1 * Mth.TWO_PI);

            float f2 = (i + 1) / (float) resolution;
            float x2 = Mth.sin(f2 * Mth.TWO_PI);
            float z2 = Mth.cos(f2 * Mth.TWO_PI);

            float windingMultiplier = winding ? 1.0F : -1.0F;

            bufferBuilder.addVertex(x1 * radius, height, z1 * radius * windingMultiplier)
                    .setUv((x1 + 1.0F) / 2.0F, (z1 * windingMultiplier + 1.0F) / 2.0F)
                    .setColor(r, g, b, a);
            bufferBuilder.addVertex(0.0F, height, 0.0F)
                    .setUv(0.5F, 0.5F)
                    .setColor(r, g, b, a);
            bufferBuilder.addVertex(x2 * radius, height, z2 * radius * windingMultiplier)
                    .setUv((x2 + 1.0F) / 2.0F, (z2 * windingMultiplier + 1.0F) / 2.0F)
                    .setColor(r, g, b, a);
        }
    }

    private static void setShaderUniform(ShaderInstance shader, String name, float... values) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(values);
    }
}
