package birsy.clinker.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtils {
    public static void drawFaceBetweenPoints(VertexConsumer consumer, PoseStack stack, float width, Vector3f pos1, Vector3f tangent1, Vector3f normal1, Vector3f biTangent1, int packedLight1, int overlay1, float u1, float v1,
                                             Vector3f pos2, Vector3f tangent2, Vector3f normal2, Vector3f biTangent2, int packedLight2, int overlay2, float u2, float v2) {
        PoseStack.Pose pose = stack.last();
        consumer.addVertex(pose, pos1.x() + biTangent1.x() * width, pos1.y + biTangent1.y() * width, pos1.z() + biTangent1.z() * width)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u1, v1)
                .setOverlay(overlay1).setLight(packedLight1)
                .setNormal(pose, normal1.x(), normal1.y(), normal1.z());
        consumer.addVertex(pose, pos2.x() + biTangent2.x() * width, pos2.y + biTangent2.y() * width, pos2.z() + biTangent2.z() * width)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u1, v2)
                .setOverlay(overlay2).setLight(packedLight2)
                .setNormal(pose, normal2.x(), normal2.y(), normal2.z());
        consumer.addVertex(pose, pos2.x() - biTangent2.x() * width, pos2.y - biTangent2.y() * width, pos2.z() - biTangent2.z() * width)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u2, v2)
                .setOverlay(overlay2).setLight(packedLight2)
                .setNormal(pose, normal2.x(), normal2.y(), normal2.z());
        consumer.addVertex(pose, pos1.x() - biTangent1.x() * width, pos1.y - biTangent1.y() * width, pos1.z() - biTangent1.z() * width)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u2, v1)
                .setOverlay(overlay1).setLight(packedLight1)
                .setNormal(pose, normal1.x(), normal1.y(), normal1.z());
    }


    public static void drawFaceBetweenPoints(VertexConsumer consumer, PoseStack stack, float width, Vec3 pos1, Vector3f tangent1, Vector3f normal1, Vector3f biTangent1, int packedLight1, int overlay1, float u1, float v1,
                                                                                                    Vec3 pos2, Vector3f tangent2, Vector3f normal2, Vector3f biTangent2, int packedLight2, int overlay2, float u2, float v2) {
        PoseStack.Pose pose = stack.last();
        consumer.addVertex(pose, (float) (pos1.x() + biTangent1.x() * width), (float) (pos1.y + biTangent1.y() * width), (float) (pos1.z() + biTangent1.z() * width))
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u1, v1)
                .setOverlay(overlay1).setLight(packedLight1)
                .setNormal(pose, normal1.x(), normal1.y(), normal1.z());
        consumer.addVertex(pose, (float) (pos2.x() + biTangent2.x() * width), (float) (pos2.y + biTangent2.y() * width), (float) (pos2.z() + biTangent2.z() * width))
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u1, v2)
                .setOverlay(overlay2).setLight(packedLight2)
                .setNormal(pose, normal2.x(), normal2.y(), normal2.z());
        consumer.addVertex(pose, (float) (pos2.x() - biTangent2.x() * width), (float) (pos2.y - biTangent2.y() * width), (float) (pos2.z() - biTangent2.z() * width))
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u2, v2)
                .setOverlay(overlay2).setLight(packedLight2)
                .setNormal(pose, normal2.x(), normal2.y(), normal2.z());
        consumer.addVertex(pose, (float) (pos1.x() - biTangent1.x() * width), (float) (pos1.y - biTangent1.y() * width), (float) (pos1.z() - biTangent1.z() * width))
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u2, v1)
                .setOverlay(overlay1).setLight(packedLight1)
                .setNormal(pose, normal1.x(), normal1.y(), normal1.z());
    }

    public static void drawFaceBetweenPoints(VertexConsumer consumer, PoseStack stack, float width, Vec3 pos1, Vector3f tangent1, Vector3f normal1, Vector3f biTangent1, float u1, float v1,
                                             Vec3 pos2, Vector3f tangent2, Vector3f normal2, Vector3f biTangent2, float u2, float v2,
                                             float r, float g, float b, float a) {
        drawFaceBetweenPoints(consumer, stack, width, pos1, tangent1, normal1, biTangent1, u1, v1, r, g, b, a, pos2, tangent2, normal2, biTangent2, u2, v2, r, g, b, a);
    }

    public static void drawFaceBetweenPoints(VertexConsumer consumer, PoseStack stack, float width, Vec3 pos1, Vector3f tangent1, Vector3f normal1, Vector3f biTangent1, float u1, float v1, float r1, float g1, float b1, float a1,
                                             Vec3 pos2, Vector3f tangent2, Vector3f normal2, Vector3f biTangent2, float u2, float v2, float r2, float g2, float b2, float a2) {
        int overlay = OverlayTexture.NO_OVERLAY;
        int light = LightTexture.FULL_BRIGHT;
        PoseStack.Pose pose = stack.last();
        consumer.addVertex(pose, (float) (pos1.x() + biTangent1.x() * width), (float) (pos1.y + biTangent1.y() * width), (float) (pos1.z() + biTangent1.z() * width))
                .setColor(r1,g1,b1,a1)
                .setUv(u1, v1)
                .setOverlay(overlay).setLight(light)
                .setNormal(pose, normal1.x(), normal1.y(), normal1.z());
        consumer.addVertex(pose, (float) (pos2.x() + biTangent2.x() * width), (float) (pos2.y + biTangent2.y() * width), (float) (pos2.z() + biTangent2.z() * width))
                .setColor(r2,g2,b2,a2)
                .setUv(u1, v2)
                .setOverlay(overlay).setLight(light)
                .setNormal(pose, normal2.x(), normal2.y(), normal2.z());
        consumer.addVertex(pose, (float) (pos2.x() - biTangent2.x() * width), (float) (pos2.y - biTangent2.y() * width), (float) (pos2.z() - biTangent2.z() * width))
                .setColor(r2,g2,b2,a2)
                .setUv(u2, v2)
                .setOverlay(overlay).setLight(light)
                .setNormal(pose, normal2.x(), normal2.y(), normal2.z());
        consumer.addVertex(pose, (float) (pos1.x() - biTangent1.x() * width), (float) (pos1.y - biTangent1.y() * width), (float) (pos1.z() - biTangent1.z() * width))
                .setColor(r1,g1,b1,a1)
                .setUv(u2, v1)
                .setOverlay(overlay).setLight(light)
                .setNormal(pose, normal1.x(), normal1.y(), normal1.z());
    }


    public static void drawFaceBetweenPoints(VertexConsumer consumer, PoseStack stack, float width, Vec3 pos1, Vector3f tangent1, Vector3f normal1, int packedLight1, int overlay1, float u1, float v1,
                                                                                                    Vec3 pos2, Vector3f tangent2, Vector3f normal2, int packedLight2, int overlay2, float u2, float v2) {
        drawFaceBetweenPoints(consumer, stack, width, pos1, tangent1, normal1, normal1.cross(tangent1, new Vector3f()), packedLight1, overlay1, u1, v1,
                                                      pos2, tangent2, normal2, normal2.cross(tangent2, new Vector3f()), packedLight2, overlay2, u2, v2);
    }

    public static void drawFaceBetweenPoints(VertexConsumer consumer, PoseStack stack, float width, Vector3f normal, Vec3 pos1, int packedLight1, int overlay1, float u1, float v1,
                                                                                                                     Vec3 pos2, int packedLight2, int overlay2, float u2, float v2) {
        Vector3f tangent = pos1.subtract(pos2).normalize().toVector3f();
        drawFaceBetweenPoints(consumer, stack, width, pos1, tangent, normal, normal.cross(tangent, new Vector3f()), packedLight1, overlay1, u1, v1,
                pos2, tangent, normal, normal.cross(tangent, new Vector3f()), packedLight2, overlay2, u2, v2);
    }
}
