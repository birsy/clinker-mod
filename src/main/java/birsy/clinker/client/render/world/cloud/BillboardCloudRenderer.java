package birsy.clinker.client.render.world.cloud;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;
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
    abstract void render(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack,
                         double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor);

    void postRender(OthershoreCloudRenderer renderer, ClientLevel level, int ticks, float partialTick, PoseStack poseStack,
                   double camX, double camY, double camZ, Matrix4f projectionMatrix, Vector3fc skyColor) {}

    AABB getRenderBounds(OthershoreCloudRenderer renderer, double camX, double camY, double camZ, float partialTick) { return AABB.INFINITE; }
}
