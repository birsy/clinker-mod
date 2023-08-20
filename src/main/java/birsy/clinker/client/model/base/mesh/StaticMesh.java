package birsy.clinker.client.model.base.mesh;

import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: add support for arbitrary meshes;
public class StaticMesh extends ModelMesh {
    private final List<Face> faces;
    final int textureWidth, textureHeight;

    public StaticMesh(int textureWidth, int textureHeight) {
        super(true);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        faces = new ArrayList<>();
    }

    public StaticMesh addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float xInflate, float yInflate, float zInflate, float uOffset, float vOffset) {
        return this.addCube(xSize, ySize, zSize, xOffset, yOffset, zOffset, xInflate, yInflate, zInflate, uOffset, vOffset, false);
    }

    public StaticMesh addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float xInflate, float yInflate, float zInflate, float uOffset, float vOffset, boolean mirrored) {
        float minX = xOffset;
        float minY = yOffset;
        float minZ = zOffset;
        float maxX = xOffset + xSize;
        float maxY = yOffset + ySize;
        float maxZ = zOffset + zSize;
        minX -= xInflate;
        minY -= yInflate;
        minZ -= zInflate;
        maxX += xInflate;
        maxY += yInflate;
        maxZ += zInflate;
        if (mirrored) {
            float swap = maxX;
            maxX = minX;
            minX = swap;
        }

        Vertex ooo = new Vertex(minX, minY, minZ);
        Vertex xoo = new Vertex(maxX, minY, minZ);
        Vertex oyo = new Vertex(minX, maxY, minZ);
        Vertex ooz = new Vertex(minX, minY, maxZ);
        Vertex xyo = new Vertex(maxX, maxY, minZ);
        Vertex oyz = new Vertex(minX, maxY, maxZ);
        Vertex xoz = new Vertex(maxX, minY, maxZ);
        Vertex xyz = new Vertex(maxX, maxY, maxZ);

        float u0 = uOffset;
        float u1 = uOffset + zSize;
        float u2 = uOffset + zSize + xSize;
        float u3 = uOffset + zSize + xSize + xSize;
        float u4 = uOffset + zSize + xSize + zSize;
        float u5 = uOffset + zSize + xSize + zSize + xSize;
        float v0 = vOffset;
        float v1 = vOffset + zSize;
        float v2 = vOffset + zSize + ySize;

        Collections.addAll(faces,
        new Face(xoz, ooz, ooo, xoo, u3, v0, u2, v1, textureWidth, textureHeight, mirrored, Direction.UP),
        new Face(xyo, oyo, oyz, xyz, u2, v1, u1, v0, textureWidth, textureHeight, mirrored, Direction.DOWN),
        new Face(ooo, ooz, oyz, oyo, u4, v2, u2, v1, textureWidth, textureHeight, mirrored, Direction.EAST),
        new Face(xoz, xoo, xyo, xyz, u1, v2, u0, v1, textureWidth, textureHeight, mirrored, Direction.WEST),
        new Face(ooz, xoz, xyz, oyz, u5, v2, u4, v1, textureWidth, textureHeight, mirrored, Direction.NORTH),
        new Face(xoo, ooo, oyo, xyo, u2, v2, u1, v1, textureWidth, textureHeight, mirrored, Direction.SOUTH));

        return this;
    }

    @Override
    public void render(InterpolatedBone part, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();
        Vector4f position = new Vector4f();
        Vector3f normal = new Vector3f();

        for (Face face : this.faces) {
            for (int i = 0; i < face.vertices.length; i++) {
                Vertex vertex = face.vertices[i];
                UV uv = face.uvs[i];
                position.set(vertex.x(), vertex.y(), vertex.z(), 1.0F);
                position.transform(matrix4f);
                normal.set(face.normal.x(), face.normal.y(), face.normal.z());
                normal.transform(matrix3f);
                pVertexConsumer.vertex(position.x(), position.y(), position.z(), pRed, pGreen, pBlue, pAlpha, uv.u(), uv.v(), pPackedOverlay, pPackedLight, normal.x(), normal.y(), normal.z());
            }
        }
    }
}
