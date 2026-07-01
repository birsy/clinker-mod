package birsy.clinker.client.render.world.cloud;

import birsy.clinker.client.ambience.AmbienceHandler;
import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.block.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11C;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChunkedUpperLayerCloudRenderer extends BillboardCloudRenderer {
    public static final int CLOUD_CELL_SIZE = 5, CLOUD_CHUNK_COUNT = 12, CLOUD_CHUNK_SIZE = CLOUD_CELL_SIZE * CLOUD_CHUNK_COUNT;
    public static final int LOWER_CLOUD_HEIGHT = 270, UPPER_CLOUD_HEIGHT = 300, CLOUD_HEIGHT = (LOWER_CLOUD_HEIGHT + UPPER_CLOUD_HEIGHT) / 2;

    static final float[] LOD_THRESHOLDS = new float[]{ 0.0F, 100.0F, 230.0F };
    final List<CloudChunk>[] cloudChunksByLOD = new List[LOD_THRESHOLDS.length];
    final VertexBuffer[] cloudChunkVboByLOD = new VertexBuffer[LOD_THRESHOLDS.length];
    List<CloudChunk> cloudChunks, chunksToRender;
    DynamicShaderBlock<List<CloudChunk>> cloudChunksBuffer;
    VertexBuffer backingGridVbo;
    AABB bounds;
    float renderRadius;

    @Override
    void initialize(int renderRadiusInBlocks) {
        // create the cloud chunk VBOs
        Tesselator tesselator = Tesselator.getInstance();
        for (int lodLevel = 0; lodLevel < cloudChunkVboByLOD.length; lodLevel++) {
            VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            buffer.bind();
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            int spacing = 1 << lodLevel, nextSpacing = 1 << (lodLevel + 1);
            for (int x = 0; x < CLOUD_CHUNK_COUNT; x++) {
                for (int z = 0; z < CLOUD_CHUNK_COUNT; z++) {
                    boolean includedInLOD = (z % spacing) == 0 && (x % spacing) == 0;
                    if (!includedInLOD) continue;

                    boolean includedInNextLOD = (z % nextSpacing) == 0 && (x % nextSpacing) == 0;
                    int bX = x * CLOUD_CELL_SIZE, bZ = z * CLOUD_CELL_SIZE;

                    float rgba = includedInNextLOD ? 1.0F : 0.0F;

                    bufferBuilder.addVertex(bX, 0, bZ).setUv(-1, -1).setColor(rgba, rgba, rgba, rgba);
                    bufferBuilder.addVertex(bX, 0, bZ).setUv( 1, -1).setColor(rgba, rgba, rgba, rgba);
                    bufferBuilder.addVertex(bX, 0, bZ).setUv( 1,  1).setColor(rgba, rgba, rgba, rgba);
                    bufferBuilder.addVertex(bX, 0, bZ).setUv(-1,  1).setColor(rgba, rgba, rgba, rgba);
                }
            }
            MeshData meshData = bufferBuilder.buildOrThrow();
            buffer.upload(meshData);
            VertexBuffer.unbind();

            cloudChunkVboByLOD[lodLevel] = buffer;
        }
        cloudChunksBuffer = ShaderBlock.dynamic(
                ShaderBlock.BufferBinding.SHADER_STORAGE, 1,
                (list, buf) -> { for (CloudChunk pos : list) pos.upload(buf); }
        );
        super.initialize(renderRadiusInBlocks);
    }

    @Override
    void rebuild(int renderRadiusInBlocks) {
        // create the LOD chunks
        cloudChunks = new ArrayList<>();
        chunksToRender = new ArrayList<>();
        for (int i = 0; i < cloudChunksByLOD.length; i++)
            cloudChunksByLOD[i] = new ArrayList<>();
        int cloudChunkRadius = (int) Math.ceil(renderRadiusInBlocks / (double) CLOUD_CHUNK_SIZE) + 2;
        for (int x = -cloudChunkRadius; x <= cloudChunkRadius; x++) {
            for (int z = -cloudChunkRadius; z <= cloudChunkRadius; z++) {
                double distance = Mth.length(x, z);
                if (distance <= cloudChunkRadius) {
                    CloudChunk chunk = new CloudChunk(x, z);
                    cloudChunks.add(chunk);
                    for (int i = 0; i < LOD_THRESHOLDS.length; i++) {
                        float lodThreshold = LOD_THRESHOLDS[i] / CLOUD_CHUNK_SIZE;
                        float nextLodThreshold = i < LOD_THRESHOLDS.length - 1 ? (LOD_THRESHOLDS[i + 1] / CLOUD_CHUNK_SIZE) : Float.POSITIVE_INFINITY;
                        if (distance >= lodThreshold && distance <= nextLodThreshold + 1) cloudChunksByLOD[i].add(chunk);
                    }
                }
            }
        }
        // sort them by distance
        for (int i = 0; i < cloudChunksByLOD.length; i++)
            cloudChunksByLOD[i].sort(Comparator.comparingInt(chunk -> chunk.x * chunk.x + chunk.z * chunk.z));

        createBackingGrid(renderRadiusInBlocks);

        this.bounds = new AABB(
                -renderRadiusInBlocks, LOWER_CLOUD_HEIGHT, -renderRadiusInBlocks,
                renderRadiusInBlocks, UPPER_CLOUD_HEIGHT, renderRadiusInBlocks
        ).inflate(10);

        this.renderRadius = renderRadiusInBlocks;
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
        // don't draw the fancy clouds if we're underground
        // still draw the backing, though, in case you get a view of the sky somewhere
        float globalFade = AmbienceHandler.SURFACE_TRACKER.getAboveGroundFactor(partialTick);
        if (globalFade < 0.01) return;

        boolean anyVisible = updateCloudChunkVisibility(camX, camY, camZ, Minecraft.getInstance().levelRenderer.getFrustum(), cloudChunks);
        if (!anyVisible) return;

        int cameraChunkX = cloudChunkPos(camX), cameraChunkZ = cloudChunkPos(camZ);
        double cameraChunkOffsetX = cloudChunkOffset(camX), cameraChunkOffsetZ = cloudChunkOffset(camZ);

        poseStack.pushPose();
        poseStack.translate(-cameraChunkOffsetX, -camY, -cameraChunkOffsetZ);
        Matrix4f pose = poseStack.last().pose();
        poseStack.popPose();

        ShaderProgram cloudShader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_BILLBOARD_LAYER);
        cloudShader.bind();
        cloudShader.bindSamplers(0);

        // set uniforms
        cloudShader.setDefaultUniforms(VertexFormat.Mode.QUADS, pose, projectionMatrix);
        cloudShader.getUniformSafe("CameraChunk").setVectorI(cameraChunkX, cameraChunkZ);
        cloudShader.getUniformSafe("CameraChunkOffset").setVector((float) cameraChunkOffsetX, (float) cameraChunkOffsetZ);
        cloudShader.getUniformSafe("ChunkSize").setInt(CLOUD_CHUNK_COUNT);
        cloudShader.getUniformSafe("CellSize").setInt(CLOUD_CELL_SIZE);
        cloudShader.getUniformSafe("CloudHeight").setFloat(CLOUD_HEIGHT);
        cloudShader.getUniformSafe("SkyColor").setVector(skyColor.x(), skyColor.y(), skyColor.z(), 1.0F);
        cloudShader.getUniformSafe("RenderRadius").setFloat(renderRadius);

        CloudHoleTracker tracker = CloudHoleTracker.getInstance();
        if (tracker != null) tracker.bind();

        float lowerLayerAlpha = (float) Mth.clampedMap(camY, UPPER_CLOUD_HEIGHT + CLOUD_CELL_SIZE * 2, UPPER_CLOUD_HEIGHT + CLOUD_CELL_SIZE * 4, 1, 0);
        float upperLayerAlpha = (float) Mth.clampedMap(camY, LOWER_CLOUD_HEIGHT - CLOUD_CELL_SIZE * 2, LOWER_CLOUD_HEIGHT - CLOUD_CELL_SIZE * 4, 1, 0);
        for (int i = 0; i < LOD_THRESHOLDS.length; i++) renderLODLevel(i, cloudShader, lowerLayerAlpha * globalFade, upperLayerAlpha * globalFade, camY < CLOUD_HEIGHT);

        ShaderProgram.unbind();
    }

    private void renderLODLevel(int lodLevel, ShaderProgram shader, float lowerLayerAlpha, float upperLayerAlpha, boolean lowerIsCloser) {
        List<CloudChunk> cloudChunksForLOD = cloudChunksByLOD[lodLevel];
        VertexBuffer vbo = cloudChunkVboByLOD[lodLevel];
        float lodThreshold = LOD_THRESHOLDS[lodLevel];
        float nextLodThreshold = lodLevel == LOD_THRESHOLDS.length - 1 ? 1000.0F : LOD_THRESHOLDS[lodLevel + 1]; // should really be infinity, but gpus are weird with infinities

        // collect visible chunks, for this LOD level
        chunksToRender.clear();
        for (CloudChunk chunk : cloudChunksForLOD)
            if (chunk.visible) chunksToRender.add(chunk);
        if (chunksToRender.isEmpty()) return;

        // fill buffer contents & bind uniforms
        int chunkCount = chunksToRender.size();
        int size = chunkCount * CloudChunk.SIZE;
        if (cloudChunksBuffer.getSize() != size) cloudChunksBuffer.setSize(size);
        cloudChunksBuffer.set(chunksToRender);
        shader.getUniformSafe("LODLevel").setInt(lodLevel);
        shader.getUniformSafe("LODThreshold").setVector(lodThreshold, nextLodThreshold);
        VeilRenderSystem.bind("CloudChunkPositions", cloudChunksBuffer);

        // draw
        vbo.bind();
        if (lowerIsCloser) {
            if (lowerLayerAlpha > 0.001) renderLayer(vbo, shader, chunkCount, lowerLayerAlpha, true);
            if (upperLayerAlpha > 0.001) renderLayer(vbo, shader, chunkCount, upperLayerAlpha, false);
        } else {
            if (upperLayerAlpha > 0.001) renderLayer(vbo, shader, chunkCount, upperLayerAlpha, false);
            if (lowerLayerAlpha > 0.001) renderLayer(vbo, shader, chunkCount, lowerLayerAlpha, true);
        }
        VertexBuffer.unbind();
    }

    void renderLayer(VertexBuffer vbo, ShaderProgram shader, int chunkCount, float alpha, boolean lowerLayer) {
        shader.getUniformSafe("AlphaMultiplier").setFloat(alpha);
        shader.getUniformSafe("DisplacementDirection").setVector(0, lowerLayer ? -1 : 1, 0);
        VeilRenderSystem.drawInstanced(vbo, chunkCount);
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
        poseStack.popPose();

        // draw the backing
        RenderSystem.disableCull();
        RenderSystem.stencilFunc(GL11C.GL_NOTEQUAL, 1, 0xFF);

        ShaderProgram backingGridShader = VeilRenderSystem.setShader(ClinkerShaders.CLOUD_LAYER_BACKING);
        backingGridShader.bindSamplers(0);
        backingGridShader.setDefaultUniforms(VertexFormat.Mode.QUADS, pose, projectionMatrix);
        backingGridShader.getUniformSafe("PlayerCloudCell").setVectorI(playerCloudX, playerCloudZ);
        backingGridShader.getUniformSafe("PlayerCloudCellOffset").setVector((float) camXOffset, (float) camZOffset);
        backingGridShader.getUniformSafe("CloudCellSize").setInt(CLOUD_CELL_SIZE);
        backingGridShader.getUniformSafe("SkyColor").setVector(skyColor.x(), skyColor.y(), skyColor.z(), 1.0F);
        backingGridShader.getUniformSafe("DisplacementDirection").setVector(0, camY < CLOUD_HEIGHT ? -1 : 1, 0);
        backingGridShader.getUniformSafe("CloudHeight").setFloat(CLOUD_HEIGHT);
        backingGridShader.getUniformSafe("AlphaMultiplier").setFloat(fade);
        backingGridShader.getUniformSafe("RenderRadius").setFloat(renderRadius);

        backingGridVbo.bind();
        backingGridVbo.drawWithShader(pose, projectionMatrix, VeilRenderBridge.toShaderInstance(backingGridShader));
        VertexBuffer.unbind();

        RenderSystem.enableCull();
        RenderSystem.stencilFunc(GL11C.GL_ALWAYS, 0, 0xFF);
    }

    @Override
    void free() {
        super.free();
        for (VertexBuffer vertexBuffer : cloudChunkVboByLOD) vertexBuffer.close();
        backingGridVbo.close();
        cloudChunksBuffer.close();
    }

    private static int cloudChunkPos(double coordinate) { return Math.floorDiv(Mth.floor(coordinate), CLOUD_CHUNK_SIZE); }
    private static double cloudChunkOffset(double coordinate) { return coordinate - cloudChunkPos(coordinate) * CLOUD_CHUNK_SIZE; }
    private static boolean updateCloudChunkVisibility(double camX, double camY, double camZ, Frustum frustum, List<CloudChunk> chunks) {
        double cameraChunkOffsetX = cloudChunkOffset(camX), cameraChunkOffsetZ = cloudChunkOffset(camZ);
        double offsetX = camX - cameraChunkOffsetX, offsetZ = camZ - cameraChunkOffsetZ;
        boolean anyVisible = false;
        for (CloudChunk chunk : chunks) {
            AABB aabb = chunk.aabb;
            boolean visible = frustum.cubeInFrustum(
                    aabb.minX + offsetX, aabb.minY, aabb.minZ + offsetZ,
                    aabb.maxX + offsetX, aabb.maxY, aabb.maxZ + offsetZ
            );
            chunk.visible = visible;
            if (visible) anyVisible = true;
        }
        return anyVisible;
    }

    static final class CloudChunk {
        static final int SIZE = Integer.BYTES * 2;
        final int x, z;
        final AABB aabb;
        boolean visible = false;

        CloudChunk(int x, int z) {
            this.x = x; this.z = z;
            this.aabb = new AABB(
                    (x + 0) * CLOUD_CHUNK_SIZE, CLOUD_HEIGHT - 30, (z + 0) * CLOUD_CHUNK_SIZE,
                    (x + 1) * CLOUD_CHUNK_SIZE, CLOUD_HEIGHT + 30, (z + 1) * CLOUD_CHUNK_SIZE
            ).inflate(CLOUD_CELL_SIZE);
        }
        void upload(ByteBuffer buffer) {
            buffer.putInt(x); buffer.putInt(z);
        }
    }
}
