package birsy.clinker.client.model.base.mesh;

import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

// TODO: add support for arbitrary meshes;
public class StaticMesh extends ModelMesh {
    private final List<QuadFace> quads;
    private final List<TriFace> tris;

    public StaticMesh() {
        super(true);
        quads = new ArrayList<>();
        tris = new ArrayList<>();
    }

    public StaticMesh addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float inflateX, float inflateY, float inflateZ, float uOffset, float vOffset) {
        return this.addCube(xSize, ySize, zSize, xOffset, yOffset, zOffset, inflateX, inflateY, inflateZ, uOffset, vOffset, false);
    }

    public StaticMesh addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float inflateX, float inflateY, float inflateZ, float uOffset, float vOffset, boolean mirrored) {
        Vertex downSouthWest = new Vertex(xOffset - inflateX, yOffset - inflateY, zOffset - inflateZ);
        Vertex downNorthWest = new Vertex(xOffset - inflateX, yOffset - inflateY, zOffset + zSize + inflateZ);
        Vertex downNorthEast = new Vertex(xOffset + zSize + inflateX, yOffset - inflateY, zOffset + zSize + inflateZ);
        Vertex downSouthEast = new Vertex(xOffset + zSize + inflateX, yOffset - inflateY, zOffset - inflateZ);
        Vertex upSouthWest   = new Vertex(xOffset - inflateX, yOffset + zSize + inflateY, zOffset - inflateZ);
        Vertex upNorthWest   = new Vertex(xOffset - inflateX, yOffset + zSize + inflateY, zOffset + zSize + inflateZ);
        Vertex upNorthEast   = new Vertex(xOffset + zSize + inflateX, yOffset + zSize + inflateY, zOffset + zSize + inflateZ);
        Vertex upSouthEast   = new Vertex(xOffset + zSize + inflateX, yOffset + zSize + inflateY, zOffset - inflateZ);

        //special cases for planes so they don't render extra faces
        if (!mirrored) {
            boolean hasXFace = (zSize != 0 || inflateZ > 0) && (ySize != 0 || inflateY > 0);
            if (hasXFace) {
                QuadFace eastFace = new QuadFace(upSouthEast, upNorthEast, downNorthEast, downSouthEast,
                        uOffset, vOffset + zSize, zSize, ySize, Vector3f.XP);
                QuadFace westFace = new QuadFace(downSouthWest, downNorthWest, upNorthWest, upSouthWest,
                        uOffset + zSize + xSize, vOffset + zSize, zSize, ySize, Vector3f.XN);
                MathUtils.addAll(this.quads, eastFace, westFace);
            }

            boolean hasYFace = (zSize != 0 || inflateZ > 0) && (xSize != 0 || inflateX > 0);
            if (hasYFace) {
                QuadFace upFace = new QuadFace(upSouthWest, upNorthWest, upNorthEast, upSouthEast,
                        uOffset + zSize, vOffset, xSize, zSize, Vector3f.YP);
                QuadFace downFace = new QuadFace(downNorthWest, downSouthWest, downSouthEast, downNorthEast,
                        uOffset + zSize + xSize, vOffset, xSize, zSize, Vector3f.YN);
                MathUtils.addAll(this.quads, upFace, downFace);
            }

            boolean hasZFace = (xSize != 0 || inflateX > 0) && (ySize != 0 || inflateY > 0);
            if (hasZFace) {
                QuadFace northFace = new QuadFace(downNorthWest, downNorthEast, upNorthEast, upNorthWest,
                        uOffset + zSize, vOffset + zSize, xSize, ySize, Vector3f.ZP);
                QuadFace southFace = new QuadFace(upSouthWest, upSouthEast, downSouthEast, downSouthWest,
                        uOffset + 2 * zSize + xSize, vOffset + zSize, xSize, ySize, Vector3f.ZN);
                MathUtils.addAll(this.quads, northFace, southFace);
            }
        } else {
            boolean hasXFace = (zSize != 0 || inflateZ > 0) && (ySize != 0 || inflateY > 0);
            if (hasXFace) {
                QuadFace eastFace = new QuadFace(upSouthEast, upNorthEast, downNorthEast, downSouthEast,
                        uOffset + 2 * zSize + xSize, vOffset + zSize, -zSize, ySize, Vector3f.XP);
                QuadFace westFace = new QuadFace(downSouthWest, downNorthWest, upNorthWest, upSouthWest,
                        uOffset + zSize, vOffset + zSize, -zSize, ySize, Vector3f.XN);
                MathUtils.addAll(this.quads, eastFace, westFace);
            }

            boolean hasYFace = (zSize != 0 || inflateZ > 0) && (xSize != 0 || inflateX > 0);
            if (hasYFace) {
                QuadFace upFace = new QuadFace(upSouthWest, upNorthWest, upNorthEast, upSouthEast,
                        uOffset + zSize + xSize, vOffset, -xSize, zSize, Vector3f.YP);
                QuadFace downFace = new QuadFace(downNorthWest, downSouthWest, downSouthEast, downNorthEast,
                        uOffset + zSize + 2 * xSize, vOffset, -xSize, zSize, Vector3f.YN);
                MathUtils.addAll(this.quads, upFace, downFace);
            }

            boolean hasZFace = (xSize != 0 || inflateX > 0) && (ySize != 0 || inflateY > 0);
            if (hasZFace) {
                QuadFace northFace = new QuadFace(downNorthWest, downNorthEast, upNorthEast, upNorthWest,
                        uOffset + zSize + xSize, vOffset + zSize, -xSize, ySize, Vector3f.ZP);
                QuadFace southFace = new QuadFace(upSouthWest, upSouthEast, downSouthEast, downSouthWest,
                        uOffset + 2 * zSize + 2 * xSize, vOffset + zSize, -xSize, ySize, Vector3f.ZN);
                MathUtils.addAll(this.quads, northFace, southFace);
            }
        }

        return this;
    }

    @Override
    public void render(InterpolatedBone part, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        for (QuadFace face : this.quads) {
            Vertex a = face.a;
            pVertexConsumer.vertex(a.x, a.y, a.z, pRed, pGreen, pBlue, pAlpha, face.u2, face.v1, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
            Vertex b = face.b;
            pVertexConsumer.vertex(b.x, b.y, b.z, pRed, pGreen, pBlue, pAlpha, face.u2, face.v2, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
            Vertex c = face.c;
            pVertexConsumer.vertex(c.x, c.y, c.z, pRed, pGreen, pBlue, pAlpha, face.u1, face.v2, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
            Vertex d = face.d;
            pVertexConsumer.vertex(d.x, d.y, d.z, pRed, pGreen, pBlue, pAlpha, face.u1, face.v1, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
        }

        for (TriFace face : this.tris) {
            Vertex a = face.a;
            pVertexConsumer.vertex(a.x, a.y, a.z, pRed, pGreen, pBlue, pAlpha, face.aU, face.aV, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
            Vertex b = face.b;
            pVertexConsumer.vertex(b.x, b.y, b.z, pRed, pGreen, pBlue, pAlpha, face.bU, face.bV, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
            Vertex c = face.c;
            pVertexConsumer.vertex(c.x, c.y, c.z, pRed, pGreen, pBlue, pAlpha, face.cU, face.cV, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
        }
    }
    
    private record Vertex(float x, float y, float z) {}
    private record QuadFace(float u1, float v1, float u2, float v2, Vertex a, Vertex b, Vertex c, Vertex d, Vector3f normal) {
        private QuadFace(Vertex a, Vertex b, Vertex c, Vertex d, float uOffset, float vOffset, float sizeU, float sizeV, Vector3f normal) {
            this(uOffset, vOffset, uOffset + sizeU, vOffset + sizeV, a, b, c, d, normal);
        }
    }
    private record TriFace(Vertex a, Vertex b, Vertex c, float aU, float aV, float bU, float bV, float cU, float cV, Vector3f normal) {}
    private record Cube(QuadFace[] faces) {}
}
