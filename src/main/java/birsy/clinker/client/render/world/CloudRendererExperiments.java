package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerFramebuffers;
import birsy.clinker.client.render.ClinkerShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.block.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.Comparator;

public class CloudRendererExperiments {
    VertexBuffer billboardVBO;

    DynamicShaderBlock<int[]> instancePositionsBlock;
    int instanceCount = 0;

    static int LOWER_CLOUD_HEIGHT = 270, UPPER_CLOUD_HEIGHT = 290, CLOUD_HEIGHT = (LOWER_CLOUD_HEIGHT + UPPER_CLOUD_HEIGHT) / 2;

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        renderCloudDensityTexture(ticks, partialTick);
        renderCloudSpriteTexture(ticks, partialTick);

        if (billboardVBO == null) rebuild();

        int cloudCellSize = 5;
        int playerCloudX = Math.floorDiv(Mth.floor(camX), cloudCellSize),
            playerCloudZ = Math.floorDiv(Mth.floor(camZ), cloudCellSize);

        double camXOffset = camX - (playerCloudX * cloudCellSize), camZOffset = camZ - (playerCloudZ * cloudCellSize);

        poseStack.pushPose();
        poseStack.translate(-camXOffset, -camY, -camZOffset);
        Matrix4f pose = poseStack.last().pose();

        ShaderProgram cloudShader = VeilRenderSystem.setShader(ClinkerShaders.INSTANCED_CLOUD_BILLBOARD);
        cloudShader.getUniformSafe("SkyColor").setVector(skyColor.x() * 0.8F, skyColor.y() * 0.8F, skyColor.z() * 0.8F, 1.0F);

        UPPER_CLOUD_HEIGHT = 300;
        CLOUD_HEIGHT = (LOWER_CLOUD_HEIGHT + UPPER_CLOUD_HEIGHT) / 2;

        int lowerThreshold = LOWER_CLOUD_HEIGHT - cloudCellSize * 2, upperThreshold = UPPER_CLOUD_HEIGHT + cloudCellSize * 2;
        if (camY < lowerThreshold) {
            renderCloudLayer(playerCloudX, playerCloudZ, cloudCellSize, true, cloudShader, pose, projectionMatrix);
        } else if (camY > lowerThreshold && camY < upperThreshold) {
            // closer one renders last
            if (camY < CLOUD_HEIGHT) {
                renderCloudLayer(playerCloudX, playerCloudZ, cloudCellSize, false, cloudShader, pose, projectionMatrix);
                renderCloudLayer(playerCloudX, playerCloudZ, cloudCellSize, true, cloudShader, pose, projectionMatrix);
            } else {
                renderCloudLayer(playerCloudX, playerCloudZ, cloudCellSize, true, cloudShader, pose, projectionMatrix);
                renderCloudLayer(playerCloudX, playerCloudZ, cloudCellSize, false, cloudShader, pose, projectionMatrix);
            }
        } else {
            renderCloudLayer(playerCloudX, playerCloudZ, cloudCellSize, false, cloudShader, pose, projectionMatrix);
        }

