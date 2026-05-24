package birsy.clinker.client.render.world.cloud;

import birsy.clinker.client.render.ClinkerShaders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.block.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UpperLayerCloudRenderer extends BillboardCloudRenderer {
    DynamicShaderBlock<CloudPosition[]> instancePositions;
    int instanceCount = 0;
    public static final int CLOUD_CELL_SIZE = 5,
                     LOWER_CLOUD_HEIGHT = 270,
                     UPPER_CLOUD_HEIGHT = 300,
                     CLOUD_HEIGHT = (LOWER_CLOUD_HEIGHT + UPPER_CLOUD_HEIGHT) / 2;

    @Override
    void rebuild(int renderRadiusInBlocks) {
        CloudPosition[] data = createInstanceData(renderRadiusInBlocks / CLOUD_CELL_SIZE + 3);
        int size = data.length * CloudPosition.SIZE;

        if (instancePositions == null)
            instancePositions = ShaderBlock.dynamic(
                    ShaderBlock.BufferBinding.SHADER_STORAGE, size,
                    (array, buf) -> { for (CloudPosition pos : array) pos.upload(buf); }
            );
        if (instancePositions.getSize() != size)
            instancePositions.setSize(size);
        instancePositions.set(data);
        instanceCount = data.length;
    }
    private CloudPosition[] createInstanceData(int radius) {
        int diameter = radius * 2;
        int r2 = radius * radius;
        int area = (int) Math.ceil(Math.PI * r2);
        List<CloudPosition> cloudPositions = new ArrayList<>(area + 5);

        for (int x = 0; x < diameter; x++) {
            int pX = x - radius;
            for (int z = 0; z < diameter; z++) {
                int pZ = z - radius;
                if (Mth.lengthSquared(pX, pZ) < r2)
                    cloudPositions.add(new CloudPosition(pX, pZ));
            }
        }

        cloudPositions.sort(Comparator.comparingInt(pos -> pos.x * pos.x + pos.z * pos.z));

        return cloudPositions.toArray(new CloudPosition[0]);
    }
    private record CloudPosition(int x, int z) {
        static final int SIZE = Integer.BYTES * 2;
        private void upload(ByteBuffer buffer) {
            buffer.putInt(x); buffer.putInt(z);
        }
    }
    @Override
    void free() {
        instancePositions.free();
    }

    @Override
    void renderSolid(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        render(renderer, poseStack, camX, camY, camZ, projectionMatrix, skyColor, false);
    }

    @Override
    void renderTranslucent(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        render(renderer, poseStack, camX, camY, camZ, projectionMatrix, skyColor, true);
    }

    void render(OthershoreCloudRenderer renderer, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor, boolean transparent) {
        int playerCloudX = Math.floorDiv(Mth.floor(camX), CLOUD_CELL_SIZE),
            playerCloudZ = Math.floorDiv(Mth.floor(camZ), CLOUD_CELL_SIZE);

        double camXOffset = camX - (playerCloudX * CLOUD_CELL_SIZE), camZOffset = camZ - (playerCloudZ * CLOUD_CELL_SIZE);

        poseStack.pushPose();
        poseStack.translate(-camXOffset, -camY, -camZOffset);
        Matrix4f pose = poseStack.last().pose();

        ShaderProgram cloudShader = VeilRenderSystem.setShader(ClinkerShaders.INSTANCED_CLOUD_BILLBOARD_LAYER);

        cloudShader.bind();
        cloudShader.bindSamplers(0);
        cloudShader.setDefaultUniforms(VertexFormat.Mode.QUADS, pose, projectionMatrix);

        cloudShader.getUniformSafe("PlayerCloudCell").setVectorI(playerCloudX, playerCloudZ);
        cloudShader.getUniformSafe("PlayerCloudCellOffset").setFloats((float) camXOffset, (float) camZOffset);
        cloudShader.getUniformSafe("CloudCellSize").setInt(CLOUD_CELL_SIZE);
        cloudShader.getUniformSafe("InstanceCount").setInt(instanceCount);
        cloudShader.getUniformSafe("SkyColor").setVector(skyColor.x() * 0.8F, skyColor.y() * 0.8F, skyColor.z() * 0.8F, 1.0F);
        cloudShader.getUniformSafe("Transparent").setInt(transparent ? 1 : 0);

        VeilRenderSystem.bind("LayerInstancePositions", instancePositions);

        VertexBuffer vbo = renderer.getBillboardVbo();

        int lowerThreshold = LOWER_CLOUD_HEIGHT - CLOUD_CELL_SIZE * 2,
            upperThreshold = UPPER_CLOUD_HEIGHT + CLOUD_CELL_SIZE * 2;
        if (camY <= lowerThreshold) {
            // only render lower
            renderLayer(vbo, cloudShader, true);
        } else if (camY > lowerThreshold && camY < upperThreshold) {
            // kind of opaque boolean logic here, but
            // renders the closer one first if solid
            // and the closer one last if translucent
            boolean lowerIsCloser = camY < CLOUD_HEIGHT;
            boolean lowerFirst = lowerIsCloser != transparent;
            renderLayer(vbo, cloudShader, lowerFirst);
            renderLayer(vbo, cloudShader, !lowerFirst);
        } else {
            // only render upper
            renderLayer(vbo, cloudShader, false);
        }
        ShaderProgram.unbind();
        poseStack.popPose();
    }

    void renderLayer(VertexBuffer vbo, ShaderProgram cloudShader, boolean lowerLayer) {
        cloudShader.getUniformSafe("DisplacementDirection").setVector(0, lowerLayer ? -1 : 1, 0);
        cloudShader.getUniformSafe("CloudHeight").setFloat(lowerLayer ? LOWER_CLOUD_HEIGHT : UPPER_CLOUD_HEIGHT);

        vbo.bind();
        VeilRenderSystem.drawInstanced(vbo, instanceCount);
        VertexBuffer.unbind();
    }
}
