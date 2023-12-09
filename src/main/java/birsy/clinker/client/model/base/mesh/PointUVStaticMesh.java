package birsy.clinker.client.model.base.mesh;

import birsy.clinker.client.model.base.InterpolatedBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: add support for arbitrary meshes;
public class PointUVStaticMesh extends ModelMesh {
    private final List<Face> faces;
    final int textureWidth, textureHeight;

    public PointUVStaticMesh(int textureWidth, int textureHeight) {
        super(true);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        faces = new ArrayList<>();
    }

    public Face[] addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float xInflate, float yInflate, float zInflate, float u, float v) {
        return this.addCube(xSize, ySize, zSize, xOffset, yOffset, zOffset, xInflate, yInflate, zInflate, u, v, false);
    }

    public Face[] addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float xInflate, float yInflate, float zInflate, float u, float v, boolean mirrored) {
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

        Face[] cubeFaces = {new Face(xoz, ooz, ooo, xoo, u, v, u, v, textureWidth, textureHeight, mirrored, Direction.UP),
                new Face(xyo, oyo, oyz, xyz, u, v, u, v, textureWidth, textureHeight, mirrored, Direction.DOWN),
                new Face(ooo, ooz, oyz, oyo, u, v, u, v, textureWidth, textureHeight, mirrored, Direction.EAST),
                new Face(xoz, xoo, xyo, xyz, u, v, u, v, textureWidth, textureHeight, mirrored, Direction.WEST),
                new Face(ooz, xoz, xyz, oyz, u, v, u, v, textureWidth, textureHeight, mirrored, Direction.NORTH),
                new Face(xoo, ooo, oyo, xyo, u, v, u, v, textureWidth, textureHeight, mirrored, Direction.SOUTH)};

        Collections.addAll(faces, cubeFaces);

        return cubeFaces;
    }

    public record FaceVertex(float x, float y, float z, float u, float v) {}

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
                matrix4f.transform(position);
                normal.set(face.normal.x(), face.normal.y(), face.normal.z());
                matrix3f.transform(normal);
                pVertexConsumer.vertex(position.x(), position.y(), position.z(), pRed, pGreen, pBlue, pAlpha, uv.u(), uv.v(), pPackedOverlay, pPackedLight, normal.x(), normal.y(), normal.z());
            }
        }
    }
}
