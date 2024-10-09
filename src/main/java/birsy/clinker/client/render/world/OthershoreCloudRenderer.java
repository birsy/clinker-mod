package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@OnlyIn(Dist.CLIENT)
public class OthershoreCloudRenderer {
    private static final ResourceLocation NOISE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/noise.png");

    private VertexBuffer cloudLayerDownBuffer;
    private VertexBuffer cloudLayerUpBuffer;


    public OthershoreCloudRenderer() {

    }

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
        this.cloudLayerDownBuffer = buildCloudBuffer(cloudLayerDownBuffer, 64, 16, true, radius);
        this.cloudLayerUpBuffer = buildCloudBuffer(cloudLayerUpBuffer, 64, 16, false, radius);

        RenderSystem.setShader(ClinkerShaders::getCloudShader);
        RenderSystem.setShaderTexture(0, NOISE_TEXTURE);
        poseStack.pushPose();

        ShaderInstance shader = RenderSystem.getShader();

        poseStack.translate(0, -camY, 0);
        int layers = 16;
        float thickness = 3F;
        if (camY < 250 + layers*thickness) {
            for (int i = layers - 1; i >= 0; i--) {
                poseStack.pushPose();
                float y = 250 + i*thickness;
                poseStack.translate(0, y, 0);
                poseStack.scale(1, thickness, 1);

                setShaderUniform(shader, "SkyColor", skyColor.x() * 0.8F, skyColor.y() * 0.8F, skyColor.z() * 0.8F, 1.0F);
                setShaderUniform(shader, "FogColor", RenderSystem.getShaderFogColor());
                setShaderUniform(shader, "UVOffset", (float)((camX * 0.5F) % radius), (float)((camZ * 0.5F) % radius));
                setShaderUniform(shader, "Depth", (float) i / (layers - 2));
                setShaderUniform(shader, "Radius", radius);

                if (y + thickness > camY) {
                    cloudLayerDownBuffer.bind();
                    cloudLayerDownBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shader);
                    VertexBuffer.unbind();
                }

                poseStack.popPose();
            }
        }
        if (camY > 250) {
            for (int i = 0; i < layers; i++) {
                poseStack.pushPose();
                float y = 250 + i*thickness;
                poseStack.translate(0, y, 0);
                poseStack.scale(1, thickness, 1);

                setShaderUniform(shader, "FogColor", RenderSystem.getShaderFogColor());
                setShaderUniform(shader, "UVOffset", (float)(((camX * 0.5F)/radius) % 1), (float)(((camZ * 0.5F)/radius) % 1));
                setShaderUniform(shader, "Depth", (float) i / (layers - 2));
                setShaderUniform(shader, "Radius", radius);

                if (y - thickness < camY){
                    cloudLayerUpBuffer.bind();
                    cloudLayerUpBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shader);
                    VertexBuffer.unbind();
                }

                poseStack.popPose();
            }
        }


        poseStack.popPose();
    }

    private VertexBuffer buildCloudBuffer(VertexBuffer vbo, int resolution, int layers, boolean down, float radius) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
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
        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();

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

            bufferBuilder.vertex(x1 * radius, height, z1 * radius * windingMultiplier)
                    .uv((x1 + 1.0F) / 2.0F, (z1 * windingMultiplier + 1.0F) / 2.0F)
                    .color(r, g, b, a)
                    .endVertex();
            bufferBuilder.vertex(0.0, height, 0.0)
                    .uv(0.5F, 0.5F)
                    .color(r, g, b, a)
                    .endVertex();
            bufferBuilder.vertex(x2 * radius, height, z2 * radius * windingMultiplier)
                    .uv((x2 + 1.0F) / 2.0F, (z2 * windingMultiplier + 1.0F) / 2.0F)
                    .color(r, g, b, a)
                    .endVertex();
        }
    }

    private static void setShaderUniform(ShaderInstance shader, String name, float... values) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(values);
    }
}
