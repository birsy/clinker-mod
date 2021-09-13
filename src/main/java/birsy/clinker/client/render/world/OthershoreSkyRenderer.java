package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ISkyRenderHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class OthershoreSkyRenderer implements ISkyRenderHandler {
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation(Clinker.MOD_ID, "textures/environment/sun.png");
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");
    private final Minecraft mc;
    private final TextureManager textureManager;
    private final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION;
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
    public void render(int ticks, float partialTicks, MatrixStack matrixStackIn, ClientWorld world, Minecraft mc) {
        Random rand = new Random(10842L);
        Vector3d skyColor = world.getSkyColor(this.mc.gameRenderer.getActiveRenderInfo().getBlockPos(), partialTicks);
        float skyRed = (float)skyColor.x;
        float skyGreen = (float)skyColor.y;
        float skyBlue = (float)skyColor.z;

        float fogBrightness = 1.0F;
        //this.renderHorizonFog(matrixStackIn, new Vector3d(Math.min(1.0, skyRed * fogBrightness), Math.min(1.0, skyBlue * fogBrightness), Math.min(1.0, skyGreen * fogBrightness)), 1.0F, ticks + partialTicks);

        RenderSystem.disableTexture();

        FogRenderer.applyFog();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(skyRed, skyGreen, skyBlue);

        this.skyVBO.bindBuffer();
        this.skyVertexFormat.setupBufferState(0L);
        this.skyVBO.draw(matrixStackIn.getLast().getMatrix(), 7);
        VertexBuffer.unbindBuffer();
        this.skyVertexFormat.clearBufferState();
        renderStars(matrixStackIn, 50.0F, ticks, partialTicks);
        renderFancyStars(matrixStackIn, 50.0F, rand, ticks, partialTicks);

        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableFog();
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);

        float[] sunriseColors = world.getDimensionRenderInfo().func_230492_a_(world.func_242415_f(partialTicks), partialTicks);
        float skyRadius = 50.0F;

        if (sunriseColors != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            matrixStackIn.push();

            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(matrix4f, 0.0F, skyRadius / 3, 0.0F).color(skyRed, skyGreen, skyBlue, 1.0F).endVertex();
            int circleRes = 32;
            Vector3f fogTransitionColors = new Vector3f(98.0F / 256.0F, 88.0F / 256.0F, 114.0F / 256.0F);
            for(int vertexNum = 0; vertexNum <= circleRes; ++vertexNum) {
                float circleRotation = (float) (vertexNum * (Math.PI * 2F) / circleRes);
                float circleX = MathHelper.sin(circleRotation);
                float circleZ = MathHelper.cos(circleRotation);
                bufferbuilder.pos(matrix4f, circleX * skyRadius, 0, -circleZ * skyRadius).color(fogTransitionColors.getX(), fogTransitionColors.getY(), fogTransitionColors.getZ(), 0.0F).endVertex();
            }

            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            matrixStackIn.pop();
            RenderSystem.shadeModel(7424);
        }

        Vector3f darkFogColor = OthershoreFogRenderer.getDarknessFogColors();
        float distanceBelowSeaLevel = (float) MathHelper.clamp((world.getSeaLevel() - mc.getRenderViewEntity().getPositionVec().getY()) / 15.0F, 0.0F, 1.0F);
        if (distanceBelowSeaLevel > 0) {
            skyRadius *= 0.125;
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            matrixStackIn.push();

            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(matrix4f, 0.0F, skyRadius / 3, 0.0F).color(darkFogColor.getX() / 0.75f, darkFogColor.getY() / 0.75f, darkFogColor.getZ() / 0.75f, distanceBelowSeaLevel).endVertex();
            int circleRes = 32;

            for(int vertexNum = 0; vertexNum <= circleRes; ++vertexNum) {
                float circleRotation = (float) (vertexNum * (Math.PI * 2F) / circleRes);
                float circleX = MathHelper.sin(circleRotation);
                float circleZ = MathHelper.cos(circleRotation);
                bufferbuilder.pos(matrix4f, circleX * skyRadius, 0, -circleZ * skyRadius).color(darkFogColor.getX(), darkFogColor.getY(), darkFogColor.getZ(), distanceBelowSeaLevel).endVertex();
            }

            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            matrixStackIn.pop();
            RenderSystem.shadeModel(7424);
        }

        RenderSystem.disableFog();
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        matrixStackIn.push();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.125F * MathUtils.invert(distanceBelowSeaLevel));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-45.0F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(this.getCelestialAngle() * 360.0F));
        Matrix4f matrix4f1 = matrixStackIn.getLast().getMatrix();

        float f12 = 40.0F;
        this.textureManager.bindTexture(SUN_TEXTURES);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix4f1, -f12 * 5, 100.0F, -f12).tex(0.0F, 0.0F).color(0.9f * 0.05f, 0.05f, 0.7f * 0.05f, 0.01f * MathUtils.invert(distanceBelowSeaLevel)).endVertex();
        bufferbuilder.pos(matrix4f1, f12 * 5, 100.0F, -f12).tex(1.0F, 0.0F).color(0.9f * 0.05f, 0.05f, 0.7f * 0.05f, 0.01f * MathUtils.invert(distanceBelowSeaLevel)).endVertex();
        bufferbuilder.pos(matrix4f1, f12 * 5, 100.0F, f12).tex(1.0F, 1.0F).color(0.9f, 1.0f, 0.7f, 0.01f * MathUtils.invert(distanceBelowSeaLevel)).endVertex();
        bufferbuilder.pos(matrix4f1, -f12 * 5, 100.0F, f12).tex(0.0F, 1.0F).color(0.9f, 1.0f, 0.7f, 0.01f * MathUtils.invert(distanceBelowSeaLevel)).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.disableTexture();
        VertexBuffer.unbindBuffer();
        this.skyVertexFormat.clearBufferState();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        matrixStackIn.pop();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0F, 0.0F, 0.0F);
        double d0 = this.mc.player.getEyePosition(partialTicks).y - world.getWorldInfo().getVoidFogHeight();
        if (d0 < 0.0D) {
            matrixStackIn.push();
            matrixStackIn.translate(0.0D, 12.0D, 0.0D);
            this.sky2VBO.bindBuffer();
            this.skyVertexFormat.setupBufferState(0L);
            this.sky2VBO.draw(matrixStackIn.getLast().getMatrix(), 7);
            VertexBuffer.unbindBuffer();
            this.skyVertexFormat.clearBufferState();
            matrixStackIn.pop();
        }

        RenderSystem.color3f(MathHelper.lerp(distanceBelowSeaLevel, skyRed, darkFogColor.getX()), MathHelper.lerp(distanceBelowSeaLevel, skyGreen, darkFogColor.getY()), MathHelper.lerp(distanceBelowSeaLevel, skyBlue, darkFogColor.getZ()));
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    private void renderStars(MatrixStack matrixStackIn, float skyRadius, int ticksIn, float partialTicks) {
        Random rand = new Random(1337);
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        matrixStackIn.push();
        float ticks = ticksIn + partialTicks;

        int starCount = 3000;
        double starRadius = (skyRadius / 3) - 2;
        for(int i = 0; i < starCount; ++i) {
            double starX = rand.nextFloat() * 2.0F - 1.0F;
            double starY = rand.nextFloat();
            double starZ = rand.nextFloat() * 2.0F - 1.0F;
            double starSize = (0.01F + rand.nextFloat() * 0.017F);
            starSize += ((MathHelper.sin(ticks * (rand.nextFloat() * 0.125F)) + 1) * 0.5) * (0.01F + rand.nextFloat() * 0.022F);
            double starDistance = starRadius * MathUtils.getRandomFloatBetween(rand, 0.65F, 1.0F);

            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float) ((ticks * 0.005F) * (starY * 0.005F)) * 1.5F));

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

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
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
                    float r = MathHelper.lerp(starHeight, 131, 205) / 255;
                    float g = MathHelper.lerp(starHeight, 102, 255) / 255;
                    float b = MathHelper.lerp(starHeight, 177, 171) / 255;

                    bufferbuilder.pos(matrixStackIn.getLast().getMatrix(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).color(r, g, b, starHeight).endVertex();
                }
                tessellator.draw();
            }
        }
        matrixStackIn.pop();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void renderFancyStars(MatrixStack matrixStackIn, float skyRadius, Random rand, int ticksIn, float partialTicks) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.textureManager.bindTexture(STAR_TEXTURE);
        matrixStackIn.push();
        float ticks = ticksIn + partialTicks;

        float starBrightness = 1.5F;
        int starCount = 75;
        double starRadius = (skyRadius / 3) - 2;
        for(int i = 0; i < starCount; ++i) {
            double starX = rand.nextFloat() * 2.0F - 1.0F;
            double starY = rand.nextFloat();
            double starZ = rand.nextFloat() * 2.0F - 1.0F;
            double starSize = (0.01F + rand.nextFloat() * 0.017F) * 7.5F;
            starSize += ((MathHelper.sin(ticks * (rand.nextFloat() * 0.125F)) + 1) * 0.5) * (0.01F + rand.nextFloat() * 0.062F);

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

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
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
                    float r = Math.min(MathHelper.lerp(starHeight, 131, 205) * starBrightness, 255) / 255;
                    float g = Math.min(MathHelper.lerp(starHeight, 102, 255) * starBrightness, 255) / 255;
                    float b = Math.min(MathHelper.lerp(starHeight, 177, 171) * starBrightness, 255) / 255;

                    float starTexEnd = 13.0F / 16.0F;

                    if (vertexCount == 0) {
                        bufferbuilder.pos(matrixStackIn.getLast().getMatrix(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).tex(0,0)             .color(r, g, b, starHeight).endVertex();
                    } else if (vertexCount == 1) {
                        bufferbuilder.pos(matrixStackIn.getLast().getMatrix(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).tex(0, starTexEnd)      .color(r, g, b, starHeight).endVertex();
                    } else if (vertexCount == 2) {
                        bufferbuilder.pos(matrixStackIn.getLast().getMatrix(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).tex(starTexEnd, starTexEnd).color(r, g, b, starHeight).endVertex();
                    } else {
                        bufferbuilder.pos(matrixStackIn.getLast().getMatrix(), (float) (d5 + d25), (float) (d6 + d23), (float) (d7 + d26)).tex(starTexEnd,0)       .color(r, g, b, starHeight).endVertex();
                    }
                }
                tessellator.draw();
            }
        }
        matrixStackIn.pop();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void renderNebula(MatrixStack matrixStackIn, float skyRadius, int ticksIn, float partialTicks) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableFog();
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        RenderSystem.depthMask(false);
        this.textureManager.bindTexture(SUN_TEXTURES);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        matrixStackIn.push();
        float ticks = ticksIn + partialTicks;

        matrixStackIn.pop();

        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void applyBobbing(MatrixStack matrixStackIn, float partialTicks, float intensity) {
        if (this.mc.getRenderViewEntity() instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)this.mc.getRenderViewEntity();
            float f = playerentity.distanceWalkedModified - playerentity.prevDistanceWalkedModified;
            float f1 = -(playerentity.distanceWalkedModified + f * partialTicks);
            float f2 = MathHelper.lerp(partialTicks, playerentity.prevCameraYaw, playerentity.cameraYaw);
            matrixStackIn.translate(MathHelper.sin((f1 * (float)Math.PI) * f2 * 0.5F) * intensity, (-Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2)) * intensity, 0.0D);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F) * intensity));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees((Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F) * intensity));
        }
    }

    public float getCelestialAngle() {
        double d0 = MathHelper.frac(24000 / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
        return (float)(d0 * 2.0D + d1) / 3.0F;
    }
}
