package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.world.block.blockentity.SarcophagusBlockEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SarcophagusInnardsRenderer<T extends SarcophagusBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation TEXTURE = Clinker.resource("textures/block/sarcophagus/sarcophagus_innards.png");
    private final BlockEntityRendererProvider.Context context;
    public static final Vector3f white = new Vector3f(1, 1, 1);
    public static final Vec2[] faceUVs = new Vec2[]{new Vec2(1, 1), new Vec2(1, 0), new Vec2(0, 0), new Vec2(0, 1)};
    private final Vertex[][] fullCube;

    public SarcophagusInnardsRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;

        Vertex upNorthEast = new Vertex(new Vector4f(1, 1, 1, 1), white);
        Vertex upNorthWest = new Vertex(new Vector4f(0, 1, 1, 1), white);
        Vertex upSouthWest = new Vertex(new Vector4f(0, 1, 0, 1), white);
        Vertex upSouthEast = new Vertex(new Vector4f(1, 1, 0, 1), white);
        Vertex downNorthEast = new Vertex(new Vector4f(1, 0, 1, 1), white);
        Vertex downNorthWest = new Vertex(new Vector4f(0, 0, 1, 1), white);
        Vertex downSouthWest = new Vertex(new Vector4f(0, 0, 0, 1), white);
        Vertex downSouthEast = new Vertex(new Vector4f(1, 0, 0, 1), white);

        Vertex[] eastFace = new Vertex[]{upSouthEast, upNorthEast, downNorthEast, downSouthEast};
        Vertex[] westFace = new Vertex[]{downSouthWest, downNorthWest, upNorthWest, upSouthWest};
        Vertex[] upFace = new Vertex[]{upSouthWest, upNorthWest, upNorthEast, upSouthEast};
        Vertex[] downFace = new Vertex[]{downNorthWest, downSouthWest, downSouthEast, downNorthEast};
        Vertex[] northFace = new Vertex[]{downNorthWest, downNorthEast, upNorthEast, upNorthWest};
        Vertex[] southFace = new Vertex[]{upSouthWest, upSouthEast, downSouthEast, downSouthWest};

        fullCube = new Vertex[][]{downFace, upFace, northFace, southFace, eastFace, westFace};
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        Matrix4f matrix = pPoseStack.last().pose();
        VertexConsumer consumer = pBufferSource.getBuffer(RenderType.entitySolid(Clinker.resource("textures/block/sarcophagus/sarcophagus_innards.png")));

        float gutLevel = pBlockEntity.getNeighboringLevels(Direction.values().length, pPartialTick) / 3.0F;
        float[] surroundingGutLevels = new float[Direction.values().length];
        for (int i = 0; i < Direction.values().length; i++) {
            surroundingGutLevels[i] = pBlockEntity.getNeighboringLevels(i, pPartialTick) / 3.0F;
        }
        float min = gutLevel / 2;
        float max = 1 - min;

        Vertex upNorthEast = new Vertex(new Vector4f(max, max, max, 1), white);
        Vertex upNorthWest = new Vertex(new Vector4f(min, max, max, 1), white);
        Vertex upSouthWest = new Vertex(new Vector4f(min, max, min, 1), white);
        Vertex upSouthEast = new Vertex(new Vector4f(max, max, min, 1), white);
        Vertex downNorthEast = new Vertex(new Vector4f(max, min, max, 1), white);
        Vertex downNorthWest = new Vertex(new Vector4f(min, min, max, 1), white);
        Vertex downSouthWest = new Vertex(new Vector4f(min, min, min, 1), white);
        Vertex downSouthEast = new Vertex(new Vector4f(max, min, min, 1), white);

        Vertex[] eastFace = new Vertex[]{upSouthEast, upNorthEast, downNorthEast, downSouthEast};
        Vertex[] westFace = new Vertex[]{downSouthWest, downNorthWest, upNorthWest, upSouthWest};
        Vertex[] upFace = new Vertex[]{upSouthWest, upNorthWest, upNorthEast, upSouthEast};
        Vertex[] downFace = new Vertex[]{downNorthWest, downSouthWest, downSouthEast, downNorthEast};
        Vertex[] northFace = new Vertex[]{downNorthWest, downNorthEast, upNorthEast, upNorthWest};
        Vertex[] southFace = new Vertex[]{upSouthWest, upSouthEast, downSouthEast, downSouthWest};

        Vertex[][] faces = new Vertex[][]{downFace, upFace, northFace, southFace, eastFace, westFace};

        for (int f = 0; f < faces.length; f++) {
            Vertex[] face = faces[f];
            Direction direction = Direction.from3DDataValue(f);
            Vec3i normal = direction.getNormal();
            float surroundingGutLevel = surroundingGutLevels[f];
            for (int v = 0; v < face.length; v++) {
                Vertex vertex = face[v];

                if (surroundingGutLevel > 0) {
                    Vector4f delta = new Vector4f(Math.abs(normal.getX()), Math.abs(normal.getY()), Math.abs(normal.getZ()), 0);
                    face[v] = new Vertex(vectorLerp(delta, vertex.position(), fullCube[f][v].position()), vertex.setColor());
                } else {

                }
            }
        }

        for (int i = 0; i < faces.length; i++) {
            Vertex[] face = faces[i];
            Direction direction = Direction.from3DDataValue(i);
            renderFace(matrix, consumer, face, direction.getAxis(), new Vector4f(direction.getNormal().getX(), direction.getNormal().getY(), direction.getNormal().getZ(), 0), pPackedLight, pPackedOverlay);
        }

        pPoseStack.popPose();
    }

    private static Vector4f vectorLerp(Vector4f delta, Vector4f from, Vector4f to) {
        return new Vector4f(Mth.lerp(delta.x(), from.x(), to.x()),
                            Mth.lerp(delta.y(), from.y(), to.y()),
                            Mth.lerp(delta.z(), from.z(), to.z()),
                            Mth.lerp(delta.w(), from.w(), to.w()));
    }

    private Vec2[] calculateUVs(Vertex[] verticies, Direction.Axis primaryDirection) {
        Vec2[] uvs = new Vec2[verticies.length];

        switch (primaryDirection) {
            case X:
                for (int i = 0; i < verticies.length; i++) {
                    Vertex vertex = verticies[i];
                    uvs[i] = new Vec2(vertex.position().z(), vertex.position().y());
                }
                break;
            case Y:
                for (int i = 0; i < verticies.length; i++) {
                    Vertex vertex = verticies[i];
                    uvs[i] = new Vec2(vertex.position().x(), vertex.position().z());
                }
                break;
            case Z:
                for (int i = 0; i < verticies.length; i++) {
                    Vertex vertex = verticies[i];
                    uvs[i] = new Vec2(vertex.position().x(), vertex.position().y());
                }
                break;
        }

        return uvs;
    }

    private void renderFace(Matrix4f matrix, VertexConsumer consumer, Vertex[] verticies, Direction.Axis primaryDirection, Vector4f normal, int pPackedLight, int pPackedOverlay) {
        Vec2[] uvs = calculateUVs(verticies, primaryDirection);
        for (int i = 0; i < verticies.length; i++) {
            Vertex vertex = verticies[i];

            Vector4f pos = new Vector4f(vertex.position().x(), vertex.position().y(), vertex.position().z(), 1.0F);
            matrix.transform(pos);
           // normal.transform(matrix);

            Vector3f color = vertex.setColor();

            Vec2 uv = uvs[i];

            consumer.addVertex(pos.x(), pos.y(), pos.z(), color.x(), color.y(), color.z(), 0.0F, uv.x, uv.y, pPackedOverlay, pPackedLight, normal.x(), normal.y(), normal.z());
        }
    }

    private record Vertex(Vector4f position, Vector3f color) {}
}
