package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraftforge.client.ISkyRenderHandler;

import javax.annotation.Nullable;
import java.util.Random;

import ResourceLocation;

public class OthershoreSkyRenderer implements ISkyRenderHandler {
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation(Clinker.MOD_ID, "textures/environment/sun.png");
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");
    private final Minecraft mc;
    private final TextureManager textureManager;
    private final VertexFormat skyVertexFormat = DefaultVertexFormat.POSITION;
    private final OthershoreCloudRenderer cloudRenderer;
    @Nullable
    private VertexBuffer starVBO;
    @Nullable
    private final VertexBuffer skyVBO;
    @Nullable
    private final VertexBuffer sky2VBO;

    public OthershoreSkyRenderer(Minecraft mc) {
        this.mc = mc;
        this.textureManager = mc.getTextureManager();
        this.cloudRenderer = new OthershoreCloudRenderer(mc);
        this.skyVBO = new VertexBuffer(this.skyVertexFormat);
        this.sky2VBO = new VertexBuffer(this.skyVertexFormat);
    }

    @Override
    public void render(int ticks, float partialTicks, PoseStack matrixStackIn, ClientLevel world, Minecraft mc) {
        Random rand = new Random(10842L);
        Vec3 skyColor = world.getSkyColor(this.mc.gameRenderer.getMainCamera().getBlockPosition(), partialTicks);
        float skyRed = (float)skyColor.x;
        float skyGreen = (float)skyColor.y;
        float skyBlue = (float)skyColor.z;

        float fogBrightness = 1.0F;
        //this.renderHorizonFog(matrixStackIn, new Vector3d(Math.min(1.0, skyRed * fogBrightness), Math.min(1.0, skyBlue * fogBrightness), Math.min(1.0, skyGreen * fogBrightness)), 1.0F, ticks + partialTicks);

        RenderSystem.disableTexture();

        FogRenderer.levelFogColor();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(skyRed, skyGreen, skyBlue);

        this.skyVBO.bind();
        this.skyVertexFormat.setupBufferState(0L);
        this.skyVBO.draw(matrixStackIn.last().pose(), 7);
        VertexBuffer.unbind();
        this.skyVertexFormat.clearBufferState();
        renderStars(matrixStackIn, 50.0F, rand, ticks, partialTicks);
        renderFancyStars(matrixStackIn, 50.0F, rand, ticks, partialTicks);

        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableFog();
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);

        float[] sunriseColors = world.effects().getSunriseColor(world.getTimeOfDay(partialTicks), partialTicks);
        float skyRadius = 50.0F;

        if (sunriseColors != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            matrixStackIn.pushPose();
            float f3 = Mth.sin(this.getCelestialAngle() * 2 * 3.1415f) < 0.0F ? 180.0F : 0.0F;
            Matrix4f matrix4f = matrixStackIn.last().pose();
            bufferbuilder.begin(6, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.vertex(matrix4f, 0.0F, skyRadius / 3, 0.0F).color(skyRed, skyGreen, skyBlue, 1.0F).endVertex();
            int circleRes = 32;
            Vector3f fogTransitionColors = new Vector3f(98.0F / 256.0F, 88.0F / 256.0F, 114.0F / 256.0F);
            for(int vertexNum = 0; vertexNum <= circleRes; ++vertexNum) {
                float circleRotation = (float) (vertexNum * (Math.PI * 2F) / circleRes);
                float circleX = Mth.sin(circleRotation);
                float circleZ = Mth.cos(circleRotation);
                bufferbuilder.vertex(matrix4f, circleX * skyRadius, 0, -circleZ * skyRadius).color(fogTransitionColors.x(), fogTransitionColors.y(), fogTransitionColors.z(), 0.0F).endVertex();
            }

            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
            matrixStackIn.popPose();
            RenderSystem.shadeModel(7424);
        }

        RenderSystem.disableFog();
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        matrixStackIn.pushPose();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-45.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(this.getCelestialAngle() * 360.0F));
        Matrix4f matrix4f1 = matrixStackIn.last().pose();

