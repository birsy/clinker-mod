package birsy.clinker.client.render.world.gas;

import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;

import java.nio.ByteBuffer;

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

        RandomSource randomSource = level.getRandom();
        boolean empty = true;
        this.gasData = new int[SECTION_SIZE * SECTION_SIZE * SECTION_SIZE];
        for (int x = 0; x < SECTION_SIZE; x++) {
            for (int y = 0; y < SECTION_SIZE; y++) {
                for (int z = 0; z < SECTION_SIZE; z++) {
                    int globalX = x + sectionPos.minBlockX();
                    int globalY = y + sectionPos.minBlockX();
                    int globalZ = z + sectionPos.minBlockX();

                    int i = x + y * SECTION_SIZE + z * SECTION_SIZE * SECTION_SIZE;
                    float density = Math.max(0,  noise.GetNoise(globalX, globalY, globalZ));
                    gasData[i] = FastColor.ARGB32.colorFromFloat(
                            density,
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
        DataLayer skyLight = level.getLightEngine().getLayerListener(LightLayer.SKY).getDataLayerData(sectionPos);
        DataLayer blockLight = level.getLightEngine().getLayerListener(LightLayer.BLOCK).getDataLayerData(sectionPos);
        for (int x = 0; x < SECTION_SIZE; x++) {
            for (int y = 0; y < SECTION_SIZE; y++) {
                for (int z = 0; z < SECTION_SIZE; z++) {
                    byte skyLightByte = (byte) skyLight.get(x, y, z);
                    byte blockLightByte = (byte) blockLight.get(x, y, z);
                    // pack sky and block light into single byte
                    buffer.put((byte) (skyLightByte | blockLightByte << 4));
                }
            }
        }
    }
}
