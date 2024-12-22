package birsy.clinker.client.render.world.gas;

import foundry.veil.api.client.render.shader.definition.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.definition.ShaderBlock;
import net.minecraft.core.SectionPos;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43C.*;
import static net.minecraft.core.SectionPos.SECTION_SIZE;

public class GasBufferManager {
    private static final int GAS_RENDER_RADIUS = 4;
    private static final int GAS_RENDER_SIZE = GAS_RENDER_RADIUS * 2;
    private static final int SECTION_COUNT = GAS_RENDER_SIZE * GAS_RENDER_SIZE * GAS_RENDER_SIZE;
    private static final int BLOCKS_PER_SECTION = SECTION_SIZE * SECTION_SIZE * SECTION_SIZE;
    // gas color + density packed in int, light uvs packed in byte
    private static final int BYTES_PER_GAS_BLOCK = Integer.BYTES;
    private static final int BYTES_PER_LIGHT_UV = 1;
    private static final int BYTES_PER_BLOCK = BYTES_PER_GAS_BLOCK + BYTES_PER_LIGHT_UV;

    SectionPos currentCenterPos;
    final GasSectionManager gasSectionManager;

    // turns a position (in player space) to indices into the gas data buffer
    int[] sectionToDataIndex;
    final int sectionToDataIndexBufferPointer;
    final DynamicShaderBlock<?> sectionToDataIndexShaderBlock;
    // the gas data buffer. each loaded sectionPos has its own block
    final GasSectionList gasData;
    final int gasDataBufferPointer;
    final DynamicShaderBlock<?> gasDataShaderBlock;

