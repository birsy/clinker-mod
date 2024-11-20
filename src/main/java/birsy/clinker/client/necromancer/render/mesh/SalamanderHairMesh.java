package birsy.clinker.client.necromancer.render.mesh;

import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.model.entity.SalamanderSkeletonFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

// TODO: add support for arbitrary meshes;
public class SalamanderHairMesh extends Mesh {
    private static int IDS = 0;

    private Face initialFace;
    private DynamicFace renderFace;
    final int textureWidth, textureHeight;
    private final int id;
    public float wiggleSpeed, wiggleAmount;
    private boolean mirrored;

    public SalamanderHairMesh(int textureWidth, int textureHeight) {
        super(false);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.id = IDS++;
    }

    public DynamicFace createFace(float xSize, float ySize, float xOffset, float yOffset, float zOffset, float xInflate, float yInflate, float uOffset, float vOffset, boolean mirrored) {
        float minX = xOffset;
        float minY = yOffset;
        float z = zOffset;
        float maxX = xOffset + xSize;
        float maxY = yOffset + ySize;
        minX -= xInflate;
        minY -= yInflate;
        maxX += xInflate;
        maxY += yInflate;
        if (mirrored) {
            float swap = maxX;
            maxX = minX;
            minX = swap;
        }

        Vertex ooo = new Vertex(minX, minY, z);
        Vertex xoo = new Vertex(maxX, minY, z);
        Vertex oyo = new Vertex(minX, maxY, z);
        Vertex xyo = new Vertex(maxX, maxY, z);

        Vector3f oooD = new Vector3f(minX, minY, z);
        Vector3f xooD = new Vector3f(maxX, minY, z);
        Vector3f oyoD = new Vector3f(minX, maxY, z);
        Vector3f xyoD = new Vector3f(maxX, maxY, z);

        float u1 = uOffset;
        float u2 = uOffset + xSize;
        float v1 = vOffset;
        float v2 = vOffset + ySize;

        this.initialFace = new Face(xoo, ooo, oyo, xyo, u2, v2, u1, v1, textureWidth, textureHeight, mirrored, Direction.SOUTH);
        this.renderFace = new DynamicFace(xooD, oooD, oyoD, xyoD, u2, v2, u1, v1, textureWidth, textureHeight, mirrored, Direction.SOUTH);
        this.mirrored = mirrored;
        return renderFace;
    }

    @Override
    public void update(@Nullable Bone part, Skeleton model, float time) {
        super.update(part, model, time);
        if (mirrored) {
            wiggleVertex(0, time);
            wiggleVertex(1, time);
        } else {
            wiggleVertex(2, time);
            wiggleVertex(3, time);
        }
    }

    private void wiggleVertex(int index, float time) {
        if (this.initialFace != null && this.renderFace != null) {
            Vertex baseVertex = initialFace.vertices[index];
            float t = time + this.id * 200;
            float wiggleA = Mth.sin(t * wiggleSpeed) * wiggleAmount;
            float wiggleB = Mth.sin(t * wiggleSpeed * 0.9F) * wiggleAmount;
            float wiggleC = Mth.sin(t * wiggleSpeed * 0.95F) * wiggleAmount;
            renderFace.vertices[index].set(baseVertex.x() + wiggleA, baseVertex.y() + wiggleB, baseVertex.z() + wiggleC);
        }
    }

    @Override
    public void render(Bone part, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        Matrix4f matrix4f;
        Matrix3f matrix3f = pPoseStack.last().normal();
        Vector4f position = new Vector4f();
        Vector3f normal = new Vector3f();
        Vector3f correctNormals = new Vector3f();
        float brightness = 1.0F;

        if (part instanceof SalamanderSkeletonFactory.SalamanderFurBone bone) {
            pPoseStack.pushPose();
            pPoseStack.mulPose(bone.rot);
            matrix4f = new Matrix4f(pPoseStack.last().pose());
            Matrix3f correctNormalMatrix = pPoseStack.last().normal();
            correctNormals.set(renderFace.normal.x(), renderFace.normal.y(), renderFace.normal.z());
            correctNormalMatrix.transform(correctNormals);
            pPoseStack.popPose();

            brightness = bone.brightness;
        } else {
            matrix4f = new Matrix4f(pPoseStack.last().pose());
        }

        for (int i = 0; i < renderFace.vertices.length; i++) {
            Vector3f vertex = renderFace.vertices[i];
            UV uv = renderFace.uvs[i];
            position.set(vertex.x(), vertex.y(), vertex.z(), 1.0F);
            matrix4f.transform(position);

            if (part instanceof SalamanderSkeletonFactory.SalamanderFurBone bone) {
                normal.set(bone.normal.x(), bone.normal.y(), bone.normal.z());
            } else {
                normal.set(renderFace.normal.x(), renderFace.normal.y(), renderFace.normal.z());
            }
            matrix3f.transform(normal);
            if (part instanceof SalamanderSkeletonFactory.SalamanderFurBone bone) {
                //normal.lerp(correctNormals, 0.1F);
                //normal.normalize();
            }
            pVertexConsumer.vertex(position.x(), position.y(), position.z(), pRed * brightness, pGreen * brightness, pBlue * brightness, pAlpha, uv.u(), uv.v(), pPackedOverlay, pPackedLight, normal.x(), normal.y(), normal.z());
        }
    }
}
