package birsy.clinker.client.render.world.cloud;

import birsy.clinker.client.render.ClinkerShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.block.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11C;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UpperLayerCloudRenderer extends BillboardCloudRenderer {
    public static final int CLOUD_CELL_SIZE = 5,
            LOWER_CLOUD_HEIGHT = 270,
            UPPER_CLOUD_HEIGHT = 300,
            CLOUD_HEIGHT = (LOWER_CLOUD_HEIGHT + UPPER_CLOUD_HEIGHT) / 2;
    DynamicShaderBlock<CloudPosition[]> instancePositionsBlock;
    int instanceCount = 0;
    VertexBuffer backingGridVbo;
    AABB bounds;

    @Override
    void rebuild(int renderRadiusInBlocks) {
        int radius = renderRadiusInBlocks / CLOUD_CELL_SIZE + 5;
        CloudPosition[] data = createInstanceData(radius);
        int size = data.length * CloudPosition.SIZE;

        if (instancePositionsBlock == null)
            instancePositionsBlock = ShaderBlock.dynamic(
                    ShaderBlock.BufferBinding.SHADER_STORAGE, size,
                    (array, buf) -> { for (CloudPosition pos : array) pos.upload(buf); }
            );
        if (instancePositionsBlock.getSize() != size)
            instancePositionsBlock.setSize(size);
        instancePositionsBlock.set(data);
        instanceCount = data.length;

        createBackingGrid(radius);

        this.bounds = new AABB(
                -renderRadiusInBlocks, LOWER_CLOUD_HEIGHT, -renderRadiusInBlocks,
                renderRadiusInBlocks, UPPER_CLOUD_HEIGHT, renderRadiusInBlocks
        ).inflate(10);
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
    private void createBackingGrid(int radius) {
        if (backingGridVbo != null) backingGridVbo.close();
        backingGridVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        backingGridVbo.bind();
        int diameter = radius * 2;
        int r2 = radius * radius;
        BufferBuilder vertexConsumer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (int x = 0; x < diameter; x++) {
            int pX = x - radius;
            for (int z = 0; z < diameter; z++) {
                int pZ = z - radius;
                if (Mth.lengthSquared(pX, pZ) < r2) {
                    vertexConsumer.addVertex(pX + 0, 0, pZ + 0).setUv((x + 0.0F) / diameter, (z + 0.0F) / diameter);
                    vertexConsumer.addVertex(pX + 1, 0, pZ + 0).setUv((x + 1.0F) / diameter, (z + 0.0F) / diameter);
                    vertexConsumer.addVertex(pX + 1, 0, pZ + 1).setUv((x + 1.0F) / diameter, (z + 1.0F) / diameter);
                    vertexConsumer.addVertex(pX + 0, 0, pZ + 1).setUv((x + 0.0F) / diameter, (z + 1.0F) / diameter);
                }
            }
        }
        backingGridVbo.upload(vertexConsumer.buildOrThrow());
        VertexBuffer.unbind();
    }

    @Override
    void free() {
        instancePositionsBlock.free();
        if (backingGridVbo != null) backingGridVbo.close();
    }

    @Override
    AABB getRenderBounds(OthershoreCloudRenderer renderer, double camX, double camY, double camZ, float partialTick) {
        return bounds.move(camX, 0, camZ);
    }

    @Override
    void preRender(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        CloudHoleTracker tracker = CloudHoleTracker.getInstance();
        if (tracker == null) return;
        tracker.updateFrame(partialTick);
    }

    @Override
    void render(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
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
        cloudShader.getUniformSafe("PlayerCloudCellOffset").setVector((float) camXOffset, (float) camZOffset);
        cloudShader.getUniformSafe("CloudCellSize").setInt(CLOUD_CELL_SIZE);
        cloudShader.getUniformSafe("InstanceCount").setInt(instanceCount);
        cloudShader.getUniformSafe("SkyColor").setVector(skyColor.x(), skyColor.y(), skyColor.z(), 1.0F);
        cloudShader.getUniformSafe("CloudHeight").setFloat(CLOUD_HEIGHT);

        VeilRenderSystem.bind("LayerInstancePositions", instancePositionsBlock);

        CloudHoleTracker tracker = CloudHoleTracker.getInstance();
        if (tracker != null) tracker.bind();

        VertexBuffer vbo = renderer.getBillboardVbo();

        float lowerLayerAlpha = (float) Mth.clampedMap(camY,
                UPPER_CLOUD_HEIGHT + CLOUD_CELL_SIZE * 2,
                UPPER_CLOUD_HEIGHT + CLOUD_CELL_SIZE * 4,
                1, 0
        );
        float upperLayerAlpha = (float) Mth.clampedMap(camY,
                LOWER_CLOUD_HEIGHT - CLOUD_CELL_SIZE * 2,
                LOWER_CLOUD_HEIGHT - CLOUD_CELL_SIZE * 4,
                1, 0
        );
        boolean lowerIsCloser = camY < CLOUD_HEIGHT;

        if (lowerIsCloser) {
            if (lowerLayerAlpha > 0.001) renderLayer(vbo, cloudShader, lowerLayerAlpha, true);
            if (upperLayerAlpha > 0.001) renderLayer(vbo, cloudShader, upperLayerAlpha, false);
        } else {
            if (upperLayerAlpha > 0.001) renderLayer(vbo, cloudShader, upperLayerAlpha, false);
            if (lowerLayerAlpha > 0.001) renderLayer(vbo, cloudShader, lowerLayerAlpha, true);
        }

        ShaderProgram.unbind();
        poseStack.popPose();
    }

    @Override
    void postRender(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        float fade = (float) (camY < CLOUD_HEIGHT ?
                Mth.map(camY, LOWER_CLOUD_HEIGHT - 20, LOWER_CLOUD_HEIGHT, 1, 0) :
                Mth.map(camY, UPPER_CLOUD_HEIGHT + 20, UPPER_CLOUD_HEIGHT, 1, 0)
        );
        if (fade < 0.01) return;

        int playerCloudX = Math.floorDiv(Mth.floor(camX), CLOUD_CELL_SIZE),
            playerCloudZ = Math.floorDiv(Mth.floor(camZ), CLOUD_CELL_SIZE);
        double camXOffset = camX - (playerCloudX * CLOUD_CELL_SIZE),
               camZOffset = camZ - (playerCloudZ * CLOUD_CELL_SIZE);

        poseStack.pushPose();
        poseStack.translate(-camXOffset, -camY, -camZOffset);
        Matrix4f pose = poseStack.last().pose();

        // draw the backing
        RenderSystem.disableCull();
        RenderSystem.stencilFunc(GL11C.GL_NOTEQUAL, 1, 0xFF);

        ShaderProgram backingGridShader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_LAYER_BACKING);
        backingGridShader.bindSamplers(0);
        backingGridShader.setDefaultUniforms(VertexFormat.Mode.QUADS, pose, projectionMatrix);
        backingGridShader.getUniformSafe("PlayerCloudCell").setVectorI(playerCloudX, playerCloudZ);
        backingGridShader.getUniformSafe("PlayerCloudCellOffset").setVector((float) camXOffset, (float) camZOffset);
        backingGridShader.getUniformSafe("CloudCellSize").setInt(CLOUD_CELL_SIZE);
        backingGridShader.getUniformSafe("InstanceCount").setInt(instanceCount);
        backingGridShader.getUniformSafe("SkyColor").setVector(skyColor.x(), skyColor.y(), skyColor.z(), 1.0F);
        backingGridShader.getUniformSafe("DisplacementDirection").setVector(0,camY < CLOUD_HEIGHT ? -1 : 1, 0);
        backingGridShader.getUniformSafe("CloudHeight").setFloat(CLOUD_HEIGHT);
        backingGridShader.getUniformSafe("AlphaMultiplier").setFloat(fade);

        backingGridVbo.bind();
        backingGridVbo.drawWithShader(pose, projectionMatrix, VeilRenderBridge.toShaderInstance(backingGridShader));
        VertexBuffer.unbind();

        RenderSystem.stencilFunc(GL11C.GL_ALWAYS, 0, 0xFF);
    }

    void renderLayer(VertexBuffer vbo, ShaderProgram cloudShader, float alpha, boolean lowerLayer) {
        cloudShader.getUniformSafe("AlphaMultiplier").setFloat(alpha);
        cloudShader.getUniformSafe("DisplacementDirection").setVector(0, lowerLayer ? -1 : 1, 0);
        vbo.bind();
        VeilRenderSystem.drawInstanced(vbo, instanceCount);
        VertexBuffer.unbind();
    }
}
