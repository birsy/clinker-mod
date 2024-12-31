package birsy.clinker.client.render.world.gas;

import birsy.clinker.client.render.ClinkerFramebuffers;
import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class GasRenderer {
    public static final int SIZE = SectionPos.SECTION_SIZE;
    private static final ResourceLocation VOLUME_POST = Clinker.resource("volume");

    private static GasSectionManager sectionManager;
    private static GasBufferManager bufferManager;

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            // temporarily disabled
            //GasRenderer.render(Minecraft.getInstance().level);
        }
    }

    public static void updateLight(int chunkX, int chunkZ) {
        if (bufferManager == null) return;
        for (int y = Minecraft.getInstance().level.getMinSection(); y < Minecraft.getInstance().level.getMaxSection(); y++) {
            bufferManager.onLightUpdate(SectionPos.of(new ChunkPos(chunkX, chunkZ), y));
        }
    }

    public static void render(ClientLevel level) {
        if (sectionManager == null) sectionManager = new GasSectionManager(level);
        if (bufferManager == null) bufferManager = new GasBufferManager(sectionManager);

        bufferManager.changeSection(SectionPos.of(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition()));
        bufferManager.updateQueue();
        AdvancedFbo framebuffer = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.VOLUME);
        framebuffer.bind(true);

        ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.VOLUME);
        shader.setInt("BlueNoiseOffset", (int)(Minecraft.getInstance().levelRenderer.ticks * 100));
        bufferManager.bind();
        AbstractTexture abstracttexture = Minecraft.getInstance().getTextureManager()
                .getTexture(Minecraft.getInstance().gameRenderer.lightTexture().lightTextureLocation);
        shader.addSampler("LightTextureSampler", abstracttexture.getId());
        shader.setup();
        VeilRenderSystem.drawScreenQuad();
        ShaderProgram.unbind();
        AdvancedFbo.unbind();
    }
}
