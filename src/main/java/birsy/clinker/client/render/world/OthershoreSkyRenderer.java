package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtil;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;


public class OthershoreSkyRenderer {
    private static final ResourceLocation NOISE_TEXTURE = Clinker.resource("textures/environment/noise.png");
    private static final ResourceLocation SKY_TEXTURE = Clinker.resource("textures/environment/sky.png");
    private static final ResourceLocation OUTER_CLOUD_TEXTURE = Clinker.resource("textures/environment/outer_clouds.png");
    private static final ResourceLocation STAR_TEXTURE = Clinker.resource("textures/environment/star.png");
    private static final ResourceLocation STAR_COLOR_TEXTURE = Clinker.resource("textures/environment/star_color.png");
    private static final ResourceLocation MATURE_STAR_COLOR_TEXTURE = Clinker.resource("textures/environment/star_color_mature.png");

    private VertexBuffer outerSkyBuffer;
    private VertexBuffer outerCloudsBuffer;
    private VertexBuffer outerStarsBuffer;

    private VertexBuffer cloudRingBuffer;
    private VertexBuffer[] floatingStarBuffers;

    private VertexBuffer horizonFogBuffer;

    private final RandomSource random;

    private static int RING_COUNT = 24;
    private static float RING_MIN_RADIUS = 192.0F;
    private static float RING_MAX_RADIUS = 3125.0F;
    private static float RING_HEIGHT = 500.0F;

