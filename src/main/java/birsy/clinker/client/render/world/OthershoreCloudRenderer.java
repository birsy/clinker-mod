package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

    private boolean cloudsNeedUpdate = true;
    @Nullable
    private VertexBuffer cloudsVBO;

    private int cloudsCheckX = Integer.MIN_VALUE;
    private int cloudsCheckY = Integer.MIN_VALUE;
    private int cloudsCheckZ = Integer.MIN_VALUE;
    private Vector3d cloudsCheckColor = Vector3d.ZERO;
    private CloudOption cloudOption;
    
    protected static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation(Clinker.MOD_ID, "textures/environment/cloud_map.png");

    private static final float cloudWidth = 8;
    private static final float cloudHeight = 60;

    public OthershoreCloudRenderer(Minecraft mc) {
        this.mc = mc;
        this.textureManager = mc.getTextureManager();
    }

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStackIn, ClientWorld world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) {}

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

        final int fogDensity = 10;
        
        for (int i = 0; i < fogDensity; i++) {
            float cloudHeight = (float) Math.floor(cloudsY / 4.0D) * 4.0F - i;
            
            for (int k = -3; k <= 4; ++k) {
                for (int l = -3; l <= 4; ++l) {
                    float cloudXPos = (float) (k * 8);
                    float cloudZPos = (float) (l * 8);
                    if (cloudHeight > -5.0F) {
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f11, f12, f13,0.095F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f11, f12, f13,0.095F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f11, f12, f13,0.095F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f11, f12, f13,0.095F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (cloudHeight <= 5.0F) {
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 8.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f5, f6, f7,0.095F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 8.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f5, f6, f7,0.095F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f5, f6, f7,0.095F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F - 9.765625E-4F, cloudZPos + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f5, f6, f7,0.095F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    if (k > -1) {
                        for (int i1 = 0; i1 < 8; ++i1) {
                            bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 4.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 4.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos(cloudXPos + (float) i1 + 0.0F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) i1 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (k <= 1) {
                        for (int j2 = 0; j2 < 8; ++j2) {
                            bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 0.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 4.0F, cloudZPos + 8.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 8.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 4.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos(cloudXPos + (float) j2 + 1.0F - 9.765625E-4F, cloudHeight + 0.0F, cloudZPos + 0.0F).tex((cloudXPos + (float) j2 + 0.5F) * 0.00390625F + f3, (cloudZPos + 0.0F) * 0.00390625F + f4).color(f8, f9, f10,0.095F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (l > -1) {
                        for (int k2 = 0; k2 < 8; ++k2) {
                            bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + (float) k2 + 0.0F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (l <= 1) {
                        for (int l2 = 0; l2 < 8; ++l2) {
                            bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 4.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 4.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferIn.pos(cloudXPos + 8.0F, cloudHeight + 0.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 8.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferIn.pos(cloudXPos + 0.0F, cloudHeight + 0.0F, cloudZPos + (float) l2 + 1.0F - 9.765625E-4F).tex((cloudXPos + 0.0F) * 0.00390625F + f3, (cloudZPos + (float) l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16,0.095F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
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