    public GasBufferManager(GasSectionManager gasSectionManager) {
        this.gasSectionManager = gasSectionManager;

        this.sectionToDataIndex = new int[GAS_RENDER_SIZE * GAS_RENDER_SIZE * GAS_RENDER_SIZE];
        this.gasData = new GasSectionList(GAS_RENDER_SIZE * GAS_RENDER_SIZE * GAS_RENDER_SIZE);

        this.sectionToDataIndexBufferPointer = glGenBuffers();
        this.sectionToDataIndexShaderBlock = ShaderBlock.wrapper(GL_SHADER_STORAGE_BUFFER, this.sectionToDataIndexBufferPointer);
        this.gasDataBufferPointer = glGenBuffers();
        this.gasDataShaderBlock = ShaderBlock.wrapper(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);

        // initialize the buffers
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.sectionToDataIndexBufferPointer);
        glBufferData(GL_SHADER_STORAGE_BUFFER, SECTION_COUNT, GL_DYNAMIC_DRAW);
        this.sectionToDataIndexShaderBlock.setSize(SECTION_COUNT);

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);
        // size is 1 bigger than it should be to include space for the "empty" gas sectionPos (always at index 0)
        // this simplifies the shader-side code a little.
        glBufferData(GL_SHADER_STORAGE_BUFFER, (SECTION_COUNT + 1) * BLOCKS_PER_SECTION * BYTES_PER_BLOCK, GL_DYNAMIC_DRAW);
        this.gasDataShaderBlock.setSize((SECTION_COUNT + 1) * BLOCKS_PER_SECTION * BYTES_PER_BLOCK);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void changeSection(SectionPos newCenterPos) {
        int[] newSectionToDataIndex = new int[GAS_RENDER_SIZE * GAS_RENDER_SIZE * GAS_RENDER_SIZE];

        for (int x = 0; x < GAS_RENDER_SIZE; x++) {
            for (int y = 0; y < GAS_RENDER_SIZE; y++) {
                for (int z = 0; z < GAS_RENDER_SIZE; z++) {
                    // remove any data that's outside the new range
                    SectionPos oldSectionPos = currentCenterPos.offset(
                            x - GAS_RENDER_RADIUS, y - GAS_RENDER_RADIUS, z - GAS_RENDER_RADIUS
                    );
                    int oldIndex = getSectionIndex(newCenterPos, oldSectionPos);
                    if (oldIndex == -1) {
                        int dataIndex = sectionToDataIndex[getSectionIndex(currentCenterPos, oldSectionPos)];
                        gasData.remove(dataIndex);
                    }

                    // fill in the new data
                    SectionPos sectionPos = newCenterPos.offset(
                            x - GAS_RENDER_RADIUS, y - GAS_RENDER_RADIUS, z - GAS_RENDER_RADIUS
                    );
                    int newIndex = getSectionIndex(newCenterPos, sectionPos);
                    oldIndex = getSectionIndex(currentCenterPos, sectionPos);
                    if (oldIndex != -1) { // the old stuff is in range of the new center - keep it!
                        newSectionToDataIndex[newIndex] = sectionToDataIndex[oldIndex];
                    } else { // we're in new territory - ask the section manager for what lies out here...
                        GasSection newSection = gasSectionManager.getGasSection(sectionPos);
                        int newSectionIndex = 0;
                        if (!newSection.isEmpty()) { // only upload data if there's useful data to upload!
                            newSectionIndex = gasData.add(newSection);
                            this.uploadSectionGasData(newSectionIndex);
                            this.uploadSectionLightData(newSectionIndex);
                        }

                        newSectionToDataIndex[newIndex] = newSectionIndex;
                    }
                }
            }
        }

        sectionToDataIndex = newSectionToDataIndex;
        currentCenterPos = newCenterPos;
        this.uploadSectionToDataIndex();
    }
    public void onLightUpdate(SectionPos pos) {
        int index = getSectionIndex(currentCenterPos, pos);
        if (index != -1) uploadSectionLightData(sectionToDataIndex[index]);
    }
    public void onGasUpdate(SectionPos pos) {
        int index = getSectionIndex(currentCenterPos, pos);
        if (index != -1) uploadSectionGasData(sectionToDataIndex[index]);
    }

    private void uploadSectionToDataIndex() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.sectionToDataIndexBufferPointer);
        glBufferSubData(this.sectionToDataIndexBufferPointer, 0, this.sectionToDataIndex);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
    private void uploadSectionGasData(int sectionIndex) {
        GasSection section = this.gasData.retrieve(sectionIndex);
        ByteBuffer buffer = ByteBuffer.allocate(BLOCKS_PER_SECTION * BYTES_PER_GAS_BLOCK);
        section.uploadGasData(buffer);
        buffer.rewind();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);
        glBufferSubData(this.gasDataBufferPointer, (long) sectionIndex * BLOCKS_PER_SECTION * BYTES_PER_BLOCK, buffer);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        MemoryUtil.memFree(buffer);
    }
    private void uploadSectionLightData(int sectionIndex) {
        GasSection section = this.gasData.retrieve(sectionIndex);
        ByteBuffer buffer = ByteBuffer.allocate(BLOCKS_PER_SECTION * BYTES_PER_LIGHT_UV);
        section.constructAndUploadLightData(buffer);
        buffer.rewind();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);
        glBufferSubData(this.gasDataBufferPointer,
                (long) sectionIndex * BLOCKS_PER_SECTION * BYTES_PER_BLOCK + (BLOCKS_PER_SECTION * BYTES_PER_GAS_BLOCK),
                buffer);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        MemoryUtil.memFree(buffer);
    }

    private int getSectionIndex(SectionPos center, SectionPos pos) {
        int x = (pos.x() - center.x()) + GAS_RENDER_RADIUS;
        int y = (pos.y() - center.y()) + GAS_RENDER_RADIUS;
        int z = (pos.z() - center.z()) + GAS_RENDER_RADIUS;
        // out-of-bounds!
        if (x < 0 || x >= GAS_RENDER_SIZE || y < 0 || y >= GAS_RENDER_SIZE || z < 0 || z >= GAS_RENDER_SIZE) return -1;
        return x + y * GAS_RENDER_SIZE + z * GAS_RENDER_SIZE * GAS_RENDER_SIZE;
    }
}
