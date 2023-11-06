package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.FutureTask;

public class ChunkLightTexture implements AutoCloseable {
    public final DynamicTexture lightTexture;
    private final NativeImage lightPixels;
    public final ResourceLocation lightTextureLocation;
    private final GameRenderer renderer;
    private final Minecraft minecraft;
    public static int sampleWidth = 256;
    public static int sampleHeight = 64;
    public static int imageWidth = sampleWidth;
    public static int imageHeight = sampleWidth * sampleHeight;
    public ChunkLightTexture(GameRenderer pRenderer, Minecraft pMinecraft) {
        this.renderer = pRenderer;
        this.minecraft = pMinecraft;

        this.lightTexture = new DynamicTexture(sampleWidth, sampleWidth * sampleHeight, false);
        this.lightTextureLocation = this.minecraft.getTextureManager().register("chunk_light_map", this.lightTexture);
        this.lightPixels = this.lightTexture.getPixels();

        Clinker.LOGGER.info(this.lightTextureLocation);

        for(int x = 0; x < imageWidth; ++x) {
            for(int y = 0; y < imageHeight; ++y) {
                this.lightPixels.setPixelRGBA(x, y, -1);
            }
        }

        this.lightTexture.upload();
    }

    public FutureTask<Void> updateChunkLightTexture() {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Clinker.LOGGER.info("began updating lightmap");
            for(int x = 0; x < sampleWidth; ++x) {
                for(int y = 0; y < sampleHeight; ++y) {
                    for(int z = 0; z < sampleWidth; ++z) {
                        int pixelX = x;
                        int pixelY = (y * sampleWidth) + (z);

                        int color = -1;
                        if (this.minecraft.level != null) {
                            BlockPos pos = renderer.getMainCamera().getBlockPosition();
                            int halfWidth = sampleWidth / 2;
                            int halfHeight = sampleHeight / 2;
                            int r = this.minecraft.level.getBrightness(LightLayer.BLOCK, pos.offset(x - halfWidth, y - halfHeight, z - halfWidth));
                            int g = this.minecraft.level.getBrightness(LightLayer.SKY, pos.offset(x - halfWidth, y - halfHeight, z - halfWidth));
                            int b = 0;
                            color = new Color(b * 15, g * 15, r * 15).getRGB();
                        }
                        this.lightPixels.setPixelRGBA(pixelX, pixelY, color);
                    }
                }
            }
            this.lightTexture.upload();
            Clinker.LOGGER.info("finished updating lightmap");
        }, null);

        return task;
    }

    @Override
    public void close() {
        lightTexture.close();
        minecraft.getTextureManager().getTexture(this.lightTextureLocation).close();
        minecraft.getTextureManager().release(this.lightTextureLocation);
    }
}
