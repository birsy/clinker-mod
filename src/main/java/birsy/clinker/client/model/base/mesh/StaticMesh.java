package birsy.clinker.client.model.base.mesh;

import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.Quaternionf;
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

    public Face[] addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float xInflate, float yInflate, float zInflate, float uOffset, float vOffset) {
        return this.addCube(xSize, ySize, zSize, xOffset, yOffset, zOffset, xInflate, yInflate, zInflate, uOffset, vOffset, false);
    }

    public Face[] addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float xInflate, float yInflate, float zInflate, float uOffset, float vOffset, boolean mirrored) {
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

        Face[] cubeFaces = {new Face(xoz, ooz, ooo, xoo, u3, v0, u2, v1, textureWidth, textureHeight, mirrored, Direction.UP),
                new Face(xyo, oyo, oyz, xyz, u2, v1, u1, v0, textureWidth, textureHeight, mirrored, Direction.DOWN),
                new Face(ooo, ooz, oyz, oyo, u4, v2, u2, v1, textureWidth, textureHeight, mirrored, Direction.EAST),
                new Face(xoz, xoo, xyo, xyz, u1, v2, u0, v1, textureWidth, textureHeight, mirrored, Direction.WEST),
                new Face(ooz, xoz, xyz, oyz, u5, v2, u4, v1, textureWidth, textureHeight, mirrored, Direction.NORTH),
                new Face(xoo, ooo, oyo, xyo, u2, v2, u1, v1, textureWidth, textureHeight, mirrored, Direction.SOUTH)};

        Collections.addAll(faces, cubeFaces);

        return cubeFaces;
    }

    public Face addTri(float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, float normalX, float normalY, float normalZ) {
        Face face = new Face(new Vertex[]{new Vertex(x1, y1, z1), new Vertex(x2, y2, z2), new Vertex(x2, y2, z2)}, new UV[]{new UV(u1, v1), new UV(u2, v2), new UV(u3, v3)}, new Vector3f(normalX, normalY, normalZ));
        faces.add(face);
        return face;
    }

    public Face addFace(float normalX, float normalY, float normalZ, FaceVertex... vertices) {
        Vertex[] vertArray = new Vertex[vertices.length];
        UV[] uvArray = new UV[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            FaceVertex vertex = vertices[i];
            vertArray[i] = new Vertex(vertex.x(), vertex.y(), vertex.z());
            uvArray[i] = new UV(vertex.u() / (float)this.textureWidth, vertex.v() / (float)this.textureHeight);
        }
        Face face = new Face(vertArray, uvArray, new Vector3f(normalX, normalY, normalZ));
        faces.add(face);
        return face;
    }

//    public static void rotate(Face[] faces, Quaternionf quaternion) {
//        for (Face face : faces) {
//            for (int i = 0; i < face.vertices.length; i++) {
//                Vertex initialVertex = face.vertices[i];
//                Vector3f newVertex = quaternion.transform(initialVertex.x(), initialVertex.y(), initialVertex.z());
//                face.vertices[i] = new Vertex(newVertex.x(), newVertex.y(), newVertex.z());
//            }
//            quaternion.transform(face.normal);
//        }
//    }
//
//    public static void rotate(float originX, float originY, float originZ, Face[] faces, Quaternionf quaternion) {
//        for (Face face : faces) {
//            for (int i = 0; i < face.vertices.length; i++) {
//                Vertex initialVertex = face.vertices[i];
//                Vector3f newVertex = quaternion.transform(initialVertex.x() + originX, initialVertex.y() + originY, initialVertex.z() + originZ);
//                face.vertices[i] = new Vertex(newVertex.x() - originX, newVertex.y() - originY, newVertex.z() - originZ);
//            }
//            quaternion.transform(face.normal);
//        }
//    }

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
                position.transform(matrix4f);
                normal.set(face.normal.x(), face.normal.y(), face.normal.z());
                normal.transform(matrix3f);
                pVertexConsumer.vertex(position.x(), position.y(), position.z(), pRed, pGreen, pBlue, pAlpha, uv.u(), uv.v(), pPackedOverlay, pPackedLight, normal.x(), normal.y(), normal.z());
            }
        }
    }
}