        float f12 = 40.0F;
        this.textureManager.bind(SUN_TEXTURES);
        bufferbuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f12 * 5, 100.0F, -f12).uv(0.0F, 0.0F).color(0.9f * 0.05f, 0.05f, 0.7f * 0.05f, 0.05f).endVertex();
        bufferbuilder.vertex(matrix4f1, f12 * 5, 100.0F, -f12).uv(1.0F, 0.0F).color(0.9f * 0.05f, 0.05f, 0.7f * 0.05f, 0.05f).endVertex();
        bufferbuilder.vertex(matrix4f1, f12 * 5, 100.0F, f12).uv(1.0F, 1.0F).color(0.9f, 1.0f, 0.7f, 0.25f).endVertex();
        bufferbuilder.vertex(matrix4f1, -f12 * 5, 100.0F, f12).uv(0.0F, 1.0F).color(0.9f, 1.0f, 0.7f, 0.25f).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.disableTexture();
        VertexBuffer.unbind();
        this.skyVertexFormat.clearBufferState();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        matrixStackIn.popPose();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0F, 0.0F, 0.0F);
        double d0 = this.mc.player.getEyePosition(partialTicks).y - world.getLevelData().getHorizonHeight();
        if (d0 < 0.0D) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0D, 12.0D, 0.0D);
            this.sky2VBO.bind();
            this.skyVertexFormat.setupBufferState(0L);
            this.sky2VBO.draw(matrixStackIn.last().pose(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.clearBufferState();
            matrixStackIn.popPose();
        }

        RenderSystem.color3f(skyRed, skyGreen, skyBlue);
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    private void renderStars(PoseStack matrixStackIn, float skyRadius, Random rand, int ticksIn, float partialTicks) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        matrixStackIn.pushPose();
        float ticks = ticksIn + partialTicks;

        int starCount = 3000;
        double starRadius = (skyRadius / 3) - 2;
        for(int i = 0; i < starCount; ++i) {
            double starX = rand.nextFloat() * 2.0F - 1.0F;
            double starY = rand.nextFloat();
            double starZ = rand.nextFloat() * 2.0F - 1.0F;
            double starSize = (0.01F + rand.nextFloat() * 0.017F);
            starSize += ((Mth.sin(ticks * (rand.nextFloat() * 0.125F)) + 1) * 0.5) * (0.01F + rand.nextFloat() * 0.022F);
            double starDistance = starRadius * MathUtils.getRandomFloatBetween(rand, 0.65F, 1.0F);

            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float) ((ticks * 0.005F) * (starY * 0.005F)) * 1.5F));

            double d4 = starX * starX + starY * starY + starZ * starZ;
            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                starX = starX * d4;
                starY = starY * d4;
                starZ = starZ * d4;
                double d5 = starX * starDistance;
                double d6 = starY * starDistance;
                double d7 = starZ * starDistance;
                double d8 = Math.atan2(starX, starZ);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(starX * starX + starZ * starZ), starY);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = rand.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                bufferbuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
                for(int vertexCount = 0; vertexCount < 4; ++vertexCount) {
                    double d18 = ((vertexCount & 2) - 1) * starSize;
                    double d19 = ((vertexCount + 1 & 2) - 1) * starSize;

                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;

                    float starHeight = MathUtils.mapRange(0, (float) starDistance, 0.0f, 1.0f, (float) (d6 + d23));
                    float r = Mth.lerp(starHeight, 131, 205) / 255;
                    float g = Mth.lerp(starHeight, 102, 255) / 255;
                    float b = Mth.lerp(starHeight, 177, 171) / 255;

                    bufferbuilder.vertex(matrixStackIn.last().pose(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).color(r, g, b, starHeight).endVertex();
                }
                tessellator.end();
            }
        }
        matrixStackIn.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void renderFancyStars(PoseStack matrixStackIn, float skyRadius, Random rand, int ticksIn, float partialTicks) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        this.textureManager.bind(STAR_TEXTURE);
        matrixStackIn.pushPose();
        float ticks = ticksIn + partialTicks;

        float starBrightness = 1.5F;
        int starCount = 75;
        double starRadius = (skyRadius / 3) - 2;
        for(int i = 0; i < starCount; ++i) {
            double starX = rand.nextFloat() * 2.0F - 1.0F;
            double starY = rand.nextFloat();
            double starZ = rand.nextFloat() * 2.0F - 1.0F;
            double starSize = (0.01F + rand.nextFloat() * 0.017F) * 7.5F;
            starSize += ((Mth.sin(ticks * (rand.nextFloat() * 0.125F)) + 1) * 0.5) * (0.01F + rand.nextFloat() * 0.062F);

            double d4 = starX * starX + starY * starY + starZ * starZ;
            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                starX = starX * d4;
                starY = starY * d4;
                starZ = starZ * d4;
                double d5 = starX * starRadius;
                double d6 = starY * starRadius;
                double d7 = starZ * starRadius;
                double d8 = Math.atan2(starX, starZ);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(starX * starX + starZ * starZ), starY);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = rand.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                bufferbuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                for(int vertexCount = 0; vertexCount < 4; ++vertexCount) {
                    double d18 = ((vertexCount & 2) - 1) * starSize;
                    double d19 = ((vertexCount + 1 & 2) - 1) * starSize;

                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;

                    float starHeight = MathUtils.mapRange(0, (float) starRadius, 0.0f, 1.0f, (float) (d6 + d23));
                    float r = Math.min(Mth.lerp(starHeight, 131, 205) * starBrightness, 255) / 255;
                    float g = Math.min(Mth.lerp(starHeight, 102, 255) * starBrightness, 255) / 255;
                    float b = Math.min(Mth.lerp(starHeight, 177, 171) * starBrightness, 255) / 255;

                    float starTexEnd = 13.0F / 16.0F;

                    if (vertexCount == 0) {
                        bufferbuilder.vertex(matrixStackIn.last().pose(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).uv(0,0)             .color(r, g, b, starHeight).endVertex();
                    } else if (vertexCount == 1) {
                        bufferbuilder.vertex(matrixStackIn.last().pose(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).uv(0, starTexEnd)      .color(r, g, b, starHeight).endVertex();
                    } else if (vertexCount == 2) {
                        bufferbuilder.vertex(matrixStackIn.last().pose(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).uv(starTexEnd, starTexEnd).color(r, g, b, starHeight).endVertex();
                    } else {
                        bufferbuilder.vertex(matrixStackIn.last().pose(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).uv(starTexEnd,0)       .color(r, g, b, starHeight).endVertex();
                    }
                }
                tessellator.end();
            }
        }
        matrixStackIn.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void renderNebula(PoseStack matrixStackIn, float skyRadius, int ticksIn, float partialTicks) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableFog();
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        RenderSystem.depthMask(false);
        this.textureManager.bind(SUN_TEXTURES);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        matrixStackIn.pushPose();
        float ticks = ticksIn + partialTicks;

        matrixStackIn.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void applyBobbing(PoseStack matrixStackIn, float partialTicks, float intensity) {
        if (this.mc.getCameraEntity() instanceof Player) {
            Player playerentity = (Player)this.mc.getCameraEntity();
            float f = playerentity.walkDist - playerentity.walkDistO;
            float f1 = -(playerentity.walkDist + f * partialTicks);
            float f2 = Mth.lerp(partialTicks, playerentity.oBob, playerentity.bob);
            matrixStackIn.translate(Mth.sin((f1 * (float)Math.PI) * f2 * 0.5F) * intensity, (-Math.abs(Mth.cos(f1 * (float)Math.PI) * f2)) * intensity, 0.0D);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((Mth.sin(f1 * (float)Math.PI) * f2 * 3.0F) * intensity));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees((Math.abs(Mth.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F) * intensity));
        }
    }

    public float getCelestialAngle() {
        double d0 = Mth.frac(24000 / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
        return (float)(d0 * 2.0D + d1) / 3.0F;
    }
}
