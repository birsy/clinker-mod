package birsy.clinker.client.render.utilities;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;

// assumes triangle buffer mode + pos / uv / rgb vertex format
public final class MeshHelper {
    public static void consumeCircle(VertexConsumer vertexConsumer, Matrix4f matrix,
                                     int resolutionScale, float radius, float r, float g, float b, float a) {
        if (resolutionScale < 0) return;
        // initial center triangle
        float initialAngleIncrement = Mth.TWO_PI / 3.0F;
        for (int k = 0; k < 3; k++) {
            float angle = k * initialAngleIncrement;
            float x = Mth.sin(angle), z = Mth.cos(angle);
            vertexConsumer.addVertex(matrix, x * radius, 0, z * radius)
                    .setUv(x * 0.5F + 0.5F, z * 0.5F + 0.5F)
                    .setColor(r, g, b, a);
        }
        // rim triangles
        for (int i = 0; i < resolutionScale; i++) {
            int triCount = 3 * (1 << i);
            float angleIncrement = Mth.PI / triCount;
            for (int j = 0; j < triCount; j++) {
                int startingIndex = j * 2;
                for (int k = 0; k < 3; k++) {
                    float angle = (startingIndex + k) * angleIncrement;
                    float x = Mth.sin(angle), z = Mth.cos(angle);
                    vertexConsumer.addVertex(matrix, x * radius, 0, z * radius)
                            .setUv(x * 0.5F + 0.5F, z * 0.5F + 0.5F)
                            .setColor(r, g, b, a);
                }
            }
        }
    }

    public static void consumeCone(VertexConsumer vertexConsumer, Matrix4f matrix,
                             int resolution, float radius, boolean flipped,
                             float centerY, float centerR, float centerG, float centerB, float centerA,
                             float ringY, float ringR, float ringG, float ringB, float ringA) {
        float sign = flipped ? -1F : 1F;
        for (int i = 0; i < resolution; i++) {
            float angle0 = ((i + 0F) / resolution) * sign * Mth.TWO_PI;
            float x0 = Mth.sin(angle0), z0 = Mth.cos(angle0);
            float angle1 = ((i + 1F) / resolution) * sign * Mth.TWO_PI;
            float x1 = Mth.sin(angle1), z1 = Mth.cos(angle1);
            vertexConsumer.addVertex(matrix,0, centerY, 0).setUv(0.5F, 0.5F).setColor(centerR, centerG, centerB, centerA);
            vertexConsumer.addVertex(matrix, x0 * radius, ringY, z0 * radius).setUv( x0 * 0.5F + 0.5F, z0 * 0.5F + 0.5F).setColor(ringR, ringG, ringB, ringA);
            vertexConsumer.addVertex(matrix, x1 * radius, ringY, z1 * radius).setUv( x1 * 0.5F + 0.5F, z1 * 0.5F + 0.5F).setColor(ringR, ringG, ringB, ringA);
        }
    }

    public static void consumeCylinder(VertexConsumer vertexConsumer, Matrix4f matrix, int resolution,
                                 float radius0, float y0, float r0, float g0, float b0, float a0,
                                 float radius1, float y1, float r1, float g1, float b1, float a1) {
        for (int i = 0; i < resolution; i++) {
            float factor0 = (i + 0F) / resolution;
            float angle0 = factor0 * Mth.TWO_PI;
            float x0 = Mth.sin(angle0), z0 = Mth.cos(angle0);
            float factor1 = (i + 1F) / resolution;
            float angle1 = factor1 * Mth.TWO_PI;
            float x1 = Mth.sin(angle1), z1 = Mth.cos(angle1);

            vertexConsumer.addVertex(matrix, x0 * radius0, y0, z0 * radius0).setUv(factor0, 0).setColor(r0, g0, b0, a0);
            vertexConsumer.addVertex(matrix, x1 * radius0, y0, z1 * radius0).setUv(factor1, 0).setColor(r0, g0, b0, a0);
            vertexConsumer.addVertex(matrix, x1 * radius1, y1, z1 * radius1).setUv(factor1, 1).setColor(r1, g1, b1, a1);

            vertexConsumer.addVertex(matrix, x1 * radius1, y1, z1 * radius1).setUv(factor1, 1).setColor(r1, g1, b1, a1);
            vertexConsumer.addVertex(matrix, x0 * radius1, y1, z0 * radius1).setUv(factor0, 1).setColor(r1, g1, b1, a1);
            vertexConsumer.addVertex(matrix, x0 * radius0, y0, z0 * radius0).setUv(factor0, 0).setColor(r0, g0, b0, a0);
        }
    }

