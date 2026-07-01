package birsy.clinker.client.render.world.sky;

import birsy.clinker.client.render.utilities.MeshHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

public class OthershoreSkyRenderer2 {
    private VertexBuffer outerSkyVbo, outerCloudsVbo, outerStarsVbo;

    void initialize(int renderDistanceInBlocks) {
        Tesselator tesselator = Tesselator.getInstance();
        RandomSource random = RandomSource.create(0);
        Matrix4f identity = new Matrix4f().identity();

        // outer sky buffer
        {
            outerSkyVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerSkyVbo.bind();
            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            MeshHelper.consumeSphereSegment(vertexConsumer, identity, 1.0F, false, true,
                    16, 16,
                    0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                    Mth.HALF_PI, 1.0F, 1.0F, 1.0F, 1.0F);
            outerStarsVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }

        // outer clouds sheet
        {
            outerCloudsVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerCloudsVbo.bind();
            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            MeshHelper.consumeSphereSegment(vertexConsumer, new Matrix4f().scale(1, 0.5F, 1), 1.0F, false, true,
                    8, 16,
                    0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                    Mth.HALF_PI, 1.0F, 1.0F, 1.0F, 1.0F);
            outerCloudsVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }

        // outer stars buffer
        {
            outerStarsVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            outerStarsVbo.bind();

            int starCount = 1000;
            Quaternionf quaternion = new Quaternionf();
            Matrix4f matrix = new Matrix4f();

            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            for (int i = 0; i < starCount; i++) {
                float dirX = (float) random.nextGaussian(), dirY = (float) random.nextGaussian(), dirZ = (float) random.nextGaussian();
                float length = (float) Mth.length(dirX, dirY, dirZ);
                dirX /= length; dirY /= length; dirZ /= length;

                // random rotation
                quaternion.rotationTo(0, 0, 1, dirX, dirY, dirZ);
                quaternion.rotateAxis(random.nextFloat() * Mth.TWO_PI, dirX, dirY, dirZ);
                matrix.rotation(quaternion);

                float radius = Mth.lerp(random.nextFloat(), 0.01F, 0.1F);

                boolean fancyStar = random.nextInt(5) == 0;
                float temperature = random.nextFloat();

                float r = fancyStar ? 1.0F : 0.0F, g = temperature + 0.0F, b = 0.0F, a = 0.0F;
                vertexConsumer.addVertex(matrix,-1 * radius, -1 * radius, 1).setUv(0, 0).setColor(r, g, b, a);
                vertexConsumer.addVertex(matrix, 1 * radius, -1 * radius, 1).setUv(1, 0).setColor(r, g, b, a);
                vertexConsumer.addVertex(matrix, 1 * radius,  1 * radius, 1).setUv(1, 1).setColor(r, g, b, a);
                vertexConsumer.addVertex(matrix,-1 * radius,  1 * radius, 1).setUv(0, 1).setColor(r, g, b, a);
            }

            outerStarsVbo.upload(vertexConsumer.buildOrThrow());
            VertexBuffer.unbind();
        }
    }

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, Vector3fc skyColor) {

    }
}
