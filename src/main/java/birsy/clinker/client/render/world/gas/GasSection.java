package birsy.clinker.client.render.world.gas;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.SectionPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static net.minecraft.core.SectionPos.SECTION_SIZE;

public class GasSection {
    private static final FastNoiseLite noise = new FastNoiseLite();

    final Level level;
    final SectionPos sectionPos;
    final int[] gasData;
    final boolean isEmpty;

    public GasSection(Level level, SectionPos sectionPos) {
        this.level = level;
        this.sectionPos = sectionPos;
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(4);
        noise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
        noise.SetDomainWarpAmp(3.0F);
        RandomSource randomSource = level.getRandom();
        boolean empty = true;
        this.gasData = new int[SECTION_SIZE * SECTION_SIZE * SECTION_SIZE];
        for (int x = 0; x < SECTION_SIZE; x++) {
            for (int y = 0; y < SECTION_SIZE; y++) {
                for (int z = 0; z < SECTION_SIZE; z++) {
                    int globalX = x + sectionPos.minBlockX();
                    int globalY = y + sectionPos.minBlockY();
                    int globalZ = z + sectionPos.minBlockZ();

                    int i = x + y * SECTION_SIZE + z * SECTION_SIZE * SECTION_SIZE;
                    float density = Math.max(0,  noise.GetNoise(globalX * 2, globalY * 2, globalZ * 2));
                    gasData[i] = FastColor.ARGB32.colorFromFloat(
                            density, //1F, 1F, 1F
                            randomSource.nextFloat(), randomSource.nextFloat(), randomSource.nextFloat()
                    );
                    if (density > 0.001) empty = false;
                }
            }
        }

        this.isEmpty = empty;
    }

    protected boolean isEmpty() {
        return this.isEmpty;
    }

    protected void uploadGasData(ByteBuffer buffer) {
        buffer.asIntBuffer().put(gasData);
    }

    protected void constructAndUploadLightData(ByteBuffer buffer) {
        DataLayer skyLightLayer = level.getLightEngine().getLayerListener(LightLayer.SKY).getDataLayerData(sectionPos);
        DataLayer blockLightLayer = level.getLightEngine().getLayerListener(LightLayer.BLOCK).getDataLayerData(sectionPos);
        for (int x = 0; x < SECTION_SIZE; x++) {
            for (int y = 0; y < SECTION_SIZE; y++) {
                for (int z = 0; z < SECTION_SIZE; z++) {
                    int blockLight = 0;
                    if (blockLightLayer != null) blockLight = blockLightLayer.get(z, y, x);
                    int skyLight = 15;
                    if (skyLightLayer != null) skyLight = skyLightLayer.get(z, y, x);
                    buffer.putInt(LightTexture.pack(blockLight, skyLight));
                }
            }
        }
    }
}