    public OthershoreSkyRenderer(RandomSource random) {
        RenderSystem.assertOnRenderThread();
        this.random = random;
        this.buildVBOs();
    }

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, Vector3fc skyColor) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 cameraPos = camera.getPosition();

        Matrix4f projMatrix = new Matrix4f(projectionMatrix);
        RenderSystem.setProjectionMatrix(projMatrix.setPerspective(
                (float)(mc.gameRenderer.getFov(camera, partialTick, true) * (float) (Math.PI / 180.0)),
                (float)mc.getWindow().getWidth() / (float)mc.getWindow().getHeight(),
                0.05F,
                5000.0F), VertexSorting.DISTANCE_TO_ORIGIN);

        // rebuilds the star VBOs
        // runs only if the render distance changes
        buildStarVBOs();

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

        poseStack.pushPose();
        float scale = 4990.0F;
        poseStack.scale(scale, scale, scale);
        float aboveCloudsDarken = 0.7F;
        aboveCloudsDarken = Mth.lerp(
                Mth.clamp(MathUtil.mapRange(OthershoreCloudRenderer.CLOUDS_END - OthershoreCloudRenderer.CLOUD_LAYER_THICKNESS*0.5F, OthershoreCloudRenderer.CLOUDS_END, 0F, 1F, (float)cameraPos.y), 0, 1),
                1.0F,
                aboveCloudsDarken
        );
        RenderSystem.setShaderFogColor(fogColor[0] * aboveCloudsDarken, fogColor[1] * aboveCloudsDarken, fogColor[2] * aboveCloudsDarken);
        if (cameraPos.y > OthershoreCloudRenderer.CLOUDS_START + OthershoreCloudRenderer.CLOUD_LAYER_THICKNESS) {
            drawOuterSky(level, ticks, partialTick, projMatrix, poseStack,
                    (float) cameraPos.x / scale, (float) cameraPos.z / scale,
                    //fogColor[0], fogColor[1], fogColor[2],
                    32/255.0F, 28/255.0F, 35/255.0F,
                    //skyColor.x(), skyColor.y(), skyColor.z()
                    19/255.0F, 13/255.0F, 17/255.0F);
        }

        poseStack.popPose();

        drawCloudRings(level, ticks, partialTick, projMatrix, poseStack,
                (float) cameraPos.y,
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
        setShaderUniform(outerSkyShader, "SkyColor1", skyR, skyG, skyB, 1.0F);
        setShaderUniform(outerSkyShader, "SkyColor2", fogR, fogG, fogB, 1.0F);

        outerSkyBuffer.bind();
        outerSkyBuffer.drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();


        // outer stars
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees((level.getGameTime() + partialTick) * 0.01F));
        RenderSystem.setShader(ClinkerShaders::getSkyOuterStarShader);
        RenderSystem.setShaderTexture(0, STAR_TEXTURE);
        RenderSystem.setShaderTexture(1, MATURE_STAR_COLOR_TEXTURE);

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
        setShaderUniform(outerCloudShader, "SkyColor1", skyR, skyG, skyB, 1.0F);
        setShaderUniform(outerCloudShader, "SkyColor2", fogR, fogG, fogB, 1.0F);
        setShaderUniform(outerCloudShader, "WindOffset", 0.5F * time * 0.00002F + camX, time * 0.00002F + camZ);

        outerCloudsBuffer.bind();
        outerCloudsBuffer.drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();
    }

    private void drawCloudRings(ClientLevel level, int ticks, float partialTick, Matrix4f projMatrix, PoseStack poseStack,
                                float camY,
                                float fogR, float fogG, float fogB,
                                float skyR, float skyG, float skyB) {
        float aboveCloudAlphaOffset = Mth.clamp(MathUtil.mapRange(400F, 450F, 1.0F, 0.0F, camY), 0.0F, 1.0F);


        float time = ticks + partialTick;

        float cloudR = 68.0F / 256.0F, cloudG = 75.0F / 256.0F, cloudB = 125.0F / 256.0F;
        float delta = 0.1F;
        cloudR = Mth.lerp(delta, cloudR, skyR); cloudG = Mth.lerp(delta, cloudG, skyG); cloudB = Mth.lerp(delta, cloudB, skyB);
        delta = 0.0F;
        cloudR = Mth.lerp(delta, cloudR, fogR); cloudG = Mth.lerp(delta, cloudG, fogG); cloudB = Mth.lerp(delta, cloudB, fogB);

        poseStack.pushPose();
        poseStack.translate(0, -(camY - 250.0F), 0);
        float skyColMult = 0.8F;

        drawHorizon(poseStack.last().pose(), skyR * skyColMult, skyG * skyColMult, skyB * skyColMult, aboveCloudAlphaOffset);

        for (int ring = RING_COUNT; ring >= 0; ring--) {
            poseStack.pushPose();
            float ringDist = ((float)ring) / RING_COUNT;
            ringDist *= ringDist;

            float ringRadius = Mth.lerp(ringDist, RING_MIN_RADIUS, RING_MAX_RADIUS);

            // draw stars
            RenderSystem.setShader(ClinkerShaders::getSkyStarShader);
            RenderSystem.setShaderTexture(0, STAR_TEXTURE);
            RenderSystem.setShaderTexture(1, STAR_COLOR_TEXTURE);
            ShaderInstance starShader = RenderSystem.getShader();

            setShaderUniform(starShader, "Rotation", time * 0.0005F);
            setShaderUniform(starShader, "TwinkleTime", time * 0.1F);

            floatingStarBuffers[ring].bind();
            floatingStarBuffers[ring].drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
            VertexBuffer.unbind();


            // draw cloud rings
            RenderSystem.setShaderTexture(0, NOISE_TEXTURE);
            RenderSystem.setShader(ClinkerShaders::getSkyCloudShader);
            RenderSystem.setShaderGameTime(ticks, partialTick);

            float smoothRingDist = MathUtil.ease(ringDist, MathUtil.EasingType.easeOutCirc);
            float ringR = fogR, ringG = fogG, ringB = fogB;
            //delta = smoothRingDist * 0.7F;
            //ringR = Mth.lerp(delta, ringR, cloudR); ringG = Mth.lerp(delta, ringG, cloudG); ringB = Mth.lerp(delta, ringB, cloudB);
            delta = smoothRingDist;
            ringR = Mth.lerp(delta, ringR, skyR*skyColMult); ringG = Mth.lerp(delta, ringG, skyG*skyColMult); ringB = Mth.lerp(delta, ringB, skyB*skyColMult);
            delta = (1 - aboveCloudAlphaOffset);
            ringR = Mth.lerp(delta, ringR, fogR); ringG = Mth.lerp(delta, ringG, fogG); ringB = Mth.lerp(delta, ringB, fogB);

            ShaderInstance cloudShader = RenderSystem.getShader();
            setShaderUniform(cloudShader, "UVRatio", (Mth.TWO_PI * ringRadius) / RING_HEIGHT);
            setShaderUniform(cloudShader, "UVSquish", ringDist * ringDist * 3.0F);
            setShaderUniform(cloudShader, "RingDistance", ringDist);
            setShaderUniform(cloudShader, "RingColor", ringR, ringG, ringB, 1.0F);

            poseStack.scale(ringRadius, RING_HEIGHT, ringRadius);
            cloudRingBuffer.bind();
            cloudRingBuffer.drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
            VertexBuffer.unbind();

            poseStack.popPose();
        }
        poseStack.popPose();

        RenderSystem.setShaderColor(fogR, fogG, fogB, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        horizonFogBuffer.bind();
        horizonFogBuffer.drawWithShader(poseStack.last().pose(), projMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    }

    private void drawHorizon(Matrix4f matrix, float skyR, float skyG, float skyB, float aboveCloudOffset) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < 16; i++) {
            float f1 = (i / (float) 16);
            float x1 = Mth.sin(f1 * Mth.TWO_PI) * RING_MAX_RADIUS;
            float z1 = Mth.cos(f1 * Mth.TWO_PI) * RING_MAX_RADIUS;

            float f2 = ((i + 1) / (float) 16);
            float x2 = Mth.sin(f2 * Mth.TWO_PI) * RING_MAX_RADIUS;
            float z2 = Mth.cos(f2 * Mth.TWO_PI) * RING_MAX_RADIUS;

            bufferbuilder.addVertex(matrix, x1,-RING_HEIGHT, z1).setColor(skyR, skyG, skyB, 0.0F);
            bufferbuilder.addVertex(matrix, x1, 0.0F, z1).setColor(skyR, skyG, skyB, aboveCloudOffset);
            bufferbuilder.addVertex(matrix, x2, 0.0F, z2).setColor(skyR, skyG, skyB, aboveCloudOffset);
            bufferbuilder.addVertex(matrix, x2,-RING_HEIGHT, z2).setColor(skyR, skyG, skyB, 0.0F);

            bufferbuilder.addVertex(matrix, x1,0.0F, z1).setColor(skyR, skyG, skyB, aboveCloudOffset);
            bufferbuilder.addVertex(matrix, x1, RING_HEIGHT, z1).setColor(skyR, skyG, skyB, 0.0F);
            bufferbuilder.addVertex(matrix, x2, RING_HEIGHT, z2).setColor(skyR, skyG, skyB, 0.0F);
            bufferbuilder.addVertex(matrix, x2,0.0F, z2).setColor(skyR, skyG, skyB, aboveCloudOffset);
        }

        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    private void buildVBOs() {
        this.outerSkyBuffer = this.buildOuterSkyBuffer(this.outerSkyBuffer, 32);
        this.outerStarsBuffer = this.buildOuterStarsBuffer(this.outerStarsBuffer, 2048);
        this.outerCloudsBuffer = this.buildOuterCloudsBuffer(this.outerCloudsBuffer, 32, 32);
        this.cloudRingBuffer = this.buildCloudBuffer(this.cloudRingBuffer, 32);
        this.horizonFogBuffer = this.buildHorizonFogBuffer(this.horizonFogBuffer, 32);
        buildStarVBOs();
    }
    private float previousRenderDistance = -1;
    private void buildStarVBOs() {
        RING_MIN_RADIUS = (Minecraft.getInstance().options.getEffectiveRenderDistance()+1) * 16;
        if (previousRenderDistance != RING_MIN_RADIUS) {
            previousRenderDistance = RING_MIN_RADIUS;
            this.floatingStarBuffers = new VertexBuffer[RING_COUNT + 1];
            for (int i = 0; i < RING_COUNT + 1; i++) {
                float factor = i / (float) RING_COUNT;
                factor *= factor;
                float nextFactor = (i + 1) / (float) RING_COUNT;
                nextFactor *= nextFactor;
                this.floatingStarBuffers[i] = buildStarBuffer(this.floatingStarBuffers[i], factor, nextFactor, 50);
            }

            this.horizonFogBuffer = this.buildHorizonFogBuffer(this.horizonFogBuffer, 32);
        }
    }
    private VertexBuffer buildStarBuffer(VertexBuffer vbo, float rangeStart, float rangeEnd, int count) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        float maxStarDistance = RING_MAX_RADIUS;
        float minStarDistance = RING_MIN_RADIUS;
        float minStarRadius = 3.0F;
        float maxStarRadius = 5.0F;

        Matrix4f matrix = new Matrix4f();
        for (int i = 0; i < count; i++) {
            matrix.identity().rotateY(random.nextFloat() * Mth.TWO_PI);
            float distanceFactor = Mth.lerp(random.nextFloat(), rangeStart, rangeEnd);

            float distance = Mth.lerp(distanceFactor, minStarDistance, maxStarDistance);
            float scaling = Mth.lerp(distanceFactor, 1.0F, 5.0F);
            float radius = Mth.lerp(random.nextFloat(), minStarRadius, maxStarRadius) * scaling;
            float height = -30.0F + (float) random.nextGaussian() * RING_HEIGHT * 0.5F * 0.1F;

            float fancy = random.nextInt(10) == 0 ? 0.5F : 0.0F;
            float u1 = 0.0F, v1 = 0.0F;
            float u2 = 1.0F, v2 = 1.0F;

            float rotationSpeedMultiplier = (distanceFactor * distanceFactor * 0.08F) * ((random.nextFloat() * 2.0F) - 1.0F);
            rotationSpeedMultiplier = (rotationSpeedMultiplier + 1.0F) * 0.5F;
            float gradientPos = random.nextFloat();
            float brightness = Mth.clamp((1.0F - distanceFactor) + ((random.nextFloat() * 2.0F) - 1.0F) * 0.7F, 0.0F, 1.0F);
            fancy += (brightness * brightness) * 0.5F * random.nextFloat();
            float twinkleOffset = random.nextFloat();

            bufferBuilder.addVertex(matrix, -radius, height - radius, distance)
                    .setUv(u1, v1)
                    .setColor(rotationSpeedMultiplier, gradientPos, twinkleOffset, fancy);
            bufferBuilder.addVertex(matrix, -radius, height + radius, distance)
                    .setUv(u2, v1)
                    .setColor(rotationSpeedMultiplier, gradientPos, twinkleOffset, fancy);
            bufferBuilder.addVertex(matrix,  radius, height + radius, distance)
                    .setUv(u2, v2)
                    .setColor(rotationSpeedMultiplier, gradientPos, twinkleOffset, fancy);
            bufferBuilder.addVertex(matrix,  radius, height - radius, distance)
                    .setUv(u1, v2)
                    .setColor(rotationSpeedMultiplier, gradientPos, twinkleOffset, fancy);
        }
        MeshData renderedBuffer = bufferBuilder.buildOrThrow();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildCloudBuffer(VertexBuffer vbo, int resolution) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        for (int i = 0; i < resolution; i++) {
            float f1 = (i / (float) resolution);
            float x1 = Mth.sin(f1 * Mth.TWO_PI);
            float z1 = Mth.cos(f1 * Mth.TWO_PI);

            float f2 = ((i + 1) / (float) resolution);
            float x2 = Mth.sin(f2 * Mth.TWO_PI);
            float z2 = Mth.cos(f2 * Mth.TWO_PI);

            bufferBuilder.addVertex(x1,-0.5F, z1).setColor(0xFFFFFFFF).setUv(f1, 0.0F);
            bufferBuilder.addVertex(x1, 0.5F, z1).setColor(0xFFFFFFFF).setUv(f1, 1.0F);
            bufferBuilder.addVertex(x2, 0.5F, z2).setColor(0xFFFFFFFF).setUv(f2, 1.0F);
            bufferBuilder.addVertex(x2,-0.5F, z2).setColor(0xFFFFFFFF).setUv(f2, 0.0F);
        }
        MeshData renderedBuffer = bufferBuilder.buildOrThrow();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildOuterSkyBuffer(VertexBuffer vbo, int resolution) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        // r = fog color / sky color
        // g = use texture
        // sky
        buildCone(bufferBuilder, resolution, 1.0F, 1.0F, 0.0F,
                1.0F, 0.0F, 0.0F, 1.0F,
                 0.0F, 1.0F, 0.0F, 1.0F);
        buildCone(bufferBuilder, resolution, 1.0F, -1.0F, 0.0F,
                1.0F,0.0F, 0.0F, 1.0F,
                 1.0F, 0.0F, 0.0F, 1.0F);

        MeshData renderedBuffer = bufferBuilder.buildOrThrow();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildOuterCloudsBuffer(VertexBuffer vbo, int resolution, int layerCount) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        for (int i = 0; i < layerCount; i++) {
            float factor = (float) i / layerCount;
            float height = Mth.lerp(factor, 0.7F, 0.5F);
            buildCone(bufferBuilder, resolution, 1.0F, height/5.0F, (height - 0.5F)/5.0F,
                    0.0F,1.0F,0.0F, factor,
                     0.02F, 1.0F, 1.0F, factor);
        }

        MeshData renderedBuffer = bufferBuilder.buildOrThrow();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildOuterStarsBuffer(VertexBuffer vbo, int count) {
        RandomSource random = RandomSource.create(1337);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        float minStarRadius = 1.0F/150.0F;
        float maxStarRadius = 3.0F/150.0F;

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
            bufferBuilder.addVertex(matrix, - radius, - radius, distance)
                    .setUv(u1, v1)
                    .setColor(brightness, gradientPos, twinkleOffset, fancy);
            bufferBuilder.addVertex(matrix, - radius, + radius, distance)
                    .setUv(u2, v1)
                    .setColor(brightness, gradientPos, twinkleOffset, fancy);
            bufferBuilder.addVertex(matrix, + radius, + radius, distance)
                    .setUv(u2, v2)
                    .setColor(brightness, gradientPos, twinkleOffset, fancy);
            bufferBuilder.addVertex(matrix, + radius, - radius, distance)
                    .setUv(u1, v2)
                    .setColor(brightness, gradientPos, twinkleOffset, fancy);
        }
        MeshData renderedBuffer = bufferBuilder.buildOrThrow();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return vbo;
    }
    private VertexBuffer buildHorizonFogBuffer(VertexBuffer vbo, int resolution) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (vbo != null) vbo.close();
        vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        
        for (int i = 0; i < resolution; i++) {
            float f1 = i / (float) resolution;
            float x1 = Mth.sin(f1 * Mth.TWO_PI) * RING_MIN_RADIUS;
            float z1 = Mth.cos(f1 * Mth.TWO_PI) * RING_MIN_RADIUS;

            float f2 = (i + 1) / (float) resolution;
            float x2 = Mth.sin(f2 * Mth.TWO_PI) * RING_MIN_RADIUS;
            float z2 = Mth.cos(f2 * Mth.TWO_PI) * RING_MIN_RADIUS;

            // triangle at the bottom
            bufferBuilder.addVertex(x1, -0.25F * RING_HEIGHT, -z1)
                    .setColor(1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.addVertex(0.0F, -0.5F * RING_HEIGHT, 0.0F)
                    .setColor(1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.addVertex(x2, -0.25F * RING_HEIGHT, -z2)
                    .setColor(1.0F, 1.0F, 1.0F, 1.0F);

            // two quads
            bufferBuilder.addVertex(x1, 0.1F * RING_HEIGHT, z1)
                    .setColor(1.0F, 1.0F, 1.0F, 0.0F);
            bufferBuilder.addVertex(x2, 0.1F * RING_HEIGHT, z2)
                    .setColor(1.0F, 1.0F, 1.0F, 0.0F);
            bufferBuilder.addVertex(x1,-0.25F * RING_HEIGHT, z1)
                    .setColor(1.0F, 1.0F, 1.0F, 1.0F);

            bufferBuilder.addVertex(x2, 0.1F * RING_HEIGHT, z2)
                    .setColor(1.0F, 1.0F, 1.0F, 0.0F);
            bufferBuilder.addVertex(x2,-0.25F * RING_HEIGHT, z2)
                    .setColor(1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.addVertex(x1,-0.25F * RING_HEIGHT, z1)
                    .setColor(1.0F, 1.0F, 1.0F, 1.0F);




//            bufferBuilder.addVertex(x2, 0.0F, z2)
//                    .setColor(1.0F, 1.0F, 1.0F, 1.0F)
//                    ;
//            bufferBuilder.addVertex(x2, -0.25F * RING_HEIGHT, z2)
//                    .setColor(1.0F, 1.0F, 1.0F, 1.0F)
//                    ;
//            bufferBuilder.addVertex(x1,-0.25F * RING_HEIGHT , z1)
//                    .setColor(1.0F, 1.0F, 1.0F, 1.0F)
//                    ;
        }

        MeshData renderedBuffer = bufferBuilder.buildOrThrow();

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

            bufferBuilder.addVertex(x1 * radius, ringHeight, z1 * radius * windingMultiplier)
                    .setUv((x1 + 1.0F) / 2.0F, (z1 * windingMultiplier + 1.0F) / 2.0F)
                    .setColor(ringR, ringG, ringB, ringA);
            bufferBuilder.addVertex(0.0F, height, 0.0F)
                    .setUv(0.5F, 0.5F)
                    .setColor(topR, topG, topB, topA);
            bufferBuilder.addVertex(x2 * radius, ringHeight, z2 * radius * windingMultiplier)
                    .setUv((x2 + 1.0F) / 2.0F, (z2 * windingMultiplier + 1.0F) / 2.0F)
                    .setColor(ringR, ringG, ringB, ringA);
        }
    }

    private static void setShaderUniform(ShaderInstance shader, String name, float... values) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(values);
    }
}
