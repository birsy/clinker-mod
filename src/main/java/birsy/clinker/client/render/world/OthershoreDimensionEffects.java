package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class OthershoreDimensionEffects extends DimensionSpecialEffects {
    private final Minecraft mc = Minecraft.getInstance();
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");

    private float[][] starInfo = null;
    public OthershoreDimensionEffects() {
        super(256.0F, true, SkyType.NORMAL, false, false);
    }

    public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
        return pFogColor.multiply(pBrightness * 0.94F + 0.06F, pBrightness * 0.94F + 0.06F, pBrightness * 0.91F + 0.09F);
    }

    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        return true;//super.renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, projectionMatrix);
    }

    private float[][] getOrCreateStarInfo() {
        if (starInfo == null) {
            RandomSource random = RandomSource.create(10842L);
            float maxStarDistance = 300.0F;
            float minStarDistance = 30.0F;
            float minStarRadius = 0.1F;
            float maxStarRadius = 1.8F;
            int starCount = 1024;

            this.starInfo = new float[starCount][];
            for(int star = 0; star < starCount; ++star) {
                float d = random.nextFloat();
                d *= d;
                float sDist = Mth.lerp(d, minStarDistance, maxStarDistance);
                float sRad = Mth.lerp(d, minStarRadius, maxStarRadius) * random.nextFloat();
                float sHeight = (float)random.nextGaussian();

                float dFactor = Mth.clamp(d, 0.0F, 1.0F);

                float xRot = (random.nextFloat() * 2) - 1;
                float zRot = (random.nextFloat() * 2) - 1;
                float yRot = random.nextFloat() * 360.0F;

                Color color = Color.getHSBColor((float) Math.abs(random.nextGaussian()), 0.3F, 1.0F);

                float red = color.getRed() / 255.0F;
                float green = color.getGreen() / 255.0F;
                float blue = color.getBlue() / 255.0F;
                float alpha = (1 - dFactor); //* Mth.sqrt(1 - Mth.clamp(Math.abs(sHeight), 0.0F, 1.0F));

                xRot = 90.0F;//90.0F + (xRot * 60.0F);
                zRot = (zRot * 180.0F);

                boolean isFancy = star < (0.1F * starCount);

                this.starInfo[star] = new float[]{sDist, sRad, xRot, yRot, zRot, red, green, blue, alpha, sHeight * 25.0F, dFactor, isFancy ? 1.0F : 0.0F};
            }
        }

        return starInfo;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        //setup and variables i'll probably need for everything
        setupFog.run();
        float time = (ticks + partialTick);
        float lightFactor = OthershoreFogRenderer.calculateInterpolatedLight(level, camera.getPosition(), LightLayer.SKY, true);
        Vec3 skyColor = level.getSkyColor(camera.getPosition(), partialTick);
        float[] tfg = RenderSystem.getShaderFogColor();
        Vector3f trueFogColor = new Vector3f(tfg[0], tfg[1], tfg[2]);
        Vector3f fogColor = new Vector3f(68.0F / 256.0F, 75.0F / 256.0F, 125.0F / 256.0F);
        fogColor.lerp(new Vector3f((float) skyColor.x(), (float) skyColor.y(), (float) skyColor.z()), 0.1F);
        fogColor.lerp(trueFogColor, lightFactor);
        float skyRed = (float)skyColor.x;
        float skyGreen = (float)skyColor.y;
        float skyBlue = (float)skyColor.z;
        Vec3 cameraPos = camera.getPosition();
        float cameraOffset = (float) -Math.min((cameraPos.y - 64.0F) * 0.1F, 100.0F);
        //Clinker.LOGGER.info(cameraOffset);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        //creates the sky disc
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new ResourceLocation(Clinker.MOD_ID, "textures/environment/cloud_map_a.png"));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR_TEX);
        poseStack.pushPose();
        Matrix4f m1 = poseStack.last().pose();
        float height = MathUtils.minMaxSin(time * 0.01F, 50, 60);
        int resolution = 24;
        float radius = 120.0F;

        float uvOffset1 = -time * 0.0005F;
        float distortionAmount = 0.5F;
        float distortWiggleU = Mth.sin(uvOffset1) * distortionAmount;
        float distortWiggleV = Mth.cos(uvOffset1) * distortionAmount;

        bufferbuilder.vertex(m1, 0.0F, height, 0.0F).color(skyRed, skyGreen, skyBlue, 1.0F).uv(0.5F + uvOffset1 + distortWiggleU, 0.5F + uvOffset1 + distortWiggleV).endVertex();
        for(int vertex = 0; vertex <= resolution; ++vertex) {
            float angle = (float)vertex * ((float)Math.PI * 2F) / ((float)resolution);
            float x = Mth.sin(angle) * radius;
            float z = Mth.cos(angle) * radius;

            float vOffset = Mth.sin(angle);
            float wiggleTime = vOffset - uvOffset1 * 2.0F;
            float distortWiggleU1 = Mth.sin(wiggleTime) * 0.5F * distortionAmount;
            float distortWiggleV1 = Mth.cos(wiggleTime) * 0.5F * distortionAmount;
            bufferbuilder.vertex(m1, x, Mth.sin((angle * 4) + (time * 0.1F)) * 5, -z).color(fogColor.x(), fogColor.y(), fogColor.z(), 0.0F).uv(((x / radius) + 1) * 0.5F + uvOffset1 + distortWiggleV1, ((z / radius) + 1) * 0.5F + uvOffset1 + distortWiggleU1).endVertex();
        }
        BufferUploader.drawWithShader(bufferbuilder.end());

        float aboveCloudAlphaOffset = Mth.clamp(MathUtils.mapRange(-55.0F, -60.0F, 1.0F, 0.0F, cameraOffset), 0.0F, 1.0F);
        //Clinker.LOGGER.info(cameraOffset);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        renderCone(poseStack, bufferbuilder, resolution, false, radius, height, skyRed, skyGreen, skyBlue, 1.0F * aboveCloudAlphaOffset, 0.0F, skyRed, skyGreen, skyBlue, 1.0F * aboveCloudAlphaOffset);
        renderCone(poseStack, bufferbuilder, resolution, true, radius, -height, skyRed, skyGreen, skyBlue, 1.0F, 0.0F, skyRed, skyGreen, skyBlue, 1.0F * aboveCloudAlphaOffset);

        poseStack.pushPose();
        poseStack.translate(0, cameraOffset, 0);
        Vector3f edgeColor = new Vector3f(trueFogColor.x(), trueFogColor.y(), trueFogColor.z());
        edgeColor.lerp(fogColor, 0.7F);
        renderCone(poseStack, bufferbuilder, resolution, true, 500.0F, -20.0F, trueFogColor.x(), trueFogColor.y(), trueFogColor.z(), 1.0F, -20.0F, edgeColor.x(), edgeColor.y(), edgeColor.z(), 0.0F);
        renderCone(poseStack, bufferbuilder, resolution, false, 500.0F, 60.0F, trueFogColor.x(), trueFogColor.y(), trueFogColor.z(), 1.0F * aboveCloudAlphaOffset, 40.0F, edgeColor.x(), edgeColor.y(), edgeColor.z(), 0.0F);
        poseStack.popPose();
        poseStack.popPose();


        //clouds and stars
        poseStack.pushPose();
        poseStack.translate(0, cameraOffset, 0);
        int ringResolution = 24;
        float ringHeight = 100.0F;
        int ringNum = mc.options.graphicsMode().get() == GraphicsStatus.FAST ? 16 : 24;
        float maxRadius = 500.0F;
        float minRadius = 20.0F;

        float alpha = 0.8F;

        ArrayList<float[]> stars = new ArrayList<>(Arrays.stream(getOrCreateStarInfo()).toList());

        for (int ring = ringNum; ring >= 0; ring--) {
            poseStack.pushPose();
            float ringDist = (float)ring / (float)ringNum;
            ringDist *= ringDist;

            float rRadius = Mth.lerp(ringDist, minRadius, maxRadius);
            Vector3f ringColor = new Vector3f(trueFogColor.x(), trueFogColor.y(), trueFogColor.z());
            ringColor.lerp(fogColor, ringDist * 0.7F);
            ringColor.lerp(new Vector3f((float) skyColor.x(), (float) skyColor.y(), (float) skyColor.z()), ringDist);
            ringColor.lerp(trueFogColor, (1 - aboveCloudAlphaOffset) * 0.8F);

            RenderSystem.setShaderGameTime(ticks, partialTick);
            poseStack.pushPose();
            poseStack.translate(0, (ringHeight / 5), 0);
            for (int starIndex = 0; starIndex < stars.size(); starIndex++) {
                float[] star = stars.get(starIndex);
                if (star[0] > rRadius) {
                    renderStar(poseStack, bufferbuilder, star, lightFactor, time, 0.08F, star[11] > 0.0F, true);
                    stars.remove(starIndex);
                }
            }
            poseStack.popPose();
            renderCloudRing(poseStack, bufferbuilder, ringResolution, rRadius, ringHeight, ringHeight / 5.0F, 0.0F, ringDist, ringColor.x(), ringColor.y(), ringColor.z(), alpha * 0.8F, ringColor.x(), ringColor.y(), ringColor.z(), 1.0F);
            poseStack.popPose();
        }
        poseStack.popPose();


        //stars
        /*RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.enableBlend();
        //RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        poseStack.pushPose();

        float starSpeed = 0.03F;
        int specialStarCount = 100;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float[][] starInf = getOrCreateStarInfo();
        for(int st = 0; st < starInf.length - specialStarCount; ++st) {
            poseStack.pushPose();
            float[] star = starInf[st];

            float distanceFactor = star[10];

            float sDist = star[0];
            float sRad = star[1] * (1 - distanceFactor);
            float sHeight = star[9];

            float xRot = star[2];
            float yRot = star[3];
            float zRot = star[4];

            float red = star[5];
            float green = star[6];
            float blue = star[7];
            float alpha = star[8] * Mth.lerp(lightFactor, 1.0F, 0.1F) * 0.8F;


            poseStack.translate(0, sHeight + cameraOffset + (ringHeight / 5), 0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(time * distanceFactor * starSpeed));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(zRot));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));

            Matrix4f m2 = poseStack.last().pose();

            bufferbuilder.vertex(m2, -sRad, sDist, -sRad).color(red, green, blue, alpha * alpha).endVertex();
            bufferbuilder.vertex(m2,  sRad, sDist, -sRad).color(red, green, blue, alpha * alpha).endVertex();
            bufferbuilder.vertex(m2,  sRad, sDist,  sRad).color(red, green, blue, alpha * alpha).endVertex();
            bufferbuilder.vertex(m2, -sRad, sDist,  sRad).color(red, green, blue, alpha * alpha).endVertex();

            poseStack.popPose();
        }
        BufferUploader.drawWithShader(bufferbuilder.end());

        //fancy stars! with a little shine
        //adds some variety
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, STAR_TEXTURE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        for(int st = starInf.length - specialStarCount; st < starInf.length; ++st) {
            poseStack.pushPose();
            float[] star = starInf[st];
            float sDist = star[0];
            float sRad = star[1] * 3.0F;
            float sHeight = star[9];

            float xRot = star[2];
            float yRot = star[3];
            float zRot = star[4];

            float red = star[5];
            float green = star[6];
            float blue = star[7];
            float alpha = star[8] * Mth.lerp(lightFactor, 1.0F, 0.2F) * 0.8F;

            float distanceFactor = star[10];

            poseStack.translate(0, sHeight + cameraOffset + (ringHeight / 5), 0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(time * distanceFactor * starSpeed));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(zRot));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));

            Matrix4f m2 = poseStack.last().pose();

            bufferbuilder.vertex(m2, -sRad, sDist, -sRad).color(red, green, blue, alpha * alpha).uv(0, 0).endVertex();
            bufferbuilder.vertex(m2,  sRad, sDist, -sRad).color(red, green, blue, alpha * alpha).uv(1, 0).endVertex();
            bufferbuilder.vertex(m2,  sRad, sDist,  sRad).color(red, green, blue, alpha * alpha).uv(1, 1).endVertex();
            bufferbuilder.vertex(m2, -sRad, sDist,  sRad).color(red, green, blue, alpha * alpha).uv(0, 1).endVertex();

            poseStack.popPose();
        }
        BufferUploader.drawWithShader(bufferbuilder.end());
        poseStack.popPose();*/

        RenderSystem.depthMask(true);
        return true;//super.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
    }

    private void renderCone(PoseStack poseStack, BufferBuilder bufferBuilder, int resolution, boolean normal, float radius, float topVertexHeight, float topR, float topG, float topB, float topA, float bottomVertexHeight, float bottomR, float bottomG, float bottomB, float bottomA) {
        Matrix4f matrix = poseStack.last().pose();
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix, 0.0F, topVertexHeight, 0.0F).color(topR, topG, topB, topA).endVertex();
        for(int vertex = 0; vertex <= resolution; ++vertex) {
            float angle = (float)vertex * ((float)Math.PI * 2F) / ((float)resolution);
            float x = Mth.sin(angle) * radius;
            float z = Mth.cos(angle) * radius;

            bufferBuilder.vertex(matrix, x, bottomVertexHeight, normal ? z : -z).color(bottomR, bottomG, bottomB, bottomA).endVertex();
        }

        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    private void renderCloudRing(PoseStack poseStack, BufferBuilder bufferBuilder, int resolution, float radius, float ringHeight, float ringOffset, float uvOffset, float cameraDistance, float topR, float topG, float topB, float topA, float bottomR, float bottomG, float bottomB, float bottomA) {
        RenderSystem.depthMask(true);
        RenderSystem.setShader(ClinkerShaders::getSkyCloudShader);
        RenderSystem.disableTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        Matrix4f matrix = poseStack.last().pose();
        float uvSquish = cameraDistance * cameraDistance * 3.0F;

        RenderSystem.setShaderFogEnd(cameraDistance);

        float u1 = 0;
        float u2 = 0; //nothing changes new year's day!
        for(int segment = 0; segment < resolution; ++segment) {
            float angle1 = (float)segment * ((float)Math.PI * 2F) / ((float)resolution);
            float x1 = Mth.sin(angle1) * radius;
            float z1 = Mth.cos(angle1) * radius;

            float angle2 = (segment + 1.0F) * ((float)Math.PI * 2F) / ((float)resolution);
            float x2 = Mth.sin(angle2) * radius;
            float z2 = Mth.cos(angle2) * radius;

            float dist = Mth.sqrt((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2));
            float uvRatio = dist / ringHeight;

            /*int uOffset = (int) (cameraDistance * 512);
            float segU1 = (float)(segment + uOffset) % resolution;
            float segU2 = segU1 + 1.0F;
            float u1 = uvOffset + (segU1 * uvRatio);
            float u2 = uvOffset + (segU2 * uvRatio); //nothing changes new years day!*/
            u1 = (segment * uvRatio);
            u2 = ((segment + 1) * uvRatio);
            bufferBuilder.vertex(matrix, x1, - ringOffset, z1).color(bottomR, bottomG, bottomB, bottomA).uv(u1, 0.0F - uvSquish).endVertex();
            bufferBuilder.vertex(matrix, x1, ringHeight - ringOffset,  z1).color(topR, topG, topB, topA).uv(u1, 1.0F + uvSquish).endVertex();
            bufferBuilder.vertex(matrix, x2, ringHeight - ringOffset,  z2).color(topR, topG, topB, topA).uv(u2, 1.0F + uvSquish).endVertex();
            bufferBuilder.vertex(matrix, x2, - ringOffset, z2).color(bottomR, bottomG, bottomB, bottomA).uv(u2, 0.0F - uvSquish).endVertex();
        }
        RenderSystem.setShaderFogStart(u2);
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    private void renderStar(PoseStack poseStack, BufferBuilder bufferBuilder, float[] star, float skyLightFactor, float time, float starSpeed, boolean isFancy, boolean renderIndividual) {
        poseStack.pushPose();

        float distanceFactor = star[10];

        float distance = star[0];
        float radius = star[1] * (1 - distanceFactor) * (isFancy ? 3.0F : 1.0F);
        float height = star[9];

        float xRot = star[2];
        float yRot = star[3];
        float zRot = star[4];

        float red = star[5];
        float green = star[6];
        float blue = star[7];
        float alpha = star[8] * Mth.lerp(skyLightFactor, 1.0F, 0.1F) * 0.8F;

        poseStack.translate(0, height, 0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(time * distanceFactor * starSpeed));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(zRot));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));

        Matrix4f matrix = poseStack.last().pose();

        if (renderIndividual) bufferBuilder.begin(VertexFormat.Mode.QUADS, isFancy ? DefaultVertexFormat.POSITION_COLOR_TEX : DefaultVertexFormat.POSITION_COLOR);
        if (isFancy) {
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            RenderSystem.enableTexture();
            RenderSystem.setShaderTexture(0, STAR_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            bufferBuilder.vertex(matrix, -radius, distance, -radius).color(red, green, blue, alpha * alpha).uv(0, 0).endVertex();
            bufferBuilder.vertex(matrix,  radius, distance, -radius).color(red, green, blue, alpha * alpha).uv(1, 0).endVertex();
            bufferBuilder.vertex(matrix,  radius, distance,  radius).color(red, green, blue, alpha * alpha).uv(1, 1).endVertex();
            bufferBuilder.vertex(matrix, -radius, distance,  radius).color(red, green, blue, alpha * alpha).uv(0, 1).endVertex();
        } else {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            bufferBuilder.vertex(matrix, -radius, distance, -radius).color(red, green, blue, alpha * alpha).endVertex();
            bufferBuilder.vertex(matrix,  radius, distance, -radius).color(red, green, blue, alpha * alpha).endVertex();
            bufferBuilder.vertex(matrix,  radius, distance,  radius).color(red, green, blue, alpha * alpha).endVertex();
            bufferBuilder.vertex(matrix, -radius, distance,  radius).color(red, green, blue, alpha * alpha).endVertex();
        }
        if (renderIndividual) BufferUploader.drawWithShader(bufferBuilder.end());

        poseStack.popPose();
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return true;//super.renderSnowAndRain(level, ticks, partialTick, lightTexture, camX, camY, camZ);
    }
}
