package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class Mesh {
    public final List<Face> faces;
    public final List<Vertex> vertices;

    public Mesh() {
        this.faces = new ArrayList<>();
        this.vertices = new ArrayList<>();
    }

    public void render(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        Matrix4f matrix4f = pPose.pose();
        Matrix3f matrix3f = pPose.normal();
        for (Face face : this.faces) {
            face.draw(matrix4f, matrix3f, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }

    public void addFace(Face face) {
        this.faces.add(face);
        for (Vertex vertex : face.vertices) {
            this.vertices.add(vertex);
        }
    }

    public void addCube(int pTexCoordU, int pTexCoordV, float pMinX, float pMinY, float pMinZ, float pDimensionX, float pDimensionY, float pDimensionZ, float pGrowX, float pGrowY, float pGrowZ, boolean pMirror, float pTexWidthScaled, float pTexHeightScaled) {
        float f = pMinX + pDimensionX;
        float f1 = pMinY + pDimensionY;
        float f2 = pMinZ + pDimensionZ;
        pMinX -= pGrowX;
        pMinY -= pGrowY;
        pMinZ -= pGrowZ;
        f += pGrowX;
        f1 += pGrowY;
        f2 += pGrowZ;

        if (pMirror) {
            float f3 = f;
            f = pMinX;
            pMinX = f3;
        }

        Vertex vertex0 = new Vertex(pMinX, pMinY, pMinZ, 0.0F, 0.0F);
        Vertex vertex1 = new Vertex(f, pMinY, pMinZ, 0.0F, 8.0F);
        Vertex vertex2 = new Vertex(f, f1, pMinZ, 8.0F, 8.0F);
        Vertex vertex3 = new Vertex(pMinX, f1, pMinZ, 8.0F, 0.0F);
        Vertex vertex4 = new Vertex(pMinX, pMinY, f2, 0.0F, 0.0F);
        Vertex vertex5 = new Vertex(f, pMinY, f2, 0.0F, 8.0F);
        Vertex vertex6 = new Vertex(f, f1, f2, 8.0F, 8.0F);
        Vertex vertex7 = new Vertex(pMinX, f1, f2, 8.0F, 0.0F);

        float f4 = pTexCoordU;
        float f5 = pTexCoordU + pDimensionZ;
        float f6 = pTexCoordU + pDimensionZ + pDimensionX;
        float f7 = pTexCoordU + pDimensionZ + pDimensionX + pDimensionX;
        float f8 = pTexCoordU + pDimensionZ + pDimensionX + pDimensionZ;
        float f9 = pTexCoordU + pDimensionZ + pDimensionX + pDimensionZ + pDimensionX;
        float f10 = pTexCoordV;
        float f11 = pTexCoordV + pDimensionZ;
        float f12 = pTexCoordV + pDimensionZ + pDimensionY;
        this.addFace(new Face(new Vertex[]{vertex5, vertex4, vertex0, vertex1}, f5, f10, f6, f11, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.DOWN));
        this.addFace(new Face(new Vertex[]{vertex2, vertex3, vertex7, vertex6}, f6, f11, f7, f10, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.UP));
        this.addFace(new Face(new Vertex[]{vertex0, vertex4, vertex7, vertex3}, f4, f11, f5, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.WEST));
        this.addFace(new Face(new Vertex[]{vertex1, vertex0, vertex3, vertex2}, f5, f11, f6, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.NORTH));
        this.addFace(new Face(new Vertex[]{vertex5, vertex1, vertex2, vertex6}, f6, f11, f8, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.EAST));
        this.addFace(new Face(new Vertex[]{vertex4, vertex5, vertex6, vertex7}, f8, f11, f9, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.SOUTH));
    }


    @OnlyIn(Dist.CLIENT)
    static class Face {
        public final Vertex[] vertices;
        public final Vector3f normal;

        public Face(Vertex[] vertices, float u1, float v1, float u2, float v2, float textureWidth, float textureHeight, boolean mirrored, Direction normal) {
            this(vertices, u1, v1, u2, v2, textureWidth, textureHeight, mirrored, normal.step().x(), normal.step().y(), normal.step().z());
        }

        public Face(Vertex[] vertices, float u1, float v1, float u2, float v2, float textureWidth, float textureHeight, boolean mirrored, float nX, float nY, float nZ) {
            if (vertices.length > 4) Clinker.LOGGER.warn("! Invalid number of verticies in face");
            this.vertices = vertices;

            vertices[0] = vertices[0].remap(u2 / textureWidth, v1 / textureHeight);
            vertices[1] = vertices[1].remap(u1 / textureWidth, v1 / textureHeight);
            vertices[2] = vertices[2].remap(u1 / textureWidth, v2 / textureHeight);
            vertices[3] = vertices[3].remap(u2 / textureWidth, v2 / textureHeight);

            if (mirrored) {
                int i = vertices.length;

                for(int j = 0; j < i / 2; ++j) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }

            this.normal = new Vector3f(nX, nY, nZ);
            if (mirrored) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }
        }

        public void draw(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            Matrix4f matrix4f = pPose.pose();
            Matrix3f matrix3f = pPose.normal();
            this.draw(matrix4f, matrix3f, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        public void draw(Matrix4f transform, Matrix3f normalTransform, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            Vector3f vector3f = this.normal.copy();
            vector3f.transform(normalTransform);
            float nX = vector3f.x();
            float nY = vector3f.y();
            float nZ = vector3f.z();

            for(Vertex vertex : this.vertices) {
                float x = vertex.pos.x() / 16.0F;
                float y = vertex.pos.y() / 16.0F;
                float z = vertex.pos.z() / 16.0F;
                Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
                vector4f.transform(transform);
                pVertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), pRed, pGreen, pBlue, pAlpha, vertex.u, vertex.v, pPackedOverlay, pPackedLight, nX, nY, nZ);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float pX, float pY, float pZ, float pU, float pV) {
            this(new Vector3f(pX, pY, pZ), pU, pV);
        }

        public Vertex remap(float pU, float pV) {
            return new Vertex(this.pos, pU, pV);
        }

        public Vertex(Vector3f pPos, float pU, float pV) {
            this.pos = pPos;
            this.u = pU;
            this.v = pV;
        }
    }
}
