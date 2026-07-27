package birsy.clinker.client.render.world.sky;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.client.render.utilities.MeshHelper;
import birsy.clinker.client.render.world.cloud.ChunkedUpperLayerCloudRenderer;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

public class OthershoreSkyRenderer2 {
    private boolean initialized = false;
    private VertexBuffer outerAtmosphereVbo, outerCloudsVbo, outerStarsVbo, outerSkyMaskVbo, outerSkyMaskGradientVbo;

    void initialize(int renderDistanceInBlocks) {
        Tesselator tesselator = Tesselator.getInstance();
        RandomSource random = RandomSource.create(0);
        Matrix4f identity = new Matrix4f().identity();

        // outer atmosphere
        {
            if (outerAtmosphereVbo != null) outerAtmosphereVbo.close();
            outerAtmosphereVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerAtmosphereVbo.bind();
            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            MeshHelper.consumeSphereSegment(vertexConsumer, identity, 1.0F, false, true,
                    16, 16,
                    0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                    Mth.HALF_PI, 1.0F, 1.0F, 1.0F, 1.0F);
            outerAtmosphereVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }

        // outer clouds sheet
        {
            if (outerCloudsVbo != null) outerCloudsVbo.close();
            outerCloudsVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerCloudsVbo.bind();
            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            MeshHelper.consumeSphereSegment(vertexConsumer, new Matrix4f().scale(1, 0.5F, 1), 1.0F, false, true,
                    8, 16,
                    0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                    Mth.HALF_PI, 1.0F, 1.0F, 1.0F, 1.0F);
            outerCloudsVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }

        // outer stars buffer
        {
            if (outerStarsVbo != null) outerStarsVbo.close();
            outerStarsVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerStarsVbo.bind();

            int starCount = 10000;
            Quaternionf quaternion = new Quaternionf();
            Matrix4f matrix = new Matrix4f();
            Vector3f pos = new Vector3f();

            NormalNoise noise = NormalNoise.create(random, 2, 1.0, 0.1);

            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            for (int i = 0; i < starCount; i++) {
                float dirX = (float) random.nextGaussian(), dirY = (float) random.nextGaussian(), dirZ = (float) random.nextGaussian();
                float length = (float) Mth.length(dirX, dirY, dirZ);
                dirX /= length; dirY /= length; dirZ /= length;

                // random rotation
                quaternion.rotationTo(0, 0, 1, dirX, dirY, dirZ);
                quaternion.rotateAxis(random.nextFloat() * Mth.TWO_PI, dirX, dirY, dirZ);
                matrix.rotation(quaternion);
                matrix.transformDirection(pos.set(0, 0, 1));

                float noiseSample = Mth.clampedMap((float) noise.getValue(pos.x, pos.y, pos.z),
                        -0.6F, 1.0F,
                        0.0F, 1.0F
                );

                float radius = Mth.lerp(random.nextFloat() * random.nextFloat() * random.nextFloat(), 0.01F, 0.05F);

                boolean fancyStar = random.nextInt(5) == 0;
                float temperature = random.nextFloat();

                float r = fancyStar ? 1.0F : 0.0F, g = temperature + 0.0F, b = random.nextFloat(), a = random.nextFloat() * noiseSample;
                if (a <= 0.01F) continue;

                vertexConsumer.addVertex(matrix,-1 * radius, -1 * radius, 1).setUv(0, 0).setColor(r, g, b, a);
                vertexConsumer.addVertex(matrix, 1 * radius, -1 * radius, 1).setUv(1, 0).setColor(r, g, b, a);
                vertexConsumer.addVertex(matrix, 1 * radius,  1 * radius, 1).setUv(1, 1).setColor(r, g, b, a);
                vertexConsumer.addVertex(matrix,-1 * radius,  1 * radius, 1).setUv(0, 1).setColor(r, g, b, a);
            }

            outerStarsVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }

        // outer sky mask
        {
            if (outerSkyMaskVbo != null) outerSkyMaskVbo.close();
            outerSkyMaskVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerSkyMaskVbo.bind();
            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            MeshHelper.consumeCone(vertexConsumer, identity, 16,
                    1.0F, true,
                    -3.0F, 1.0F, 1.0F, 1.0F, 1.0F,
                    0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
            outerSkyMaskVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }

        // outer sky mask gradient
        {
            if (outerSkyMaskGradientVbo != null) outerSkyMaskGradientVbo.close();
            outerSkyMaskGradientVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerSkyMaskGradientVbo.bind();
            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            MeshHelper.consumeCone(vertexConsumer, identity, 16,
                    1.0F, true,
                    0.5F, 1.0F, 1.0F, 1.0F, 0.0F,
                    0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
            MeshHelper.consumeCone(vertexConsumer, identity, 16,
                    1.0F, true,
                    -3.0F, 1.0F, 1.0F, 1.0F, 1.0F,
                    0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
            outerSkyMaskGradientVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }
    }

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, Vector3fc skyColor) {
        double renderDistance = Minecraft.getInstance().levelRenderer.getLastViewDistance() * 16;
        if (!initialized) initialize((int) renderDistance);
        double camX = camera.getPosition().x, camY = camera.getPosition().y, camZ = camera.getPosition().z;

        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // "outer sky" blocker, when below the clouds
        float lowerCloudsBlockerAlpha = (float) Mth.clampedMap(camY,
                ChunkedUpperLayerCloudRenderer.LOWER_CLOUD_HEIGHT, ChunkedUpperLayerCloudRenderer.UPPER_CLOUD_HEIGHT,
                1.0F, 0.0F
        );
        poseStack.pushPose();
        float inverseRenderDistance = (1.0F / (float) renderDistance) * 0.5F;
        poseStack.scale(inverseRenderDistance, inverseRenderDistance, inverseRenderDistance);
        poseStack.translate(0, ChunkedUpperLayerCloudRenderer.CLOUD_HEIGHT, 0);
        poseStack.translate(0, -camY, 0);
        poseStack.scale((float) renderDistance, (float) renderDistance, (float) renderDistance);
        Matrix4f blockerTransform = new Matrix4f(poseStack.last().pose());
        poseStack.popPose();

        if (lowerCloudsBlockerAlpha >= 1.0F) {
            RenderSystem.colorMask(false, false, false, false);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();

            ShaderProgram outerSkyMaskShader = VeilRenderSystem.setShader(ClinkerShaders.SKY_OUTER_MASK);
            outerSkyMaskShader.bind();
            outerSkyMaskShader.bindSamplers(0);
            outerSkyMaskShader.setDefaultUniforms(VertexFormat.Mode.TRIANGLES, blockerTransform, projectionMatrix);
            outerSkyMaskShader.getUniform("AlphaMultiplier").setFloat(lowerCloudsBlockerAlpha);
            outerSkyMaskVbo.bind();
            outerSkyMaskVbo.drawWithShader(blockerTransform, projectionMatrix, VeilRenderBridge.toShaderInstance(outerSkyMaskShader));
            VertexBuffer.unbind();

            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.depthMask(false);
        }

        ShaderProgram outerAtmosphereShader = VeilRenderSystem.setShader(ClinkerShaders.SKY_OUTER_ATMOSPHERE);
        outerAtmosphereShader.bind();
        outerAtmosphereShader.bindSamplers(0);
        outerAtmosphereShader.setDefaultUniforms(VertexFormat.Mode.TRIANGLES, poseStack.last().pose(), projectionMatrix);
        outerAtmosphereShader.getUniformSafe("SkyColor").setVector(32/255.0F, 28/255.0F, 35/255.0F, 1.0F);
        outerAtmosphereShader.getUniformSafe("SkyFogColor").setVector(19/255.0F, 13/255.0F, 17/255.0F, 1.0F);

        outerAtmosphereVbo.bind();
        outerAtmosphereVbo.drawWithShader(poseStack.last().pose(), projectionMatrix, VeilRenderBridge.toShaderInstance(outerAtmosphereShader));
        VertexBuffer.unbind();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XN.rotationDegrees((ticks + partialTick) * 0.01F));
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ZERO,
                GlStateManager.DestFactor.ONE
        );
        ShaderProgram outerStarsShader = VeilRenderSystem.setShader(ClinkerShaders.SKY_OUTER_STARS);
        outerStarsShader.bind();
        outerStarsShader.bindSamplers(0);
        outerStarsShader.setDefaultUniforms(VertexFormat.Mode.QUADS, poseStack.last().pose(), projectionMatrix);
        outerStarsShader.getUniformSafe("TwinkleTime").setFloat((float) ((ticks + (double)partialTick) / 20.0));
        outerStarsVbo.bind();
        outerStarsVbo.drawWithShader(poseStack.last().pose(), projectionMatrix, VeilRenderBridge.toShaderInstance(outerStarsShader));
        VertexBuffer.unbind();

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        poseStack.popPose();

        if (lowerCloudsBlockerAlpha >= 0.0F) {
            RenderSystem.disableDepthTest();
            ShaderProgram outerSkyMaskShader = VeilRenderSystem.setShader(ClinkerShaders.SKY_OUTER_MASK);
            outerSkyMaskShader.bind();
            outerSkyMaskShader.bindSamplers(0);
            outerSkyMaskShader.setDefaultUniforms(VertexFormat.Mode.TRIANGLES, blockerTransform, projectionMatrix);
            outerSkyMaskShader.getUniformSafe("AlphaMultiplier").setFloat(lowerCloudsBlockerAlpha);
            outerSkyMaskGradientVbo.bind();
            outerSkyMaskGradientVbo.drawWithShader(blockerTransform, projectionMatrix, VeilRenderBridge.toShaderInstance(outerSkyMaskShader));
            VertexBuffer.unbind();
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);


        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false);
    }
}
