package birsy.clinker.client.render.world.cloud;

import birsy.clinker.client.ambience.AmbienceHandler;
import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.client.render.world.OthershoreStormRenderHelper;
import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.types.StormApproachingWeather;
import birsy.clinker.core.util.CubicBezierSpline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.block.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.ByteBuffer;
import java.util.*;

public class StormFrontCloudRenderer extends BillboardCloudRenderer {
    public static final int CLOUD_CELL_SIZE = 5;

    final List<DistanceAtHeight> distancesAtHeight = new ArrayList<>();
    DynamicShaderBlock<CloudPosition[]> instancePositions;
    int instanceCount = 0;
    AABB bounds;

    @Override
    void rebuild(int renderRadiusInBlocks) {
        CloudPosition[] data = createInstanceData(renderRadiusInBlocks);
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

        bounds = new AABB(
                -renderRadiusInBlocks, distancesAtHeight.getFirst().height, distancesAtHeight.getLast().distance,
                renderRadiusInBlocks, distancesAtHeight.getLast().height, distancesAtHeight.getFirst().distance
        ).inflate(20.0);
    }
    private CloudPosition[] createInstanceData(int radius) {
        final float height = (UpperLayerCloudRenderer.LOWER_CLOUD_HEIGHT - 20) - OthershoreGenerationConstants.SEA_HEIGHT;
        final float depth = -120;
        List<CloudPosition> positions = new ArrayList<>();
        CubicBezierSpline spline = new CubicBezierSpline(
                0.0F, 0.0F, 0.0F,
                0.0F, height * 1.0F, 0.0F,
                0.0F, height * 0.8F, depth,
                0.0F, height, depth
        );

        final float cloudDistance = CLOUD_CELL_SIZE;

        Vector3f lastPos = new Vector3f(0, -1, 0), normal = new Vector3f(), lastNormal = new Vector3f(0, 0, -1);
        distancesAtHeight.clear();
        spline.forEachEvenlySpaced(
                cloudDistance, 256,
                (startPos, t) -> {
                    Vector3f tangent = lastPos.sub(startPos, lastPos).normalize();
                    tangent.cross(1, 0, 0, normal).normalize();
                    if (lastNormal.dot(normal) < 0) normal.mul(-1);

                    for (float length = -radius; length < radius; length += cloudDistance) {
                        positions.add(new CloudPosition(
                                startPos.x() + length, startPos.y(), startPos.z(),
                                normal.x(), normal.y(), normal.z()
                        ));
                    }
                    lastPos.set(startPos);
                    lastNormal.set(normal);

                    if (distancesAtHeight.isEmpty() || startPos.y() > distancesAtHeight.getLast().height) {
                        distancesAtHeight.add(new DistanceAtHeight(startPos.z(), startPos.y()));
                    }
                }
        );

        positions.sort(Comparator.comparingDouble(pos -> Mth.lengthSquared(pos.x, pos.z - 30)));
        // output to flat array
        return positions.toArray(new CloudPosition[0]);
    }
    private record CloudPosition(float x, float y, float z, float normalX, float normalY, float normalZ) {
        static final int SIZE = Float.BYTES * 8;
        private void upload(ByteBuffer buffer) {
            buffer.putFloat(x); buffer.putFloat(y); buffer.putFloat(z);
            buffer.putFloat(0); // padding
            buffer.putFloat(normalX); buffer.putFloat(normalY); buffer.putFloat(normalZ);
            buffer.putFloat(0); // padding
        }
    }
    private record DistanceAtHeight(float distance, float height) {}

    @Override
    void free() {
        instancePositions.free();
    }

    @Override
    AABB getRenderBounds(OthershoreCloudRenderer renderer, double camX, double camY, double camZ, float partialTick) {
        OthershoreWeatherSystem weatherSystem = ClientOthershoreWeatherSystem.get();
        if (weatherSystem == null) return super.getRenderBounds(renderer, camX, camY, camZ, partialTick);

        float progress = OthershoreStormRenderHelper.getNormalizedStormApproachDistance(weatherSystem, partialTick);
        float distanceToPlayer = progress * renderer.lastRenderRadius;
        float cloudDistanceOffset = getCloudDistanceOffset((float) camY - OthershoreGenerationConstants.SEA_HEIGHT);
        distanceToPlayer += cloudDistanceOffset - CLOUD_CELL_SIZE;
        return bounds.move(camX, 0, camZ - distanceToPlayer);
    }

