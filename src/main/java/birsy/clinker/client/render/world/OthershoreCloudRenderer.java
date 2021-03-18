package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.ICloudRenderHandler;

import javax.annotation.Nullable;

public class OthershoreCloudRenderer implements ICloudRenderHandler {
    private final TextureManager textureManager;
    private final Minecraft mc;

    private boolean cloudsNeedUpdate = true;
    @Nullable
    private VertexBuffer cloudsVBO;

    private int cloudsCheckX = Integer.MIN_VALUE;
    private int cloudsCheckY = Integer.MIN_VALUE;
    private int cloudsCheckZ = Integer.MIN_VALUE;
    private Vector3d cloudsCheckColor = Vector3d.ZERO;
    private CloudOption cloudOption;
    
    protected static final ResourceLocation CLOUD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/cloud.png");

    private static final float cloudWidth = 8;
    private static final float cloudHeight = 60;

    public OthershoreCloudRenderer(Minecraft mc) {
        this.mc = mc;
        this.textureManager = mc.getTextureManager();
    }

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStackIn, ClientWorld world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) {
        float f = world.func_239132_a_().func_239213_a_();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableFog();
        RenderSystem.depthMask(true);
        
        double d1 = ((float) world.getGameTime() + partialTicks) * 0.03F;
        double d2 = (viewEntityX + d1) / 12.0D;
        double d3 = f - (float) viewEntityY + 0.33F;
        double d4 = viewEntityZ / 12.0D + (double) 0.33F;
        d2 = d2 - (double) (MathHelper.floor(d2 / 2048.0D) * 2048);
        d4 = d4 - (double) (MathHelper.floor(d4 / 2048.0D) * 2048);
        float f3 = (float) (d2 - (double) MathHelper.floor(d2));
        float f4 = (float) (d3 / 4.0D - (double) MathHelper.floor(d3 / 4.0D)) * 4.0F;
        float f5 = (float) (d4 - (double) MathHelper.floor(d4));
        Vector3d vector3d = world.getCloudColor(partialTicks);
        int i = (int) Math.floor(d2);
        int j = (int) Math.floor(d3 / 4.0D);
        int k = (int) Math.floor(d4);
        
        if (i != this.cloudsCheckX || j != this.cloudsCheckY || k != this.cloudsCheckZ || this.mc.gameSettings.getCloudOption() != this.cloudOption || this.cloudsCheckColor.squareDistanceTo(vector3d) > 2.0E-4D) {
            this.cloudsCheckX = i;
            this.cloudsCheckY = j;
            this.cloudsCheckZ = k;
            this.cloudsCheckColor = vector3d;
            this.cloudOption = this.mc.gameSettings.getCloudOption();
            this.cloudsNeedUpdate = true;
        }
        
        if (this.cloudsNeedUpdate) {
            this.cloudsNeedUpdate = false;
            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            if (this.cloudsVBO != null) {
                this.cloudsVBO.close();
            }
            this.cloudsVBO = new VertexBuffer(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

            this.drawClouds(bufferbuilder, d2, d3, d4, vector3d);
            this.createCloud(bufferbuilder, new Vector3d(viewEntityX + 8, viewEntityX + 20, viewEntityZ + 8), vector3d);

            bufferbuilder.finishDrawing();
            this.cloudsVBO.upload(bufferbuilder);
        }
        
        this.textureManager.bindTexture(CLOUD_TEXTURE);
        matrixStackIn.push();
        matrixStackIn.scale(12.0F, 1.0F, 12.0F);
        matrixStackIn.translate(-f3, f4, -f5);
        if (this.cloudsVBO != null) {
            this.cloudsVBO.bindBuffer();
            DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL.setupBufferState(0L);
            int i1 = this.cloudOption == CloudOption.FANCY ? 0 : 1;

            for (int l = i1; l < 2; ++l) {
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
        float f3 = (float) MathHelper.floor(cloudsX) * 0.00390625F;
        float f4 = (float) MathHelper.floor(cloudsZ) * 0.00390625F;
        float f5 = (float) cloudsColor.x;
        float f6 = (float) cloudsColor.y;
        float f7 = (float) cloudsColor.z;
        float f8 = f5 * 0.9F;
        float f9 = f6 * 0.9F;
        float f10 = f7 * 0.9F;
        float f11 = f5 * 0.7F;
        float f12 = f6 * 0.7F;
        float f13 = f7 * 0.7F;
        float f14 = f5 * 0.8F;
        float f15 = f6 * 0.8F;
        float f16 = f7 * 0.8F;
        bufferIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        float cloudHeight = (float) Math.floor(cloudsY / 4.0D) * 4.0F;

        for (int k = -3; k <= 4; ++k) {
            for (int l = -3; l <= 4; ++l) {
                float cloudXPos = (float) (k * 8);
                float cloudZPos = (float) (l * 8);
                if (cloudHeight > -5.0F) {
                    bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                }

                if (cloudHeight <= 5.0F) {
                    bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 8.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 8.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                }

                if (k > -1) {
                    for (int i1 = 0; i1 < 8; ++i1) {
                        bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 4.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 4.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                    }
                }

                if (k <= 1) {
                    for (int j2 = 0; j2 < 8; ++j2) {
                        bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 4.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 4.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                    }
                }

                if (l > -1) {
                    for (int k2 = 0; k2 < 8; ++k2) {
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                    }
                }

                if (l <= 1) {
                    for (int l2 = 0; l2 < 8; ++l2) {
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                    }
                }
            }
        }
    }

    //Does this work? Probably not!
    //Too bad!
    private void createCloud(BufferBuilder bufferIn, Vector3d cloudPosition, Vector3d cloudsColor) {
        double x = cloudPosition.getX();
        double y = cloudPosition.getY();
        double z = cloudPosition.getZ();

        float r = (float) cloudsColor.getX();
        float g = (float) cloudsColor.getY();
        float b = (float) cloudsColor.getZ();
        float a = 1.0F;

        float height = (float) y + cloudHeight;

        bufferIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        //Bottom face
        bufferIn.pos(x, y,z + cloudWidth)                 .tex(0.0F, 0.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, y, z + cloudWidth).tex(0.0F, 0.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, y, z + cloudWidth).tex(0.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x, y, z)                                .tex(0.0F, 1.0F).color(r, g, b, a).endVertex();

        //North Face
        bufferIn.pos(x, y, z)                    .tex(0.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x, y,z + cloudWidth)     .tex(1.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x, height,z + cloudWidth).tex(1.0F, 0.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x, height, z)               .tex(0.0F, 0.0F).color(r, g, b, a).endVertex();

        //South Face
        bufferIn.pos(x + cloudWidth, y, z)                    .tex(0.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, y, z + cloudWidth)    .tex(1.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, height,z + cloudWidth).tex(1.0F, 0.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, height, z)               .tex(0.0F, 0.0F).color(r, g, b, a).endVertex();

        //East Face
        bufferIn.pos(x, y,z + cloudWidth)                      .tex(0.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, y, z + cloudWidth)     .tex(1.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, height, z + cloudWidth).tex(1.0F, 0.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x,height, z + cloudWidth)                 .tex(0.0F, 0.0F).color(r, g, b, a).endVertex();

        //West Face
        bufferIn.pos(x, y, z)                     .tex(0.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, y, z)     .tex(1.0F, 1.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x + cloudWidth, height, z).tex(1.0F, 0.0F).color(r, g, b, a).endVertex();
        bufferIn.pos(x, height, z)                .tex(0.0F, 0.0F).color(r, g, b, a).endVertex();

        Clinker.LOGGER.info("Cloud rendered from " + cloudPosition.toString() + " to " + cloudPosition.add(cloudWidth, cloudHeight, cloudWidth).toString());
    }
}
