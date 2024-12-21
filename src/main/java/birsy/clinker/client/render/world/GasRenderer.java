package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.definition.ShaderBlock;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL43C.*;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class GasRenderer {
    public static final int SIZE = SectionPos.SECTION_SIZE;
    private static final ResourceLocation VOLUME_FRAMEBUFFER = Clinker.resource("volume");
    private static final ResourceLocation VOLUME_POST = Clinker.resource("volume");
    private static ShaderBlock<GasBlock[]> shaderBlock;

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            GasRenderer.render();
        }
    }

    private static SectionPos testSection = SectionPos.of(new BlockPos(62, 100, -254));
    public static void render() {
        testSection = SectionPos.of(new BlockPos(68, 139, -256));
        try {
            AdvancedFbo framebuffer = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VOLUME_FRAMEBUFFER);
            framebuffer.bind(true);

            if (Minecraft.getInstance().level == null) return;
            if (shaderBlock == null) {
                shaderBlock = ShaderBlock.withSize(GL_SHADER_STORAGE_BUFFER, SIZE * SIZE * SIZE * 2 * Integer.BYTES, (gasChunk, buffer) -> {
                    IntBuffer intBuffer = buffer.asIntBuffer();
                    for (int i = 0; i < gasChunk.length; i++) GasBlock.upload(gasChunk[i], intBuffer);
                });
            }
            shaderBlock.set(volumeDataFromSection(Minecraft.getInstance().level, testSection));
            VeilRenderSystem.bind("VolumetricData", shaderBlock);
            ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.VOLUME);

            AbstractTexture abstracttexture = Minecraft.getInstance().getTextureManager()
                    .getTexture(Minecraft.getInstance().gameRenderer.lightTexture().lightTextureLocation);
            shader.addSampler("LightTextureSampler", abstracttexture.getId());

            shader.setup();
            VeilRenderSystem.drawScreenQuad();
        } finally {
            VeilRenderSystem.unbind(shaderBlock);
            AdvancedFbo.unbind();
        }
    }

    private static GasBlock[] volumeDataFromSection(ClientLevel level, SectionPos section) {
        RandomSource random = RandomSource.create(0);
        GasBlock[] data = new GasBlock[SIZE * SIZE * SIZE];
        DataLayer skyLight = level.getLightEngine().getLayerListener(LightLayer.SKY).getDataLayerData(section);
        DataLayer blockLight = level.getLightEngine().getLayerListener(LightLayer.BLOCK).getDataLayerData(section);
        if (skyLight == null || blockLight == null) return data;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    int i = x + y * SIZE + z * SIZE * SIZE;
                    data[i] = new GasBlock(
                            0.5F, 0.5F, 0.5F,//random.nextFloat(), random.nextFloat(), random.nextFloat(),
                            0.15F,
                            blockLight.get(x, y, z), skyLight.get(x, y, z)
                    );
                }
            }
        }
        return data;
    }

    private record GasBlock(float r, float g, float b, float density, int blockLight, int skyLight) {
        public static void upload(GasBlock gas, IntBuffer buffer) {
            if (gas == null) {
                buffer.put(0).put(0);
            } else {
                buffer.put(FastColor.ARGB32.colorFromFloat(gas.density, gas.r, gas.g, gas.b))
                      .put(LightTexture.pack(gas.blockLight, gas.skyLight));
            }
        }
    }
}
