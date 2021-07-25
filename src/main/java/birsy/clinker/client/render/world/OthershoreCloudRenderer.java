package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.articdive.jnoise.JNoise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.ICloudRenderHandler;

import javax.annotation.Nullable;

public class OthershoreCloudRenderer implements ICloudRenderHandler {
    private final TextureManager textureManager;
    private final Minecraft mc;
    private final JNoise noise = JNoise.newBuilder().fastSimplex().setSeed(16).build();

    private boolean cloudsNeedUpdate = true;
    @Nullable
    private VertexBuffer cloudsVBO;
    private static final ResourceLocation CLOUD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/cloud.png");

    //2 Clouds peer chunk.
    private static final float cloudWidth = 8;
    private static final float maxCloudHeight = 150;
    private static final float cloudHeight = 120;

    public OthershoreCloudRenderer(Minecraft mc) {
        this.mc = mc;
        this.textureManager = mc.getTextureManager();
    }

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStackIn, ClientWorld world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableFog();
        RenderSystem.depthMask(true);

        Vector3d cloudColor = world.getCloudColor(partialTicks);

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        if (this.cloudsVBO != null) {
            this.cloudsVBO.close();
        } else {
            this.cloudsVBO = new VertexBuffer(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        }
        this.drawClouds(bufferbuilder, mc.getRenderViewEntity().getPosX(), mc.getRenderViewEntity().getPosY(), mc.getRenderViewEntity().getPosZ(), cloudColor);
        bufferbuilder.finishDrawing();
        this.cloudsVBO.upload(bufferbuilder);

        this.textureManager.bindTexture(CLOUD_TEXTURE);
        matrixStackIn.push();
        if (this.cloudsVBO != null) {
            this.cloudsVBO.bindBuffer();
            DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL.setupBufferState(0L);

            for(int l = 0; l < 2; ++l) {
                if (l == 0) {
                    RenderSystem.colorMask(false, false, false, false);
                } else {
                    RenderSystem.colorMask(true, true, true, true);
                }

                this.cloudsVBO.draw(matrixStackIn.getLast().getMatrix(), 7);
            }

            VertexBuffer.unbindBuffer();
            DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL.clearBufferState();
        }

        matrixStackIn.pop();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableFog();
    }

    private void drawClouds(BufferBuilder bufferIn, double cloudsX, double cloudsY, double cloudsZ, Vector3d cloudsColor) {
        float cloudHeight = 1.0F;
        float cloudWidth = 1.0F;

        float r = (float) cloudsColor.x;
        float g = (float) cloudsColor.y;
        float b = (float) cloudsColor.z;
        float alpha = 0.8F;
        
        bufferIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        for(int x = -3; x <= 4; ++x) {
            for(int z = -3; z <= 4; ++z) {
                float cloudX = (float) ((x * cloudWidth) + cloudsX);
                float cloudY = (float) cloudsY;
                float cloudZ = (float) ((z * cloudWidth) + cloudsZ);
                float noiseOffset = (float) noise.getNoise(cloudX, cloudZ);

                //Bottom Face
                bufferIn.pos(cloudX + 0.0F,       cloudY + noiseOffset, cloudZ + 0.0F)      .tex(1, 1).color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();
                bufferIn.pos(cloudX + cloudWidth, cloudY + noiseOffset, cloudZ + 0.0F)      .tex(1, 1).color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();
                bufferIn.pos(cloudX + cloudWidth, cloudY + noiseOffset, cloudZ + cloudWidth).tex(1, 1).color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();
                bufferIn.pos(cloudX + 0.0F,       cloudY + noiseOffset, cloudZ + cloudWidth).tex(1, 1).color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();

                //Top Face
                bufferIn.pos(cloudX + 0.0F,       cloudY + noiseOffset + cloudHeight, cloudZ + 0.0F)      .tex(0, 0).color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
                bufferIn.pos(cloudX + cloudWidth, cloudY + noiseOffset + cloudHeight, cloudZ + 0.0F)      .tex(0, 0).color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
                bufferIn.pos(cloudX + cloudWidth, cloudY + noiseOffset + cloudHeight, cloudZ + cloudWidth).tex(0, 0).color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
                bufferIn.pos(cloudX + 0.0F,       cloudY + noiseOffset + cloudHeight, cloudZ + cloudWidth).tex(0, 0).color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
            }
        }
    }
}
