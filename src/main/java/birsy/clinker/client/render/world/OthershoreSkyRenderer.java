package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class OthershoreSkyRenderer {
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");
    private static final ResourceLocation NOISE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/noise.png");
    private static final ResourceLocation CLOUD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/cloud_map_a.png");
    private static final ResourceLocation FOG_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/fog.png");

    private VertexBuffer outerSkyBuffer;
    private VertexBuffer cloudRingBuffer;
    private VertexBuffer upperStarBuffer;
    private VertexBuffer[] floatingStarBuffers;
    private final RandomSource random;

    public OthershoreSkyRenderer(RandomSource random) {
        RenderSystem.assertOnRenderThread();
        this.random = random;
        this.buildVBOs();
    }

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix) {
        Vec3 skyColor = level.getSkyColor(camera.getPosition(), partialTick);
        float[] fogColor = RenderSystem.getShaderFogColor();

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, CLOUD_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        outerSkyBuffer.bind();
        outerSkyBuffer.drawWithShader(matrix, projectionMatrix, RenderSystem.getShader());
        poseStack.popPose();
    }

    private void buildVBOs() {
        this.outerSkyBuffer = this.buildOuterSkyBuffer(this.outerSkyBuffer, 64);
        this.cloudRingBuffer = this.buildCloudBuffer(this.cloudRingBuffer, 64);

        int ringCount = 24;
        this.floatingStarBuffers = new VertexBuffer[ringCount];
        for (int i = 0; i < ringCount; i++) {
            float factor = i / (float) ringCount;
            this.floatingStarBuffers[i] = buildStarBuffer(this.floatingStarBuffers[i], factor, factor + (1.0F / ringCount), 1500 / ringCount);
        }
    }
    private VertexBuffer buildStarBuffer(VertexBuffer vbo, float rangeStart, float rangeEnd, int count) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        float maxStarDistance = 300.0F;
        float minStarDistance = 30.0F;
        float minStarRadius = 0.1F;
        float maxStarRadius = 1.8F;

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        Matrix4f matrix = new Matrix4f();
        for (int i = 0; i < count; i++) {
            matrix.identity().rotateLocalY(random.nextFloat() * Mth.TWO_PI);
            float distanceFactor = Mth.lerp(random.nextFloat(), rangeStart, rangeEnd);

            float distance = Mth.lerp(distanceFactor, minStarDistance, maxStarDistance);
            float radius = Mth.lerp(distanceFactor, minStarRadius, maxStarRadius) * random.nextFloat();
            float height = (float) random.nextGaussian() * 15.0F;

            boolean fancy = random.nextInt(10) == 0;
            float u1 = fancy ? 0.5F : 0.0F, v1 = 0.0F;
            float u2 = fancy ? 1.0F : 0.5F, v2 = 1.0F;

            float rotationSpeedMultiplier = distanceFactor * 0.08F;
            float gradientPos = random.nextFloat();

            bufferBuilder.vertex(matrix, -radius, height - radius, distance)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .uv(u1, v1).endVertex();
            bufferBuilder.vertex(matrix, -radius, height + radius, distance)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .uv(u2, v1).endVertex();
            bufferBuilder.vertex(matrix,  radius, height + radius, distance)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .uv(u2, v2).endVertex();
            bufferBuilder.vertex(matrix,  radius, height - radius, distance)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .uv(u1, v2).endVertex();
        }
        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildCloudBuffer(VertexBuffer vbo, int resolution) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (int i = 0; i < resolution; i++) {
            float f1 = (i / (float) resolution);
            float x1 = Mth.sin(f1 * Mth.TWO_PI);
            float z1 = Mth.cos(f1 * Mth.TWO_PI);

            float f2 = (i + 1 / (float) resolution);
            float x2 = Mth.sin(f2 * Mth.TWO_PI);
            float z2 = Mth.cos(f2 * Mth.TWO_PI);

            bufferBuilder.vertex(x1,-0.5, z1).uv(f1, 0.0F).endVertex();
            bufferBuilder.vertex(x1, 0.5, z1).uv(f1, 1.0F).endVertex();
            bufferBuilder.vertex(x2, 0.5, z2).uv(f2, 1.0F).endVertex();
            bufferBuilder.vertex(x2,-0.5, z2).uv(f2, 0.0F).endVertex();
        }
        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildOuterSkyBuffer(VertexBuffer vbo, int resolution) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
        // r = fog color / sky color
        // g = is part of the cloud coverage
        // b = is part of the upper clouds
        // a = alpha

        // sky
        buildCone(bufferBuilder, resolution, 1.0F, 1.0F, 0.0F,
                1.0F, 0.0F, 0.0F, 1.0F,
                0.0F, 0.0F, 0.0F, 1.0F);
        buildCone(bufferBuilder, resolution, 1.0F, -1.0F, 0.0F,
                1.0F, 0.0F, 0.0F, 1.0F,
                0.0F, 0.0F, 0.0F, 1.0F);
        // cloud coverage
        buildCone(bufferBuilder, resolution, 1.0F, 1.0F, 0.5F,
                1.0F, 1.0F, 0.0F, 0.0F,
                1.0F, 0.0F, 0.0F, 1.0F);
        buildCone(bufferBuilder, resolution, 1.0F, -1.0F, -0.5F,
                1.0F, 1.0F, 0.0F, 0.0F,
                1.0F, 0.0F, 0.0F, 1.0F);
        // upper clouds
        buildCone(bufferBuilder, resolution, 1.0F, 1.0F, 0.0F,
                1.0F, 0.0F, 1.0F, 1.0F,
                0.0F, 0.0F, 1.0F, 1.0F);

        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private void buildCone(BufferBuilder bufferBuilder, int resolution, float radius, float height, float ringHeight, float ringR, float ringG, float ringB, float ringA, float topR,  float topG,  float topB,  float topA) {
        for (int i = 0; i < resolution; i++) {
            float f1 = (i / (float) resolution);
            float x1 = Mth.sin(f1 * Mth.TWO_PI);
            float z1 = Mth.cos(f1 * Mth.TWO_PI);

            float f2 = (i + 1 / (float) resolution);
            float x2 = Mth.sin(f2 * Mth.TWO_PI);
            float z2 = Mth.cos(f2 * Mth.TWO_PI);

            float windingMultiplier = height > ringHeight ? 1.0F : -1.0F;

            bufferBuilder.vertex( x1 * radius,0.0, z1 * radius * windingMultiplier)
                    .uv((x1 + 1.0F) / 2.0F, (z1 * windingMultiplier + 1.0F) / 2.0F)
                    .color(ringR, ringG, ringB, ringA)
                    .endVertex();
            bufferBuilder.vertex( 0.0, height, 0.0)
                    .uv(0.5F, 0.5F)
                    .color(topR, topG, topB, topA)
                    .endVertex();
            bufferBuilder.vertex( x2 * radius, 0.0, z2 * radius * windingMultiplier)
                    .color(ringR, ringG, ringB, ringA)
                    .uv((x2 + 1.0F) / 2.0F, (z2 * windingMultiplier + 1.0F) / 2.0F)
                    .endVertex();
        }
    }
}
