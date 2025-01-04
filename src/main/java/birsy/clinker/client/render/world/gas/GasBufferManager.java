package birsy.clinker.client.render.world.gas;

import birsy.clinker.core.Clinker;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.definition.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.definition.ShaderBlock;
import net.minecraft.core.SectionPos;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL43C.*;
import static net.minecraft.core.SectionPos.SECTION_SIZE;

public class GasBufferManager {
    // todo: de-hardcode some of this.
    private static final int GAS_RENDER_RADIUS = 4;
    private static final int GAS_RENDER_SIZE = GAS_RENDER_RADIUS * 2;
    private static final int SECTION_COUNT = GAS_RENDER_SIZE * GAS_RENDER_SIZE * GAS_RENDER_SIZE;
    private static final int BLOCKS_PER_SECTION = SECTION_SIZE * SECTION_SIZE * SECTION_SIZE;
    // gas color + density packed in int, light uvs packed in byte
    private static final int BYTES_PER_GAS_DATA_BLOCK = Integer.BYTES;
    private static final int BYTES_PER_LIGHT_DATA_BLOCK = Integer.BYTES;
    private static final int BYTES_PER_BLOCK = BYTES_PER_GAS_DATA_BLOCK + BYTES_PER_LIGHT_DATA_BLOCK;

    boolean initialized = false;
    SectionPos currentCenterPos;
    final GasSectionManager gasSectionManager;

    // turns a position (in player space) to indices into the gas data buffer
    int[] sectionToDataIndex;
    final ShaderBlock<int[]> sectionToDataIndexShaderBlock;
    // the gas data buffer. each loaded sectionPos has its own block
    final GasSectionHeap gasData;
    final int gasDataBufferPointer;
    final DynamicShaderBlock<?> gasDataShaderBlock;

    private record QueuedGasData(int sectionIndex, GasSection data) {}
    Queue<QueuedGasData> gasDataToUpload;

    public GasBufferManager(GasSectionManager gasSectionManager) {
        this.gasSectionManager = gasSectionManager;

        this.sectionToDataIndex = new int[SECTION_COUNT];
        this.gasData = new GasSectionHeap(SECTION_COUNT);

        this.gasDataToUpload = new ArrayDeque<>();

        this.sectionToDataIndexShaderBlock = ShaderBlock.withSize(GL_SHADER_STORAGE_BUFFER, SECTION_COUNT * Integer.BYTES,
                (array, buffer) -> buffer.asIntBuffer().put(array));
        this.gasDataBufferPointer = glGenBuffers();
        this.gasDataShaderBlock = ShaderBlock.wrapper(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);

        // initialize the buffer
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);
        // size is 1 bigger than it should be to include space for the "empty" gas sectionPos (always at index 0)
        // this simplifies the shader-side code a little.
        glBufferData(GL_SHADER_STORAGE_BUFFER, (SECTION_COUNT + 1) * BLOCKS_PER_SECTION * BYTES_PER_BLOCK, GL_DYNAMIC_DRAW);
        this.gasDataShaderBlock.setSize((SECTION_COUNT + 1) * BLOCKS_PER_SECTION * BYTES_PER_BLOCK);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
    public void bind() {
        VeilRenderSystem.bind("SectionToDataIndexBuffer", this.sectionToDataIndexShaderBlock);
        VeilRenderSystem.bind("GasDataBuffer", this.gasDataShaderBlock);
    }

    public void updateQueue() {
        if (gasDataToUpload.isEmpty()) return;
        for (int i = 0; i < Math.min(3, gasDataToUpload.size()); i++) {
            if (this.gasData.full()) break;
            QueuedGasData data = gasDataToUpload.poll();
            int dataIndex = this.gasData.add(data.data);
            sectionToDataIndex[data.sectionIndex] = dataIndex;
            uploadSectionGasData(dataIndex);
            uploadSectionLightData(dataIndex);
        }
        this.uploadSectionToDataIndex();
    }
    public void markUnusedData() {
        int amountRemoved = 0;
        for (int i = 1; i < this.gasData.array.length; i++) {
            GasSection section = this.gasData.retrieve(i);
            if (section == null) continue;
            SectionPos pos = section.sectionPos;
            boolean outOfBounds =
                    pos.x() - currentCenterPos.x() < -GAS_RENDER_RADIUS || pos.x() - currentCenterPos.x() >= GAS_RENDER_RADIUS ||
                    pos.y() - currentCenterPos.y() < -GAS_RENDER_RADIUS || pos.y() - currentCenterPos.y() >= GAS_RENDER_RADIUS ||
                    pos.z() - currentCenterPos.z() < -GAS_RENDER_RADIUS || pos.z() - currentCenterPos.z() >= GAS_RENDER_RADIUS;
            if (outOfBounds) {
                if (this.gasData.remove(i)) amountRemoved++;
            }
        }
        if (amountRemoved > 0) Clinker.LOGGER.info("Marked {} gas data chunks as unused.", amountRemoved);
    }

