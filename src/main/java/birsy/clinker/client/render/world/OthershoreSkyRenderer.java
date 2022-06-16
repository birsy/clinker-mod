package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ISkyRenderHandler;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class OthershoreSkyRenderer implements ISkyRenderHandler {
    /*private static final ResourceLocation SUN_TEXTURES = new ResourceLocation(Clinker.MOD_ID, "textures/environment/sun.png");
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");
    private Minecraft mc;
    @Nullable
    private VertexBuffer skyBuffer;
    @Nullable
    private VertexBuffer darkBuffer;

    public OthershoreSkyRenderer(Minecraft mc) {
        this.mc = mc;
        this.createLightSky();
        this.createDarkSky();
    }

    private void createDarkSky() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        if (this.darkBuffer != null) {
            this.darkBuffer.close();
        }

        this.darkBuffer = new VertexBuffer();
        buildSkyDisc(bufferbuilder, -16.0F);
        this.darkBuffer.upload(bufferbuilder);
    }

    private void createLightSky() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        if (this.skyBuffer != null) {
            this.skyBuffer.close();
        }

        this.skyBuffer = new VertexBuffer();
        buildSkyDisc(bufferbuilder, 16.0F);
        this.skyBuffer.upload(bufferbuilder);
    }

    private static void buildSkyDisc(BufferBuilder pBuilder, float y) {
        float f1 = 512.0F;
        float f = Math.signum(y) * f1;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        pBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        pBuilder.vertex(0.0D, y, 0.0D).endVertex();

        for(int angle = -180; angle <= 180; angle += 45) {
            pBuilder.vertex(f * Mth.cos((float) angle * ((float) Math.PI / 180F)), y, f1 * Mth.sin((float) angle * ((float) Math.PI / 180F))).endVertex();
        }

        pBuilder.end();
    }

    @Override
    public void render(int ticks, float partialTicks, PoseStack matrixStack, ClientLevel level, Minecraft mc) {
        RenderSystem.disableTexture();
        Vec3 vec3 = level.getSkyColor(mc.gameRenderer.getMainCamera().getPosition(), partialTicks);
        float f = (float)vec3.x;
        float f1 = (float)vec3.y;
        float f2 = (float)vec3.z;
        FogRenderer.levelFogColor();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, f1, f2, 1.0F);
        ShaderInstance shaderinstance = RenderSystem.getShader();
        this.skyBuffer.drawWithShader(matrixStack.last().pose(), mc.gameRenderer.getProjectionMatrix(partialTicks), shaderinstance);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] afloat = level.effects().getSunriseColor(level.getTimeOfDay(partialTicks), partialTicks);
        if (afloat != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            float f3 = Mth.sin(level.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F;
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f3));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            float f4 = afloat[0];
            float f5 = afloat[1];
            float f6 = afloat[2];
            Matrix4f matrix4f = matrixStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, afloat[3]).endVertex();
            int i = 16;

            for(int j = 0; j <= 16; ++j) {
                float f7 = (float)j * ((float)Math.PI * 2F) / 16.0F;
                float f8 = Mth.sin(f7);
                float f9 = Mth.cos(f7);
                bufferbuilder.vertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
            }

            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
            matrixStack.popPose();
        }

        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        matrixStack.pushPose();

        float rainAmount = 1.0F - level.getRainLevel(partialTicks);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, rainAmount);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));
        
        float starBrightness = level.getStarBrightness(partialTicks) * rainAmount;
        if (starBrightness > 0.0F) {
            RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, starBrightness);
            FogRenderer.setupNoFog();
            renderStars(matrixStack, 50.0F, (int) level.getGameTime(), partialTicks);
            renderFancyStars(matrixStack, 50.0F, (int) level.getGameTime(), partialTicks);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        matrixStack.popPose();
        RenderSystem.disableTexture();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double d0 = mc.player.getEyePosition(partialTicks).y - level.getLevelData().getHorizonHeight(level);
        if (d0 < 0.0D) {
            matrixStack.pushPose();
            matrixStack.translate(0.0D, 12.0D, 0.0D);
            this.darkBuffer.drawWithShader(matrixStack.last().pose(), mc.gameRenderer.getProjectionMatrix(partialTicks), shaderinstance);
            matrixStack.popPose();
        }

        if (level.effects().hasGround()) {
            RenderSystem.setShaderColor(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F, 1.0F);
        } else {
            RenderSystem.setShaderColor(f, f1, f2, 1.0F);
        }

        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
    }

    private void renderStars(PoseStack matrixStackIn, float skyRadius, int ticksIn, float partialTicks) {
        Random rand = new Random(1337);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
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

                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
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
                bufferbuilder.end();
                BufferUploader.end(bufferbuilder);
            }
        }
        matrixStackIn.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private void renderFancyStars(PoseStack matrixStackIn, float skyRadius, int ticksIn, float partialTicks) {
        Random rand = new Random(1337);

        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, STAR_TEXTURE);
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

                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
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
                bufferbuilder.end();
                BufferUploader.end(bufferbuilder);
            }
        }
        matrixStackIn.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
    }
    */
}
