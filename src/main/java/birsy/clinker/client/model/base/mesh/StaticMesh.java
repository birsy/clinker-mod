package birsy.clinker.client.model.base.mesh;

import birsy.clinker.client.model.base.InterpolatedBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

// TODO: add support for arbitrary meshes;
public class StaticMesh extends ModelMesh {
    private final List<Cube> cubes;

    public StaticMesh() {
        super(true);
        cubes = new ArrayList<>();
    }

    public StaticMesh addCube(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset, float inflateX, float inflateY, float inflateZ, float uOffset, float vOffset) {
        Vertex downSouthWest = new Vertex(xOffset - inflateX, yOffset - inflateY, zOffset - inflateZ);
        Vertex downNorthWest = new Vertex(xOffset - inflateX, yOffset - inflateY, zOffset + zSize + inflateZ);
        Vertex downNorthEast = new Vertex(xOffset + zSize + inflateX, yOffset - inflateY, zOffset + zSize + inflateZ);
        Vertex downSouthEast = new Vertex(xOffset + zSize + inflateX, yOffset - inflateY, zOffset - inflateZ);
        Vertex upSouthWest   = new Vertex(xOffset - inflateX, yOffset + zSize + inflateY, zOffset - inflateZ);
        Vertex upNorthWest   = new Vertex(xOffset - inflateX, yOffset + zSize + inflateY, zOffset + zSize + inflateZ);
        Vertex upNorthEast   = new Vertex(xOffset + zSize + inflateX, yOffset + zSize + inflateY, zOffset + zSize + inflateZ);
        Vertex upSouthEast   = new Vertex(xOffset + zSize + inflateX, yOffset + zSize + inflateY, zOffset - inflateZ);

        Face eastFace = new Face(upSouthEast, upNorthEast, downNorthEast, downSouthEast,
                uOffset, vOffset + zSize, zSize, ySize, Vector3f.XP);
        Face westFace = new Face(downSouthWest, downNorthWest, upNorthWest, upSouthWest,
                uOffset + zSize + xSize, vOffset + zSize, zSize, ySize, Vector3f.XN);
        Face upFace = new Face(upSouthWest, upNorthWest, upNorthEast, upSouthEast,
                uOffset + zSize, vOffset, xSize, zSize, Vector3f.YP);
        Face downFace = new Face(downNorthWest, downSouthWest, downSouthEast, downNorthEast,
                uOffset + zSize + xSize, vOffset, xSize, zSize, Vector3f.YN);
        Face northFace = new Face(downNorthWest, downNorthEast, upNorthEast, upNorthWest,
                uOffset + zSize, vOffset + zSize, xSize, ySize, Vector3f.ZP);
        Face southFace = new Face(upSouthWest, upSouthEast, downSouthEast, downSouthWest,
                uOffset + 2 * zSize + xSize, vOffset + zSize, xSize, ySize, Vector3f.ZN);

        this.cubes.add(new Cube(new Face[]{northFace, eastFace, southFace, westFace, upFace, downFace}));

        return this;
    }

    @Override
    public void render(InterpolatedBone part, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        for (Cube cube : cubes) {
            for (Face face : cube.faces) {
                Vertex a = face.a;
                pVertexConsumer.vertex(a.x, a.y, a.z, pRed, pGreen, pBlue, pAlpha, face.u2, face.v1, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
                Vertex b = face.b;
                pVertexConsumer.vertex(b.x, b.y, b.z, pRed, pGreen, pBlue, pAlpha, face.u2, face.v2, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
                Vertex c = face.c;
                pVertexConsumer.vertex(c.x, c.y, c.z, pRed, pGreen, pBlue, pAlpha, face.u1, face.v2, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
                Vertex d = face.d;
                pVertexConsumer.vertex(d.x, d.y, d.z, pRed, pGreen, pBlue, pAlpha, face.u1, face.v1, pPackedOverlay, pPackedLight, face.normal.x(), face.normal.y(), face.normal.z());
            }
        }
    }
    
    private record Vertex(float x, float y, float z) {}
    private record Face(float u1, float v1, float u2, float v2, Vertex a, Vertex b, Vertex c, Vertex d, Vector3f normal) {
        private Face(Vertex a, Vertex b, Vertex c, Vertex d, float uOffset, float vOffset, float sizeU, float sizeV, Vector3f normal) {
            this(uOffset, vOffset, uOffset + sizeU, vOffset + sizeV, a, b, c, d, normal);
        }
    }
    private record Cube(Face[] faces) {}
}
