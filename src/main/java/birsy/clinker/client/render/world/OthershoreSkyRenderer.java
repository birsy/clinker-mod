package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ISkyRenderHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class OthershoreSkyRenderer implements ISkyRenderHandler {
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation(Clinker.MOD_ID, "textures/environment/sun.png");
    private final Minecraft mc;
    private final TextureManager textureManager;
    private final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION;

    @Nullable
    private VertexBuffer starVBO;
    @Nullable
    private final VertexBuffer skyVBO;
    @Nullable
    private final VertexBuffer sky2VBO;

    public OthershoreSkyRenderer(Minecraft mc) {
        this.mc = mc;
        this.textureManager = mc.getTextureManager();

        this.skyVBO = new VertexBuffer(this.skyVertexFormat);
        this.sky2VBO = new VertexBuffer(this.skyVertexFormat);

        this.generateStars();
    }

    private void generateStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        if (this.starVBO != null) {
            this.starVBO.close();
        }

        this.starVBO = new VertexBuffer(this.skyVertexFormat);
        this.renderStars(bufferbuilder);
        bufferbuilder.finishDrawing();
        this.starVBO.upload(bufferbuilder);
    }

    private void renderStars(BufferBuilder bufferBuilderIn) {
        Random random = new Random(10842L);
        bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

        for(int i = 0; i < 3000; ++i) {
            double d0 = random.nextFloat() * 2.0F - 1.0F;
            double d1 = random.nextFloat() * 2.0F - 1.0F;
            double d2 = random.nextFloat() * 2.0F - 1.0F;
            double d3 = 0.15F + random.nextFloat() * 0.1F;
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 = d0 * d4;
                d1 = d1 * d4;
                d2 = d2 * d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for(int j = 0; j < 4; ++j) {
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }
    }

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStackIn, ClientWorld world, Minecraft mc) {
        RenderSystem.disableTexture();
        Vector3d skyColor = world.getSkyColor(this.mc.gameRenderer.getActiveRenderInfo().getBlockPos(), partialTicks);
        float skyRed = (float)skyColor.x;
        float skyGreen = (float)skyColor.y;
        float skyBlue = (float)skyColor.z;

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

        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] afloat = world.getDimensionRenderInfo().func_230492_a_(world.func_242415_f(partialTicks), partialTicks);
        if (afloat != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
            float f3 = MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F;
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f3));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
            float f4 = afloat[0];
            float f5 = afloat[1];
            float f6 = afloat[2];
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, afloat[3]).endVertex();
            int i = 16;

            for(int j = 0; j <= 16; ++j) {
                float f7 = (float)j * ((float)Math.PI * 2F) / 16.0F;
                float f8 = MathHelper.sin(f7);
                float f9 = MathHelper.cos(f7);
                bufferbuilder.pos(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
            }

            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            matrixStackIn.pop();
            RenderSystem.shadeModel(7424);
        }

        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        matrixStackIn.push();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-45.0F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(world.func_242415_f(partialTicks) * 360.0F));
        Matrix4f matrix4f1 = matrixStackIn.getLast().getMatrix();

        float f12 = 40.0F;
        this.textureManager.bindTexture(SUN_TEXTURES);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix4f1, -f12 * 5, 100.0F, -f12).tex(0.0F, 0.0F).color(0.9f * 0.05f, 1.0f * 0.05f, 0.7f * 0.05f, 0.05f).endVertex();
        bufferbuilder.pos(matrix4f1, f12 * 5, 100.0F, -f12).tex(1.0F, 0.0F).color(0.9f * 0.05f, 1.0f * 0.05f, 0.7f * 0.05f, 0.05f).endVertex();
        bufferbuilder.pos(matrix4f1, f12 * 5, 100.0F, f12).tex(1.0F, 1.0F).color(0.9f, 1.0f, 0.7f, 0.25f).endVertex();
        bufferbuilder.pos(matrix4f1, -f12 * 5, 100.0F, f12).tex(0.0F, 1.0F).color(0.9f, 1.0f, 0.7f, 0.25f).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.starVBO.bindBuffer();
        this.skyVertexFormat.setupBufferState(0L);
        this.starVBO.draw(matrixStackIn.getLast().getMatrix(), 7);
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


        RenderSystem.color3f(skyRed, skyGreen, skyBlue);
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }
}
