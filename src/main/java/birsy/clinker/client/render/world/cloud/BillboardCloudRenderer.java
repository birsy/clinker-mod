package birsy.clinker.client.render.world.cloud;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

public abstract class BillboardCloudRenderer {
    void initialize(int renderRadiusInBlocks) {
        this.rebuild(renderRadiusInBlocks);
    }
    // called whenever render distance changes...
    void rebuild(int renderRadiusInBlocks) {}
    void free() {}

    void preRender(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack,
                   double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {}

    // called first with depth testing and depth writing etc enabled. should be rendered front-to-back
    abstract void renderSolid(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack,
                              double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor);
    // called last without depth writing. should be rendered back-to-front.
    abstract void renderTranslucent(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack,
                                    double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor);
}
