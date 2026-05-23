package birsy.clinker.client.render.world.cloud;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.core.util.CubicBezierSpline;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.ByteBuffer;
import java.util.*;

public class StormFrontCloudRenderer extends BillboardCloudRenderer {
    @Override
    void rebuild(int renderRadiusInBlocks) {

    }
    private CloudPosition[] createInstanceData(int radius) {
        final float height = UpperLayerCloudRenderer.CLOUD_HEIGHT - OthershoreGenerationConstants.SEA_HEIGHT;
        final float depth = 60;
        List<CloudPosition> positions = new ArrayList<>();
        CubicBezierSpline spline = new CubicBezierSpline(
                0.0F, 0.0F, 0.0F,
                0.0F, height, 0.0F,
                0.0F, height, depth * -0.5F,
                0.0F, height, depth * -1.0F
        );

        final float cloudDistance = 5.0F;

        Vector3f tangent = new Vector3f(), normal = new Vector3f(), biNormal = new Vector3f();
        spline.forEachEvenlySpaced(
                cloudDistance, 256,
                (startPos, t) -> {
                    if (!spline.frenet(t, tangent, normal, biNormal)) return;
                    for (float length = -radius; length < radius; length += cloudDistance) {
                        positions.add(new CloudPosition(
                                startPos.x() + length, startPos.y(), startPos.z(),
                                normal.x(), normal.y(), normal.z()
                        ));
                    }
                }
        );
        positions.sort(Comparator.comparingDouble(pos -> Mth.lengthSquared(pos.x, pos.z)));
        // output to flat array
        return positions.toArray(new CloudPosition[0]);
    }
    private record CloudPosition(float x, float y, float z, float normalX, float normalY, float normalZ) {
        private void upload(ByteBuffer buffer) {
            buffer.putFloat(x); buffer.putFloat(y); buffer.putFloat(z);
            buffer.putFloat(normalX); buffer.putFloat(normalY); buffer.putFloat(normalZ);
        }
    }
    
    @Override
    void renderSolid(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {

    }

    @Override
    void renderTranslucent(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {

    }
}
