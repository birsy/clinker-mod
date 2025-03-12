package birsy.clinker.client.render.world.gas;

import birsy.clinker.client.render.ClinkerFramebuffers;
import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.forge.event.ForgeVeilPostProcessingEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static org.lwjgl.opengl.GL11.*;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class GasRenderer {
    public static final ResourceLocation VOLUME_POST = Clinker.resource("volume");

    private static GasSectionManager sectionManager;
    private static GasBufferManager bufferManager;

    @SubscribeEvent
    public static void startGasCompositePipeline(ForgeVeilPostProcessingEvent event) {
//        if (Minecraft.getInstance().level == null) return;
//        if (!event.getName().equals(VOLUME_POST)) return;
//        GasRenderer.render(Minecraft.getInstance().level);
    }

    public static void updateLight(int chunkX, int chunkZ) {
        if (bufferManager == null) return;
        for (int y = Minecraft.getInstance().level.getMinSection(); y < Minecraft.getInstance().level.getMaxSection(); y++) {
            //bufferManager.onLightUpdate(SectionPos.of(new ChunkPos(chunkX, chunkZ), y));
        }
    }

    public static void render(ClientLevel level) {
        ProfilerFiller profiler = Minecraft.getInstance().getProfiler();

        profiler.push("volumetrics");
        if (sectionManager == null) sectionManager = new GasSectionManager(level);
        if (bufferManager == null) bufferManager = new GasBufferManager(sectionManager);

        profiler.push("upload_volume_data");
        bufferManager.changeSection(SectionPos.of(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition()));
        bufferManager.markUnusedData();
        bufferManager.updateQueue();
        AdvancedFbo framebuffer = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.VOLUME);
        framebuffer.bind(true);

        profiler.popPush("render_volume");

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.VOLUME);
        shader.setInt("BlueNoiseOffset", (int)(System.currentTimeMillis() % 512));
        bufferManager.bind();
        AbstractTexture abstracttexture = Minecraft.getInstance().getTextureManager()
                .getTexture(Minecraft.getInstance().gameRenderer.lightTexture().lightTextureLocation);
        shader.setSampler("LightTextureSampler", abstracttexture.getId());
        shader.bind();
        shader.bindSamplers(0);
        shader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_STRIP);
        VeilRenderSystem.drawScreenQuad();
        ShaderProgram.unbind();
        AdvancedFbo.unbind();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        profiler.popPush("composite_volume");
        PostPipeline volumeCompositePipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(VOLUME_POST);
        if (volumeCompositePipeline != null) {
            VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(volumeCompositePipeline, false);
        }
        profiler.pop();

        profiler.pop();
    }
}