        poseStack.popPose();
    }

    void renderCloudDensityTexture(int ticks, double partialTicks) {
        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.CLOUD_DENSITY);
        fbo.bind(true);
        ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_DENSITY);
        shader.bind();
        shader.bindSamplers(0);
        shader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_STRIP);
        shader.getUniform("GameTime").setFloat((float) ((ticks / 20.0 + partialTicks / 20.0) * 0.3));
        VeilRenderSystem.drawScreenQuad();
        ShaderProgram.unbind();
        AdvancedFbo.unbind();
    }

    void renderCloudSpriteTexture(int ticks, double partialTicks) {
        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.CLOUD_SPRITE);
        fbo.bind(true);
        ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_SPRITE);
        shader.bind();
        shader.bindSamplers(0);
        shader.setDefaultUniforms(VertexFormat.Mode.TRIANGLE_STRIP);
        shader.getUniform("GameTime").setFloat((float) ((ticks / 20.0 + partialTicks / 20.0) * 0.3));
        VeilRenderSystem.drawScreenQuad();
        ShaderProgram.unbind();
        AdvancedFbo.unbind();
    }

    void renderCloudLayer(int playerCloudX, int playerCloudZ, int cloudCellSize, boolean down, ShaderProgram cloudShader, Matrix4f pose, Matrix4f projectionMatrix) {
        cloudShader.getUniformSafe("Transparent").setInt(0);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        renderBillboards(playerCloudX, playerCloudZ, cloudCellSize, down, cloudShader, pose, projectionMatrix);

        cloudShader.getUniformSafe("Transparent").setInt(1);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        renderBillboards(playerCloudX, playerCloudZ, cloudCellSize, down, cloudShader, pose, projectionMatrix);
    }

    void renderBillboards(int playerCloudX, int playerCloudZ, int cloudCellSize, boolean down, ShaderProgram cloudShader, Matrix4f pose, Matrix4f projectionMatrix) {
        cloudShader.bind();
        cloudShader.bindSamplers(0);
        cloudShader.setDefaultUniforms(VertexFormat.Mode.QUADS, pose, projectionMatrix);
        VeilRenderSystem.bind("InstancePositions", instancePositionsBlock);

        cloudShader.getUniformSafe("PlayerCloudCell").setVectorI(playerCloudX, playerCloudZ);
        cloudShader.getUniformSafe("CloudCellSize").setInt(cloudCellSize);
        cloudShader.getUniformSafe("InstanceCount").setInt(instanceCount);

        cloudShader.getUniformSafe("DisplacementDirection").setVector(0, down ? -1 : 1, 0);
        cloudShader.getUniformSafe("CloudHeight").setFloat(down ? LOWER_CLOUD_HEIGHT : UPPER_CLOUD_HEIGHT);

        billboardVBO.bind();
        VeilRenderSystem.drawInstanced(billboardVBO, instanceCount);
        VertexBuffer.unbind();
        ShaderProgram.unbind();
    }

    void rebuild() {
        int[] data = createInstanceData(151);
        int size = data.length * Integer.BYTES;

        if (instancePositionsBlock == null)
            instancePositionsBlock = ShaderBlock.dynamic(
                    ShaderBlock.BufferBinding.SHADER_STORAGE, size,
                    (arr, buf) -> buf.asIntBuffer().put(arr)
            );
        if (instancePositionsBlock.getSize() != size)
            instancePositionsBlock.setSize(size);
        instancePositionsBlock.set(data);

        instanceCount = data.length / 2;

        createBillboardVBO();
    }

    int[] createInstanceData(int radius) {
        int diameter = radius * 2;
        Position[] positions = new Position[diameter * diameter];

        int i = 0;
        for (int x = 0; x < diameter; x++) {
            int pX = x - radius;
            for (int z = 0; z < diameter; z++) {
                int pZ = z - radius;
                positions[i++] = new Position(pX, pZ);
            }
        }

        Arrays.sort(positions, Comparator.<Position>comparingInt(pos -> pos.x * pos.x + pos.z * pos.z));

        // output to flat array
        int length = positions.length * 2;
        int[] data = new int[length];
        for (int j = 0; j < positions.length; j++) {
            Position pos = positions[j];
            data[j * 2 + 0] = pos.x;
            data[j * 2 + 1] = pos.z;
        }

        return data;
    }
    private record Position(int x, int z) {}

    private void createBillboardVBO() {
        if (billboardVBO != null) billboardVBO.close();
        billboardVBO = new VertexBuffer(VertexBuffer.Usage.STATIC);
        billboardVBO.bind();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bufferBuilder.addVertex(0, 0, 0)
                .setUv(-1, -1);
        bufferBuilder.addVertex(0, 0, 0)
                .setUv( 1, -1);
        bufferBuilder.addVertex(0, 0, 0)
                .setUv( 1,  1);
        bufferBuilder.addVertex(0, 0, 0)
                .setUv(-1,  1);

        MeshData meshData = bufferBuilder.buildOrThrow();
        billboardVBO.upload(meshData);
        VertexBuffer.unbind();
    }
}
