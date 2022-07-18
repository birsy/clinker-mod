package birsy.clinker.client.render.entity.model.base;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.SecondOrderDynamics;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DynamicModelPart {
    public SecondOrderDynamics xPosDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics yPosDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics zPosDynamics = new SecondOrderDynamics();
    public float x;
    public float y;
    public float z;
    public float xInit;
    public float yInit;
    public float zInit;

    public SecondOrderDynamics xRotDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics yRotDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics zRotDynamics = new SecondOrderDynamics();
    public float xRot;
    public float yRot;
    public float zRot;
    public float xRotInit;
    public float yRotInit;
    public float zRotInit;

    public SecondOrderDynamics xScaleDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics yScaleDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics zScaleDynamics = new SecondOrderDynamics();
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    public float xScaleInit = 1.0F;
    public float yScaleInit = 1.0F;
    public float zScaleInit = 1.0F;

    public boolean mirror;
    public boolean visible = true;
    public boolean skipDraw;

    public int u;
    public int v;
    public float textureWidth;
    public float textureHeight;

    private static final Minecraft mc = Minecraft.getInstance();
    
    //public Map<String, Cube> cubes;
    public DynamicModelPart parent;
    public List<DynamicModelPart> children = new ArrayList<>();

    public final DynamicModel modelSkeleton;
    public Map<String, ModelPart.Cube> cubes;

    public DynamicModelPart(DynamicModel skeleton, int u, int v) {
        this.u = u;
        this.v = v;
        this.modelSkeleton = skeleton;
        this.modelSkeleton.addPart(this);
        this.cubes = new HashMap<>();
    }

    public void addCube(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ) {
        this.addCube("Cube_" + cubes.size(), minX, minY, minZ, sizeX, sizeY, sizeZ, expansionX, expansionY, expansionZ);
    }

    public void addCube(String name, float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ) {
        ModelPart.Cube cube = new ModelPart.Cube(this.u, this.v, minX, minY, minZ, sizeX, sizeY, sizeZ, expansionX, expansionY, expansionZ, this.mirror, this.textureWidth, this.textureHeight);
        cubes.put(name, cube);
    }

    public void setInitialPosition(float x, float y, float z) {
        this.x = x;
        this.xInit = x;
        this.y = y;
        this.yInit = y;
        this.z = z;
        this.zInit = z;
    }
    public void setInitialRotation(float x, float y, float z) {
        this.xRot = x;
        this.xRotInit = x;
        this.yRot = y;
        this.yRotInit = y;
        this.zRot = z;
        this.zRotInit = z;
    }
    public void setInitialScale(float x, float y, float z) {
        this.xScale = x;
        this.xScaleInit = x;
        this.yScale = y;
        this.yScaleInit = y;
        this.zScale = z;
        this.zScaleInit = z;
    }
    public void createDynamics(float frequency, float damping, float response) {
        this.xPosDynamics.createDynamics(frequency, damping, response, this.x);
        this.yPosDynamics.createDynamics(frequency, damping, response, this.y);
        this.zPosDynamics.createDynamics(frequency, damping, response, this.z);
        this.xRotDynamics.createDynamics(frequency, damping, response, this.xRot);
        this.yRotDynamics.createDynamics(frequency, damping, response, this.yRot);
        this.zRotDynamics.createDynamics(frequency, damping, response, this.zRot);
        this.xScaleDynamics.createDynamics(frequency, damping, response, this.xScale);
        this.yScaleDynamics.createDynamics(frequency, damping, response, this.yScale);
        this.zScaleDynamics.createDynamics(frequency, damping, response, this.zScale);
    }

    public void resetPose() {
        this.x = xInit;
        this.y = yInit;
        this.z = zInit;
        this.xRot = xRotInit;
        this.yRot = yRotInit;
        this.zRot = zRotInit;
        this.xScale = xScaleInit;
        this.yScale = yScaleInit;
        this.zScale = zScaleInit;
    }

    public void setScale(float x, float y, float z) {
        this.xScale = x;
        this.yScale = y;
        this.zScale = z;
    }

    public void translateAndRotateAndScale(PoseStack pPoseStack) {
        pPoseStack.translate(this.xPosDynamics.getValue() / 16.0F, this.yPosDynamics.getValue() / 16.0F, this.zPosDynamics.getValue() / 16.0F);

        if (this.zRotDynamics.getValue() != 0.0F) {
            pPoseStack.mulPose(Vector3f.ZP.rotation(this.zRotDynamics.getValue()));
        }

        if (this.yRotDynamics.getValue() != 0.0F) {
            pPoseStack.mulPose(Vector3f.YP.rotation(this.yRotDynamics.getValue()));
        }

        if (this.xRotDynamics.getValue() != 0.0F) {
            pPoseStack.mulPose(Vector3f.XP.rotation(this.xRotDynamics.getValue()));
        }

        pPoseStack.scale(xScaleDynamics.getValue(), yScaleDynamics.getValue(), zScaleDynamics.getValue());
    }

    // Run every frame, updates the dynamics. Should be able to handle time-steps of any value!
    // With some impacts on accuracy, of course - but no system instability.
    public void update() {
        if (!mc.isPaused()) {
            float deltaTime = mc.getDeltaFrameTime() / 20.0F;

            xPosDynamics.update(deltaTime, x);
            yPosDynamics.update(deltaTime, y);
            zPosDynamics.update(deltaTime, z);

            xRotDynamics.update(deltaTime, xRot);
            yRotDynamics.update(deltaTime, yRot);
            zRotDynamics.update(deltaTime, zRot);

            xScaleDynamics.update(deltaTime, xScale);
            yScaleDynamics.update(deltaTime, yScale);
            zScaleDynamics.update(deltaTime, zScale);

            //Clinker.LOGGER.info("Pos: "   + xPosDynamics.getValue() + ", "   + yPosDynamics.getValue() + ", "   + zPosDynamics.getValue() + ", ");
            //Clinker.LOGGER.info("Rot: "   + xRotDynamics.getValue() + ", "   + yRotDynamics.getValue() + ", "   + zRotDynamics.getValue() + ", ");
            //Clinker.LOGGER.info("Scale: " + xScaleDynamics.getValue() + ", " + yScaleDynamics.getValue() + ", " + zScaleDynamics.getValue() + ", ");

            for (DynamicModelPart child : this.children) {
                child.update();
            }
        }
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay) {
        this.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                pPoseStack.pushPose();
                this.translateAndRotateAndScale(pPoseStack);
                this.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

                for(DynamicModelPart part : this.children) {
                    part.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
                }

                pPoseStack.popPose();
            }
        }
    }

    private void compile(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.cubes.forEach((name, cube) -> cube.compile(pPose, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
    }

    public void setParent(DynamicModelPart part) {
        this.parent = part;
        part.children.add(this);
    }

    public DynamicModelPart getParent() {
        return this.parent;
    }

    public void getGlobalTransForm(PoseStack matrixStack) {
        DynamicModelPart parent = this.parent;
        if (parent != null) {
            parent.getGlobalTransForm(matrixStack);
        }
        this.translateAndRotateAndScale(matrixStack);
    }

    public Vec3 getWorldPos(Entity entity, float partialTick) {
        PoseStack matrixStack = new PoseStack();
        Vec3 position = entity.getPosition(partialTick);
        matrixStack.translate(position.x(), position.y(), position.z());
        matrixStack.mulPose(new Quaternion(0, -entity.getViewYRot(partialTick) + 180, 0, true));
        matrixStack.scale(-1, -1, 1);
        matrixStack.translate(0, -1.5f, 0);
        this.getGlobalTransForm(matrixStack);
        PoseStack.Pose matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();

        Vector4f vec = new Vector4f(0, 0, 0, 1);
        vec.transform(matrix4f);
        return new Vec3(vec.x(), vec.y(), vec.z());
    }


    //TODO: GET ALL THIS STUFF WORKING
    //**
    @OnlyIn(Dist.CLIENT)
    public static class Cube {
        public Polygon[] polygons;
        public float minX;
        public float minY;
        public float minZ;
        public float maxX;
        public float maxY;
        public float maxZ;
        public float u;
        public float v;
        public float textureWidth;
        public float textureHeight;
        public boolean mirrored;
        public boolean flippedNormals = false;

        public Cube(float pTexCoordU, float pTexCoordV, float pMinX, float pMinY, float pMinZ, float pDimensionX, float pDimensionY, float pDimensionZ, float pGrowX, float pGrowY, float pGrowZ, boolean pMirror, float pTexWidthScaled, float pTexHeightScaled) {
            this.minX = pMinX;
            this.minY = pMinY;
            this.minZ = pMinZ;
            this.maxX = pMinX + pDimensionX;
            this.maxY = pMinY + pDimensionY;
            this.maxZ = pMinZ + pDimensionZ;
            this.polygons = new Polygon[6];
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

            Vertex modelpart$vertex7 = new Vertex(pMinX, pMinY, pMinZ, 0.0F, 0.0F);
            Vertex modelpart$vertex = new Vertex(f, pMinY, pMinZ, 0.0F, 8.0F);
            Vertex modelpart$vertex1 = new Vertex(f, f1, pMinZ, 8.0F, 8.0F);
            Vertex modelpart$vertex2 = new Vertex(pMinX, f1, pMinZ, 8.0F, 0.0F);
            Vertex modelpart$vertex3 = new Vertex(pMinX, pMinY, f2, 0.0F, 0.0F);
            Vertex modelpart$vertex4 = new Vertex(f, pMinY, f2, 0.0F, 8.0F);
            Vertex modelpart$vertex5 = new Vertex(f, f1, f2, 8.0F, 8.0F);
            Vertex modelpart$vertex6 = new Vertex(pMinX, f1, f2, 8.0F, 0.0F);
            float f4 = pTexCoordU;
            float f5 = pTexCoordU + pDimensionZ;
            float f6 = pTexCoordU + pDimensionZ + pDimensionX;
            float f7 = pTexCoordU + pDimensionZ + pDimensionX + pDimensionX;
            float f8 = pTexCoordU + pDimensionZ + pDimensionX + pDimensionZ;
            float f9 = pTexCoordU + pDimensionZ + pDimensionX + pDimensionZ + pDimensionX;
            float f10 = pTexCoordV;
            float f11 = pTexCoordV + pDimensionZ;
            float f12 = pTexCoordV + pDimensionZ + pDimensionY;
            this.polygons[2] = new Polygon(new Vertex[]{modelpart$vertex4, modelpart$vertex3, modelpart$vertex7, modelpart$vertex}, f5, f10, f6, f11, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.DOWN);
            this.polygons[3] = new Polygon(new Vertex[]{modelpart$vertex1, modelpart$vertex2, modelpart$vertex6, modelpart$vertex5}, f6, f11, f7, f10, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.UP);
            this.polygons[1] = new Polygon(new Vertex[]{modelpart$vertex7, modelpart$vertex3, modelpart$vertex6, modelpart$vertex2}, f4, f11, f5, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.WEST);
            this.polygons[4] = new Polygon(new Vertex[]{modelpart$vertex, modelpart$vertex7, modelpart$vertex2, modelpart$vertex1}, f5, f11, f6, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.NORTH);
            this.polygons[0] = new Polygon(new Vertex[]{modelpart$vertex4, modelpart$vertex, modelpart$vertex1, modelpart$vertex5}, f6, f11, f8, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.EAST);
            this.polygons[5] = new Polygon(new Vertex[]{modelpart$vertex3, modelpart$vertex4, modelpart$vertex5, modelpart$vertex6}, f8, f11, f9, f12, pTexWidthScaled, pTexHeightScaled, pMirror, Direction.SOUTH);
        }

        public void setSize(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = minX + sizeX;
            this.maxY = minY + sizeY;
            this.maxZ = minZ + sizeZ;
            float f = minX + sizeX;
            float f1 = minY + sizeY;
            float f2 = minZ + sizeZ;
            minX -= expansionX;
            minY -= expansionY;
            minZ -= expansionZ;
            f += expansionX;
            f1 += expansionY;
            f2 += expansionZ;
            if (mirrored) {
                float f3 = f;
                f = minX;
                minX = f3;
            }

            Vertex vertex0 = new Vertex(minX, minY, minZ, 0.0F, 0.0F);
            Vertex vertex1 = new Vertex(f, minY, minZ, 0.0F, 8.0F);
            Vertex vertex2 = new Vertex(f, f1, minZ, 8.0F, 8.0F);
            Vertex vertex3 = new Vertex(minX, f1, minZ, 8.0F, 0.0F);
            Vertex vertex4 = new Vertex(minX, minY, f2, 0.0F, 0.0F);
            Vertex vertex5 = new Vertex(f, minY, f2, 0.0F, 8.0F);
            Vertex vertex6 = new Vertex(f, f1, f2, 8.0F, 8.0F);
            Vertex vertex7 = new Vertex(minX, f1, f2, 8.0F, 0.0F);
            float f4 = this.u;
            float f5 = this.u + sizeZ;
            float f6 = this.u + sizeZ + sizeX;
            float f7 = this.u + sizeZ + sizeX + sizeX;
            float f8 = this.u + sizeZ + sizeX + sizeZ;
            float f9 = this.u + sizeZ + sizeX + sizeZ + sizeX;
            float f10 = this.v;
            float f11 = this.v + sizeZ;
            float f12 = this.v + sizeZ + sizeY;
            this.polygons[2] = new Polygon(new Vertex[]{vertex5, vertex4, vertex0, vertex1}, f5, f10, f6, f11, textureWidth, textureHeight, mirrored, Direction.DOWN);
            this.polygons[3] = new Polygon(new Vertex[]{vertex2, vertex3, vertex7, vertex6}, f6, f11, f7, f10, textureWidth, textureHeight, mirrored, Direction.UP);
            this.polygons[1] = new Polygon(new Vertex[]{vertex0, vertex4, vertex7, vertex3}, f4, f11, f5, f12, textureWidth, textureHeight, mirrored, Direction.WEST);
            this.polygons[4] = new Polygon(new Vertex[]{vertex1, vertex0, vertex3, vertex2}, f5, f11, f6, f12, textureWidth, textureHeight, mirrored, Direction.NORTH);
            this.polygons[0] = new Polygon(new Vertex[]{vertex5, vertex1, vertex2, vertex6}, f6, f11, f8, f12, textureWidth, textureHeight, mirrored, Direction.EAST);
            this.polygons[5] = new Polygon(new Vertex[]{vertex4, vertex5, vertex6, vertex7}, f8, f11, f9, f12, textureWidth, textureHeight, mirrored, Direction.SOUTH);
        }

        public void compile(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            Matrix4f matrix4f = pPose.pose();
            Matrix3f matrix3f = pPose.normal();

            for(Polygon modelpart$polygon : this.polygons) {
                Vector3f normal = modelpart$polygon.normal.copy();

                normal.transform(matrix3f);
                float xNormal = normal.x();
                float yNormal = normal.y();
                float zNormal = normal.z();

                for(Vertex vertex : modelpart$polygon.vertices) {
                    float xPos = vertex.pos.x() / 16.0F;
                    float yPos = vertex.pos.y() / 16.0F;
                    float zPos = vertex.pos.z() / 16.0F;
                    Vector4f vertexPosition = new Vector4f(xPos, yPos, zPos, 1.0F);
                    vertexPosition.transform(matrix4f);
                    pVertexConsumer.vertex(vertexPosition.x(), vertexPosition.y(), vertexPosition.z(), pRed, pGreen, pBlue, pAlpha, 18, 18, pPackedOverlay, pPackedLight, xNormal, yNormal, zNormal);
                }
            }

        }
    }
    @OnlyIn(Dist.CLIENT)
    static class Polygon {
        public final Vertex[] vertices;
        public final Vector3f normal;

        public final boolean flippedNormals = false;
        public Polygon(Vertex[] vertices, float p_104363_, float p_104364_, float p_104365_, float p_104366_, float p_104367_, float p_104368_, boolean mirrored, Direction normal) {
            this.vertices = vertices;
            float f = 0.0F / p_104367_;
            float f1 = 0.0F / p_104368_;
            vertices[0] = vertices[0].remap(p_104365_ / p_104367_ - f, p_104364_ / p_104368_ + f1);
            vertices[1] = vertices[1].remap(p_104363_ / p_104367_ + f, p_104364_ / p_104368_ + f1);
            vertices[2] = vertices[2].remap(p_104363_ / p_104367_ + f, p_104366_ / p_104368_ - f1);
            vertices[3] = vertices[3].remap(p_104365_ / p_104367_ - f, p_104366_ / p_104368_ - f1);
            if (mirrored) {
                int i = vertices.length;

                for(int j = 0; j < i / 2; ++j) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }

            this.normal = normal.step();
            if (flippedNormals) {
                this.normal.mul(1.0F, 1.0F, -1.0F);
            }
            if (mirrored) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
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