    @Override
    void render(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {
        OthershoreWeatherSystem weatherSystem = ClientOthershoreWeatherSystem.get();
        if (weatherSystem == null) return;
        if (!(weatherSystem.getWeather() instanceof StormApproachingWeather)) return;

        float alphaAboveCloudHeight = (float) Mth.clampedMap(camY, UpperLayerCloudRenderer.LOWER_CLOUD_HEIGHT - 20, UpperLayerCloudRenderer.LOWER_CLOUD_HEIGHT, 1.0, 0.0);
        float alphaFromUndergroundness = AmbienceHandler.SURFACE_AMBIENCE_HANDLER.getAboveGroundFactor(1.0F);
        float alphaFromFade = OthershoreStormRenderHelper.getStormCloudAlpha(weatherSystem, partialTick);
        float fade = alphaFromFade * alphaAboveCloudHeight * alphaFromUndergroundness;
        if (fade < 0.05) return;

        int playerCloudX = Math.floorDiv(Mth.floor(camX), CLOUD_CELL_SIZE);
        double camXOffset = camX - (playerCloudX * CLOUD_CELL_SIZE);

        float progress = OthershoreStormRenderHelper.getNormalizedStormApproachDistance(weatherSystem, partialTick);
        float distanceToPlayer = progress * renderer.lastRenderRadius;
        float cloudDistanceOffset = getCloudDistanceOffset((float) camY - OthershoreGenerationConstants.SEA_HEIGHT);
        distanceToPlayer += cloudDistanceOffset - CLOUD_CELL_SIZE;

        poseStack.pushPose();
        poseStack.translate(-camXOffset, -camY + OthershoreGenerationConstants.SEA_HEIGHT, -distanceToPlayer);
        Matrix4f pose = poseStack.last().pose();

        ShaderProgram cloudShader = VeilRenderSystem.setShader(ClinkerShaders.INSTANCED_CLOUD_BILLBOARD_STORMFRONT);

        cloudShader.bind();
        cloudShader.bindSamplers(0);
        cloudShader.setDefaultUniforms(VertexFormat.Mode.QUADS, pose, projectionMatrix);

        cloudShader.getUniformSafe("PlayerCloudCell").setInt(playerCloudX);
        cloudShader.getUniformSafe("PlayerCloudCellOffset").setFloat((float) camXOffset);
        cloudShader.getUniformSafe("DistanceToCamera").setFloat(distanceToPlayer);
        cloudShader.getUniformSafe("Fade").setFloat(fade);

        cloudShader.getUniformSafe("CloudCellSize").setInt(CLOUD_CELL_SIZE);
        cloudShader.getUniformSafe("InstanceCount").setInt(instanceCount);
        cloudShader.getUniformSafe("SkyColor").setVector(skyColor.x(), skyColor.y(), skyColor.z(), 1.0F);

        VeilRenderSystem.bind("StormFrontInstancePositions", instancePositions);

        VertexBuffer vbo = renderer.getBillboardVbo();

        vbo.bind();
        VeilRenderSystem.drawInstanced(vbo, instanceCount);
        VertexBuffer.unbind();

        ShaderProgram.unbind();
        poseStack.popPose();
    }


    float getCloudDistanceOffset(float y) {
        if (y <= distancesAtHeight.getFirst().height) return distancesAtHeight.getFirst().distance;
        if (y >= distancesAtHeight.getLast().height) return distancesAtHeight.getLast().distance;

        for (int i = 1; i < distancesAtHeight.size(); i++) {
            DistanceAtHeight distanceAtHeight = distancesAtHeight.get(i);
            if (y <= distanceAtHeight.height) {
                DistanceAtHeight lastDistanceAtHeight = distancesAtHeight.get(i - 1);
                float interpolationFactor = (y - lastDistanceAtHeight.height) / (distanceAtHeight.height - lastDistanceAtHeight.height);
                return Mth.lerp(interpolationFactor, lastDistanceAtHeight.distance, distanceAtHeight.distance);
            }
        }
        return 0;
    }
}