    public static void consumeSphereSegment(VertexConsumer vertexConsumer, Matrix4f matrix, float radius, boolean flipped, boolean mirrorBallUvs,
                                      int latitudinalResolution, int longitudinalResolution,
                                      float latitude0, float r0, float g0, float b0, float a0,
                                      float latitude1, float r1, float g1, float b1, float a1) {
        boolean southPole = latitude0 <= -Mth.HALF_PI, northPole = latitude1 >= Mth.HALF_PI;
        latitude0 = Math.max(latitude0, -Mth.HALF_PI); latitude1 = Math.min(latitude1, Mth.HALF_PI);
        int startLatIndex = 0 + (southPole ? 1 : 0), endLatIndex = latitudinalResolution - (northPole ? 1 : 0);
        float sign = flipped ? -1F : 1F;
        for (int i = 0; i < longitudinalResolution; i++) {
            float lonFactor0 = (i + 0F) / longitudinalResolution;
            float lonAngle0 = lonFactor0 * sign * Mth.TWO_PI;
            float x0 = Mth.sin(lonAngle0), z0 = Mth.cos(lonAngle0);

            float lonFactor1 = (i + 1F) / longitudinalResolution;
            float lonAngle1 = lonFactor1 * sign * Mth.TWO_PI;
            float x1 = Mth.sin(lonAngle1), z1 = Mth.cos(lonAngle1);

            // south cap
            if (southPole) {
                float rimLatFactor = startLatIndex / (float) latitudinalResolution;
                float rimLatAngle = Mth.lerp(rimLatFactor, latitude0, latitude1);
                float ringFrac = Mth.cos(rimLatAngle);
                float rimY = Mth.sin(rimLatAngle) * radius;
                float rimR = Mth.lerp(rimLatFactor, r0, r1),
                        rimG = Mth.lerp(rimLatFactor, g0, g1),
                        rimB = Mth.lerp(rimLatFactor, b0, b1),
                        rimA = Mth.lerp(rimLatFactor, a0, a1);

                vertexConsumer.addVertex(matrix, x1 * ringFrac * radius, rimY, z1 * ringFrac * radius)
                        .setUv(mirrorBallUvs ? x1 * ringFrac * 0.5F + 0.5F : lonFactor1,
                                mirrorBallUvs ? z1 * ringFrac * 0.5F + 0.5F : rimLatFactor)
                        .setColor(rimR, rimG, rimB, rimA);
                vertexConsumer.addVertex(matrix, x0 * ringFrac * radius, rimY, z0 * ringFrac * radius)
                        .setUv(mirrorBallUvs ? x0 * ringFrac * 0.5F + 0.5F : lonFactor0,
                                mirrorBallUvs ? z0 * ringFrac * 0.5F + 0.5F : rimLatFactor)
                        .setColor(rimR, rimG, rimB, rimA);
                vertexConsumer.addVertex(matrix, 0, -radius, 0)
                        .setUv(mirrorBallUvs ? 0.5F : (lonFactor0 + lonFactor1) * 0.5F, mirrorBallUvs ? 0.5F : 0F)
                        .setColor(r0, g0, b0, a0);
            }
            // fillers
            for (int j = startLatIndex; j < endLatIndex; j++) {
                float latFactor0 = (j + 0F) / latitudinalResolution;
                float latAngle0 = Mth.lerp(latFactor0, latitude0, latitude1);
                float y0 = Mth.sin(latAngle0) * radius;
                float radius0 = Mth.cos(latAngle0);
                float red0 = Mth.lerp(latFactor0, r0, r1),
                        green0 = Mth.lerp(latFactor0, g0, g1),
                        blue0 = Mth.lerp(latFactor0, b0, b1),
                        alpha0 = Mth.lerp(latFactor0, a0, a1);

                float latFactor1 = (j + 1F) / latitudinalResolution;
                float latAngle1 = Mth.lerp(latFactor1, latitude0, latitude1);
                float y1 = Mth.sin(latAngle1) * radius;
                float radius1 = Mth.cos(latAngle1);
                float red1 = Mth.lerp(latFactor1, r0, r1),
                        green1 = Mth.lerp(latFactor1, g0, g1),
                        blue1 = Mth.lerp(latFactor1, b0, b1),
                        alpha1 = Mth.lerp(latFactor1, a0, a1);

                if (mirrorBallUvs) {
                    vertexConsumer.addVertex(matrix, x0 * radius0 * radius, y0, z0 * radius0 * radius)
                            .setUv(x0 * radius0 * 0.5F + 0.5F, z0 * radius0 * 0.5F + 0.5F).setColor(red0, green0, blue0, alpha0);
                    vertexConsumer.addVertex(matrix, x1 * radius0 * radius, y0, z1 * radius0 * radius)
                            .setUv(x1 * radius0 * 0.5F + 0.5F, z1 * radius0 * 0.5F + 0.5F).setColor(red0, green0, blue0, alpha0);
                    vertexConsumer.addVertex(matrix, x1 * radius1 * radius, y1, z1 * radius1 * radius)
                            .setUv(x1 * radius1 * 0.5F + 0.5F, z1 * radius1 * 0.5F + 0.5F).setColor(red1, green1, blue1, alpha1);

                    vertexConsumer.addVertex(matrix, x1 * radius1 * radius, y1, z1 * radius1 * radius)
                            .setUv(x1 * radius1 * 0.5F + 0.5F, z1 * radius1 * 0.5F + 0.5F).setColor(red1, green1, blue1, alpha1);
                    vertexConsumer.addVertex(matrix, x0 * radius1 * radius, y1, z0 * radius1 * radius)
                            .setUv(x0 * radius1 * 0.5F + 0.5F, z0 * radius1 * 0.5F + 0.5F).setColor(red1, green1, blue1, alpha1);
                    vertexConsumer.addVertex(matrix, x0 * radius0 * radius, y0, z0 * radius0 * radius)
                            .setUv(x0 * radius0 * 0.5F + 0.5F, z0 * radius0 * 0.5F + 0.5F).setColor(red0, green0, blue0, alpha0);
                } else {
                    vertexConsumer.addVertex(matrix, x0 * radius0 * radius, y0, z0 * radius0 * radius)
                            .setUv(lonFactor0, latFactor0).setColor(red0, green0, blue0, alpha0);
                    vertexConsumer.addVertex(matrix, x1 * radius0 * radius, y0, z1 * radius0 * radius)
                            .setUv(lonFactor1, latFactor0).setColor(red0, green0, blue0, alpha0);
                    vertexConsumer.addVertex(matrix, x1 * radius1 * radius, y1, z1 * radius1 * radius)
                            .setUv(lonFactor1, latFactor1).setColor(red1, green1, blue1, alpha1);

                    vertexConsumer.addVertex(matrix, x1 * radius1 * radius, y1, z1 * radius1 * radius)
                            .setUv(lonFactor1, latFactor1).setColor(red1, green1, blue1, alpha1);
                    vertexConsumer.addVertex(matrix, x0 * radius1 * radius, y1, z0 * radius1 * radius)
                            .setUv(lonFactor0, latFactor1).setColor(red1, green1, blue1, alpha1);
                    vertexConsumer.addVertex(matrix, x0 * radius0 * radius, y0, z0 * radius0 * radius)
                            .setUv(lonFactor0, latFactor0).setColor(red0, green0, blue0, alpha0);
                }
            }
            // north cap
            if (northPole) {
                float rimLatFactor = endLatIndex / (float) latitudinalResolution;
                float rimLatAngle = Mth.lerp(rimLatFactor, latitude0, latitude1);
                float ringFrac = Mth.cos(rimLatAngle);
                float rimY = Mth.sin(rimLatAngle) * radius;
                float rimR = Mth.lerp(rimLatFactor, r0, r1),
                        rimG = Mth.lerp(rimLatFactor, g0, g1),
                        rimB = Mth.lerp(rimLatFactor, b0, b1),
                        rimA = Mth.lerp(rimLatFactor, a0, a1);

                vertexConsumer.addVertex(matrix, x0 * ringFrac * radius, rimY, z0 * ringFrac * radius)
                        .setUv(mirrorBallUvs ? x0 * ringFrac * 0.5F + 0.5F : lonFactor0,
                                mirrorBallUvs ? z0 * ringFrac * 0.5F + 0.5F : rimLatFactor)
                        .setColor(rimR, rimG, rimB, rimA);
                vertexConsumer.addVertex(matrix, x1 * ringFrac * radius, rimY, z1 * ringFrac * radius)
                        .setUv(mirrorBallUvs ? x1 * ringFrac * 0.5F + 0.5F : lonFactor1,
                                mirrorBallUvs ? z1 * ringFrac * 0.5F + 0.5F : rimLatFactor)
                        .setColor(rimR, rimG, rimB, rimA);
                vertexConsumer.addVertex(matrix, 0, radius, 0)
                        .setUv(mirrorBallUvs ? 0.5F : (lonFactor0 + lonFactor1) * 0.5F, mirrorBallUvs ? 0.5F : 1F)
                        .setColor(r1, g1, b1, a1);
            }
        }
    }
}
