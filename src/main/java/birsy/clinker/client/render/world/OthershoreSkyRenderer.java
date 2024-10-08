package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@OnlyIn(Dist.CLIENT)
public class OthershoreSkyRenderer {
    private static final ResourceLocation NOISE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/noise.png");
    private static final ResourceLocation SKY_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/sky.png");
    private static final ResourceLocation OUTER_CLOUD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/outer_clouds.png");
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");
    private static final ResourceLocation STAR_COLOR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star_color.png");

    private VertexBuffer outerSkyBuffer;
    private VertexBuffer outerCloudsBuffer;
    private VertexBuffer outerStarsBuffer;

    private VertexBuffer cloudRingBuffer;
    private VertexBuffer[] floatingStarBuffers;
    private final RandomSource random;


    public OthershoreSkyRenderer(RandomSource random) {
        RenderSystem.assertOnRenderThread();
        this.random = random;
        this.buildVBOs();
    }

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix) {
        Minecraft mc = Minecraft.getInstance();

        Matrix4f projMatrix = new Matrix4f(projectionMatrix);
        RenderSystem.setProjectionMatrix(projMatrix.setPerspective(
                (float)(mc.gameRenderer.getFov(camera, partialTick, true) * (float) (Math.PI / 180.0)),
                (float)mc.getWindow().getWidth() / (float)mc.getWindow().getHeight(),
                0.05F,
                5000.0F), VertexSorting.DISTANCE_TO_ORIGIN);

        buildVBOs();
        Vec3 cameraPos = camera.getPosition();
        Vector3fc skyColor = this.getSkyColor(level, cameraPos, partialTick);
        float[] fogColor = RenderSystem.getShaderFogColor();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        poseStack.pushPose();

        // account for view bobbing
        if (mc.options.bobView().get() && mc.getCameraEntity() instanceof Player) {
            Player player = (Player) mc.getCameraEntity();
            float playerStep = player.walkDist - player.walkDistO;
            float stepSize = -(player.walkDist + playerStep * partialTick);
            float viewBob = Mth.lerp(partialTick, player.oBob, player.bob);

            Quaternionf cameraRotation = camera.rotation();
            Vector3f xAxis = cameraRotation.transform(new Vector3f(1, 0, 0));
            Vector3f ZAxis = cameraRotation.transform(new Vector3f(0, 0, 1));

            Quaternionf bobXRotation = new Quaternionf().rotateAxis((float) Math.toRadians(Math.abs(Mth.cos(stepSize * (float) Math.PI - 0.2f) * viewBob) * 5f), xAxis);
            Quaternionf bobZRotation = new Quaternionf().rotateAxis((float) Math.toRadians(Mth.sin(stepSize * (float) Math.PI) * viewBob * 3f), ZAxis);
            poseStack.mulPose(bobXRotation.conjugate());
            poseStack.mulPose(bobZRotation.conjugate());
            poseStack.translate(Mth.sin(stepSize * (float) Math.PI) * viewBob * 0.5f, Math.abs(Mth.cos(stepSize * (float) Math.PI) * viewBob), 0f);
        }

        float scale = 3000.0F;
        poseStack.scale(scale, scale, scale);

        cameraPos = cameraPos.scale(1.0 / scale);

        drawOuterSky(level, ticks, partialTick, projMatrix, poseStack,
                (float) cameraPos.x, (float) cameraPos.z,
                fogColor[0], fogColor[1], fogColor[2],
                skyColor.x(), skyColor.y(), skyColor.z());

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorting.DISTANCE_TO_ORIGIN);
    }

    private void drawOuterSky(ClientLevel level, int ticks, float partialTick, Matrix4f projMatrix, PoseStack poseStack,
                              float camX, float camZ,
                              float fogR, float fogG, float fogB,
                              float skyR, float skyG, float skyB) {
        float time = (ticks + partialTick);


        // outer sky
        RenderSystem.setShader(ClinkerShaders::getSkyOuterShader);
        RenderSystem.setShaderTexture(0,SKY_TEXTURE);

        ShaderInstance outerSkyShader = RenderSystem.getShader();
        setShaderUniform(outerSkyShader, "WiggleTime", time * 0.01F);
        setShaderUniform(outerSkyShader, "SkyColor", skyR * 0.8F, skyG * 0.8F, skyB * 0.8F, 1.0F);
        setShaderUniform(outerSkyShader, "FogColor", fogR, fogG, fogB, 1.0F);

        outerSkyBuffer.bind();
        outerSkyBuffer.drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();


        // outer stars
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees((level.getGameTime() + partialTick) * 0.01F));
        RenderSystem.setShader(ClinkerShaders::getSkyOuterStarShader);
        RenderSystem.setShaderTexture(0, STAR_TEXTURE);
        RenderSystem.setShaderTexture(1, STAR_COLOR_TEXTURE);

        ShaderInstance outerStarShader = RenderSystem.getShader();
        setShaderUniform(outerStarShader, "TwinkleTime", time * 0.1F);

        outerStarsBuffer.bind();
        outerStarsBuffer.drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();
        poseStack.popPose();


        // outer sky clouds
        RenderSystem.setShader(ClinkerShaders::getSkyOuterCloudShader);
        RenderSystem.setShaderTexture(0,OUTER_CLOUD_TEXTURE);

        ShaderInstance outerCloudShader = RenderSystem.getShader();
        setShaderUniform(outerCloudShader, "SkyColor", skyColor.x(), skyColor.y(), skyColor.z(), 1.0F);
        setShaderUniform(outerSkyShader, "FogColor", fogR, fogG, fogB, 1.0F);
        setShaderUniform(outerCloudShader, "WindOffset", 0.5F * time * 0.00002F + camX, time * 0.00002F + camZ);

        outerCloudsBuffer.bind();
        outerCloudsBuffer.drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();
    }

    private void drawCloudRings(ClientLevel level, int ticks, float partialTick, Matrix4f projMatrix, PoseStack poseStack,
                                float camY,
                                float fogR, float fogG, float fogB,
                                float skyR, float skyG, float skyB) {
//        for (int ring = 24; ring >= 0; ring--) {
//            poseStack.pushPose();
//            float ringDist = (float)ring / (float)ringNum;
//            ringDist *= ringDist;
//
//            float rRadius = Mth.lerp(ringDist, minRadius, maxRadius);
//            float smoothRingDist = MathUtils.ease(ringDist, MathUtils.EasingType.easeOutCirc);//ringDist * ringDist * (3.0F - 2.0F * ringDist);
//           // smoothRingDist = smoothRingDist * smoothRingDist * (3.0F - 2.0F * smoothRingDist);
//
//            Vector3f ringColor = new Vector3f(trueFogColor.x(), trueFogColor.y(), trueFogColor.z());
//            ringColor.lerp(fogColor, smoothRingDist * 0.7F);
//            ringColor.lerp(new Vector3f((float) skyColor.x(), (float) skyColor.y(), (float) skyColor.z()), smoothRingDist);
//            ringColor.lerp(trueFogColor, (1 - aboveCloudAlphaOffset) * 0.8F);
//
//            RenderSystem.setShaderGameTime(ticks, partialTick);
//            poseStack.pushPose();
//            poseStack.translate(0, (ringHeight / 5), 0);
//            poseStack.scale(6.25F, 6.25F, 6.25F);
//
//            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
//            RenderSystem.setShaderTexture(0, STAR_TEXTURE);
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//            for (int starIndex = starStartIndex; starIndex >= 0; starIndex--) {
//                Star star = stars.get(starIndex);
//
//                if (star.distance > rRadius / 6.25F) {
//                    renderStar(poseStack, bufferbuilder, star, lightFactor, time, 0.08F, true);
//                } else {
//                    starStartIndex = starIndex;
//                    break;
//                }
//
//            }
//
//            poseStack.popPose();
//
//            RenderSystem.setShaderTexture(0, NOISE_TEXTURE);
//            renderCloudRing(poseStack, bufferbuilder, ringResolution, rRadius, ringHeight, 20.0F, 0.0F, ringDist, ringColor.x(), ringColor.y(), ringColor.z(), alpha * 0.8F, ringColor.x(), ringColor.y(), ringColor.z(), 1.0F);
//            poseStack.popPose();
//        }
    }

    private Vector3f skyColor = new Vector3f();
    public Vector3fc getSkyColor(ClientLevel level, Vec3 pPos, float pPartialTick) {
        Vec3 pos = pPos.subtract(2.0, 2.0, 2.0).scale(0.25);
        BiomeManager biomemanager = level.getBiomeManager();
        Vec3 interpolatedSkyColor = CubicSampler.gaussianSampleVec3(
                pos, (x, y, z) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).value().getSkyColor())
        );
        float r = (float)interpolatedSkyColor.x;
        float g = (float)interpolatedSkyColor.y;
        float b = (float)interpolatedSkyColor.z;

        int i = level.getSkyFlashTime();
        if (i > 0) {
            float lightningFlicker = i - pPartialTick;
            if (lightningFlicker > 1.0F) lightningFlicker = 1.0F;

            lightningFlicker *= 0.45F;
            r = r * (1.0F - lightningFlicker) + 0.8F * lightningFlicker;
            g = g * (1.0F - lightningFlicker) + 0.8F * lightningFlicker;
            b = b * (1.0F - lightningFlicker) + lightningFlicker;
        }

        return skyColor.set(r, g, b);
    }

    private void buildVBOs() {
        this.outerSkyBuffer = this.buildOuterSkyBuffer(this.outerSkyBuffer, 32);
        this.outerStarsBuffer = this.buildOuterStarsBuffer(this.outerStarsBuffer, 2048);
        this.outerCloudsBuffer = this.buildOuterCloudsBuffer(this.outerCloudsBuffer, 32, 32);
        this.cloudRingBuffer = this.buildCloudBuffer(this.cloudRingBuffer, 64);

        int ringCount = 24;
        //this.floatingStarBuffers = new VertexBuffer[ringCount];
        for (int i = 0; i < ringCount; i++) {
            float factor = i / (float) ringCount;
            //this.floatingStarBuffers[i] = buildStarBuffer(this.floatingStarBuffers[i], factor, factor + (1.0F / ringCount), 1500 / ringCount);
        }
    }
    private VertexBuffer buildStarBuffer(VertexBuffer vbo, float rangeStart, float rangeEnd, int count) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        float maxStarDistance = 300.0F;
        float minStarDistance = 30.0F;
        float minStarRadius = 0.1F;
        float maxStarRadius = 1.8F;

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Matrix4f matrix = new Matrix4f();
        for (int i = 0; i < count; i++) {
            matrix.identity()
                    .rotateLocalZ(random.nextFloat() * Mth.TWO_PI)
                    .rotateY(random.nextFloat() * Mth.TWO_PI);
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
                    .uv(u1, v1)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .endVertex();
            bufferBuilder.vertex(matrix, -radius, height + radius, distance)
                    .uv(u2, v1)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .endVertex();
            bufferBuilder.vertex(matrix,  radius, height + radius, distance)
                    .uv(u2, v2)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .endVertex();
            bufferBuilder.vertex(matrix,  radius, height - radius, distance)
                    .uv(u1, v2)
                    .color(rotationSpeedMultiplier, gradientPos, 1.0F, 1.0F)
                    .endVertex();
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
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
        // r = fog color / sky color
        // g = use texture
        // sky
        buildCone(bufferBuilder, resolution, 1.0F, 1.0F, 0.0F,
                1.0F, 0.0F, 0.0F, 1.0F,
                0.0F, 1.0F, 0.0F, 1.0F);
        buildCone(bufferBuilder, resolution, 1.0F, -1.0F, 0.0F,
                1.0F, 0.0F, 0.0F, 1.0F,
                0.0F, 0.0F, 0.0F, 1.0F);

        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildOuterCloudsBuffer(VertexBuffer vbo, int resolution, int layerCount) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (int i = 0; i < layerCount; i++) {
            float factor = (float) i / layerCount;
            float height = Mth.lerp(factor, 0.7F, 0.5F);
            buildCone(bufferBuilder, resolution, 1.0F, height/5.0F, (height - 0.5F)/5.0F,
                    0.0F,1.0F,1.0F, factor,
                     0.02F, 1.0F, 1.0F, factor);
        }

        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildOuterStarsBuffer(VertexBuffer vbo, int count) {
        RandomSource random = RandomSource.create(1337);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        float minStarRadius = 1.0F/150.0F;
        float maxStarRadius = 3.0F/150.0F;

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Matrix4f matrix = new Matrix4f();
        for (int i = 0; i < count; i++) {
            matrix.identity()
                    .rotateLocalY(random.nextFloat() * Mth.TWO_PI)
                    .rotateX(random.nextFloat() * Mth.TWO_PI)
                    .rotateZ(random.nextFloat() * Mth.TWO_PI);
            float distanceFactor = random.nextFloat();

            float distance = 1.0F;
            float radius = Mth.lerp(distanceFactor, minStarRadius, maxStarRadius) * random.nextFloat();

            float fancy = random.nextInt(10) == 0 ? 1.0F : 0.0F;
            float u1 = 0.0F, v1 = 0.0F;
            float u2 = 1.0F, v2 = 1.0F;

            float gradientPos = random.nextFloat();
            float brightness = distanceFactor * distanceFactor;
            float twinkleOffset = random.nextFloat();
            bufferBuilder.vertex(matrix, - radius, - radius, distance)
                    .uv(u1, v1)
                    .color(brightness, gradientPos, twinkleOffset, fancy)
                    .endVertex();
            bufferBuilder.vertex(matrix, - radius, + radius, distance)
                    .uv(u2, v1)
                    .color(brightness, gradientPos, twinkleOffset, fancy)
                    .endVertex();
            bufferBuilder.vertex(matrix, + radius, + radius, distance)
                    .uv(u2, v2)
                    .color(brightness, gradientPos, twinkleOffset, fancy)
                    .endVertex();
            bufferBuilder.vertex(matrix, + radius, - radius, distance)
                    .uv(u1, v2)
                    .color(brightness, gradientPos, twinkleOffset, fancy)
                    .endVertex();
        }
        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }

    private void buildCone(BufferBuilder bufferBuilder, int resolution, float radius, float height, float ringHeight, float ringR, float ringG, float ringB, float ringA, float topR,  float topG,  float topB,  float topA) {
        for (int i = 0; i < resolution; i++) {
            float f1 = i / (float) resolution;
            float x1 = Mth.sin(f1 * Mth.TWO_PI);
            float z1 = Mth.cos(f1 * Mth.TWO_PI);

            float f2 = (i + 1) / (float) resolution;
            float x2 = Mth.sin(f2 * Mth.TWO_PI);
            float z2 = Mth.cos(f2 * Mth.TWO_PI);

            float windingMultiplier = height >= ringHeight ? 1.0F : -1.0F;

            bufferBuilder.vertex(x1 * radius, ringHeight, z1 * radius * windingMultiplier)
                    .uv((x1 + 1.0F) / 2.0F, (z1 * windingMultiplier + 1.0F) / 2.0F)
                    .color(ringR, ringG, ringB, ringA)
                    .endVertex();
            bufferBuilder.vertex(0.0, height, 0.0)
                    .uv(0.5F, 0.5F)
                    .color(topR, topG, topB, topA)
                    .endVertex();
            bufferBuilder.vertex(x2 * radius, ringHeight, z2 * radius * windingMultiplier)
                    .uv((x2 + 1.0F) / 2.0F, (z2 * windingMultiplier + 1.0F) / 2.0F)
                    .color(ringR, ringG, ringB, ringA)
                    .endVertex();
        }
    }

    private static void setShaderUniform(ShaderInstance shader, String name, float... values) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(values);
    }
}