    public void changeSection(SectionPos newCenterPos) {
        if (!initialized) {
            initialize(newCenterPos);
            return;
        }
        if (newCenterPos.equals(currentCenterPos)) return;

        int[] newSectionToDataIndex = new int[SECTION_COUNT];

        for (int x = -GAS_RENDER_RADIUS; x < GAS_RENDER_RADIUS; x++) {
            for (int y = -GAS_RENDER_RADIUS; y < GAS_RENDER_RADIUS; y++) {
                for (int z = -GAS_RENDER_RADIUS; z < GAS_RENDER_RADIUS; z++) {
                    int sectionIndex =
                            (x + GAS_RENDER_RADIUS) +
                            (y + GAS_RENDER_RADIUS) * GAS_RENDER_SIZE +
                            (z + GAS_RENDER_RADIUS) * GAS_RENDER_SIZE * GAS_RENDER_SIZE;
                    SectionPos sectionPos = newCenterPos.offset(x, y, z);
                    int existingDataIndex = gasData.getIndex(sectionPos);

                    if (existingDataIndex != -1) {
                        // this chunk is already in our data.
                        newSectionToDataIndex[sectionIndex] = existingDataIndex;
                    } else {
                        // this chunk is not in our data and must be retrieved.
                        GasSection section = gasSectionManager.getGasSection(sectionPos);
                        // queue the data for uploading to the gpu
                        this.gasDataToUpload.add(new QueuedGasData(sectionIndex, section));
                        newSectionToDataIndex[sectionIndex] = 0;
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

    private void initialize(SectionPos centerPos) {
        int[] newSectionToDataIndex = new int[SECTION_COUNT];
        for (int x = 0; x < GAS_RENDER_SIZE; x++) { for (int y = 0; y < GAS_RENDER_SIZE; y++) { for (int z = 0; z < GAS_RENDER_SIZE; z++) {
            SectionPos sectionPos = centerPos.offset(x - GAS_RENDER_RADIUS, y - GAS_RENDER_RADIUS, z - GAS_RENDER_RADIUS);
            int newSectionIndex = getSectionIndex(centerPos, sectionPos);
            GasSection section = gasSectionManager.getGasSection(sectionPos);
            newSectionToDataIndex[newSectionIndex] = 0;
            if (!section.isEmpty()) this.gasDataToUpload.add(new QueuedGasData(newSectionIndex, section));
        }}}
        sectionToDataIndex = newSectionToDataIndex;
        currentCenterPos = centerPos;
        this.uploadSectionToDataIndex();
        this.initialized = true;
    }

    private void uploadSectionToDataIndex() {
        VeilRenderSystem.bind("SectionToDataIndexBuffer", this.sectionToDataIndexShaderBlock);
        this.sectionToDataIndexShaderBlock.update(this.sectionToDataIndex);
    }
    private void uploadSectionGasData(int sectionIndex) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GasSection section = this.gasData.retrieve(sectionIndex);
            ByteBuffer buffer = stack.malloc(BLOCKS_PER_SECTION * BYTES_PER_GAS_DATA_BLOCK);
            section.uploadGasData(buffer);
            buffer.rewind();
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);
            glBufferSubData(
                    GL_SHADER_STORAGE_BUFFER,
                    (long) sectionIndex * (BLOCKS_PER_SECTION * BYTES_PER_BLOCK),
                    buffer
            );
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
            VeilRenderSystem.bind("GasDataBuffer", this.gasDataShaderBlock);
        }
    }
    private void uploadSectionLightData(int sectionIndex) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GasSection section = this.gasData.retrieve(sectionIndex);
            ByteBuffer buffer = stack.malloc(BLOCKS_PER_SECTION * BYTES_PER_LIGHT_DATA_BLOCK);
            section.constructAndUploadLightData(buffer);
            buffer.rewind();
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.gasDataBufferPointer);
            glBufferSubData(
                    GL_SHADER_STORAGE_BUFFER,
                    (long) sectionIndex * (BLOCKS_PER_SECTION * BYTES_PER_BLOCK) + (BLOCKS_PER_SECTION * BYTES_PER_GAS_DATA_BLOCK),
                    buffer
            );
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
            VeilRenderSystem.bind("GasDataBuffer", this.gasDataShaderBlock);
        }
    }

    private int getSectionIndex(SectionPos center, SectionPos pos) {
        int x = (pos.x() - center.x()) + GAS_RENDER_RADIUS;
        int y = (pos.y() - center.y()) + GAS_RENDER_RADIUS;
        int z = (pos.z() - center.z()) + GAS_RENDER_RADIUS;
        // out-of-bounds!
        if (x < 0 || x >= GAS_RENDER_SIZE ||
            y < 0 || y >= GAS_RENDER_SIZE ||
            z < 0 || z >= GAS_RENDER_SIZE) return -1;
        return x + y * GAS_RENDER_SIZE + z * GAS_RENDER_SIZE * GAS_RENDER_SIZE;
    }
}
