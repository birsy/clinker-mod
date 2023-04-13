package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class DebugRenderUtil {
    /*public static void renderCircle(PoseStack pPoseStack, VertexConsumer pBuffer, float radius, double x, double y, double z, float r, float g, float b, float a) {
        Minecraft mc = Minecraft.getInstance();
        Camera pRenderInfo = mc.gameRenderer.getMainCamera();

        Quaternion rotation = pRenderInfo.rotation();
        pPoseStack.pushPose();
        pPoseStack.translate(x, y, z);
        pPoseStack.mulPose(rotation);

        Matrix4f pose = pPoseStack.last().pose();
        Vector3f[] verticies = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = verticies[i];
            vertex.mul(radius);
            Vector4f v4 = new Vector4f(vertex.x(), vertex.y(), vertex.z(), 1.0F);
            v4.transform(pose);
            vertex.set(v4.x(), v4.y(), v4.z());
        }

        Vector3f normal = new Vector3f(0, 0, -1);
        pPoseStack.popPose();

        pBuffer.vertex(verticies[0].x(), verticies[0].y(), verticies[0].z(), r, g, b, a, 1, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, normal.x(), normal.y(), normal.z());
        pBuffer.vertex(verticies[1].x(), verticies[1].y(), verticies[1].z(), r, g, b, a, 1, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, normal.x(), normal.y(), normal.z());
        pBuffer.vertex(verticies[2].x(), verticies[2].y(), verticies[2].z(), r, g, b, a, 0, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, normal.x(), normal.y(), normal.z());
        pBuffer.vertex(verticies[3].x(), verticies[3].y(), verticies[3].z(), r, g, b, a, 0, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, normal.x(), normal.y(), normal.z());
    }*/

    public static void renderCircle(PoseStack pPoseStack, VertexConsumer pConsumer, int resolution, float radius, double x, double y, double z, float pRed, float pGreen, float pBlue, float pAlpha) {
        Minecraft mc = Minecraft.getInstance();
        Camera pRenderInfo = mc.gameRenderer.getMainCamera();

        Quaternion rotation = pRenderInfo.rotation();
        pPoseStack.pushPose();
        pPoseStack.translate(x, y, z);

        for (int i = 0; i < 3; i++) {
            pPoseStack.pushPose();
            switch (i) {
                case 1: pPoseStack.mulPose(Vector3f.YP.rotationDegrees(90)); break;
                case 2: pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90)); break;
            }
            Matrix4f matrix4f = pPoseStack.last().pose();
            Matrix3f matrix3f = pPoseStack.last().normal();
            for (int segment = 0; segment < resolution; segment++) {
                float angle1 = (segment / (float)resolution) * Mth.TWO_PI;
                float angle2 = ((segment + 1) / (float)resolution) * Mth.TWO_PI;
                float s1 = Mth.sin(angle1) * radius;
                float c1 = Mth.cos(angle1) * radius;
                float s2 = Mth.sin(angle2) * radius;
                float c2 = Mth.cos(angle2) * radius;

                Vector3f normal = new Vector3f(s1, 0, c1);
                normal.sub(new Vector3f(s2, 0, c2));
                normal.normalize();

                renderLine(matrix4f, matrix3f, pConsumer, s1, c1, 0, s2, c2, 0, pRed, pGreen, pBlue, pAlpha);
            }
            pPoseStack.popPose();
        }


        pPoseStack.popPose();
    }

    public static void renderLine(PoseStack pPoseStack, VertexConsumer pConsumer, double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ, float pRed, float pGreen, float pBlue, float pAlpha) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();
        float minX = (float)pMinX;
        float minY = (float)pMinY;
        float minZ = (float)pMinZ;
        float maxX = (float)pMaxX;
        float maxY = (float)pMaxY;
        float maxZ = (float)pMaxZ;
        Vector3f normal = new Vector3f(minX, minY, minZ);
        normal.sub(new Vector3f(maxX, maxY, maxZ));
        normal.normalize();

        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, normal.x(), normal.y(), normal.z()).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, normal.x(), normal.y(), normal.z()).endVertex();
    }

    public static void renderLine(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer pConsumer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float pRed, float pGreen, float pBlue, float pAlpha) {
        Vector3f normal = new Vector3f(minX, minY, minZ);
        normal.sub(new Vector3f(maxX, maxY, maxZ));
        normal.normalize();

        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, normal.x(), normal.y(), normal.z()).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, normal.x(), normal.y(), normal.z()).endVertex();
    }
}
