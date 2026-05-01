package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class OthershoreFogLayerRenderer {
    static final FogLayer[] layers = {
            new FogLayer(0.5F, 0.2F, 0, 0, 0, false)
    };

    @SubscribeEvent
    static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            double cameraY = event.getCamera().getPosition().y;
            Arrays.sort(layers, Comparator.comparingDouble((layer) -> -Math.abs(layer.height - cameraY)));

            float renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
            for (FogLayer layer : layers) {
                layer.render(renderDistance, event.getCamera(), event.getPoseStack(), event.getProjectionMatrix());
            }
        }
    }

    public static class FogLayer {
        static VertexBuffer vbo;
        final float height;
        final float density;
        final float r, g, b;
        final boolean affectedByFogColor;
        FogLayer(float height, float density, float r, float g, float b, boolean affectedByFogColor) {
            this.height = height;
            this.density = density;
            this.r = r;
            this.g = g;
            this.b = b;
            this.affectedByFogColor = affectedByFogColor;
        }

        VertexBuffer buildVBO(float radius, int resolution) {
            Tesselator tesselator = Tesselator.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);

            if (vbo != null) vbo.close();
            vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

            bufferBuilder.addVertex(0, 0, 0).setUv(0.5F, 0.5F);
            for (int i = 0; i <= resolution; i++) {
                float angle = ((float)i / resolution) * Mth.TWO_PI;
                float x = Mth.sin(angle), z = Mth.cos(angle);
                bufferBuilder.addVertex(x * radius, 0, z * radius)
                        .setUv(0.5F + (x * 0.5F), 0.5F + (z * 0.5F))
                        .setColor(1,1,1,1);
            }

            MeshData renderedBuffer = bufferBuilder.buildOrThrow();

            vbo.bind();
            vbo.upload(renderedBuffer);
            VertexBuffer.unbind();

            return vbo;
        }

        void render(float renderDistance, Camera camera, PoseStack poseStack, Matrix4f projectionMatrix) {
            if (vbo == null) buildVBO(1.0F, 16);

            poseStack.pushPose();

            float fogLayerHeight = Math.min(this.height, (float) camera.getPosition().y - 0.1F);
            float fogAlpha = Mth.clampedMap(fogLayerHeight, this.height, this.height - 20, 1, 0);
            fogAlpha *= fogAlpha;
            if (fogAlpha <= 0) return;

            poseStack.mulPose(camera.rotation().conjugate(new Quaternionf()));
            poseStack.translate(0, fogLayerHeight - camera.getPosition().y(), 0);
            poseStack.scale(renderDistance, 1, renderDistance);

            float[] fogColor = RenderSystem.getShaderFogColor();
            float red = r, green = g, blue = b;
            if (affectedByFogColor) {
                red *= fogColor[0];
                green *= fogColor[1];
                blue *= fogColor[2];
            }

            ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.FOG_LAYER);
            shader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_FAN);
            shader.getUniformSafe("MaxFogDistance")
                    .setFloat(renderDistance);
            shader.getUniformSafe("FogLayerHeight")
                    .setFloat(fogLayerHeight);
            shader.getUniformSafe("FogLayerColor")
                    .setVector(red, green, blue, 0.9F);
            shader.getUniformSafe("FogLayerDensity")
                    .setFloat(density * fogAlpha);

            shader.getUniformSafe("ScreenResolution")
                    .setVector(AdvancedFbo.getMainFramebuffer().getWidth(), AdvancedFbo.getMainFramebuffer().getHeight());

            vbo.bind();
            vbo.drawWithShader(poseStack.last().pose(), projectionMatrix, VeilRenderBridge.toShaderInstance(shader));
            VertexBuffer.unbind();
            RenderSystem.enableCull();

            poseStack.popPose();
        }
    }
}
