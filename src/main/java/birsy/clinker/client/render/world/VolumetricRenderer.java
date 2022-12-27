package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class VolumetricRenderer {
    private static Minecraft minecraft = Minecraft.getInstance();
    @Nullable
    private static PostChain volumetricChain;
    public static Optional<PostChain> getPostChain() {
        return Optional.ofNullable(volumetricChain);
    }
    private static ChunkLightTexture texture;
    public static Optional<ChunkLightTexture> getChunkLightTexture() {
        return Optional.ofNullable(texture);
    }

    private static RenderTarget volumetricBuffer;

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            render();
        }
    }

    //debug
    @SubscribeEvent
    public static void inputEvent(InputEvent.Key event) {
        if (event.getKey() == InputConstants.KEY_R && Clinker.devmode) {
            updateLightmap();
        }
    }

    public static void render() {
        getPostChain().ifPresent((postChain) -> {
            //custom postchain processing because minecraft's is fucking ass and doesnt support custom uniforms :P
            float partialTick = minecraft.getFrameTime();
            if (partialTick < postChain.lastStamp) {
                postChain.time += 1.0F - postChain.lastStamp;
                postChain.time += partialTick;
            } else {
                postChain.time += partialTick - postChain.lastStamp;
            }


            Camera camera = minecraft.gameRenderer.getMainCamera();
            Vec3 cameraPos = camera.getPosition();
            Vector3f cameraLook = camera.getLookVector();
            Vector3f cameraUp = camera.getUpVector();
            Vector3f cameraLeft = camera.getLeftVector();

            float clipStart = 0.05F;
            float clipEnd = minecraft.gameRenderer.getDepthFar();
            float fov = (float) minecraft.gameRenderer.getFov(camera, partialTick, true);
            int aspectX = minecraft.getWindow().getWidth();
            int aspectY = minecraft.getWindow().getHeight();

            for(PostPass pass : postChain.passes) {
                pass.effect.safeGetUniform("GameTime").set(minecraft.levelRenderer.ticks + minecraft.getPartialTick());

                pass.effect.safeGetUniform("CameraPosition").set((float) cameraPos.x(), (float) cameraPos.y(), (float) cameraPos.z());
                pass.effect.safeGetUniform("CameraForward").set(cameraLook);
                pass.effect.safeGetUniform("CameraLeft").set(cameraLeft);
                pass.effect.safeGetUniform("CameraUp").set(cameraUp);

                pass.effect.safeGetUniform("NearPlaneDistance").set(clipStart);
                pass.effect.safeGetUniform("FarPlaneDistance").set(clipEnd);
                pass.effect.safeGetUniform("FOV").set(fov);
                pass.effect.safeGetUniform("ScreenSize").set((float) aspectX, (float) aspectY);

                pass.effect.safeGetUniform("TimeOfDay").set(minecraft.level.getTimeOfDay(minecraft.getPartialTick()) / 24000.0F);

                getChunkLightTexture().ifPresent((chunkLight) -> pass.effect.setSampler("ChunkLightSampler", chunkLight.lightTexture::getId));

         //       pass.process(postChain.time / 20.0F);
            }
        });
       // minecraft.getMainRenderTarget().bindWrite(false);
    }

    public static void init() throws IOException {
        deInit();
        Clinker.LOGGER.info("initializing volumetrics");
        ResourceLocation resourcelocation = new ResourceLocation(Clinker.MOD_ID, "shaders/post/volumetrics.json");

        try {
            PostChain postchain = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), minecraft.getMainRenderTarget(), resourcelocation);
            postchain.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());

            ChunkLightTexture chunkLight = new ChunkLightTexture(minecraft.gameRenderer, minecraft);
            chunkLight.updateChunkLightTexture().run();

            //volumetricBuffer = postchain.getTempTarget("volumetric");

            for(PostPass pass : postchain.passes) {
                RenderSystem.setShaderTexture(0, chunkLight.lightTextureLocation);
                minecraft.getTextureManager().bindForSetup(chunkLight.lightTextureLocation);
                pass.addAuxAsset("ChunkLightSampler",  chunkLight.lightTexture::getId, chunkLight.imageWidth, chunkLight.imageHeight);
            }

            volumetricChain = postchain;
            texture = chunkLight;
        } catch (Exception exception) {
            String e = exception instanceof JsonSyntaxException ? "parse" : "load";
            String string = "Failed to " + e + " shader: " + resourcelocation;
            throw exception;
        }
    }

    public static void deInit() {
        close();
        volumetricChain = null;
        texture = null;
        if (volumetricBuffer != null) {
            volumetricBuffer.destroyBuffers();
        }
    }

    public static void close() {
        getPostChain().ifPresent((postChain -> postChain.close()));
        getChunkLightTexture().ifPresent((texture -> texture.close()));
    }

    public static void resize(int width, int height) {
        getPostChain().ifPresent((postChain -> postChain.resize(width, height)));
    }

    public static void updateLightmap() {
        getChunkLightTexture().ifPresent(texture -> texture.updateChunkLightTexture().run());
    }
}
